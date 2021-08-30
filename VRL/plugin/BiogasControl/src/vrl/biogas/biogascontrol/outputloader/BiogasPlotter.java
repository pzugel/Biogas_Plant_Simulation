package vrl.biogas.biogascontrol.outputloader;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ObjectInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import eu.mihosoft.vrl.math.*;
import eu.mihosoft.vrl.reflection.Pair;
import layout.TableLayout;
import layout.TableLayoutConstants;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.MainPanelContainerType;
import vrl.biogas.biogascontrol.panels.HydrolysisSelector;

/**
 * Main panel for the outputFiles tree in VRL
 * @author paul
 */
@ComponentInfo(name="BiogasPlotter", 
	category="Biogas", 
	description="Plot Component")
@ObjectInfo(name = "BiogasPlotter")
public class BiogasPlotter implements Serializable{
	private static final long serialVersionUID = 1L;
	
	static CheckBoxTree tree;
	static List<Trajectory> trajectories;
	static File filepath;
	static JPanel mainPanel;
	
	private boolean userDefined;
	
	@MethodInfo(name="Load", hide=false, interactive=true, num=1)
	public JComponent loadBiogas(
			@ParamInfo(name = "ControlPanel",
	    		nullIsValid = false,
	    		options = "invokeOnChange=true") BiogasControl main) throws FileNotFoundException {	
		userDefined = false;
		return load(BiogasControl.struct.numHydrolysis());
	}
	
	@MethodInfo(name="LoadUserDefined", hide=false, interactive=true, num=1)
	public JComponent loadBiogasUser(
			@ParamInfo(name = "ControlPanel",
	    		nullIsValid = false,
	    		options = "invokeOnChange=true") BiogasUserControl main) throws FileNotFoundException {	
		userDefined = true;
		return load(BiogasUserControl.numHydrolysis);
	}
	
	/**
	 * Initialize the BiogasPlotter panel
	 * @param numHydrolysis
	 * @return
	 * @throws FileNotFoundException
	 */
	private JComponent load(final int numHydrolysis) throws FileNotFoundException {		
		mainPanel = new JPanel(new BorderLayout());
		
		JPanel upperPanel = new JPanel();
		JButton loadBtn = new JButton("Load");
		loadBtn.setBackground(BiogasControlClass.BUTTON_BLUE);
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
	    
	    mainPanel.add(upperPanel, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstants.CENTER, TableLayoutConstants.CENTER));
        mainPanel.add(treeScrollPane, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
	    
	    MainPanelContainerType cont = new MainPanelContainerType();
	    cont.setViewValue(mainPanel);
	    
	    /*
	     * Initialize the tree on mouseclick event
	     */
	    loadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				boolean isReady = BiogasControlClass.setupPanelObj.environment_ready;
				if(!isReady && plotSelect.getSelectedIndex() != 3) {
					JOptionPane.showMessageDialog(mainPanel,
						    "There is currently no simulation to load.",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
					updateTree(null);
				}
				
				if(isReady && plotSelect.getSelectedIndex() != 3) {
					final File environmentDir = BiogasControlClass.workingDirectory;
					
					//Methane
					if(plotSelect.getSelectedIndex() == 0) {
						File methaneOutputFile = new File(environmentDir, "methane" + File.separator + "outputFiles.lua");
						if(methaneOutputFile.exists()) {
							updateTree(methaneOutputFile);	
						} else {
							JOptionPane.showMessageDialog(mainPanel,
								    "Nothing to load.",
								    "Warning",
								    JOptionPane.WARNING_MESSAGE);
							System.out.println("Methane outputFiles not found: " + methaneOutputFile);
							updateTree(null);
						}					
					}
					
					//Hydrolysis
					if(plotSelect.getSelectedIndex() == 1) {
						final HydrolysisSelector selector = new HydrolysisSelector();
						selector.showSelector(numHydrolysis, mainPanel, userDefined);
						
						selector.okBtn.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent arg0) {
								String reactor = (String) selector.reactorList.getSelectedItem();
								File hydrolysisOutputFile = new File(environmentDir, reactor + File.separator + "outputFiles.lua");
								if(hydrolysisOutputFile.exists()) {
									updateTree(hydrolysisOutputFile);	
								} else {
									JOptionPane.showMessageDialog(mainPanel,
										    "Nothing to load.",
										    "Warning",
										    JOptionPane.WARNING_MESSAGE);
									System.out.println("Hydrolysis outputFiles not found: " + hydrolysisOutputFile);
									updateTree(null);
								}	
							}
						});
						
					}
					
					//Storage
					if(plotSelect.getSelectedIndex() == 2) {
						File storageOutputFile = new File(environmentDir, "storage_hydrolysis" + File.separator + "outputFiles.lua");
						if(storageOutputFile.exists()) {
							updateTree(storageOutputFile);	
						} else {
							JOptionPane.showMessageDialog(mainPanel,
								    "Nothing to load.",
								    "Warning",
								    JOptionPane.WARNING_MESSAGE);
							System.out.println("Storage outputFiles not found: " + storageOutputFile);
							updateTree(null);
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
				
				/*
				 * Add MouseEvent to generate a PopUp-Menu for right clicking a file in the tree.
				 * Allows to select/deselect all parameters of the selected file.
				 */
				MouseAdapter ml = new MouseAdapter() {
				     public void mousePressed(MouseEvent e) {
				         TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
				         if(e.getButton() == MouseEvent.BUTTON3) {
				        	 if(selPath != null) {
				        		 final DefaultMutableTreeNode node = (DefaultMutableTreeNode) selPath.getLastPathComponent();
					        	 if(!node.isLeaf()) {
					        		 JPopupMenu menu = new JPopupMenu();
						        	 JMenuItem selAll = new JMenuItem("Select All");
						        	 JMenuItem unselAll = new JMenuItem("Unselect All");
						        	 menu.add(selAll);
						        	 menu.add(unselAll);
						        	 menu.show(e.getComponent(), e.getX(), e.getY());
						        	 
						        	 selAll.addActionListener(new ActionListener() {
										@Override
										public void actionPerformed(ActionEvent arg0) {
											selectAll(node);
										}							 
						        	 });
						        	 unselAll.addActionListener(new ActionListener() {
											@Override
											public void actionPerformed(ActionEvent arg0) {
												unselectAll(node);
											}							        		 
						        	 });
					        	 }
				        	 }
				         }
				     }
				 };
				 tree.addMouseListener(ml);			
			}
	    });
	    
		return cont;
	}
	
	private void updateTree(File outputFilesPath) {
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
	        
			JOptionPane.showMessageDialog(mainPanel,
				    "Invalid Path.",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
		
		
		//Updating Tree		
		model.setRoot(root);
		model.reload();
	}
	

	/**
	 * Return all selected nodes from the tree.
	 * Called by getValues() function.
	 * @param tree
	 * @return List of selected nodes
	 */
	private static DefaultMutableTreeNode[] getSelectedNodes(JTree tree) {
		ArrayList<DefaultMutableTreeNode> sel = new ArrayList<DefaultMutableTreeNode>();
		DefaultMutableTreeNode root = (DefaultMutableTreeNode) ((DefaultTreeModel) tree.getModel()).getRoot();
		int numFiles = root.getChildCount();
		for(int i=0; i<numFiles; i++) {
			DefaultMutableTreeNode fileNode = (DefaultMutableTreeNode) root.getChildAt(i);	    
			int numParams = fileNode.getChildCount();
			for(int j=0; j<numParams; j++) {
				DefaultMutableTreeNode paramNode = (DefaultMutableTreeNode) fileNode.getChildAt(j);
				Object userObject = paramNode.getUserObject();
				
				if(userObject instanceof TreeNodeCheckBox) {										
					boolean isSelected = ((TreeNodeCheckBox) userObject).isSelected();
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
	
	/**
	 * Reads all selected parameters from the Tree, loads the according *.txt files
	 * and generates a trajectory for each file
	 * @return Array of Trajectories
	 * @throws IOException
	 * @throws InterruptedException
	 */
	@MethodInfo(name="Plot", hide=false, valueName="Trajectory[][]", hideCloseIcon=true, num=1)
	public static ArrayList<ArrayList<Trajectory>> getValues() throws IOException, InterruptedException {		
		
		Set<String> filenames = new HashSet<String>();
		DefaultMutableTreeNode[] sel = getSelectedNodes(tree);
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
		
		// Create a Trajectory for every selected entry
		List<Pair<OutputEntry, Trajectory>> dataSetList = new ArrayList<Pair<OutputEntry, Trajectory>>();
		for(DefaultMutableTreeNode treeNode : sel) {
			Object userObject = treeNode.getUserObject();
			if(userObject instanceof TreeNodeCheckBox) {
				OutputEntry treeEntry = ((TreeNodeCheckBox) userObject).getEntry();
				if(treeEntry.isValue()) {
					File f = new File(filepath.getParent(), treeEntry.getFilename());
					
					/*
					System.out.println("Plotting from File: " + f);
					System.out.println("--> x value " + treeEntry.getXValueName() + treeEntry.getXValueUnit() 
						+ " from column " + treeEntry.getXValueColumn());
					System.out.println("--> y value " + treeEntry.getName() + treeEntry.getUnit() 
						+ " from column " + treeEntry.getColumn());
					*/
					
					CSVReader reader = new CSVReader(f);
					List<Double> col = reader.getCol(treeEntry.getColumn());
					List<Double> x_col = reader.getCol(treeEntry.getXValueColumn());
					
					Trajectory t = new Trajectory(treeEntry.getName());
					for(int i=0; i<col.size(); i++) {			
						t.add(x_col.get(i), col.get(i));
						//System.out.println("\t --> " + x_col.get(i) + ", " + col.get(i));
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
			System.out.println("Trajectories for " + name);
			ArrayList<Trajectory> fileOutput= new ArrayList<Trajectory>();
			for(Pair<OutputEntry, Trajectory> pair : dataSetList) {
				if(pair.getFirst().getFilename().equals(name)){
					fileOutput.add(pair.getSecond());
					
					System.out.println("\t--> " + pair.getSecond().getLabel() + " " + pair.getSecond().getyAxisLabel());
				}
			}
			outList.add(fileOutput);
		}		
		
		return outList;
	}
	
	
	/**
	 * Some example plots to demonstrate in the PlotDisplay
	 * @return Sample Trajectory
	 */
	@MethodInfo(name="SamplePlot", hide=true, valueName="Trajectory[][]", hideCloseIcon=false)
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
	

	/**
	 * Select all subparameters from a selected node in the Tree.
	 * Called via PopUp Menu by right clicking a file entry in the Tree.
	 * @param node - The selected node
	 */
	private void selectAll(DefaultMutableTreeNode node) {
		TreePath path = new TreePath(node.getPath());
		tree.expandPath(path);
		
		for(int i=0; i<node.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			Object userObject = child.getUserObject();
			
			if(userObject instanceof TreeNodeCheckBox) {										
				((TreeNodeCheckBox) userObject).setSelected(true);
			} 
			
			if(userObject instanceof OutputEntry) {	
				OutputEntry obj = (OutputEntry) userObject;
				TreeNodeCheckBox newNode = new TreeNodeCheckBox(obj.getName(), null, true, obj);
				child.setUserObject(newNode);
			}
		}
		
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		model.reload(node);	
	}
	
	/**
	 * Unselect all subparameters from a selected node in the Tree.
	 * Called via PopUp Menu by right clicking a file entry in the Tree.
	 * @param node - The selected node
	 */
	private void unselectAll(DefaultMutableTreeNode node) {
		for(int i=0; i<node.getChildCount(); i++) {
			DefaultMutableTreeNode child = (DefaultMutableTreeNode) node.getChildAt(i);
			Object userObject = child.getUserObject();
			
			if(userObject instanceof TreeNodeCheckBox) {										
				((TreeNodeCheckBox) userObject).setSelected(false);
			} 
			
			if(userObject instanceof OutputEntry) {	
				OutputEntry obj = (OutputEntry) userObject;
				TreeNodeCheckBox newNode = new TreeNodeCheckBox(obj.getName(), null, false, obj);
				child.setUserObject(newNode);
			}
		}
		
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		model.reload(node);		
	}
}
