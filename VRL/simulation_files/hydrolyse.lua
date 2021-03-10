problem={
	reactorSetup={
		operatingTemperature=311.15,
		reactorType="Downflow",
		realReactorVolume=34.5,
	},
	reactionSetup={
		activeReactions={"simpleHydrolysis_stage"},
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
	initialValues={
		drymass=0.1,
		analysis={
			["Acetic"] = 457.0*1E-3,
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
		data={"Acetic"},
		timetable={
			{0, 0, 0},
		},

	},	
}
