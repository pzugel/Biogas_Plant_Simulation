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
#include <fstream>	
#include <regex>
#include <boost/algorithm/string.hpp>
#include <iostream>

static int number_of_validation_entries;

/**
 * Transform the validation input
 *
 * Modifies the validation file it in such
 * a way that the "generateValues()" method can 
 * get easier access to the data.
 */
void BiogasSpecValiReader::
transformValiInput()
{
	std::regex range_value ("(range=\\{values=\\{[0-9\\.]+,[0-9\\.]+\\}\\})|(range=\\{[a-zA-Z0-9=\\.]+,[a-zA-Z0-9=\\.]+\\})");
	std::regex table_content ("tableContent=\\{values=\\{[\"a-zA-Z0-9_,]+\\}\\}");
	std::regex time_table_content ("timeTableContent=\\{numberEntries=[0-9]+\\}");
	std::regex table_element ("\"[a-zA-Z0-9_]+\"");
	std::regex number ("[0-9]+");
	std::regex character ("[a-zA-Z]+");
	this->input_valiModified = this->input;
	std::smatch match_range_value; //Replace all ranges with "range=[...]"
	while (regex_search(this->input_valiModified, match_range_value, range_value))
	{	
		std::string match = match_range_value.str(0);
		match = std::regex_replace(match, character, "");
		boost::replace_all(match, "{", "[");
		boost::replace_all(match, "}", "]");
		boost::replace_all(match, "=", "");
		boost::replace_all(match, "[[", "[");
		boost::replace_all(match, "]]", "]");
		boost::replace_all(match, ",", "-");
		std::string replacement = "range=" + match;
		boost::replace_all(this->input_valiModified, match_range_value.str(0), replacement);
	}

	std::smatch match_table; //Replace all tables with as many "{}" as there are elements in the table
	std::smatch match_table_entries;
	while (regex_search(this->input_valiModified, match_table, table_content)) 
	{
		std::string match = match_table.str(0);
		std::string replacement = "";
		while (regex_search(match, match_table_entries, table_element)) 
		{
			std::string tableEntry = match_table_entries.str(0);
			replacement += tableEntry + "={}";
			match = match_table_entries.suffix(); 
		}
		boost::replace_all(this->input_valiModified, match_table.str(0), replacement);
	} 

	std::smatch match_time_table; //Replace all time tables with as many "{}" as there are elements in the table
	std::smatch num_entries;
	while (regex_search(this->input_valiModified, match_time_table, time_table_content)) 
	{
		std::string match = match_time_table.str(0);
		std::string replacement = "";
		if(regex_search(match, num_entries, number))
		{
			int num = std::stoi(num_entries.str(0));
			for (int i=0; i<num+1;i++)
				replacement += "timeTableContent={}";
		}
		
		boost::replace_all(this->input_valiModified, match, replacement);  
	}
}


/**
 * Generate all validation data  
 *
 * Parses the validation file and searches for keywords
 * such as "type", "default" or "range" and their names. 
 * The values are then saved in the "entries"
 * data structure. The input string "input_valiModified" has 
 * been prepared by the "transformValiInput()" method for 
 * easier parsing.
 */
bool BiogasSpecValiReader::
generateValues()
{	
	number_of_validation_entries = 0;
	for(int i=0; i<this->input_valiModified.size(); i++)
	{
		if(this->input_valiModified.at(i) == '{')
			++ number_of_validation_entries;
	}
	
	std::string line_input = this->input_valiModified; 
	boost::replace_all(line_input, "{", "{\n");
	boost::replace_all(line_input, "}", "\n}\n");
	boost::replace_all(line_input, ",", "\n");

	std::regex names_re ("^[a-zA-Z0-9_\"]+=\\{");
	std::regex table_entry ("^((\")|(timeTableContent))");
	std::regex type_re ("^type=");
	std::regex default_re ("^default=");
	std::regex range_re ("^range=");
		
	int index = -1;
	std::istringstream lineIter(line_input);
	std::string last_type;
	std::string last_default;
	for (std::string line; std::getline(lineIter, line); )
	{
		if(std::regex_search(line, names_re))
		{
			++index;
			this->entries[index].leftCell = line.substr(0,line.size()-2);
			if(std::regex_search(line, table_entry))
			{
				this->entries[index].type = last_type;	
				this->entries[index].defaultVal = last_default;
			} else {
				last_default = "";
			}
		}

		if(std::regex_search(line, type_re))
		{
			last_type = line.substr(6,line.size()-7);
			if(entries[index].glyph == 0)
				this->entries[index].type = last_type;
		}

		if(std::regex_search(line, default_re))
		{
			boost::replace_all(line, "\"", "");
			last_default = line.substr(8,line.size()-8);
			if(entries[index].glyph == 0)
				this->entries[index].defaultVal = last_default;
		}

		if(std::regex_search(line, range_re))
		{
			std::string::size_type min_pos = line.find("[");
			std::string::size_type max_pos = line.find("-");
			std::string::size_type end_pos = line.find("]");
			this->entries[index].rangeMin = line.substr(min_pos+1,max_pos-min_pos-1);
			this->entries[index].rangeMax = line.substr(max_pos+1,end_pos-max_pos-1);
		}
	}

	this->valiString = "";
	for(int i=0; i<number_of_validation_entries; i++)
	{
		this->valiString += std::to_string(this->entries[i].indent) + " " + 
			std::to_string(this->entries[i].glyph) + " " + 
			this->entries[i].leftCell + " " +
			this->entries[i].type + " " +
			this->entries[i].defaultVal + "\n";		
	}
	this->valiString.resize(this->valiString.size() - 1);
	//std::cout << std::endl;
	//std::cout << this->valiString << std::endl;
	
	return testValidationMatch();
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
	std::cout << "testValidationMatch" << std::endl;
	std::istringstream lineIterCount(this->valiString);
	//int num_lines_count = 0;
	//for(std::string line; std::getline(lineIterCount, line); )
	//	++num_lines_count;
	//std::cout << "num_lines_count: " << num_lines_count << std::endl;
	std::cout << "number_of_validation_entries: " << number_of_validation_entries << std::endl;
	std::cout << "this->number_of_entries: " << this->number_of_entries << std::endl;	
	if(number_of_validation_entries!=this->number_of_entries)
	{
		this->validationMessage = "Failed matching of specificaiton file with validation file!\n";
		this->validationMessage += "--> #parameters in the validation file: " + std::to_string(number_of_validation_entries) + "\n";
		this->validationMessage += "--> #parameters in the specification file: " + std::to_string(this->number_of_entries) + "\n";
		std::cout << this->validationMessage << std::endl;
		return false; //Different number of parameters
	}

	std::regex timestamp ("\\{([0-9Ee.\\-\\*]+,)+[0-9Ee.\\-\\*]+\\}");
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
					std::cout << this->validationMessage << std::endl;
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
					std::cout << this->validationMessage << std::endl;
					return false; //Name does not match the validation file
				}
			}
		}
		++num_lines_test;
	}
	
	std::cout << this->validationMessage << std::endl;
	return true;
}
