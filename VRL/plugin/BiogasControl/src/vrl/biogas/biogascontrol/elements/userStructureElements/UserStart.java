package vrl.biogas.biogascontrol.elements.userStructureElements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.elements.functions.ElementHelperFunctions;

/**
 * Biogas VRL element: Start
 * @author Paul Zügel
 *
 */
@ComponentInfo(name="Start", 
	category="Biogas_UserElements")
public class UserStart extends ElementHelperFunctions implements Serializable{
	private static final long serialVersionUID=1L;	

	@MethodInfo(name="Main", hide=false, hideCloseIcon=true, interactive=false, num=1)
	public void run() throws InterruptedException, IOException{
		boolean firstTimestep = ((Integer) BiogasUserControl.settingsPanelObj.simStarttime.getValue() == BiogasUserControl.currentTime);
		
		if(!BiogasUserControl.wasCancelled) {
			System.out.println("UserStart not cancelled!");
			System.out.println("UserStart firstTimestep?: " + firstTimestep);
			if(BiogasUserControl.setupPanelObj.environment_ready) {
				
				if(firstTimestep) {
					BiogasUserControl.simulationFile = BiogasUserControl.settingsPanelObj.simulation_path.getText();
					String logText = "**************************************\n";
					if(BiogasUserControl.setupPanelObj.mergePreexisting) {
						logText += "** Continue Simulation\n";
					}
					else {
						logText += "** New Simulation\n";
					}
					logText += "** Hydrolysis File: " + BiogasUserControl.settingsPanelObj.hydrolysis_path.getText() + "\n"
							+ "** Methane File: " + BiogasUserControl.settingsPanelObj.methane_path.getText() + "\n"
							+ "** Simulation File: " + BiogasUserControl.simulationFile + "\n"
							+ "**************************************\n";
					BiogasUserControl.simulationPanelObj.simulationLog.setText(logText);
					BiogasUserControl.iteration = 0;
				}
				BiogasUserControl.feedingPanelObj.nextTimestep.setText(String.valueOf(BiogasUserControl.currentTime + 1));
				log("Iteration: " + BiogasUserControl.iteration + " (Time " + BiogasUserControl.currentTime + ")\n");
				//CleanUp
				boolean cleanUp = BiogasControl.settingsPanelObj.autoCleanup.isSelected();
				if(cleanUp) {
					cleanUp();
				}
			} else {
				JFrame frame = new JFrame();
				frame.setLocationRelativeTo(BiogasUserControl.panel);
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				JOptionPane.showMessageDialog(frame,
					    "You need to set up a working environment before starting the simulation.",
					    "Warning",
					    JOptionPane.WARNING_MESSAGE);
				BiogasUserControl.wasCancelled = true;
			}
		}
	}
	
	private void cleanUp() {
		System.out.println("cleanUp()");
		int currentTime = BiogasUserControl.currentTime;
		int previousTime = currentTime-2;
		File directory = BiogasUserControl.workingDirectory;
		
		//Methane
		File methanePath = new File(directory, "methane");
		File methaneTimePath = new File(methanePath, String.valueOf(previousTime));
		System.out.println("methaneTimePath: " + methaneTimePath);
		if(methaneTimePath.exists()) {
			String[] files = methaneTimePath.list();
			for(String f: files){
			    File currentFile = new File(methaneTimePath.getPath(), f);
			    currentFile.delete();
			}
			methaneTimePath.delete();
		}
		
		//Hydrolysis
		for(String name : BiogasUserControl.hydrolysisNames) {
			File hydroPath = new File(directory, name);
			File hydroTimePath = new File(hydroPath, String.valueOf(previousTime));
			System.out.println("hydroTimePath: " + hydroTimePath);
			if(hydroTimePath.exists()) {
				String[] files = hydroTimePath.list();
				for(String f: files){
				    File currentFile = new File(hydroTimePath.getPath(), f);
				    currentFile.delete();
				}
				hydroTimePath.delete();
			}
		}		
	}
}
