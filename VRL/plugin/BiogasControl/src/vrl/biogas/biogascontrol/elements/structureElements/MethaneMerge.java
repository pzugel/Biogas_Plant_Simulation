package vrl.biogas.biogascontrol.elements.structureElements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.elements.functions.MergeFunctions;
import vrl.biogas.biogascontrol.elements.structureElements.execution.ElementRunner;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

/**
 * Biogas element: Methane merge
 * @author Paul Zügel
 *
 */
public class MethaneMerge implements SimulationElement, Serializable{
private static final long serialVersionUID = 1L;

private File methaneDirectory;
private File directory;
private Structure structure;

public MethaneMerge(Structure struct) {
	directory = struct.directory();
	structure = struct;
	methaneDirectory = new File(struct.directory(), "methane");
}

@Override
public String name() {
	return "Storage";
}

@Override
public File path() {
	return methaneDirectory;
}

@Override
public void run() throws IOException {
	SimulationPanel simPanel = BiogasControl.simulationPanelObj;
	
	if(!structure.wasCancelled()) {
		MergeFunctions.merge_all_methane(methaneDirectory, directory);
		
		ElementRunner myRunnable = new ElementRunner(structure);
		Thread t = new Thread(myRunnable);
		t.start();
	} else {
		String logEnd = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logEnd + "Cancelled!\n");
	}
}

}