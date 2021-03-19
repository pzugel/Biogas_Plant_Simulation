package vrl.biogas.biogascontrol.elements;

import java.io.IOException;

import vrl.biogas.biogascontrol.structures.Structure;

public class ElementRunner implements Runnable{
	private Structure structure;

	public ElementRunner(Structure struct) {
		this.structure = struct;
	}
	
	@Override
	public void run() {
		try {
			System.out.println("Element Runner!");
			structure.runNext();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}

}
