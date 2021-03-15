package vrl.biogas.biogascontrol;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class HydrolysisSelector {
	
	static JButton okBtn;
	static JComboBox<String> reactorList;
	
	public static void showSelector() {
		//int numHydrolysis = BiogasControlPlugin.struct.numHydrolysis();
		int numHydrolysis = 2;
	
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
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(BiogasControlPlugin.panel);
		frame.setVisible(true);
		
		okBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				frame.dispose();
			}
		});
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{         		   
		showSelector();
		okBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("returnvalue: " + (String) reactorList.getSelectedItem());
			}
			
		});
	}
}
