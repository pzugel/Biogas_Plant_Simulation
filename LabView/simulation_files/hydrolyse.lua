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
		outputSetting = "plantBasics",
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
			{0, 243},
		},
	},
	inflow={
		data={"Acetic"},
		timetable={
			{0, 0, 0},
		},

	},	
}
