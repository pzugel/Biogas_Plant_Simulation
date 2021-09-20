package vrl.biogas.biogascontrol.elements.structureElements.execution;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.elements.functions.MergeFunctions;
import vrl.biogas.biogascontrol.elements.structureElements.SimulationElement;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

/**
 * Runs a {@link vrl.biogas.biogascontrol.elements.structureElements.SimulationElement} in the structure element queue by starting a new thread.
 * @author Paul Zügel
 * 
 */
public class ElementExecution extends SwingWorker<String, String> implements Serializable{
	private static final long serialVersionUID = 1L;
	private String command;
	private Structure structure;
	private File timeDirectory;
	private SimulationElement element;
	
	/**
	 * Setup ElementExecution
	 * 
	 * @param specification Path to specification file
	 * @param currentTimePath Path to time folder
	 * @param struct Structure object
	 * @param elem SimulationElement object
	 */
	public ElementExecution(File specification, File currentTimePath, Structure struct, SimulationElement elem) {
		String home = System.getProperty("user.home");
		File ugpath = new File(home, "ug4");
		File ugshell = new File(new File(ugpath, "bin").getAbsolutePath(), "ugshell");
		
		final String cmd = ugshell + " -ex " + BiogasControlClass.simulationFile + " -p " + specification.toString();
		System.out.println("\t +++ Execution");
		System.out.println("\t +++ directory: " + currentTimePath);
		System.out.println("\t +++ cmd: " + cmd);
		
		command = cmd;
		timeDirectory = currentTimePath;
		structure = struct;
		element = elem;
	}
	
    /**
     * Execute ugshell in background (in a new thread). This is needed to ensure, that the GUI
     * is not blocked by the ugshell execution.
     */
    @Override
	public String doInBackground() throws IOException, InterruptedException {
    	
    	Process proc = Runtime.getRuntime().exec(command, null, timeDirectory);
    	Scanner s = new Scanner(proc.getInputStream());
    	StringBuilder procText = new StringBuilder();
    	while(s.hasNextLine()) {
    		procText.append(s.nextLine());
			procText.append("\n");
    	}
    	s.close();
    	int exitVal = proc.waitFor(); 	
    	
    	//Check if execution throws any errors
    	if(exitVal != 0 && !structure.wasCancelled()) {
    		JFrame frame = new JFrame();   		
			frame.setLocationRelativeTo(BiogasControl.panel);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			JOptionPane.showMessageDialog(frame,
				    "Something went wrong!",
				    "UG Error",
				    JOptionPane.ERROR_MESSAGE);
			structure.cancelRun();
			BiogasControlClass.running.setSelected(false);
    	}
    	return proc.toString();
    }

    /**
     * If the execution finished, update simulation panel and 
     * run the next element in the queu.
     */
    @Override
	public void done() {
    	SimulationPanel simPanel = BiogasControlClass.simulationPanelObj;
    	
    	if(!structure.wasCancelled()) {			
    		String log = simPanel.simulationLog.getText();
        	if(element.name().equals("Hydrolysis")) {
        		log = log.replace("... \n", " ... Done!\n");
        		simPanel.simulationLog.setText(log + "\n");
        	} 
        	else {
        		
        		simPanel.simulationLog.setText(log + "Done!\n");
        	}
        	
    		
    		try {
    			MergeFunctions.merge(element.name(), structure.currentTime(), timeDirectory);
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
    		if(element.name().equals("Hydrolysis")) {
    			logEnd = logEnd.replace("... \n", " ... Cancelled!\n");
        		simPanel.simulationLog.setText(logEnd);
        	} else {
        		simPanel.simulationLog.setText(logEnd + "Cancelled!");
        	}  		
    	}

    }
}