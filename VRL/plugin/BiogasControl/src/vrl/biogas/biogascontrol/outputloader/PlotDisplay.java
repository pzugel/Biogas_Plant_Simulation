package vrl.biogas.biogascontrol.outputloader;

import java.util.ArrayList;
import java.awt.GridLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.Serializable;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.Range;

import edu.gcsc.vrl.jfreechart.TrajectoryPlotter;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.math.Trajectory;

import layout.TableLayout;
import layout.TableLayoutConstants;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.MainPanelContainerType;

/**
 * Main panel for the plotter in VRL
 * @author paul
 */
@ComponentInfo(name = "PlotDisplay", category = "Biogas")
@ObjectInfo(name = "PlotDisplay")
public class PlotDisplay implements Serializable {  
  private static final long serialVersionUID = 1;
  
  public static JPanel mainPanel;
  
  @MethodInfo(name="Plot", hide=false, hideCloseIcon=true, num=1)
  public JComponent plot(
    @ParamInfo(name = "Parameters",
      nullIsValid = false) ArrayList<ArrayList<Trajectory>> list){
    mainPanel = new JPanel();
    double mainPanelSize[][] = {{TableLayoutConstants.FILL},{0.15, 0.85}};
    mainPanel.setLayout(new TableLayout(mainPanelSize));
    
    MainPanelContainerType cont = new MainPanelContainerType();
    
    GridLayout plotsLayout = new GridLayout(0, 1);
    final JPanel plotsPanel = new JPanel();
    plotsPanel.setLayout(plotsLayout);
    
    JPanel checkBoxPanel = new JPanel();
    checkBoxPanel.setLayout(new GridLayout(list.size()+2, 0));
    checkBoxPanel.add(new JLabel("Enable/Disable Plots:"));
    for(final ArrayList<Trajectory> tList : list){
    	JPanel plotCheckerPanel = new JPanel();
    	final JPanel singlePlotPanel = new JPanel();
    	//double singlePlotSize[][] = {{0.8, 0.2},{TableLayoutConstants.FILL}};
    	double singlePlotSize[][] = {{0.8, 0.2},{TableLayoutConstants.FILL,0.1}};
    	singlePlotPanel.setLayout(new TableLayout(singlePlotSize));
    	plotCheckerPanel.setLayout(new GridLayout(tList.size(),0));
    	
    	Trajectory[] trajectoryArr = new Trajectory[tList.size()];
    	trajectoryArr = tList.toArray(trajectoryArr);    
      
    	final TrajectoryPlotter plotter = new TrajectoryPlotter();
    	final JFreeChart chart = plotter.lineCharts(trajectoryArr);
    	final ChartPanel chartPanel = new ChartPanel(chart);
    	
    	int trajectoryIndex = 0;
    	for(Trajectory t : tList) {
    		final JCheckBox plotChecker = new JCheckBox(t.getLabel());
    		plotChecker.setName(String.valueOf(trajectoryIndex));
    		plotChecker.setSelected(true);
    		//Events to enable/disable single plots in a chart
    		plotChecker.addItemListener(new ItemListener() {

				@Override
				public void itemStateChanged(ItemEvent arg0) {
					XYPlot plot = (XYPlot) chart.getPlot();					
					XYItemRenderer renderer = plot.getRenderer();
					int index = Integer.valueOf(plotChecker.getName());
					if(plotChecker.isSelected()) { 
						
						renderer.setSeriesVisible(index, true);
					}
					else {
						renderer.setSeriesVisible(index, false);
					}				
				} 			
    		});
    		
    		plotCheckerPanel.add(plotChecker);
    		++ trajectoryIndex;
    	}
    	singlePlotPanel.add(plotCheckerPanel, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstants.LEFT, TableLayoutConstants.LEFT));
    	
    	final JCheckBox checkBox = new JCheckBox(tList.get(0).getTitle()); 
    	checkBox.setSelected(true);
    	
    	//Slider
    	final JSlider slider = new JSlider(JSlider.HORIZONTAL);
    	final XYPlot plot = (XYPlot) chart.getPlot();	
		final ValueAxis axis = plot.getDomainAxis();
		
    	final double min = axis.getRange().getLowerBound();
    	final double max = axis.getRange().getUpperBound();
    	slider.setMinimum((int) min);
    	slider.setMaximum((int) max);
    	slider.setMajorTickSpacing(10);
    	slider.setMinorTickSpacing(1);
    	slider.setPaintTicks(false);
    	slider.setSnapToTicks(true);
    	slider.addChangeListener(new ChangeListener() {

			@Override
			public void stateChanged(ChangeEvent arg0) {	
				int sliderval = slider.getValue();
				if(sliderval > min) {
					axis.setRange(new Range(axis.getRange().getLowerBound(), slider.getValue() + slider.getValue()*0.05));
				}			
			}
    		
    	});    	
    	singlePlotPanel.add(slider, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
    	
    	//Events to enable/disable chart panels
    	checkBox.addItemListener(new ItemListener() {

    		@Override
    		public void itemStateChanged(ItemEvent arg0) {
    			if(checkBox.isSelected()) {  			  
    				System.out.println("CheckBox " + checkBox.getText() + " is selected.");
    				int rows = ((GridLayout) plotsPanel.getLayout()).getRows();
					System.out.println("\t --> Rows old: " + rows + ", Rows new: " + (rows+1));
					GridLayout newLayout = new GridLayout(rows+1,1);
					plotsPanel.setLayout(newLayout);
					plotsPanel.add(singlePlotPanel);
					plotsPanel.repaint();
					plotsPanel.revalidate();
    			}
    			else {
					System.out.println("CheckBox " + checkBox.getText() + " is unselected.");
					  
					int rows = ((GridLayout) plotsPanel.getLayout()).getRows();
					System.out.println("\t --> Rows old: " + rows + ", Rows new: " + (rows-1));
					GridLayout newLayout = new GridLayout(rows-1,1);
					plotsPanel.setLayout(newLayout);
					plotsPanel.remove(singlePlotPanel);
					plotsPanel.repaint();
					plotsPanel.revalidate();
    		  }		
    		}		
    	});
      
    	//Add chart to panel
    	int rows = ((GridLayout) plotsPanel.getLayout()).getRows();
    	GridLayout newLayout = new GridLayout(rows+1,1);
    	plotsPanel.setLayout(newLayout);
    	singlePlotPanel.add(chartPanel, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
    	plotsPanel.add(singlePlotPanel);
      
    	checkBoxPanel.add(checkBox);
  	
    }
    checkBoxPanel.add(new JLabel(""));
    
    mainPanel.add(checkBoxPanel, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
    mainPanel.add(plotsPanel, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));

    cont.setViewValue(mainPanel);
    return cont;
  }
  
  
  public static void main(String args[]){  
	  PlotDisplay plotter = new PlotDisplay();

	  ArrayList<ArrayList<Trajectory>> trajectories = BiogasPlotter.samplePlot();
	  plotter.plot(trajectories); 
	  //JPanel panel = (JPanel) cont.getViewValue();
	  
	  javax.swing.JFrame frame = new javax.swing.JFrame(); 
    
	  frame.add(mainPanel);	  
	  frame.setSize(300, 500);
	  frame.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
	  frame.setVisible(true);
  }
  
}
