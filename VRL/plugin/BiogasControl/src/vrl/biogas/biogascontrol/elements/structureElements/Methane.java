package vrl.biogas.biogascontrol.elements.structureElements;

import java.io.File;
import java.io.Serializable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.elements.functions.ElementFunctions;
import vrl.biogas.biogascontrol.elements.structureElements.execution.ElementExecution;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Methane", 
	category="Biogas_Elements", 
	description="Methane reactor")
public class Methane implements SimulationElement, Serializable{
	private static final long serialVersionUID = 1L;
	private File methaneDirectory;
	private Structure structure;
	
	public Methane(Structure struct) {
		structure = struct;
		methaneDirectory = new File(struct.directory(), "methane");
	}
	
	@Override
	public File path() {
		return methaneDirectory;
	}
	
	@Override
	public String name() {
	    return "Methane";
	}

	@Override
	public void run() {
		SimulationPanel simPanel = BiogasControl.simulationPanelObj;
				
		simPanel.activeElement.setText("Methane");
		String logStart = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logStart + "** Methane ... ");
		
		int currentTime = structure.currentTime();
		ElementFunctions.methaneSetup(structure.directory(), currentTime);
		
		final File currentTimePath = new File(methaneDirectory, String.valueOf(currentTime));
		File methaneFile = new File(currentTimePath, "methane_checkpoint.lua");
		new ElementExecution(methaneFile, currentTimePath, structure, this).execute();
	}
}

