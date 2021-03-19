package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Hydrolysis", 
	category="Biogas_Elements", 
	description="Hydrolysis reactor")
public class Hydrolysis implements SimulationElement{
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
		SimulationPanel.activeElement.setText("Hydrolysis");
		String logStart = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logStart + "** Hydrolysis " + numeration + " ... ");
		
		final File currentTimePath = new File(hydrolysisDirectory, String.valueOf(structure.currentTime()));
		final File previousTimePath = new File(hydrolysisDirectory, String.valueOf(structure.currentTime()-1));
		System.out.println("Running hydrolysis");
		System.out.println(currentTimePath);
		if (!currentTimePath.exists()){
			currentTimePath.mkdirs();
		}
		
		try {
			
			File hydolysisFile = new File(currentTimePath, "hydrolysis_checkpoint.lua");
			File previousSpec = new File(previousTimePath, "hydrolysis_checkpoint.lua");
			
			if(previousSpec.exists()) { //not first timestep
				Files.copy(previousSpec.toPath(), 
						hydolysisFile.toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
				SpecfileUpdater.update_read_checkpoint(hydolysisFile, previousTimePath);
			} else { //first timestep			
				Files.copy(new File(hydrolysisDirectory, "hydrolysis_startfile.lua").toPath(), 
						hydolysisFile.toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
			}
			SpecfileUpdater.update_starttime(hydolysisFile, structure.currentTime());
			SpecfileUpdater.update_endtime(hydolysisFile, structure.currentTime()+1);
			
			new ElementExecution(hydolysisFile, currentTimePath, structure, this).execute();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
