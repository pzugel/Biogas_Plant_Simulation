package vrl.biogas.biogascontrol.structures;

import java.io.Serializable;
import java.util.ArrayList;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.elements.structureElements.HydrolysisParallel;
import vrl.biogas.biogascontrol.elements.structureElements.HydrolysisSetup;
import vrl.biogas.biogascontrol.elements.structureElements.Methane;
import vrl.biogas.biogascontrol.elements.structureElements.MethaneMerge;
import vrl.biogas.biogascontrol.elements.structureElements.Pause;
import vrl.biogas.biogascontrol.elements.structureElements.SimulationElement;
import vrl.biogas.biogascontrol.elements.structureElements.Start;
import vrl.biogas.biogascontrol.elements.structureElements.Stop;
import vrl.biogas.biogascontrol.elements.structureElements.StorageHydrolysis;

/**
 * Predefined strucutre: 2P_STAGE <br>
 * Implementing <br>
 *&emsp;- {@link vrl.biogas.biogascontrol.elements.structureElements.HydrolysisParallel} with 2 Reactors <br>
 *&emsp;- {@link vrl.biogas.biogascontrol.elements.structureElements.StorageHydrolysis} <br>
 *&emsp;- {@link vrl.biogas.biogascontrol.elements.structureElements.Methane} Reactor <br>
 * @author Paul Zügel
 *
 */
@ComponentInfo(name="2P_STAGE", 
category="Biogas_Structures", 
description="2P_STAGE plant structure")
public class STRUCT_2P_STAGE extends StructureFunctions implements Structure,Serializable {
	private static final long serialVersionUID = 1L;
	
	@Override
	public int numHydrolysis() {
		return 2;
	}
	
	@Override
	public String name() {
		return "2P_STAGE";
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
		return new String[] {"hydrolysis_0", "hydrolysis_1"};
	}
	
	@Override
	public void fillQueue() {
		System.out.println("fillQueue()");
		
		reactorQueue = new ArrayList<SimulationElement>();
		reactorQueue.add(new Start(this));
		reactorQueue.add(new HydrolysisSetup(this));
		reactorQueue.add(new HydrolysisParallel(this));	
		reactorQueue.add(new StorageHydrolysis(this));
		reactorQueue.add(new Methane(this));
		reactorQueue.add(new MethaneMerge(this));
		reactorQueue.add(new Pause(this));
		reactorQueue.add(new Stop(this));
	}
}
