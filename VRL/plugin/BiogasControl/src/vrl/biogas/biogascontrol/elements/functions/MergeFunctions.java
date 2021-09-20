package vrl.biogas.biogascontrol.elements.functions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Scanner;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasControlClass;

/**
 * Functions to be called when merging files in the hydrolysis or methane reactor
 * @author Paul Zügel
 */
public class MergeFunctions {
	
	private final static String[] output_files = {
		"digestateConcentrations.txt",
		"subMO_mass.txt",
		"producedNormVolumeCumulative.txt",
		"dbg_nitrogenRates.txt",
		"producedNormVolumeHourly.txt",
		"dbg_phContribution.txt",
		"outflow_integratedSum_fullTimesteps.txt"};
	
	private final static String[] all_files ={
		"digestateConcentrations.txt",
		"subMO_mass.txt",
		"valveGasFlow.txt",
		"gas_Volfraction.txt",
		"producedNormVolumeCumulative.txt",
		"dbg_avgEqValues.txt",
		"dbg_nitrogenRates.txt",
		"producedNormVolumeHourly.txt",
		"dbg_phContribution.txt",
		"outflow.txt",
		"reactorState.txt",
		"dbg_reactionrates.txt"};
	
	private final static String[] output_files_integration ={
		"outflow.txt"};
	
	private final static String[] output_files_nonAdditive = {
		"dbg_avgEqValues.txt",
		"gas_Volfraction.txt",
		"reactorState.txt",
		"dbg_gamma.txt"};
	
	private static ArrayList<ArrayList<Double>> mergedArray;
	
	/**
	 * Function called by the hydrolysis or methane reactor after finishing the computations.
	 * Calls the merge_one_reactor() method to concat all files from all timesteps.
	 * 
	 * @param reactor_name Name of the reactor to be merged, e.g. hydrolysis_0
	 * @param currenttime Current time of the simulation
	 * @param timePath Joined path from the reactor name and the current timestep
	 * @throws IOException
	 */
	public static void merge(String reactor_name, int currenttime, File timePath) throws IOException {
		File basePath = timePath.getParentFile();
		int startTime = (Integer) BiogasControlClass.settingsPanelObj.simStarttime.getValue();
		boolean preexisting = BiogasControl.setupPanelObj.mergePreexisting;		
		
		System.out.println("\n\t********************************************************************************");
		System.out.println("\tMerge " + reactor_name);
		System.out.println("\tPreexisting?: " + preexisting);
		System.out.println("\tStartTime: " + startTime);
		System.out.println("\tCurrentTime: " + currenttime);
		System.out.println("\t\t---> " + timePath);
		System.out.println("\t\t---> " + basePath);
		System.out.println("\t********************************************************************************");
		
		merge_one_reactor(basePath.toString(), startTime, currenttime, preexisting);
	}
	
	/**
	 * Function called by the storage element. Merges all files from all hydrolysis reactors and timesteps 
	 * via merge_hydrolysis_files() and integrates via merge_files_integration().
	 * 
	 * Also calls merge_storage_outflow() to merge the outflow files of all hydrolysis reactors, which 
	 * needs to be done separately.
	 * 
	 * @param storage_dir Path to the "storage_hydrolysis"
	 * @param working_dir Working directory
	 * @param reactor_names	Names of the reactors to be merged
	 * @throws IOException
	 */
	public static void merge_all_hydrolysis(
			File storage_dir,
			File working_dir,
			String[] reactor_names) throws IOException 
	{					
		
		for(String f: output_files_integration) {			
			//Integrate outflow files in each hydrolysis reactor
			for(String r : reactor_names) {
				integrate_one_file(new File(working_dir, r),f);		
			}
		}
			
		//Merge hydrolysis files in each reactor (no integration)
		for(String f: output_files) {
			
			String output_file_string = merge_hydrolysis_files(
				working_dir, 
				f, 
				reactor_names);
			
			//If file does not exist an empty string is returned
			if(!output_file_string.isEmpty()){
				File output_file_name = new File(storage_dir, f);
				
				Writer output = new BufferedWriter(new FileWriter(output_file_name));
				output.append(output_file_string);
				output.close();		
			}
		}
		
		merge_storage_outflow(storage_dir, working_dir, reactor_names);
	}
	
	/**
	 * Used for the storage_hydrolysis. Called by the merge_all_hydrolysis() function to merge 
	 * the outflow files out all hydrolysis reactors. We add the "outflow_integratedSum_fullTimesteps" 
	 * files from all hydrolysis reactors and recompute the rates in [L/h] and [g/L]. The result will
	 * be written into the "outflow_integratedSum_Rates" file. This file can be used to update the 
	 * inflow for the methane specifications.
	 * 
	 * @param storage_dir Path to the "storage_hydrolysis"
	 * @param working_dir Working directory
	 * @param reactor_names Names of the hydrolysis reactors
	 * @throws IOException
	 */
	private static void merge_storage_outflow(File storage_dir, File working_dir, String[] reactor_names) throws IOException {
		
		//Writes "mergedArray" which contains the sum of all "outflow_integratedSum_fullTimesteps" files
		merge_hydrolysis_files(working_dir, "outflow_integratedSum_fullTimesteps.txt", reactor_names);
				
		//This part is the same as in the integrate_one_file() function
		for(ArrayList<Double> line : mergedArray) {
			int line_size = line.size();
			double all_liquid = line.get(1); //In [L/h] - But since h=1 it can be read as [L]
			
			for(int j=2; j<line_size; j++) {
				double gram = line.get(j);
				double gram_per_liter = 0.0;
				
				if(all_liquid > 0.0) {
					gram_per_liter = gram/all_liquid; //Recompute [g/L]
				}
				line.set(j, gram_per_liter);
			}
		}
		
		File pathOfFirstReactor = new File(working_dir, reactor_names[0]);
		File pathOfFirstOutflow = new File(pathOfFirstReactor, "outflow_integratedSum_Rates.txt");
		String output_file_string = HelperFunctions.get_header(pathOfFirstOutflow);
		for(ArrayList<Double> line : mergedArray) {
			for(Double d : line) {
				output_file_string += String.valueOf(d) + "\t";
			}
			output_file_string += "\n";
		}	
			
		File newFile = new File(storage_dir, "outflow_integratedSum_Rates.txt");
		FileWriter myWriter = new FileWriter(newFile);
		myWriter.write(output_file_string);
		myWriter.close();
		
	}
	
	/**
	 * Called by the methane element to integrate the outflow files.
	 * 
	 * @param methane_dir Path to the methane element
	 * @throws IOException
	 */
	public static void merge_all_methane(File methane_dir) throws IOException 
	{							
		//Merge methane files with integration
		for(String f: output_files_integration) {
			integrate_one_file(methane_dir, f);
		}
	}
	
	/**
	 * Used for the hydrolysis storage to sum up files from all hydrolysis reactors.
	 * Needs to check if entries that need to be merged exist in all files.
	 * 
	 * @param working_dir Working directory
	 * @param filename Name of the file to be merged
	 * @param reactor_names Names of the hydrolysis reactors
	 * @return output_file_string
	 * @throws FileNotFoundException
	 */
	private static String merge_hydrolysis_files(
			File working_dir, 
			String filename, 
			String[] reactor_names) throws FileNotFoundException
	{
		int num_reactors = reactor_names.length;
		String output_file_string = "";
		HelperFunctions.values = new ArrayList<ArrayList<ArrayList<String>>>();
		
		//Add header to new file
		String dir_for_header = working_dir.toString() + File.separator  + reactor_names[0] + File.separator  + filename;
		output_file_string += HelperFunctions.get_header(new File(dir_for_header)); 
		
		
		//Write data into "values" vector - Check if file exists for every reactor
		boolean allExist = true;
		for(String d: reactor_names) {
			String file_direction = working_dir + File.separator
				+ d + File.separator 
				+  filename;
			allExist = allExist && HelperFunctions.read_values_from_reactor(file_direction);
		}	
		
		/*
		 * Reactors might have different timesteps. Therefore we first need to check
		 * if a timestep exists in all files for all reactors
		 */
		if(allExist)
		{			
						
			ArrayList<String> timeCol = new ArrayList<String>(); //Contains the times from first reactor file
			for(ArrayList<String> line : HelperFunctions.values.get(0)) {
				timeCol.add(line.get(0));
			}
			
			//Check if all reactors contain a timestep
			ArrayList<String> validTimes = new ArrayList<String>(); //All valid timesteps
			for(String time : timeCol) {
				boolean exists = true;
				for(int i=0; i<num_reactors; i++) {
					boolean found = false;
					int numLines = HelperFunctions.values.get(i).size();
					for(int j=0; j<numLines; j++) {
						if(HelperFunctions.values.get(i).get(j).get(0).equals(time)) {
							found = true;
						}
					}
					exists = exists && found;
				}
				
				if(exists) {
					validTimes.add(time);
				}
			}
			
			//Initialize an output array
			int num_entries_line = HelperFunctions.values.get(0).get(0).size(); //Entries per line
			ArrayList<ArrayList<Double>> outputValues = new ArrayList<ArrayList<Double>>();
			for(String time : validTimes) {
				ArrayList<Double> line = new ArrayList<Double>();
				line.add(Double.valueOf(time));
				for(int i=1; i<num_entries_line; i++) {
					line.add(0.0);
				}
				outputValues.add(line);
			}
			
			//Summation over all files and timesteps
			int lineCount = 0;
			for(String time : validTimes) {
				for(int i=0; i<num_reactors; i++) {
					for(int j=0; j<HelperFunctions.values.get(i).size(); j++) {
						if(HelperFunctions.values.get(i).get(j).get(0).equals(time)) {
							for(int k=1; k<num_entries_line; k++) {
								double sum = outputValues.get(lineCount).get(k) + Double.valueOf(HelperFunctions.values.get(i).get(j).get(k));
								outputValues.get(lineCount).set(k, sum);
							}
						}
					}
				}
				++lineCount;
			}
			
			//Write output array to string
			mergedArray = outputValues;
			for(ArrayList<Double> line : outputValues) {
				for(Double d : line) {
					output_file_string += String.valueOf(d) + "\t";
				}
				output_file_string += "\n";
			}			
		}	
		return output_file_string;
	}
	
	/**
	 * Called by hydrolysis and methane. Only concat the files from different timesteps.
	 * 
	 * @param working_dir Working directory
	 * @param simulation_starttime Starttime
	 * @param current_starttime Current time
	 * @param merge_preexisting Do prior simulation files exist? 
	 * @throws IOException
	 */
	private static void merge_one_reactor(
			String working_dir,
			int simulation_starttime,
			int current_starttime,
			boolean merge_preexisting) throws IOException
	{	
		File timestep_dir = new File(working_dir, String.valueOf(current_starttime));
		
		boolean is_first_timestep = false;
		if(simulation_starttime == current_starttime)
		{
			is_first_timestep = true;
			//Copy outputFiles.lua
			File outputFiles = new File(timestep_dir, "outputFiles.lua");
			File outputFilesDst = new File(working_dir, "outputFiles.lua");
			Files.copy(outputFiles.toPath(), outputFilesDst.toPath(), StandardCopyOption.REPLACE_EXISTING);			
		}

		for(String f: all_files) {
			File input_file_name = new File(timestep_dir, f);
			File output_file_name = new File(working_dir, f);
			
			Writer output;
			if(input_file_name.exists())
			{	
				
				if(is_first_timestep){
					if(merge_preexisting)
					{
						if(output_file_name.exists()) //merge with previous files
						{
							output = new BufferedWriter(new FileWriter(output_file_name, true));
							String data = HelperFunctions.remove_header(input_file_name);
							output.append(data);
							output.close();
						}
						else //should merge but no previous files found
						{
							output = new BufferedWriter(new FileWriter(output_file_name));
							String header = HelperFunctions.get_header(input_file_name);
							String data = HelperFunctions.remove_header(input_file_name);
							output.append(header);
							output.append(data);
							output.close();
						}
					}
					else //dont merge
					{
						output = new BufferedWriter(new FileWriter(output_file_name));
						String header = HelperFunctions.get_header(input_file_name);
						String data = HelperFunctions.remove_header(input_file_name);
						output.append(header);
						output.append(data);
						output.close();
					}
				}
				else{
					output = new BufferedWriter(new FileWriter(output_file_name, true));
					String data = HelperFunctions.remove_header(input_file_name);
					output.append(data);
					output.close();
				}
			}			
		}
	}
	
	/**
	 * Integrates a single file "f" and writes out
	 * --> f_integrated
	 * --> f_integratedSum
	 * --> f_integratedSum_fullTimesteps
	 * --> f_integrated_merged
	 * 
	 * @param reactorDir Path to reactor
	 * @param f File to be merged
	 * @return
	 * @throws IOException
	 */
	private static String integrate_one_file(
			File reactorDir,
			String f) throws IOException 
	{		
		System.out.println("\t integrate_one_file");
		System.out.println("\t f --> " + f);
		
		HelperFunctions.values = new ArrayList<ArrayList<ArrayList<String>>>();
		File fileDir = new File(reactorDir, f);
		System.out.println("\t fileDir: " + fileDir);
		if(!fileDir.exists()) {
			System.out.println("\t file does not exist!");
			return "";
		}
		HelperFunctions.read_values_from_reactor(fileDir.toString());
		
		ArrayList<ArrayList<String>> values = HelperFunctions.values.get(0);				
		ArrayList<ArrayList<String>> integratedValues = new ArrayList<ArrayList<String>>();
		
		int timesteps = values.size();
		int numValues = values.get(0).size();

		for(int i=0; i<timesteps; i++) { //time iteration							
			ArrayList<String> line = new ArrayList<String>();			
			
			double previous_time;
			if(i == 0) { 
				previous_time = Math.floor(Double.valueOf(values.get(0).get(0))); //First timestep
			}
			else {
				previous_time = Double.valueOf(values.get(i-1).get(0)); //Previous timestep
			}
			
			double time = Double.valueOf(values.get(i).get(0)); //Time [h]
			double stepsize = time-previous_time;
			
			double all_liquid = Double.valueOf(values.get(i).get(1)); //All Liquid [L/h] 
			double liquid_per_timestep = all_liquid * stepsize; //Liquid in [L]

			line.add(String.valueOf(time));
			line.add(String.valueOf(liquid_per_timestep));
			
			for(int j=2; j<numValues; j++) { //parameter iteration
				
				double amount = Double.valueOf(values.get(i).get(j)); //[g/L]
				
				double amount_in_grams = amount * liquid_per_timestep; //[g]
				line.add(String.valueOf(amount_in_grams));
			}
			integratedValues.add(line);
		}				
		
		/*
		 * Compute sum for full timesteps only.
		 * This is needed because files might have different substeps.
		 * 
		 * First compute the sum for every step:
		 */
		ArrayList<ArrayList<String>> integratedValuesSum = new ArrayList<ArrayList<String>>();
		ArrayList<String> sumLines = new ArrayList<String>();
		for(int i=0; i<integratedValues.get(0).size(); i++) {
			sumLines.add("0.0"); //Initialize
		}
			
		double firstTimestep = Double.valueOf(integratedValues.get(0).get(0));
		int firstTimestepFull = (int) firstTimestep;
		for(ArrayList<String> line : integratedValues) {
			sumLines.set(0, line.get(0)); //Add time entry
			
			double currentTime = Double.valueOf(line.get(0));
			int currentTimeFull = (int) currentTime;
			
			for(int i=1; i<line.size(); i++) {			
				double sum = Double.valueOf(sumLines.get(i)) + Double.valueOf(line.get(i));
				sumLines.set(i, String.valueOf(sum));
			}
			integratedValuesSum.add(new ArrayList<String>(sumLines));
			
			if(currentTimeFull != firstTimestepFull) {
				firstTimestepFull = currentTimeFull;
				for(int i=0; i<sumLines.size(); i++) {
					sumLines.set(i, "0.0"); //Reset	
				}
			}
		}
		
		/*
		 * Now take only the full steps
		 */
		ArrayList<ArrayList<String>> integratedValuesSumFull = new ArrayList<ArrayList<String>>();
		for(ArrayList<String> line : integratedValuesSum) {
			double time = Double.valueOf(line.get(0));
			if ((time == Math.floor(time)) && !Double.isInfinite(time)) { //Is full timestep?
				ArrayList<String> lineCopy = new ArrayList<String>(line); //Copy the line, otherwise there will be reference issues
				integratedValuesSumFull.add(lineCopy);
			}
		}

		/*
		 * Once we have the full timesteps (for one hour each) 
		 * the "All Liquid" value in [L] are actually in [L/h]
		 * since we have hourly intervals.
		 * 
		 * We can now recompute every parameter in [g/L] by simply dividing
		 * the grams with the "All Liquid" value.
		 * 
		 * We do this because for the inflow we expect all 
		 * parameters in [g/L] and the total amount in [L/h] 
		 */
		ArrayList<ArrayList<String>> integratedValuesMerged = new ArrayList<ArrayList<String>>();
		for(ArrayList<String> line : integratedValuesSumFull) {
			ArrayList<String> lineCopy = new ArrayList<String>(line);
			int line_size = lineCopy.size();
			double all_liquid = Double.valueOf(lineCopy.get(1)); //In [L/h] - But since h=1 it can be read as [L]
			
			for(int j=2; j<line_size; j++) {
				double gram = Double.valueOf(lineCopy.get(j));
				double gram_per_liter = 0.0;
				
				if(all_liquid > 0.0) {
					gram_per_liter = gram/all_liquid; //Recompute [g/L]
				}
				lineCopy.set(j, String.valueOf(gram_per_liter));
			}
			integratedValuesMerged.add(lineCopy);
		}

		/*
		 * Write out different files
		 */
		int extensionPos = fileDir.toString().lastIndexOf(".");
		String header = HelperFunctions.get_header(fileDir);
		String integratedHeader = header;
		integratedHeader = integratedHeader.replaceAll("L/h", "L");
		integratedHeader = integratedHeader.replaceAll("g/L", "g");
		
		//Only integrated
		String integratedOnly = integratedHeader;
		for(int i=0; i<integratedValues.size(); i++) {
			for(int j=0; j<integratedValues.get(0).size(); j++) {
				integratedOnly += integratedValues.get(i).get(j) + "\t";
			}
			integratedOnly += "\n";
		}			
		String newFileName = fileDir.toString().substring(0, extensionPos) + "_integrated" + fileDir.toString().substring(extensionPos);
		File newFile = new File(newFileName);
		FileWriter myWriter = new FileWriter(newFile);
		myWriter.write(integratedOnly);
		myWriter.close();
		
		//Integrated and summed
		String integratedSum = integratedHeader;
		for(int i=0; i<integratedValuesSum.size(); i++) {
			for(int j=0; j<integratedValuesSum.get(0).size(); j++) {
				integratedSum += integratedValuesSum.get(i).get(j) + "\t";
			}
			integratedSum += "\n";
		}	
		newFileName = fileDir.toString().substring(0, extensionPos) + "_integratedSum" + fileDir.toString().substring(extensionPos);
		newFile = new File(newFileName);
		myWriter = new FileWriter(newFile);
		myWriter.write(integratedSum);
		myWriter.close();
		
		//Integrated and summed - only full timesteps
		String integratedSumFull = integratedHeader;
		for(int i=0; i<integratedValuesSumFull.size(); i++) {
			for(int j=0; j<integratedValuesSumFull.get(0).size(); j++) {
				integratedSumFull += integratedValuesSumFull.get(i).get(j) + "\t";
			}
			integratedSumFull += "\n";
		}	
		newFileName = fileDir.toString().substring(0, extensionPos) + "_integratedSum_fullTimesteps" + fileDir.toString().substring(extensionPos);
		newFile = new File(newFileName);
		myWriter = new FileWriter(newFile);
		myWriter.write(integratedSumFull);
		myWriter.close();
		
		//Recomputed [g/L]
		String integratedMerged = header;
		for(int i=0; i<integratedValuesMerged.size(); i++) {
			for(int j=0; j<integratedValuesMerged.get(0).size(); j++) {
				integratedMerged += integratedValuesMerged.get(i).get(j) + "\t";
			}
			integratedMerged += "\n";
		}	
		newFileName = fileDir.toString().substring(0, extensionPos) + "_integratedSum_Rates" + fileDir.toString().substring(extensionPos);
		newFile = new File(newFileName);
		myWriter = new FileWriter(newFile);
		myWriter.write(integratedMerged);
		myWriter.close();
		
		return integratedMerged;
	}
	
	/**
	 * Copy outputFiles.lua to the hydrolysis storage
	 * 
	 * @param workingDir Working directory
	 * @param hydrolysisName
	 * @throws IOException
	 */
	public static void copy_outputFiles(File workingDir, String hydrolysisName) throws IOException {
		File storageDir = new File(workingDir, "storage_hydrolysis");
		File hydrolysisDir = new File(workingDir, hydrolysisName);
		
		File source_outputFiles = new File(hydrolysisDir, "outputFiles.lua");
		File destination_outputFiles = new File(storageDir, "outputFiles.lua");
		Files.copy(source_outputFiles.toPath(), destination_outputFiles.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Updates the names of the integrated files in the outputFiles from the hydrolysis_storage
	 * 
	 * @param storage_dir Path to the "storage_hydrolysis"
	 * @throws IOException
	 */
	public static void update_outputFiles_integration(File storage_dir) throws IOException {
		File outputFiles = new File(storage_dir, "outputFiles.lua");
		
		Scanner lineIter = new Scanner(outputFiles);
		String fileString = "";
		while (lineIter.hasNextLine()) {
			fileString += lineIter.nextLine() + "\n";
		}
		lineIter.close();
		
		fileString = fileString.replace("outflow.txt", "outflow_integrated.txt");
		fileString = fileString.replace("outflow=", "outflow_integrated=");
		
		//Write file
		FileWriter myWriter = new FileWriter(outputFiles);
		myWriter.write(fileString);
		myWriter.close();
	}
	
	/**
	 * Removes unwanted files from the outputFiles.lua
	 * We use this for example to remove "reactorState.txt" out
	 * of the hydrolysis storage, since the values from this textfile
	 * cannot be added together (e.g. PH values). They are specific for 
	 * the hydrolysis reactor and should only be regarded in the 
	 * according context.
	 * 
	 * @param storage_dir Path to the "storage_hydrolysis"
	 * @throws IOException
	 */
	public static void update_outputFiles(File storage_dir) throws IOException
	{
		File outputFiles_path = new File(storage_dir, "outputFiles.lua");
		String newOutputFiles = "";
		boolean takeLine = true;
		
		String lastLine = "";
		System.out.println("\t Updating: " + outputFiles_path);
		if(outputFiles_path.exists())
		{
			Scanner myReader = new Scanner(outputFiles_path);
			while(myReader.hasNextLine()) {
				String line = myReader.nextLine();
				
				if(line.contains("filename") && !takeLine)
					newOutputFiles += lastLine + "\n";
					
				if(line.contains("filename"))
					takeLine = true;	
				
				for(String f: output_files_nonAdditive) {
					if (line.contains(f)) {
						takeLine = false;
						int line_begin = newOutputFiles.indexOf(lastLine);
						
						StringBuffer newOutputBuffer = new StringBuffer(newOutputFiles);
						newOutputBuffer.replace(line_begin, line_begin+lastLine.length()+1, "");
						newOutputFiles = newOutputBuffer.toString();
					}
				}	
				
				if(takeLine)
				{
					newOutputFiles += line + "\n";
				}				
				lastLine = line;
			}
			myReader.close();
		}
		
		//If the last file entry was deleted we need to close a paranthesis		
		if(!takeLine){ 
			newOutputFiles = newOutputFiles.substring(0, newOutputFiles.length()-2);
			newOutputFiles += "\n}";
		}
				
		FileWriter myWriter = new FileWriter(outputFiles_path);
		myWriter.write(newOutputFiles);
		myWriter.close();
	}	
}
