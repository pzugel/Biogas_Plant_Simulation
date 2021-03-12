package vrl.biogas.biogascontrol;

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

public class SimulationPanel {
	static JPanel simulationPanel;
	static JTextField plantStructure;
	static JTextField activeElement;
	static JTextField iteration;
	static JTextField runtime;
	static JTextField workingDirectory;
	static JTextArea simulationLog;
	
	public SimulationPanel() {
		simulationPanel = new JPanel();
		createPanel();
	}
	
	JPanel getPanel() {
		return simulationPanel;
	}
	
	void createPanel() {
		plantStructure = new JTextField(14);
		plantStructure.setPreferredSize(new Dimension(100,22));
		plantStructure.setEditable(false);
		plantStructure.setBackground(Color.WHITE);
		plantStructure.setText(BiogasControlPlugin.struct.name());
		
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
            	0.01, 
            	0.06, //TextField 3
            	0.03, 
            	0.05, //Label 5
            	0.01, 
            	0.06, //TextField 7
            	0.03, 
            	0.05, //Label 9
            	0.01,
            	TableLayout.FILL, //TextArea 11
            	0.06}};
        simulationPanel.setLayout(new TableLayout(size));
        
        simulationPanel.add(plantStructureLabel, new TableLayoutConstraints(1, 1, 1, 1, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(activeElementLabel, new TableLayoutConstraints(3, 1, 3, 1, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(iterationLabel, new TableLayoutConstraints(5, 1, 5, 1, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(runtimeLabel, new TableLayoutConstraints(7, 1, 7, 1, TableLayout.FULL, TableLayout.FULL));
 
        simulationPanel.add(plantStructure, new TableLayoutConstraints(1, 3, 1, 3, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(activeElement, new TableLayoutConstraints(3, 3, 3, 3, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(iteration, new TableLayoutConstraints(5, 3, 5, 3, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(runtime, new TableLayoutConstraints(7, 3, 7, 3, TableLayout.FULL, TableLayout.FULL));
        
        simulationPanel.add(workingDirectoryLabel, new TableLayoutConstraints(1, 5, 1, 5, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(workingDirectory, new TableLayoutConstraints(1, 7, 7, 7, TableLayout.FULL, TableLayout.FULL));
       
        simulationPanel.add(simulationLogLabel, new TableLayoutConstraints(1, 9, 1, 9, TableLayout.FULL, TableLayout.FULL));
        simulationPanel.add(simulationLogScroll, new TableLayoutConstraints(1, 11, 7, 11, TableLayout.FULL, TableLayout.FULL));
	}
}
