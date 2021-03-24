package vrl.biogas.biogascontrol;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JToggleButton;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import layout.TableLayout;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.panels.FeedbackPanel;
import vrl.biogas.biogascontrol.panels.SettingsPanel;
import vrl.biogas.biogascontrol.panels.SetupPanel;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

@ComponentInfo(name="MainPanel_User", 
	category="Biogas", 
	description="MainPanel Component")
public class BiogasUserControlPlugin implements Serializable{
	private static final long serialVersionUID = 1L;
	public static final Color BUTTON_BLUE = Color.decode("#F0F6FF");
	public static final Border border = BorderFactory.createLineBorder(Color.black);
	
	public static File projectPath;
	public static int currenttime;
	public static int iteration;
	static public JPanel panel;
	public static File workingDirectory;
	public static String simulationFile;

	public static JToggleButton pauseBtn;
	public static JToggleButton stopBtn;
	public static JButton startBtn;
	public static JButton breakBtn;
	
	public static JCheckBox running;
	
	public static SimulationPanel simulationPanelObj;
	public static SetupPanel setupPanelObj;
	public static SettingsPanel settingsPanelObj;
	public static FeedbackPanel feedbackPanelObj;
	//public static FeedingPanel feedingPanelObj;
	
    JTabbedPane tab_panel = new JTabbedPane();
	
	@MethodInfo(name="Main", hide=false,
			hideCloseIcon=true, interactive=true, num=1)
	public static JComponent controlSTRUCT(
			@ParamInfo(name = "Directory",
	    		nullIsValid = false,
	    		options = "invokeOnChange=true") Path projectDir)
		    			throws IOException, InterruptedException{
		control(projectDir);
		MainPanelContainerType cont = new MainPanelContainerType();
	    cont.setViewValue(panel);
		return cont;		
	}
	
	@MethodInfo(hide=true)
	private static JPanel control(
			@ParamInfo(name = "Directory",
	    		nullIsValid = false,
	    		options = "invokeOnChange=true") Path projectDir)
		    			throws IOException, InterruptedException{
		running = new JCheckBox("running?", false);
		projectPath = new File(projectDir.toString()).getParentFile();
		System.out.println("projectPath: " + projectPath);

		simulationPanelObj = new SimulationPanel(true);
		setupPanelObj = new SetupPanel(true);
		settingsPanelObj = new SettingsPanel();
		feedbackPanelObj = new FeedbackPanel();
		
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
            {{0.02, 0.23, 0.01, 0.23, 0.01, 0.23, 0.01, 0.23, 0.01, TableLayout.FILL},
             {0.04, 0.06, 0.03, 0.82, TableLayout.FILL}};
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
        
        panel.add(startBtn, new TableLayoutConstraints(1, 1, 1, 1, TableLayout.FULL, TableLayout.FULL));
        panel.add(pauseBtn, new TableLayoutConstraints(3, 1, 3, 1, TableLayout.FULL, TableLayout.FULL));
        panel.add(stopBtn, new TableLayoutConstraints(5, 1, 5, 1, TableLayout.FULL, TableLayout.FULL));
        panel.add(breakBtn, new TableLayoutConstraints(7, 1, 7, 1, TableLayout.FULL, TableLayout.FULL));
        panel.add(tab_panel, new TableLayoutConstraints(1, 3, 7, 3, TableLayout.FULL, TableLayout.FULL));
        
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
					SettingsPanel.simStarttime.setEnabled(true);
					SetupPanel.clear_Btn.doClick();
				} else {
					SettingsPanel.simStarttime.setEnabled(false);
				}
			}	
		});
		
	    return panel;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{         	
		File f = new File("/home/paul/Schreibtisch/Biogas_plant_setup/VRL/Biogas_plant_setup.vrlp");
		Path p = Paths.get(f.getPath());
		
	    JFrame frame = new JFrame();

	    control(p);	    
	    
		frame.add(panel);
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);	
	}
}
