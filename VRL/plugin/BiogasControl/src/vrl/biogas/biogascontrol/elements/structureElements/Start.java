package vrl.biogas.biogascontrol.elements.structureElements;

import java.io.File;
import java.io.Serializable;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.elements.structureElements.execution.ElementRunner;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

/**
 * Biogas element: Start
 * @author Paul Zügel
 *
 */
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
		System.out.println("--> Start");
		SimulationPanel simPanel = BiogasControl.simulationPanelObj;
		BiogasControl.feedingPanelObj.nextTimestep.setText(String.valueOf(BiogasControl.struct.currentTime() + 1));
		String log = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(log + "Iteration " + BiogasControl.iteration + " (Time " + BiogasControl.struct.currentTime() + ")\n");
		//CleanUp
		boolean cleanUp = BiogasControl.settingsPanelObj.autoCleanup.isSelected();
		if(cleanUp) {
			cleanUp();
		}
		ElementRunner myRunnable = new ElementRunner(structure);
		Thread t = new Thread(myRunnable);
		t.start();	
	}
	
	private void cleanUp() {
		System.out.println("cleanUp()");
		int currentTime = structure.currentTime();
		int previousTime = currentTime-2;
		File directory = structure.directory();
		
		//Methane
		File methanePath = new File(directory, "methane");
		File methaneTimePath = new File(methanePath, String.valueOf(previousTime));
		System.out.println("methaneTimePath: " + methaneTimePath);
		if(methaneTimePath.exists()) {
			String[] files = methaneTimePath.list();
			for(String f: files){
			    File currentFile = new File(methaneTimePath.getPath(), f);
			    currentFile.delete();
			}
			methaneTimePath.delete();
		}
		
		//Hydrolysis
		for(String name : structure.hydrolysisNames()) {
			File hydroPath = new File(directory, name);
			File hydroTimePath = new File(hydroPath, String.valueOf(previousTime));
			System.out.println("hydroTimePath: " + hydroTimePath);
			if(hydroTimePath.exists()) {
				String[] files = hydroTimePath.list();
				for(String f: files){
				    File currentFile = new File(hydroTimePath.getPath(), f);
				    currentFile.delete();
				}
				hydroTimePath.delete();
			}
		}		
	}
}
