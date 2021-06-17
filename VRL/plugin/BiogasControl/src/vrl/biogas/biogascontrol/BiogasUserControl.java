package vrl.biogas.biogascontrol;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

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

/**
 * Main user interface to simulate user defined structures.<br>
 * Contains the JTabbedPane implementing the panels in {@link vrl.biogas.biogascontrol.panels}.<br>
 * To be used in combination with the {@link vrl.biogas.biogascontrol.UserStructureIterator} class.
 * 
 * @author Paul Zügel
 *
 */
@ComponentInfo(name="MainPanel_User", 
	category="Biogas", 
	description="MainPanel Component")
@ObjectInfo(name = "BiogasUserControl")
public class BiogasUserControl extends BiogasControlClass implements Serializable{
	private static final long serialVersionUID = 1L;
	public static int numHydrolysis;
	public static boolean wasCancelled;
	public static String[] hydrolysisNames;
	public static String structureName;
	
	@MethodInfo(name="Main", hide=false,
			hideCloseIcon=true, interactive=true, num=1)
	public JComponent mainControl(
			@ParamInfo(name = "Directory",
	    		nullIsValid = false,
	    		options = "invokeOnChange=true") Path projectDir,
			@ParamInfo(name = "#Hydrolysis",
				nullIsValid = false,
				options = "invokeOnChange=true") int numHydrolysis,
			@ParamInfo(name = "UserStructure",
				style = "silent",
				nullIsValid = false,
				options = "invokeOnChange=true") Object structure)
		    			throws IOException, InterruptedException{
		control(projectDir, numHydrolysis, structure);
		MainPanelContainerType cont = new MainPanelContainerType();
	    cont.setViewValue(panel);
		return cont;		
	}
	
	private JPanel control(Path projectDir,int numHydro, final Object structure) throws IOException, InterruptedException{
		wasCancelled = false;
		numHydrolysis = numHydro;	
		structureName = "";
		projectPath = new File(projectDir.toString()).getParentFile();
		hydrolysisNames = new String[numHydro];
		for(int i=0; i<numHydro; i++) {
			hydrolysisNames[i] = "hydrolysis_" + String.valueOf(i);
		}
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
            {{0.02, 0.23, 0.01, 0.23, 0.01, 0.23, 0.01, 0.23, 0.01, TableLayoutConstants.FILL},
             {0.04, 
            	0.06, //Top buttons
            	0.03, 
            	0.82, //Tabbed pane
            	TableLayoutConstants.FILL}};
        panel.setLayout(new TableLayout(size));
        
        // Important to create new startButton --> Otherwise causes bugs when changing the structure
        startBtn = new JButton("Start");
        
        panel.add(startBtn, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        panel.add(pauseBtn, new TableLayoutConstraints(3, 1, 3, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        panel.add(stopBtn, new TableLayoutConstraints(5, 1, 5, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        panel.add(breakBtn, new TableLayoutConstraints(7, 1, 7, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        panel.add(tab_panel, new TableLayoutConstraints(1, 3, 7, 3, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
               
        startBtn.setBackground(BUTTON_BLUE);
        startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				final UserStructureIterator iterator = new UserStructureIterator();
				
				Thread thread = new Thread(){
					public void run(){
						try {
							iterator.run(structure);
				    	} catch (IllegalAccessException e) {
							e.printStackTrace();
						} catch (IllegalArgumentException e) {
							e.printStackTrace();
						} catch (InvocationTargetException e) {
							e.printStackTrace();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
				    }
				  };
				  thread.start();			
			};
        });
        
        breakBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				wasCancelled = true;
			}
		});
	    return panel;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{ 
		/*
		File f = new File("/home/paul/Schreibtisch/Biogas_plant_setup/VRL/Biogas_plant_setup.vrlp");
		Path p = Paths.get(f.getPath());
		
	    JFrame frame = new JFrame();
	    BiogasUserControl userControl = new BiogasUserControl();
	    userControl.control(p, 2);	    
	    
		frame.add(panel);
		frame.setSize(500, 600);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		frame.setVisible(true);	
		*/		
		
		JFrame frame = new JFrame();
		File projectPath = new File("/home/paul/Schreibtisch/Biogas_plant_setup/VRL");
		File iconPath = new File(projectPath, "icons");
		File hydroIcon_path = new File(iconPath, "hydrolysis_reactor.png");
		ImageIcon hydroIcon = new ImageIcon(hydroIcon_path.toString());
		System.out.println("hydroIcon_path: " + hydroIcon_path);
		
		JPanel returnPanel = new JPanel();
		returnPanel.add(new JLabel(hydroIcon));
		Dimension expectedDimension = new Dimension(50, 50);

		returnPanel.setPreferredSize(expectedDimension);
		returnPanel.setMaximumSize(expectedDimension);
		returnPanel.setMinimumSize(expectedDimension);
		
		Box box = new Box(BoxLayout.Y_AXIS);

        box.add(Box.createVerticalGlue());
        box.add(returnPanel);     
        box.add(Box.createVerticalGlue());
		
		
		frame.add(box);
		frame.setSize(100, 100);
		frame.setVisible(true);	
	}
}

