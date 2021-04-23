package vrl.biogas.biogascontrol.elements.functions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.panels.SetupPanel;

public class ElementFunctions {

	public static void hydrolysisSetup(File directory, int currentTime, boolean firstTimestep, String[] reactors) throws IOException {
		System.out.println("Not Cancelled");
		
		//Write reactors to ArrayList
		ArrayList<File> specDirs = new ArrayList<File>();		
		for(String reactor : reactors) {
			File reactorDir = new File(directory, reactor);
			specDirs.add(new File(reactorDir, String.valueOf(currentTime) + File.separator + "hydrolysis_checkpoint.lua"));
		}			
		
		//Iterate hydrolysis reactors
		for(String reactor : reactors) {
			System.out.println("reactor: " + reactor);
			final File reactorPath = new File(directory, reactor);	
			final File currentTimePath = new File(reactorPath, String.valueOf(currentTime));
			final File previousTimePath = new File(reactorPath, String.valueOf(currentTime-1));
			
			//Create directory
			if (!currentTimePath.exists()){
				currentTimePath.mkdirs();
				System.out.println("create dir");
			}
			SetupPanel setPanel = BiogasControlClass.setupPanelObj;
			setPanel.update_tree(directory);
			
			try {			
				//Copy specification files
				File hydolysisFile = new File(currentTimePath, "hydrolysis_checkpoint.lua");
				File previousSpec = new File(previousTimePath, "hydrolysis_checkpoint.lua");			
				
				if(previousSpec.exists()) { //not first timestep
					Files.copy(previousSpec.toPath(), 
							hydolysisFile.toPath(), 
							StandardCopyOption.REPLACE_EXISTING);
					SpecfileUpdater.update_read_checkpoint(hydolysisFile, previousTimePath);
				} else { //first timestep			
					Files.copy(new File(reactorPath, "hydrolysis_startfile.lua").toPath(), 
							hydolysisFile.toPath(), 
							StandardCopyOption.REPLACE_EXISTING);
				}
				//Update starttime/endtime
				SpecfileUpdater.update_starttime(hydolysisFile, currentTime);
				SpecfileUpdater.update_endtime(hydolysisFile, currentTime+1);
					
			} catch (IOException e) {
				e.printStackTrace();
			} 	
		}		
		
		//Get fractions from feedback sliders
		ArrayList<Double> fractions = BiogasControlClass.feedbackPanelObj.computeFractions();
		Double[] fractionsDoubleArr = new Double[fractions.size()];
		fractions.toArray(fractionsDoubleArr);
		
		double[] fractionsArr = new double[fractionsDoubleArr.length];
		int i = 0;
		for(Double d : fractionsDoubleArr) {
			fractionsArr[i] = (double) d;
			i++;
		}
		
		//Update inflow in hydrolysis specification
		File[] specDirsArr = new File[specDirs.size()];
		specDirs.toArray(specDirsArr);
		final File methanePath = new File(directory, "methane");
		File outflowFile = new File(methanePath, "outflow_integratedSum_fullTimesteps.txt");
		if(!firstTimestep) {
			System.out.println("Not first timestep --> OutflowInflowUpdater");
			// TODO Check if this works!
			System.out.println("outflowFile: " + outflowFile);
			System.out.println("specDirsArr: " + Arrays.toString(specDirsArr));
			System.out.println("fractions: " + Arrays.toString(fractionsArr));
			OutflowInflowUpdater.write_hydrolysis_inflow(outflowFile, specDirsArr, fractionsArr);
		}
	}
	
	public static void methaneSetup(File workingDirectory, int currentTime) {
		File methaneDirectory = new File(workingDirectory, "methane");
		File storageDirectory = new File(workingDirectory, "storage_hydrolysis");
		
		SetupPanel setPanel = BiogasControlClass.setupPanelObj;
		final File currentTimePath = new File(methaneDirectory, String.valueOf(currentTime));
		final File previousTimePath = new File(methaneDirectory, String.valueOf(currentTime-1));
		System.out.println("Running methane");
		System.out.println("currentTimePath : " + currentTimePath);
		
		//Create directory
		if (!currentTimePath.exists()){
			currentTimePath.mkdirs();
		}
		setPanel.update_tree(workingDirectory);
		
		try { //Copy specifications
			File methaneFile = new File(currentTimePath, "methane_checkpoint.lua");			
			File previousSpec = new File(previousTimePath, "methane_checkpoint.lua");
			
			if(previousSpec.exists()) { //not first timestep
				Files.copy(previousSpec.toPath(), 
						methaneFile.toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
				SpecfileUpdater.update_read_checkpoint(methaneFile, previousTimePath);			
			} else { //first timestep			
				Files.copy(new File(methaneDirectory, "methane.lua").toPath(), 
						methaneFile.toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
			}
			
			File outflowFile = new File(storageDirectory, "outflow_integratedSum_fullTimesteps.txt");
			System.out.println("outflowFile: " + outflowFile.toString());
			
			//Update specification
			OutflowInflowUpdater.write_methane_inflow(outflowFile, methaneFile);
			SpecfileUpdater.update_starttime(methaneFile, currentTime);
			SpecfileUpdater.update_endtime(methaneFile, currentTime+1);
		}
		catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public static void writeSummary(File dir, boolean finished, String structureName, int numHydrolysis, int currentTime) throws IOException {
		String summary = "FINISHED=" + finished + "\n";
		summary += "WORKING_DIR=" + dir + "\n";
		summary += "STRUCTURE=" + structureName + "\n";
		summary += "NUM_HYDROLYSIS=" + numHydrolysis + "\n";
		summary += "STARTTIME=" + BiogasControl.settingsPanelObj.simStarttime.getValue() + "\n";
		int endtime = currentTime+1;
		summary += "ENDTIME=" + endtime + "\n";
		summary += "PREEXISTING=" + BiogasControl.setupPanelObj.mergePreexisting + "\n";
		summary += "RUNTIME=" + BiogasControl.simulationPanelObj.runtime.getText() + "\n";
		
		File summaryFile = new File(dir, "simulation_summary.txt");		
		
		//File summaryFile = new File(summary);
		if (!summaryFile.exists()) { //Create summary
			FileWriter summaryWriter = new FileWriter(summaryFile);
			summaryWriter.write(summary);
			summaryWriter.close();
		} else { //Update summary
			String existingSummary = "";
			Scanner lineIter = new Scanner(summaryFile);		
			while (lineIter.hasNextLine()) {
				existingSummary += lineIter.nextLine() + "\n";
			}
			lineIter.close();
			existingSummary += "RUNTIME=" + BiogasControl.simulationPanelObj.runtime.getText() + "\n";
			
			String endtimeString = "";
			Pattern pEndtime = Pattern.compile("ENDTIME=[0-9]+");
			Matcher mEndtime = pEndtime.matcher(existingSummary);
			if(mEndtime.find()) {				
				System.out.println("FOUND ENDTIME ENTRY");
				endtimeString = mEndtime.group(0);
			}
			System.out.println("Old endtime: " + endtime);
			int newEndtime = currentTime+1;
			System.out.println("New endtime: " + newEndtime);
			existingSummary = existingSummary.replace(endtimeString, "ENDTIME=" + newEndtime);
			
			String preexisting = "";
			Pattern pPreexisting = Pattern.compile("PREEXISTING=(true|false)");
			Matcher mPreexisting = pPreexisting.matcher(existingSummary);
			if(mPreexisting.find()) {	
				System.out.println("FOUND PREEXISTING ENTRY");
				preexisting = mPreexisting.group(0);
			}
			existingSummary = existingSummary.replace(preexisting, "PREEXISTING=true");
			
			FileWriter summaryWriter = new FileWriter(summaryFile, false);
			summaryWriter.write(existingSummary);
			summaryWriter.close();
		}		
	}
}
