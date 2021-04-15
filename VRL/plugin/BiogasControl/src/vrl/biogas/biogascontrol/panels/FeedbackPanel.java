package vrl.biogas.biogascontrol.panels;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasUserControl;

public class FeedbackPanel {

	private JPanel feedbackPanel;
	
	public FeedbackPanel() {
		feedbackPanel = new JPanel();
		createPanel(false);
	}
	
	public FeedbackPanel(boolean userDefined) {
		feedbackPanel = new JPanel();
		createPanel(userDefined);
	}
	
	public JPanel getPanel() {
		return feedbackPanel;
	}
	
	private void createPanel(boolean userDefined) {
		int numHydrolysis;
		if(userDefined) {
			numHydrolysis = BiogasUserControl.numHydrolysis;
		}
		else {
			numHydrolysis = BiogasControl.struct.numHydrolysis();
		}
		
		for(int i=0; i<numHydrolysis; i++) {
			final JSlider slider = new JSlider(SwingConstants.VERTICAL,0, 100, 100);		
			slider.setMajorTickSpacing(20);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);
			
			final JTextField field1 = new JTextField();
			field1.setText(String.valueOf(slider.getValue()) + "%");
			
			feedbackPanel.add(slider);
			feedbackPanel.add(field1);
			
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent arg0) {
					field1.setText(String.valueOf(slider.getValue()) + "%");
				}	
			});	
		}
		
	}
}
