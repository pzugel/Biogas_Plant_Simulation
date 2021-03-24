package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Storage", 
	category="Biogas_Elements", 
	description="Hydrolysis reactor")
public class Storage implements SimulationElement{
	private File storageDirectory;
	private File directory;
	private Structure structure;
	private String[] reactors;
	
	public Storage(Structure struct, File dir, String[] reactorNames) {
		this.directory = dir;
		this.structure = struct;
		this.storageDirectory = new File(dir, "storage_hydrolyse");
		this.reactors = reactorNames;
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
		SimulationPanel.activeElement.setText("Storage");
		String logStart = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logStart + "** Storage ... ");
		
		if(!structure.wasCancelled()) {
			ElementFunctions.merge_all_hydrolysis(directory, reactors);
	
			String logEnd = SimulationPanel.simulationLog.getText();
			SimulationPanel.simulationLog.setText(logEnd + "Done!\n");
			
			ElementRunner myRunnable = new ElementRunner(structure);
			Thread t = new Thread(myRunnable);
			t.start();
		} else {
			String logEnd = SimulationPanel.simulationLog.getText();
			SimulationPanel.simulationLog.setText(logEnd + "Cancelled!\n");
		}
	}

}
