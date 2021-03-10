package vrl.biogas.biogascontrol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class SimulationPanel {
	static JPanel simulationPanel;
	static JTextField plantStructure;
	static JTextField activeElement;
	static JTextField iteration;
	static JTextField runtime;
	static JTextField workingDirectory;
	
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
		
		runtime = new JTextField(6);
		runtime.setPreferredSize(new Dimension(80,22));
		runtime.setEditable(false);
		runtime.setBackground(Color.WHITE);
		runtime.setText("00:00:00");
		
		workingDirectory = new JTextField(42);
		workingDirectory.setPreferredSize(new Dimension(400,22));
		workingDirectory.setEditable(false);
		workingDirectory.setBackground(Color.WHITE);
		
		JTextArea simulationLog = new JTextArea(22,46);
		
		JScrollPane simulationLogScroll = new JScrollPane (simulationLog, 
				JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		simulationLogScroll.setPreferredSize(new Dimension(470,200));
		
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
		
		//JButton workingDirectoryBtn = new JButton("...Load");
		//workingDirectoryBtn.setPreferredSize(new Dimension(105,22));
		
		JLabel simulationLogLabel = new JLabel("Simulation log");
		simulationLogLabel.setLabelFor(simulationLog);
		
		JPanel simulationPanel_TopControls = new JPanel();
        simulationPanel_TopControls.setLayout(new GridBagLayout());
        
        JPanel simulationPanel_BottomControls_1 = new JPanel();
        simulationPanel_BottomControls_1.setLayout(new GridBagLayout());
        
        JPanel simulationPanel_BottomControls_2 = new JPanel();
        simulationPanel_BottomControls_2.setLayout(new GridBagLayout());
        
        GridBagConstraints c00 = new GridBagConstraints();
        GridBagConstraints c01 = new GridBagConstraints();
        GridBagConstraints c02 = new GridBagConstraints();
        GridBagConstraints c03 = new GridBagConstraints();
        GridBagConstraints c10 = new GridBagConstraints();
        GridBagConstraints c11 = new GridBagConstraints();
        GridBagConstraints c12 = new GridBagConstraints();
        GridBagConstraints c13 = new GridBagConstraints();
        GridBagConstraints c20 = new GridBagConstraints();
        GridBagConstraints c30 = new GridBagConstraints();
        //GridBagConstraints c31 = new GridBagConstraints();
        GridBagConstraints c40 = new GridBagConstraints();
        GridBagConstraints c50 = new GridBagConstraints();
        
        //plantStructure - Label
        c00.gridx = 0;
        c00.gridy = 0;
        c00.weightx = 0.5;
        c00.weighty = 0.5;
        c00.insets = new Insets(20,10,0,0);
        c00.anchor = GridBagConstraints.FIRST_LINE_START;
        
        //activeElement - Label
        c01.gridx = 1;
        c01.gridy = 0;
        c01.weightx = 0.5;
        c01.weighty = 0.5;
        c01.insets = new Insets(20,0,0,0);  
        c01.anchor = GridBagConstraints.FIRST_LINE_START;
        
        //iteration - Label
        c02.gridx = 2;
        c02.gridy = 0;
        c02.weightx = 0.5;
        c02.weighty = 0.5;
        c02.insets = new Insets(20,0,0,0);
        c02.anchor = GridBagConstraints.FIRST_LINE_START;
        
        //runtime - Label
        c03.gridx = 3;
        c03.gridy = 0;
        c03.insets = new Insets(20,0,0,0);
        c03.anchor = GridBagConstraints.FIRST_LINE_START;
        
        //plantStructure - TextField
        c10.gridx = 0;
        c10.gridy = 1;
        c10.weightx = 0.5;
        c10.weighty = 0.5;
        c10.insets = new Insets(2,10,0,20);
        //activeElement - TextField
        c11.gridx = 1;
        c11.gridy = 1;
        c11.weightx = 0.5;
        c11.weighty = 0.5;
        c11.insets = new Insets(2,0,0,20);
        //iteration - TextField
        c12.gridx = 2;
        c12.gridy = 1;
        c12.weightx = 0.5;
        c12.weighty = 0.5;
        c12.insets = new Insets(2,0,0,20);
        //runtime - TextField
        c13.gridx = 3;
        c13.gridy = 1;
        c13.weightx = 0.5;
        c13.weighty = 0.5;
        c13.insets = new Insets(2,0,0,20);
       
        //workingDirectory - Label
        c20.gridx = 0;
        c20.gridy = 2;
        c20.weightx = 0.5;
        c20.weighty = 0.5;
        c20.insets = new Insets(10,5,0,0);
        c20.anchor = GridBagConstraints.FIRST_LINE_START;
        
        //workingDirectory - TextField
        c30.gridx = 0;
        c30.gridy = 3;
        c30.weightx = 0.5;
        c30.weighty = 0.5;
        c30.insets = new Insets(2,5,0,0);
        c30.anchor = GridBagConstraints.FIRST_LINE_START;
        
        //workingDirectory - Button
        //c31.gridx = 1;
        //c31.gridy = 3;
        //c31.insets = new Insets(2,0,0,0);
        //c31.anchor = GridBagConstraints.FIRST_LINE_START;
        
        //simulationLog - Label
        c40.gridx = 0;
        c40.gridy = 4;
        c40.weightx = 0.5;
        c40.weighty = 0.5;
        c40.insets = new Insets(10,5,0,0);
        c40.anchor = GridBagConstraints.FIRST_LINE_START;
        
        //simulationLog - TextField
        c50.gridx = 0;
        c50.gridy = 5;
        c50.weightx = 0.5;
        c50.weighty = 0.5;
        c50.insets = new Insets(2,5,0,20);
        c50.anchor = GridBagConstraints.FIRST_LINE_START;
        
        simulationPanel_TopControls.add(plantStructureLabel, c00);
        simulationPanel_TopControls.add(activeElementLabel, c01);
        simulationPanel_TopControls.add(iterationLabel, c02);
        simulationPanel_TopControls.add(runtimeLabel, c03);
 
        simulationPanel_TopControls.add(plantStructure, c10);
        simulationPanel_TopControls.add(activeElement, c11);
        simulationPanel_TopControls.add(iteration, c12);
        simulationPanel_TopControls.add(runtime, c13);
        
        simulationPanel_BottomControls_1.add(workingDirectoryLabel, c20);
        simulationPanel_BottomControls_1.add(workingDirectory, c30);
        //simulationPanel_BottomControls_1.add(workingDirectoryBtn, c31);
        simulationPanel_BottomControls_2.add(simulationLogLabel, c40);
        simulationPanel_BottomControls_2.add(simulationLogScroll, c50);
        
        simulationPanel.setLayout(new GridBagLayout());
     
        GridBagConstraints top = new GridBagConstraints();
        GridBagConstraints btm_1 = new GridBagConstraints();
        GridBagConstraints btm_2 = new GridBagConstraints();
        
        top.gridx = 0;
        top.gridy = 1;
        top.weightx = 0.5;
        top.weighty = 0.5;
        top.anchor = GridBagConstraints.FIRST_LINE_START;
        
        btm_1.gridx = 0;
        btm_1.gridy = 2;
        btm_1.weightx = 0.5;
        btm_1.weighty = 0.5;
        btm_1.insets = new Insets(10,5,0,0);
        btm_1.anchor = GridBagConstraints.FIRST_LINE_START;
        
        btm_2.gridx = 0;
        btm_2.gridy = 3;
        btm_2.weightx = 0.5;
        btm_2.weighty = 0.5;
        btm_2.insets = new Insets(10,5,0,0);
        btm_2.anchor = GridBagConstraints.FIRST_LINE_START;
       
        simulationPanel.add(simulationPanel_TopControls, top);
        simulationPanel.add(simulationPanel_BottomControls_1, btm_1);
        simulationPanel.add(simulationPanel_BottomControls_2, btm_2);
        simulationPanel.setPreferredSize(new Dimension(560,650));
	}
}
