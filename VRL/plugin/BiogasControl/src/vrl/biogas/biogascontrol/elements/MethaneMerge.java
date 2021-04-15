package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.elements.functions.ElementFunctions;
import vrl.biogas.biogascontrol.elements.functions.ElementRunner;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="MethaneMerge", 
	category="Biogas_Elements", 
	description="Methane merger")
public class MethaneMerge implements SimulationElement, Serializable{
private static final long serialVersionUID = 1L;

private File methaneDirectory;
private File directory;
private Structure structure;

public MethaneMerge(Structure struct, File dir) {
	directory = dir;
	structure = struct;
	methaneDirectory = new File(dir, "methane");
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
		ElementFunctions.merge_all_methane(methaneDirectory, directory);
		
		ElementRunner myRunnable = new ElementRunner(structure);
		Thread t = new Thread(myRunnable);
		t.start();
	} else {
		String logEnd = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logEnd + "Cancelled!\n");
	}
}

}