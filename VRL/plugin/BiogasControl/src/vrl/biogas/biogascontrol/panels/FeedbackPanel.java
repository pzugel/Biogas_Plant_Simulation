package vrl.biogas.biogascontrol.panels;

import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

//For ph dial indicator
/*
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.dial.DialPlot;
import org.jfree.chart.plot.dial.DialPointer;
import org.jfree.chart.plot.dial.DialValueIndicator;
import org.jfree.chart.plot.dial.StandardDialFrame;
import org.jfree.chart.plot.dial.StandardDialRange;
import org.jfree.chart.plot.dial.StandardDialScale;
import org.jfree.data.general.DefaultValueDataset;
*/
import layout.TableLayout;
import layout.TableLayoutConstants;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.BiogasUserControl;

/**
 * JTabbedPane: Feedback tab <br> 
 * Displayed in the {@link vrl.biogas.biogascontrol.BiogasControl}/{@link vrl.biogas.biogascontrol.BiogasUserControl} panel
 * @author Paul Zügel
 *
 */
public class FeedbackPanel {

	public JPanel feedbackPanel;
	public ArrayList<JSlider> sliderList;
	
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
		final int numHydrolysis;
		if(userDefined) {
			numHydrolysis = BiogasUserControl.numHydrolysis;
		}
		else {
			numHydrolysis = BiogasControl.struct.numHydrolysis();
		}
		
		sliderList = new ArrayList<JSlider>();
		
		final JPanel sliderPanel = new JPanel();
		double[] sliderCols = new double[numHydrolysis+1];
		double colSize = 1./(numHydrolysis+1);
		for(int i=0; i<numHydrolysis; i++) {
			sliderCols[i] = colSize;
		}
		sliderCols[numHydrolysis] = TableLayoutConstants.FILL;
		double sliderPanelSize[][] ={sliderCols,{TableLayoutConstants.FILL}};
		sliderPanel.setLayout(new TableLayout(sliderPanelSize));
;
		for(int i=0; i<numHydrolysis; i++) {
			final JPanel singleSlider = new JPanel();
			
			double sliderSize[][] =
	            {{0.8, TableLayoutConstants.FILL},
	             {0.1, 
	            	0.1, //Label
	            	0.6, //Slider
	            	0.06, //Legend
	            	TableLayoutConstants.FILL}};
	            	
			singleSlider.setLayout(new TableLayout(sliderSize));
	        
			final JSlider slider = new JSlider(SwingConstants.VERTICAL,0, 100, 100);		
			slider.setMajorTickSpacing(20);
			slider.setMinorTickSpacing(5);
			slider.setPaintTicks(true);
			slider.setPaintLabels(true);
			slider.setSnapToTicks(true);
			sliderList.add(slider);
			
			final JTextField legend = new JTextField();
			legend.setText(String.valueOf(slider.getValue()) + "%");
			legend.setHorizontalAlignment(JTextField.CENTER);
			legend.setEditable(false);
						
			final JLabel label = new JLabel();
			label.setText("Hydrolysis " + i);
			singleSlider.add(label, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
			singleSlider.add(slider, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
			singleSlider.add(legend, new TableLayoutConstraints(0, 3, 0, 3, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
			
			sliderPanel.add(singleSlider, new TableLayoutConstraints(i, 0, i, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
			
			slider.addChangeListener(new ChangeListener() {
				@Override
				public void stateChanged(ChangeEvent arg0) {
					legend.setText(String.valueOf(slider.getValue()) + "%");					
					computeFractions();
					if(allZero()) {
						slider.setValue(10);
						slider.updateUI();				    
						
						JOptionPane.showMessageDialog(feedbackPanel,
							    "Sliders cannot all be zero!",
							    "Warning",
							    JOptionPane.WARNING_MESSAGE);
						
						
					}
				}	
			});	
		}	
		
		JLabel text = new JLabel("<html><body>Splits the methane outflow when<br>feeding back into the simulation loop:</body></html>");
		double size[][] =
            {{TableLayoutConstants.FILL, 0.9},
             {0.1, 
            	0.08, //Text
            	0.8, //Sliders
            	TableLayoutConstants.FILL}};
		feedbackPanel.setLayout(new TableLayout(size));
		feedbackPanel.setBorder(BiogasControlClass.BORDER);
		
		feedbackPanel.add(text, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		feedbackPanel.add(sliderPanel, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		
		if(numHydrolysis == 1) {
			JSlider slider = sliderList.get(0);
			slider.setEnabled(false);
			sliderPanel.add(new JLabel("<html><body>Single hydrolysis reactor.<br> Outflow cannot be split.</body></html>"),
					new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		}
	}
	
	/**
	 * Turn the percentages from the sliders to fractions.
	 * @return ArrayList<Double> of the slider fractions.
	 */
	public ArrayList<Double> computeFractions() {
		int sum = 0;
		for(JSlider slider : sliderList) {
			sum += slider.getValue();
		}
		
		ArrayList<Double> fractions = new ArrayList<Double>();
		for(JSlider slider : sliderList) {
			double fract = (double) slider.getValue()/sum;
			fractions.add(fract);
		}
		
		return fractions;
	}
	
	/**
	 * Check if all sliders are set to zero
	 * @return All sliders are zero?
	 */
	private boolean allZero() {
		boolean isZero = true;
		for(JSlider slider : sliderList) {
			if(slider.getValue() != 0) {
				isZero = false;
			}
		}
		return isZero;
	}
}
