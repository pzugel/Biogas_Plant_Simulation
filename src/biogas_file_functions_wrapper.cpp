/*
 * Copyright (c) 2020:  G-CSC, Goethe University Frankfurt
 * Author: Paul Zügel
 * 
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */

#include <string>
#include <vector>
#include <fstream>
#include <regex>
#include <boost/algorithm/string.hpp>
#include <iostream>
#include <iomanip>
#include <algorithm>
#include <filesystem>

#include "file_functions/functions.h"
#include "file_functions/merge_hydrolysis.cpp"
#include "file_functions/merge_hydrolysis_integration.cpp"
#include "file_functions/update_feeding.cpp"
#include "file_functions/update_inflow.cpp"

static const std::vector<std::string> output_files{
	"digestateConcentrations.txt",
	"subMO_mass.txt",
	"valveGasFlow.txt",
	"producedNormVolumeCumulative.txt",
	"dbg_nitrogenRates.txt",
	"producedNormVolumeHourly.txt",
	"dbg_phContribution.txt",
	"outflow_integratedSum_fullTimesteps.txt"};

static const std::vector<std::string> all_files{
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
	"reactorState.txt",
	"dbg_gamma.txt"};
	
static const std::vector<std::string> output_files_integration{
	"dbg_reactionrates.txt",
	"outflow.txt"};
	
static const std::vector<std::string> output_files_nonAdditive = {
	"dbg_avgEqValues.txt",
	"gas_Volfraction.txt",
	"reactorState.txt",
	"dbg_gamma.txt"};

extern "C"{

/**
 * Removes unwanted files from the outputFiles.lua
 * We use this for example to remove "reactorState.txt" out
 * of the hydrolysis storage, since the values from this textfile
 * cannot be added together (e.g. PH values). They are specific for 
 * the hydrolysis reactor and should only be regarded in the 
 * according context.
 * 
 * @param outputFiles_path: Path pointing to outputFiles.lua
 */
void update_outputFiles(const char* outputFiles_path)
{
	std::string newOutputFiles = "";
	bool takeLine = true;
	
	std::string lastLine = "";
	std::cout << "Updating: " << outputFiles_path << std::endl;
	std::fstream stream(outputFiles_path);
	if(stream.good())
	{
		for(std::string line; getline(stream,line);)
		{
			if(line.find("filename") != std::string::npos && !takeLine)
				newOutputFiles += lastLine + "\n";
				
			if(line.find("filename") != std::string::npos)
				takeLine = true;	
			
			for(const auto& f: output_files_nonAdditive) {
				if (line.find(f) != std::string::npos) {
					takeLine = false;
					size_t line_begin = newOutputFiles.find(lastLine);
					newOutputFiles.erase(line_begin-1, lastLine.size()+1);
				}
			}	
			
			if(takeLine)
				newOutputFiles += line + "\n";
			
			lastLine = line;
		}
	}
	
	//If the last file entry was deleted we need to close a paranthesis
	if(!takeLine){ 
		newOutputFiles = newOutputFiles.substr(0, newOutputFiles.size()-2);
		newOutputFiles += "\n}";
	}
	
	std::ofstream output_file;
	output_file.open(outputFiles_path);
	output_file << newOutputFiles;
	output_file.close();
}

/**
 * Function called by the storage element
 * 
 * First iterate over all possible output files that need integration
 * as specified in the "output_files_integration" vector and call the 
 * "merge_files_integration" function.
 * 
 * Then iterate over all other possible hydrolyse output files as 
 * specified in the "output_files" vector and call the 
 * "merge_hydrolysis_files" function to merge all output files for
 * all hydrolyse reactors.
 * 
 * @param working_dir: Path to working directory
 * @param reactor_names: String of names for the hydrolyse reactors
 * @param current_starttime: Current timestep in the simulation
 */
void merge_all_hydrolysis(
	const char* working_dir,
	const char* reactor_names)
{
	//Write reactor names into vector
	std::vector<std::string> reactors;
	std::istringstream dir_stream(reactor_names);
	for(std::string line; std::getline(dir_stream,line);)
	{
		reactors.push_back(line);
	}
	int num_reactors = reactors.size();
	std::cout << "num_reactors: " << num_reactors << std::endl;

	std::string storage_dir = (std::string) working_dir + "/storage_hydrolyse";
	std::cout << "storage_dir: " << storage_dir << "\n" << std::endl;
	
	//Merge hydrolysis files with integration
	for(std::string f: output_files_integration) {
		std::string output_file_string = merge_files_integration(
			working_dir, 
			f,
			reactors);
		
		std::string output_file_name = 	storage_dir + "/" + f;
		std::cout << output_file_name << std::endl;
		
		std::ofstream output;
		output.open(output_file_name);
		output << output_file_string;
		output.close();
		
		std::cout << output_file_string << std::endl;
	}
	
	//Merge hydrolysis files (no integration)
	for(std::string f: output_files) {
		std::string output_file_string = merge_hydrolysis_files(
			working_dir, 
			f,
			reactors);
		
		std::string output_file_name = 	storage_dir + "/" + f;
		std::cout << output_file_name << std::endl;
		
		std::ofstream output;
		output.open(output_file_name);
		output << output_file_string;
		output.close();
		
		std::cout << output_file_string << std::endl;
	}
}

/**
 * Function called by the feedback element
 * 
 * First iterate over all possible output files that need integration
 * as specified in the "output_files_integration" vector and call the 
 * "merge_files_integration" function.
 * 
 * Then iterate over all other possible methane output files as 
 * specified in the "output_files" vector and call the 
 * "merge_hydrolysis_files" function to merge all output files for
 * all hydrolyse reactors.
 * 
 * @param methane_dir: Path pointing to the methane folders
 * @param working_dir: Path to the working directory
 * @param current_starttime: Current timestep in the simulation
 */
void merge_all_methane(
	const char* working_dir,
	const char* methane_dir)
{
	std::cout << "methane_dir: " << methane_dir << std::endl;		
		
	//Merge methane files with integration
	for(std::string f: output_files_integration) {
		std::vector<std::string> methaneReactor;
		methaneReactor.push_back("methane");
		std::string output_file_string = merge_files_integration(
				working_dir, 
				f,  
				methaneReactor);
				
		std::string output_file_name = 	(std::string) methane_dir + "/" + f;
		std::cout << output_file_name << std::endl;
		
		std::ofstream output;
		output.open(output_file_name);
		output << output_file_string;
		output.close();
		
		std::cout << output_file_string << std::endl;
	}
}

/**
 * Only concatenates files from one single reactor. 
 * No summation,integration or modification of any sort.
 * 
 * Function is called by the hydrolysis- and methane reactor
 * after completion of computations 
 * 
 * @param working_dir: Path pointing to a reactor folder
 * @param simulation_starttime: Starttime of the whole simulation
 * @param current_starttime: Current timestep in the simulation
 */
void merge_one_reactor(
	const char* working_dir,
	int simulation_starttime,
	int current_starttime,
	bool merge_preexisting)
{	
	std::string timestep_dir = (std::string) working_dir + "/" + std::to_string(current_starttime);
	
	bool is_first_timestep = false;
	if(simulation_starttime == current_starttime)
	{
		is_first_timestep = true;
		//Copy outputFiles.lua
		std::ifstream output_files_lua;
		output_files_lua.open(timestep_dir + "/outputFiles.lua");
		std::ofstream copy_file;
		copy_file.open((std::string) working_dir + "/outputFiles.lua");
		copy_file << output_files_lua.rdbuf();
		copy_file.close();
	}
	
	for(const auto& f: all_files) {
		std::ofstream output_file;
		std::string input_file_name = (std::string) timestep_dir + "/" + f;
		std::string output_file_name = (std::string) working_dir + "/" + f;
		
		std::ifstream input_file_stream(input_file_name);
		if(input_file_stream.good())
		{	
			if(is_first_timestep){
				if(merge_preexisting)
				{
					std::ifstream output_file_stream(output_file_name);
					if(output_file_stream.good()) //merge with previous files
					{
						output_file.open(output_file_name, std::ios_base::app);
						remove_header(input_file_name.c_str());
						output_file << data_string;
					}
					else //should merge but no previous files found
					{
						output_file.open(output_file_name);
						output_file << input_file_stream.rdbuf();
						output_file << "\n";
					}
				}
				else //dont merge
				{
					output_file.open(output_file_name);
					output_file << input_file_stream.rdbuf();
					output_file << "\n";
				}
			}
			else{
				output_file.open(output_file_name, std::ios_base::app);
				remove_header(input_file_name.c_str());
				output_file << data_string;
			}
		}
		output_file.close();
	}
}

/**
 * Updates the specification file for the methane reacor with the current
 * outflow from the hydrolysis reactors (placed in storage)
 * 
 * @param outflow_infile: Path pointing the outflow.txt
 * @param methane_specfile: Path pointing the methane spec file 
 */
void update_methane_inflow(
	const char* outflow_infile,
	const char* methane_specfile)
{
	write_methane_inflow(outflow_infile, methane_specfile);
}

/**
 * Updates the specification file for the hydrolysis reacors with the current
 * outflow from the methane reactor
 * 
 * @param outflow_infile: Path pointing the outflow.txt (methane)
 * @param hydrolysis_specfiles: String with the specfiles directories
 * @param fractions: Array with fractional values to split the inflow
 */
void update_hydrolysis_inflow(
	const char* outflow_infile,
	const char* hydrolysis_specfiles,
	double fractions[])
{
	write_hydrolysis_inflow(outflow_infile, 
		hydrolysis_specfiles, 
		fractions);
}

/**
 * Updates the feeding timetable in a specification file 
 * for a hydrolysis reacote
 * 
 * @param hydrolysis_specfile: Path pointing the the specification
 * @param time: Timestamp time
 * @param amount: Timestamp amount
 * @param number_timestamps: size of arrays time and amount
 */
void update_hydrolysis_feeding(
	const char* hydrolysis_specfile,
	const char* time,
	const char* amount,
	int number_timestamps)
{
	update_feeding_timetable(hydrolysis_specfile, time, amount, number_timestamps);
} 

/**
 * Returns feeding timetable formatted into a CSV styls string
 * to be loaded into LabView
 * 
 * @param hydrolysis_specfile: Path pointing the the specification
 */
const char* load_hydrolysis_feeding(const char* hydrolysis_specfile)
{
	get_feeding_timetable(hydrolysis_specfile);
	return feeding_timetable_csv.c_str();
}

/**
 * Loads a CSV-style *.txt file and removes all header lines 
 * 
 * @param path: Path pointing to a *.txt file
 * @return The plain text without header lines
 */
const char* remove_header(const char* path)
{
	data_string = "";
	std::regex header ("(^#)|(^[a-zA-Z])");
	std::ifstream CSVInput(path);
	for(std::string line; getline(CSVInput,line);)
	{
		//Header lines might begin whith whitespaces --> remove
		std::string line_noWS = line;
		line_noWS.erase(remove_if(line_noWS.begin(), line_noWS.end(), isspace), line_noWS.end());
		if(!(std::regex_search(line_noWS, header)))
			data_string += line + "\n";
	}
	return data_string.c_str();
}

/**
 * Loads a CSV-style *.txt file and extracts all header lines 
 * 
 * @param path: Path pointing to a *.txt file
 * @return The plain text header
 */
const char* get_header(const char* path)
{
	header_string = "";
	std::regex header ("(^#)|(^[a-zA-Z])");
	std::ifstream CSVInput(path);
	for(std::string line; getline(CSVInput,line);)
	{
		if(std::regex_search(line, header))
			header_string += line + "\n";
	}
	return header_string.c_str();
}

/**
 * Reads out pH value from "reactorState.txt" files. We only use this
 * for hydrolysis reactors to display in LabView.
 * 
 * @param reactor_state_files: String with the reactorState.txt directories
 * @return ph_string: String with PH values
 */
const char* get_hydrolysis_PH(const char* reactor_state_files)
{
	ph_string = "";
	int reactor_num = 0;
	std::istringstream dir_stream(reactor_state_files);
	values = {};
	for(std::string reactor_state; std::getline(dir_stream,reactor_state);)
	{
		std::ifstream f(reactor_state.c_str());
		if(f.good() && !(f.peek() == EOF))
		{
			read_values_from_reactor(reactor_state);
			int last_row = values.at(0).size();
			get_header(reactor_state.c_str());
			read_outflow_header();
			for(int col=0; col<outflow_input_header.size(); col++)
			{
				std::string header_val = outflow_input_header.at(col);
				if(header_val.find("pH") != std::string::npos)
					ph_string += values.at(reactor_num).at(last_row-1).at(col) + "\n";
			}
			reactor_num += 1;
		}
	}
	std::replace(ph_string.begin(), ph_string.end(), '.', ',');
	return ph_string.c_str();
}

} //end extern "C"

/**
 * Only to test functionality
 */
int main(){
	//const char* storage_dir = "/home/paul/Schreibtisch/smalltest/testFolderLabView/storage_hydrolysis";
	const char* working_dir = "/home/paul/Schreibtisch/smalltest/testFolderLabView";
	const char* reactor_names = "hydrolyse_0";
		
	merge_all_hydrolysis(working_dir, reactor_names);
	
	return 0;
}
