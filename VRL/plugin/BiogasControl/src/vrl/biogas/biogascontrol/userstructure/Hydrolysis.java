package vrl.biogas.biogascontrol.userstructure;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasUserControlPlugin;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

@ComponentInfo(name="Hydrolysis", category="BiogasUserElements")
public class Hydrolysis implements java.io.Serializable{
	private static final long serialVersionUID=1L;
	
	@MethodInfo(name="Main", hide=false,
			hideCloseIcon=true, interactive=true, num=1)
	public void run() throws InterruptedException{
		SimulationPanel simPanel = BiogasControl.simulationPanelObj;
		
		String logBefore = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logBefore + "Hydrolysis ... ");
		
		simPanel.activeElement.setText("Hydrolysis");
		Thread.sleep(1000);

		String logAfter = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logAfter + "Done!\n");
	}
}
