package edu.gcsc.vrl.jfreechart;

import java.io.Serializable;
import java.util.ArrayList;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.ChartPanel;
import edu.gcsc.vrl.jfreechart.TrajectoryPlotter;
import vrl.biogas.biogascontrol.MainPanelContainerType;

@ComponentInfo(name = "BiogasPlotter", category = "Biogas")
@ObjectInfo(name = "BiogasPlotter")
public class BiogasPlotter implements Serializable {  
	private static final long serialVersionUID = 1;

	@MethodInfo(name="Plot", hide=false, hideCloseIcon=true, num=1)
	public JComponent plot(
		@ParamInfo(name = "Parameters",
			nullIsValid = false) ArrayList<ArrayList<Trajectory>> list){
		JPanel panel = new JPanel();
		MainPanelContainerType cont = new MainPanelContainerType();

		for(ArrayList<Trajectory> t : list){
			Trajectory[] b = (Trajectory[]) t.toArray();
			TrajectoryPlotter plot = new TrajectoryPlotter();
			JFreeChart chart = plot.lineCharts(b);
			ChartPanel chartPanel = new ChartPanel(chart);
			panel.add(chartPanel);
		}

		cont.setViewValue(panel);
		return cont;
	}
}
