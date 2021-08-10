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

static std::vector<std::vector<double>> mergedArray;
/**
 * Reads all hydrolysis output files of all hydrolysis reactors for 
 * the current timestep and merges them into the 
 * /storage_hydrolysis folder.  
 * 
 * @param dir: Path pointing to the hydrolysis folders
 * @param filename: Output file to merge, e.g. "subMO_mass.txt"
 * @param reactors: Vector of names for the hydrolysis reactors
 * @return File to write as string
 */
std::string merge_hydrolysis_files(
	std::string working_dir,  
	std::string filename,
	std::vector<std::string> reactors)
{	
	int num_reactors = reactors.size();
	std::string output_file_string = "";
	values.clear();
	
	//Add header to new file
	std::string dir_for_header = working_dir + "/"  + reactors[0] + "/"  + filename;
	output_file_string += get_header(dir_for_header.c_str()); 
	
	
	//Write data into "values" vector - Check if file exists for every reactor	
	bool allExist = true;
	for(std::string d: reactors) {
		std::string file_direction = working_dir + "/"
			+ d + "/" 
			+  filename;
		allExist = allExist && read_values_from_reactor(file_direction.c_str());
	}	
	
	/*
	 * Reactors might have different timesteps. Therefore we first need to check
	 * if a timestep exists in all files for all reactors
	 */
	if(allExist)
	{			
					
		std::vector<std::string> timeCol; //Contains the times from first reactor file
		for(std::vector<std::string> line : values.at(0)) {
			timeCol.push_back(line.at(0));
		}
		
		//Check if all reactors contain a timestep
		std::vector<std::string> validTimes; //All valid timesteps
		for(std::string time : timeCol) {
			bool exists = true;
			for(int i=0; i<num_reactors; i++) {
				bool found = false;
				int numLines = values.at(i).size();
				for(int j=0; j<numLines; j++) {
					if(values.at(i).at(j).at(0) == time) {
						found = true;
					}
				}
				exists = exists && found;
			}
			
			if(exists) {
				validTimes.push_back(time);
			}
		}
		
		//Initialize an output array
		int num_entries_line = values.at(0).at(0).size(); //Entries per line
		std::vector<std::vector<double>> outputValues;
		for(std::string time : validTimes) {
			std::vector<double> line;
			line.push_back(dot_conversion(time));
			for(int i=1; i<num_entries_line; i++) {
				line.push_back(0.0);
			}
			outputValues.push_back(line);
		}
		
		//Summation over all files and timesteps
		int lineCount = 0;
		for(std::string time : validTimes) {
			for(int i=0; i<num_reactors; i++) {
				for(int j=0; j<values.at(i).size(); j++) {
					if(values.at(i).at(j).at(0) == time) {
						for(int k=1; k<num_entries_line; k++) {
							double sum = outputValues.at(lineCount).at(k) + dot_conversion(values.at(i).at(j).at(k));
							outputValues.at(lineCount).at(k) = sum;
						}
					}
				}
			}
			++lineCount;
		}
		
		//Write output array to string
		mergedArray = outputValues;
		for(std::vector<double> line : outputValues) {
			for(double d : line) {
				output_file_string += conv_to_string(d) + "\t";
			}
			output_file_string += "\n";
		}		
	}
	else
		std::cout << "Nothing to add up!" << std::endl;	
	return output_file_string;
}
