package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;


import vrl.biogas.biogascontrol.structures.Structure;

public class HydrolysisSetup implements SimulationElement{
	private File directory;
	private Structure structure;
	private String[] reactors;
	private double[] fractions;
	private boolean firstTimestep;
	
	public HydrolysisSetup(Structure struct, File dir, String[] reactorNames, double[] fract, boolean first) {
		this.structure = struct;
		this.directory = dir;
		this.reactors = reactorNames;
		this.fractions = fract;
		this.firstTimestep = first;
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
			
		if(!structure.wasCancelled()) {
			
			//Update inflow in hydrolysis specification
			final File methanePath = new File(directory, "methane");
			File outflowFile = new File(methanePath, "outflow.txt");
			ArrayList<File> specDirs = new ArrayList<File>();		
			for(String reactor : reactors) {
				File reactorDir = new File(directory, reactor);
				specDirs.add(new File(reactorDir, String.valueOf(structure.currentTime()-1) + File.separator + "hydrolysis_checkpoint.lua"));
			}
			File[] specDirsArr = new File[specDirs.size()];
			specDirs.toArray(specDirsArr);
			if(!firstTimestep) {
					OutflowInflowUpdater.write_hydrolysis_inflow(outflowFile, specDirsArr, fractions);
			}
			
			//Iterate hydrolysis reactors
			for(String reactor : reactors) {
				
				final File reactorPath = new File(directory, reactor);	
				final File currentTimePath = new File(reactorPath, String.valueOf(structure.currentTime()));
				final File previousTimePath = new File(reactorPath, String.valueOf(structure.currentTime()-1));
				
				//Create directory
				if (!currentTimePath.exists()){
					currentTimePath.mkdirs();
				}
				
				try {			
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
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 	
			}		
			
			ElementRunner myRunnable = new ElementRunner(structure);
			Thread t = new Thread(myRunnable);
			t.start();
		}
	}
}