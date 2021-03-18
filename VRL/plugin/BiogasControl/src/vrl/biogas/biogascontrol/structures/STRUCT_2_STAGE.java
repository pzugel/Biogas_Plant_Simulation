package vrl.biogas.biogascontrol.structures;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControlPlugin;
import vrl.biogas.biogascontrol.elements.*;

@ComponentInfo(name="2_STAGE", 
  category="Biogas_Structures", 
  description="2_STAGE plant structure")
public class STRUCT_2_STAGE implements Structure,Serializable{
  private static final long serialVersionUID = 1L;
  public static ArrayList<SimulationElement> reactorQueue;
  
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
  public void run() throws IOException {
	  System.out.println("2_STAGE_STRUCT");
	  File dir = BiogasControlPlugin.workingDirectory;
	  reactorQueue = new ArrayList<SimulationElement>();
	  reactorQueue.add(new Hydrolysis("0", this, new File(dir,"hydrolyse_0")));
	  reactorQueue.add(new Hydrolysis("1", this, new File(dir,"hydrolyse_1")));
	  String[] reactorNames = {"hydrolyse_0", "hydrolyse_1"};
	  reactorQueue.add(new Storage(dir, reactorNames));
	  runNext();
  }
  
  @Override
  public void runNext() throws IOException {
	  System.out.println("runNext()");
	  if(hasNext()) {
		  reactorQueue.get(0).run();  
		  reactorQueue.remove(0);
	  }
  }
  
  @Override
  public boolean hasNext() {
	  return !reactorQueue.isEmpty();
  }

}
