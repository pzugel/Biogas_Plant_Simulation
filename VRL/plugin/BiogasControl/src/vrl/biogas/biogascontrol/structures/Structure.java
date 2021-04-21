package vrl.biogas.biogascontrol.structures;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

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
	public boolean firstTimestep();
	public void incrementCurrentTime();
	public void cancelRun();
	public boolean wasCancelled();
	public String[] hydrolysisNames();
	public File directory();
	
	public void run(int currentStarttime) throws IOException;
	public void runNext() throws IOException, ExecutionException;
	public boolean hasNext();
	public void fillQueue();
}
