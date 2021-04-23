package vrl.biogas.biogascontrol.elements.userElements;

import java.io.IOException;
import java.io.Serializable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.elements.functions.ElementHelperFunctions;

@ComponentInfo(name="Pause", 
	category="Biogas_UserElements")
public class UserPause extends ElementHelperFunctions implements Serializable{
	private static final long serialVersionUID=1L;
	
	@MethodInfo(name="Main", hide=false, hideCloseIcon=false, interactive=false, num=1)
	public void run() throws InterruptedException, IOException{
		
		if(!BiogasUserControl.wasCancelled) {
			BiogasUserControl.simulationPanelObj.activeElement.setText("Pause");
			log("** Pause ... ");
			while(BiogasUserControl.pauseBtn.isSelected())
			{
				try 
				{
					Thread.sleep(1000);
					BiogasUserControl.timer.stop();
					BiogasUserControl.timerStartTime = BiogasUserControl.timerStartTime+1000;
				} 
				catch (InterruptedException e) 
				{
					e.printStackTrace();
				}                     
			}
			
			Thread.sleep(2000);
			log("Continue!\n");
		}
	}
}
