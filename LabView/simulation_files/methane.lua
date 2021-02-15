problem={
	reactorSetup={
		operatingTemperature=311.15,
		reactorType="Downflow",
		realReactorVolume=34.5,
	},
	reactionSetup={
		activeReactions={"simpleMethane_stage"},
	},
	numericalSetup={
		sim_starttime=0,
		sim_endtime=3,
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
	inflow={
		data={"Acetic"},
		timetable={
			{0, 0, 0},		
		},
	},	
}
