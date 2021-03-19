package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControlPlugin;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Pause", 
	category="Biogas_Elements", 
	description="Pause element")
public class Pause implements SimulationElement{	
	private Structure structure;
	
	public Pause(Structure struct) {
		structure = struct;
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
	public void run() throws IOException, InterruptedException {
		System.out.println("Pause here!");
		SimulationPanel.activeElement.setText("Pause");
		String logStart = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logStart + "** Pause ... ");
		
		if(!structure.wasCancelled()) { 
			Runnable runnable = new Runnable()
	        {
				@Override
				public void run() 
				{
					while(BiogasControlPlugin.pauseBtn.isSelected())
					{
						try 
						{
							Thread.sleep(500);
						} 
						catch (InterruptedException e) 
						{
							// TODO Auto-generated catch block
							e.printStackTrace();
						}                     
					}
					try {
						String logEnd = SimulationPanel.simulationLog.getText();
						SimulationPanel.simulationLog.setText(logEnd + "Continue!\n");
						structure.runNext();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}	
				}
	    	};
	    	Thread threadObject = new Thread(runnable);
	    	threadObject.start();
		}
		else {
			String logEnd = SimulationPanel.simulationLog.getText();
			SimulationPanel.simulationLog.setText(logEnd + "Cancelled!\n");
		}
	}
}
