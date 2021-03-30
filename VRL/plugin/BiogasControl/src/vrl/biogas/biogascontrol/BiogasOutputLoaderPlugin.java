package vrl.biogas.biogascontrol;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import edu.gcsc.vrl.jfreechart.TrajectoryPlotter;
import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.math.*;
import eu.mihosoft.vrl.reflection.Pair;
import vrl.biogas.biogascontrol.outputloader.CSVReader;
import vrl.biogas.biogascontrol.outputloader.OutputEntry;
import vrl.biogas.biogascontrol.outputloader.OutputLoader;
import vrl.biogas.biogascontrol.structures.STRUCT_2_STAGE;

@ComponentInfo(name="BiogasOutputPanel", 
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
		
		JPanel topPanel = new JPanel();
	    JPanel btnPanel = new JPanel();
	    
	    topPanel.setLayout(new BorderLayout());
        mainPanel.add(topPanel, BorderLayout.CENTER);
        mainPanel.add(btnPanel, BorderLayout.SOUTH);
		
        tree = new JTree(root);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		
	    JScrollPane scrollPane = new JScrollPane(tree);
	    topPanel.add(scrollPane,BorderLayout.CENTER);
	    
	    JButton plotButton = new JButton("Plot");
	    btnPanel.add(plotButton);
	    
	    MainPanelContainerType cont = new MainPanelContainerType();
	    cont.setViewValue(mainPanel);
	    
	    plotButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				try {
					getValues();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
	    });
	    
		return cont;
	}
	
	
	@MethodInfo(name="Plot", hide=false, 
			hideCloseIcon=true, num=1)
	public static ArrayList<ArrayList<Trajectory>> getValues() throws IOException, InterruptedException {		
		
		Set<String> filenames = new HashSet<String>();
		TreePath[] sel = tree.getSelectionPaths();
		if(sel == null) {
			System.out.println("Nothing to plot.");
			return null;
		}
		
		// Get all different filenames from the selected entries
		for(TreePath p : sel) {
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) p.getLastPathComponent();
			OutputEntry treeEntry = (OutputEntry) treeNode.getUserObject();
			if(treeEntry.isValue()) {
				filenames.add(treeEntry.getFilename());
			} 			
		}
		
		//if(sel.length)
		// Create an XYSerie for every selected entry
		List<Pair<OutputEntry, Trajectory>> dataSetList = new ArrayList<Pair<OutputEntry, Trajectory>>();
		for(TreePath p : sel) {
			
			DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) p.getLastPathComponent();
			OutputEntry treeEntry = (OutputEntry) treeNode.getUserObject();
			if(treeEntry.isValue()) {
				File f = new File(filepath.getParent(), treeEntry.getFilename());
				
				System.out.println("Plotting from File: " + f);
				System.out.println("--> x value " + treeEntry.getXValueName() + treeEntry.getXValueUnit() 
					+ " from column " + treeEntry.getXValueColumn());
				System.out.println("--> y value " + treeEntry.getName() + treeEntry.getUnit() 
					+ " from column " + treeEntry.getColumn());
				
				CSVReader reader = new CSVReader(f);
				List<Double> col = reader.getCol(treeEntry.getColumn());
				List<Double> x_col = reader.getCol(treeEntry.getXValueColumn());
				
				Trajectory t = new Trajectory(treeEntry.getName());
				for(int i=0; i<col.size(); i++) {			
					t.add((double) x_col.get(i), (double) col.get(i));
					System.out.println("\t --> " + x_col.get(i) + ", " + col.get(i));
				}
				t.setTitle(treeEntry.getFilename());
				t.setxAxisLabel(treeEntry.getXValueName() + treeEntry.getXValueUnit());
				t.setyAxisLabel(treeEntry.getUnit());
				t.setxAxisLogarithmic(false);
				t.setyAxisLogarithmic(false);

				Pair<OutputEntry, Trajectory> dataPair = new Pair<OutputEntry, Trajectory>(treeEntry, t);
				dataSetList.add(dataPair);	
			}
			else {
				System.out.println("Ignoring selection of non value tree node --> " + treeEntry.getName());
			}
			
		}
		
		// Match the filenames to the correct collection of series and create the chart
		ArrayList<ArrayList<Trajectory>> outList = new ArrayList<ArrayList<Trajectory>>();
		for(String name: filenames) {
			System.out.println("Merging trajectories for " + name);
			ArrayList<Trajectory> fileOutput= new ArrayList<Trajectory>();
			for(Pair<OutputEntry, Trajectory> pair : dataSetList) {
				if(pair.getFirst().getFilename().equals(name)){
					fileOutput.add(pair.getSecond());
					System.out.println("\t--> adding trajectory " + pair.getSecond().getLabel());
				}
			}
			outList.add(fileOutput);
		}		
		
		return outList;
	}
	
	public static ArrayList<ArrayList<Trajectory>> testFunc() {
		Trajectory t1 = new Trajectory("T1");
		Trajectory t2 = new Trajectory("T2");
		Trajectory t3 = new Trajectory("T3");
		Trajectory t4 = new Trajectory("T4");
		t1.setLabel("t1 Label");
		t2.setLabel("t2 Label");
		t3.setLabel("t3 Label");
		t4.setLabel("t4 Label");
		for(int i=0; i<20; i++) {
			t1.add((double) i, (double) 2*i+3);
			t2.add((double) i, (double) 3*i-4);
			t3.add((double) i, (double) 2.5*i-1);
			t4.add((double) i, (double) i-2);
		}
		
		ArrayList<ArrayList<Trajectory>> a = new ArrayList<ArrayList<Trajectory>>();
		
		ArrayList<Trajectory> firstPlot = new ArrayList<Trajectory>();
		firstPlot.add(t1);
		firstPlot.add(t2);
		ArrayList<Trajectory> secondPlot = new ArrayList<Trajectory>();
		secondPlot.add(t3);
		secondPlot.add(t4);
		
		a.add(firstPlot);
		a.add(secondPlot);
		return a;
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{
		File f = new File("/home/paul/Schreibtisch/smalltest/aceto/biogas_80h_2_STAGE_PL_ACETO/methane/outputFiles.lua");
		
	    JFrame frame = new JFrame();
	    load(f);   
	    
		frame.add(mainPanel);
		frame.setSize(600, 600);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);	
		
	}
	
	
}
