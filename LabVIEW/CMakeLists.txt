# Copyright (c) 2020:  G-CSC, Goethe University Frankfurt
# Author: Paul Zügel

# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
# GNU Lesser General Public License for more details.

################################################################################
# BioGasLabViewWrapper
#
# The Biogas LabView wrapper
################################################################################

# Name of your wrapper and sources.
set(wrapperName				BiogasLabViewWrapper)
set(SpecValiReader			SpecValiReader)
set(OutputReader			OutputReader)
set(SimulationControl		SimulationControl)
set(FileFunctions			FileFunctions)
set(CheckpointUpdate		CheckpointUpdate)

################################################################################
# The code below doesn't have to be changed (usually)
################################################################################
cmake_minimum_required(VERSION 2.6)

project(UG_PLUGIN_${wrapperName})

set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/../LabView/lib)

if (WIN32)
    set(Boost_USE_STATIC_LIBS OFF) 
	set(Boost_USE_MULTITHREADED ON)  
	set(Boost_USE_STATIC_RUNTIME OFF) 
	
	if (POLICY CMP0074)
		cmake_policy(SET CMP0074 NEW)
	endif()
	
	find_package(Boost 1.67.0)	
	
	if(Boost_FOUND)
		include_directories(${Boost_INCLUDE_DIRS}) 
	endif()
	
	add_library(${SpecValiReader} SHARED src/biogas_spec_vali_wrapper.cpp)	
	set_target_properties(${SpecValiReader} PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/../LabView/lib)
	
	add_library(${OutputReader} SHARED src/biogas_output_reader_wrapper.cpp)
	set_target_properties(${OutputReader} PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/../LabView/lib)
	
	add_library(${SimulationControl} SHARED src/biogas_simulation_control_wrapper.cpp)
	set_target_properties(${SimulationControl} PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/../LabView/lib)
	
	add_library(${FileFunctions} SHARED src/biogas_file_functions_wrapper.cpp)
	set_target_properties(${FileFunctions} PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/../LabView/lib)
	
	add_library(${CheckpointUpdate} SHARED src/biogas_checkpoint_wrapper.cpp)
	set_target_properties(${CheckpointUpdate} PROPERTIES RUNTIME_OUTPUT_DIRECTORY ${PROJECT_BINARY_DIR}/../LabView/lib)
endif (WIN32)

if (UNIX)
    add_library(${SpecValiReader} SHARED src/biogas_spec_vali_wrapper.cpp)
	add_library(${OutputReader} SHARED src/biogas_output_reader_wrapper.cpp)
	add_library(${SimulationControl} SHARED src/biogas_simulation_control_wrapper.cpp)
	add_library(${FileFunctions} SHARED src/biogas_file_functions_wrapper.cpp)
	add_library(${CheckpointUpdate} SHARED src/biogas_checkpoint_wrapper.cpp)
endif (UNIX)

