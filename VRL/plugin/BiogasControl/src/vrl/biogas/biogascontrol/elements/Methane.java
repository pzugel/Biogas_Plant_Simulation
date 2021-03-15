package vrl.biogas.biogascontrol.elements;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.SimulationPanel;

@ComponentInfo(name="Methane", 
	category="Biogas_Elements", 
	description="Methane reactor")
public class Methane implements SimulationElement{
	
	@Override
	public String name() {
	    return "Methane";
	}

	@Override
	public void run() {
		System.out.println("Methane Run Here");
		SimulationPanel.activeElement.setText("Methane");
		String logStart = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logStart + "** Methane ... ");
		
		String logEnd = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logEnd + "Done!\n");
	}

}

