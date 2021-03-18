package vrl.biogas.biogascontrol.elements;

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

import vrl.biogas.biogascontrol.BiogasControlPlugin;
import vrl.biogas.biogascontrol.SettingsPanel;
import vrl.biogas.biogascontrol.SetupPanel;

public class ElementFunctions {
	
	private final static String[] output_files = {
		"digestateConcentrations.txt",
		"subMO_mass.txt",
		"valveGasFlow.txt",
		"producedNormVolumeCumulative.txt",
		"dbg_nitrogenRates.txt",
		"producedNormVolumeHourly.txt",
		"dbg_phContribution.txt"};
	
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
		"reactorState.txt"};
	
	public static void merge(SimulationElement elem) throws IOException {
		String name = elem.name();
		File basePath = elem.path();
		int startTime = (Integer) SettingsPanel.simStarttime.getValue();
		int curTime = BiogasControlPlugin.currenttime;
		boolean preexisting = SetupPanel.mergePreexisting;
		File timePath = new File(elem.path(), String.valueOf(curTime));
		
		System.out.println("********************************************************************************");
		System.out.println("Merge " + name);
		System.out.println("\t---> " + timePath);
		System.out.println("\t---> " + basePath);
		System.out.println("********************************************************************************");
		
		merge_one_reactor(basePath.toString(), startTime, curTime, preexisting);
	}
	
	public static void merge_all_hydrolysis(
			String working_dir,
			String[] reactor_names,
			int simulation_starttime,
			int current_starttime,
			boolean merge_preexisting) throws IOException
		{
		
		//First timestep?
		boolean is_first_timestep = false;
		if(simulation_starttime == current_starttime)
			is_first_timestep = true;
			
		String storage_dir = working_dir 
				+ File.separator
				+ "storage_hydrolyse";
		System.out.println("Storage dir: " + storage_dir);
		
		//Merge hydrolysis files (no integration)
		for(String f: output_files) {
			
			String output_file_string = merge_hydrolysis_files(
				working_dir, 
				f, 
				current_starttime,
				reactor_names);
			
			//std::ofstream output_file;
			String output_file_name = storage_dir + File.separator + f;
			System.out.println(output_file_name);
			
			Writer output;		
			if(!output_file_string.isEmpty())
			{
				
				if(is_first_timestep){
					if(merge_preexisting){
						output = new BufferedWriter(new FileWriter(output_file_name, true));
						output.append(output_file_string);
						output.close();
					}
					else
					{
						output = new BufferedWriter(new FileWriter(output_file_name));
						output.append(HelperFunctions.header_string);
						output.append(output_file_string);
						output.close();
					}
				}
				else{
					output = new BufferedWriter(new FileWriter(output_file_name, true));
					output.append(output_file_string);
					output.close();
				}
				
			}			
			System.out.println(output_file_string);
			
		}	
		
		/*
		std::cout << "Integrating files ...." << std::endl;
		
		//Merge hydrolysis files with integration
		for(const auto& f: output_files_integration) {
			std::string output_names = "";
			for(int i=0; i<num_reactors; i++){
				output_names += (string) working_dir + "/" 
					+ reactors.at(i) + "/" 
					+ f;
				if(i!=num_reactors-1)
					output_names += "\n";
			}
			std::cout << "output_names: " << output_names << std::endl;
			bool exists = merge_hydrolysis_files_integration(
				current_starttime, 
				num_reactors,
				output_names);
			if(exists)
			{
				std::string output_file_string = get_merged_file();
				
				std::ofstream output_file;
				std::string output_file_name = storage_dir + "/" + f;
				std::cout << "output_file_name: " << output_file_name << std::endl;
				std::cout << "output_file_string: " << output_file_string << std::endl;
				if(is_first_timestep){
					
					if(merge_preexisting)
					{
						std::ifstream output_file_stream(output_file_name);
						if(output_file_stream.good()) //merge with previous files
						{
							output_file.open(output_file_name, std::ios_base::app);
							remove_header_from_string(output_file_string.c_str());
							output_file << data_string;
						}
						else //should merge but no previous files found
						{
							output_file.open(output_file_name);
							output_file << output_file_string;
						}
					}
					else //dont merge
					{
						output_file.open(output_file_name);
						output_file << output_file_string;
					}				
				}
				else{
					output_file.open(output_file_name, std::ios_base::app);
					remove_header_from_string(output_file_string.c_str());
					output_file << data_string;
				}
				
				output_file.close();
				
			}
		}
		*/
	}
	
	private static String merge_hydrolysis_files(
			String dir, 
			String filename, 
			int current_starttime, 
			String[] reactors) throws FileNotFoundException
	{	
		int num_reactors = reactors.length;
		String working_dir = dir;
		String output_file_string = "";
		boolean fileExists = false;
		HelperFunctions.values = new ArrayList<ArrayList<ArrayList<String>>>();
		
		
		//Write data into "values" vector
		for(String d: reactors) {
			String file_direction = working_dir + File.separator
				+ d + File.separator 
				+ String.valueOf(current_starttime) + File.separator 
				+  filename;
			fileExists = HelperFunctions.read_values_from_reactor(file_direction);
		}	
		
		
		if(fileExists)
		{
			String dir_for_header = working_dir + File.separator  + reactors[0]
				+ "/" + String.valueOf(current_starttime) + File.separator  + filename;
			
			HelperFunctions.get_header(new File(dir_for_header)); 
			
			int num_entries_line = HelperFunctions.values.get(0).get(0).size(); //Entries per line
			
			
			//line_counter to keep track of the timesteps
			ArrayList<Integer> line_counter = new ArrayList<Integer>();
			for(int i=0; i<num_reactors; i++)
				line_counter.add(0);
			
			double current_time = current_starttime;
			double endtime = current_starttime + 1;
			//std::stringstream output_stream;
			String output_stream = "";
			
			while(current_time < endtime){
				//Get current max
				double max_time = 0;
				for(int j=0; j<num_reactors; j++){
					int c = line_counter.get(j);
					double time = Double.parseDouble(HelperFunctions.values.get(j).get(c).get(0));
					if(time>max_time)
						max_time = time;
				}
					
				//Increase line counter
				for(int j=0; j<num_reactors; j++){

					while(Double.parseDouble(HelperFunctions.values.get(j).get(line_counter.get(j)).get(0)) <= max_time){
						if(Double.parseDouble(HelperFunctions.values.get(j).get(line_counter.get(j)).get(0)) == endtime){
							line_counter.set(j, line_counter.get(j)+1);
							break;
						}							
						line_counter.set(j, line_counter.get(j)+1);;
					}
				}
				
				//Combine current timestep
				output_stream += max_time + "\t";
					
				for(int i=1; i<num_entries_line; i++){
					double sum = 0;
					for(int j=0; j<num_reactors; j++){
						sum += Double.parseDouble(HelperFunctions.values.get(j).get(line_counter.get(j)-1).get(i));
					}
					output_stream += sum + "\t";
				}
				output_stream += "\n";
				current_time = max_time;
				
			}
			
			output_file_string += output_stream;
			
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
	
	public static String integrate_file(File f, int simStarttime) throws FileNotFoundException {
		//System.out.println(f.toString());
		HelperFunctions.values = new ArrayList<ArrayList<ArrayList<String>>>();
		HelperFunctions.read_values_from_reactor(f.toString());
		ArrayList<ArrayList<String>> values = HelperFunctions.values.get(0);
		
		if(!(simStarttime <= Double.valueOf(values.get(0).get(0)))) {
			return "ERROR";
		}
		
		ArrayList<ArrayList<String>> integratedValues = new ArrayList<ArrayList<String>>();
		
		int timesteps = values.size();
		int numValues = values.get(0).size();
		
		for(int i=0; i<timesteps; i++) { //time iteration
			double time = Double.valueOf(values.get(i).get(0));
			//System.out.println("time: " + time);
			
			ArrayList<String> line = new ArrayList<String>();
			line.add(values.get(i).get(0));
			
			for(int j=1; j<numValues; j++) { //parameter iteration
				
				if(i == 0) { //First timestep
					double h = time-simStarttime;
					double param = Double.valueOf(values.get(i).get(j)); 
					double val = (Math.abs(param)/2)*h;
					line.add(String.valueOf(val));
					//System.out.println("\th: " + h);
					//System.out.println("\tparam: " + param);
					//System.out.println("\tval: " + val);
				}
				else {
					double prevTime = Double.valueOf(values.get(i-1).get(0));
					double h = time-prevTime;
					
					double param = Double.valueOf(values.get(i).get(j)); 
					double prevParam = Double.valueOf(values.get(i-1).get(j)); 
					
					double val = ((Math.abs(param-prevParam)/2)*h)+(Math.min(param, prevParam)*h);
					line.add(String.valueOf(val));
					//System.out.println("\th: " + h);
					//System.out.println("\tparam: " + param);
					//System.out.println("\tprevParam: " + prevParam);
					//System.out.println("\tval: " + val);
				}	
			}
			integratedValues.add(line);
			//System.out.println("\n");

		}
		
		String valuesString = "";
		for(int i=0; i<integratedValues.size(); i++) {
			for(int j=0; j<integratedValues.get(0).size(); j++) {
				valuesString += integratedValues.get(i).get(j) + "\t";
			}
			valuesString += "\n";
		}
		System.out.println(valuesString);
		return valuesString;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{ 
		/*
		File f = new File("/home/paul/Schreibtisch/smalltestmethane/biogasVRL_20210317_135533/hydrolyse_0/0/outflow.txt");
		values = new ArrayList<ArrayList<ArrayList<String>>>();
		read_values_from_reactor(f.toString());
		ArrayList<ArrayList<String>> firstEntry = values.get(0);
		for(int i=0; i<firstEntry.size(); i++) {
			for(int j=0; j<firstEntry.get(0).size(); j++) {
				System.out.print(firstEntry.get(i).get(j) + "\t");
			}
			System.out.print("\n");
		}
		*/
		
		/*
		String dir = "/home/paul/Schreibtisch/smalltestmethane/biogasVRL_20210317_135533";
		String[] reactors = {"hydrolyse_0", "hydrolyse_1"};
	
		
		merge_all_hydrolysis(dir, reactors, 0, 0, false);
		*/
		
		//String working_dir = "/home/paul/Schreibtisch/smalltestmethane/biogasVRL_20210317_135533/hydrolyse_0";
		
		//merge_one_reactor(working_dir, 0, 1, false);
		
		//System.out.println(fileOut);
		File f = new File("/home/paul/Schreibtisch/smalltestmethane/biogasVRL_20210318_140134/hydrolyse_0/outflow.txt");
		String a = integrate_file(f, 0);
	}
}
