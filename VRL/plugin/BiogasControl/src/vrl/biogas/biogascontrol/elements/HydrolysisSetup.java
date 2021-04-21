package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.elements.functions.ElementRunner;
import vrl.biogas.biogascontrol.elements.functions.OutflowInflowUpdater;
import vrl.biogas.biogascontrol.elements.functions.SpecfileUpdater;
import vrl.biogas.biogascontrol.panels.SetupPanel;
import vrl.biogas.biogascontrol.structures.Structure;

public class HydrolysisSetup implements SimulationElement, Serializable{
	private static final long serialVersionUID = 1L;
	private Structure structure;
	private File directory;
	
	public HydrolysisSetup(Structure struct) {
		structure = struct;
		directory = struct.directory();
	}
	
	@Override
	public String name() {
		return "HydrolysisSetup";
	}

	@Override
	public File path() {
		return null;
	}

	@Override
	public void run() throws IOException, InterruptedException {
		System.out.println("Run hydrolysis setup");	
		if(!structure.wasCancelled()) {
			System.out.println("Not Cancelled");
			String[] reactors = structure.hydrolysisNames();
			
			//Write reactors to ArrayList
			ArrayList<File> specDirs = new ArrayList<File>();		
			for(String reactor : reactors) {
				File reactorDir = new File(directory, reactor);
				specDirs.add(new File(reactorDir, String.valueOf(structure.currentTime()) + File.separator + "hydrolysis_checkpoint.lua"));
			}			
			
			//Iterate hydrolysis reactors
			for(String reactor : reactors) {
				System.out.println("reactor: " + reactor);
				final File reactorPath = new File(directory, reactor);	
				final File currentTimePath = new File(reactorPath, String.valueOf(structure.currentTime()));
				final File previousTimePath = new File(reactorPath, String.valueOf(structure.currentTime()-1));
				
				//Create directory
				if (!currentTimePath.exists()){
					currentTimePath.mkdirs();
					System.out.println("create dir");
				}
				SetupPanel setPanel = BiogasControl.setupPanelObj;
				setPanel.update_tree(structure.directory());
				
				try {			
					//Copy specification files
					File hydolysisFile = new File(currentTimePath, "hydrolysis_checkpoint.lua");
					File previousSpec = new File(previousTimePath, "hydrolysis_checkpoint.lua");			
					
					if(previousSpec.exists()) { //not first timestep
						Files.copy(previousSpec.toPath(), 
								hydolysisFile.toPath(), 
								StandardCopyOption.REPLACE_EXISTING);
						SpecfileUpdater.update_read_checkpoint(hydolysisFile, previousTimePath);
					} else { //first timestep			
						Files.copy(new File(reactorPath, "hydrolysis_startfile.lua").toPath(), 
								hydolysisFile.toPath(), 
								StandardCopyOption.REPLACE_EXISTING);
					}
					//Update starttime/endtime
					SpecfileUpdater.update_starttime(hydolysisFile, structure.currentTime());
					SpecfileUpdater.update_endtime(hydolysisFile, structure.currentTime()+1);
						
				} catch (IOException e) {
					e.printStackTrace();
				} 	
			}		
			
			//Get fractions from feedback sliders
			ArrayList<Double> fractions = BiogasControl.feedbackPanelObj.computeFractions();
			Double[] fractionsDoubleArr = new Double[fractions.size()];
			fractions.toArray(fractionsDoubleArr);
			
			double[] fractionsArr = new double[fractionsDoubleArr.length];
			int i = 0;
			for(Double d : fractionsDoubleArr) {
				fractionsArr[i] = (double) d;
				i++;
			}
			
			//Update inflow in hydrolysis specification
			File[] specDirsArr = new File[specDirs.size()];
			specDirs.toArray(specDirsArr);
			final File methanePath = new File(directory, "methane");
			File outflowFile = new File(methanePath, "outflow_integratedSum_fullTimesteps.txt");
			if(!structure.firstTimestep()) {
				System.out.println("Not first timestep --> OutflowInflowUpdater");
				// TODO Check if this works!
				System.out.println("outflowFile: " + outflowFile);
				System.out.println("specDirsArr: " + Arrays.toString(specDirsArr));
				System.out.println("fractions: " + Arrays.toString(fractionsArr));
				OutflowInflowUpdater.write_hydrolysis_inflow(outflowFile, specDirsArr, fractionsArr);
			}
			
			ElementRunner myRunnable = new ElementRunner(structure);
			Thread t = new Thread(myRunnable);
			t.start();
		} else {
			System.out.println("Cancelled!");
			ElementRunner myRunnable = new ElementRunner(structure);
			Thread t = new Thread(myRunnable);
			t.start();
		}
	}
}
