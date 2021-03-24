package vrl.biogas.biogascontrol.structures;

import java.io.IOException;

import eu.mihosoft.vrl.annotation.ComponentInfo;

@ComponentInfo(name="UserStructure", 
	category="Biogas_Structures", 
	description="UserStructure interface")
public interface UserStructure {
	public int numHydrolysis();
	public String name();
	public int currentTime();
	public void incrementCurrentTime();
	public void cancelRun();
	public boolean wasCancelled();
	
	public void run(int currentStarttime) throws IOException;
}
