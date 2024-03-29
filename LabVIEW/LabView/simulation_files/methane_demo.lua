-- THIS IS JUST A DEMO
-- Use methane.lua for full simulation
problem={
	reactorSetup={
		operatingTemperature=311.15, -- 38°C (mesophil)
		reactorType="Downflow",
		realReactorVolume=70,
	},
	reactionSetup={
		activeReactions={"methane_demo"}, --"Butyric_Degradation", "aceto_Methanogenesis"
	},
	numericalSetup={
		sim_starttime=0,
		sim_endtime=1,
	},
	outputSpecs={
		customSetting={
			cDigestate=true,
			biogas=true,
			developer=true,
			debug=false,
			vtk=false,
		},
	},
	checkpoint={
		doReadCheckpoint=false,
	},
	initialValues = {
		pH = 8.06,
		drymass = 0.0906,
		analysis = {
			["MS"] = 0.004,
			["LCFA"] = 0.003,
			["AA"] = 0.001,
			["Lignin"] = 113.0,
			["Acetic"] = 1.50,
			["Propionic"] = 0.003,
			["Butyric"] = 0.0025,
			["Valeric"] = 0.0007,
			["Methane"] = 0.062,
			["Carbondioxide"] = 0.875,
			["Hydrogen"] = 4.23*1E-9,
			["Nitrogen"] = 2902.92*1E-3,
			["MO_acetoM"] = 1.8,
			["MO_hydroM"] = 3.1,
			["MO_Propionic"] = 1.1,
			["MO_ButyricValeric"] = 1.9,
			["MO_AcidoMS"] = 6.8,
			["MO_AcidoLCFA"] = 1.33,
			["MO_AcidoAA"] = 0.99,
		},
	},
	inflow={
		data={"Butyric", "Acetic"},
		timetable={
			{1, 10, 0.0025, 1.5},		
		},
	},	
	expert = {
		geometry = {
			grid_name = "TestGeom2D.ugx",
			subsets = {
				["reactorVolSubset"] = "Inner",
				["gasPhaseSubset"] = "Head",
				["reactorUpperBnd"] = "Top",
				["reactorLowerBnd"] = "Bot",
				["reactorLeftBnd"] = "SideA",
				["reactorRightBnd"] = "SideB"
			},
			setup_dim = 2,
			reactorHeight = 1.0,
			headHeight = 0.1,
			reactorWidth = 1.0,
			yTop = 1.0,
			
			numPreRefs = 1,
			numRefs = 3,
		},
		
		timestep = {
			dtStart = 0.2,
			dtMax = 1,
			dtMin = 0.00001,
		},
		
		reactionSetup = {
			thermodynModell = false,
			inhibitionTerms = {
				inhibitionAmmonia = false,
				limitedTrace = {
					["Nitrogen"] = true,  --False in Test.lua
				},
				limitedMOSpace = false,
				competitiveAcidUptake = false,
				inhibitionPH = false,
			},
		},
	},
}
