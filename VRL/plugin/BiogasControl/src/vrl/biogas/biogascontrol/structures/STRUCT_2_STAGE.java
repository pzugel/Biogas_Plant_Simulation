package vrl.biogas.biogascontrol.structures;

import java.io.Serializable;
import java.util.ArrayList;

import eu.mihosoft.vrl.annotation.ComponentInfo;
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
  public void run() {
	  System.out.println("2_STAGE_STRUCT");
	  reactorQueue = new ArrayList<SimulationElement>();
	  reactorQueue.add(new Hydrolysis("0", this));
	  reactorQueue.add(new Hydrolysis("1", this));
	  runNext();
  }
  
  @Override
  public void runNext() {
	  System.out.println("runNext()");
	  if(hasNext()) {
		  System.out.println("hasNext()");
		  reactorQueue.get(0).run();  
		  reactorQueue.remove(0);
	  }
  }
  
  @Override
  public boolean hasNext() {
	  return !reactorQueue.isEmpty();
  }

}
