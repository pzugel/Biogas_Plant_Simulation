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
#include <iostream>

/**
 * Count the number of entries and add as many 
 * "TableEntries" to the "entries" List.
 * @param input Specification file as string
 */
void BiogasSpecValiReader::
generateSpecIndents(std::string input){
	
	std::regex param_open ("\\{");
	std::regex param_close ("\\}$");
	std::regex param_close_comma ("\\},$");
	std::regex comma (",");
	std::regex eq ("=$");

	input = std::regex_replace(input, param_open, "{\n");
	input = std::regex_replace(input, comma, "\n");
	
	this->entries = {};
	std::istringstream lineIter(input);
	int ind = 0;
	for(std::string line; std::getline(lineIter, line); )
	{
		if (line.find('=') != std::string::npos || line.rfind("$", 0) == 0)
		{
			TableEntry* newEntry = new TableEntry();
			newEntry->indent = ind;
			this->entries.push_back(*newEntry);
		}
		if (line.find('{') != std::string::npos)
			ind += 1;
		if (line.find('}') != std::string::npos)
			ind -= 1;	
	}
	
	this->number_of_entries = this->entries.size();
	std::cout << "Num entries: " << this->entries.size() << std::endl;
}

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
	std::regex str_arr ("\\{(\"[a-zA-Z0-9_]+\"(,)?)*\\}");
	std::regex timestamp ("\\{([0-9Ee.\\-\\*]+,)*[0-9Ee.\\-\\*]+\\}");

	std::smatch match_strArr; //Match types Str[]
	while (regex_search(this->input_specModified, match_strArr, str_arr)) 
	{
		std::string matched_str_arr = match_strArr.str(0).substr(1, match_strArr.str(0).length()-2);
		boost::replace_all(matched_str_arr, ",", "#");
		boost::replace_all(this->input_specModified, match_strArr.str(0), "$"+matched_str_arr+"?");  
	} 
	
	std::smatch match_timeStamps; //Match timestamps
	while (regex_search(this->input_specModified, match_timeStamps, timestamp)) 
	{
		std::string replacement = match_timeStamps.str(0);
		boost::replace_all(replacement, "{", "$");
		boost::replace_all(replacement, "}", "?");
		boost::replace_all(replacement, ",", "#");
		boost::replace_all(this->input_specModified, match_timeStamps.str(0), replacement);  
	} 
	
	std::regex param_open ("\\{");
	std::regex param_close ("\\}$");
	std::regex param_close_comma ("\\},$");
	std::regex comma (",");
	std::regex eq ("=$");
	
	generateSpecIndents(this->input_specModified);

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
	std::regex timestamp ("\\{([0-9Ee.\\-\\*]+,)+[0-9Ee.\\-\\*]+\\}");
	
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
				this->entries[index].leftCell = "timeStamp";
				this->entries[index].type = "NULL";
				this->entries[index].glyph = 0;
			}
			else
			{
				this->entries[index].leftCell = line;
				this->entries[index].glyph = 15;
				this->entries[index].type = " ";
			}
		}
		else
		{
			this->entries[index].glyph = 0;
			this->entries[index].type = "NULL";
			this->entries[index].specVal = line.substr(valuePos+1);
			this->entries[index].leftCell = line.substr(0, valuePos);
		}
		++index;
	}

	for(int i=0; i<this->number_of_entries; i++)
	{
		this->specString += 
		std::to_string(this->entries[i].indent) + " " +
		std::to_string(this->entries[i].glyph) + " " +
		this->entries[i].leftCell + " " +
		this->entries[i].type + " " +
		this->entries[i].specVal + "\n";
	}
	
	std::cout << this->specString << std::endl;
	return true;
}
