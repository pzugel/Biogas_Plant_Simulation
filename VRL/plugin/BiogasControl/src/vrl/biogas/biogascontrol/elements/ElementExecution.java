package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.swing.SwingWorker;

import vrl.biogas.biogascontrol.BiogasControlPlugin;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

public class ElementExecution extends SwingWorker<String, String> implements Serializable{
	private static final long serialVersionUID = 1L;
	static String command;
	static Structure structure;
	static File directory;
	static SimulationElement element;
	
	public ElementExecution(File specification, File dir, Structure struct, SimulationElement elem) {
		String home = System.getProperty("user.home");
		File ugpath = new File(home, "ug4");
		File ugshell = new File(new File(ugpath, "bin").getAbsolutePath(), "ugshell");
		
		final String cmd = ugshell + " -ex " + BiogasControlPlugin.simulationFile + " -p " + specification.toString();
		//final String cmd = "ls";
		System.out.println("cmd: " + cmd);
		
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
    	SimulationPanel simPanel = BiogasControlPlugin.simulationPanelObj;
    	
    	if(!structure.wasCancelled()) {			
        	String logEnd = simPanel.simulationLog.getText();
        	simPanel.simulationLog.setText(logEnd + "Done!\n");
    		
    		try {
    			ElementFunctions.merge(element, structure.currentTime());
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
    	} else {
    		String logEnd = simPanel.simulationLog.getText();
    		simPanel.simulationLog.setText(logEnd + "Cancelled!\n");
    	}

    }
}