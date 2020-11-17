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

#include "biogas_spec_vali_reader.h"
#include <string>
#include <vector>
#include <regex>
#include <boost/algorithm/string.hpp>
#include <stdexcept>

/**
 * Transform the specification input
 *
 * Modifies the specification file it in such
 * a way that the "generateSpecs()" method can 
 * get easier access to the data.
 */
void BiogasSpecValiReader::
transformSpecInput()
{
	this->input_specModified= this->input;

	this->input_specModified.erase(remove_if(this->input_specModified.begin(), this->input_specModified.end(), isspace), this->input_specModified.end());
	std::regex str_arr ("\\{\"[a-zA-Z0-9_]+\"\\}");
	std::regex timestamp ("\\{[0-9E.\\-\\*]+,[0-9E.\\-\\*]+\\}");

	std::smatch match_strArr; //Match types Str[]
	while (regex_search(this->input_specModified, match_strArr, str_arr)) 
	{
		std::string matched_str_arr = match_strArr.str(0).substr(1, match_strArr.str(0).length()-2);
		boost::replace_all(this->input_specModified, match_strArr.str(0), "$"+matched_str_arr+"?");  
	} 
	
	std::smatch match_timeStamps; //Match timestamps
	while (regex_search(this->input_specModified, match_timeStamps, timestamp)) 
	{
		std::string::size_type pos = match_timeStamps.str(0).find(",");
		std::string::size_type pos_end = match_timeStamps.str(0).find("}");
		std::string replacement = "$" + match_timeStamps.str(0).substr(1, pos-1) + "#" + match_timeStamps.str(0).substr(pos+1,pos_end-(pos+1)) + "?";
		boost::replace_all(this->input_specModified, match_timeStamps.str(0), replacement);  
	} 
	
	std::regex param_open ("\\{");
	std::regex param_close ("\\}$");
	std::regex param_close_comma ("\\},$");
	std::regex comma (",");
	std::regex eq ("=$");

	this->input_specModified = std::regex_replace(this->input_specModified, param_open, "\n");
	this->input_specModified = std::regex_replace(this->input_specModified, comma, "\n");

	std::string tmp = "";
	std::istringstream lineIter(this->input_specModified);
	for(std::string line; std::getline(lineIter, line); )
	{
		line = std::regex_replace(line, param_close_comma, "");
		line = std::regex_replace(line, param_close, "");
		line = std::regex_replace(line, comma, "");
		line = std::regex_replace(line, eq, "");

		if(!line.empty())
			tmp += line + "\n";
	}
	this->input_specModified = tmp;
	boost::replace_all(this->input_specModified, "$", "{");
	boost::replace_all(this->input_specModified, "?", "}");
	boost::replace_all(this->input_specModified, "#", ",");
}

/**
 * Test if specification file matches with parameters from validation file
 *
 * Checks whether the number of parameters from the specification file
 * is equal to the number of parameters in the validation file. If true
 * we also check if all parameters have the same name.
 * 
 * If the files do not match we wont load the specification into LabView
 * and write an error message into "validationMessage".
 * 
 * @return Bool whether match is successful
 */
bool BiogasSpecValiReader::
testValidationMatch()
{
	std::istringstream lineIterCount(this->input_specModified);
	int num_lines_count = 0;
	for(std::string line; std::getline(lineIterCount, line); )
		++num_lines_count;
		
	if(num_lines_count!=this->number_of_entries)
	{
		this->validationMessage = "Failed matching of specificaiton file with validation file!\n";
		this->validationMessage += "--> #parameters in the validation file: " + std::to_string(this->number_of_entries) +"\n";
		this->validationMessage += "--> #parameters in the specification file: " + std::to_string(num_lines_count) +"\n";
		return false; //Different number of parameters
	}

	std::regex timestamp ("\\{[0-9E.\\-\\*]+,[0-9E.\\-\\*]+\\}");
	std::istringstream lineIter(this->input_specModified);
	
	int num_lines_test = 0;
	for(std::string line; std::getline(lineIter, line); )
	{
		size_t valuePos = line.find("="); 
		if(!std::regex_search(line, timestamp))
		{
			if (valuePos == std::string::npos)
			{
				if(line!=this->entries[num_lines_test].leftCell)
				{
					this->validationMessage = "Failed matching of specificaiton file with validation file!\n";
					this->validationMessage += "--> " + line +"\n";
					this->validationMessage += "--> " + entries[num_lines_test].leftCell +"\n";
					return false; //Name does not match the validation file
				}
			}
			else
			{
				std::string specName = line.substr(0,valuePos);
				boost::replace_all(specName, "[", "");
				boost::replace_all(specName, "]", "");
				if(specName!=this->entries[num_lines_test].leftCell)
				{
					this->validationMessage = "Failed matching of specificaiton file with validation file!\n";
					this->validationMessage += "--> " + specName +"\n";
					this->validationMessage += "--> " + entries[num_lines_test].leftCell +"\n";
					return false; //Name does not match the validation file
				}
			}
		}
		++num_lines_test;
	}
	
	return true;
}

/**
 * Generate all specification data  
 *
 * Parses the specification file and save all data in the "entries"
 * data structure. The input string "input_specModified" has been
 * prepared by the "transformSpecInput()" method for easier parsing.
 * 
 * We run the "testValidationMatch()" method first to see if the 
 * specification we loaded matches the validation file.
 * 
 * @return Bool whether match is successful / specs were loaded
 */
bool BiogasSpecValiReader::
generateSpecs()
{	
	if(!testValidationMatch())
		return false;
		
	std::regex timestamp ("\\{[0-9E.\\-\\*]+,[0-9E.\\-\\*]+\\}");
	
	std::istringstream lineIter(this->input_specModified);
	int index = 0;
	for(std::string line; std::getline(lineIter, line); )
	{
		size_t valuePos = line.find("="); 
    	if (valuePos == std::string::npos)
		{
			if(std::regex_search(line, timestamp))
			{
				this->entries[index].specVal = line;
			}
		}
		else
		{
			this->entries[index].specVal = line.substr(valuePos+1);
		}
		++index;
	}

	for(int i=0; i<this->number_of_entries; i++)
		this->specString += entries[i].specVal + "\n";
		
	return true;
}
