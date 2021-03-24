package vrl.biogas.biogascontrol.panels;

import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class FeedbackPanel {

	static JPanel feedbackPanel;
	
	public FeedbackPanel() {
		feedbackPanel = new JPanel();
		createPanel();
	}
	
	public JPanel getPanel() {
		return feedbackPanel;
	}
	
	private void createPanel() {
		final JSlider slider1 = new JSlider(JSlider.VERTICAL,0, 100, 100);
		JSlider slider2 = new JSlider(JSlider.VERTICAL,0, 100, 100);
		JSlider slider3 = new JSlider(JSlider.VERTICAL,0, 100, 100);
		
		
		slider1.setMajorTickSpacing(20);
		slider1.setMinorTickSpacing(5);
		slider1.setPaintTicks(true);
		slider1.setPaintLabels(true);
		slider1.setSnapToTicks(true);
		
		final JTextField field1 = new JTextField();
		field1.setText(String.valueOf(slider1.getValue()) + "%");
		
		feedbackPanel.add(slider1);
		feedbackPanel.add(field1);
		
		slider1.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				field1.setText(String.valueOf(slider1.getValue()) + "%");
			}	
		});
	}
}
