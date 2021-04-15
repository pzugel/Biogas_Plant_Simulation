package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;

import vrl.biogas.biogascontrol.elements.functions.ElementRunner;
import vrl.biogas.biogascontrol.elements.functions.OutflowInflowUpdater;
import vrl.biogas.biogascontrol.elements.functions.SpecfileUpdater;
import vrl.biogas.biogascontrol.structures.STRUCT_2_STAGE;
import vrl.biogas.biogascontrol.structures.Structure;

public class HydrolysisSetup implements SimulationElement, Serializable{
	private static final long serialVersionUID = 1L;
	private File directory;
	private Structure structure;
	private String[] reactors;
	private double[] fractions;
	
	public HydrolysisSetup(Structure struct, File dir, String[] reactorNames) {
		structure = struct;
		directory = dir;
		reactors = reactorNames;
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
			computeFractions();
			System.out.println("Not Cancelled");
			
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
				System.out.println("fractions: " + Arrays.toString(fractions));
				OutflowInflowUpdater.write_hydrolysis_inflow(outflowFile, specDirsArr, fractions);
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
	
	private void computeFractions() {
		int num = structure.numHydrolysis();
		double frac[] = new double[num]; 
		for(int i=0; i<num; i++) {
			frac[i] = 1.0 / num; //EVEN SPLIT
		}
		fractions = frac;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{  
		File dir = new File("/home/paul/Schreibtisch/smalltestmethane/biogasVRL_20210415_124715");
		String names[] = {"hydrolyse_0", "hydrolyse_1"};
		Structure struct = new STRUCT_2_STAGE();
		struct.incrementCurrentTime();
		HydrolysisSetup setup = new HydrolysisSetup(struct,dir,names);
		setup.run();
	}
}
