# Biogas_Plant_Simulation

This projects contains two applications using VRL.Studio and LabVIEW. Both applications feature a control panel where the user can set up a biogas plant using hydrolysis and methane reactors and simulate the biogas production. The computations rely on [UG4](https://github.com/UG4) and the associated biogas plugins.

# Contents
This document should give a broad overview on how to setup both applications on your local system. For an introduction into the usage we refer to the provided handbook.

* [Prerequisites](#prerequisites)
* [Using VRL.Studio](#using-vrlstudio)
* [Using LabVIEW](#using-labview)

# Prerequisites
For both applications you need a running **UG4** installation on your local machine. Please use the **ughub** paket manager and follow the provided installation steps: https://github.com/UG4/ughub

For this project it is important to set up UG4 in your home directory as proposed in the installation manual:
```
$HOME/ug4 (Linux) 
%HOMEPATH%\ug4 (Windows)
```
We refer to this path as `$UGPATH`.

Make sure to also install the following UG plugins:
```
cd $UGPATH
ughub install Biogas
ughub install biogas_app
```

Finally clone this repository to some place on your machine:
```
git clone https://github.com/pzugel/Biogas_Plant_Simulation
```
# Using VRL.Studio

## Installation

You can download [VRL.Studio](https://vrl-studio.mihosoft.eu/) for free at the official website. To use the biogas application you simply need to install a plugin as follows. Open up VRL and click

> File -> Select Plugins

Now select the following plugin

> Biogas_plant_setup/VRL/plugin/VRLBiogas_Plugin.jar

You might need to previously install 

> Biogas_plant_setup/VRL/plugin/VRL-JFreeChart.jar

depending on your downloaded version of VRL.

## Run

You should now be able to run the VRL Poject files

```
Biogas_plant_setup.vrlp
Biogas_user_plant_setup.vrlp
```

# Using LabVIEW

## Installation

First download [LabVIEW](https://www.ni.com/de-de/support/downloads/software-products/download.labview.html) from the *Nation Instruments* website. Make sure to use at least Version 2019. Now you need to compile some libraries on your local machine using [CMake](https://cmake.org/download/).

### Linux
```
cd LabVIEW
mkdir build
cd build
cmake ..
cmake --build .
```

### Windows

For Windows systems you need [MinGW](http://mingw-w64.org/doku.php) as well as a [Boost](https://www.boost.org/users/history/version_1_67_0.html) to compile the library files. For the boost directory simply use `C:\Program Files\boost_1_67_0`. If the following lines fail please check the `CMakeLists.txt` file.

```
cd LabVIEW
mkdir build
cd build 
cmake .. -G "MinGW Makefiles"
cmake --build .
```

This should create some **.dll** or **.so** files in your `LabView\lib` directory.

## Run

To start the application simply open the LabView project file

> LabView/Biogas_Plant.lvproj

From there select the *main.vi* and run it. Alternatively, if you are working on Windows, you can use the [LabVIEW Runtime Engine](https://www.ni.com/de-de/support/downloads/software-products/download.labview-runtime.html#411217) to execute the *Biogas_Application.exe*. 
