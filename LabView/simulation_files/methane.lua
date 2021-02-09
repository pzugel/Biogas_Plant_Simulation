--	Vorlage Benutzer-Input
--	Autor: Rebecca Wittum

problem = {
-----------------------------------------------------------------	
--	Specifications for REACTOR OPERATIONAL SETUP
-----------------------------------------------------------------
	reactorSetup = {
		operatingTemperature = 311.15,		--in Kelvin!
		reactorType = "Downflow",
		realReactorVolume = 34.5	-- volume of actual reactor in L ()				--options: CSTR, Downflow
	},
-----------------------------------------------------------------
--	Setup for REACTION MODEL
-----------------------------------------------------------------
	reactionSetup = {
		activeReactions = {"simpleMethane_stage"}, -- "MethaneStage"
	},

-----------------------------------------------------------------
--	NUMERICAL Specifications for SOLVING
-----------------------------------------------------------------
	numericalSetup = {
		sim_starttime=0,
		sim_endtime=3,
	},

-----------------------------------------------------------------
--	OUTPUT Specifications
-----------------------------------------------------------------
	outputSpecs = {
		outputSetting = "plantBasics",			-- plantBasics: biogas normVol + composition, FOS, COD
												-- plantExtra: plantBasics + all digestateConcentrations
												-- developerBasics: plantExtra + all reactorMass + all biogas
												-- developerDebug: developerBasics + all dbgFiles
	},
	checkpoint={
		doReadCheckpoint=false,
	},
-----------------------------------------------------------------
--	STARTING VALUES
-----------------------------------------------------------------
	initialValues = { -- MISSING: microorganisms
		drymass = 0.1,
		analysis = {
			["Acetic"] = 457.0*1E-3,		-- concentration [g/L]
		},
	},

-----------------------------------------------------------------
--	Specifications for INFLOW (in case of downflow reactor, second stage only)
-----------------------------------------------------------------
	inflow = {
		data = {"Acetic"},	-- options are substance massfractions (e.g. Acetic, Propionic) in [g/L]
		timetable = {	-- Pairs with time of inflow [hours] and amount of overall inflow (hydrolysate) [L/h] and additional data: {time, amount overall, additional data}							
		},
	},
	
} --End of problem description
