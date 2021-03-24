package vrl.biogas.biogascontrol.panels;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import layout.TableLayout;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.BiogasControlPlugin;

public class SimulationPanel {
	static JPanel simulationPanel;
	static JTextField plantStructure;
	public static JTextField iteration;
	static JTextField runtime;
	static JTextField workingDirectory;
	
	public static JTextField activeElement;
	public static JTextArea simulationLog;
	
	public SimulationPanel() {
		simulationPanel = new JPanel();
		createPanel(false);
	}
	
	public SimulationPanel(boolean userDefined) {
		simulationPanel = new JPanel();
		createPanel(userDefined);
	}
	
	public JPanel getPanel() {
		return simulationPanel;
	}
	
	private void createPanel(boolean userDefined) {
		plantStructure = new JTextField(14);
		plantStructure.setPreferredSize(new Dimension(100,22));
		plantStructure.setEditable(false);
		plantStructure.setBackground(Color.WHITE);
		if(userDefined) {
			plantStructure.setText("User defined");
		} else {
			plantStructure.setText(BiogasControlPlugin.struct.name());
		}
				
		activeElement = new JTextField(10);
		activeElement.setPreferredSize(new Dimension(80,22));
		activeElement.setEditable(false);
		activeElement.setBackground(Color.WHITE);
		
		iteration = new JTextField(5);
		iteration.setPreferredSize(new Dimension(40,22));
		iteration.setEditable(false);
		iteration.setBackground(Color.WHITE);
		iteration.setText("0");
		iteration.setHorizontalAlignment(SwingConstants.RIGHT);
		
		runtime = new JTextField(6);
		runtime.setPreferredSize(new Dimension(80,22));
		runtime.setEditable(false);
		runtime.setBackground(Color.WHITE);
		runtime.setText("00:00:00");
		runtime.setHorizontalAlignment(SwingConstants.RIGHT);
		
		workingDirectory = new JTextField(42);
		workingDirectory.setPreferredSize(new Dimension(400,22));
		workingDirectory.setEditable(false);
		workingDirectory.setBackground(Color.WHITE);
		
		simulationLog = new JTextArea(22,46);
		
		JScrollPane simulationLogScroll = new JScrollPane (simulationLog, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		
		JLabel plantStructureLabel = new JLabel("Plant structure");
		plantStructureLabel.setLabelFor(plantStructure);

		JLabel activeElementLabel = new JLabel("Active element");
		activeElementLabel.setLabelFor(activeElement);
		
		JLabel iterationLabel = new JLabel("Iteration");
		iterationLabel.setLabelFor(iteration);
		
		JLabel runtimeLabel = new JLabel("Runtime");
		runtimeLabel.setLabelFor(runtime);

		JLabel workingDirectoryLabel = new JLabel("Working directory");
		workingDirectoryLabel.setLabelFor(workingDirectory);
		
		JLabel simulationLogLabel = new JLabel("Simulation log");
		simulationLogLabel.setLabelFor(simulationLog);
        
        simulationPanel.setLayout(new GridBagLayout());   
        
        double size[][] =
            {{0.06, 0.3, 0.01, 0.22, 0.01, 0.15, 0.01, 0.15, TableLayout.FILL},
             {0.06, 
            	0.05, //Label 1
            	0.06, //TextField 2
            	0.03, 
            	0.05, //Label 4
            	0.06, //TextField 5
            	0.03, 
            	0.05, //Label 7
            	TableLayout.FILL, //TextArea 8
            	0.06}};
        simulationPanel.setLayout(new TableLayout(size));
        simulationPanel.setBorder(BiogasControlPlugin.border);
        
        simulationPanel.add(plantStructureLabel, new TableLayoutConstraints(1, 1, 1, 1, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(activeElementLabel, new TableLayoutConstraints(3, 1, 3, 1, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(iterationLabel, new TableLayoutConstraints(5, 1, 5, 1, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(runtimeLabel, new TableLayoutConstraints(7, 1, 7, 1, TableLayout.FULL, TableLayout.FULL));
 
        simulationPanel.add(plantStructure, new TableLayoutConstraints(1, 2, 1, 2, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(activeElement, new TableLayoutConstraints(3, 2, 3, 2, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(iteration, new TableLayoutConstraints(5, 2, 5, 2, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(runtime, new TableLayoutConstraints(7, 2, 7, 2, TableLayout.FULL, TableLayout.FULL));
        
        simulationPanel.add(workingDirectoryLabel, new TableLayoutConstraints(1, 4, 1, 4, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(workingDirectory, new TableLayoutConstraints(1, 5, 7, 5, TableLayout.FULL, TableLayout.FULL));
       
        simulationPanel.add(simulationLogLabel, new TableLayoutConstraints(1, 7, 1, 7, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(simulationLogScroll, new TableLayoutConstraints(1, 8, 7, 8, TableLayout.FULL, TableLayout.FULL));
	}
}
