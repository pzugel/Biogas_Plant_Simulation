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

class BiogasSimulationControl { 
	public:
		std::string active_element;
		std::string plant_structure;
		std::string sim_log;
		int iteration;

		std::string simulation_file;
		std::string validation_file;
		std::string specification_file;
		std::string working_directory;

		double sim_starttime;
		double sim_endtime;
		double sim_currenttime;

		std::string filenames_string;
		std::string csv_data_string;

	public:
		BiogasSimulationControl(){};
};

