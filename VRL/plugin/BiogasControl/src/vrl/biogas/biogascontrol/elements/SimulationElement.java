package vrl.biogas.biogascontrol.elements;

import eu.mihosoft.vrl.annotation.ComponentInfo;

@ComponentInfo(name="SimulationElement", 
	category="Biogas_Elements", 
	description="Element interface")
public interface SimulationElement{
	public String name();
	public void run();
}
