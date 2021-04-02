package vrl.biogas.biogascontrol;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
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
import vrl.biogas.biogascontrol.panels.SettingsPanel;
import vrl.biogas.biogascontrol.panels.SetupPanel;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

@ComponentInfo(name="MainPanel_User", 
	category="Biogas", 
	description="MainPanel Component")
@ObjectInfo(name = "BiogasUserControl")
public class BiogasUserControlPlugin extends BiogasControl implements Serializable{
	private static final long serialVersionUID = 1L;
	public static int numHydrolysis;
	
	@MethodInfo(name="Main", hide=false,
			hideCloseIcon=true, interactive=true, num=1)
	public JComponent controlSTRUCT(
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
		numHydrolysis = numHydro;
		running = new JCheckBox("running?", false);
		projectPath = new File(projectDir.toString()).getParentFile();
		System.out.println("projectPath: " + projectPath);
		className =  BiogasUserControlPlugin.class.getSimpleName();	
		simulationPanelObj = new SimulationPanel(true);
		setupPanelObj = new SetupPanel(true);
		settingsPanelObj = new SettingsPanel(true);
		feedbackPanelObj = new FeedbackPanel(true);
		
        JPanel simulationPanel = simulationPanelObj.getPanel();
        JPanel setupPanel = setupPanelObj.getPanel();
        JPanel settingsPanel = settingsPanelObj.getPanel();
        JPanel feedbackPanel = feedbackPanelObj.getPanel();
        JPanel feedingPanel = new JPanel();
        
        JTabbedPane tab_panel = new JTabbedPane();
        
        tab_panel.addTab("Simulation", simulationPanel);
        tab_panel.addTab("Setup", setupPanel);
        tab_panel.addTab("Settings", settingsPanel);
        tab_panel.addTab("Feedback", feedbackPanel);
        tab_panel.addTab("Feeding", feedingPanel);   

        panel = new JPanel();
        double size[][] =
            {{0.02, 0.23, 0.01, 0.23, 0.01, 0.23, 0.01, 0.23, 0.01, TableLayoutConstants.FILL},
             {0.04, 0.06, 0.03, 0.82, TableLayoutConstants.FILL}};
        startBtn = new JButton("Start");
        startBtn.setBackground(BUTTON_BLUE);
        pauseBtn = new JToggleButton("Pause");
        pauseBtn.setBackground(BUTTON_BLUE);
        stopBtn = new JToggleButton("Stop");
        stopBtn.setBackground(BUTTON_BLUE);
        breakBtn = new JButton("Break");
        breakBtn.setBackground(BUTTON_BLUE);
        breakBtn.setForeground(Color.RED);
        panel.setLayout(new TableLayout(size));
        
        panel.add(startBtn, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        panel.add(pauseBtn, new TableLayoutConstraints(3, 1, 3, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        panel.add(stopBtn, new TableLayoutConstraints(5, 1, 5, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        panel.add(breakBtn, new TableLayoutConstraints(7, 1, 7, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        panel.add(tab_panel, new TableLayoutConstraints(1, 3, 7, 3, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        
        //MainPanelContainerType cont = new MainPanelContainerType();
	    //cont.setViewValue(panel);
	    
        /*
	    startBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(SetupPanel.environment_ready) {
					simulationFile = SettingsPanel.simulation_path.getText();
					SimulationPanel.simulationLog.setText("**************************************\n" 
							+ "** New Simulation\n"
							+ "** Hydrolysis File: " + SettingsPanel.hydrolysis_path.getText() + "\n"
							+ "** Methane File: " + SettingsPanel.methane_path.getText() + "\n"
							+ "** Simulation File: " + simulationFile + "\n"
							+ "**************************************\n");
					int starttime = (Integer) SettingsPanel.simStarttime.getValue();
					SettingsPanel.simStarttime.setEnabled(false);
					iteration = 0;							
					//SimulationPanel.iteration.setText(String.valueOf(iteration));
					
					
					try {
						running.setSelected(true);
						struct.run(starttime);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} 
					
				}
				else {
					JFrame frame = new JFrame();
					frame.setLocationRelativeTo(panel);
					frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
					JOptionPane.showMessageDialog(frame,
						    "You need to set up a working environment before starting the simulation.",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
				}
			}
	    });
	    */
        
        
		breakBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				running.setSelected(false);
				String os = System.getProperty("os.name");
				String killCmd = "";
				if(os.equals("Linux")) {
					killCmd = "killall -2 ugshell";
				} else if(os.equals("Mac")) {
					killCmd = "killall -2 ugshell";
				} else if (os.equals("Windows")) {
					killCmd = "taskkill /IM ugshell.exe /F";		
				}
				
				try {
					Runtime.getRuntime().exec(killCmd);
					//struct.cancelRun();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		
		running.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				boolean isRunning = running.isSelected();
				System.out.println("Running?: " + isRunning);
				
				if(!isRunning) {
					settingsPanelObj.simStarttime.setEnabled(true);
					SetupPanel.clear_Btn.doClick();
				} else {
					settingsPanelObj.simStarttime.setEnabled(false);
				}
			}	
		});
		
	    return panel;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{         	
		File f = new File("/home/paul/Schreibtisch/Biogas_plant_setup/VRL/Biogas_plant_setup.vrlp");
		Path p = Paths.get(f.getPath());
		
	    JFrame frame = new JFrame();
	    BiogasUserControlPlugin userControl = new BiogasUserControlPlugin();
	    userControl.control(p, 2);	    
	    
		frame.add(panel);
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);	
	}
}
