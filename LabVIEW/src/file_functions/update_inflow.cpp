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

static std::string specfile_string;
static std::vector<std::string> spec_inflowData_vec; //Holds params from data={"Acetic", "..."}
static std::string inflow_timetable_string; //Timetable to be replaced
static std::vector<std::string> outflow_input_header; //Holds header from outflow.txt
static std::vector<std::vector<std::string>> outflow_input_values; //Holds values from outflow.txt
static std::vector<std::vector<std::string>> output_timetable; //Holds new inflow for the methane spec
static std::string timetable_replacement; //Holds new inflow (as string) for the methane spec
static int sim_starttime; //sim_starttime as defined in a specification

/**
 * Reads the methane specification file and stores the inflow "data"
 * parameter into "spec_inflowData_vec"
 * 
 * e.g. data={"Acetic", "..."}
 * 
 * The whole inflow entry will be stored in the "inflow_timetable_string"
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
	if(std::regex_search(specfile_string, match_inflow, inflow))
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
			inflow_timetable_string = match_timetable[0];
	}
	
	std::regex simstarttimePattern ("sim_starttime(\\s)*=(\\s)*[0-9.]+");
	std::smatch match_simstarttime;
	if(std::regex_search(specfile_string, match_simstarttime, simstarttimePattern))
	{
		std::string simstarttimeString = match_simstarttime[0];
		size_t startInd = simstarttimeString.find('=');
		if(startInd != std::string::npos){
			simstarttimeString = simstarttimeString.substr(startInd+1);
			sim_starttime = (int) dot_conversion(simstarttimeString);
		} else {
			sim_starttime = 0.0;
		}
	}
	else {
		sim_starttime = 0.0;
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
 * 
 * The inflow can also be fractional as defined by a value in [0,1].
 * This is used to split up the methane outflow to feed back into
 * the hydrolysis reactors.
 * 1 --> No splitting
 * 0 --> Empty inflow
 * 
 * @param fraction: Value to split the inflow
 */
void write_new_timetable(double fraction, bool isMethane)
{
	output_timetable = {};
	/*
	 * Adding parameters "Time" and "All Liquid"
	 */
	for(int j=0; j<outflow_input_header.size(); j++){
		std::string header_val = outflow_input_header.at(j);
		std::vector<std::string> col_vector;
		if(header_val.find("Time") != std::string::npos
			|| ((header_val.find("All") != std::string::npos) && (header_val.find("Liquid") != std::string::npos)))
		{
			int column = j;
			for(int k=0; k<outflow_input_values.size(); k++)
			{
				/*
				 * Here we need to distinguish methane and hydrolysis reactors
				 * 
				 * e.g. If we compute the timestep 2.0 -> 3.0 in any reactor we need to set the 
				 * inflow timestep to "3.0". Now if we take the methane outflow of a computed step from
				 * 2.0 -> 3.0 we need to add one the timestep when feeding it back to the hydrolysis reactor
				 * 
				 * We also need to add a time offset dtStart as defined in the specification
				 */
				 if(header_val.find("Time") != std::string::npos){
					 if(isMethane) {
						double previousTimestep = dot_conversion(outflow_input_values.at(k).at(column));
						col_vector.push_back(conv_to_string(previousTimestep));
					} else {
						double timeOffset = dot_conversion(outflow_input_values.at(k).at(column))+1;
						col_vector.push_back(conv_to_string(timeOffset));
					}					
				 }
				 else {
					//Fractional "all liquid"
					double allLiquidFraction = dot_conversion(outflow_input_values.at(k).at(column))*fraction;
					col_vector.push_back(conv_to_string(allLiquidFraction));
				 }				
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
			if(header_val.find(data_val) != std::string::npos)
				column = j;
		}
		
		if(column>-1)
		{
			for(int k=0; k<outflow_input_values.size(); k++)
			{
				double value = dot_conversion(outflow_input_values.at(k).at(column));
				if(k == outflow_input_values.size() - 1) //only split at last value
					value = value*fraction;
				col_vector.push_back(conv_to_string(value));
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
	std::istringstream iss(specfile_string);
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
	
	std::cout << "write_new_timetable_string" << std::endl;
	
	
	int numLines = output_timetable.at(0).size();
	int numEntries = output_timetable.size();
	std::cout << "numLines: " << numLines << std::endl;
	std::cout << "sim_starttime: " << sim_starttime << std::endl;
	std::cout << "numEntries: " << numEntries << std::endl;
	
	timetable_replacement = "timetable={\n";
	timetable_replacement += tabs + "{";
	for(int i=0; i<numEntries; i++){
		timetable_replacement += output_timetable.at(i).at(numLines-1);	
		if(i!=numEntries-1)
			timetable_replacement += ", ";
	}
	timetable_replacement += "},\n";
	timetable_replacement += tabs.substr(1) + "},";
	std::cout << "timetable_replacement: " << timetable_replacement << std::endl;
}

/**
 * Main function to be called when updating the methane inflow
 * 
 * @param outflow_infile: Path pointing the outflow.txt (storage_hydrolysis)
 * @param methane_specfile: Path pointing the methane spec file 
 */
void write_methane_inflow(
	const char* outflow_infile,
	const char* methane_specfile)
{
	std::ifstream methane_specfile_stream(methane_specfile);
	std::string mBuf((std::istreambuf_iterator<char>(methane_specfile_stream)),
                 std::istreambuf_iterator<char>());
	specfile_string = mBuf;
	
	get_header(outflow_infile);
	remove_header(outflow_infile);
	
	parse_spec_file();
	read_outflow_header();
	read_outflow_values();
	write_new_timetable(1.0, true);
	write_new_timetable_string();
	
	//Replacement in spec file	
	std::size_t timetable_pos = specfile_string.find(inflow_timetable_string);	
	specfile_string.replace(timetable_pos, inflow_timetable_string.size(), timetable_replacement);
	std::cout << specfile_string << std::endl;
	
	std::ofstream new_spec;
	new_spec.open (methane_specfile);
	new_spec << specfile_string;
	new_spec.close();
}

/**
 * Main function to be called when updating the hydrolysis inflow
 * in the hydrolysis setup
 * 
 * @param outflow_infile: Path pointing the outflow.txt (methane)
 * @param hydrolysis_specfiles: String with the specfile direction
 * @param fractions: fractional value to split the inflow
 */
int write_hydrolysis_inflow(
	const char* outflow_infile,
	const char* hydrolysis_specfile,
	double fraction)
{	
	get_header(outflow_infile);
	remove_header(outflow_infile);
	read_outflow_header();
	read_outflow_values();	
	
	for(int i=0; i<outflow_input_header.size(); i++)
		std::cout << outflow_input_header.at(i) << ' ';
	std::cout << std::endl;
	for(int i=0; i<outflow_input_values.size(); i++)
	{
		for(int j=0; j<outflow_input_values.at(i).size(); j++)
			std::cout << outflow_input_values.at(i).at(j) << ' ';
		std::cout << std::endl; 
	}
	
	std::string spec_file = (std::string) hydrolysis_specfile;	
	std::ifstream hydrolysis_specfile_stream(spec_file);
	std::string mBuf((std::istreambuf_iterator<char>(hydrolysis_specfile_stream)),
				 std::istreambuf_iterator<char>());
	specfile_string = mBuf;
	
	parse_spec_file();
	write_new_timetable(fraction, false);
	write_new_timetable_string();
	
	std::size_t timetable_pos = specfile_string.find(inflow_timetable_string);	
	specfile_string.replace(timetable_pos, inflow_timetable_string.size(), timetable_replacement);
	
	std::ofstream new_spec;
	new_spec.open (spec_file);
	new_spec << specfile_string;
	new_spec.close();

	return 0;
}

/**
 * Change the inflow time to sim_starttime
 * @param hydrolysis_specfile
 */
void write_new_initial_timetable_string()
{
	std::string inflowString = inflow_timetable_string;
	std::regex inflowDataPattern ("(\\s)*\\{[0-9.,(\\s)eE-]+\\}(,)?");
	std::smatch match_inflowData;
	if(std::regex_search(inflowString, match_inflowData, inflowDataPattern)){
		std::string found = match_inflowData[0];
		std::size_t indexStart = found.find("{");
		std::size_t indexEnd = found.find(",");

		if(indexStart > 0 && indexEnd > 0) {
			std::string newStart = found.substr(0, indexStart+1) 
				+ conv_to_string(sim_starttime+1) 
				+ found.substr(indexEnd);
			boost::replace_all(inflowString, found, newStart);
		}
		timetable_replacement = inflowString;
	}
}

/**
 * Function called only once at the beginning of the simulation to set the initial
 * inflow according to the sim_starttime
 * @param hydrolysis_specfile 
 */
void write_inital_hydrolysis_inflow(
		const char* hydrolysis_specfile)
{
	std::string spec_file = (std::string) hydrolysis_specfile;	
	std::ifstream hydrolysis_specfile_stream(spec_file);
	std::string mBuf((std::istreambuf_iterator<char>(hydrolysis_specfile_stream)),
				 std::istreambuf_iterator<char>());
	specfile_string = mBuf;	
	
	parse_spec_file();
	write_new_initial_timetable_string();
	boost::replace_all(specfile_string, inflow_timetable_string, timetable_replacement);

	//Write to file
	std::ofstream new_spec;
	new_spec.open (hydrolysis_specfile);
	new_spec << specfile_string;
	new_spec.close();
}
