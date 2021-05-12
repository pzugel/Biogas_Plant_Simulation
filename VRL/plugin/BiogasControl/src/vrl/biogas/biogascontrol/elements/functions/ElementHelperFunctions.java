package vrl.biogas.biogascontrol.elements.functions;

import java.io.File;

import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

/**
 * Small helper functions to be used by simulation elements from {@link vrl.biogas.biogascontrol.elements.userStructureElements}
 * @author Paul Zügel
 */
public class ElementHelperFunctions {
	
	/**
	 * Returns the ugshell command for a specification file
	 * @param specification
	 * @return
	 */
	public String getCMD(File specification){
		String home = System.getProperty("user.home");
		File ugpath = new File(home, "ug4");
		File ugshell = new File(new File(ugpath, "bin").getAbsolutePath(), "ugshell");
		
		final String cmd = ugshell + " -ex " + BiogasUserControl.simulationFile + " -p " + specification.toString();
		return cmd;
	}
	
	/**
	 * Writes a text into the simulation log text field
	 * @param text
	 */
	public void log(String text) {
		SimulationPanel simPanel = BiogasUserControl.simulationPanelObj;
		String logBefore = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logBefore + text);		
	}
	
	/**
	 * Writes "Done" into the simulation log text field
	 */
	public void logDone() {
		SimulationPanel simPanel = BiogasUserControl.simulationPanelObj;
		String log = simPanel.simulationLog.getText();
		log = log.replace("... \n", "... Done!\n");
		simPanel.simulationLog.setText(log);		
	}
	
	/**
	 * Writes "Cancelled" into the simulation log text field
	 */
	public void logCancelled() {
		SimulationPanel simPanel = BiogasUserControl.simulationPanelObj;
		String log = simPanel.simulationLog.getText();
		log = log.replace("... \n", "... Cancelled!\n");
		simPanel.simulationLog.setText(log);		
	}
	
	/**
	 * Writes "Failed" into the simulation log text field
	 */
	public void logFailed() {
		SimulationPanel simPanel = BiogasUserControl.simulationPanelObj;
		String log = simPanel.simulationLog.getText();
		log = log.replace("... \n", "... Failed!\n");
		simPanel.simulationLog.setText(log);		
	}
}
