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
	"dbg_gamma.txt",
	"dbg_reactionrates.txt"};
	
static const std::vector<std::string> output_files_integration{
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
 * Updates the names of the integrated files in the outputFiles 
 * from the hydrolysis_storage
 * 
 * @param outputFiles_path: Path pointing to outputFiles.lua
 */
void update_outputFiles_integration(const char* outputFiles_path)
{		
	std::ifstream outputFiles_stream(outputFiles_path);
	std::string mBuf((std::istreambuf_iterator<char>(outputFiles_stream)),
				 std::istreambuf_iterator<char>());
	std::string outputFiles_string = mBuf;
	
	std::string outflow_filename = "outflow.txt";
	std::string outflow_title = "outflow=";
	std::string reactionRates_filename = "dbg_reactionrates.txt";
	std::string reactionRates_title = "reactionRates=";
	
	//Replace outflow entry
	std::size_t outflow_filename_ind = outputFiles_string.find(outflow_filename);	
	std::size_t outflow_title_ind = outputFiles_string.find(outflow_title);
	
	if(outflow_filename_ind != std::string::npos){		
		outputFiles_string.replace(outflow_filename_ind, outflow_filename.length(),
			"outflow_integrated.txt");
		outputFiles_string.replace(outflow_title_ind, outflow_title.length(), 
			"outflow_integrated=");
	}
	
	//Replace dbg_reactionrates entry
	std::size_t reactionRates_filename_ind = outputFiles_string.find(reactionRates_filename);	
	std::size_t reactionRates_title_ind = outputFiles_string.find(reactionRates_title);
	
	if(reactionRates_filename_ind != std::string::npos){	
		outputFiles_string.replace(reactionRates_filename_ind, reactionRates_filename.length(),
			"dbg_reactionrates_integrated.txt");
		outputFiles_string.replace(reactionRates_title_ind, reactionRates_title.length(), 
			"reactionRates_integrated=");
	}
	
	//Write file
	std::ofstream output_file;
	output_file.open(outputFiles_path);
	output_file << outputFiles_string;
	output_file.close();
}

/**
 * Function called by the storage element
 * 
 * First iterate over all possible output files that need integration
 * as specified in the "output_files_integration" vector and call the 
 * "merge_files_integration" function.
 * 
 * Then iterate over all other possible hydrolysis output files as 
 * specified in the "output_files" vector and call the 
 * "merge_hydrolysis_files" function to merge all output files for
 * all hydrolysis reactors.
 * 
 * @param working_dir: Path to working directory
 * @param reactor_names: String of names for the hydrolysis reactors
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

	std::string storage_dir = (std::string) working_dir + "/storage_hydrolysis";
	std::cout << "storage_dir: " << storage_dir << "\n" << std::endl;
	
	//Merge hydrolysis files with integration
	for(std::string f : output_files_integration) {
		for(std::string r : reactors) {
			std::string reactor_dir = (std::string) working_dir + "/" + r;
			integrate_one_file(reactor_dir, f);		
		}
	}

	//Merge hydrolysis files (no integration)
	for(std::string f: output_files) {
		std::string output_file_string = merge_hydrolysis_files(
			working_dir, 
			f,
			reactors);
			
		//If file does not exist an empty string is returned
		if(output_file_string != ""){ 
			std::string output_file_name = 	storage_dir + "/" + f;
			std::cout << output_file_name << std::endl;
			
			std::ofstream output;
			output.open(output_file_name);
			output << output_file_string;
			output.close();
			
			std::cout << output_file_string << std::endl;
		}
	}
	
	merge_storage_outflow(storage_dir, (std::string) working_dir, reactors);
}

/**
 * Called by the methane element to integrate the outflow files.
 * 
 * @param methane_dir Path to the methane element
 * @throws IOException
 */
void merge_all_methane(
	const char* methane_dir)
{
	std::cout << "methane_dir: " << methane_dir << std::endl;		
		
	//Merge methane files with integration
	for(std::string f : output_files_integration) {
		integrate_one_file(methane_dir, f);		
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
 * @param flowVal: Constant flow value
 */
void update_methane_inflow(
	const char* outflow_infile,
	const char* methane_specfile,
	int flowVal)
{
	write_methane_inflow(outflow_infile, methane_specfile, flowVal);
}

/**
 * Updates the specification file for the hydrolysis reacor with the current
 * outflow from the methane reactor
 * 
 * @param outflow_infile: Path pointing the outflow.txt (methane)
 * @param hydrolysis_specfiles: String with the specfile directory
 * @param fractions: Fractional value
 * @param flowVal: Constant flow value
 */
void update_hydrolysis_inflow(
	const char* outflow_infile,
	const char* hydrolysis_specfile,
	double fraction,
	int flowVal)
{
	write_hydrolysis_inflow(outflow_infile, 
		hydrolysis_specfile, 
		fraction,
		flowVal);
}

/**
 * Updates the specification file in the very first timestep by
 * adjusting the inflow time to the starttime
 * 
 * @param hydrolysis_specfiles: String with the specfile directory
 * @param flowVal: Constant flow value
 */
void update_initial_hydrolysis_inflow(
	const char* hydrolysis_specfile,
	int flowVal)
{
	write_inital_hydrolysis_inflow(hydrolysis_specfile, flowVal);
}

/**
 * Updates the feeding timetable in a specification file 
 * for a hydrolysis reactor
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
	/*
	const char* reactorDir = "/home/paul/Schreibtisch/Simulations/LabVIEW/Demo/TEST_FOLDER/hydrolysis_0";
	const char* f = "outflow.txt";
	integrate_one_file(reactorDir, f);
	*/
	
	/*
	const char* working_dir = "/home/paul/Schreibtisch/Simulations/LabVIEW/Demo/TEST_FOLDER";
	const char* reactor_names = "hydrolysis_0\nhydrolysis_1";
	merge_all_hydrolysis(working_dir, reactor_names);
	
	const char* outflow_infile = "/home/paul/Schreibtisch/Simulations/LabVIEW/Demo/biogas_20210715_120455@STRUCT_2_STAGE_PL/storage_hydrolysis/outflow_integratedSum_Rates.txt";
	const char* methane_specfile = "/home/paul/Schreibtisch/Simulations/LabVIEW/Demo/biogas_20210715_120455@STRUCT_2_STAGE_PL/methane/0/methane_checkpoint.lua";
	write_methane_inflow(outflow_infile,methane_specfile, 15);
	*/
	
	const char* hydroSpec = "/home/paul/Schreibtisch/Simulations/LabVIEW/Demo/biogas_20210715_121637@STRUCT_2_STAGE_PL/hydrolysis_0/0/hydrolysis_checkpoint.lua";
	write_inital_hydrolysis_inflow(hydroSpec, 28);
	
	/*
	const char* outflow_infile = "/home/paul/Schreibtisch/Simulations/LabVIEW/Demo/biogas_20210709_130625@STRUCT_1_STAGE/methane/outflow_integratedSum_fullTimesteps.txt";
	const char* hydrolysis_specfile = "/home/paul/Schreibtisch/Simulations/LabVIEW/Demo/biogas_20210709_130625@STRUCT_1_STAGE/hydrolysis_0/3/hydrolysis_checkpoint.lua";
	double fraction = 1.0;
	update_hydrolysis_inflow(outflow_infile,hydrolysis_specfile,fraction);
	return 0;
	*/
}
