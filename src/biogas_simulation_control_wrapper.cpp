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
 
#include "simulation_control/simulation_control.h"

static BiogasSimulationControl* simulationControl;

extern "C"{
	
void simulationControlInit(){
	simulationControl = new BiogasSimulationControl();
}

const char* get_sim_log()
{
	return simulationControl->sim_log.c_str();
}

void set_sim_log(const char* log)
{
	simulationControl->sim_log = (std::string) log;
}

void add_to_sim_log(const char* log)
{
	simulationControl->sim_log += ((std::string) log);
}

const char* get_plant_structure()
{
	return simulationControl->plant_structure.c_str();
}

void set_plant_structure(const char* structure)
{
	simulationControl->plant_structure = (std::string) structure;
}

const char* get_active_element()
{
	return simulationControl->active_element.c_str();
}

void set_active_element(const char* elem)
{
	simulationControl->active_element = (std::string) elem;
}

int get_iteration()
{
	return simulationControl->iteration;
}

void set_iteration(int iter)
{
	simulationControl->iteration = iter;
}

void set_interation_log(int iter)
{
	simulationControl->iteration = iter;
	simulationControl->sim_log += "Iteration: " + std::to_string(iter) + "\n";
}

void set_simulation_file(const char* sim_file)
{
	simulationControl->simulation_file = (std::string) sim_file;
}

const char* get_simulation_file()
{
	return simulationControl->simulation_file.c_str();
}

void set_validation_file(const char* val_file)
{
	simulationControl->validation_file = (std::string) val_file;
}

const char* get_validation_file()
{
	return simulationControl->validation_file.c_str();
}

void set_specification_file(const char* spec_file)
{
	simulationControl->specification_file = (std::string) spec_file;
}

const char* get_specification_file()
{
	return simulationControl->specification_file.c_str();
}

void set_working_directory(const char* dir)
{
	simulationControl->working_directory = (std::string) dir;
}

const char* get_working_directory()
{
	return simulationControl->working_directory.c_str();
}

void set_sim_starttime(double time)
{
	simulationControl->sim_starttime = time;
}

double get_sim_starttime()
{
	return simulationControl->sim_starttime;
}

void set_sim_endtime(double time)
{
	simulationControl->sim_endtime = time;
}

double get_sim_endtime()
{
	return simulationControl->sim_endtime;
}

void set_sim_currenttime(double time)
{
	simulationControl->sim_currenttime = time;
}

double get_sim_currenttime()
{
	return simulationControl->sim_currenttime;
}

} //end extern "C"
