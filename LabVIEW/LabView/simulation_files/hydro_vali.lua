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
        pH = {
            type = "Double",
            style = "default"
        },
        drymass = {
            type = "Double",
            style = "default"
        },
        analysis = {
            type = "Double",
            style = "default",
            tableContent = {
                values = {"Carbohydrates","Lipids","Proteins","MS","LCFA","AA","Lignin","Acetic","Propionic","Butyric","Valeric","Methane","Carbondioxide","Hydrogen","Nitrogen","MO_acetoM","MO_hydroM","MO_Propionic","MO_ButyricValeric","MO_AcidoMS","MO_AcidoLCFA","MO_AcidoAA"}
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
            type = "Integer",
            style = "default",
            timeTableContent = {
                numberEntries = 5
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
    expert = {
        geometry = {
            grid_name = {
                type = "String",
                style = "default"
            },
            subsets = {
                type = "String",
                style = "default",
				default = "abc",
                tableContent = {
                    values = {"reactorVolSubset","gasPhaseSubset","reactorUpperBnd","reactorLowerBnd","reactorLeftBnd","reactorRightBnd"}
                }
            },
            setup_dim = {
                type = "Integer",
                range = {
                    values = {0, 3}
                }
            },
            reactorHeight = {
                type = "Double",
                style = "default"
            },
            headHeight = {
                type = "Double",
                style = "default"
            },
            reactorWidth = {
                type = "Double",
                style = "default"
            },
            yTop = {
                type = "Double",
                style = "default"
            },
            numPreRefs = {
                type = "Double",
                style = "default"
            },
            numRefs = {
                type = "Double",
                style = "default"
            }
        },
        timestep = {
            dtStart = {
                type = "Double",
                style = "default",
                range = {
                    min = 0,
                    max = 5
                }
            },
            dtMax = {
                type = "Double",
                style = "default"
            },
            dtMin = {
                type = "Double",
                style = "default"
            }
        },
        reactionSetup = {
            thermodynModell = {
                type = "Boolean",
                default = "true"
            },
            inhibitionTerms = {
                inhibitionAmmonia = {
                    type = "Boolean",
                    default = "true"
                },
                limitedTrace = {
                    type = "Boolean",
                    style = "default",
                    tableContent = {
                        values = {"Nitrogen"}
                    }
                },
                limitedMOSpace = {
                    type = "Boolean",
                    default = "false"
                },
                competitiveAcidUptake = {
                    type = "Boolean",
                    default = "true"
                },
                inhibitionPH = {
                    type = "Boolean",
                    default = "true"
                }
            }
        }
    }
}
