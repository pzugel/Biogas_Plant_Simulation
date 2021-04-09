package vrl.biogas.biogascontrol;

import java.lang.reflect.InvocationTargetException;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import vrl.biogas.biogascontrol.panels.SetupPanel;

import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

@ComponentInfo(name="SimulationIterator", 
	category="Biogas", 
	description="SimulationIterator component")
@ObjectInfo(name = "SimulationIterator")
public class Iterator implements java.io.Serializable {
	private static final long serialVersionUID=1L;
  
	@MethodInfo(name="Run", hide=false,
			hideCloseIcon=true, interactive=true, num=1)
	public void run(
	@ParamInfo(name = "MainPanel",
			nullIsValid = false,
			options = "invokeOnChange=true")BiogasUserControlPlugin panel,
	@ParamInfo(name = "Structure",
			nullIsValid = false,
			options = "invokeOnChange=true")Object a) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		 
		int starttime = (Integer) BiogasUserControlPlugin.settingsPanelObj.simStarttime.getValue();
		int endtime = (Integer) BiogasUserControlPlugin.settingsPanelObj.simEndtime.getValue(); 
		
		Method[] methods = a.getClass().getMethods();
		Method runMethod = methods[0];
	    
		BiogasUserControlPlugin.iteration = 0;    
		BiogasUserControlPlugin.currenttime = starttime; 
		
		boolean isReady = SetupPanel.environment_ready;
		if(isReady) {
			BiogasUserControlPlugin.running.setSelected(true); 
			
			System.out.println("Time " + starttime + " --> " + endtime);
			while(BiogasUserControlPlugin.currenttime < endtime) {
				System.out.println("Current: " + starttime);
				BiogasUserControlPlugin.simulationPanelObj.iteration.setText(String.valueOf(BiogasUserControlPlugin.iteration));
				runMethod.invoke(a);  
				endtime = (Integer) BiogasUserControlPlugin.settingsPanelObj.simEndtime.getValue();
				++ BiogasUserControlPlugin.currenttime;
				++ BiogasUserControlPlugin.iteration;
			}
		    
			BiogasUserControlPlugin.running.setSelected(false); 
		}
		else {
			JFrame frame = new JFrame();
			frame.setLocationRelativeTo(BiogasUserControlPlugin.panel);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JOptionPane.showMessageDialog(frame,
				    "You need to set up a working environment before starting the simulation.",
				    "Warning",
				    JOptionPane.WARNING_MESSAGE);
		}
		
	}
}
