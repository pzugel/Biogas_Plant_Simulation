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

@ComponentInfo(name="Storage", 
	category="Biogas_Elements", 
	description="Hydrolysis reactor")
public class StorageHydrolysis implements SimulationElement, Serializable{
	private static final long serialVersionUID = 1L;
	private File storageDirectory;
	private File directory;
	private Structure structure;
	private String[] reactors;
	
	public StorageHydrolysis(Structure struct, File dir, String[] reactorNames) {
		directory = dir;
		structure = struct;
		storageDirectory = new File(dir, "storage_hydrolyse");
		reactors = reactorNames;
	}

	@Override
	public String name() {
		return "Storage";
	}

	@Override
	public File path() {
		return storageDirectory;
	}

	@Override
	public void run() throws IOException {
		SimulationPanel simPanel = BiogasControl.simulationPanelObj;
		
		simPanel.activeElement.setText("Storage");
		String logStart = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logStart + "** Storage ... ");
		
		if(!structure.wasCancelled()) {
			ElementFunctions.merge_all_hydrolysis(storageDirectory, directory, reactors);
			if(structure.firstTimestep()) {
				ElementFunctions.create_outputFiles(directory);
			}
			
			String logEnd = simPanel.simulationLog.getText();
			simPanel.simulationLog.setText(logEnd + "Done!\n");
			
			ElementRunner myRunnable = new ElementRunner(structure);
			Thread t = new Thread(myRunnable);
			t.start();
		} else {
			String logEnd = simPanel.simulationLog.getText();
			simPanel.simulationLog.setText(logEnd + "Cancelled!\n");
		}
	}

}
