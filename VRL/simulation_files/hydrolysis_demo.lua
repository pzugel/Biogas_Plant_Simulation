-- THIS IS JUST A DEMO
-- Use hydrolysis.lua for full simulation
problem={
	reactorSetup={
		operatingTemperature=328,15, --55°C (thermophil)
		reactorType="Downflow",
		realReactorVolume=40,
	},
	reactionSetup={
		activeReactions={"hydrolysis_demo"}, --"Hydrolysis_Carbohydrates", "Acidogenesis_MS
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
			["Carbohydrates"] = 2.7,
			["Lipids"] = 0.8,
			["Proteins"] = 0.97,
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
	feeding={
		drymass=0.3649,
		volatile=0.973,
		stoidisintegration={
			["Proteins"] = 0.077697841726619,
			["Lipids"] = 0.03186022610483,
			["Carbohydrates"] = 0.65927235354573,
		},
		analysis={
			["Acetic"] = 10713.49*1E-3,
			["Propionic"] = 745.28*1E-3,
			["Butyric"] = 443.93*1E-3,
			["Valeric"] = 35.06*1E-3,
		},
		timetable={						
			{0,	243	},
			{24, 243},
			{48, 243},
			{72, 243},
			{96, 243},
			{120, 243},
			{144, 243},
			{168, 243},
			{192, 243},
			{216, 243},
			{240, 10*243},
			{264, 10*243},
			{288, 10*243},
			{312, 10*243},
			{336, 10*243},
			{360, 243},
			{384, 243},
			{408, 243},
			{432, 243},
			{456, 243},
			{480, 243},
			{504, 243},
			{528, 243},
			{552, 243},
			{576, 243},
			{600, 243},
		},
	},
	inflow={
		data={"Butyric", "Acetic"},
		timetable={
			{1, 10, 1, 1},
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
					["Nitrogen"] = true, --False in Test.lua
				},
				limitedMOSpace = false,
				competitiveAcidUptake = false,
				inhibitionPH = false,
			},
		},
	},
}
