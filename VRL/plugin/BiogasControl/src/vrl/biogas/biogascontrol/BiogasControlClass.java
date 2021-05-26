package vrl.biogas.biogascontrol;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vrl.biogas.biogascontrol.panels.FeedbackPanel;
import vrl.biogas.biogascontrol.panels.FeedingPanel;
import vrl.biogas.biogascontrol.panels.SettingsPanel;
import vrl.biogas.biogascontrol.panels.SetupPanel;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

/**
 * Parent class for the main panels {@link vrl.biogas.biogascontrol.BiogasControl}/{@link vrl.biogas.biogascontrol.BiogasUserControl}
 * 
 * @author Paul Zügel
 */
public class BiogasControlClass {
		
	private final int ONE_SECOND = 1000;
	public static final Color BUTTON_BLUE = Color.decode("#F0F6FF");
	public static final Color BUTTON_GREY = Color.decode("#C1C4C9");
	public static final Border BORDER = BorderFactory.createLineBorder(Color.BLACK);
	
	public static File projectPath;
	public static int currentTime;
	public static int iteration;
	public static JPanel panel;
	public static File workingDirectory;
	public static String simulationFile;

	public static JToggleButton pauseBtn;
	public static JToggleButton stopBtn;
	public static JButton startBtn;
	public static JButton breakBtn;
	
	public static JCheckBox running;
	public static Timer timer;
	public static long timerStartTime;
	
	public static SetupPanel setupPanelObj;
	public static SimulationPanel simulationPanelObj;
	public static SettingsPanel settingsPanelObj;
	public static FeedbackPanel feedbackPanelObj;	
	public static FeedingPanel feedingPanelObj;	
	
	BiogasControlClass(){		
		pauseBtn = new JToggleButton("Pause");
		pauseBtn.setBackground(BUTTON_BLUE);
		pauseBtn.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(pauseBtn.isSelected()) {
					pauseBtn.setBackground(BUTTON_GREY);
				} else {
					pauseBtn.setBackground(BUTTON_BLUE);
				}			
			}		
		});
				
        stopBtn = new JToggleButton("Stop");
        stopBtn.setBackground(BUTTON_BLUE);
        stopBtn.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {
				if(stopBtn.isSelected()) {
					stopBtn.setBackground(BUTTON_GREY);
				} else {
					stopBtn.setBackground(BUTTON_BLUE);
				}			
			}		
		});

		timer = new Timer(ONE_SECOND, new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
            	long currentTime = System.currentTimeMillis();
            	long timeElapsed = currentTime - timerStartTime;
            	Date date = new Date(timeElapsed);
				DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
				formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
				String timeFormatted = formatter.format(date);;
            	simulationPanelObj.runtime.setText(timeFormatted);
            }
        });
		
		running = new JCheckBox("running?", false);
		running.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				boolean isRunning = running.isSelected();
				System.out.println("Running?: " + isRunning);
				
				if(!isRunning) {					
					timer.stop();
					//int endtime = (Integer) settingsPanelObj.simEndtime.getValue();
					//settingsPanelObj.simStarttime.setValue(endtime); //Moved into stop functions
					feedingPanelObj.nextTimestep.setEnabled(false);
					setupPanelObj.createBtn.setEnabled(true);
					setupPanelObj.loadBtn.setEnabled(true);
					setupPanelObj.openBtn.setEnabled(true);
					setupPanelObj.clearBtn.setEnabled(true);	
					startBtn.setEnabled(true);
				} else {
					timerStartTime = System.currentTimeMillis();
					timer.start();					
					settingsPanelObj.simStarttime.setEnabled(false);
					feedingPanelObj.nextTimestep.setEnabled(true);
					setupPanelObj.createBtn.setEnabled(false);
					setupPanelObj.loadBtn.setEnabled(false);
					setupPanelObj.openBtn.setEnabled(false);
					setupPanelObj.clearBtn.setEnabled(false);
					startBtn.setEnabled(false);
				}
			}	
		});
		
		breakBtn = new JButton("Break");
		breakBtn.setBackground(BUTTON_BLUE);
        breakBtn.setForeground(Color.RED);
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
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		startBtn = new JButton("Start");
        startBtn.setBackground(BUTTON_BLUE);
	}
}
