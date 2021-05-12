package vrl.biogas.biogascontrol;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import vrl.biogas.biogascontrol.elements.userStructureElements.execution.UserPause;
import vrl.biogas.biogascontrol.elements.userStructureElements.execution.UserStop;

import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Simulation iterator to be used with the {@link vrl.biogas.biogascontrol.BiogasUserControl}.<br>
 * Used to starts the simulation.
 * 
 * @author Paul Zügel
 *
 */
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
			options = "invokeOnChange=true")Object structure) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException, IOException{
		 
		int starttime = (Integer) BiogasUserControl.settingsPanelObj.simStarttime.getValue();
		int endtime = (Integer) BiogasUserControl.settingsPanelObj.simEndtime.getValue(); 
		
		Method[] methods = structure.getClass().getMethods();
		Method runMethod = methods[0];
	    
		BiogasUserControl.structureName = structure.getClass().getName();
		
		BiogasUserControl.iteration = 0;    
		BiogasUserControl.currentTime = starttime; 
		
		boolean isReady = BiogasUserControl.setupPanelObj.environment_ready;
		if(isReady) {
			BiogasUserControl.running.setSelected(true); 
			BiogasUserControl.wasCancelled = false;
			
			System.out.println("Time " + starttime + " --> " + endtime);
			while(BiogasUserControl.currentTime < endtime) {
				System.out.println("Current: " + BiogasUserControl.currentTime);
				BiogasUserControl.simulationPanelObj.iteration.setText(String.valueOf(BiogasUserControl.iteration));
				BiogasUserControl.feedingPanelObj.nextTimestep.setText(String.valueOf(BiogasUserControl.currentTime + 1));
				runMethod.invoke(structure);  
				endtime = (Integer) BiogasUserControl.settingsPanelObj.simEndtime.getValue();
				System.out.println("endtime before: " + endtime);
				
				//Pause-Stop
				new UserPause().run();
				new UserStop().run();
				if(!BiogasUserControl.running.isSelected()) {
					System.out.println("BREAK THE LOOP");
					break;
				}
				System.out.println("endtime after: " + endtime);
				++ BiogasUserControl.currentTime;
				++ BiogasUserControl.iteration;
			}
		    System.out.println("Finished!");
			//BiogasUserControl.running.setSelected(false); 
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
