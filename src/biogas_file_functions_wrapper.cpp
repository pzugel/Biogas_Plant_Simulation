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


#include "storage_functions/storage.h"
#include "storage_functions/merge_hydrolysis.cpp"
#include "storage_functions/merge_hydrolysis_integration.cpp"

static std::string data_string;
static std::string header_string;

static const std::vector<std::string> hydrolyse_files{
	"digestateConcentrations.txt",
	"subMO_mass.txt",
	"valveGasFlow.txt",
	"gas_Volfraction.txt",
	"producedNormVolumeCumulative.txt",
	"dbg_avgEqValues.txt",
	"dbg_nitrogenRates.txt",
	"producedNormVolumeHourly.txt",
	"dbg_phContribution.txt",
	"reactorState.txt"};
	
static const std::vector<std::string> hydrolyse_files_integration{
	"dbg_reactionrates.txt",
	"outflow.txt"};

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
	std::regex header ("(^#)|(^[a-zA-Z])");
	std::stringstream STRInput(file_as_string);
	for(std::string line; getline(STRInput,line);)
	{
		if(!(std::regex_search(line, header)))
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
	for(const auto& f: hydrolyse_files) {
		std::string output_file_string = merge_hydrolysis_files(
			working_dir, 
			f.c_str(), 
			current_starttime,
			reactors);
		std::ofstream output_file;
		std::string output_file_name = storage_dir + "/" + f;
		std::cout << output_file_name << std::endl;
		
		if(is_first_timestep){
			output_file.open(output_file_name);
			output_file << header_string;
			output_file << output_file_string;
		}
		else{
			output_file.open(output_file_name, std::ios_base::app);
			output_file << output_file_string;
		}
			
		output_file.close();
		std::cout << output_file_string << std::endl;
	}
	
	std::cout << "Integrating files ...." << std::endl;
	
	//Merge hydrolysis files with integration
	for(const auto& f: hydrolyse_files_integration) {
		std::string output_names = "";
		for(int i=0; i<num_reactors; i++){
			output_names += (string) working_dir + "/" 
				+ reactors.at(i) + "/" 
				+ to_string(current_starttime) + "/"
				+ f;
			if(i!=num_reactors-1)
				output_names += "\n";
		}

		std::string output_file_string = merge_hydrolysis_files_integration(
			current_starttime, 
			num_reactors,
			output_names);
		std::ofstream output_file;
		std::string output_file_name = storage_dir + "/" + f;
		std::cout << output_file_name << std::endl;
	
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

} //end extern "C"

int main(){
	const char* reactors = "hydrolyse_0\nhydrolyse_1\nhydrolyse_2";
	
	merge_all_hydrolysis(
		"/home/paul/Schreibtisch/smalltest/tmp",
		reactors,
		0,
		0);
		//std::cout << testString << std::endl;
	return 0;
}
