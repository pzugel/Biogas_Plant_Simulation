package vrl.biogas.biogascontrol;

import java.awt.Color;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.border.Border;

import vrl.biogas.biogascontrol.panels.FeedbackPanel;
import vrl.biogas.biogascontrol.panels.SettingsPanel;
import vrl.biogas.biogascontrol.panels.SetupPanel;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

public class BiogasControl {
	
	public static final Color BUTTON_BLUE = Color.decode("#F0F6FF");
	public static final Border border = BorderFactory.createLineBorder(Color.black);
	
	public static File projectPath;
	public static int currenttime;
	public static int iteration;
	public static JPanel panel;
	public static File workingDirectory;
	public static String simulationFile;

	public static JToggleButton pauseBtn;
	public static JToggleButton stopBtn;
	public static JButton startBtn;
	public static JButton breakBtn;
	
	public static JCheckBox running;
	
	public static SetupPanel setupPanelObj;
	public static SimulationPanel simulationPanelObj;
	public static SettingsPanel settingsPanelObj;
	public static FeedbackPanel feedbackPanelObj;
	
	public static String className;
}
