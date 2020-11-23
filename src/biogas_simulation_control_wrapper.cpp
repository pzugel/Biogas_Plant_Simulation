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
#include <fstream>
#include <regex>
#include <boost/algorithm/string.hpp>
 
static std::string active_element;
static std::string plant_structure;
static std::string sim_log;
static int iteration;

static std::string simulation_file;
static std::string validation_file;
static std::string specification_file;
static std::string working_directory;

static double sim_starttime;
static double sim_endtime;
static double sim_currenttime;

/**
 * This library only consists of getter/setter parameters to control
 * the simulation flow. It saves information about the current state
 * of the simulation, such as the iteration number, and is therefore
 * constantly updating. 
 */

extern "C"{

/**
 * Getter/setter for the name of the plant structure, e.g. "3_STAGE.vi"
 * 
 */
void set_plant_structure(const char* structure)
{
	plant_structure = (std::string) structure;
}

const char* get_plant_structure()
{
	return plant_structure.c_str();
}

/**
 * Getter/setter for the active element, e.g. "Hydrolyse Reactor"
 */
void set_active_element(const char* elem)
{
	active_element = (std::string) elem;
}

const char* get_active_element()
{
	return active_element.c_str();
}

/**
 * Getter/setter for the iteration counter
 * 
 */
 void set_iteration(int iter)
{
	iteration = iter;
}

int get_iteration()
{
	return iteration;
}

/**
 * Getter/setter for the path to the simulation file
 */
void set_simulation_file(const char* sim_file)
{
	simulation_file = (std::string) sim_file;
}

const char* get_simulation_file()
{
	return simulation_file.c_str();
}

/**
 * Getter/setter for the path to the validation file
 */
void set_validation_file(const char* val_file)
{
	validation_file = (std::string) val_file;
}

const char* get_validation_file()
{
	return validation_file.c_str();
}

/**
 * Getter/setter for the path to the specification file
 */
void set_specification_file(const char* spec_file)
{
	specification_file = (std::string) spec_file;
}

const char* get_specification_file()
{
	return specification_file.c_str();
}

/**
 * Getter/setter for the working directory
 */
void set_working_directory(const char* dir)
{
	working_directory = (std::string) dir;
}

const char* get_working_directory()
{
	return working_directory.c_str();
}

/**
 * Getter/setter for the starttime of the simulation
 */
void set_sim_starttime(double time)
{
	sim_starttime = time;
}

double get_sim_starttime()
{
	return sim_starttime;
}

/**
 * Getter/setter for the endtime of the simulation
 */
void set_sim_endtime(double time)
{
	sim_endtime = time;
}

double get_sim_endtime()
{
	return sim_endtime;
}

/**
 * Getter/setter for the current timestep of the simulation
 */
void set_sim_currenttime(double time)
{
	sim_currenttime = time;
}

double get_sim_currenttime()
{
	return sim_currenttime;
}

/**
 * Getter/setter for the the simulation log
 */
void set_sim_log(const char* log)
{
	sim_log = (std::string) log;
}

const char* get_sim_log()
{
	return sim_log.c_str();
}

/**
 * Adds string to the simulation log
 * 
 * @param log: New line in simulation log
 */
void add_to_sim_log(const char* log)
{
	sim_log += ((std::string) log);
}

/**
 * Sets the iteration count and adds a new line to the simulation log
 * 
 * @param iter: The iteration counter
 */
void set_interation_log(int iter)
{
	iteration = iter;
	sim_log += "Iteration: " + std::to_string(iter) + "\n";
}

} //end extern "C"
