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
            default = "bTest"
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
        customSetting = {
            cDigestate = {
                type = "Boolean",
                style = "default"
            },
            biogas = {
                type = "Boolean",
                style = "default"
            },
            developer = {
                type = "Boolean",
                style = "default"
            },
            debug = {
                type = "Boolean",
                style = "default"
            },
            vtk = {
                type = "Boolean",
                style = "default"
            }
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
    feeding = {
        drymass = {
            type = "Double",
            style = "default"
        },
        volatile = {
            type = "Double",
            style = "default"
        },
        stoidisintegration = {
            type = "Double",
            style = "default",
            tableContent = {
                values = {"Proteins","Lipids","Carbohydrates"}
            }
        },
        analysis = {
            type = "Double",
            style = "default",
            tableContent = {
                values = {"Acetic","Propionic","Butyric","Valeric"}
            }
        },
        timetable = {
            type = "Double",
            style = "default",
            timeTableContent = {
                numberEntries = 25
            }
        }
    },
	inflow = {
		data = {
            type = "String[]",
            style = "default",
        },
		timetable = {
            type = "Double",
            style = "default",
            timeTableContent = {
                numberEntries = 0
            }
        }
	},
}
