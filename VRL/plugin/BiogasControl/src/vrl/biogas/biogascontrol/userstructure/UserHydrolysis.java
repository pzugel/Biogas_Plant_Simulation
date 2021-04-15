package vrl.biogas.biogascontrol.userstructure;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.elements.functions.OutflowInflowUpdater;
import vrl.biogas.biogascontrol.elements.functions.SpecfileUpdater;

@ComponentInfo(name="Hydrolysis", category="Biogas_UserElements")
public class UserHydrolysis extends UserStructureFunctions implements java.io.Serializable{
	private static final long serialVersionUID=1L;
	
	@MethodInfo(name="Main", hide=false, hideCloseIcon=false, interactive=true, num=1)
	public void run(int number) throws InterruptedException, IOException{
		
		BiogasControlClass.simulationPanelObj.activeElement.setText("Hydrolysis");
		
		final File methanePath = new File(BiogasUserControl.workingDirectory, "methane");
		final File outflowFile = new File(methanePath, "outflow.txt");
		
		//Setup directories
		ArrayList<File> specDirs = new ArrayList<File>();		
		for(int i=0; i<number; i++) {
			final File hydrolysisDirectory = new File(BiogasUserControl.workingDirectory, "hydrolyse_" + i);
			System.out.println("hydrolysisDirectory: " + hydrolysisDirectory);
			
			final File currentTimePath = new File(hydrolysisDirectory, String.valueOf(BiogasUserControl.currenttime));
			final File previousTimePath = new File(hydrolysisDirectory, String.valueOf(BiogasUserControl.currenttime-1));
			System.out.println("currentTimePath: " + currentTimePath);
			System.out.println("previousTimePath: " + previousTimePath);
			
			//Create directory
			if (!currentTimePath.exists()){
				currentTimePath.mkdirs();
				System.out.println("create dir");
			}
			
			//Update specification
			try {			
				final File hydolysisFile = new File(currentTimePath, "hydrolysis_checkpoint.lua");
				final File previousSpec = new File(previousTimePath, "hydrolysis_checkpoint.lua");			
				
				if(previousSpec.exists()) { //not first timestep
					Files.copy(previousSpec.toPath(), 
							hydolysisFile.toPath(), 
							StandardCopyOption.REPLACE_EXISTING);
					SpecfileUpdater.update_read_checkpoint(hydolysisFile, previousTimePath);
				} else { //first timestep			
					Files.copy(new File(hydrolysisDirectory, "hydrolysis_startfile.lua").toPath(), 
							hydolysisFile.toPath(), 
							StandardCopyOption.REPLACE_EXISTING);
				}
				//Update starttime/endtime
				SpecfileUpdater.update_starttime(hydolysisFile, BiogasUserControl.currenttime);
				SpecfileUpdater.update_endtime(hydolysisFile, BiogasUserControl.currenttime+1);
					
			} catch (IOException e) {
				e.printStackTrace();
			} 	
			
			specDirs.add(new File(currentTimePath, "hydrolysis_checkpoint.lua"));
		}
		
				
		//Set fractions
		double[] fractions = new double[specDirs.size()];
		for(int i=0; i<number; i++) {
			fractions[i] = 1;
		}
		
		//Update inflow values
		File[] specDirsArr = new File[specDirs.size()];
		specDirs.toArray(specDirsArr);
		boolean firstTimestep = ((Integer) BiogasUserControl.settingsPanelObj.simStarttime.getValue() == BiogasUserControl.currenttime);
		if(!firstTimestep && outflowFile.exists()) {
			OutflowInflowUpdater.write_hydrolysis_inflow(outflowFile, specDirsArr, fractions);
		}
		
		//Run
		for(int i=0; i<number; i++) {
			log("** Hydrolysis ... ");	
			
			final File hydrolysisDirectory = new File(BiogasUserControl.workingDirectory, "hydrolyse_" + i);
			final File currentTimePath = new File(hydrolysisDirectory, String.valueOf(BiogasUserControl.currenttime));
			final File hydolysisFile = new File(currentTimePath, "hydrolysis_checkpoint.lua");
			
			String cmd = getCMD(hydolysisFile);
			System.out.println("cmd: " + cmd);
			Process proc = Runtime.getRuntime().exec(cmd, null, currentTimePath);
	    	proc.waitFor(); 

	    	log("Done!\n");
		}
	}
}
