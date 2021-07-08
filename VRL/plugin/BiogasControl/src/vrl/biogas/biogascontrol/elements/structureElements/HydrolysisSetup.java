package vrl.biogas.biogascontrol.elements.structureElements;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import vrl.biogas.biogascontrol.elements.functions.ElementFunctions;
import vrl.biogas.biogascontrol.elements.structureElements.execution.ElementRunner;
import vrl.biogas.biogascontrol.structures.Structure;

/**
 * Biogas element: Hydrolysis
 * @author Paul Zügel
 *
 */
public class HydrolysisSetup implements SimulationElement, Serializable{
	private static final long serialVersionUID = 1L;
	private Structure structure;
	private File directory;
	
	public HydrolysisSetup(Structure struct) {
		structure = struct;
		directory = struct.directory();
	}
	
	@Override
	public String name() {
		return "HydrolysisSetup";
	}

	@Override
	public File path() {
		return directory;
	}

	@Override
	public void run() throws IOException, InterruptedException {
		System.out.println("--> Hydrolysis Setup");
		if(!structure.wasCancelled()) {
			int currentTime = structure.currentTime();
			boolean firstTimestep = structure.firstTimestep();
			String[] reactors = structure.hydrolysisNames();
			ElementFunctions.hydrolysisSetup(directory, currentTime, firstTimestep, reactors);
			
			ElementRunner myRunnable = new ElementRunner(structure);
			Thread t = new Thread(myRunnable);
			t.start();
		} else {
			System.out.println("Cancelled!");
			ElementRunner myRunnable = new ElementRunner(structure);
			Thread t = new Thread(myRunnable);
			t.start();
		}
	}
}
