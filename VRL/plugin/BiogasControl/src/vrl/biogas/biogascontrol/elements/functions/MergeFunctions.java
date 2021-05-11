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

public class MergeFunctions {
	
	private final static String[] output_files = {
		"digestateConcentrations.txt",
		"subMO_mass.txt",
		"valveGasFlow.txt",
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
		"dbg_reactionrates.txt",
		"outflow.txt",
		"reactorState.txt"};
	
	private final static String[] output_files_integration ={
		"dbg_reactionrates.txt",
		"outflow.txt"};
	
	private final static String[] output_files_nonAdditive = {
		"dbg_avgEqValues.txt",
		"gas_Volfraction.txt",
		"reactorState.txt",
		"dbg_gamma.txt"};
	
	public static void merge(String name, int currenttime, File timePath) throws IOException {
		File basePath = timePath.getParentFile();
		int startTime = (Integer) BiogasControlClass.settingsPanelObj.simStarttime.getValue();
		boolean preexisting = BiogasControl.setupPanelObj.mergePreexisting;		
		
		System.out.println("********************************************************************************");
		System.out.println("Merge " + name);
		System.out.println("Preexisting? " + preexisting);
		System.out.println("\t---> " + timePath);
		System.out.println("\t---> " + basePath);
		System.out.println("********************************************************************************");
		
		merge_one_reactor(basePath.toString(), startTime, currenttime, preexisting);
	}
	
	public static void merge_all_hydrolysis(
			File storage_dir,
			File working_dir,
			String[] reactor_names) throws IOException 
	{			
		
		System.out.println("Storage dir: " + storage_dir.toString());			
		
		//Merge hydrolysis files with integration
		for(String f: output_files_integration) {
			
			String output_file_string = merge_files_integration(
					working_dir, 
					f, 
					reactor_names);
			
			String output_file_name = storage_dir + File.separator + f;
			int extensionPos = output_file_name.lastIndexOf(".");
			output_file_name = output_file_name.substring(0, extensionPos) + "_integrated" + output_file_name.substring(extensionPos);	
			System.out.println(output_file_name);
			
			Writer output = new BufferedWriter(new FileWriter(output_file_name));
			output.append(output_file_string);
			output.close();	
					
			System.out.println(output_file_string);	
		}
		
		//Merge hydrolysis files (no integration)
		for(String f: output_files) {
			
			String output_file_string = merge_hydrolysis_files(
				working_dir, 
				f, 
				reactor_names);
			
			File output_file_name = new File(storage_dir, f);
			System.out.println(output_file_name);
			
			Writer output = new BufferedWriter(new FileWriter(output_file_name));
			output.append(output_file_string);
			output.close();		
			
			System.out.println(output_file_string);		
		}
	}
	
	@SuppressWarnings("unused")
	public static void merge_all_methane(
			File methane_dir,
			File working_dir) throws IOException 
	{			
		
		System.out.println("methane_dir: " + methane_dir.toString());			
		
		//Merge methane files with integration
		for(String f: output_files_integration) {
			String methaneReactor[] = {"methane"};
			String output_file_string = merge_files_integration(
					working_dir, 
					f,  
					methaneReactor);
			/*
			File output_file_name = new File(methane_dir, f);
			System.out.println(output_file_name);
			
			Writer output = new BufferedWriter(new FileWriter(output_file_name));
			output.append(output_file_string);
			output.close();	
					
			System.out.println(output_file_string);	
			*/
		}
	}
	
	private static String merge_files_integration(
			File dir, 
			String filename,
			String[] reactors) throws IOException
	{
		for(String r : reactors) {
			integrate_one_file(new File(dir, r),filename);		
		}
		
		int extensionPos = filename.lastIndexOf(".");
		String newFileName = filename.substring(0, extensionPos) + "_integrated" + filename.substring(extensionPos);	
		String output_file_string = merge_hydrolysis_files(dir, newFileName, reactors);
				
		return output_file_string;
	}
	
	private static String merge_hydrolysis_files(
			File dir, 
			String filename, 
			String[] reactors) throws FileNotFoundException
	{	
		int num_reactors = reactors.length;
		String working_dir = dir.toString();
		String output_file_string = "";
		HelperFunctions.values = new ArrayList<ArrayList<ArrayList<String>>>();
		
		//Add header to new file
		String dir_for_header = working_dir + File.separator  + reactors[0] + File.separator  + filename;
		output_file_string += HelperFunctions.get_header(new File(dir_for_header)); 
		
		
		//Write data into "values" vector - Check if file exists for every reactor
		boolean allExist = true;
		for(String d: reactors) {
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
			for(ArrayList<Double> line : outputValues) {
				for(Double d : line) {
					output_file_string += String.valueOf(d) + "\t";
				}
				output_file_string += "\n";
			}			
		}
		else
			System.out.println("Nothing to add up!");
				
		return output_file_string;
	}
	
	public static void merge_one_reactor(
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
	
	private static String integrate_one_file(
			File reactorDir,
			String f) throws IOException 
	{		
		System.out.println("integrate_one_file");
		System.out.println("f --> " + f);
		
		HelperFunctions.values = new ArrayList<ArrayList<ArrayList<String>>>();
		File fileDir = new File(reactorDir, f);
		System.out.println("fileDir: " + fileDir);
		HelperFunctions.read_values_from_reactor(fileDir.toString());
		
		ArrayList<ArrayList<String>> values = HelperFunctions.values.get(0);				
		ArrayList<ArrayList<String>> integratedValues = new ArrayList<ArrayList<String>>();
		
		int timesteps = values.size();
		int numValues = values.get(0).size();
		System.out.println("timesteps: " + timesteps);
		for(int i=0; i<timesteps; i++) { //time iteration
			System.out.println("time: " + i);
			double x2 = Double.valueOf(values.get(i).get(0)); //current time
						
			ArrayList<String> line = new ArrayList<String>();
			line.add(values.get(i).get(0));
			
			for(int j=1; j<numValues; j++) { //parameter iteration
				
				//Integrate via trapezoid
				double x1;
				double f_x1;
				
				if(i == 0) { //First timestep
					x1 = Math.floor(Double.valueOf(values.get(0).get(0)));
					f_x1 = 0.0;
				}
				else {
					x1 = Double.valueOf(values.get(i-1).get(0)); //previous timestep
					f_x1 = Double.valueOf(values.get(i-1).get(j)); 
				}
				
				double h = x2-x1; //trapezoid width
				double f_x2 = Double.valueOf(values.get(i).get(j)); 
				
				System.out.println("x1: " + x1);
				System.out.println("x2: " + x2);
				System.out.println("f_x1: " + f_x1);
				System.out.println("f_x2: " + f_x2);
				/*
				 * Trapezoid
				 * 
				 * With stepsize h = (x2-x1)
				 * T(f) = h * [f(x1)+f(x2)]/2
				 */
				double T_f = h*(f_x1+f_x2)*0.5;
				line.add(String.valueOf(T_f));
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
		for(ArrayList<String> line : integratedValues) {	
			sumLines.set(0, line.get(0));
			for(int i=1; i<line.size(); i++) {			
				double sum = Double.valueOf(sumLines.get(i)) + Double.valueOf(line.get(i));
				sumLines.set(i, String.valueOf(sum));
			}
			integratedValuesSum.add(new ArrayList<String>(sumLines));
		}
		
		/*
		 * Now take only the full steps
		 */
		ArrayList<ArrayList<String>> integratedValuesSumFull = new ArrayList<ArrayList<String>>();
		for(ArrayList<String> line : integratedValuesSum) {
			double time = Double.valueOf(line.get(0));
			if ((time == Math.floor(time)) && !Double.isInfinite(time)) { //Is full timestep?
				integratedValuesSumFull.add(line);
			}
		}
		
		/*
		 * Write out different files
		 */
		int extensionPos = fileDir.toString().lastIndexOf(".");
		
		//Only integrated
		String integratedOnly = HelperFunctions.get_header(fileDir);
		for(int i=0; i<integratedValues.size(); i++) {
			for(int j=0; j<integratedValues.get(0).size(); j++) {
				integratedOnly += integratedValues.get(i).get(j) + "\t";
			}
			integratedOnly += "\n";
		}			
		String newFileName = fileDir.toString().substring(0, extensionPos) + "_integrated" + fileDir.toString().substring(extensionPos);
		System.out.println("newFileName: " + newFileName);
		File newFile = new File(newFileName);
		FileWriter myWriter = new FileWriter(newFile);
		myWriter.write(integratedOnly);
		myWriter.close();
		
		//Integrated and summed
		String integratedSum = HelperFunctions.get_header(fileDir);
		for(int i=0; i<integratedValuesSum.size(); i++) {
			for(int j=0; j<integratedValuesSum.get(0).size(); j++) {
				integratedSum += integratedValuesSum.get(i).get(j) + "\t";
			}
			integratedSum += "\n";
		}	
		newFileName = fileDir.toString().substring(0, extensionPos) + "_integratedSum" + fileDir.toString().substring(extensionPos);
		System.out.println("newFileName: " + newFileName);
		newFile = new File(newFileName);
		myWriter = new FileWriter(newFile);
		myWriter.write(integratedSum);
		myWriter.close();
		
		//Integrated and summed - only full timesteps
		String integratedSumFull = HelperFunctions.get_header(fileDir);
		for(int i=0; i<integratedValuesSumFull.size(); i++) {
			for(int j=0; j<integratedValuesSumFull.get(0).size(); j++) {
				integratedSumFull += integratedValuesSumFull.get(i).get(j) + "\t";
			}
			integratedSumFull += "\n";
		}	
		newFileName = fileDir.toString().substring(0, extensionPos) + "_integratedSum_fullTimesteps" + fileDir.toString().substring(extensionPos);
		System.out.println("newFileName: " + newFileName);
		newFile = new File(newFileName);
		myWriter = new FileWriter(newFile);
		myWriter.write(integratedSumFull);
		myWriter.close();
		
		return integratedSumFull;
	}
	
	public static void copy_outputFiles(File workingDir, String hydrolysisName) throws IOException {
		File storageDir = new File(workingDir, "storage_hydrolysis");
		File hydrolysisDir = new File(workingDir, hydrolysisName);
		
		File source_outputFiles = new File(hydrolysisDir, "outputFiles.lua");
		File destination_outputFiles = new File(storageDir, "outputFiles.lua");
		Files.copy(source_outputFiles.toPath(), destination_outputFiles.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}
	
	/**
	 * Updates the names of the integrated files in the outputFiles from the hydrolysis_storage
	 * @param storageDirectory: Path pointing to storage
	 */
	public static void update_outputFiles_integration(File storageDirectory) throws IOException {
		File outputFiles = new File(storageDirectory, "outputFiles.lua");
		
		Scanner lineIter = new Scanner(outputFiles);
		String fileString = "";
		while (lineIter.hasNextLine()) {
			fileString += lineIter.nextLine() + "\n";
		}
		lineIter.close();
		
		fileString = fileString.replace("outflow.txt", "outflow_integrated.txt");
		fileString = fileString.replace("outflow=", "outflow_integrated=");
		
		fileString = fileString.replace("dbg_reactionrates.txt", "dbg_reactionrates_integrated.txt");
		fileString = fileString.replace("reactionRates=", "reactionRates_integrated=");
		
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
	 * @param storageDirectory: Path pointing to storage
	 */
	public static void update_outputFiles(File storageDirectory) throws IOException
	{
		File outputFiles_path = new File(storageDirectory, "outputFiles.lua");
		String newOutputFiles = "";
		boolean takeLine = true;
		
		String lastLine = "";
		System.out.println("Updating: " + outputFiles_path);
		//std::fstream stream(outputFiles_path);
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
	
	public static void main(String args[]) throws IOException, InterruptedException{ 	
		File workingDir = new File("/home/paul/Schreibtisch/smalltestmethane/VRL/biogasVRL_20210510_174427");
		File storageDir = new File(workingDir, "storage_hydrolysis");
		copy_outputFiles(workingDir, "TEST0");		
		update_outputFiles(storageDir);
		update_outputFiles_integration(storageDir);
	}
	
}