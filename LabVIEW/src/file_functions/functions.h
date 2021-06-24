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

/**
 * Implements small help functions
 */

#pragma once 

static std::string data_string;
static std::string header_string;
static std::string ph_string;
static std::vector<std::vector<std::vector<std::string>>> values;

extern "C"{
	const char* remove_header(const char* path);
	const char* get_header(const char* path);
}

/**
 * Helper function to correcly convert doube to string 
 * (keeping the scientific notation)
 * 
 * @param d: Double value
 * @return double in string notation
 */
std::string conv_to_string(double d){
	std::ostringstream out;
    out << std::setprecision(16) << d;
    return out.str();
}

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
		valStream >> std::setprecision(16) >> out;
	return out;
}

/**
 * Helper function to correcly read out doubles in dot representation
 * 
 * @param s: Double as string
 * @return long double
 */
long double dot_conversion_regex(std::string s){
	std::string d = s;
	d = std::regex_replace(d, (std::regex) ",", ".");
	return dot_conversion(d);
}

/**
 *  Removes all header lines from a .txt file as string
 * 
 * @param path: *.txt file as string
 * @return The plain text without header lines
 */
const char* remove_header_from_string(const char* file_as_string)
{
	data_string = "";
	header_string = "";
	std::regex header ("(^#)|(^[a-zA-Z])");
	std::stringstream STRInput(file_as_string);
	for(std::string line; getline(STRInput,line);)
	{
		if(!(std::regex_search(line, header)))
			data_string += line + "\n";
		else
			header_string += line + "\n";
	}
	return data_string.c_str();
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
