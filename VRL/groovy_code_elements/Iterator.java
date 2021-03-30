package vrl.biogas.biogascontrol;
@ComponentInfo(name="Iterator", category="Biogas")
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
		options = "invokeOnChange=true")Object a){
	panel.running.setSelected(true);  
    
	int starttime = (Integer) panel.settingsPanelObj.simStarttime.getValue();
	int endtime = (Integer) panel.settingsPanelObj.simEndtime.getValue(); 

	panel.iteration = 0;    
	panel.currenttime = starttime; 
	System.out.println("Time " + starttime + " --> " + endtime);
	while(panel.currenttime < endtime) {
		System.out.println("Current: " + starttime);
		panel.simulationPanelObj.iteration.setText(String.valueOf(panel.iteration));
		a.run();  
		endtime = (Integer) panel.settingsPanelObj.simEndtime.getValue();
		++ panel.currenttime;
		++ panel.iteration;
	}
		
	panel.running.setSelected(false); 
	}
}

