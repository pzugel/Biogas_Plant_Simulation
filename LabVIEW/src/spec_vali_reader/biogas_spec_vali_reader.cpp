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
#include <fstream>

#include "biogas_vali_data_generate.cpp"
#include "biogas_spec_data_generate.cpp"

#include <iostream>

/**
 * Initialize validation input
 *
 * Main method for validation files. Loads the
 * file and calls all methods to read in the data.
 *
 * @return Bool if file could be read and matched to the specification
 */
bool BiogasSpecValiReader::
init_Vali(const char* filepath_vali)
{
	std::cout << "init_Vali()" << std::endl;
	if(this->readInput((std::string) filepath_vali))
	{
		this->transformValiInput();
		return this->generateValues();
	}
	
	return false;
}

/**
 * Initialize specification input
 *
 * Main method for specification files. Loads the
 * file and calls all methods to read in the data.
 *
 * @return Bool if file could be read
 */
bool BiogasSpecValiReader::
init_Spec(const char* filepath_spec)
{
	if(this->readInput((std::string) filepath_spec))
	{
		this->transformSpecInput();
		return this->generateSpecs();
	}

	return false;
}

/**
 * Read the validation/specification file
 *
 * Load the file and remove all comments.
 *
 * @return Bool if file could be read
 */
bool BiogasSpecValiReader::
readInput(std::string filepath)
{
	std::cout << "readInput()" << std::endl;
	
	this->input = "";
	std::ifstream LUATable(filepath);
	if(!LUATable.good())
	{
		return false;
	}

	for(std::string line; getline(LUATable,line);)
	{
		line.erase(remove_if(line.begin(), line.end(), isspace), line.end());
		std::string prefix("--");
		if (line.compare(0, prefix.size(), prefix))
		{
			if(!line.empty())
			{
				if (line.find("--") != std::string::npos)
				{
					this->input += line.substr(0, line.find("--"));
				}
				else
				{
					this->input += line;
				}		
			}
		}	
	}
	return true;
}
