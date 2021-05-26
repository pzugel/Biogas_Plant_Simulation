package vrl.biogas.biogascontrol.elements.userStructureElements.execution;

import java.io.IOException;
import java.io.Serializable;

import eu.mihosoft.vrl.annotation.MethodInfo;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.elements.functions.ElementFunctions;
import vrl.biogas.biogascontrol.elements.functions.ElementHelperFunctions;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

/**
 * Execution of the "Stop" function in a user defined structure.
 * Called by the {@link vrl.biogas.biogascontrol.UserStructureIterator} at the end of every iteration.
 * @author Paul Zügel
 *
 */
public class UserStop extends ElementHelperFunctions implements Serializable{
	private static final long serialVersionUID=1L;
	
	@MethodInfo(name="Main", hide=false, hideCloseIcon=false, interactive=false, num=1)
	public void run() throws InterruptedException, IOException{
		
		System.out.println("Stop here!");
		SimulationPanel simPanel = BiogasUserControl.simulationPanelObj;
		
		simPanel.activeElement.setText("Stop");
		
		log("** Stop ... ");
		int endtime = (Integer) BiogasUserControl.settingsPanelObj.simEndtime.getValue();
		
		String structName = BiogasUserControl.structureName;
		int numHydrolysis = BiogasUserControl.numHydrolysis;
		int currentTime = BiogasUserControl.currentTime;
		
		if(!BiogasUserControl.wasCancelled) {
			if(BiogasUserControl.stopBtn.isSelected()) {
				ElementFunctions.writeSummary(BiogasUserControl.workingDirectory, true, structName, numHydrolysis, currentTime);
				System.out.println("Simulation stopped!");
				BiogasUserControl.running.setSelected(false); 
				BiogasUserControl.settingsPanelObj.simStarttime.setValue(currentTime+1);
				System.out.println("UserStop Stopped!");
				log("Stopped!\n");
			}
			else {
				if(!(currentTime < endtime-1)) {
					ElementFunctions.writeSummary(BiogasUserControl.workingDirectory, true, structName, numHydrolysis, currentTime);
					BiogasUserControl.setupPanelObj.mergePreexisting = true;
					BiogasUserControl.running.setSelected(false);
					BiogasUserControl.settingsPanelObj.simStarttime.setValue(currentTime+1);
					System.out.println("UserStop Simulation Finished!");
					log("Simulation finished!\n");					
				}
				else {
					log("Continue!\n");
				}
			}
		}
		else {
			ElementFunctions.writeSummary(BiogasUserControl.workingDirectory, false, structName, numHydrolysis, currentTime);
			BiogasUserControl.running.setSelected(false);
			BiogasUserControl.settingsPanelObj.simStarttime.setValue(currentTime+1);
			log("Cancelled!\n");
		}
	}
}
