package vrl.biogas.biogascontrol.panels;

import java.awt.Color;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import layout.TableLayout;
import layout.TableLayoutConstants;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.BiogasControl;

/**
 * JTabbedPane: Structure tab <br>
 * Displayed in the {@link vrl.biogas.biogascontrol.BiogasControl}/{@link vrl.biogas.biogascontrol.BiogasUserControl} panel 
 * @author Paul Zügel
 *
 */
public class StructurePanel{
	
	private JPanel structurePanel;
	
	public StructurePanel() {
		structurePanel = new JPanel();
		createPanel(false);
	}
	
	public StructurePanel(boolean userDefined) {
		structurePanel = new JPanel();
		createPanel(userDefined);
	}
	
	public JPanel getPanel() {
		return structurePanel;
	}
	
	public void createPanel(boolean userDefined) {
        double size[][] =
            {{0.06, 0.88, TableLayoutConstants.FILL},
             {0.06, 
            	0.88, //Picutre
            	TableLayoutConstants.FILL}};
        structurePanel.setLayout(new TableLayout(size));
        structurePanel.setBorder(BiogasControlClass.BORDER);
        
        structurePanel.setBackground(Color.WHITE);
        
        if(!userDefined) {
    		String plantName = BiogasControl.struct.name();
    		File iconPath = new File(BiogasControlClass.projectPath, "icons");
    		ImageIcon plantIcon = new ImageIcon(new File(iconPath, plantName + ".png").toString());
    		structurePanel.add(new JLabel(plantIcon), new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));	
        }
	}

}