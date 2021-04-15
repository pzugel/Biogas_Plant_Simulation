package vrl.biogas.biogascontrol.structures;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.elements.*;

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
	public void fillQueue() {
		System.out.println("fillQueue()");
		File dir = BiogasControlClass.workingDirectory;
		
		reactorQueue = new ArrayList<SimulationElement>();
		reactorQueue.add(new Start(this));
		String[] reactorNames = {"hydrolyse_0", "hydrolyse_1"};
		reactorQueue.add(new HydrolysisSetup(this, dir, reactorNames));
		reactorQueue.add(new Hydrolysis(this, dir, 0));
		reactorQueue.add(new Hydrolysis(this, dir, 1));		
		reactorQueue.add(new StorageHydrolysis(this, dir, reactorNames));
		reactorQueue.add(new Methane(this, dir));
		reactorQueue.add(new MethaneMerge(this, dir));
		reactorQueue.add(new Pause(this));
		reactorQueue.add(new Stop(this));
	}

}
