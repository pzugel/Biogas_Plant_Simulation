function LoadTheCheckpoint()
checkpoint = checkpoint or {}
	checkpoint["myData"] = 	checkpoint["myData"] or {}
		checkpoint["myData"]["volProdPrevious"] = 		checkpoint["myData"]["volProdPrevious"] or {}
			checkpoint["myData"]["volProdPrevious"]["Hydrogen"] = 0.0045743206028716
			checkpoint["myData"]["volProdPrevious"]["Carbondioxide"] = 13.311269827462
			checkpoint["myData"]["volProdPrevious"]["Methane"] = 46.876125850562
		checkpoint["myData"]["gasFlowPrevious"] = 		checkpoint["myData"]["gasFlowPrevious"] or {}
			checkpoint["myData"]["gasFlowPrevious"]["Hydrogen"] = 0.0045433665711936
			checkpoint["myData"]["gasFlowPrevious"]["Carbondioxide"] = 13.102805700221
			checkpoint["myData"]["gasFlowPrevious"]["Methane"] = 44.450627939428
	checkpoint["ugargv"] = 	checkpoint["ugargv"] or {}
		checkpoint["ugargv"][1] = "/home/paul/ug4/bin/ugshell"
		checkpoint["ugargv"][2] = "-ex"
		checkpoint["ugargv"][3] = "/home/paul/Schreibtisch/Biogas_plant_setup/VRL/simulation_files/Biogas.lua"
		checkpoint["ugargv"][4] = "-p"
		checkpoint["ugargv"][5] = "/home/paul/Schreibtisch/Simulations/VRL/Full/biogasVRL_STRUCT_1_STAGE/methane/59/methane_checkpoint.lua"
	checkpoint["lastFilename"] = "myCheckpointSimulationEnd.ug4vec"
	checkpoint["lastId"] = "SimulationEnd"
	checkpoint["commandline"] = "/home/paul/ug4/bin/ugshell -ex /home/paul/Schreibtisch/Biogas_plant_setup/VRL/simulation_files/Biogas.lua -p /home/paul/Schreibtisch/Simulations/VRL/Full/biogasVRL_STRUCT_1_STAGE/methane/59/methane_checkpoint.lua "
	checkpoint["ugargc"] = 5
	checkpoint["stdData"] = 	checkpoint["stdData"] or {}
		checkpoint["stdData"]["numCores"] = 1
return checkpoint
end
