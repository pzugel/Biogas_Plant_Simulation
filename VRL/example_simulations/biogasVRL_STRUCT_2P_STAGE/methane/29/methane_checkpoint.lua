problem={
	reactorSetup={ -- ATB
		operatingTemperature=311.15, -- 38°C (mesophil)
		reactorType="Downflow",
		realReactorVolume=32.12,
	},
	reactionSetup={
		activeReactions={"Acidogenesis","Acetogenesis","Methanogenesis"},
	},
	numericalSetup={
		sim_starttime=29,
		sim_endtime=30,
	},
	outputSpecs={
		customSetting={
			cDigestate=true,
			biogas=true,
			developer=true,
			debug=true,
			vtk=false,
		},
	},
	checkpoint={
		doReadCheckpoint=true,
		checkpointDir="/home/paul/Schreibtisch/Simulations/VRL/Full/biogasVRL_20210908_152831/methane/28/",
	},
	initialValues={
		pH=8.06,
		drymass=0.0906,
		analysis={
			["MS"]=0.004,
			["LCFA"]=0.003,
			["AA"]=0.001,
			["Lignin"]=113.0,
			["Acetic"]=1.50,
			["Propionic"]=0.003,
			["Butyric"]=0.0025,
			["Valeric"]=0.0007,
			["Methane"]=0.062,
			["Carbondioxide"]=0.875,
			["Hydrogen"]=4.23*1E-9,
			["Nitrogen"]=2902.92*1E-3,
			["MO_acetoM"]=0.05,
			["MO_hydroM"]=3.1,
			["MO_Propionic"]=1.1,
			["MO_ButyricValeric"]=1.9,
			["MO_AcidoMS"]=6.8,
			["MO_AcidoLCFA"]=1.33,
			["MO_AcidoAA"]=0.99,
		},
	},
	inflow={
		data={"MS", "Butyric", "AA", "Propionic", "Valeric", "LCFA", "Acetic"},
		timetable={
			{30.0, 30.0, 0.0026512374410938414, 8.765002562618794E-4, 5.444750741549069E-4, 0.003575301936277806, 1.5078348004863822E-4, 0.0059736004818531615, 0.019308541813509068},
		},
	},
	expert={
		geometry={
			grid_name="TestGeom2D.ugx",
			subsets={
				["reactorVolSubset"]="Inner",
				["gasPhaseSubset"]="Head",
				["reactorUpperBnd"]="Top",
				["reactorLowerBnd"]="Bot",
				["reactorLeftBnd"]="SideA",
				["reactorRightBnd"]="SideB",
			},
			setup_dim=2,
			reactorHeight=1.0,
			headHeight=0.1,
			reactorWidth=1.0,
			yTop=1.0,
			numPreRefs=1,
			numRefs=3,
		},
		timestep={
			dtStart=0.2,
			dtMax=1,
			dtMin=0.00001,
		},
		reactionSetup={
			thermodynModell=false,
			inhibitionTerms={
				inhibitionAmmonia=false,
				limitedTrace={
					["Nitrogen"]=true,
				},
				limitedMOSpace=false,
				competitiveAcidUptake=false,
				inhibitionPH=false,
			},
		},
	},
}