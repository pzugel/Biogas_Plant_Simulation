package vrl.biogas.biogascontrol.elements;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControlPlugin;
import vrl.biogas.biogascontrol.ExeWorker;
import vrl.biogas.biogascontrol.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Hydrolysis", 
	category="Biogas_Elements", 
	description="Hydrolysis reactor")
public class Hydrolysis implements SimulationElement{
	private String numeration;
	private Structure structure;
	private File directory;
	public Process proc;
	
	
	public Hydrolysis(String num, Structure struct, File dir) {
		numeration = num;
		structure = struct;
		directory = dir;
	}
	
	@Override
	public String name() {
	    return "Hydrolysis";
	}
	
	@Override
	public File path() {
	    return directory;
	}
	
	public String numeration() {
	    return numeration;
	}

	@Override
	public void run() {
		SimulationPanel.activeElement.setText("Hydrolysis");
		String logStart = SimulationPanel.simulationLog.getText();
		SimulationPanel.simulationLog.setText(logStart + "** Hydrolysis " + numeration + " ... ");
		
		final File hydrolysisPath = new File(BiogasControlPlugin.workingDirectory, "hydrolyse_" + numeration);
		final File currentTimePath = new File(hydrolysisPath, String.valueOf(BiogasControlPlugin.currenttime));
		System.out.println("Running hydrolysis");
		System.out.println(currentTimePath);
		if (!currentTimePath.exists()){
			currentTimePath.mkdirs();
		}
		
		try {
			File hydolysisFile = new File(currentTimePath, "hydrolysis_checkpoint.lua");
			
			Files.copy(new File(hydrolysisPath, "hydrolysis_startfile.lua").toPath(), 
					hydolysisFile.toPath(), 
					StandardCopyOption.REPLACE_EXISTING);
			String home = System.getProperty("user.home");
			File ugpath = new File(home, "ug4");
			File ugshell = new File(new File(ugpath, "bin").getAbsolutePath(), "ugshell");
			
			final String cmd = ugshell + " -ex " + BiogasControlPlugin.simulationFile + " -p " + hydolysisFile;
			System.out.println("cmd: " + cmd);
			//String[] env = {"ughshell"};
			//proc = Runtime.getRuntime().exec(cmd, env, currentTimePath);
			new ExeWorker(cmd, currentTimePath, structure, this).execute();

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

}
