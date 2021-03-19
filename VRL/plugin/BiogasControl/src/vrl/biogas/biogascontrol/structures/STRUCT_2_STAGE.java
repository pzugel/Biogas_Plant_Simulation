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
	public static int time;
	public static boolean breakRun;
	  
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
	public int currentTime() {
		return time;
	}
	  
	@Override
	public void incrementCurrentTime() {
		++ time;
	}
	
	@Override
	public void run(int currentStarttime) throws IOException { 
		System.out.println("2_STAGE_STRUCT");
		breakRun = false;
		time = currentStarttime;
		fillQueue();
		ElementRunner myRunnable = new ElementRunner(this);
		Thread t = new Thread(myRunnable);
		t.start();
	}
	  
	@Override
	public void runNext() throws IOException {
		System.out.println("runNext()");
		if(hasNext() && !breakRun) {
			System.out.println("queue size before: " + reactorQueue.size());
			try {
				reactorQueue.get(0).run();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
			reactorQueue.remove(0);
			System.out.println("queue size after: " + reactorQueue.size());
		}
	}
	  
	@Override
	public boolean hasNext() {
		return !reactorQueue.isEmpty();
	}
	  
	public void fillQueue() {
		System.out.println("fillQueue()");
		File dir = BiogasControlPlugin.workingDirectory;
		reactorQueue = new ArrayList<SimulationElement>();
		reactorQueue.add(new Start(this));
		reactorQueue.add(new Hydrolysis(this, dir, 0));
		reactorQueue.add(new Hydrolysis(this, dir, 1));
		String[] reactorNames = {"hydrolyse_0", "hydrolyse_1"};
		reactorQueue.add(new Storage(this, dir, reactorNames));
		reactorQueue.add(new Methane(this, dir));
		reactorQueue.add(new Pause(this));
		reactorQueue.add(new Stop(this));
	}

	@Override
	public void cancelRun() {
		breakRun = true;
	}
	
	@Override
	public boolean wasCancelled() {
		return breakRun;
	}
}
