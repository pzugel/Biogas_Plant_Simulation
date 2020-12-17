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
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.math.*;

@ComponentInfo(name="BiogasOutputLoader", 
category="Biogas", 
description="BiogasOutputLoader Component")
public class BiogasOutputLoaderPlugin implements Serializable{
	private static final long serialVersionUID = 1L;
	
	static JTree tree;
	static List<Trajectory> trajectories;
	static File filepath;
	
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
		
		JPanel mainPanel = new JPanel(new BorderLayout());
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
	public static Trajectory getValues() throws FileNotFoundException {
		Trajectory trajectory = new Trajectory("");
		
		if(tree.getLastSelectedPathComponent() == null)
			return new Trajectory("");

		DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();
		OutputEntry entry = (OutputEntry) node.getUserObject();

		trajectory.setTitle(entry.getFilename());
		trajectory.setyAxisLabel(entry.getName() + entry.getUnit());
		trajectory.setxAxisLabel(entry.getXValueName() + entry.getXValueUnit());
		
		File path = new File(filepath.getParent() + "/" + entry.getFilename());
		
		System.out.println("Plotting from File: " + path.getPath());
		System.out.println("--> x value " + entry.getXValueName() + entry.getXValueUnit() 
			+ " from column " + entry.getXValueColumn());
		System.out.println("--> y value " + entry.getName() + entry.getUnit() 
			+ " from column " + entry.getColumn());
		
		CSVReader reader = new CSVReader(path);
		List<Double> col = reader.getCol(entry.getColumn());
		List<Double> x_col = reader.getCol(entry.getXValueColumn());
		
		for(int i=0; i<reader.getRowSize(); i++)
			trajectory.add(x_col.get(i), col.get(i));
		
		//TreePath[] paths = tree.getSelectionPaths();
		//for(TreePath p : paths)
		//	System.out.println("Plotting: " + p.toString());
		return trajectory;
	}
	
	/*
	public Trajectory[] returnTrajectories() {
		Trajectory[] t = new Trajectory[trajectories.size()];
		for(int i=0; i<trajectories.size(); i++)
			t[i] = trajectories.get(i);
		return t;
	}
	*/
	/*
	public JComponent test() {
        String data[][]={ {"101","Amit","670000"},    
                {"102","Jai","780000"},    
                {"101","Sachin","700000"}};    
		String column[]={"ID","NAME","SALARY"};         
		JTable jt=new JTable(data,column);  
		OutputContainerType cont = new OutputContainerType();
		cont.setViewValue(jt);
		return cont;
	}
	*/
	
	
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
	
	}
	
}
