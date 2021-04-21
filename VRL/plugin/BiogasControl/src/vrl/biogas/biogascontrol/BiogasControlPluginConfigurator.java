package vrl.biogas.biogascontrol;

import eu.mihosoft.vrl.io.VersionInfo;
import eu.mihosoft.vrl.system.InitPluginAPI;
import eu.mihosoft.vrl.system.PluginAPI;
import eu.mihosoft.vrl.system.PluginDependency;
import eu.mihosoft.vrl.system.PluginIdentifier;
import eu.mihosoft.vrl.system.VPluginAPI;
import eu.mihosoft.vrl.system.VPluginConfigurator;
import vrl.biogas.biogascontrol.outputloader.BiogasOutputMainPanel;
import vrl.biogas.biogascontrol.outputloader.BiogasPlotter;
import vrl.biogas.biogascontrol.structures.*;
import vrl.biogas.biogascontrol.userstructure.UserHydrolysis;
import vrl.biogas.biogascontrol.userstructure.UserStart;

/*
 * Eclipse: Export as runnable JAR
 * -> Package required libraries into generated JAR
 */
public class BiogasControlPluginConfigurator extends VPluginConfigurator{
	public BiogasControlPluginConfigurator() {
	    //specify the plugin name and version
	   setIdentifier(new PluginIdentifier("MainPanel-Plugin", "0.1"));
       
	   // describe the plugin
	   setDescription("Plugin for the control of a UG4-Biogasmodel");

	   // copyright info
	   setCopyrightInfo("Sample-Plugin",
	           "(c) Paul Zügel - GCSC 2021",
	           "https://github.com/pzugel/Biogas_Plant_Simulation", "GNU Lesser General Public License", 
	           "This program is distributed in the hope that it will be useful,\n" + 
	           "but WITHOUT ANY WARRANTY; without even the implied warranty of\n" + 
	           "MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the\n" + 
	           "GNU Lesser General Public License for more details.");
	   
	   // add dependency - DO NOT REMOVE
	   addDependency(new PluginDependency("VRL-JFreeChart", "0.2.5.1", VersionInfo.UNDEFINED)); 
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
	       vapi.addComponent(MainPanelContainerType.class); // Check if needed
	       vapi.addComponent(BiogasPlotter.class);
	       vapi.addComponent(BiogasOutputMainPanel.class);
	       
	       //For user defined strucutres
	       vapi.addComponent(BiogasUserControl.class);
	       vapi.addComponent(UserStart.class);	
	       vapi.addComponent(UserHydrolysis.class);	                     
	       vapi.addComponent(Iterator.class);
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
