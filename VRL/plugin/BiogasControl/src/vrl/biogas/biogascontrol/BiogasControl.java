package vrl.biogas.biogascontrol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
import vrl.biogas.biogascontrol.structures.*;
import vrl.biogas.biogascontrol.structures.Structure;

/**
 * Main user interface to simulate predefined structures.<br>
 * Contains the JTabbedPane implementing the panels in {@link vrl.biogas.biogascontrol.panels}
 * @author Paul Zügel
 *
 */
@ComponentInfo(name="MainPanel", 
	category="Biogas", 
	description="MainPanel Component")
@ObjectInfo(name = "BiogasControl")
public class BiogasControl extends BiogasControlClass implements Serializable{
	private static final long serialVersionUID = 1L;
	static public Structure struct;
	
	@MethodInfo(name="Main", hide=false,
			hideCloseIcon=true, interactive=true, num=1)
	public JComponent mainControl(
			@ParamInfo(name = "Structure",
		    	nullIsValid = false,
		    	options = "invokeOnChange=false") Structure structure,
			@ParamInfo(name = "Directory",
	    		nullIsValid = false,
	    		options = "invokeOnChange=false") Path projectDir)
		    			throws IOException, InterruptedException{
		control(structure, projectDir);
		MainPanelContainerType cont = new MainPanelContainerType();
	    cont.setViewValue(panel);
	    
		return cont;		
	}
	
	private JPanel control(Structure structure, Path projectDir) throws IOException, InterruptedException{
		struct = structure;
		projectPath = new File(projectDir.toString()).getParentFile();
		System.out.println("projectPath: " + projectPath);
				
		simulationPanelObj = new SimulationPanel();
		setupPanelObj = new SetupPanel();
		settingsPanelObj = new SettingsPanel();
		feedbackPanelObj = new FeedbackPanel();
		feedingPanelObj = new FeedingPanel();
		
        JPanel simulationPanel = simulationPanelObj.getPanel();
        JPanel setupPanel = setupPanelObj.getPanel();
        JPanel settingsPanel = settingsPanelObj.getPanel();
        //JPanel structurePanel = (new StructurePanel()).getPanel();
        JPanel feedbackPanel = feedbackPanelObj.getPanel();
        JPanel feedingPanel = feedingPanelObj.getPanel();
        JTabbedPane tab_panel = new JTabbedPane();	
        
        tab_panel.addTab("Simulation", simulationPanel);
        tab_panel.addTab("Setup", setupPanel);
        tab_panel.addTab("Settings", settingsPanel);
        //tab_panel.addTab("Structure", structurePanel);
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
				if(setupPanelObj.environment_ready) {
					simulationFile = settingsPanelObj.simulation_path.getText();
					String logText = "**************************************\n";
					if(setupPanelObj.mergePreexisting) {
						logText += "** Continue Simulation\n";
					}
					else {
						logText += "** New Simulation\n";
					}
					logText += "** Hydrolysis File: " + settingsPanelObj.hydrolysis_path.getText() + "\n"
							+ "** Methane File: " + settingsPanelObj.methane_path.getText() + "\n"
							+ "** Simulation File: " + simulationFile + "\n"
							+ "**************************************\n";
					simulationPanelObj.simulationLog.setText(logText);
					int starttime = (Integer) settingsPanelObj.simStarttime.getValue();
					iteration = 0;												
					simulationPanelObj.iteration.setText("0");
					
					try {
						running.setSelected(true);
						struct.run(starttime);
					} catch (IOException e) {
						e.printStackTrace();
					} 
					
				}
				else {
					JFrame frame = new JFrame();
					frame.setLocationRelativeTo(panel);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					JOptionPane.showMessageDialog(frame,
						    "You need to set up a working environment before starting the simulation.",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
				}
			}
	    });
	    
		breakBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				struct.cancelRun();
			}
		});
		
		running.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				boolean isRunning = running.isSelected();
				if(!isRunning) {
					startBtn.setEnabled(true);
				} else {
					startBtn.setEnabled(false);
				}
			}
		});
		
	    return panel;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{
		JFrame frame = new JFrame();
	    BiogasControl b = new BiogasControl();
	    
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogTitle("Select the project folder.");
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);	
		
		int result = fileChooser.showOpenDialog(panel);
		if (result == JFileChooser.APPROVE_OPTION) {
			File selectedPath = fileChooser.getSelectedFile();
			File simFiles = new File(selectedPath, "simulation_files");
			if(simFiles.isDirectory()) {
				Path p = Paths.get(selectedPath.getPath());
				b.mainControl(new STRUCT_1_STAGE(), p);	    

				frame.add(panel);
				frame.setSize(600, 600);
				frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				frame.setVisible(true);	
			} else {
				System.out.println("Could not find \"simulation_files\" folder in the choosen directory.");
			}		
		} else {
			System.out.println("Not a valid path!");
		}
	}
	
}
