package vrl.biogas.biogascontrol.elements;

import java.io.IOException;
import java.io.Serializable;

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
			System.out.println("Element Runner!");
			structure.runNext();
		} catch (IOException e) {
			System.out.println("CATCH Element Runner");
			e.printStackTrace();
		}		
	}

}
