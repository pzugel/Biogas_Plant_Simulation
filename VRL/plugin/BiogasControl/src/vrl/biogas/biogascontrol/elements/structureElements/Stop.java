package vrl.biogas.biogascontrol.elements.structureElements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.elements.functions.ElementFunctions;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

/**
 * Biogas element: Stop
 * @author Paul Zügel
 *
 */
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
		System.out.println("--> Stop");
		SimulationPanel simPanel = BiogasControl.simulationPanelObj;
		
		simPanel.activeElement.setText("Stop");
		String logStart = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logStart + "** Stop ... ");
		int endtime = (Integer) BiogasControl.settingsPanelObj.simEndtime.getValue();
		
		String structName = structure.name();
		int numHydrolysis = structure.numHydrolysis();
		int currentTime = structure.currentTime();
		int flowValue = (Integer) BiogasControl.settingsPanelObj.flowValue.getValue();
		
		if(!structure.wasCancelled()) {
			System.out.println("Not cancelled");
			if(BiogasControl.stopBtn.isSelected()) {
				ElementFunctions.writeSummary(BiogasControl.workingDirectory, true, structName, numHydrolysis, flowValue, currentTime);
				BiogasControl.running.setSelected(false);
				BiogasControl.settingsPanelObj.simStarttime.setValue(currentTime+1);
				System.out.println("\t Simulation stopped!");
				
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
			        
			        System.out.println("\t Endtime not reached!");
					System.out.println("\t --> endtime: " + endtime);
					System.out.println("\t --> current: " + structure.currentTime());
					
			        Thread threadObject = new Thread(runnable);
			        threadObject.start();			        
					
				}
				else {
					System.out.println("Finisehd. Writing summary: ");
					System.out.println("flowValue: " + flowValue);
					ElementFunctions.writeSummary(BiogasControl.workingDirectory, true, structName, numHydrolysis, flowValue, currentTime);
					BiogasControl.setupPanelObj.mergePreexisting = true;
					BiogasControl.running.setSelected(false);
					BiogasControl.settingsPanelObj.simStarttime.setValue(currentTime+1);
					String logEnd = simPanel.simulationLog.getText();
					simPanel.simulationLog.setText(logEnd + "Simulation finished!\n");
					
				}
			}
		}
		else {
			ElementFunctions.writeSummary(BiogasControl.workingDirectory, false, structName, numHydrolysis, flowValue, currentTime);
			BiogasControl.settingsPanelObj.simStarttime.setValue(currentTime+1);
			String logEnd = simPanel.simulationLog.getText();
			simPanel.simulationLog.setText(logEnd + "Cancelled!\n");
		}
	}
}
