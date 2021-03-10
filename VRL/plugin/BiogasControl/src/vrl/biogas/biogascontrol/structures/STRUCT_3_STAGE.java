package vrl.biogas.biogascontrol.structures;

import java.io.Serializable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.Structure;

@ComponentInfo(name="3_STAGE", 
	category="Biogas_Structures", 
	description="3_STAGE plant structure")
public class STRUCT_3_STAGE implements Structure,Serializable{
	private static final long serialVersionUID = 1L;
	
	@Override
	public int numHydrolysis() {
		return 3;
	}

	@Override
	public String name() {
		return "3_STAGE";
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