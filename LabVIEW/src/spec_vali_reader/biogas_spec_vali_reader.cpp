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

#include "biogas_vali_data_generate.cpp"
#include "biogas_spec_data_generate.cpp"
//#include "biogas_spec_writer.cpp" //ONLY FOR DEBUGGING (REMOVE)

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

/**
 * Only for debugging
 */
 /*
int main()
{
	BiogasSpecValiReader* testReader = new BiogasSpecValiReader();
	std::string specfile = "/home/paul/Schreibtisch/smalltest/3_STAGE_PL_TEST/tmp/hydrolysis_1/0/hydrolysis_checkpoint.lua";
	testReader->readInput(specfile);
	testReader->transformSpecInput();
	testReader->generateSpecs();

	std::string specs = "";
	for(int i=0; i<testReader->entries.size(); i++)
		specs += testReader->entries[i].specVal + "\n";
	std::cout << "SPECS -->" << std::endl;
	std::cout << specs << std::endl;
	std::cout << "--> SPECS END" << std::endl;
	testReader->writeOutputSpecs(specs);
	std::cout << testReader->outputSpecs << std::endl;
	return 0;
}
*/
