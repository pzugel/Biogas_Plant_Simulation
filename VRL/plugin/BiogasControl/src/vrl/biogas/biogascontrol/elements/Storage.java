package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControlPlugin;
import vrl.biogas.biogascontrol.SettingsPanel;
import vrl.biogas.biogascontrol.SetupPanel;
import vrl.biogas.biogascontrol.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Storage", 
	category="Biogas_Elements", 
	description="Hydrolysis reactor")
public class Storage implements SimulationElement{
	private File storageDirectory;
	private File workingDirectory;
	private String[] reactors;
	
	public Storage(File dir, String[] reactorNames) {
		workingDirectory = dir;
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
		SimulationPanel.activeElement.setText("Storage");
		String logStart = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logStart + "** Storage ... ");
		
		int startTime = (Integer) SettingsPanel.simStarttime.getValue();
		int currentTime = BiogasControlPlugin.currenttime;
		boolean preexisting = SetupPanel.mergePreexisting;
		
		ElementFunctions.merge_all_hydrolysis(workingDirectory.toString(), reactors, startTime, currentTime, preexisting);

		String logEnd = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logEnd + "Done!\n");
	}

}
