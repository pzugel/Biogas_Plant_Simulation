package vrl.biogas.biogascontrol.elements.functions;

import java.io.File;

import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

public class ElementHelperFunctions {
	
	public String getCMD(File specification){
		String home = System.getProperty("user.home");
		File ugpath = new File(home, "ug4");
		File ugshell = new File(new File(ugpath, "bin").getAbsolutePath(), "ugshell");
		
		final String cmd = ugshell + " -ex " + BiogasControlClass.simulationFile + " -p " + specification.toString();
		return cmd;
	}
	
	public void log(String text) {
		SimulationPanel simPanel = BiogasControlClass.simulationPanelObj;
		String logBefore = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logBefore + text);		
	}
}
