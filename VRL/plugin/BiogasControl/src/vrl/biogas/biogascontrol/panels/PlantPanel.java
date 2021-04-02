package vrl.biogas.biogascontrol.panels;

import java.awt.Color;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import layout.TableLayout;
import layout.TableLayoutConstants;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasControlPlugin;


public class PlantPanel {
	
	static JPanel plantPanel;
	
	public PlantPanel() {
		plantPanel = new JPanel();
		createPanel(false);
	}
	
	public PlantPanel(boolean userDefined) {
		plantPanel = new JPanel();
		createPanel(userDefined);
	}
	
	public JPanel getPanel() {
		return plantPanel;
	}
	
	private void createPanel(boolean userDefined) {
        double size[][] =
            {{0.06, 0.88, TableLayoutConstants.FILL},
             {0.06, 
            	0.88, //Picutre
            	TableLayoutConstants.FILL}};
        plantPanel.setLayout(new TableLayout(size));
        plantPanel.setBorder(BiogasControl.border);
        
        plantPanel.setBackground(Color.WHITE);
        
        if(!userDefined) {
    		String plantName = BiogasControlPlugin.struct.name();
    		File iconPath = new File(BiogasControl.projectPath, "icons");
    		ImageIcon plantIcon = new ImageIcon(new File(iconPath, plantName + ".png").toString());
    		plantPanel.add(new JLabel(plantIcon), new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));	
        }
	}
}