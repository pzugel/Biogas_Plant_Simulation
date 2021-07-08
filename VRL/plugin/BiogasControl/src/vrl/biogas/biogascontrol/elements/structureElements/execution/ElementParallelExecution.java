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
import vrl.biogas.biogascontrol.structures.Structure;

/**
 * Runs the {@link vrl.biogas.biogascontrol.elements.structureElements.HydrolysisParallel} element in the structure element
 * queue by starting a new thread.
 * @author Paul Zügel
 * 
 */
public class ElementParallelExecution extends SwingWorker<String, String> implements Serializable{
	private static final long serialVersionUID = 1L;
	private String command;
	private Structure structure;
	private File timeDirectory;
	private String reactorName;
	
	public ElementParallelExecution(Structure struct, int num) {
		String home = System.getProperty("user.home");
		File ugpath = new File(home, "ug4");
		File ugshell = new File(new File(ugpath, "bin").getAbsolutePath(), "ugshell");
		
		final int currentTime = struct.currentTime();
		reactorName = struct.hydrolysisNames()[num];
    	File reactorDir = new File(struct.directory(), reactorName);
    	timeDirectory = new File(reactorDir, String.valueOf(currentTime));
    	
		File specDir = new File(timeDirectory, "hydrolysis_checkpoint.lua");
		final String cmd = ugshell + " -ex " + BiogasControlClass.simulationFile + " -p " + specDir;
		
		System.out.println("\t +++ Parallel Execution");
		System.out.println("\t +++ timeDirectory: " + timeDirectory);
		System.out.println("\t +++ cmd: " + cmd);
		command = cmd;
		structure = struct;
	}
	
    @Override
	public String doInBackground() throws IOException, InterruptedException {		

		Process proc;
		try {
			proc = Runtime.getRuntime().exec(command, null, timeDirectory);
			
			Scanner s = new Scanner(proc.getInputStream());
	    	StringBuilder procText = new StringBuilder();
	    	while(s.hasNextLine()) {
	    		procText.append(s.nextLine());
				procText.append("\n");
	    	}
	    	s.close();
	    	int exitVal = proc.waitFor(); 	
	    	System.out.println(procText);
	    	
			return String.valueOf(exitVal);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
    	return "1";
    }

    @Override
	public void done() {
    	try {
			int exitValue = Integer.valueOf(this.get());
		   	
	    	if(!structure.wasCancelled() && (exitValue == 0)) {			     	      	    		
	    		try {
	    			MergeFunctions.merge(reactorName, structure.currentTime(), timeDirectory);   			
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
	    	} else if(exitValue != 0) {
	    		JFrame frame = new JFrame();   		
				frame.setLocationRelativeTo(BiogasControl.panel);
				frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
				JOptionPane.showMessageDialog(frame,
					    "Something went wrong!",
					    "UG Error",
					    JOptionPane.ERROR_MESSAGE);
	    	}
    	
    	} catch (NumberFormatException e1) {
			e1.printStackTrace();
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			e1.printStackTrace();
		}
    }
}
