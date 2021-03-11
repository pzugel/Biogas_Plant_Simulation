package vrl.biogas.biogascontrol.structures;

import java.io.Serializable;
import eu.mihosoft.vrl.annotation.ComponentInfo;

@ComponentInfo(name="2_STAGE", 
  category="Biogas_Structures", 
  description="2_STAGE plant structure")
public class STRUCT_2_STAGE implements vrl.biogas.biogascontrol.Structure,Serializable{
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
  public void run() {
    // TODO Auto-generated method stub
    System.out.println("Run");
  }

}
