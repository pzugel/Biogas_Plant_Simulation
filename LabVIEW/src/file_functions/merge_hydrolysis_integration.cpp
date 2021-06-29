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

#include <sstream>
#include <vector>
#include <regex>
#include <iostream>
#include <math.h>
#include <cmath>
#include <boost/algorithm/string.hpp>

std::string integrate_one_file(
			std::string reactorDir,
			std::string f)
{		
	std::cout << "integrate_one_file" << std::endl;	
	std::cout << "f --> " <<  f << std::endl;
	std::cout.precision(14);
	
	values.clear();
	std::string fileDir = reactorDir + "/" + f;
	std::cout << "fileDir: " << fileDir << std::endl;
	std::ifstream fileStream(fileDir.c_str());
	if(!fileStream.good()){
		std::cout << "file does not exist!" << std::endl;
		return "";
	}
	read_values_from_reactor(fileDir);
	
	std::vector<std::vector<std::string>> singleFileValue = values.at(0);
	std::vector<std::vector<std::string>> integratedValues;
	
	int timesteps = singleFileValue.size();
	int numValues = singleFileValue.at(0).size();
	
	for(int i=0; i<timesteps; i++) { //time iteration
		std::vector<std::string> line;
		
		double previous_time;
		if(i == 0) { 
			previous_time = floor(dot_conversion(singleFileValue.at(0).at(0)));//First timestep
		}
		else {
			previous_time = dot_conversion(singleFileValue.at(i-1).at(0)); //Previous timestep
		}
			
		double time = dot_conversion(singleFileValue.at(i).at(0)); //Time [h]
		double stepsize = time-previous_time;
		double all_liquid = std::abs(dot_conversion(singleFileValue.at(i).at(1))); //All Liquid [L/h]
		double liquid_per_timestep = all_liquid * stepsize; //Liquid in L			
		
		line.push_back(conv_to_string(time));
		line.push_back(conv_to_string(liquid_per_timestep));
		
		for(int j=2; j<numValues; j++) { //parameter iteration
			
			double amount = std::abs(dot_conversion(singleFileValue.at(i).at(j))); //[g/L]
			
			double amount_in_grams = amount * liquid_per_timestep; //g
			line.push_back(conv_to_string(amount_in_grams));
		}
		integratedValues.push_back(line);
	}				
	
	/*
	 * Compute sum for full timesteps only.
	 * This is needed because files might have different substeps.
	 * 
	 * First compute the sum for every step:
	 */
	std::vector<std::vector<std::string>> integratedValuesSum;
	std::vector<std::string> sumLines;
	for(int i=0; i<integratedValues.at(0).size(); i++) {
		sumLines.push_back("0.0"); //Initialize
	
	}
	double firstTimestep = dot_conversion(integratedValues.at(0).at(0));
	int firstTimestepFull = (int) firstTimestep;
	for(std::vector<std::string> line : integratedValues) {	
		sumLines.at(0) = line.at(0);
		
		double currentTime = dot_conversion(line.at(0));
		int currentTimeFull = (int) currentTime;
			
		for(int i=1; i<line.size(); i++) {			
			double sum = dot_conversion(sumLines.at(i)) + dot_conversion(line.at(i));
			sumLines.at(i) = conv_to_string(sum);
		}
		integratedValuesSum.push_back(sumLines);
		
		if(currentTimeFull != firstTimestepFull) {
			firstTimestepFull = currentTimeFull;
			for(int i=0; i<sumLines.size(); i++) {
				sumLines.at(i) = "0.0"; //Reset
			}
		}
	}
	
	/*
	 * Now take only the full steps
	 */	 
	std::vector<std::vector<std::string>> integratedValuesSumFull;
	for(std::vector<std::string> line : integratedValuesSum) {
		double time = dot_conversion(line.at(0));
		double time_rest;
		if (std::modf(time, &time_rest) == 0.0) { //Is full timestep?
			integratedValuesSumFull.push_back(line);
		}
	}
	
	/*
	 * Write out different files
	 */
	std::size_t extensionPos = fileDir.find_last_of(".");
	std::string header = get_header(fileDir.c_str());
	std::string integrated_header = header;
	boost::replace_all(integrated_header, "L/h", "L");
	boost::replace_all(integrated_header, "g/L", "g");
	
	//Only integrated
	std::string integratedOnly = integrated_header;
	for(int i=0; i<integratedValues.size(); i++) {
		for(int j=0; j<integratedValues.at(0).size(); j++) {
			integratedOnly += integratedValues.at(i).at(j) + "\t";
		}
		integratedOnly += "\n";
	}			
	std::string newFileName = fileDir.substr(0, extensionPos) + "_integrated" + fileDir.substr(extensionPos);	
	std::cout << "newFileName: " << newFileName << std::endl;
	std::ofstream output_file;
	output_file.open(newFileName);
	output_file << integratedOnly;
	output_file.close();
	
	//Integrated and summed
	std::string integratedSum = header;
	for(int i=0; i<integratedValuesSum.size(); i++) {
		for(int j=0; j<integratedValuesSum.at(0).size(); j++) {
			integratedSum += integratedValuesSum.at(i).at(j) + "\t";
		}
		integratedSum += "\n";
	}	
	newFileName = fileDir.substr(0, extensionPos) + "_integratedSum" + fileDir.substr(extensionPos);	
	std::cout << "newFileName: " << newFileName << std::endl;
	output_file.open(newFileName);
	output_file << integratedSum;
	output_file.close();
	
	//Integrated and summed - only full timesteps
	std::string integratedSumFull = header;
	for(int i=0; i<integratedValuesSumFull.size(); i++) {
		for(int j=0; j<integratedValuesSumFull.at(0).size(); j++) {
			integratedSumFull += integratedValuesSumFull.at(i).at(j) + "\t";
		}
		integratedSumFull += "\n";
	}	
	newFileName = fileDir.substr(0, extensionPos) + "_integratedSum_fullTimesteps" + fileDir.substr(extensionPos);	
	std::cout << "newFileName: " << newFileName << std::endl;
	output_file.open(newFileName);
	output_file << integratedSumFull;
	output_file.close();
	
	return integratedSumFull;
}

std::string  merge_files_integration(
	const char* dir, 
	std::string filename,
	std::vector<std::string> reactors)
{
	for(std::string r : reactors) {
		std::string reactor_dir = (std::string) dir + "/" + r;
		integrate_one_file(reactor_dir, filename);		
	}
		
	std::size_t extensionPos = filename.find_last_of(".");
	std::string newFileName = filename.substr(0, extensionPos) + "_integrated" + filename.substr(extensionPos);
	std::cout << newFileName << std::endl;	
	std::string output_file_string = merge_hydrolysis_files(dir, newFileName, reactors);
			
	return output_file_string;
}
