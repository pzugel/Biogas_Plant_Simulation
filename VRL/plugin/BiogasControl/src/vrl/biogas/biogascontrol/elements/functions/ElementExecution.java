package vrl.biogas.biogascontrol.elements.functions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.elements.SimulationElement;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

public class ElementExecution extends SwingWorker<String, String> implements Serializable{
	private static final long serialVersionUID = 1L;
	private String command;
	private Structure structure;
	private File directory;
	private SimulationElement element;
	
	public ElementExecution(File specification, File dir, Structure struct, SimulationElement elem) {
		String home = System.getProperty("user.home");
		File ugpath = new File(home, "ug4");
		File ugshell = new File(new File(ugpath, "bin").getAbsolutePath(), "ugshell");
		
		final String cmd = ugshell + " -ex " + BiogasControlClass.simulationFile + " -p " + specification.toString();
		System.out.println("directory: " + dir);
		System.out.println("cmd: " + cmd);
		
		command = cmd;
		directory = dir;
		structure = struct;
		element = elem;
	}
	
    @Override
	public String doInBackground() throws IOException, InterruptedException {
    	
    	Process proc = Runtime.getRuntime().exec(command, null, directory);
    	proc.waitFor(); 	
    	
    	//Check if execution throws any errors
    	final int exitVal =  proc.exitValue(); //TODO Does not show console error
    	if(exitVal != 0 && !structure.wasCancelled()) {
    		BufferedReader errBuffer = new BufferedReader(new InputStreamReader(proc.getErrorStream())); 
    		String errOutput = "";
    		if(errBuffer.readLine() != null)
    			errOutput += errBuffer.readLine();
    		JFrame frame = new JFrame();   		
			frame.setLocationRelativeTo(BiogasControl.panel);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			JOptionPane.showMessageDialog(frame,
				    "Something went wrong! \n" + errOutput.toString(),
				    "UG Error",
				    JOptionPane.ERROR_MESSAGE);
			structure.cancelRun();
			BiogasControlClass.running.setSelected(false);
    	}
    	return proc.toString();
    }

    @Override
	public void done() {
    	SimulationPanel simPanel = BiogasControlClass.simulationPanelObj;
    	
    	if(!structure.wasCancelled()) {			
    		String log = simPanel.simulationLog.getText();
        	if(element.name().equals("Hydrolysis")) {
        		System.out.println("Is hydrolysis");
        		log = log.replace("... \n", " ... Done!\n");
        		simPanel.simulationLog.setText(log + "\n");
        	} 
        	else {
        		
        		simPanel.simulationLog.setText(log + "Done!\n");
        	}
        	
    		
    		try {
    			ElementFunctions.merge(element, structure.currentTime(), directory);
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    		
			try {
    			structure.runNext();
    		} catch (IOException e) {
    			e.printStackTrace();
    		} catch (ExecutionException e) {
				e.printStackTrace();
			}	
    		
    	} else {
    		String logEnd = simPanel.simulationLog.getText();
    		simPanel.simulationLog.setText(logEnd + "Cancelled!\n");
    	}

    }
}