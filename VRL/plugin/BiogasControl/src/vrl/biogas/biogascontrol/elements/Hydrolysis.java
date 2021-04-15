package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.Serializable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.elements.functions.ElementExecution;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Hydrolysis", 
	category="Biogas_Elements", 
	description="Hydrolysis reactor")
public class Hydrolysis implements SimulationElement, Serializable{
	private static final long serialVersionUID = 1L;
	private int numeration;
	private Structure structure;
	private File hydrolysisDirectory;
	public Process proc;
	
	public Hydrolysis(Structure struct, File dir, int num) {
		numeration = num;
		structure = struct;
		hydrolysisDirectory = new File(dir, "hydrolyse_" + numeration);
	}
	
	@Override
	public String name() {
	    return "Hydrolysis";
	}
	
	@Override
	public File path() {
	    return hydrolysisDirectory;
	}
	
	public int numeration() {
	    return numeration;
	}

	@Override
	public void run() {
		SimulationPanel simPanel = BiogasControl.simulationPanelObj;
		
		simPanel.activeElement.setText("Hydrolysis");
		String logStart = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logStart + "** Hydrolysis " + numeration + " ... ");
		
		final File currentTimePath = new File(hydrolysisDirectory, String.valueOf(structure.currentTime()));
		File hydolysisFile = new File(currentTimePath, "hydrolysis_checkpoint.lua");
		
		new ElementExecution(hydolysisFile, currentTimePath, structure, this).execute();
	}

}
