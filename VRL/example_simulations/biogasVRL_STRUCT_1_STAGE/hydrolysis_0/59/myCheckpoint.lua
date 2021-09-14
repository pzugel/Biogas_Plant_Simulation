function LoadTheCheckpoint()
checkpoint = checkpoint or {}
	checkpoint["myData"] = 	checkpoint["myData"] or {}
		checkpoint["myData"]["volProdPrevious"] = 		checkpoint["myData"]["volProdPrevious"] or {}
			checkpoint["myData"]["volProdPrevious"]["Hydrogen"] = 0.33477813737227
			checkpoint["myData"]["volProdPrevious"]["Carbondioxide"] = 119.78854233299
			checkpoint["myData"]["volProdPrevious"]["Methane"] = 293.64797817426
		checkpoint["myData"]["gasFlowPrevious"] = 		checkpoint["myData"]["gasFlowPrevious"] or {}
			checkpoint["myData"]["gasFlowPrevious"]["Hydrogen"] = 0.3340739645358
			checkpoint["myData"]["gasFlowPrevious"]["Carbondioxide"] = 118.28899271121
			checkpoint["myData"]["gasFlowPrevious"]["Methane"] = 288.02416288164
	checkpoint["ugargv"] = 	checkpoint["ugargv"] or {}
		checkpoint["ugargv"][1] = "/home/paul/ug4/bin/ugshell"
		checkpoint["ugargv"][2] = "-ex"
		checkpoint["ugargv"][3] = "/home/paul/Schreibtisch/Biogas_plant_setup/VRL/simulation_files/Biogas.lua"
		checkpoint["ugargv"][4] = "-p"
		checkpoint["ugargv"][5] = "/home/paul/Schreibtisch/Simulations/VRL/Full/biogasVRL_STRUCT_1_STAGE/hydrolysis_0/59/hydrolysis_checkpoint.lua"
	checkpoint["lastFilename"] = "myCheckpointSimulationEnd.ug4vec"
	checkpoint["lastId"] = "SimulationEnd"
	checkpoint["commandline"] = "/home/paul/ug4/bin/ugshell -ex /home/paul/Schreibtisch/Biogas_plant_setup/VRL/simulation_files/Biogas.lua -p /home/paul/Schreibtisch/Simulations/VRL/Full/biogasVRL_STRUCT_1_STAGE/hydrolysis_0/59/hydrolysis_checkpoint.lua "
	checkpoint["ugargc"] = 5
	checkpoint["stdData"] = 	checkpoint["stdData"] or {}
		checkpoint["stdData"]["numCores"] = 1
return checkpoint
end
