package vrl.biogas.biogascontrol.userstructure;

import java.io.IOException;
import java.io.Serializable;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.panels.SettingsPanel;
import vrl.biogas.biogascontrol.panels.SetupPanel;

@ComponentInfo(name="Start", category="BiogasUserElements")
public class UserStart extends UserStructureFunctions implements Serializable{
private static final long serialVersionUID=1L;
	
	@MethodInfo(name="start()", hide=false, hideCloseIcon=true, interactive=true, num=1)
	public void run() throws InterruptedException, IOException{
		boolean firstTimestep = ((Integer) BiogasUserControl.settingsPanelObj.simStarttime.getValue() == BiogasUserControl.currenttime);
		
		if(SetupPanel.environment_ready) {
			
			if(firstTimestep) {
				BiogasUserControl.simulationFile = SettingsPanel.simulation_path.getText();
				BiogasUserControl.simulationPanelObj.simulationLog.setText("**************************************\n" 
						+ "** New Simulation\n"
						+ "** Hydrolysis File: " + SettingsPanel.hydrolysis_path.getText() + "\n"
						+ "** Methane File: " + SettingsPanel.methane_path.getText() + "\n"
						+ "** Simulation File: " + BiogasUserControl.simulationFile + "\n"
						+ "**************************************\n");
				BiogasUserControl.iteration = 0;
				BiogasUserControl.running.setSelected(true);
			} else {
				++ BiogasUserControl.iteration;
			}
			log("Iteration: " + BiogasUserControl.iteration + "\n");
			
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
