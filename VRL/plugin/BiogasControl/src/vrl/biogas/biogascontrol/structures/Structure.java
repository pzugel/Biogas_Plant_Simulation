package vrl.biogas.biogascontrol.structures;

import java.io.IOException;

import eu.mihosoft.vrl.annotation.ComponentInfo;


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
	public int currentTime();
	public void incrementCurrentTime();
	public void cancelRun();
	public boolean wasCancelled();
	
	public void run(int currentStarttime) throws IOException;
	public void runNext() throws IOException;
	public boolean hasNext();
	public void fillQueue();
}
