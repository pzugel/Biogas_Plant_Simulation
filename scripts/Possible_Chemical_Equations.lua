-----------------------------------------------------------------
--  Possible Chemical Equations
--  Author: Rebecca Wittum
--  Edited by Paul Zügel
-----------------------------------------------------------------
ReactionSet = {
	["Disintegration"] = {"Disintegration_Initial", "Disintegration_Substrate"},
	["Hydrolysis"] = {"Hydrolysis_Carbohydrates", "Hydrolysis_Proteins", "Hydrolysis_Lipids"},
	["Acidogenesis"] = {"Acidogenesis_MS", "Acidogenesis_AA", "Acidogenesis_LCFA"},
	["Valeric_Degradation"] = {"Valeric_Degradation"},
	["Butyric_Degradation"] = {"Butyric_Degradation"},
	["Propionic_Degradation"] = {"Propionic_Degradation"},
	["small_Acetogenesis"] = {"Propionic_Degradation", "aceto_Methanogenesis", "hydro_Methanogenesis"},
	["medium_Acetogenesis"] = {"Propionic_Degradation", "aceto_Methanogenesis", "hydro_Methanogenesis", "Butyric_Degradation"},
	["all_Acetogenesis"] = {"Propionic_Degradation", "aceto_Methanogenesis", "hydro_Methanogenesis", "Butyric_Degradation", "Valeric_Degradation"},
	["Methanogenesis"] = {"aceto_Methanogenesis", "hydro_Methanogenesis"},
	["Acetogenesis"] = 	{"Propionic_Degradation", "Butyric_Degradation", "Valeric_Degradation", "Acetic_Degradation"},  --Added this
	["dbg_acetoMethanogenesis"] = {"aceto_Methanogenesis"},
	["simpleTwoStage"] = {"simpleHydrolysis", "aceto_Methanogenesis"},
	["all"] = {"Disintegration_Initial", "Disintegration_Substrate",
		"Hydrolysis_Carbohydrates", "Hydrolysis_Proteins", "Hydrolysis_Lipids",
		"Acidogenesis_MS", "Acidogenesis_AA", "Acidogenesis_LCFA",
		"Valeric_Degradation", "Butyric_Degradation", "Propionic_Degradation", "aceto_Methanogenesis", "hydro_Methanogenesis"},
	["allwithSAO"] = {"Disintegration_Initial", "Disintegration_Substrate",
		"Hydrolysis_Carbohydrates", "Hydrolysis_Proteins", "Hydrolysis_Lipids",
		"Acidogenesis_MS", "Acidogenesis_AA", "Acidogenesis_LCFA",
		"Valeric_Degradation", "Butyric_Degradation", "Propionic_Degradation", "Acetic_Degradation", "aceto_Methanogenesis", "hydro_Methanogenesis"},
	["hydrolysis_demo"] = {"Hydrolysis_Carbohydrates", "Acidogenesis_MS"}, -- DEMO
	["methane_demo"] = {"Butyric_Degradation", "aceto_Methanogenesis"}, -- DEMO
}

ChemReaction = {}
ChemReaction["aceto_Methanogenesis"] = {
	description = "acetoclastic Methanogenesis",
	type = "monod_chem",
	reactants = {
		["Acetic"] = 1,
	},
	products = {
		["Methane"] = 1,
		["Carbondioxide"] = 1,
	},
	limitingReactant = "Acetic",
	associated_mo = "MO_acetoM",
	stdGibbs = -23.8,
}
ChemReaction["Acetic_Degradation"] = {
	description = "Acetate Oxidation",
	type = "monod_chem",
	reactants = {
		["Acetic"] = 1,
		["Water"] = 2,
	},
	products = {
		["Carbondioxide"] = 2,
		["Hydrogen"] = 4,
	},
	limitingReactant = "Acetic",
	associated_mo = "MO_Acetic",
	stdGibbs = 169.2,
}
ChemReaction["hydro_Methanogenesis"] = {
	description = "hydrogenotrophic Methanogenesis",
	type = "monod_chem",
	reactants = {
		["Hydrogen"] = 4,
		["Carbondioxide"] = 1,
	},
	products = {
		["Methane"] = 1,
		["Water"] = 2,
	},
	
	limitingReactant = "Hydrogen",
	associated_mo = "MO_hydroM",
	--stdGibbs = -193.0,
}
ChemReaction["Propionic_Degradation"] = {
	description = "Acetogenesis - Degradation of Propionic Acid",
	type = "monod_chem",
	reactants = {
		["Propionic"] = 1,
		["Water"] = 2,
	},
	products = {
		["Acetic"] = 1,
		["Carbondioxide"] = 1,
		["Hydrogen"] = 3,
	},
	limitingReactant = "Propionic",
	associated_mo = "MO_Propionic",
	stdGibbs = 133.5,
}
ChemReaction["Butyric_Degradation"] = {
	description = "Acetogenesis - Degradation of Butyric Acid",
	type = "monod_chem",
	reactants = {
		["Butyric"] = 1,
		["Water"] = 2,
	},
	products = {
		["Acetic"] = 2,
		["Hydrogen"] = 2,
	},
	limitingReactant = "Butyric",
	associated_mo = "MO_ButyricValeric",
	stdGibbs = 96.4,
}
ChemReaction["Valeric_Degradation"] = {
	description = "Acetogenesis - Degradation of Valeric Acid",
	type = "monod_chem",
	reactants = {
		["Valeric"] = 1,
		["Water"] = 2,
	},
	products = {
		["Propionic"] = 1,
		["Acetic"] = 1,
		["Hydrogen"] = 2,
	},
	limitingReactant = "Valeric",
	associated_mo = "MO_ButyricValeric",
	stdGibbs = 95.8,
}

ChemReaction["Disintegration_Substrate"] = {
	description = "Disintegration of Substrate",
	type = "first_order",
	reactants = {
		["Substrate"] = 1,
	},
	products = {
		["Carbohydrates"] = disFrac["Carbohydrates"],
		["Proteins"] = disFrac["Proteins"],
		["Lipids"] = disFrac["Lipids"],
		["Lignin"] = disFrac["Lignin"],
	},
	limitingReactant = "Substrate",
}
ChemReaction["Disintegration_Initial"] = {
	description = "Disintegration of inital Substrate",
	type = "first_order",
	reactants = {
		["Initial_Substrate"] = 1,
	},
	products = {
		["Carbohydrates"] = initValFracs["Carbohydrates"],
		["Proteins"] = initValFracs["Proteins"],
		["Lipids"] = initValFracs["Lipids"],
		["Lignin"] = initValFracs["Lignin"],
	},
	limitingReactant = "Initial_Substrate",
}

ChemReaction["Hydrolysis_Carbohydrates"] = {
	description = "Hydrolysis of Carbohydrates",
	type = "first_order",
	reactants = {
		["Carbohydrates"] = 1,
	},
	products = {
		["MS"] = 1,
	},
	limitingReactant = "Carbohydrates",
}
ChemReaction["Hydrolysis_Proteins"] = {
	description = "Hydrolysis of Proteins",
	type = "first_order",
	reactants = {
		["Proteins"] = 1,
	},
	products = {
		["AA"] = 1,
	},
	limitingReactant = "Proteins",
}
ChemReaction["Hydrolysis_Lipids"] = {
	description = "Hydrolysis of Lipids",
	type = "first_order",
	reactants = {
		["Lipids"] = 1,
	},
	products = {
		["MS"] = 0.105,
		["LCFA"] = 0.893,
		["Hydrogen"] = 0.002,
	},
	limitingReactant = "Lipids",
}
ChemReaction["Acidogenesis_MS"] = {
	description = "Acidogenesis of Monosaccharides",
	type = "monod_chem",
	reactants = {
		["MS"] = 1,
		["Water"] = 0.997,
	},
	products = {
		["Acetic"] = 1.23,
		["Propionic"] = 0.46,
		["Butyric"] = 0.156,
		["Carbondioxide"] = 1.54,
		["Hydrogen"] = 2.304,
		["Water"] = 0.23,
	},
	limitingReactant = "MS",
	associated_mo = "MO_AcidoMS",
	--stdGibbs = -184.7,
}

ChemReaction["Acidogenesis_AA"] = {
	description = "Acidogenesis of Aminoacids",
	type = "monod_chem",
	reactants = {
		["AA"] = 1,
	},
	products = {
		["Valeric"] = 0.213,
		["Butyric"] = 0.259,
		["Propionic"] = 0.096,
		["Acetic"] = 0.979,
		["Carbondioxide"] = 0.819,
		["Hydrogen"] = 0.660,
	},
	limitingReactant = "AA",
	associated_mo = "MO_AcidoAA",
}
ChemReaction["Acidogenesis_LCFA"] = {
	description = "Acidogenesis of Long Chain Fatty Acids",
	type = "monod_chem",
	reactants = {
		["LCFA"] = 1,
		["Water"] = 14,
	},
	products = {
		["Acetic"] = 8,
		["Hydrogen"] = 14,
	},
	limitingReactant = "LCFA",
	associated_mo = "MO_AcidoLCFA",
	--stdGibbs = 722.4,
}
ChemReaction["simpleHydrolysis"] = {
	description = "simple Hydrolysis for two stage reactor",
	type = "first_order",
	reactants = {
		["Substrate"] = 1,
	},
	products = {
		["Acetic"] = disFrac["Acetic"],
		["Lignin"] = disFrac["Lignin"],
	},
	limitingReactant = "Substrate",
}
