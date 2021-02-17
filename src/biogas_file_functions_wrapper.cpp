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
#include "file_functions/methane_update_inflow.cpp"

//static std::string data_string;
//static std::string header_string;

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
	"reactorState.txt"};
	
static const std::vector<std::string> output_files{
	"digestateConcentrations.txt",
	"subMO_mass.txt",
	"valveGasFlow.txt",
	"producedNormVolumeCumulative.txt",
	"dbg_nitrogenRates.txt",
	"producedNormVolumeHourly.txt",
	"dbg_phContribution.txt"};
	
static const std::vector<std::string> output_files_integration{
	"dbg_reactionrates.txt",
	"outflow.txt"};
	
static const std::vector<std::string> output_files_nonAdditive{
	"dbg_avgEqValues.txt",
	"gas_Volfraction.txt",
	"reactorState.txt"};

//dbg_density.txt
//reactorCODcontent.txt

extern "C"{

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
		if(!(std::regex_search(line, header)))
			data_string += line + "\n";
	}
	return data_string.c_str();
}

/**
 *  Removes all header lines from a .txt file as string
 * 
 * @param path: *.txt file as string
 * @return The plain text without header lines
 */
const char* remove_header_from_string(const char* file_as_string)
{
	data_string = "";
	header_string = "";
	std::regex header ("(^#)|(^[a-zA-Z])");
	std::stringstream STRInput(file_as_string);
	for(std::string line; getline(STRInput,line);)
	{
		if(!(std::regex_search(line, header)))
			data_string += line + "\n";
		else
			header_string += line + "\n";
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
 * Reads all filenames from an outputFiles.lua  
 * 
 * @param path: Path pointing to outputFiles.lua
 * @return Filenames as string
 */
const char* read_filenames(const char* path)
{
	data_string = "";
	std::regex filename ("^filename=");
	std::ifstream LUAOutput(path);
	if(LUAOutput.good())
	{
		for(std::string line; getline(LUAOutput,line);)
		{
			line.erase(std::remove_if(line.begin(), line.end(), ::isspace), line.end());
			if(std::regex_search(line, filename))
			{
				boost::replace_all(line, ",", "");
				boost::replace_all(line, "\"", "");
				boost::replace_all(line, "filename=", "");
				data_string += line + "\n";
			}
		}
	}
	return data_string.c_str();
}

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
 * Iterate over all possible hydrolyse output files as specified
 * in the "hydrolyse_files" vector and call the 
 * "merge_hydrolysis_files" function to merge all output files for
 * all hydrolyse reactors.
 * 
 * @param dir: Path pointing to the hydrolyse folders
 * @param reactor_names: String of names for the hydrolyse reactors
 * @param simulation_starttime: Starttime of the whole simulation
 * @param current_starttime: Current timestep in the simulation
 */
void merge_all_hydrolysis(
	const char* working_dir,
	const char* reactor_names,
	int simulation_starttime,
	int current_starttime)
{
	//Write reactor names into vector
	std::vector<std::string> reactors;
	std::istringstream dir_stream(reactor_names);
	for(std::string line; std::getline(dir_stream,line);)
	{
		reactors.push_back(line);
	}
	int num_reactors = reactors.size();
	
	//First timestep?
	bool is_first_timestep = false;
	if(simulation_starttime == current_starttime)
		is_first_timestep = true;
		
	std::string storage_dir = (std::string) working_dir + "/storage_hydrolyse";
	std::cout << "storage_dir: " << storage_dir << "\n" << std::endl;
	
	//Merge hydrolysis files (no integration)
	for(const auto& f: output_files) {
		std::string output_file_string = merge_hydrolysis_files(
			working_dir, 
			f.c_str(), 
			current_starttime,
			reactors);
		std::ofstream output_file;
		std::string output_file_name = storage_dir + "/" + f;
		std::cout << output_file_name << std::endl;
		if(!output_file_string.empty())
		{
			if(is_first_timestep){
				output_file.open(output_file_name);
				output_file << header_string;
				output_file << output_file_string;
			}
			else{
				output_file.open(output_file_name, std::ios_base::app);
				output_file << output_file_string;
			}
		}
		output_file.close();
		std::cout << output_file_string << std::endl;
	}
	
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
			
			if(is_first_timestep){
				output_file.open(output_file_name);
				output_file << output_file_string;
			}
			else{
				output_file.open(output_file_name, std::ios_base::app);
				remove_header_from_string(output_file_string.c_str());
				output_file << data_string;
			}
			
			output_file.close();
			std::cout << output_file_string << std::endl;
		}
	}
}

/**
 * Only concatenates files from one single reactor. 
 * No summation,integration or modification of any sort. 
 * This function will only be used to plot the unmodified
 * outputs of the reactor.
 * 
 * @param working_dir: Path pointing to a reactor folder
 * @param simulation_starttime: Starttime of the whole simulation
 * @param current_starttime: Current timestep in the simulation
 */
void merge_one_reactor(
	const char* working_dir,
	int simulation_starttime,
	int current_starttime)
{	
	std::string timestep_dir = (std::string) working_dir + "/" + to_string(current_starttime);
	
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
				output_file.open(output_file_name);
				output_file << input_file_stream.rdbuf();
				output_file << "\n";
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
 * Reads out pH value from "reactorState.txt" files. We only use this
 * for hydrolysis reactors to display in LabView.
 * 
 * @param ph_arr: Array containing the pH values
 * @param reactor_state_files: String with the reactorState.txt directories
 */
void get_hydrolysis_PH(
	double (&ph_arr)[3],
	const char* reactor_state_files)
{
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
				if(header_val.find("pH") != string::npos)
					ph_arr[reactor_num] = dot_conversion(values.at(reactor_num).at(last_row-1).at(col));
			}
			reactor_num += 1;
		}
		else
			ph_arr[reactor_num] = 0;
	}
}

} //end extern "C"

/**
 * Only to test functionality
 */
int main(){
	/*
	double timestep;
	std::cout << "timestep: ";
	std::cin >> timestep;
	const char* reactors = "hydrolyse_0\nhydrolyse_1\n";
	merge_all_hydrolysis(
		"/home/paul/Schreibtisch/smalltest/tmp",
		reactors,
		0,
		timestep);
	
	*/
	/*
	int time;
	std::string reactor;
	
	std::cout << "Current time:";
	std::cin >> time;

	merge_one_reactor(
		"/home/paul/Schreibtisch/smalltest/tmp/hydrolyse_0",
		0,
		time
	);
	*/
	
	
	//const char* outflow_infile = "/home/paul/Schreibtisch/smalltest/tmp/storage_hydrolyse/outflow.txt";
	//const char* methane_specfile = "/home/paul/Schreibtisch/smalltest/tmp/methane/0/methane_checkpoint.lua";
	//update_methane_inflow(outflow_infile, methane_specfile);
	
	//update_outputFiles("/home/paul/Schreibtisch/smalltest/tmp/storage_hydrolyse/outputFiles.lua");
	
	/*
	const char* reactor_state_files = "/home/paul/Schreibtisch/smalltest/tmp/hydrolyse_0/reactorState.txt\n/home/paul/Schreibtisch/smalltest/tmp/hydrolyse_1/reactorState.txt";
	double a[3]; 
	get_hydrolysis_PH(a, reactor_state_files);
	std::cout << a[0] << std::endl;
	std::cout << a[1] << std::endl;
	*/
	
	return 0;
}
