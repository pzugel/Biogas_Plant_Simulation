package vrl.biogas.biogascontrol;

import java.io.File;
import java.io.IOException;

import javax.swing.SwingWorker;

import vrl.biogas.biogascontrol.elements.ElementFunctions;
import vrl.biogas.biogascontrol.elements.SimulationElement;
import vrl.biogas.biogascontrol.structures.Structure;

public class ExeWorker extends SwingWorker<String, String> {
	static String command;
	static Structure structure;
	static File directory;
	static SimulationElement element;
	
	public ExeWorker(String cmd, File dir, Structure struct, SimulationElement elem) {
		command = cmd;
		directory = dir;
		structure = struct;
		element = elem;
	}
    public String doInBackground() throws IOException, InterruptedException {
    	Process proc = Runtime.getRuntime().exec(command, null, directory);
    	proc.waitFor();
    	return proc.toString();
    }

    public void done() {
    	String logEnd = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logEnd + "Done!\n");
		try {
			ElementFunctions.merge(element);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			structure.runNext();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}