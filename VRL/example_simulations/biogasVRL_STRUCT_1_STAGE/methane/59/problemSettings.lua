pSettings = {
    numericalSetup={
      sim_starttime=59,
      sim_endtime=60
    },
    reactorSetup={
      operatingTemperature=311.15,
      realReactorVolume=32.12,
      reactorType="Downflow"
    },
    inflow={
      data={
"MS",
"Butyric",
"AA",
"Propionic",
"Valeric",
"LCFA",
"Acetic"
      },
      timetable={
{
60,
10,
0.0026442616619,
0.00092180199525598,
0.00054972940383735,
0.0038651375334903,
0.00015925665843811,
0.0028494938520611,
0.018976383359381
        }
      }
    },
    checkpoint={
      doReadCheckpoint=true,
      checkpointDir="/home/paul/Schreibtisch/Simulations/VRL/Full/biogasVRL_STRUCT_1_STAGE/methane/58/"
    },
    outputSpecs={
      customSetting={
        developer=true,
        debug=true,
        vtk=false,
        cDigestate=true,
        biogas=true
      }
    },
    expert={
      debugging={
        toggleKinetics={
          dbg_noMOdeath=false
        }
      },
      timestep={
        dtStart=0.2,
        dtMax=1,
        dtMin=1e-05
      },
      solverDesc={
        type="bicgstab",
        precond={
          type="gmg",
          baseSolver="lu",
          preSmooth=3,
          smoother={
            type="sgs"
          },
          cycle="V",
          baseLevel=2,
          postSmooth=3,
          adaptive=true,
          rap=true
        },
        convCheck={
          type="standard",
          iterations=34,
          verbose=true,
          absolute=1e-12,
          reduction=1e-08
        }
      },
      geometry={
        subsets={
          reactorVolSubset="Inner",
          reactorUpperBnd="Top",
          reactorLowerBnd="Bot",
          reactorLeftBnd="SideA",
          gasPhaseSubset="Head",
          reactorRightBnd="SideB"
        },
        numPreRefs=1,
        setup_dim=2,
        headHeight=0.1,
        grid_name="TestGeom2D.ugx",
        reactorHeight=1,
        numRefs=3,
        yTop=1,
        reactorWidth=1
      },
      reactionSetup={
        thermodynModell=false,
        thermodynApproach="factor",
        inhibitionTerms={
          inhibitionPH=false,
          competitiveAcidUptake=false,
          inhibitionHydrogen={
            Acetic_Degradation=false,
            Propionic_Degradation=false,
            Acidogenesis_LCFA=true,
            Valeric_Degradation=false,
            Butyric_Degradation=false
          },
          inhibitionAmmonia=false,
          limitedTrace={
            Nitrogen=true
          },
          limitedMOSpace=false
        }
      }
    },
    reactionSetup={
      activeReactions={
"Acidogenesis",
"Acetogenesis",
"Methanogenesis"
      }
    },
    initialValues={
      pH=8.06,
      drymass=0.0906,
      analysis={
        MS=0.004,
        AA=0.001,
        Butyric=0.0025,
        MO_ButyricValeric=1.9,
        Acetic=1.5,
        Lignin=113,
        Valeric=0.0007,
        MO_Propionic=1.1,
        Nitrogen=2.90292,
        Propionic=0.003,
        zeroDefault=1e-07,
        Carbondioxide=0.875,
        MO_hydroM=3.1,
        moDefault=1,
        LCFA=0.003,
        MO_AcidoMS=6.8,
        Methane=0.062,
        Hydrogen=4.23e-09,
        MO_AcidoLCFA=1.33,
        MO_AcidoAA=0.99,
        MO_acetoM=1.8
      }
    }
  }