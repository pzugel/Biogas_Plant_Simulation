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

@ComponentInfo(name="Methane", 
	category="Biogas_UserElements")
public class UserMethane extends ElementHelperFunctions implements Serializable{
	private static final long serialVersionUID=1L;
	
	@MethodInfo(name="Main", hide=false, hideCloseIcon=false, interactive=false, num=1)
	public void run() throws InterruptedException, IOException{
		
		if(!BiogasUserControl.wasCancelled) {

			BiogasUserControl.simulationPanelObj.activeElement.setText("Methane");

			log("** Methane ... ");	
			
			int currentTime = BiogasUserControl.currentTime;
			ElementFunctions.methaneSetup(BiogasUserControl.workingDirectory, currentTime);
			
			final File methaneDirectory = new File(BiogasUserControl.workingDirectory, "methane");
			final File currentTimePath = new File(methaneDirectory, String.valueOf(BiogasUserControl.currentTime));
			final File methaneFile = new File(currentTimePath, "methane_checkpoint.lua");
			
			String cmd = getCMD(methaneFile);
			System.out.println("cmd: " + cmd);
			Process proc = Runtime.getRuntime().exec(cmd, null, currentTimePath);
	    	proc.waitFor(); 
	    	
	    	try {
    			MergeFunctions.merge("Methane", BiogasUserControl.currentTime, currentTimePath);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
	    	
	    	MergeFunctions.merge_all_methane(methaneDirectory, BiogasUserControl.workingDirectory);
	    	log("Done!\n");
		}
	}
}
