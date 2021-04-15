package vrl.biogas.biogascontrol.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import vrl.biogas.biogascontrol.BiogasControlClass;

public class HydrolysisSelector {
	
	public JButton okBtn;
	public JComboBox<String> reactorList;
	
	public void showSelector(int numHydrolysis) {
		showSelector(numHydrolysis, BiogasControlClass.panel);
	}
	
	public void showSelector(int numHydrolysis, JPanel referencePanel) {
	
		String[] reactors = new String[numHydrolysis];
		for(int i=0; i<numHydrolysis; i++) {
			reactors[i] = "hydrolyse_" + i;
		}
		reactorList = new JComboBox<String>(reactors);
		
		JPanel panel = new JPanel();
		okBtn = new JButton("Ok");
		
		panel.add(reactorList);
		panel.add(okBtn);
		
		final JFrame frame = new JFrame("");
		frame.add(panel);
		frame.setSize(200, 70);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(referencePanel);
		frame.setVisible(true);
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
	}
}
