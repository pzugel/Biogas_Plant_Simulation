package vrl.biogas.biogascontrol.elements.functions;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.elements.Hydrolysis;
import vrl.biogas.biogascontrol.structures.Structure;

public class ElementParallelExecution extends SwingWorker<String, String> implements Serializable{
	private static final long serialVersionUID = 1L;
	private String command;
	private Structure structure;
	private int reactorNumber;
	private File timeDirectory;
	
	public ElementParallelExecution(Structure struct, int num) {
		String home = System.getProperty("user.home");
		File ugpath = new File(home, "ug4");
		File ugshell = new File(new File(ugpath, "bin").getAbsolutePath(), "ugshell");
		
		final int currentTime = struct.currentTime();
		final String name = struct.hydrolysisNames()[num];
    	File reactorDir = new File(struct.directory(), name);
    	timeDirectory = new File(reactorDir, String.valueOf(currentTime));
    	
		File specDir = new File(timeDirectory, "hydrolysis_checkpoint.lua");
		final String cmd = ugshell + " -ex " + BiogasControlClass.simulationFile + " -p " + specDir;
		
		System.out.println("timeDir: " + timeDirectory);
		System.out.println("specDir: " + specDir);
		System.out.println("command: " + cmd);
		command = cmd;
		structure = struct;
		reactorNumber = num;
	}
	
    @Override
	public String doInBackground() throws IOException, InterruptedException {		

		Process proc;
		try {
			proc = Runtime.getRuntime().exec(command, null, timeDirectory);
			proc.waitFor(); 
			return String.valueOf(proc.exitValue());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
    	return "";
    }

    @Override
	public void done() {
    	try {
			int exitValue = Integer.valueOf(this.get());
		   	
	    	if(!structure.wasCancelled() && (exitValue == 0)) {			     	      	    		
	    		try {
	    			final String name = structure.hydrolysisNames()[reactorNumber];
	    			Hydrolysis element = new Hydrolysis(structure, reactorNumber);
					final int currentTime = structure.currentTime();
					File reactorDir = new File(structure.directory(), name);
					File timeDir = new File(reactorDir, String.valueOf(currentTime));
	    			ElementFunctions.merge(element, currentTime, timeDir);   			
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		}
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
