package vrl.biogas.biogascontrol;

import java.io.File;
import java.io.IOException;

import javax.swing.SwingWorker;

import vrl.biogas.biogascontrol.structures.Structure;

public class ExeWorker extends SwingWorker<String, String> {
	static String command;
	static Structure structure;
	static File directory;
	
	public ExeWorker(String cmd, File dir, Structure struct) {
		command = cmd;
		directory = dir;
		structure = struct;
	}
    public String doInBackground() throws IOException, InterruptedException {
    	Process proc = Runtime.getRuntime().exec(command, null, directory);
    	proc.waitFor();
    	return proc.toString();
    }

    public void done() {
    	String logEnd = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logEnd + "Done!\n");
		structure.runNext();
    }
}