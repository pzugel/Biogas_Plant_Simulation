problem = {
    reactorSetup = {
        operatingTemperature = {
            type = "Double",
        },
        reactorType = {
            type = "String",
            style = "default"
        },
        realReactorVolume = {
            type = "Double",
            style = "default"
        }
    },
    reactionSetup = {
        activeReactions = {
            type = "String[]",
            style = "default",
        }
    },
    numericalSetup = {
        sim_starttime = {
            type = "Double",
            style = "default"
        },
        sim_endtime = {
            type = "Double",
            style = "default"
        }
    },
    outputSpecs = {
        outputSetting = {
            type = "String",
            style = "default"
        }
    },
    checkpoint = {
        doReadCheckpoint = {
            type = "Boolean",
            style = "default"
        }
    },
    initialValues = {
        drymass = {
            type = "Double",
            style = "default"
        },
        analysis = {
            type = "Double",
            style = "default",
            tableContent = {
                values = {"Acetic"}
            }
        }
    },
	inflow = {
	    data = {
            type = "String[]",
            style = "default",
            timetable = {
				type = "Double",
            	style = "default",
        		timeTableContent = {
            	}
        	}
        }
	},
}
