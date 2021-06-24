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

static std::string feeding_timetable_string;
static std::string feeding_timetable_csv;

/**
 * Read the feeding timetable from a hydrolysis specification
 * 
 * @param hydrolysis_specfile: Path pointing the the specification
 */
void read_feeding_timetable(const char* hydrolysis_specfile)
{
	bool feeding = false;
	bool feeding_timetable = false;
	bool feeding_timestamp = false;
	feeding_timetable_string = "";
	
	std::regex timestamp ("(\\s)*\\{(\\s)*[0-9eE\\.\\-\\*]+(\\s)*,(\\s)*[0-9eE\\.\\-\\*]+(\\s)*\\},(\\s)*");
	std::ifstream stream(hydrolysis_specfile);
	if(stream.good())
	{
		for(std::string line; getline(stream,line);)
		{
			if(line.find("feeding") != std::string::npos)
				feeding = true;
			if(line.find("timetable") != std::string::npos
				&& feeding == true)
				feeding_timetable = true; 
			
			if(feeding_timetable)
			{
				if(std::regex_search(line, timestamp))
				{
					feeding_timetable_string += line + "\n";
					feeding_timestamp = true;
				} 
				else if(!std::regex_search(line, timestamp) && feeding_timestamp)
				{
					feeding = false;
					feeding_timetable = false;
					feeding_timestamp = false;
				}
			}
		}
	}
}

/**
 * Returns feeding timetable formatted into a CSV styls string
 * to be loaded into LabView
 * 
 * @param hydrolysis_specfile: Path pointing the the specification
 */
void get_feeding_timetable(const char* hydrolysis_specfile)
{
	feeding_timetable_csv = "";
	read_feeding_timetable(hydrolysis_specfile);
	std::stringstream stream(feeding_timetable_string);
	
	for(std::string line; getline(stream,line);)
	{
		line.erase(std::remove_if(line.begin(), line.end(), ::isspace), line.end());
		std::size_t time = line.find('{');
		std::size_t amount = line.find(',');
		std::size_t end = line.find('}');
		feeding_timetable_csv += line.substr(time+1, amount-time-1) 
			+ " " 
			+ line.substr(amount+1, end-amount-1)
			+ "\n";
	}
}

/**
 * Updates the feeding timetable in a specification file 
 * for a hydrolysis reacote
 * 
 * @param hydrolysis_specfile: Path pointing the the specification
 * @param time: Timestamp time
 * @param amount: Timestamp amount
 * @param number_timestamps: size of arrays time and amount
 */
void update_feeding_timetable(
	const char* hydrolysis_specfile,
	const char* time,
	const char* amount,
	int number_timestamps)
{	
	std::ifstream hydrolysis_specfile_stream(hydrolysis_specfile);
	std::string mBuf((std::istreambuf_iterator<char>(hydrolysis_specfile_stream)),
                 std::istreambuf_iterator<char>());
	std::string specfile_string = mBuf;
	
	read_feeding_timetable(hydrolysis_specfile);
	
	std::string new_timetable = "";
	std::string tabs = "";
	std::string first_line;
	std::stringstream tab_stream(feeding_timetable_string);
	getline(tab_stream,first_line);
	int num_tabs = std::count(first_line.begin(), first_line.end(), '\t');
	
	std::stringstream time_stream(time);
	std::stringstream amount_stream(amount);
	std::vector<std::string> time_vec = {};
	std::vector<std::string> amount_vec = {};
	
	for(std::string line; getline(time_stream,line);)
		time_vec.push_back(line);
	for(std::string line; getline(amount_stream,line);)
		amount_vec.push_back(line);
	for(int i=0; i<num_tabs; i++)
		tabs += "\t";
	for(int i=0; i<number_timestamps; i++)
		new_timetable += tabs + "{" + time_vec.at(i) + ", " + amount_vec.at(i) + "},\n";
	
	std::size_t timetable_pos = specfile_string.find(feeding_timetable_string);	
	specfile_string.replace(timetable_pos, feeding_timetable_string.size(), new_timetable);
	
	std::ofstream new_spec;
	new_spec.open (hydrolysis_specfile);
	new_spec << specfile_string;
	new_spec.close();
}

