package vrl.biogas.biogascontrol.outputloader;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.WindowConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.math.*;
import eu.mihosoft.vrl.reflection.Pair;
import layout.TableLayout;
import layout.TableLayoutConstants;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasControlPlugin;
import vrl.biogas.biogascontrol.BiogasUserControlPlugin;
import vrl.biogas.biogascontrol.MainPanelContainerType;
import vrl.biogas.biogascontrol.panels.HydrolysisSelector;
import vrl.biogas.biogascontrol.panels.SetupPanel;

@ComponentInfo(name="BiogasOutputPanel", 
	category="Biogas", 
	description="BiogasOutputLoader Component")
@ObjectInfo(name = "BiogasOutputPanel")
public class BiogasOutputMainPanel implements Serializable{
	private static final long serialVersionUID = 1L;
	
	static CheckBoxTree tree;
	static List<Trajectory> trajectories;
	static File filepath;
	static JPanel mainPanel;
	
	@MethodInfo(name="Load", hide=false, interactive=true, num=1)
	public static JComponent loadBiogas(
			@ParamInfo(name = "BiogasControlPlugin",
	    		nullIsValid = false,
	    		options = "invokeOnChange=true") BiogasControlPlugin main) throws FileNotFoundException {		
		return load(BiogasControlPlugin.struct.numHydrolysis());
	}
	
	@MethodInfo(name="LoadUserDefined", hide=false, interactive=true, num=1)
	public static JComponent loadBiogasUser(
			@ParamInfo(name = "BiogasUserControlPlugin",
	    		nullIsValid = false,
	    		options = "invokeOnChange=true") BiogasUserControlPlugin main) throws FileNotFoundException {	
		return load(BiogasUserControlPlugin.numHydrolysis);
	}
	
	private static JComponent load(final int numHydrolysis) throws FileNotFoundException {		
		mainPanel = new JPanel(new BorderLayout());
		
		JPanel upperPanel = new JPanel();
		JButton loadBtn = new JButton("Load");
		loadBtn.setBackground(BiogasControl.BUTTON_BLUE);
		final JComboBox<String> plotSelect = new JComboBox<String>();
		plotSelect.addItem("Methane");
		plotSelect.addItem("Hydrolysis");
		plotSelect.addItem("Storage");
		plotSelect.addItem("... From File");
		
		double sizeTop[][] =
            {{0.75, TableLayoutConstants.FILL},
             {TableLayoutConstants.FILL}};
		upperPanel.setLayout(new TableLayout(sizeTop)); 			    
		upperPanel.add(plotSelect, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		upperPanel.add(loadBtn, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));	    
	    
	    double size[][] =
            {{TableLayoutConstants.FILL},
             {0.08, 0.84, TableLayoutConstants.FILL}};
	    mainPanel.setLayout(new TableLayout(size));
	    
		OutputEntry emptyEntry = new OutputEntry();
		emptyEntry.setName("...");
		
        tree = new CheckBoxTree();
        DefaultTreeModel model =(DefaultTreeModel) tree.getModel();
        model.setAsksAllowsChildren(true);
        DefaultMutableTreeNode emptyNode = new DefaultMutableTreeNode(emptyEntry, true);               
        model.setRoot(emptyNode);
        
	    final JScrollPane treeScrollPane = new JScrollPane(tree);	    
	    JButton clearBtn = new JButton("Clear");
	    
	    mainPanel.add(upperPanel, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstants.CENTER, TableLayoutConstants.CENTER));
        mainPanel.add(treeScrollPane, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        mainPanel.add(clearBtn, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstants.CENTER, TableLayoutConstants.CENTER));
	    
	    MainPanelContainerType cont = new MainPanelContainerType();
	    cont.setViewValue(mainPanel);
	    
	    loadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean isReady = SetupPanel.environment_ready;
				//boolean isReady = false; //TODO For debug
				if(!isReady && plotSelect.getSelectedIndex() != 3) {
					JFrame frame = new JFrame();
					frame.setLocationRelativeTo(mainPanel);
					frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
					JOptionPane.showMessageDialog(frame,
						    "There is currently no simulation to load.",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
					updateTree(null);
				}
				
				if(isReady && plotSelect.getSelectedIndex() != 3) {
					boolean envLoaded = SetupPanel.mergePreexisting;
					if(!envLoaded && BiogasControl.iteration == 0) {
						JFrame frame = new JFrame();
						frame.setLocationRelativeTo(mainPanel);
						frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						JOptionPane.showMessageDialog(frame,
							    "Wait until the first iteration has finished.",
							    "Warning",
							    JOptionPane.WARNING_MESSAGE);
						updateTree(null);
					}
					else if(envLoaded || BiogasControl.iteration != 0){
						final File environmentDir = SetupPanel.environment_path;
						
						//Methane
						if(plotSelect.getSelectedIndex() == 0) {
							File methaneOutputFile = new File(environmentDir, "methane" + File.separator + "outputFiles.lua");
							updateTree(methaneOutputFile);
						}
						
						//hydrolysis
						if(plotSelect.getSelectedIndex() == 1) {
							HydrolysisSelector.showSelector(numHydrolysis);
							
							HydrolysisSelector.okBtn.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent arg0) {
									String reactor = (String) HydrolysisSelector.reactorList.getSelectedItem();
									File hydrolysisOutputFile = new File(environmentDir, reactor + File.separator + "outputFiles.lua");
									updateTree(hydrolysisOutputFile);
								}
							});
							
						}
						
						//Storage
						if(plotSelect.getSelectedIndex() == 2) {
							File storageOutputFile = new File(environmentDir, "storage_hydrolyse" + File.separator + "outputFiles.lua");
							updateTree(storageOutputFile);
						}
					}
				}
				
				//Load from File
				if(plotSelect.getSelectedIndex() == 3) {
					JFileChooser dirChooser = new JFileChooser();
			        FileNameExtensionFilter filter = new FileNameExtensionFilter("LUA", "lua");
			        dirChooser.setFileFilter(filter);
					dirChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
					int result = dirChooser.showOpenDialog(mainPanel);
					if (result == JFileChooser.APPROVE_OPTION) {
					    File selectedPath = dirChooser.getSelectedFile();
					    System.out.println("Selected dir: " + selectedPath.getAbsolutePath());					    
					    updateTree(selectedPath);
					}
					else
						System.out.println("Invalid path!");
				}
				
			}
	    });
	    
	    clearBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				DefaultMutableTreeNode root = (DefaultMutableTreeNode) model.getRoot();
				int numFiles = root.getChildCount();
				for(int i=0; i<numFiles; i++) {
					DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode) root.getChildAt(i);	    
					int numParams = fileNode.getChildCount();
					
					for(int j=0; j<numParams; j++) {
						DefaultMutableTreeNode paramNode = (DefaultMutableTreeNode) fileNode.getChildAt(j);
						Object userObject = paramNode.getUserObject();
						
						if(userObject instanceof TreeNodeCheckBox) {
							((TreeNodeCheckBox) userObject).setSelected(false);
						}
					}
				}
				//model.reload();
				mainPanel.revalidate();
				mainPanel.repaint();
			}
			
	    });
	    
		return cont;
	}
	
	private static void updateTree(File outputFilesPath) {
		System.out.println("updateTree --> " + outputFilesPath);
		
		DefaultMutableTreeNode root;
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		
		if(outputFilesPath == null) {//Create an empty tree
			OutputEntry empty = new OutputEntry();
			empty.setName("...");
	        root = new DefaultMutableTreeNode(empty);
	        model.setAsksAllowsChildren(true);
	        model.setRoot(root);
			model.reload();
			return;
		}
		
		if(outputFilesPath.isFile()) {
			filepath = outputFilesPath;
			OutputLoader loader = new OutputLoader(outputFilesPath);
			try {
				loader.load();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			List<OutputEntry> data = loader.getData();

			root = new DefaultMutableTreeNode("Parameters");
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
			model.setAsksAllowsChildren(false);
		}
		else { //Create an empty tree
			OutputEntry empty = new OutputEntry();
			empty.setName("...");
	        root = new DefaultMutableTreeNode(empty);
	        model.setAsksAllowsChildren(true);
	        
	        JFrame frame = new JFrame();
			frame.setLocationRelativeTo(mainPanel);
			frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			JOptionPane.showMessageDialog(frame,
				    "Invalid Path.",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		
		
		//Updating Tree		
		model.setRoot(root);
		model.reload();
	}
	
	private static DefaultMutableTreeNode[] fetchPaths(JTree tree) {
		ArrayList<DefaultMutableTreeNode> sel = new ArrayList<DefaultMutableTreeNode>();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) ((DefaultTreeModel) tree.getModel()).getRoot();
		int numFiles = root.getChildCount();
		System.out.println("numFiles: " + numFiles);
		for(int i=0; i<numFiles; i++) {
			DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode) root.getChildAt(i);	    
			int numParams = fileNode.getChildCount();
			
			for(int j=0; j<numParams; j++) {
				DefaultMutableTreeNode paramNode = (DefaultMutableTreeNode) fileNode.getChildAt(j);
				Object userObject = paramNode.getUserObject();
				
				if(userObject instanceof TreeNodeCheckBox) {
					boolean isSelected = ((TreeNodeCheckBox) userObject).isSelected();
					System.out.println("userObject: " + isSelected);
					if(isSelected) {
						sel.add(paramNode);
					}
				}
			}
		}
	   
		DefaultMutableTreeNode[] returnArr = new DefaultMutableTreeNode[sel.size()];
		returnArr = sel.toArray(returnArr);    	
		return returnArr;
	}
	
	@MethodInfo(name="Plot", hide=false, valueName="Trajectory[][]", hideCloseIcon=true, num=1)
	public static ArrayList<ArrayList<Trajectory>> getValues() throws IOException, InterruptedException {		
		
		Set<String> filenames = new HashSet<String>();
		DefaultMutableTreeNode[] sel = fetchPaths(tree);
		if(sel.length == 0) {
			System.out.println("Nothing to plot.");
			return null;
		}
		
		// Get all different filenames from the selected entries
		for(DefaultMutableTreeNode treeNode : sel) {
			Object userObject = treeNode.getUserObject();
			if(userObject instanceof TreeNodeCheckBox) {
				TreeNodeCheckBox userNodeObject = (TreeNodeCheckBox) userObject;
				OutputEntry treeEntry = userNodeObject.getEntry();
				if(treeEntry.isValue()) {
					filenames.add(treeEntry.getFilename());
				} 		
			}			
		}
		
		// Create an XYSerie for every selected entry
		List<Pair<OutputEntry, Trajectory>> dataSetList = new ArrayList<Pair<OutputEntry, Trajectory>>();
		for(DefaultMutableTreeNode treeNode : sel) {
			Object userObject = treeNode.getUserObject();
			if(userObject instanceof TreeNodeCheckBox) {
				OutputEntry treeEntry = ((TreeNodeCheckBox) userObject).getEntry();
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
						t.add(x_col.get(i), col.get(i));
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
	
	@MethodInfo(name="SamplePlot", hide=false, valueName="Trajectory[][]", hideCloseIcon=false)
	public static ArrayList<ArrayList<Trajectory>> samplePlot() {
		Trajectory t1 = new Trajectory("T1");
		Trajectory t2 = new Trajectory("T2");
		Trajectory t3 = new Trajectory("T3");
		Trajectory t4 = new Trajectory("T4");
		Trajectory t5 = new Trajectory("T5");
		Trajectory t6 = new Trajectory("T6");
		t1.setLabel("2x+3");
		t2.setLabel("3x-4");
		t3.setLabel("sin(x)");
		t4.setLabel("cos(y)");
		t5.setLabel("x²");
		t6.setLabel("x³");
		
		t1.setTitle("Linear");
		t2.setTitle("Linear");
		t3.setTitle("Trigonometric");
		t4.setTitle("Trigonometric");
		t5.setTitle("Exponents");
		t6.setTitle("Exponents");
		for(int i=0; i<1000; i++) {
			double x = (double) i/100;
			t1.add(x, (double) 2*x+3);
			t2.add(x, (double) 3*x-4);
			t3.add(x, (double) Math.sin(x));
			t4.add(x, (double) Math.cos(x));
			System.out.println("sin(" + x + ")=" + Math.sin(x));
			t5.add(x, (double) Math.pow(x, 2));
			t6.add(x, (double) Math.pow(x, 3));
		}
		
		ArrayList<ArrayList<Trajectory>> a = new ArrayList<ArrayList<Trajectory>>();
		
		ArrayList<Trajectory> firstPlot = new ArrayList<Trajectory>();
		firstPlot.add(t1);
		firstPlot.add(t2);
		ArrayList<Trajectory> secondPlot = new ArrayList<Trajectory>();
		secondPlot.add(t3);
		secondPlot.add(t4);
		ArrayList<Trajectory> thirdPlot = new ArrayList<Trajectory>();
		thirdPlot.add(t5);
		thirdPlot.add(t6);
		
		a.add(firstPlot);
		a.add(secondPlot);
		a.add(thirdPlot);
		return a;
	}
	
	@MethodInfo(hide = true)
	public static void main(String args[]) throws IOException, InterruptedException{
		//File f = new File("/home/paul/Schreibtisch/smalltest/aceto/biogas_80h_2_STAGE_PL_ACETO/methane/outputFiles.lua");
		
	    JFrame frame = new JFrame();
	    load(2);   	    
		frame.add(mainPanel);
		
		frame.setSize(300, 500);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);	
		
	}
	
	
}
