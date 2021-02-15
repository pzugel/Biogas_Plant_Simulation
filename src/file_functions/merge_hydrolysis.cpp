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

#include "functions.h"

static std::vector<std::vector<std::vector<std::string>>> values;

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

/**
 * Writes all values from an output file of a reactor at direction
 * "dir" into the values vector 
 * 
 * @param dir: Direction to output file
 * @return bool if file exists
 */
bool read_values_from_reactor(std::string dir){
		std::vector<std::vector<std::string>> hydrofile;
		std::cout << "read_values_from_reactor: "  << dir << std::endl;
		std::ifstream f(dir.c_str());
		if(f.good()){
			const char* vals = remove_header(dir.c_str());
			
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
		else{
			std::cout << "File does not exist!" << std::endl;
			return false;
		}
		return true;
	}	

/**
 * Reads all hydrolyse output files of all hydrolyse reactors for 
 * the current timestep and merges them into the 
 * /storage_hydrolyse folder.  
 * 
 * @param dir: Path pointing to the hydrolyse folders
 * @param filename: Output file to merge, e.g. "subMO_mass.txt"
 * @param current_starttime: Timestep in the simulation
 * @param reactors: Vector of names for the hydrolyse reactors
 * @return File to write as string
 */
std::string merge_hydrolysis_files(
	const char* dir,  
	const char* filename,
	int current_starttime,
	std::vector<std::string> reactors)
{	
	int num_reactors = reactors.size();
	int p = 14;	
	std::string working_dir = dir;
	std::string output_file_string = "";
	
	//Write data into "values" vector
	bool fileExists = false;
	values = {};
	for(const auto& d: reactors) {
		std::string file_direction = working_dir + "/" 
			+ d + "/" 
			+ std::to_string(current_starttime) + "/" 
			+ (std::string) filename;
		fileExists = read_values_from_reactor(file_direction);
	}	
	
	if(fileExists)
	{
		std::cout << "Adding:" << std::endl;

		std::string dir_for_header = working_dir + "/" + reactors.at(0) 
			+ "/" + std::to_string(current_starttime) + "/" + filename;
		
		get_header(dir_for_header.c_str()); 
		
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
	}
	else
		std::cout << "Nothing to add up!" << std::endl;
	return output_file_string;
}
