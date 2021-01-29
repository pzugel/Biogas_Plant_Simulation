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

static std::string data_string;
static std::string header_string;
static std::string output_file_string;

static const std::vector<std::string> hydrolyse_files{
	"digestateConcentrations.txt",
	"subMO_mass.txt",
	"valveGasFlow.txt",
	"gas_Volfraction.txt",
	"producedNormVolumeCumulative.txt",
	"dbg_avgEqValues.txt",
	"dbg_nitrogenRates.txt",
	"producedNormVolumeHourly.txt",
	"dbg_reactionrates.txt",
	"dbg_phContribution.txt",
	"reactorState.txt"};

//dbg_density.txt
//reactorCODcontent.txt
//outflow.txt --Downflow

/**
 * Helper function to correcly read out doubles in dot representation
 * "311.15" -> 311,15 
 * 
 * @param s: Double as string
 * @return double
 */
double dot_conversion(std::string s){
	std::istringstream valStream(s);
	double out;
	while (valStream)
		valStream >> out;
	return out;
}

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
			line.erase(remove_if(line.begin(), line.end(), isspace), line.end());
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
 * Reads all hydrolyse output files of all hydrolyse reactors for 
 * the current timestep and merges them into the 
 * /storage_hydrolyse folder.  
 * 
 * @param dir: Path pointing to the hydrolyse folders
 * @param reactor_names: String of names for the hydrolyse reactors
 * @param filename: Output file to merge, e.g. "subMO_mass.txt"
 * @param current_starttime: Timestep in the simulation
 * @return File to write as string
 */
const char* merge_hydrolysis_files(
	const char* dir,  
	const char* reactor_names,
	const char* filename,
	int current_starttime)
{	
	int p = 14;	
	std::string working_dir = dir;
	output_file_string = "";
	
	//Write reactor names into vector
	std::vector<std::string> reactors;
	std::istringstream dir_stream(reactor_names);
	for(std::string line; std::getline(dir_stream,line);)
	{
		reactors.push_back(line);
	}
	int num_reactors = reactors.size();
	
	//Write data into "values" vector
	std::vector<std::vector<std::vector<std::string>>> values;
	for(const auto& d: reactors) {
		std::vector<std::vector<std::string>> hydrofile;
		std::string name = working_dir + "/" + d + "/" + std::to_string(current_starttime) + "/" + (std::string) filename;
		std::ifstream f(name.c_str());
		if(f.good()){
			const char* vals = remove_header(name.c_str());
			
			std::stringstream csv(vals);
			for(std::string line; getline(csv,line);)
			{
				std::stringstream iss(line);
				std::string item;
				std::vector<std::string> a;
				while (std::getline(iss, item, '\t')) {
					a.push_back(item);
				}
				hydrofile.push_back(a);
			}
			values.push_back(hydrofile);
		}
		else
			std::cout << "File does not exist!" << std::endl;
	}	
	
	std::string dir_for_header = working_dir + "/" + reactors.at(0) 
		+ "/" + std::to_string(current_starttime) + "/" + filename;

	get_header(dir_for_header.c_str()); 
	//output_file_string = header_string; //Add header to output
	
	int num_entries_line = values.at(0).at(0).size(); //Entries per line
	
	//line_counter to keep track of the timesteps
	std::vector<int> line_counter;
	for(int i=0; i<num_reactors; i++)
		line_counter.push_back(0);
	
	double current_time = current_starttime;
	double endtime = current_starttime + 1;
	std::stringstream output_stream;
	
	while(current_time < endtime){
		//Get current max
		double max_time = 0;
		for(int j=0; j<num_reactors; j++){
			int c = line_counter.at(j);
			double time = dot_conversion(values.at(j).at(c).at(0));
			if(time>max_time)
				max_time = time;
		}
			
		//Increase line counter
		for(int j=0; j<num_reactors; j++){

			while(dot_conversion(values.at(j).at(line_counter.at(j)).at(0)) <= max_time){
				if(dot_conversion(values.at(j).at(line_counter.at(j)).at(0)) == endtime){
					line_counter.at(j) += 1;
					break;
				}
					
				line_counter.at(j) += 1;
			}
		}
	
		//Combine current timestep
		output_stream << std::fixed << std::setprecision(p) << max_time << "\t";
			
		for(int i=1; i<num_entries_line; i++){
			double sum = 0;
			for(int j=0; j<num_reactors; j++){
				sum += dot_conversion(values.at(j).at(line_counter.at(j)-1).at(i));
			}
			output_stream << std::fixed << std::setprecision(p) << sum << "\t";
		}
		output_stream << "\n";
		current_time = max_time;
	}
	
	output_file_string += output_stream.str();
	return output_file_string.c_str();
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
	bool is_first_timestep = false;
	if(simulation_starttime == current_starttime)
		is_first_timestep = true;
		
	std::string storage_dir = (std::string) working_dir + "/storage_hydrolyse";
	std::cout << "storage_dir: " << storage_dir << std::endl;
	
	for(const auto& f: hydrolyse_files) {
		merge_hydrolysis_files(working_dir, reactor_names, f.c_str(), current_starttime);
		std::ofstream output_file;
		std::string new_file = storage_dir + "/" + f;
		std::cout << new_file << std::endl;
		
		if(is_first_timestep){
			output_file.open(new_file);
			output_file << header_string;
			output_file << output_file_string;
		}
		else{
			output_file.open(new_file, std::ios_base::app);
			output_file << output_file_string;
		}
			
		output_file.close();
		std::cout << output_file_string << std::endl;
	}
}

} //end extern "C"
