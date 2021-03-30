package vrl.biogas.outputloader;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.math.*;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.statistics.HistogramDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.sun.tools.javac.util.Pair;

@ComponentInfo(name="BiogasOutputLoader", 
category="Biogas", 
description="BiogasOutputLoader Component")
public class BiogasOutputLoaderPlugin implements Serializable{
	private static final long serialVersionUID = 1L;
	
	static JTree tree;
	static List<Trajectory> trajectories;
	static File filepath;
	static JPanel mainPanel;
	
	@MethodInfo(name="Load", hide=false, 
			hideCloseIcon=true, interactive=false, num=1)
	public static JComponent load(
			@ParamInfo(name = "outputFiles.lua",
				      nullIsValid = false,
				      style = "load-dialog",
				      options = "endings=[\"lua\"]; invokeOnChange=true") File path) 
				    		  throws FileNotFoundException {
		filepath = path;
		OutputLoader loader = new OutputLoader(path);
		loader.load();
		List<OutputEntry> data = loader.getData();
		/*
		for(OutputEntry entry: data)
			System.out.println(entry.getIndent() + " " 
					+ entry.getName() + entry.getUnit() + " "
					+ entry.getFilename() + " " + entry.getColumn() + " xCol:" + entry.getXValueColumn());
		*/
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Parameters");
		DefaultMutableTreeNode lastParent = null;
		
		for(OutputEntry entry: data) {
			if(entry.getIndent() == 0) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry);
				root.add(node);
				lastParent = node;
			}
			else {
				DefaultMutableTreeNode param = new DefaultMutableTreeNode(entry);
				lastParent.add(param);
			}
		}
		
		mainPanel = new JPanel(new BorderLayout());
		mainPanel.setSize(300, 450);
		
		JPanel topPanel = new JPanel();
	    JPanel btnPanel = new JPanel();
	    
	    topPanel.setLayout(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
		
        tree = new JTree(root);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		//tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		
	    JScrollPane scrollPane = new JScrollPane(tree);
	    topPanel.add(scrollPane,BorderLayout.CENTER);
	    
	    JButton plotButton = new JButton("Plot");
	    btnPanel.add(plotButton);
	    
	    OutputContainerType cont = new OutputContainerType();
	    cont.setViewValue(mainPanel);
	    
	    //trajectories = new ArrayList<Trajectory>();
	    
		plotButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					getValues();
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				/*
				trajectories.clear();
				TreePath[] paths = tree.getSelectionPaths();
				for(TreePath p : paths) {
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
					OutputEntry entry = (OutputEntry) node.getUserObject();
					Trajectory newTrajectory = new Trajectory(entry.getName());
					newTrajectory.setTitle(entry.getName());
					newTrajectory.setxAxisLabel(entry.getName() + entry.getUnit());
					newTrajectory.setyAxisLabel(entry.getXValueName() + entry.getXValueUnit());
					newTrajectory.add(0.0, 1.0);
					newTrajectory.add(1.0, 4.0);
					
					trajectories.add(newTrajectory);
				}
				*/
			}
	    });
	    

		//frame.setVisible(true);
		return cont;
	}
	
	@MethodInfo(name="Plot", hide=false,
			hideCloseIcon=true, interactive=true, num=1)
	public static Trajectory getValues() throws IOException, InterruptedException {
		Trajectory trajectory = new Trajectory("");
		
		if(tree.getLastSelectedPathComponent() == null)
			return new Trajectory("");
		
		
		Set<String> filenames = new HashSet<String>();
		TreePath[] sel = tree.getSelectionPaths();
		
		// Get all different filenames from the selected entries
		for(TreePath p : sel) {
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) p.getLastPathComponent();
			OutputEntry treeEntry = (OutputEntry) treeNode.getUserObject();
			filenames.add(treeEntry.getFilename());
		}
		
		// Create an XYSerie for every selected entry
		List<Pair<OutputEntry, XYSeries>> dataSetList = new ArrayList<Pair<OutputEntry, XYSeries>>();
		for(TreePath p : sel) {

			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) p.getLastPathComponent();
			OutputEntry treeEntry = (OutputEntry) treeNode.getUserObject();
			File f = new File(filepath.getParent(), treeEntry.getFilename());
			
			System.out.println("Plotting from File: " + f);
			System.out.println("--> x value " + treeEntry.getXValueName() + treeEntry.getXValueUnit() 
				+ " from column " + treeEntry.getXValueColumn());
			System.out.println("--> y value " + treeEntry.getName() + treeEntry.getUnit() 
				+ " from column " + treeEntry.getColumn());
			
			CSVReader reader = new CSVReader(f);
			List<Double> col = reader.getCol(treeEntry.getColumn());
			List<Double> x_col = reader.getCol(treeEntry.getXValueColumn());
			
			XYSeries series = new XYSeries(treeEntry.getName());
			for(int i=0; i<col.size(); i++) {
				series.add(x_col.get(i), col.get(i));
			}

		   Pair<OutputEntry, XYSeries> dataPair = new Pair<OutputEntry, XYSeries>(treeEntry, series);
		   dataSetList.add(dataPair);
		}
		
		// Match the filenames to the correct collection of series and create the chart
		for(String name: filenames) {					    
			XYSeriesCollection dataset = new XYSeriesCollection();
			
			String xLabel = "";
			String yLabel = "";
			for(Pair<OutputEntry, XYSeries> pair : dataSetList) {
				if(pair.fst.getFilename().equals(name)){
					dataset.addSeries(pair.snd);
					xLabel = pair.fst.getXValueName() + pair.fst.getXValueUnit();
					yLabel = pair.fst.getUnit();
				}		
			}
			
			JFreeChart chart = ChartFactory.createXYLineChart(
					name, // Title
					xLabel, // x-axis Label
					yLabel, // y-axis Label
					dataset, // Dataset
					PlotOrientation.VERTICAL, // Plot Orientation
					true, // Show Legend
					true, // Use tooltips
					false
					);
			
			ChartPanel panel = new ChartPanel(chart);		        
			JFrame frame = new JFrame();
		    frame.add(panel);
		    frame.setSize(600, 600);
		    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		    frame.setVisible(true);	
		       
		}
		
		return trajectory;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{
		/*
		File path = new File("/home/paul/Schreibtisch/outputFiles.lua");
		OutputLoader loader = new OutputLoader(path);
		loader.load();
		List<OutputEntry> data = loader.getData();
		//for(OutputEntry entry: data)
		//	System.out.println(entry.getIndent() + " " 
		//			+ entry.getName() + entry.getUnit() + " "
		//			+ entry.getFilename());
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Parameters");
		DefaultMutableTreeNode lastParent = null;
		
		for(OutputEntry entry: data) {
			if(entry.getIndent() == 0) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry);
				root.add(node);
				lastParent = node;
			}
			else {
				DefaultMutableTreeNode param = new DefaultMutableTreeNode(entry);
				lastParent.add(param);
			}
		}
		JFrame frame = new JFrame();
		//frame.setTitle("Plotter"); 
		//frame.setLocationRelativeTo(null);
		frame.setSize(300, 450);
		
		JPanel topPanel = new JPanel();
	    JPanel btnPanel = new JPanel();

	    topPanel.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);
		
		final JTree tree = new JTree(root);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
	    JScrollPane scrollPane = new JScrollPane(tree);
	    topPanel.add(scrollPane,BorderLayout.CENTER);
	    
	    JButton plotButton = new JButton("Plot");
	    btnPanel.add(plotButton);

		frame.setVisible(true);
		
		plotButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
				OutputEntry entry = (OutputEntry) node.getUserObject();
				
				TreePath[] paths = tree.getSelectionPaths();
				for(TreePath p : paths)
					System.out.println(p.toString());
			}
	    });
	    
		
		CSVReader reader = new CSVReader(new File("/home/paul/Schreibtisch/Biogas_plant_setup/example/Sample_Output/dbg_density.txt"));
		List<Double> a = reader.getCol(2);
		System.out.println(reader.getColumnSize());
		for(Double d : a)
			System.out.println(d);
		File testfile = new File("/home/paul/Schreibtisch/Biogas_plant_setup/example/Sample_Output/output_Files.lua");
		System.out.println(testfile.getParent() + "/dbg_density.txt");
		
		//load(new File("/home/paul/Schreibtisch/Biogas_plant_setup/example/Sample_Output/outputFiles.lua"));
		//tree.expandRow(1);
		//tree.setSelectionRow(5);
		//getValues();
		 * */
		
		
		
		
		// KEEP THIS - MIGHT HELP LATER
		/*
		final Path path = Paths.get("/home/paul/Schreibtisch/Run_Folder");
		System.out.println(path);
		WatchService watchService = FileSystems.getDefault().newWatchService();
	    path.register(watchService, ENTRY_MODIFY, ENTRY_CREATE);
	    while (true) {
	        final WatchKey wk = watchService.take();
	        for (WatchEvent<?> event : wk.pollEvents()) {
	            final Path changed = (Path) event.context();	            
	            if (changed.endsWith("valveGasFlow.txt")) {
	                System.out.println("My file has changed");
	            }
	        }
	        // reset the key
	        boolean valid = wk.reset();
	        if (!valid) {
	        	System.out.println("Key has been unregisterede");
	        	break; 
	        }
	    }
		*/
		
		XYSeries series1 = new XYSeries("FIRST");
		XYSeries series2 = new XYSeries("SECOND");
		for(int i=0; i<20; i++) {
			series1.add(i, 2*i+3);
			series2.add(i, 3*i-5);
		}
		
		XYSeriesCollection dataset = new XYSeriesCollection();
		dataset.addSeries(series1);
		dataset.addSeries(series2);
		JFreeChart chart = ChartFactory.createXYLineChart(
			   "TEST", // Title
			   "X", // x-axis Label
			   "Y", // y-axis Label
			   dataset, // Dataset
			   PlotOrientation.VERTICAL, // Plot Orientation
			   true, // Show Legend
			   true, // Use tooltips
			   false // Configure chart to generate URLs?
		);

		ChartPanel panel = new ChartPanel(chart);		        
        
        JFrame frame = new JFrame();
        load(new File("/home/paul/Schreibtisch/smalltest/aceto/biogas_80h_2_STAGE_PL_ACETO/methane/outputFiles.lua"));
        frame.add(mainPanel);
        //frame.add(panel);
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);	

	}
	
}
