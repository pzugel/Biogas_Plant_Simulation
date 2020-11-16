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

static std::string data_string;

extern "C"{

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
		if(!(std::regex_search(line, header)))
			data_string += line + "\n";
	}
	return data_string.c_str();
}

/**
 * Reads all filenames from an outputFiles.lua  
 * 
 * @param path: Path pointing to outputFiles.lua
 * @return Filenames as string
 */
const char* read_filenames(const char* path)
{
	data_string = "";
	std::regex filename ("^filename=");
	std::ifstream LUAOutput(path);
	if(LUAOutput.good())
	{
		for(std::string line; getline(LUAOutput,line);)
		{
			line.erase(remove_if(line.begin(), line.end(), isspace), line.end());
			if(std::regex_search(line, filename))
			{
				boost::replace_all(line, ",", "");
				boost::replace_all(line, "\"", "");
				boost::replace_all(line, "filename=", "");
				data_string += line + "\n";
			}
		}
	}
	return data_string.c_str();
}

} //end extern "C"
