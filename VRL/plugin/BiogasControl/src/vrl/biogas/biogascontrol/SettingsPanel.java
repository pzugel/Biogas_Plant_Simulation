package vrl.biogas.biogascontrol;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import java.awt.geom.*;

import layout.TableLayout;
import layout.TableLayoutConstraints;

public class SettingsPanel {
	final Color BUTTON_BLUE = Color.decode("#F0F6FF");
	
	static JPanel settingsPanel;
	static JSpinner simStarttime;
	static JSpinner simEndtime;
	static JCheckBox autoCleanup;
	
	public SettingsPanel() {
		settingsPanel = new JPanel();
		createPanel();
	}
	
	JPanel getPanel() {
		return settingsPanel;
	}
	
	void createPanel() {
		SpinnerModel startModel = new SpinnerNumberModel(0, 0, 1000, 1);
		SpinnerModel endModel = new SpinnerNumberModel(1, 1, 1000, 1);
		 
		
		simStarttime = new JSpinner(startModel);
		//simStarttime.setPreferredSize(new Dimension(70,20));
		simEndtime = new JSpinner(endModel);
		//simEndtime.setPreferredSize(new Dimension(70,20));
		autoCleanup = new JCheckBox("Off/On");
		
		JLabel simStarttimeLabel = new JLabel("Starttime");
		simStarttimeLabel.setLabelFor(simStarttime);
		
		JLabel simEndtimeLabel = new JLabel("Endtime");
		simEndtimeLabel.setLabelFor(simEndtime);
		
		JLabel autoCleanupLabel = new JLabel("Auto cleanup?");
		autoCleanupLabel.setLabelFor(autoCleanup);
		
		JLabel text = new JLabel("<html><body>If you want the hydrolysis reactors to start with different"
				+ "<br>initial values, you need to set up a working environtment first.</body></html>");
		
        double size[][] =
            {{0.06, 0.18, 0.04, 0.18, 0.04, 0.22, 0.3, TableLayout.FILL},
             {0.06, 0.05, 0.06, 0.1, 0.15, 0.41, TableLayout.FILL}};
        settingsPanel.setLayout(new TableLayout(size));

        /*
        JPanel line = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);            
                    g.drawLine(0, 20, 440, 20);
            }
        };
        */
        
		settingsPanel.add(simStarttimeLabel, new TableLayoutConstraints(1, 1, 1, 1, TableLayout.FULL, TableLayout.FULL));
		settingsPanel.add(simEndtimeLabel, new TableLayoutConstraints(3, 1, 3, 1, TableLayout.FULL, TableLayout.FULL));
		settingsPanel.add(autoCleanupLabel, new TableLayoutConstraints(5, 1, 5, 1, TableLayout.FULL, TableLayout.FULL));
		settingsPanel.add(simStarttime, new TableLayoutConstraints(1, 2, 1, 2, TableLayout.FULL, TableLayout.FULL));
		settingsPanel.add(simEndtime, new TableLayoutConstraints(3, 2, 3, 2, TableLayout.FULL, TableLayout.FULL));
		settingsPanel.add(autoCleanup, new TableLayoutConstraints(5, 2, 5, 2, TableLayout.FULL, TableLayout.FULL));
		settingsPanel.add(text, new TableLayoutConstraints(1, 4, 6, 4, TableLayout.FULL, TableLayout.FULL));
		//settingsPanel.add(line, new TableLayoutConstraints(1, 3, 6, 3, TableLayout.CENTER, TableLayout.CENTER));
		
		//settingsPanel.add(new JLabel("15"), new TableLayoutConstraints(1, 5, 1, 5, TableLayout.TOP, TableLayout.LEFT));
		//settingsPanel.add(new JLabel("16"), new TableLayoutConstraints(1, 6, 1, 6, TableLayout.TOP, TableLayout.LEFT));
		
		JPanel elementsPanel = new JPanel();
        double elements_size[][] =
            {{0.09, 0.04, 0.44, 0.01, 0.06, 0.05, 0.2, TableLayout.FILL},
             {0.24, 0.11, 0.24, 0.11, 0.24, TableLayout.FILL}};
        elementsPanel.setLayout(new TableLayout(elements_size));
        
        JButton methane_edit = new JButton("Edit");
        methane_edit.setBackground(BUTTON_BLUE);
        JTextField methane_path = new JTextField(5);
        methane_path.setText(new File(BiogasControlPlugin.projectPath, "methane.lua").toString());
        JButton open_methane_edit = new JButton("...");
        open_methane_edit.setBackground(BUTTON_BLUE);
        
        JButton hydrolysis_edit = new JButton("Edit");
        hydrolysis_edit.setBackground(BUTTON_BLUE);
        JTextField hydrolysis_path = new JTextField(5);
        hydrolysis_path.setText(new File(BiogasControlPlugin.projectPath, "hydrolyse.lua").toString());
        JButton open_hydrolysis_edit = new JButton("...");
        open_hydrolysis_edit.setBackground(BUTTON_BLUE);
        
        JTextField simulation_path = new JTextField(5);
        simulation_path.setText(new File(BiogasControlPlugin.projectPath, "Biogas.lua").toString());
        JButton open_simulation_edit = new JButton("...");
        open_simulation_edit.setBackground(BUTTON_BLUE);
        
        elementsPanel.add(new JButton("P"), new TableLayoutConstraints(0, 0, 0, 0, TableLayout.FULL, TableLayout.FULL));
        elementsPanel.add(methane_path, new TableLayoutConstraints(2, 0, 2, 0, TableLayout.FULL, TableLayout.FULL));
        elementsPanel.add(open_methane_edit, new TableLayoutConstraints(4, 0, 4, 0, TableLayout.FULL, TableLayout.FULL));
        elementsPanel.add(methane_edit, new TableLayoutConstraints(6, 0, 6, 0, TableLayout.FULL, TableLayout.FULL));
        
        elementsPanel.add(new JButton("P"), new TableLayoutConstraints(0, 2, 0, 2, TableLayout.FULL, TableLayout.FULL));
        elementsPanel.add(hydrolysis_path, new TableLayoutConstraints(2, 2, 2, 2, TableLayout.FULL, TableLayout.FULL));
        elementsPanel.add(open_hydrolysis_edit, new TableLayoutConstraints(4, 2, 4, 2, TableLayout.FULL, TableLayout.FULL));
        elementsPanel.add(hydrolysis_edit, new TableLayoutConstraints(6, 2, 6, 2, TableLayout.FULL, TableLayout.FULL));
        
        elementsPanel.add(new JButton("P"), new TableLayoutConstraints(0, 4, 0, 4, TableLayout.FULL, TableLayout.FULL));
        elementsPanel.add(simulation_path, new TableLayoutConstraints(2, 4, 2, 4, TableLayout.FULL, TableLayout.FULL));
        elementsPanel.add(open_simulation_edit, new TableLayoutConstraints(4, 4, 4, 4, TableLayout.FULL, TableLayout.FULL));
        
        settingsPanel.add(elementsPanel, new TableLayoutConstraints(1, 5, 6, 5, TableLayout.FULL, TableLayout.FULL));
        
		simStarttime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if((Integer) simEndtime.getValue() <= (Integer) simStarttime.getValue()) {
					simEndtime.setValue((Integer) simStarttime.getValue() + 1);
				}
					
			}
	    }); 
		
		simEndtime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if((Integer) simEndtime.getValue() <= (Integer) simStarttime.getValue()) {
					simEndtime.setValue((Integer) simEndtime.getValue() + 1);
				}
					
			}
	    }); 
	}
}
