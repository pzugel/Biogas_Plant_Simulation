package vrl.biogas.biogascontrol.structures;

import java.util.ArrayList;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.elements.SimulationElement;

@ComponentInfo(name="Structure", 
	category="Biogas_Structures", 
	description="Structure interface")
public interface Structure {	
	//public static ArrayList<SimulationElement> reactorQueue = null;
	
	public int numHydrolysis();
	public String name();
	public boolean methane();
	public boolean storage();
	public boolean feedback();

	public void run();
	public void runNext();
	public boolean hasNext();
}
