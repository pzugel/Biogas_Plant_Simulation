package vrl.biogas.biogascontrol.elements.userStructureElements.execution;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.elements.functions.ElementHelperFunctions;
import vrl.biogas.biogascontrol.elements.functions.MergeFunctions;

/**
 * Parallale execution for the user defined element {@link vrl.biogas.biogascontrol.elements.userStructureElements.UserHydrolysis}
 * @author Paul Zügel
 *
 */
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
		}
		return "1";
	}
	
	@Override
	public void done() {
		try {
			int exitValue = Integer.valueOf(this.get());
			if(exitValue == 0) {
				MergeFunctions.merge("Hydrolysis", BiogasUserControl.currentTime, timeDirectory);
			} else {
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
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
