package vrl.biogas.biogascontrol.elements.userStructureElements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.elements.functions.ElementFunctions;
import vrl.biogas.biogascontrol.elements.functions.ElementHelperFunctions;
import vrl.biogas.biogascontrol.elements.userStructureElements.execution.UserParallelExecution;

/**
 * Biogas VRL element: Hydrolysis
 * @author Paul Zügel
 *
 */
@ComponentInfo(name="Hydrolysis", 
	category="Biogas_UserElements")
public class UserHydrolysis extends ElementHelperFunctions implements Serializable{
	private static final long serialVersionUID=1L;
	
	@MethodInfo(name="Icon", hide=false, hideCloseIcon=false, interactive=true, num=1)
	public BufferedImage icon() {
		File iconPath = new File(BiogasUserControl.projectPath, "icons");
		File hydroIcon_path = new File(iconPath, "hydrolysis_muha.png");
		
		BufferedImage img = null;
		try {
		    img = ImageIO.read(hydroIcon_path);
		} catch (IOException e) {
		}

		return img; 
	}
	
	@MethodInfo(name="Main", hide=false, hideCloseIcon=false, interactive=false, num=1)
	public void run() throws InterruptedException, IOException, ExecutionException{
		System.out.println("--> Hydrolysis");	
		int numHydrolysis = BiogasUserControl.numHydrolysis;
		
		//Log
		log("** Hydrolysis ... \n");
		for(int i=0; i<numHydrolysis; i++) {
			log("    > hydrolysis " + i +  "\n");
		}
		
		if(!BiogasUserControl.wasCancelled) {
			hydrolysisSetup();
			BiogasUserControl.simulationPanelObj.activeElement.setText("Hydrolysis");			
			
			//Run
			switch (numHydrolysis) {
				case 1:
					run_1_reactor();
					break;
				case 2:
					run_2_reactors();
					break;
				case 3:
					run_3_reactors();
					break;
				case 4:
					run_4_reactors();
					break;
			}
		}
		else {
			logCancelled();
		}
	}
	
	private void hydrolysisSetup() throws IOException{
		File directory = BiogasUserControl.workingDirectory;
		int currentTime = BiogasUserControl.currentTime;
		boolean firstTimestep = ((Integer) BiogasUserControl.settingsPanelObj.simStarttime.getValue() == BiogasUserControl.currentTime);
		String[] reactors = BiogasUserControl.hydrolysisNames;
		
		ElementFunctions.hydrolysisSetup(directory, currentTime, firstTimestep, reactors);
	}
	
	private void done() {	
		logDone();	
	}
	
	private void fail() {		
		if(BiogasUserControl.wasCancelled) {
			logCancelled();
		}
		else {		
			logFailed();
			//Show Message
			JFrame frame = new JFrame();
			frame.setLocationRelativeTo(BiogasControl.panel);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			JOptionPane.showMessageDialog(frame,
			    "Something went wrong during the execution.",
			    "Error",
			    JOptionPane.ERROR_MESSAGE);	
		}
	}
	
	private void run_1_reactor() throws InterruptedException, ExecutionException{
		UserParallelExecution exec0 = new UserParallelExecution(0);	
		
		exec0.execute();

		while(!exec0.isDone()) {
			//Wait			
			long millis = System.currentTimeMillis();
		    Thread.sleep(1000 - millis % 1000);
		}	
		
		String exit0 = exec0.get();
		int exitValue = Integer.valueOf(exit0);
		if(exitValue == 0) {
			done();
		}
		else {
			fail();
		}		
	}
	
	private void run_2_reactors() throws InterruptedException, ExecutionException {
		UserParallelExecution exec0 = new UserParallelExecution(0);	
		UserParallelExecution exec1 = new UserParallelExecution(1);
		
		exec0.execute();
		exec1.execute();
		
		while(!exec0.isDone() || !exec1.isDone()) {
			//Wait			
			long millis = System.currentTimeMillis();
		    Thread.sleep(1000 - millis % 1000);
		}
		
		String exit0 = exec0.get();
		String exit1 = exec1.get();
		int exitValue = Integer.valueOf(exit0) + Integer.valueOf(exit1);
		if(exitValue == 0) {
			done();
		}
		else {
			fail();
		}		
	}

	private void run_3_reactors() throws InterruptedException, ExecutionException{
		UserParallelExecution exec0 = new UserParallelExecution(0);	
		UserParallelExecution exec1 = new UserParallelExecution(1);
		UserParallelExecution exec2 = new UserParallelExecution(2);
		
		exec0.execute();
		exec1.execute();
		exec2.execute();
		
		while(!exec0.isDone() || !exec1.isDone() || !exec2.isDone()) {
			//Wait			
			long millis = System.currentTimeMillis();
		    Thread.sleep(1000 - millis % 1000);
		}	
		
		String exit0 = exec0.get();
		String exit1 = exec1.get();
		String exit2 = exec2.get();
		int exitValue = Integer.valueOf(exit0) + Integer.valueOf(exit1) + Integer.valueOf(exit2);
		if(exitValue == 0) {
			done();
		}
		else {
			fail();
		}	
	}
	
	private void run_4_reactors() throws InterruptedException, ExecutionException{
		UserParallelExecution exec0 = new UserParallelExecution(0);	
		UserParallelExecution exec1 = new UserParallelExecution(1);
		UserParallelExecution exec2 = new UserParallelExecution(2);
		UserParallelExecution exec3 = new UserParallelExecution(3);
		
		exec0.execute();
		exec1.execute();
		exec2.execute();
		exec3.execute();
		
		while(!exec0.isDone() || !exec1.isDone() || !exec2.isDone() || !exec3.isDone()) {
			//Wait			
			long millis = System.currentTimeMillis();
		    Thread.sleep(1000 - millis % 1000);
		}	
		
		String exit0 = exec0.get();
		String exit1 = exec1.get();
		String exit2 = exec2.get();
		String exit3 = exec3.get();
		int exitValue = Integer.valueOf(exit0) + Integer.valueOf(exit1) + Integer.valueOf(exit2) + Integer.valueOf(exit3);
		if(exitValue == 0) {
			done();
		}
		else {
			fail();
		}	
	}
}
