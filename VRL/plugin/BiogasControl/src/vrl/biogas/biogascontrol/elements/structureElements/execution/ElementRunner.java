package vrl.biogas.biogascontrol.elements.structureElements.execution;

import java.io.IOException;
import java.io.Serializable;
import java.util.concurrent.ExecutionException;

import vrl.biogas.biogascontrol.structures.Structure;

public class ElementRunner implements Runnable, Serializable{
	private static final long serialVersionUID = 1L;
	private Structure structure;

	public ElementRunner(Structure struct) {
		structure = struct;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Element Runner --> ");
			structure.runNext();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}		
	}

}
