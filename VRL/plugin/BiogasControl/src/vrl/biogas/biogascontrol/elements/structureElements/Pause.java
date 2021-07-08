package vrl.biogas.biogascontrol.elements.structureElements;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

/**
 * Biogas element: Pause
 * @author Paul Zügel
 *
 */
public class Pause implements SimulationElement{	
	private Structure structure;
	
	public Pause(Structure struct) {
		this.structure = struct;
	}
	
	@Override
	public File path() {
		return null;
	}
	
	@Override
	public String name() {
	    return "Pause";
	}
	
	@Override
	public void run() throws IOException, InterruptedException, ExecutionException {
		System.out.println("--> Pause");
		final SimulationPanel simPanel = BiogasControl.simulationPanelObj;
		
		simPanel.activeElement.setText("Pause");
		String logStart = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logStart + "** Pause ... ");
		
		if(!structure.wasCancelled()) {
			Runnable runnable = new Runnable()
	        {
				@Override
				public void run() 
				{
					while(BiogasControl.pauseBtn.isSelected())
					{
						try 
						{
							Thread.sleep(1000);
							BiogasControl.timer.stop();
							BiogasControl.timerStartTime = BiogasControl.timerStartTime+1000;
						} 
						catch (InterruptedException e) 
						{
							e.printStackTrace();
						}                     
					}
					try {
						BiogasControl.timer.start();
						String logEnd = simPanel.simulationLog.getText();
						simPanel.simulationLog.setText(logEnd + "Continue!\n");
						structure.runNext();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (ExecutionException e) {
						e.printStackTrace();
					}	
				}
	    	};
	    	Thread threadObject = new Thread(runnable);
	    	threadObject.start();
		}
		else {
			String logEnd = simPanel.simulationLog.getText();
			simPanel.simulationLog.setText(logEnd + "Cancelled!\n");
		}
	}
}
