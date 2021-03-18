package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.IOException;

import eu.mihosoft.vrl.annotation.ComponentInfo;

@ComponentInfo(name="SimulationElement", 
	category="Biogas_Elements", 
	description="Element interface")
public interface SimulationElement{
	public String name();
	public File path();
	public void run() throws IOException;
}
