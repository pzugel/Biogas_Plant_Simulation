-----------------------------------------------------------------
--  Setup Boundary Conditions
--  Author: Rebecca Wittum
--  Edited by Paul Zügel
-----------------------------------------------------------------
dirichletBnd = DirichletBoundary()
neumannBnd = {}
interiorDiriBnds = {}--DirichletBoundary()
interiorNeumBnds = {}--NeumannBoundary()
gasflux = {}

if print_info then
	print("Setting Boundary Conditions")
end

--------------------------------------------------------------------------
-- INFLOW
--------------------------------------------------------------------------
-- function for reading inflow of hydrolysate (L/h) from table in problem description
function InflowOverallAmount(t)
	local overallInflow = 0.0
	local continue = true
	
	for i, vals in ipairs (pSettings.inflow.timetable) do
		if continue and (vals[1]>=t) then
			overallInflow = vals[2]
			continue = false
		end
	end
	if continue then overallInflow = 0.0 end
	return -1.0*overallInflow
end
function InflowOverallAmount_1D(x, t) return InflowOverallAmount(t) end
function InflowOverallAmount_2D(x, y, t) return InflowOverallAmount(t) end
function InflowOverallAmount_3D(x, y, z, t) return InflowOverallAmount(t) end

-- function for reading individual concentrations in hydrolysate inflow (g/L) from table in problem description
function getInflowComposition(t)
	local oldTime = 0.0
	local composition = {}
	local continue = true
	
	for i, vals in ipairs (pSettings.inflow.timetable) do
		if continue and (vals[1]>=t) then
			for j, subs in ipairs (pSettings.inflow.data) do
				composition[subs] = vals[2+j]
			end
			continue = false
		end
		if continue then
			for j, subs in ipairs (pSettings.inflow.data) do
				composition[subs] = 0.0
			end
		end
	end
	return composition
end

-- function to extract information for Acetic in inflow
function inflowConcAcetic(t)
	local composition = getInflowComposition(t)
	return composition["Acetic"]
end
function inflowConcAcetic_1D(x, t) return inflowConcAcetic(t) end
function inflowConcAcetic_2D(x, y, t) return inflowConcAcetic(t) end
function inflowConcAcetic_3D(x, y, z, t) return inflowConcAcetic(t) end

-- function to extract information for Propionic in inflow
function inflowConcPropionic(t)
	local composition = getInflowComposition(t)
	return composition["Propionic"]
end
function inflowConcPropionic_1D(x, t) return inflowConcPropionic(t) end
function inflowConcPropionic_2D(x, y, t) return inflowConcPropionic(t) end
function inflowConcPropionic_3D(x, y, z, t) return inflowConcPropionic(t) end

-- function to extract information for Butyric in inflow
function inflowConcButyric(t)
	local composition = getInflowComposition(t)
	return composition["Butyric"]
end
function inflowConcButyric_1D(x, t) return inflowConcButyric(t) end
function inflowConcButyric_2D(x, y, t) return inflowConcButyric(t) end
function inflowConcButyric_3D(x, y, z, t) return inflowConcButyric(t) end

-- function to extract information for Valeric in inflow
function inflowConcValeric(t)
	local composition = getInflowComposition(t)
	return composition["Valeric"]
end
function inflowConcValeric_1D(x, t) return inflowConcValeric(t) end
function inflowConcValeric_2D(x, y, t) return inflowConcValeric(t) end
function inflowConcValeric_3D(x, y, z, t) return inflowConcValeric(t) end

-- function to extract information for MS in inflow
function inflowConcMS(t)
	local composition = getInflowComposition(t)
	return composition["MS"]
end
function inflowConcMS_1D(x, t) return inflowConcMS(t) end
function inflowConcMS_2D(x, y, t) return inflowConcMS(t) end
function inflowConcMS_3D(x, y, z, t) return inflowConcMS(t) end

-- function to extract information for AA in inflow
function inflowConcAA(t)
	local composition = getInflowComposition(t)
	return composition["AA"]
end
function inflowConcAA_1D(x, t) return inflowConcAA(t) end
function inflowConcAA_2D(x, y, t) return inflowConcAA(t) end
function inflowConcAA_3D(x, y, z, t) return inflowConcAA(t) end

-- function to extract information for LCFS in inflow
function inflowConcLCFA(t)
	local composition = getInflowComposition(t)
	return composition["LCFA"]
end
function inflowConcLCFA_1D(x, t) return inflowConcLCFA(t) end
function inflowConcLCFA_2D(x, y, t) return inflowConcLCFA(t) end
function inflowConcLCFA_3D(x, y, z, t) return inflowConcLCFA(t) end

if (no_flow==false) then
	dirichletBnd:add(1.271376E12*pSettings.expert.geometry.reactorHeight+1.296E14, "p_l", pSettings.expert.geometry.subsets["reactorLowerBnd"])
	inflowConcentration = {}
	areaTOP = Integral(1,u, pSettings.expert.geometry.subsets["reactorUpperBnd"])
	areaBOT = Integral(1,u, pSettings.expert.geometry.subsets["reactorLowerBnd"])
	
	local inflowAmount = 0
	if pSettings.inflow then
		inflowAmount = LuaUserNumber("InflowOverallAmount_"..setup_dim.."D")/factor_volCorrection/areaTOP
		for j, sname in ipairs (pSettings.inflow.data) do
			inflowConcentration[sname] = LuaUserNumber("inflowConc"..sname.."_"..setup_dim.."D") 
		end
		
		neumannBnd_p_l = NeumannBoundary("p_l")
		neumannBnd_p_l:add(inflowAmount*Density["Water"], pSettings.expert.geometry.subsets["reactorUpperBnd"], pSettings.expert.geometry.subsets["reactorVolSubset"])
		if print_info then
			print("\t Added Dirichlet and Neumann for pressure in "..pSettings.expert.geometry.subsets["reactorVolSubset"])
			if outputSpecs.debug then
				print("\t Value of NeumannBnd for p_l: "..-1.0/factor_volCorrection/areaTOP*Density["Water"])
				print("\t Integrated value direct of NeumannBnd for p_l: ".. Integral(-1.0/factor_volCorrection/areaTOP*Density["Water"],u,pSettings.expert.geometry.subsets["reactorUpperBnd"]))
				print("\t Integrated value via LuaUserFunction of NeumannBnd for p_l: ".. Integral(inflowAmount*Density["Water"],u,pSettings.expert.geometry.subsets["reactorUpperBnd"]))
			end
		end
	end
	
	--ACHTUNG: nur für phase gas, liquid und eben traces
	for sname, props in pairs (Eq_Subs_MOs) do
		if ((Phase[sname] == 	"liquid") or (Phase[sname] == "gas")) then
			dirichletBnd:add(0.0, sname, pSettings.expert.geometry.subsets["reactorLowerBnd"])
			--two options: dirichlet on TOP (concentration in hydrolysate), neumann on TOP (flux)
			--dirichletBnd:add(inflowConcentration[sname]/Density["Water"], sname, pSettings.expert.geometry.subsets["reactorUpperBnd"])
			if print_info then
				print("\t Added Dirichlet-Zero for "..sname.." on "..pSettings.expert.geometry.subsets["reactorLowerBnd"])
			end
			if inflowConcentration[sname] then
				neumannBnd[sname] = NeumannBoundary(sname)
				neumannBnd[sname]:add(inflowAmount*inflowConcentration[sname], pSettings.expert.geometry.subsets["reactorUpperBnd"], pSettings.expert.geometry.subsets["reactorVolSubset"])
				if print_info then
					print("\t Added Neumann inflow for "..sname.." in "..pSettings.expert.geometry.subsets["reactorVolSubset"])
					if outputSpecs.debug then
						print("\t Integrated value direct of NeumannBnd: ".. Integral(-1.0/factor_volCorrection/areaTOP*inflowConcentration[sname],u,pSettings.expert.geometry.subsets["reactorUpperBnd"]))
						print("\t Integrated value via LuaUserFunction of NeumannBnd: ".. Integral(inflowAmount*inflowConcentration[sname],u,pSettings.expert.geometry.subsets["reactorUpperBnd"]))
					end
				end
			end
		end
	end
	for sname, props in pairs (Eq_Traces) do
		dirichletBnd:add(0.0, sname, pSettings.expert.geometry.subsets["reactorLowerBnd"])
		if print_info then
			print("\t Added Dirichlet-Zero for "..sname.." on "..pSettings.expert.geometry.subsets["reactorLowerBnd"])
		end
		if inflowConcentration[sname] then
			neumannBnd[sname] = NeumannBoundary(sname)
			neumannBnd[sname]:add(inflowAmount*inflowConcentration[sname], pSettings.expert.geometry.subsets["reactorUpperBnd"], pSettings.expert.geometry.subsets["reactorVolSubset"])
			if print_info then
				print("\t Added Neumann inflow for "..sname.." in "..pSettings.expert.geometry.subsets["reactorVolSubset"])
				if outputSpecs.debug then
					print("\t Integrated value direct of NeumannBnd: ".. Integral(-1.0/factor_volCorrection/areaTOP*inflowConcentration[sname],u,pSettings.expert.geometry.subsets["reactorUpperBnd"]))
					print("\t Integrated value via LuaUserFunction of NeumannBnd: ".. Integral(inflowAmount*inflowConcentration[sname],u,pSettings.expert.geometry.subsets["reactorUpperBnd"]))
				end
			end
		end
	end
end

if pSettings.expert.geometry.subsets["gasPhaseSubset"] then
	interiorDiriBnds["phi"] = DirichletBoundary()
	interiorDiriBnds["phi"]:add(0.0,"phi", pSettings.expert.geometry.subsets["gasPhaseSubset"])
	if print_info then
		print("\t Added Dirichlet 0 for Phi in "..pSettings.expert.geometry.subsets["gasPhaseSubset"])
	end
	interiorDiriBnds["Hions"] = DirichletBoundary()
	interiorDiriBnds["Hions"]:add(0.0,"Hions", pSettings.expert.geometry.subsets["gasPhaseSubset"])
	if print_info then
		print("\t Added Dirichlet 0 for Hydrogen ions in "..pSettings.expert.geometry.subsets["gasPhaseSubset"])
	end
	
	if (no_flow==false) then
		interiorDiriBnds["p_l"] = DirichletBoundary()
		interiorDiriBnds["p_l"]:add(0.0,"p_l", pSettings.expert.geometry.subsets["gasPhaseSubset"]) 
		if print_info then
			print("\t Added Dirichlet 0 for pressure in "..pSettings.expert.geometry.subsets["gasPhaseSubset"])
		end
	end
	
	for sname, props in pairs(Eq_Subs_MOs) do
		if Phase[sname] == "gas" then
			if extraGasphase == false then
				interiorDiriBnds[sname] = DirichletBoundary()
				interiorDiriBnds[sname]:add(0.0,sname, pSettings.expert.geometry.subsets["gasPhaseSubset"])
				if print_info then
					print("\t Added Dirichlet 0 for "..sname.." in "..pSettings.expert.geometry.subsets["gasPhaseSubset"])
				end
			end
		else
			interiorDiriBnds[sname] = DirichletBoundary()
			interiorDiriBnds[sname]:add(0.0,sname, pSettings.expert.geometry.subsets["gasPhaseSubset"])
			if print_info then
				print("\t Added Dirichlet 0 for "..sname.." in "..pSettings.expert.geometry.subsets["gasPhaseSubset"])
			end
		end	
	end
	for sname, props in pairs(Eq_Traces) do
			interiorDiriBnds[sname] = DirichletBoundary()
			interiorDiriBnds[sname]:add(0.0,sname, pSettings.expert.geometry.subsets["gasPhaseSubset"])
			if print_info then
				print("\t Added Dirichlet 0 for "..sname.." in "..pSettings.expert.geometry.subsets["gasPhaseSubset"])
			end
	end
end

if dbg_activateONEfct then
	oneBnds = {}
	for subid, subname in pairs (pSettings.expert.geometry.subsets) do
		if subid ~= "reactorVolSubset" then
		oneBnds[subname] = DirichletBoundary()
		oneBnds[subname]:add(1.0,"one", subname)
		end
	end
	oneBnds[pSettings.expert.geometry.subsets["reactorLowerBnd"]]:add(0.0,"one", pSettings.expert.geometry.subsets["reactorLowerBnd"])
end
-------------------------------------------
--  Setup Domain Discretization
-------------------------------------------
domainDisc = DomainDiscretization(approxSpace)
domainDisc:add(phiEq)
domainDisc:add(HEq)
if dbg_activateONEfct then domainDisc:add(oneEq) end
if (no_flow==false) then
	domainDisc:add(p_lEq)
end

if pSettings.expert.geometry.subsets["gasPhaseSubset"] then
	domainDisc:add(interiorDiriBnds["phi"])
	domainDisc:add(interiorDiriBnds["Hions"])
	if (no_flow==false) then domainDisc:add(interiorDiriBnds["p_l"]) end
	for sname, props in pairs (Eq_Subs_MOs) do
		if (Phase[sname] == "gas") then
			if extraGasphase == false then
				domainDisc:add(interiorDiriBnds[sname])
			end
		else
			domainDisc:add(interiorDiriBnds[sname])
		end
	end
	for sname, props in pairs (Eq_Traces) do
		domainDisc:add(interiorDiriBnds[sname])
	end
end
for sname, props in pairs (Eq_Subs_MOs) do
	domainDisc:add(props.eqname)
	if (Phase[sname] == "gas" and extraGasphase) then
		domainDisc:add(props.peqname)
	end
end
for sname, props in pairs (Eq_Traces) do
	domainDisc:add(props.eqname)
end

if (no_flow==false) then
	domainDisc:add(dirichletBnd)
	if neumannBnd_p_l then domainDisc:add(neumannBnd_p_l) end
	for sname, props in pairs(Eq_Subs_MOs) do
		if neumannBnd[sname] then domainDisc:add(neumannBnd[sname]) end
	end
end
if dbg_activateONEfct then
	for subid, subname in pairs (pSettings.expert.geometry.subsets) do
		if oneBnds[subname] then
			domainDisc:add(oneBnds[subname])
		end
	end
end
