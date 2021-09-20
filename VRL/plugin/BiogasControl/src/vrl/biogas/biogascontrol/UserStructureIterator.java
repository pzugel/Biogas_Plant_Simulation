package vrl.biogas.biogascontrol;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import vrl.biogas.biogascontrol.elements.userStructureElements.execution.UserPause;
import vrl.biogas.biogascontrol.elements.userStructureElements.execution.UserStop;

import java.lang.reflect.Method;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Simulation iterator to be used with the {@link vrl.biogas.biogascontrol.BiogasUserControl}.<br>
 * Used to iterate a user defined structure.
 * 
 * @author Paul Zügel
 *
 */
public class UserStructureIterator implements java.io.Serializable {
	private static final long serialVersionUID=1L;

	public void run(Object structure) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InterruptedException, IOException{
		 
		int starttime = (Integer) BiogasUserControl.settingsPanelObj.simStarttime.getValue();
		int endtime = (Integer) BiogasUserControl.settingsPanelObj.simEndtime.getValue(); 
		
		Method[] methods = structure.getClass().getMethods();
		Method runMethod = methods[0];
		
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
				
				//Pause-Stop
				new UserPause().run();
				new UserStop().run();
				if(!BiogasUserControl.running.isSelected()) {
					System.out.println("BREAK THE LOOP");
					break;
				}

				++ BiogasUserControl.currentTime;
				++ BiogasUserControl.iteration;
			}
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
