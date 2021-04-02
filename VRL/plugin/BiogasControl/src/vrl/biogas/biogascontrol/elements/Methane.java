package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasControlPlugin;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Methane", 
	category="Biogas_Elements", 
	description="Methane reactor")
public class Methane implements SimulationElement, Serializable{
	private static final long serialVersionUID = 1L;
	private File methaneDirectory;
	private File storageDirectory;
	private Structure structure;
	
	public Methane(Structure struct, File dir) {
		structure = struct;
		methaneDirectory = new File(dir, "methane");
		storageDirectory = new File(dir, "storage_hydrolyse");
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
		
		final File currentTimePath = new File(methaneDirectory, String.valueOf(structure.currentTime()));
		final File previousTimePath = new File(methaneDirectory, String.valueOf(structure.currentTime()-1));
		System.out.println("Running methane");
		System.out.println("currentTimePath : " + currentTimePath);
		if (!currentTimePath.exists()){
			currentTimePath.mkdirs();
		}
		
		try {
			File methaneFile = new File(currentTimePath, "methane_checkpoint.lua");			
			File previousSpec = new File(previousTimePath, "methane_checkpoint.lua");
			
			if(previousSpec.exists()) { //not first timestep
				Files.copy(previousSpec.toPath(), 
						methaneFile.toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
				SpecfileUpdater.update_read_checkpoint(methaneFile, previousTimePath);			
			} else { //first timestep			
				Files.copy(new File(methaneDirectory, "methane.lua").toPath(), 
						methaneFile.toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
			}
			
			File outflowFile = new File(storageDirectory, "outflow.txt");
			System.out.println("outflowFile: " + outflowFile.toString());
			//OutflowInflowUpdater.write_methane_inflow(outflowFile, methaneFile);
			SpecfileUpdater.update_starttime(methaneFile, structure.currentTime());
			SpecfileUpdater.update_endtime(methaneFile, structure.currentTime()+1);
			
			new ElementExecution(methaneFile, currentTimePath, structure, this).execute();

			
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
}

