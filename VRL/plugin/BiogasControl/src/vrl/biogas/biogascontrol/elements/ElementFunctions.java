package vrl.biogas.biogascontrol.elements;

public class ElementFunctions {
	void merge() {
		String a = ((SimulationElement) this).name();
		System.out.println("Merge " + a);
	}
}
