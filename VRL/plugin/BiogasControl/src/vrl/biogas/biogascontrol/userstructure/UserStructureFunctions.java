package vrl.biogas.biogascontrol.userstructure;

import java.io.File;

import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

public class UserStructureFunctions {
	
	public String getCMD(File specification){
		String home = System.getProperty("user.home");
		File ugpath = new File(home, "ug4");
		File ugshell = new File(new File(ugpath, "bin").getAbsolutePath(), "ugshell");
		
		final String cmd = ugshell + " -ex " + BiogasUserControl.simulationFile + " -p " + specification.toString();
		return cmd;
	}
	
	public void log(String text) {
		SimulationPanel simPanel = BiogasUserControl.simulationPanelObj;
		String logBefore = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logBefore + text);		
	}
}
