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


extern "C"{
	
double dot_conversion(std::string s){
	std::istringstream valStream(s);
	double out;
	while (valStream)
		valStream >> out;
	return out;
}

/**
 * Reads a specification file and updates the simulation starttime
 * 
 * @param spec_dir: Path pointing to a specification
 * @param starttime: The new starttime
 */
void update_starttime(const char* spec_dir, double starttime)
{
	std::regex starttime_regex ("sim_starttime(\\s)*=(\\s)*[0-9.E\\-\\*]*(\\s)*,?");
	std::ifstream spec_stream(spec_dir);
	std::string output_spec = "";
	for(std::string line; getline(spec_stream,line);)
	{
		if((std::regex_search(line, starttime_regex)))
		{
			std::string newLine = "";
			size_t num_tabs = std::count(line.begin(), line.end(), '\t');
			for(int i=0; i<num_tabs; i++)
				newLine += "\t";
			std::string starttime_string = std::to_string(starttime);
			boost::replace_all(starttime_string, ",", ".");
			newLine += "sim_starttime=" + starttime_string + ",";
			output_spec += newLine + "\n";
		}
		else
			output_spec += line + "\n";
	}
		
	std::ofstream new_spec;
	new_spec.open (spec_dir);
	new_spec << output_spec;
	new_spec.close();
}

/**
 * Reads a specification file and updates the simulation endtime
 * 
 * @param spec_dir: Path pointing to a specification
 * @param endtime: The new endtime
 */
void update_endtime(const char* spec_dir, double endtime)
{
	std::regex endtime_regex ("sim_endtime(\\s)*=(\\s)*[0-9.E\\-\\*]*(\\s)*,?");
	std::ifstream spec_stream(spec_dir);
	std::string output_spec = "";
	for(std::string line; getline(spec_stream,line);)
	{
		if((std::regex_search(line, endtime_regex)))
		{
			std::string newLine = "";
			size_t num_tabs = std::count(line.begin(), line.end(), '\t');
			for(int i=0; i<num_tabs; i++)
				newLine += "\t";
			std::string endtime_string = std::to_string(endtime);
			boost::replace_all(endtime_string, ",", ".");
			newLine += "sim_endtime=" + endtime_string + ",";
			output_spec += newLine + "\n";
		}
		else
			output_spec += line + "\n";
	}
		
	std::ofstream new_spec;
	new_spec.open (spec_dir);
	new_spec << output_spec;
	new_spec.close();
}

/**
 * Reads a specification file and updates the checkpoint directory
 * 
 * @param spec_dir: Path pointing to a specification
 * @param checkpoint_dir: New path for the checkpoint directory
 */
void update_read_checkpoint(const char* spec_dir, const char* checkpoint_dir)
{
	std::regex checkpoint_regex ("doReadCheckpoint(\\s)*=(\\s)*(true|false)(\\s)*,?");
	std::regex checkpointDir_regex ("checkpointDir");
	
	
	/*
	 * Check if the file already contains a checkpoint dir
	 * If yes, we remove it
	*/
	std::ifstream spec_stream(spec_dir);
	std::string input_no_dir = "";
	for(std::string line; getline(spec_stream,line);)
	{
		if(!(std::regex_search(line, checkpointDir_regex)))
			input_no_dir += line + "\n";
	}

	/*
	 * Now we set "doReadCheckpoint" to true and add the new
	 * checkpoint directory
	*/
	std::string output_spec = "";
	std::stringstream spec_string_stream(input_no_dir);
	for(std::string line; getline(spec_string_stream,line);)
	{
		if((std::regex_search(line, checkpoint_regex)))
		{
			std::string newLine = "";
			size_t num_tabs = std::count(line.begin(), line.end(), '\t');
			for(int i=0; i<num_tabs; i++)
				newLine += "\t";
			newLine += "doReadCheckpoint=true,\n";
			for(int i=0; i<num_tabs; i++)
				newLine += "\t";
			newLine += "checkpointDir=\"" + (std::string) checkpoint_dir + "\",";
			output_spec += newLine + "\n";
		}
		else
			output_spec += line + "\n";
	}
	
	std::ofstream new_spec;
	new_spec.open (spec_dir);
	new_spec << output_spec;
	new_spec.close();
}

} //end extern "C"

/**
 * Only to test functionality
 */
int main(){
	const char* spec = "/home/paul/Schreibtisch/hydrolyse_checkpoint.lua";
	const char* checkpoint = "/home/paul/Schreibtisch/smalltest/tmp/hydrolyse_0/1/";
	double starttime;
	double endtime;
	std::cout << "starttime: ";
	std::cin >> starttime;
	std::cout << "endtime: ";
	std::cin >> endtime;
	
	update_starttime(spec, starttime);
	update_endtime(spec, endtime);
	update_read_checkpoint(spec, checkpoint);
	return 0;
}
