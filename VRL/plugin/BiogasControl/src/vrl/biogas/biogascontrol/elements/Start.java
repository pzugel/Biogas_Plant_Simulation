package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.Serializable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Start", 
	category="Biogas_Elements", 
	description="Start element")
public class Start implements SimulationElement, Serializable{	
	private static final long serialVersionUID = 1L;
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
		SimulationPanel simPanel = BiogasControl.simulationPanelObj;
		String log = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(log + "Iteration " + BiogasControl.iteration + "\n");
		System.out.println("Start done!");
		ElementRunner myRunnable = new ElementRunner(structure);
		Thread t = new Thread(myRunnable);
		t.start();	
	}
}
