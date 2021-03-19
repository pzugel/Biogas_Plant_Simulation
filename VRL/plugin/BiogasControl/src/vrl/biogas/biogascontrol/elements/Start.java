package vrl.biogas.biogascontrol.elements;

import java.io.File;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControlPlugin;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Start", 
	category="Biogas_Elements", 
	description="Start element")
public class Start implements SimulationElement{	
	private Structure structure;
	
	public Start(Structure struct) {
		structure = struct;
	}
	
	@Override
	public File path() {
		return null;
	}
	
	@Override
	public String name() {
		return "Start";
	}
	
	@Override
	public void run() throws InterruptedException {
		System.out.println("Start here!");
		String log = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(log + "Iteration " + BiogasControlPlugin.iteration + "\n");
		System.out.println("Start done!");
		ElementRunner myRunnable = new ElementRunner(structure);
		Thread t = new Thread(myRunnable);
		t.start();	
	}
}
