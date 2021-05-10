package vrl.biogas.biogascontrol.elements.userElements;

import java.io.File;
import java.io.IOException;

import javax.swing.SwingWorker;

import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.elements.functions.ElementHelperFunctions;
import vrl.biogas.biogascontrol.elements.functions.MergeFunctions;

public class UserParallelExecution extends SwingWorker<String, String>{
	private String command;
	private File timeDirectory;
	
	public UserParallelExecution(int num) {
		final File hydrolysisDirectory = new File(BiogasUserControl.workingDirectory, "hydrolysis_" + num);
		timeDirectory = new File(hydrolysisDirectory, String.valueOf(BiogasUserControl.currentTime));
		final File hydolysisFile = new File(timeDirectory, "hydrolysis_checkpoint.lua");
		
		ElementHelperFunctions helper = new ElementHelperFunctions();
		command = helper.getCMD(hydolysisFile);
		System.out.println("cmd: " + command);		
	}
	
	@Override
	protected String doInBackground() throws Exception {
		Process proc;
    	try {
    		proc = Runtime.getRuntime().exec(command, null, timeDirectory);
        	proc.waitFor(); 
        	return String.valueOf(proc.exitValue());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	@Override
	public void done() {
		try {
			MergeFunctions.merge("Hydrolysis", BiogasUserControl.currentTime, timeDirectory);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
