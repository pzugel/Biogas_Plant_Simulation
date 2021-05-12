package vrl.biogas.biogascontrol.elements.structureElements;

import java.io.File;
import java.io.Serializable;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.elements.structureElements.execution.ElementExecution;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

/**
 * Biogas element: Hydrolysis
 * @author Paul Zügel
 *
 */
public class Hydrolysis implements SimulationElement, Serializable{
	private static final long serialVersionUID = 1L;
	private int numeration;
	private Structure structure;
	private File hydrolysisDirectory;
	
	public Hydrolysis(Structure struct, int num) {
		numeration = num;
		structure = struct;
		String reactor = struct.hydrolysisNames()[num];
		hydrolysisDirectory = new File(struct.directory(), reactor);
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
		simPanel.simulationLog.setText(logStart + "** Hydrolysis ... \n    >" + structure.hydrolysisNames()[numeration]);
		
		final File currentTimePath = new File(hydrolysisDirectory, String.valueOf(structure.currentTime()));
		File hydolysisFile = new File(currentTimePath, "hydrolysis_checkpoint.lua");
		
		new ElementExecution(hydolysisFile, currentTimePath, structure, this).execute();
	}

}
