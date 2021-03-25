package vrl.biogas.biogascontrol;

import eu.mihosoft.vrl.system.InitPluginAPI;
import eu.mihosoft.vrl.system.PluginAPI;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.VPluginAPI;
import eu.mihosoft.vrl.system.VPluginConfigurator;
import vrl.biogas.biogascontrol.elements.SimulationElement;
import vrl.biogas.biogascontrol.structures.*;
import vrl.biogas.biogascontrol.userstructure.Hydrolysis;

public class BiogasControlPluginConfigurator extends VPluginConfigurator{
	public BiogasControlPluginConfigurator() {
	    //specify the plugin name and version
	   setIdentifier(new PluginIdentifier("MainPanel-Plugin", "0.1"));

	   // describe the plugin
	   setDescription("Plugin Description");

	   // copyright info
	   setCopyrightInfo("Sample-Plugin",
	           "(c) Your Name",
	           "www.you.com", "License Name", "License Text...");
	}

	@Override
	public void register(PluginAPI api) {

	   // register plugin with canvas
	   if (api instanceof VPluginAPI) {
	       VPluginAPI vapi = (VPluginAPI) api;
	       vapi.addComponent(BiogasControlPlugin.class);
	       vapi.addTypeRepresentation(MainPanelContainerType.class);
	       vapi.addComponent(STRUCT_2_STAGE.class);
	       vapi.addComponent(Structure.class);
	       vapi.addComponent(SimulationElement.class);
	       
	       vapi.addComponent(MainPanelContainerType.class); //TEST
	       vapi.addComponent(UserStructure.class);
	       vapi.addComponent(BiogasUserControlPlugin.class);
	       vapi.addComponent(Hydrolysis.class);
	       
	       vapi.addComponent(BiogasControl.class); //Not sure if needed
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
