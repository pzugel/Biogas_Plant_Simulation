package vrl.biogas.biogascontrol;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import layout.TableLayout;
import layout.TableLayoutConstants;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.panels.FeedbackPanel;
import vrl.biogas.biogascontrol.panels.FeedingPanel;
import vrl.biogas.biogascontrol.panels.SettingsPanel;
import vrl.biogas.biogascontrol.panels.SetupPanel;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

@ComponentInfo(name="MainPanel_User", 
	category="Biogas", 
	description="MainPanel Component")
@ObjectInfo(name = "BiogasUserControl")
public class BiogasUserControl extends BiogasControlClass implements Serializable{
	private static final long serialVersionUID = 1L;
	public static int numHydrolysis;
	public static boolean wasCancelled;
	
	@MethodInfo(name="Main", hide=false,
			hideCloseIcon=true, interactive=true, num=1)
	public JComponent mainControl(
			@ParamInfo(name = "Directory",
	    		nullIsValid = false,
	    		options = "invokeOnChange=true") Path projectDir,
			@ParamInfo(name = "#Hydrolysis",
				nullIsValid = false,
				options = "invokeOnChange=true") int numHydrolysis)
		    			throws IOException, InterruptedException{
		control(projectDir, numHydrolysis);
		MainPanelContainerType cont = new MainPanelContainerType();
	    cont.setViewValue(panel);
		return cont;		
	}
	
	@MethodInfo(hide=true)
	private JPanel control(Path projectDir,int numHydro) throws IOException, InterruptedException{
		wasCancelled = false;
		numHydrolysis = numHydro;		
		projectPath = new File(projectDir.toString()).getParentFile();
		System.out.println("projectPath: " + projectPath);

		simulationPanelObj = new SimulationPanel(true);
		setupPanelObj = new SetupPanel(true);
		settingsPanelObj = new SettingsPanel(true);
		feedbackPanelObj = new FeedbackPanel(true);
		feedingPanelObj = new FeedingPanel(true);
		
        JPanel simulationPanel = simulationPanelObj.getPanel();
        JPanel setupPanel = setupPanelObj.getPanel();
        JPanel settingsPanel = settingsPanelObj.getPanel();
        JPanel feedbackPanel = feedbackPanelObj.getPanel();
        JPanel feedingPanel = feedingPanelObj.getPanel();       
        JTabbedPane tab_panel = new JTabbedPane();
        
        tab_panel.addTab("Simulation", simulationPanel);
        tab_panel.addTab("Setup", setupPanel);
        tab_panel.addTab("Settings", settingsPanel);
        tab_panel.addTab("Feedback", feedbackPanel);
        tab_panel.addTab("Feeding", feedingPanel);   

        panel = new JPanel();
        double size[][] =
            {{0.02, 0.31, 0.01, 0.31, 0.01, 0.31, 0.01, TableLayoutConstants.FILL},
             {0.04, 
            	0.06, //Buttons
            	0.03, 
            	0.82, //Panel
            	TableLayoutConstants.FILL}};

        
        panel.setLayout(new TableLayout(size));
        
        panel.add(pauseBtn, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        panel.add(stopBtn, new TableLayoutConstraints(3, 1, 3, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        panel.add(breakBtn, new TableLayoutConstraints(5, 1, 5, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        panel.add(tab_panel, new TableLayoutConstraints(1, 3, 5, 3, TableLayoutConstants.FULL, TableLayoutConstants.FULL));        	
		
	    return panel;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{         	
		File f = new File("/home/paul/Schreibtisch/Biogas_plant_setup/VRL/Biogas_plant_setup.vrlp");
		Path p = Paths.get(f.getPath());
		
	    JFrame frame = new JFrame();
	    BiogasUserControl userControl = new BiogasUserControl();
	    userControl.control(p, 2);	    
	    
		frame.add(panel);
		frame.setSize(500, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);			
	}
}
