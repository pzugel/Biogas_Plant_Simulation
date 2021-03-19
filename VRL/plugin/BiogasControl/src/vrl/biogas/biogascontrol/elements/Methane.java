package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Methane", 
	category="Biogas_Elements", 
	description="Methane reactor")
public class Methane implements SimulationElement{
	private File methaneDirectory;
	private Structure structure;
	
	public Methane(Structure struct, File dir) {
		structure = struct;
		methaneDirectory = new File(dir, "methane");
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
		SimulationPanel.activeElement.setText("Methane");
		String logStart = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logStart + "** Methane ... ");
		
		final File currentTimePath = new File(methaneDirectory, String.valueOf(structure.currentTime()));
		final File previousTimePath = new File(methaneDirectory, String.valueOf(structure.currentTime()-1));
		System.out.println("Running methane");
		System.out.println(currentTimePath);
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
			SpecfileUpdater.update_starttime(methaneFile, structure.currentTime());
			SpecfileUpdater.update_endtime(methaneFile, structure.currentTime()+1);
			
			new ElementExecution(methaneFile, currentTimePath, structure, this).execute();

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}



}

