package vrl.biogas.biogascontrol.structures;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.elements.structureElements.SimulationElement;
import vrl.biogas.biogascontrol.elements.structureElements.execution.ElementRunner;

/**
 * Default functions for the {@link vrl.biogas.biogascontrol.structures.Structure} interface to be used by predefined structures
 * @author Paul Zügel
 *
 */
public class StructureFunctions implements Structure,Serializable{
	private static final long serialVersionUID = 1L;
	
	public static ArrayList<SimulationElement> reactorQueue;
	public static int time;
	public static boolean breakRun;
	public static BiogasControl panel;
	
	@Override
	public int numHydrolysis() {
		// Default
		return 0;
	}

	@Override
	public String name() {
		// Default
		return null;
	}

	@Override
	public boolean methane() {
		// Default
		return false;
	}

	@Override
	public boolean storage() {
		// Default
		return false;
	}

	@Override
	public boolean feedback() {
		// Default
		return false;
	}

	@Override
	public int currentTime() {
		return time;
	}
	  
	@Override
	public void incrementCurrentTime() {
		++ time;
	}

	@Override
	public void run(int currentStarttime) throws IOException { 
		breakRun = false;
		time = currentStarttime;
		fillQueue();
		ElementRunner myRunnable = new ElementRunner(this);
		Thread t = new Thread(myRunnable);
		t.start();
	}

	@Override
	public void runNext() throws IOException, ExecutionException {
		System.out.println("\n++++++ Structure runNext() ++++++");
		if(hasNext() && !breakRun) {
			try {
				reactorQueue.get(0).run();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}  
			reactorQueue.remove(0);
		}
	}

	@Override
	public boolean hasNext() {
		return !reactorQueue.isEmpty();
	}
	
	@Override
	public void cancelRun() {
		breakRun = true;
	}
	
	@Override
	public boolean wasCancelled() {
		return breakRun;
	}

	@Override
	public void fillQueue() {
		// Default		
	}

	@Override
	public boolean firstTimestep() {
		//TODO Not nice but works
		File hydrolysisDir = new File(directory(), hydrolysisNames()[0]);
		File previousTimeStep = new File(hydrolysisDir, String.valueOf(time - 1));
		return !previousTimeStep.exists();		
	}

	@Override
	public String[] hydrolysisNames() {
		// Default
		return null;
	}

	@Override
	public File directory() {
		// Default
		return BiogasControlClass.workingDirectory;
	}
}
