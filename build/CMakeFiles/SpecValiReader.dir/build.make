# CMAKE generated file: DO NOT EDIT!
# Generated by "Unix Makefiles" Generator, CMake Version 3.18

# Delete rule output on recipe failure.
.DELETE_ON_ERROR:


#=============================================================================
# Special targets provided by cmake.

# Disable implicit rules so canonical targets will work.
.SUFFIXES:


# Disable VCS-based implicit rules.
% : %,v


# Disable VCS-based implicit rules.
% : RCS/%


# Disable VCS-based implicit rules.
% : RCS/%,v


# Disable VCS-based implicit rules.
% : SCCS/s.%


# Disable VCS-based implicit rules.
% : s.%


.SUFFIXES: .hpux_make_needs_suffix_list


# Command-line flag to silence nested $(MAKE).
$(VERBOSE)MAKESILENT = -s

#Suppress display of executed commands.
$(VERBOSE).SILENT:

# A target that is always out of date.
cmake_force:

.PHONY : cmake_force

#=============================================================================
# Set environment variables for the build.

# The shell in which to execute make rules.
SHELL = /bin/sh

# The CMake executable.
CMAKE_COMMAND = /usr/local/bin/cmake

# The command to remove a file.
RM = /usr/local/bin/cmake -E rm -f

# Escaping for special characters.
EQUALS = =

# The top-level source directory on which CMake was run.
CMAKE_SOURCE_DIR = /home/paul/Schreibtisch/Biogas_plant_setup

# The top-level build directory on which CMake was run.
CMAKE_BINARY_DIR = /home/paul/Schreibtisch/Biogas_plant_setup/build

# Include any dependencies generated for this target.
include CMakeFiles/SpecValiReader.dir/depend.make

# Include the progress variables for this target.
include CMakeFiles/SpecValiReader.dir/progress.make

# Include the compile flags for this target's objects.
include CMakeFiles/SpecValiReader.dir/flags.make

CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.o: CMakeFiles/SpecValiReader.dir/flags.make
CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.o: ../src/biogas_spec_vali_wrapper.cpp
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --progress-dir=/home/paul/Schreibtisch/Biogas_plant_setup/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_1) "Building CXX object CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.o"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -o CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.o -c /home/paul/Schreibtisch/Biogas_plant_setup/src/biogas_spec_vali_wrapper.cpp

CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.i: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Preprocessing CXX source to CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.i"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -E /home/paul/Schreibtisch/Biogas_plant_setup/src/biogas_spec_vali_wrapper.cpp > CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.i

CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.s: cmake_force
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green "Compiling CXX source to assembly CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.s"
	/usr/bin/c++ $(CXX_DEFINES) $(CXX_INCLUDES) $(CXX_FLAGS) -S /home/paul/Schreibtisch/Biogas_plant_setup/src/biogas_spec_vali_wrapper.cpp -o CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.s

# Object files for target SpecValiReader
SpecValiReader_OBJECTS = \
"CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.o"

# External object files for target SpecValiReader
SpecValiReader_EXTERNAL_OBJECTS =

../LabView/lib/libSpecValiReader.so: CMakeFiles/SpecValiReader.dir/src/biogas_spec_vali_wrapper.cpp.o
../LabView/lib/libSpecValiReader.so: CMakeFiles/SpecValiReader.dir/build.make
../LabView/lib/libSpecValiReader.so: CMakeFiles/SpecValiReader.dir/link.txt
	@$(CMAKE_COMMAND) -E cmake_echo_color --switch=$(COLOR) --green --bold --progress-dir=/home/paul/Schreibtisch/Biogas_plant_setup/build/CMakeFiles --progress-num=$(CMAKE_PROGRESS_2) "Linking CXX shared library ../LabView/lib/libSpecValiReader.so"
	$(CMAKE_COMMAND) -E cmake_link_script CMakeFiles/SpecValiReader.dir/link.txt --verbose=$(VERBOSE)

# Rule to build all files generated by this target.
CMakeFiles/SpecValiReader.dir/build: ../LabView/lib/libSpecValiReader.so

.PHONY : CMakeFiles/SpecValiReader.dir/build

CMakeFiles/SpecValiReader.dir/clean:
	$(CMAKE_COMMAND) -P CMakeFiles/SpecValiReader.dir/cmake_clean.cmake
.PHONY : CMakeFiles/SpecValiReader.dir/clean

CMakeFiles/SpecValiReader.dir/depend:
	cd /home/paul/Schreibtisch/Biogas_plant_setup/build && $(CMAKE_COMMAND) -E cmake_depends "Unix Makefiles" /home/paul/Schreibtisch/Biogas_plant_setup /home/paul/Schreibtisch/Biogas_plant_setup /home/paul/Schreibtisch/Biogas_plant_setup/build /home/paul/Schreibtisch/Biogas_plant_setup/build /home/paul/Schreibtisch/Biogas_plant_setup/build/CMakeFiles/SpecValiReader.dir/DependInfo.cmake --color=$(COLOR)
.PHONY : CMakeFiles/SpecValiReader.dir/depend

