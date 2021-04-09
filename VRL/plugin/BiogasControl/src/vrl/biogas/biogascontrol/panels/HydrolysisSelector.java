package vrl.biogas.biogascontrol.panels;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import vrl.biogas.biogascontrol.BiogasControl;

public class HydrolysisSelector {
	
	static public JButton okBtn;
	static public JComboBox<String> reactorList;
	
	public static void showSelector(int numHydrolysis) {
		//int numHydrolysis = BiogasControlPlugin.struct.numHydrolysis();
		//int numHydrolysis = 2;
	
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
		frame.setLocationRelativeTo(BiogasControl.panel);
		frame.setVisible(true);
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{         		   
		showSelector(2);
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("returnvalue: " + (String) reactorList.getSelectedItem());
			}
			
		});
	}
}
