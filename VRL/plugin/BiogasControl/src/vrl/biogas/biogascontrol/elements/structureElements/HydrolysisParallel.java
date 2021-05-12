package vrl.biogas.biogascontrol.elements.structureElements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.WindowConstants;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.elements.structureElements.execution.ElementParallelExecution;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

/**
 * Biogas element: Parallel Hydrolysis
 * @author Paul Zügel
 *
 */
public class HydrolysisParallel implements SimulationElement, Serializable{
	private static final long serialVersionUID = 1L;
	private Structure structure;
	
	public HydrolysisParallel(Structure struct) {
		structure = struct;
	}
	
	@Override
	public String name() {
	    return "Hydrolysis Parallel";
	}
	
	@Override
	public File path() {
	    return structure.directory();
	}

	@Override
	public void run() throws IOException, InterruptedException, ExecutionException {
		SimulationPanel simPanel = BiogasControl.simulationPanelObj;
		
		String[] reactorNames = structure.hydrolysisNames();
		
		simPanel.activeElement.setText("Hydrolysis");
		String logStart = simPanel.simulationLog.getText();
		
		
		String logText = "** Hydrolysis ... \n";
		for(String name : reactorNames) {
			logText += "    > " + name + "\n";
		}
		simPanel.simulationLog.setText(logStart + logText);
		
		//TODO Not nice but works
		int num = structure.numHydrolysis();
		try {
			switch (num) {
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
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
			
	}
	
	private void done() {
		SimulationPanel simPanel = BiogasControl.simulationPanelObj;
		if(!structure.wasCancelled()) {			
    		String log = simPanel.simulationLog.getText();
    		log = log.replace("... \n", "... Done!\n");
    		simPanel.simulationLog.setText(log); 
		} else {
			System.out.println("Cancelled!");
			String logEnd = simPanel.simulationLog.getText();
			logEnd = logEnd.replace("... \n", "... Cancelled!\n");
			simPanel.simulationLog.setText(logEnd);
		}
		
		Thread t = new Thread(new Runnable() {

			@Override
			public void run() {
				try {
					structure.runNext();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}					
			}
			
		});
        t.start();	
	}
	
	private void fail() {
		SimulationPanel simPanel = BiogasControl.simulationPanelObj;
		String logEnd = simPanel.simulationLog.getText();		
		
		if(structure.wasCancelled()) {
			//Log
			logEnd = logEnd.replace("... \n", "... Cancelled!\n");
			simPanel.simulationLog.setText(logEnd);
		}
		else {
			//Log
			logEnd = logEnd.replace("... \n", "... Failed!\n");
			simPanel.simulationLog.setText(logEnd);
			
			//Show Message
			JFrame frame = new JFrame();
			frame.setLocationRelativeTo(BiogasControl.panel);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			JOptionPane.showMessageDialog(frame,
			    "Something went wrong during the execution.",
			    "Error",
			    JOptionPane.ERROR_MESSAGE);			
		}
		
		structure.cancelRun();
		BiogasControl.breakBtn.doClick();
	}
	
	private void run_1_reactor() throws InterruptedException, ExecutionException{
		ElementParallelExecution exec0 = new ElementParallelExecution(structure, 0);	
		
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
		ElementParallelExecution exec0 = new ElementParallelExecution(structure, 0);	
		ElementParallelExecution exec1 = new ElementParallelExecution(structure, 1);
		
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
		ElementParallelExecution exec0 = new ElementParallelExecution(structure, 0);	
		ElementParallelExecution exec1 = new ElementParallelExecution(structure, 1);
		ElementParallelExecution exec2 = new ElementParallelExecution(structure, 2);
		
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
		ElementParallelExecution exec0 = new ElementParallelExecution(structure, 0);	
		ElementParallelExecution exec1 = new ElementParallelExecution(structure, 1);
		ElementParallelExecution exec2 = new ElementParallelExecution(structure, 2);
		ElementParallelExecution exec3 = new ElementParallelExecution(structure, 3);
		
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