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

static std::string methane_specfile_string;
static std::string outflow_infile_string;
static std::vector<std::string> spec_inflowData_vec; //Holds params from data={"Acetic", "..."}
static std::string timetable_string; //Timetable to be replaced
static std::vector<std::string> outflow_input_header; //Holds header from outflow.txt
static std::vector<std::vector<std::string>> outflow_input_values; //Holds values from outflow.txt
static std::vector<std::vector<std::string>> output_timetable; //Holds new inflow for the methane spec
static std::string timetable_replacement; //Holds new inflow (as string) for the methane spec

/**
 * Reads the methane specification file and stores the inflow "data"
 * parameter into "spec_inflowData_vec"
 * 
 * e.g. data={"Acetic", "..."}
 * 
 * The whole inflow entry will be stored in the "timetable_string"
 * so we can later replace it.
 */
void parse_spec_file()
{
	std::string inflow_string;
	spec_inflowData_vec = {};
	
	std::regex inflow ("inflow(\\s)*=(\\s)*\\{(\\s)*data(\\s)*=(\\s)*\\{[a-zA-Z,\"\\s]*\\},(\\s)*timetable(\\s)*=(\\s)*\\{(\\s)*(\\{.*\\},?(\\s)*)*\\},(\\s)*\\},?");
	std::regex timetable ("timetable(\\s)*=(\\s)*\\{(\\s)*(\\{.*\\},?(\\s)*)*\\},");
	std::regex data ("\"[a-zA-Z0-9\\s]+\"");
	std::smatch match_inflow;
	if(std::regex_search(methane_specfile_string, match_inflow, inflow))
	{
		inflow_string = match_inflow[0];
		
		std::sregex_iterator iter(inflow_string.begin(), inflow_string.end(), data);
		std::sregex_iterator end;
		while(iter != end)
		{
			for(unsigned i = 0; i < iter->size(); ++i)
				spec_inflowData_vec.push_back((*iter)[i]);
			++iter;
		}
		
		std::smatch match_timetable;
		if(std::regex_search(inflow_string, match_timetable, timetable))
			timetable_string = match_timetable[0];
	}
}

/**
 * Reads the header of the outflow.txt file to determine the columns
 * for the different outlow components. 
 * 
 * e.g. "Time", "All Liquid", "AA" ...
 * They are stored in "outflow_input_header"   
 */
void read_outflow_header()
{
	std::regex col("\\w[\\s\\w]*\\[[a-zA-Z0-9/-]+\\]");
	std::sregex_iterator iter(header_string.begin(), header_string.end(), col);
	std::sregex_iterator end;
	outflow_input_header = {};
	while(iter != end)
	{
		for(unsigned i = 0; i < iter->size(); ++i)
			outflow_input_header.push_back((*iter)[i]);
		++iter;
	}	
}

/**
 * Reads the values of the outflow.txt and writes them to 
 * the "outflow_input_values" vector  
 */
void read_outflow_values()
{
	outflow_input_values = {};
	std::stringstream outlow_input_data_stream(data_string);
	for(std::string line; getline(outlow_input_data_stream, line);)
	{
		std::stringstream iss(line);
		std::string item;
		std::vector<std::string> a;
		while (std::getline(iss, item, '\t')) {
			a.push_back(item);
		}
		outflow_input_values.push_back(a);
	}
}

/**
 * Writes a new timetable by combining the values from the
 * outflow.txt with the according parameters set in "data={...}".
 * The new timetable will be stored in "output_timetable"
 */
void write_new_timetable()
{
	output_timetable = {};
	/*
	 * Adding parameters "Time" and "All Liquid"
	 */
	for(int j=0; j<outflow_input_header.size(); j++){
		std::string header_val = outflow_input_header.at(j);
		std::vector<std::string> col_vector;
		if(header_val.find("Time") != string::npos
			|| header_val.find("All Liquid") != string::npos)
		{
			int column = j;
			for(int k=0; k<outflow_input_values.size(); k++)
			{
				col_vector.push_back(outflow_input_values.at(k).at(column));
			}
			
			output_timetable.push_back(col_vector);	
		}
		
	}
	 
	/*
	 * Adding parameters defined in data={"...", "..."}
	 */
	for(int i=0; i<spec_inflowData_vec.size(); i++)
	{
		int column = -1;
		std::vector<std::string> col_vector;
		std::string data_val = spec_inflowData_vec.at(i);
		boost::replace_all(data_val, "\"", "");
		/*
		* Match parameter from "data" with the currect column 
		* from the "outflow.txt" files header
		*/
		for(int j=0; j<outflow_input_header.size(); j++){
			std::string header_val = outflow_input_header.at(j);
			if(header_val.find(data_val) != string::npos)
				column = j;
		}
		
		if(column>-1)
		{
			for(int k=0; k<outflow_input_values.size(); k++)
			{
				col_vector.push_back(outflow_input_values.at(k).at(column));
			}	
			output_timetable.push_back(col_vector);		
		}	
	}
}

/**
 * Converts the new timetable stored in output_timetable to a string.
 * This new string will replace the old timetable in the methane
 * specification file.
 */
void write_new_timetable_string()
{
	std::smatch match_inflow_tabs;
	std::regex inflow_tabs ("inflow");
	std::istringstream iss(methane_specfile_string);
	size_t num_tabs;
	for (std::string line; std::getline(iss, line); )
	{
		if(std::regex_search(line, match_inflow_tabs, inflow_tabs))
			 num_tabs = std::count(line.begin(), line.end(), '\t');
	}
	num_tabs += 2;
	std::string tabs = "";
	for(int i=0; i<num_tabs; i++)
		tabs += "\t";
		
	timetable_replacement = "timetable={\n";
	for(int i=0; i<output_timetable.at(0).size(); i++){
		timetable_replacement += tabs + "{";
		for(int j=0; j<output_timetable.size(); j++)
		{
			timetable_replacement += output_timetable.at(j).at(i);
			if(j!=output_timetable.size()-1)
				timetable_replacement += ", ";
		}
		timetable_replacement += "},\n";
	}
	tabs.erase(0,1);
	timetable_replacement += tabs + "},\n";
}

/**
 * Main function to be called
 * 
 * @param outflow_infile: Path pointing the outflow.txt
 * @param methane_specfile: Path pointing the methane spec file 
 */
void write_inflow(
	const char* outflow_infile,
	const char* methane_specfile)
{
	std::ifstream methane_specfile_stream(methane_specfile);
	std::string mBuf((std::istreambuf_iterator<char>(methane_specfile_stream)),
                 std::istreambuf_iterator<char>());
	methane_specfile_string = mBuf;
   
   	std::ifstream outflow_infile_stream(outflow_infile);
	std::string oBuf((std::istreambuf_iterator<char>(outflow_infile_stream)),
                 std::istreambuf_iterator<char>());
	outflow_infile_string = oBuf;
	
	get_header(outflow_infile);
	remove_header(outflow_infile);
	
	parse_spec_file();
	read_outflow_header();
	read_outflow_values();
	write_new_timetable();
	write_new_timetable_string();
	
	//Replacement in spec file	
	std::size_t timetable_pos = methane_specfile_string.find(timetable_string);	
	methane_specfile_string.replace(timetable_pos, timetable_string.size(), timetable_replacement);
	std::cout << methane_specfile_string << std::endl;
	
	std::ofstream new_spec;
	new_spec.open (methane_specfile);
	new_spec << methane_specfile_string;
	new_spec.close();
}
