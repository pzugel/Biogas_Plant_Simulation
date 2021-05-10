package vrl.biogas.biogascontrol.elements.functions;

import java.io.File;

import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

public class ElementHelperFunctions {
	
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
	
	public void logDone() {
		SimulationPanel simPanel = BiogasUserControl.simulationPanelObj;
		String log = simPanel.simulationLog.getText();
		log = log.replace("... \n", "... Done!\n");
		simPanel.simulationLog.setText(log);		
	}
	
	public void logCancelled() {
		SimulationPanel simPanel = BiogasUserControl.simulationPanelObj;
		String log = simPanel.simulationLog.getText();
		log = log.replace("... \n", "... Cancelled!\n");
		simPanel.simulationLog.setText(log);		
	}
	
	public void logFailed() {
		SimulationPanel simPanel = BiogasUserControl.simulationPanelObj;
		String log = simPanel.simulationLog.getText();
		log = log.replace("... \n", "... Failed!\n");
		simPanel.simulationLog.setText(log);		
	}
}
