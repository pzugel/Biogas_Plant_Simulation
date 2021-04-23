package vrl.biogas.biogascontrol.elements.structureElements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.elements.functions.MergeFunctions;
import vrl.biogas.biogascontrol.elements.structureElements.execution.ElementRunner;
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
	
	public StorageHydrolysis(Structure struct) {
		directory = struct.directory();
		structure = struct;
		storageDirectory = new File(struct.directory(), "storage_hydrolysis");
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
			MergeFunctions.merge_all_hydrolysis(storageDirectory, directory, structure.hydrolysisNames());
			if(structure.firstTimestep()) {
				String hydrolysisName = structure.hydrolysisNames()[0];
				MergeFunctions.create_outputFiles(directory, hydrolysisName);
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
