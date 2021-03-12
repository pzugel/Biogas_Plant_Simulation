package vrl.biogas.biogascontrol;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import layout.TableLayout;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.structures.STRUCT_2_STAGE;

@ComponentInfo(name="MainPanel", 
	category="Biogas", 
	description="MainPanel Component")
public class BiogasControlPlugin implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final Color BUTTON_BLUE = Color.decode("#F0F6FF");
	
	static File projectPath;
	static JPanel panel;
	static File workingDirectory;
	static Structure struct;
	
	@MethodInfo(name="Main", hide=false,
			hideCloseIcon=true, interactive=true, num=1)
	public static JComponent control(
			@ParamInfo(name = "Structure",
		    	nullIsValid = false,
		    	options = "invokeOnChange=true") Structure structure,
			@ParamInfo(name = "Directory",
	    		nullIsValid = false,
	    		options = "invokeOnChange=true") Path projectDir)
		    			throws IOException, InterruptedException{
		struct = structure;
		projectPath = new File(projectDir.toString()).getParentFile();
		System.out.println("projectPath: " + projectPath);
        JPanel simulationPanel = (new SimulationPanel()).getPanel();
        JPanel setupPanel = (new SetupPanel()).getPanel();
        JPanel settingsPanel = (new SettingsPanel()).getPanel();
        JPanel feedbackPanel = new JPanel();
        JPanel feedingPanel = new JPanel();

        JTabbedPane tab_panel = new JTabbedPane
            (JTabbedPane.TOP,JTabbedPane.SCROLL_TAB_LAYOUT);
 
        tab_panel.addTab("Simulation", simulationPanel);
        tab_panel.addTab("Setup", setupPanel);
        tab_panel.addTab("Settings", settingsPanel);
        tab_panel.addTab("Feedback", feedbackPanel);
        tab_panel.addTab("Feeding", feedingPanel);
        //tab_panel.setSize(510, 540);     

        panel = new JPanel();
    	//panel.setSize(340, 330);
        double size[][] =
            {{0.02, 0.23, 0.01, 0.23, 0.01, 0.23, 0.01, 0.23, 0.01, TableLayout.FILL},
             {0.04, 0.06, 0.03, 0.82, TableLayout.FILL}};
        JButton startBtn = new JButton("Start");
        startBtn.setBackground(BUTTON_BLUE);
        JButton pauseBtn = new JButton("Pause");
        pauseBtn.setBackground(BUTTON_BLUE);
        JButton stopBtn = new JButton("Stop");
        stopBtn.setBackground(BUTTON_BLUE);
        JButton plotBtn = new JButton("Plot");
        plotBtn.setBackground(BUTTON_BLUE);

        panel.setLayout(new TableLayout(size));
        
        panel.add(startBtn, new TableLayoutConstraints(1, 1, 1, 1, TableLayout.FULL, TableLayout.FULL));
        panel.add(pauseBtn, new TableLayoutConstraints(3, 1, 3, 1, TableLayout.FULL, TableLayout.FULL));
        panel.add(stopBtn, new TableLayoutConstraints(5, 1, 5, 1, TableLayout.FULL, TableLayout.FULL));
        panel.add(plotBtn, new TableLayoutConstraints(7, 1, 7, 1, TableLayout.FULL, TableLayout.FULL));
        panel.add(tab_panel, new TableLayoutConstraints(1, 3, 7, 3, TableLayout.FULL, TableLayout.FULL));
        
        MainPanelContainerType cont = new MainPanelContainerType();
	    cont.setViewValue(panel);
	    return cont;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{         	
	   
		//String dir = System.getProperty("user.dir");
		//File f = new File(dir);
		//f = f.getParentFile();
		//f = f.getParentFile();
		File f = new File("/home/paul/Schreibtisch/Biogas_plant_setup/VRL/Biogas_plant_setup.vrlp");
		Path p = Paths.get(f.getPath());
		
	    JFrame frame = new JFrame();

	    control(new STRUCT_2_STAGE(), p);
	    
	    
		frame.add(panel);
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);		
	}
}
