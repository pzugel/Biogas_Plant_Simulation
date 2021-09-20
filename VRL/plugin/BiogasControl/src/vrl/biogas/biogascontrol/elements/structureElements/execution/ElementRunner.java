package vrl.biogas.biogascontrol.elements.structureElements.execution;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import vrl.biogas.biogascontrol.structures.Structure;

/**
 * Calls the next {@link vrl.biogas.biogascontrol.elements.structureElements.SimulationElement} in the structure element queue.
 * @author Paul Zügel
 * 
 */
public class ElementRunner implements Runnable, Serializable{
	private static final long serialVersionUID = 1L;
	private Structure structure;

	/**
	 * Setup ElementRunner
	 * 
	 * @param struct Structure object
	 */
	public ElementRunner(Structure struct) {
		structure = struct;
	}
	
	/**
	 * Execute the strucutre
	 */
	@Override
	public void run() {
		try {
			structure.runNext();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}		
	}

}
