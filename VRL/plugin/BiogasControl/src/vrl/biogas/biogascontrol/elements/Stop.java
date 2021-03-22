package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControlPlugin;
import vrl.biogas.biogascontrol.panels.SettingsPanel;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Stop", 
	category="Biogas_Elements", 
	description="Stop element")
public class Stop implements SimulationElement{	
	private Structure structure;
	
	public Stop(Structure struct) {
		this.structure = struct;
	}
	
	@Override
	public File path() {
		return null;
	}
	
	@Override
	public String name() {
	    return "Stop";
	}
	
	@Override
	public void run() throws IOException {
		System.out.println("Stop here!");
		SimulationPanel.activeElement.setText("Stop");
		String logStart = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logStart + "** Stop ... ");
		int endtime = (Integer) SettingsPanel.simEndtime.getValue();
		
		if(!structure.wasCancelled()) {
			if(BiogasControlPlugin.stopBtn.isSelected()) {
				BiogasControlPlugin.running.setSelected(false);
				System.out.println("Simulation stopped!");
				
				String logEnd = SimulationPanel.simulationLog.getText();
				SimulationPanel.simulationLog.setText(logEnd + "Stopped!\n");
			}
			else {
				if(structure.currentTime() < endtime-1) {
					
					String logEnd = SimulationPanel.simulationLog.getText();
					SimulationPanel.simulationLog.setText(logEnd + "Continue!\n");
					++ BiogasControlPlugin.iteration;
					SimulationPanel.iteration.setText(String.valueOf(BiogasControlPlugin.iteration));
					structure.incrementCurrentTime();
								
					
					Runnable runnable = new Runnable()
			        {
			            @Override
			            public void run() 
			            {
			            	try {
								structure.run(structure.currentTime());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			            }
			        };
			        
			        System.out.println("Endtime not reached!");
					System.out.println("\tendtime: " + endtime);
					System.out.println("\tcurrent: " + structure.currentTime());
					
			        Thread threadObject = new Thread(runnable);
			        threadObject.start();			        
					
				}
				else {
					BiogasControlPlugin.running.setSelected(false);
					String logEnd = SimulationPanel.simulationLog.getText();
					SimulationPanel.simulationLog.setText(logEnd + "Simulation finished!\n");
				}
			}
		}
		else {
			String logEnd = SimulationPanel.simulationLog.getText();
			SimulationPanel.simulationLog.setText(logEnd + "Cancelled!\n");
		}
	}
}
