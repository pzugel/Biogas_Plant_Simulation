package vrl.biogas.biogascontrol;

import eu.mihosoft.vrl.io.VersionInfo;
import eu.mihosoft.vrl.system.InitPluginAPI;
import eu.mihosoft.vrl.system.PluginAPI;
import eu.mihosoft.vrl.system.PluginDependency;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.VPluginAPI;
import eu.mihosoft.vrl.system.VPluginConfigurator;
import vrl.biogas.biogascontrol.elements.userStructureElements.UserHydrolysis;
import vrl.biogas.biogascontrol.elements.userStructureElements.UserMethane;
import vrl.biogas.biogascontrol.elements.userStructureElements.UserStart;
import vrl.biogas.biogascontrol.elements.userStructureElements.UserStorage;
import vrl.biogas.biogascontrol.outputloader.BiogasPlotter;
import vrl.biogas.biogascontrol.outputloader.PlotDisplay;
import vrl.biogas.biogascontrol.structures.*;

/**
 * Plugin Configurator to export Eclipse project into VRL plugin<br>
 * Select "Export as runnable JAR" - Check "Package required libraries into generated JAR"<br>
 * 
 * @author Paul Zügel, Michael Hoffer
 *
 */
public class BiogasControlPluginConfigurator extends VPluginConfigurator{
	public BiogasControlPluginConfigurator() {
	    //specify the plugin name and version
	   setIdentifier(new PluginIdentifier("VRLBiogas-Plugin", "0.1"));
       
	   // describe the plugin
	   setDescription("Plugin for the control of a UG4-Biogasmodel");

	   // copyright info
	   setCopyrightInfo("VRLBiogas-Plugin",
	           "(c) Paul Zügel - GCSC 2021",
	           "https://github.com/pzugel/Biogas_Plant_Simulation", "", "");
	   
	   // add dependency - DO NOT REMOVE
	   addDependency(new PluginDependency("VRL-JFreeChart", "0.2.5.1", VersionInfo.UNDEFINED)); 
	   exportPackage("vrl.biogas.biogascontrol");
	}

	@Override
	public void register(PluginAPI api) {

	   // register plugin with canvas
	   if (api instanceof VPluginAPI) {
		   VPluginAPI vapi = (VPluginAPI) api;
	       vapi.addTypeRepresentation(MainPanelContainerType.class);

	       //Main
	       vapi.addComponent(BiogasControl.class);	
	       
	       //Structures
	       vapi.addComponent(Structure.class);
	       vapi.addComponent(STRUCT_1_STAGE.class);      
	       vapi.addComponent(STRUCT_2_STAGE.class); 
	       vapi.addComponent(STRUCT_3_STAGE.class); 
	       vapi.addComponent(STRUCT_2P_STAGE.class);
	       
	       //For the BiogasPlotter
	       vapi.addComponent(MainPanelContainerType.class);
	       vapi.addComponent(PlotDisplay.class);
	       vapi.addComponent(BiogasPlotter.class);
	       
	       //For user defined strucutres
	       vapi.addComponent(BiogasUserControl.class);
	       
	       //User elements
	       vapi.addComponent(UserStart.class);	
	       vapi.addComponent(UserHydrolysis.class);	 
	       vapi.addComponent(UserStorage.class);
	       vapi.addComponent(UserMethane.class);	       
	   }
	}

	@Override
	public void unregister(PluginAPI api) {
	   // nothing to unregister
	}

	@Override
	public void init(InitPluginAPI iApi) {
	   // nothing to init
	}
}
