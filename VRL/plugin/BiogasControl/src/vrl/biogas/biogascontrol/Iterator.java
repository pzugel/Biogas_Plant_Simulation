package vrl.biogas.biogascontrol;

import java.lang.reflect.InvocationTargetException;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;

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
			options = "invokeOnChange=true")BiogasUserControl panel,
	@ParamInfo(name = "Structure",
			nullIsValid = false,
			options = "invokeOnChange=true")Object structure) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException{
		 
		int starttime = (Integer) BiogasUserControl.settingsPanelObj.simStarttime.getValue();
		int endtime = (Integer) BiogasUserControl.settingsPanelObj.simEndtime.getValue(); 
		
		Method[] methods = structure.getClass().getMethods();
		Method runMethod = methods[0];
	    
		BiogasUserControl.iteration = 0;    
		BiogasUserControl.currenttime = starttime; 
		
		boolean isReady = BiogasUserControl.setupPanelObj.environment_ready;
		if(isReady) {
			BiogasUserControl.running.setSelected(true); 
			
			System.out.println("Time " + starttime + " --> " + endtime);
			while(BiogasUserControl.currenttime < endtime) {
				System.out.println("Current: " + starttime);
				BiogasUserControl.simulationPanelObj.iteration.setText(String.valueOf(BiogasUserControl.iteration));
				runMethod.invoke(structure);  
				endtime = (Integer) BiogasUserControl.settingsPanelObj.simEndtime.getValue();
				++ BiogasUserControl.currenttime;
				++ BiogasUserControl.iteration;
			}
		    
			BiogasUserControl.running.setSelected(false); 
		}
		else {
			JFrame frame = new JFrame();
			frame.setLocationRelativeTo(BiogasUserControl.panel);
			frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
			JOptionPane.showMessageDialog(frame,
				    "You need to set up a working environment before starting the simulation.",
				    "Warning",
				    JOptionPane.WARNING_MESSAGE);
		}
		
	}
}
