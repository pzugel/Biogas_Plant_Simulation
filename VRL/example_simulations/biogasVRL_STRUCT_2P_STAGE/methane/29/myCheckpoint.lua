function LoadTheCheckpoint()
checkpoint = checkpoint or {}
	checkpoint["myData"] = 	checkpoint["myData"] or {}
		checkpoint["myData"]["volProdPrevious"] = 		checkpoint["myData"]["volProdPrevious"] or {}
			checkpoint["myData"]["volProdPrevious"]["Hydrogen"] = 0.0019678026242446
			checkpoint["myData"]["volProdPrevious"]["Carbondioxide"] = 3.6617563160349
			checkpoint["myData"]["volProdPrevious"]["Methane"] = 9.8146569689084
		checkpoint["myData"]["gasFlowPrevious"] = 		checkpoint["myData"]["gasFlowPrevious"] or {}
			checkpoint["myData"]["gasFlowPrevious"]["Hydrogen"] = 0.0019236996268372
			checkpoint["myData"]["gasFlowPrevious"]["Carbondioxide"] = 3.5479857248727
			checkpoint["myData"]["gasFlowPrevious"]["Methane"] = 7.3081880867595
	checkpoint["ugargv"] = 	checkpoint["ugargv"] or {}
		checkpoint["ugargv"][1] = "/home/paul/ug4/bin/ugshell"
		checkpoint["ugargv"][2] = "-ex"
		checkpoint["ugargv"][3] = "/home/paul/Schreibtisch/Biogas_plant_setup/VRL/simulation_files/Biogas.lua"
		checkpoint["ugargv"][4] = "-p"
		checkpoint["ugargv"][5] = "/home/paul/Schreibtisch/Simulations/VRL/Full/biogasVRL_20210908_152831/methane/29/methane_checkpoint.lua"
	checkpoint["lastFilename"] = "myCheckpointSimulationEnd.ug4vec"
	checkpoint["lastId"] = "SimulationEnd"
	checkpoint["commandline"] = "/home/paul/ug4/bin/ugshell -ex /home/paul/Schreibtisch/Biogas_plant_setup/VRL/simulation_files/Biogas.lua -p /home/paul/Schreibtisch/Simulations/VRL/Full/biogasVRL_20210908_152831/methane/29/methane_checkpoint.lua "
	checkpoint["ugargc"] = 5
	checkpoint["stdData"] = 	checkpoint["stdData"] or {}
		checkpoint["stdData"]["numCores"] = 1
return checkpoint
end
