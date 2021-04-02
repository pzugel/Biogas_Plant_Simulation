package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasControlPlugin;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Stop", 
	category="Biogas_Elements", 
	description="Stop element")
public class Stop implements SimulationElement, Serializable{	
	private static final long serialVersionUID = 1L;
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
		SimulationPanel simPanel = BiogasControl.simulationPanelObj;
		
		simPanel.activeElement.setText("Stop");
		String logStart = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logStart + "** Stop ... ");
		int endtime = (Integer) BiogasControl.settingsPanelObj.simEndtime.getValue();
		
		if(!structure.wasCancelled()) {
			if(BiogasControl.stopBtn.isSelected()) {
				BiogasControl.running.setSelected(false);
				System.out.println("Simulation stopped!");
				
				String logEnd = simPanel.simulationLog.getText();
				simPanel.simulationLog.setText(logEnd + "Stopped!\n");
			}
			else {
				if(structure.currentTime() < endtime-1) {
					
					String logEnd = simPanel.simulationLog.getText();
					simPanel.simulationLog.setText(logEnd + "Continue!\n");
					++ BiogasControl.iteration;					
					BiogasControl.simulationPanelObj.iteration.setText(String.valueOf(BiogasControl.iteration));
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
					BiogasControl.running.setSelected(false);
					String logEnd = simPanel.simulationLog.getText();
					simPanel.simulationLog.setText(logEnd + "Simulation finished!\n");
				}
			}
		}
		else {
			String logEnd = simPanel.simulationLog.getText();
			simPanel.simulationLog.setText(logEnd + "Cancelled!\n");
		}
	}
}
