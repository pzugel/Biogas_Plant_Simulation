package vrl.biogas.biogascontrol.elements.userElements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.elements.functions.ElementFunctions;
import vrl.biogas.biogascontrol.elements.functions.ElementHelperFunctions;
import vrl.biogas.biogascontrol.elements.functions.MergeFunctions;

@ComponentInfo(name="Hydrolysis", 
	category="Biogas_UserElements")
public class UserHydrolysis extends ElementHelperFunctions implements Serializable{
	private static final long serialVersionUID=1L;
	
	@MethodInfo(name="Main", hide=false, hideCloseIcon=false, interactive=false, num=1)
	public void run(int number) throws InterruptedException, IOException{
		
		if(!BiogasUserControl.wasCancelled) {
			hydrolysisSetup();
			BiogasUserControl.simulationPanelObj.activeElement.setText("Hydrolysis");
			
			
			//Run
			for(int i=0; i<number; i++) {
				log("** Hydrolysis " + i + " ... ");	
				
				final File hydrolysisDirectory = new File(BiogasUserControl.workingDirectory, "hydrolysis_" + i);
				final File currentTimePath = new File(hydrolysisDirectory, String.valueOf(BiogasUserControl.currentTime));
				final File hydolysisFile = new File(currentTimePath, "hydrolysis_checkpoint.lua");
				
				String cmd = getCMD(hydolysisFile);
				System.out.println("cmd: " + cmd);
				Process proc = Runtime.getRuntime().exec(cmd, null, currentTimePath);
		    	proc.waitFor(); 
	
		    	try {
	    			MergeFunctions.merge("Hydrolysis", BiogasUserControl.currentTime, currentTimePath);
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
		    	log("Done!\n");
			}
		}
		else {
			log("Cancelled!");
		}
	}
	
	private void hydrolysisSetup() throws IOException{
		System.out.println("Run hydrolysis setup");	
		File directory = BiogasUserControl.workingDirectory;
		int currentTime = BiogasUserControl.currentTime;
		boolean firstTimestep = ((Integer) BiogasUserControl.settingsPanelObj.simStarttime.getValue() == BiogasUserControl.currentTime);
		String[] reactors = BiogasUserControl.hydrolysisNames;
		
		ElementFunctions.hydrolysisSetup(directory, currentTime, firstTimestep, reactors);
	}
}
