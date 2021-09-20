pSettings = {
    reactorSetup={
      operatingTemperature=328.15,
      realReactorVolume=100,
      reactorType="Downflow"
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
"allwithSAO"
      }
    },
    numericalSetup={
      sim_starttime=59,
      sim_endtime=60
    },
    checkpoint={
      doReadCheckpoint=true,
      checkpointDir="/home/paul/Schreibtisch/Simulations/VRL/Full/biogasVRL_STRUCT_1_STAGE/hydrolysis_0/58/"
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
1.6056975249778e-08,
2.0330613273343e-06,
2.9659783902316e-07,
2.7978336356928e-05,
4.8481754444986e-07,
0.001327612637801,
0.0086207272441976
        }
      }
    },
    feeding={
      stoidisintegration={
        Lipids=0.03186022610483,
        Proteins=0.077697841726619,
        Carbohydrates=0.65927235354573
      },
      volatile=0.973,
      timetable={
{
0,
243
        },
{
24,
243
        },
{
48,
243
        },
{
72,
243
        },
{
96,
243
        }
      },
      analysis={
        Propionic=0.74528,
        Valeric=0.03506,
        Butyric=0.44393,
        Acetic=10.71349
      },
      drymass=0.3649
    },
    initialValues={
      pH=8.06,
      drymass=0.0906,
      analysis={
        MS=0.004,
        AA=0.001,
        Butyric=0.0025,
        Carbohydrates=2.7,
        Acetic=1.5,
        MO_ButyricValeric=1.9,
        Lignin=113,
        Propionic=0.003,
        Valeric=0.0007,
        Proteins=0.97,
        Carbondioxide=0.875,
        Nitrogen=2.90292,
        Lipids=0.8,
        zeroDefault=1e-07,
        moDefault=1,
        MO_acetoM=1.8,
        MO_hydroM=3.1,
        LCFA=0.003,
        MO_AcidoMS=6.8,
        Methane=0.062,
        Hydrogen=4.23e-09,
        MO_AcidoLCFA=1.33,
        MO_AcidoAA=0.99,
        MO_Propionic=1.1
      }
    }
  }