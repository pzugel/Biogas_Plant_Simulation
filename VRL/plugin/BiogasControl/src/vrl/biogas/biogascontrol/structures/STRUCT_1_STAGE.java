package vrl.biogas.biogascontrol.structures;

import java.io.Serializable;
import java.util.ArrayList;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.elements.Hydrolysis;
import vrl.biogas.biogascontrol.elements.HydrolysisSetup;
import vrl.biogas.biogascontrol.elements.Methane;
import vrl.biogas.biogascontrol.elements.MethaneMerge;
import vrl.biogas.biogascontrol.elements.Pause;
import vrl.biogas.biogascontrol.elements.SimulationElement;
import vrl.biogas.biogascontrol.elements.Start;
import vrl.biogas.biogascontrol.elements.Stop;
import vrl.biogas.biogascontrol.elements.StorageHydrolysis;

@ComponentInfo(name="1_STAGE", 
	category="Biogas_Structures", 
	description="1_STAGE plant structure")
public class STRUCT_1_STAGE extends StructureFunctions implements Structure,Serializable {
	private static final long serialVersionUID = 1L;
	
	@Override
	public int numHydrolysis() {
		return 1;
	}
	
	@Override
	public String name() {
		return "1_STAGE";
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
		return new String[] {"TEST0"};
	}
	
	@Override
	public void fillQueue() {
		System.out.println("fillQueue()");		
		reactorQueue = new ArrayList<SimulationElement>();
		reactorQueue.add(new Start(this));
		reactorQueue.add(new HydrolysisSetup(this));
		reactorQueue.add(new Hydrolysis(this, 0));	
		reactorQueue.add(new StorageHydrolysis(this));
		reactorQueue.add(new Methane(this));
		reactorQueue.add(new MethaneMerge(this));
		reactorQueue.add(new Pause(this));
		reactorQueue.add(new Stop(this));
	}
}
