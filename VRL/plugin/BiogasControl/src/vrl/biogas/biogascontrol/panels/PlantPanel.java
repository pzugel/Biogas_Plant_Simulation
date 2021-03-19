package vrl.biogas.biogascontrol.panels;

import java.awt.Color;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import layout.TableLayout;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.BiogasControlPlugin;


public class PlantPanel {
	
	static JPanel plantPanel;
	
	public PlantPanel() {
		plantPanel = new JPanel();
		createPanel();
	}
	
	public JPanel getPanel() {
		return plantPanel;
	}
	
	private void createPanel() {
        double size[][] =
            {{0.06, 0.88, TableLayout.FILL},
             {0.06, 
            	0.88, //Picutre
            	TableLayout.FILL}};
        plantPanel.setLayout(new TableLayout(size));
        plantPanel.setBackground(Color.WHITE);
        
		String plantName = BiogasControlPlugin.struct.name();
		File iconPath = new File(BiogasControlPlugin.projectPath, "icons");
		ImageIcon plantIcon = new ImageIcon(new File(iconPath, plantName + ".png").toString());
		plantPanel.add(new JLabel(plantIcon), new TableLayoutConstraints(1, 1, 1, 1, TableLayout.FULL, TableLayout.FULL));
	}
}