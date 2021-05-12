package vrl.biogas.biogascontrol.structures;

import java.io.Serializable;
import java.util.ArrayList;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.elements.structureElements.*;

/**
 * Predefined strucutre: 2_STAGE <br>
 * Implementing <br>
 *&emsp;- 2 {@link vrl.biogas.biogascontrol.elements.structureElements.Hydrolysis} Reactors <br>
 *&emsp;- {@link vrl.biogas.biogascontrol.elements.structureElements.StorageHydrolysis} <br>
 *&emsp;- {@link vrl.biogas.biogascontrol.elements.structureElements.Methane} Reactor <br>
 * @author Paul Zügel
 *
 */
@ComponentInfo(name="2_STAGE", 
	category="Biogas_Structures", 
	description="2_STAGE plant structure")
public class STRUCT_2_STAGE extends StructureFunctions implements Structure,Serializable {
	private static final long serialVersionUID = 1L;
	
	@Override
	public int numHydrolysis() {
		return 2;
	}
	
	@Override
	public String name() {
		return "2_STAGE";
	}
	
	@Override
	public boolean methane() {
		return true;
	}
	
	@Override
	public boolean storage() {
		return true;
	}
	
	@Override
	public boolean feedback() {
		return true;
	}
	
	@Override
	public String[] hydrolysisNames() {
		return new String[] {"hydrolyse_0", "hydrolyse_1"};
	}
	
	@Override
	public void fillQueue() {
		System.out.println("fillQueue()");
		
		reactorQueue = new ArrayList<SimulationElement>();
		reactorQueue.add(new Start(this));
		reactorQueue.add(new HydrolysisSetup(this));
		reactorQueue.add(new Hydrolysis(this, 0));
		reactorQueue.add(new Hydrolysis(this, 1));		
		reactorQueue.add(new StorageHydrolysis(this));
		reactorQueue.add(new Methane(this));
		reactorQueue.add(new MethaneMerge(this));
		reactorQueue.add(new Pause(this));
		reactorQueue.add(new Stop(this));
	}

}
