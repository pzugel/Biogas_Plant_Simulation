package vrl.biogas.biogascontrol.panels;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import layout.TableLayout;
import layout.TableLayoutConstants;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.BiogasControl;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;


public class SetupPanel {
	private static final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	
	static JPanel setupPanel;
	public static boolean environment_ready;
	public File environment_path;
	static JTree environment_tree;
	static DefaultTreeModel environment_tree_model;
	static JTextField dir;
	
	public static boolean mergePreexisting; //TODO: Check if needs to be static
	
	public static JButton clear_Btn;
	
	public SetupPanel() {
		new SetupPanel(false);
	}
	
	public SetupPanel(boolean userDefined) {
		environment_ready = false;
		mergePreexisting = false;
		setupPanel = new JPanel();
		createPanel(userDefined);
	}

	public JPanel getPanel() {
		return setupPanel;
	}
	
	private void createPanel(final boolean userDefined) {
		
		JButton open_Btn = new JButton("...");
		JButton create_Btn = new JButton("Create");
		create_Btn.setBackground(Color.WHITE);
		clear_Btn = new JButton("Clear");
		clear_Btn.setForeground(Color.RED);
		clear_Btn.setBackground(Color.WHITE);
		JButton load_Btn = new JButton("Load");
		load_Btn.setBackground(Color.WHITE);
		
		dir = new JTextField(8);
		dir.setPreferredSize(new Dimension(100,25));
		dir.setEditable(false);
		dir.setBackground(Color.WHITE);
		
		// Tree
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("...");
		environment_tree_model = new DefaultTreeModel(root);
		environment_tree = new JTree(environment_tree_model);
		environment_tree.setBorder(BorderFactory.createLineBorder(Color.black));
		JScrollPane environment_tree_pane = new JScrollPane(environment_tree,
			      ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
			      ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		environment_tree_pane.setPreferredSize(new Dimension(280,290));
		
		JLabel text = new JLabel("<html><body>Create a working environtment for your<br>selected plant structure:</body></html>");

        double size[][] =
            {{0.1, 0.5, 0.01, 0.22, 0.07, TableLayoutConstants.FILL},
             {0.2, //Text 0
            	0.05, //Directory 1
            	0.01,  
            	0.08, //Create 3
            	0.01, 
            	0.08, //Clear 5
            	0.01, 
            	0.08, //Load 7
            	//0.18, 
            	//0.1, 
            	//0.1, 
            	TableLayoutConstants.FILL,
            	0.06}};
        setupPanel.setLayout(new TableLayout(size));
        setupPanel.setBorder(BiogasControlClass.border);
        
        setupPanel.add(text, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstants.LEFT, TableLayoutConstants.CENTER));
        setupPanel.add(environment_tree_pane, new TableLayoutConstraints(1, 1, 1, 8, TableLayoutConstants.CENTER, TableLayoutConstants.CENTER));
        setupPanel.add(dir, new TableLayoutConstraints(3, 1, 3, 1, TableLayoutConstants.FULL, TableLayoutConstants.TOP));
        setupPanel.add(open_Btn, new TableLayoutConstraints(4, 1, 4, 1, TableLayoutConstants.FULL, TableLayoutConstants.TOP));
        setupPanel.add(create_Btn, new TableLayoutConstraints(3, 3, 4, 3, TableLayoutConstants.CENTER, TableLayoutConstants.TOP));
        setupPanel.add(clear_Btn, new TableLayoutConstraints(3, 5, 4, 5, TableLayoutConstants.CENTER, TableLayoutConstants.TOP));
        setupPanel.add(load_Btn, new TableLayoutConstraints(3, 7, 4, 7, TableLayoutConstants.CENTER, TableLayoutConstants.TOP));
        
        //Register Events
		open_Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser dirChooser = new JFileChooser();
				dirChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = dirChooser.showOpenDialog(setupPanel);
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedPath = dirChooser.getSelectedFile();
				    dir.setText(selectedPath.toString());
				    
				    environment_path = selectedPath;
				    //create_environment(selectedPath);
				    System.out.println("Selected dir: " + selectedPath.getAbsolutePath());
				}
				else
					System.out.println("Invalid path!");
			}
	    });    
		
		create_Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) throws NullPointerException{
				try {
					if (environment_path.isDirectory()) {
						create_environment(environment_path, userDefined);
						environment_ready = true;
						BiogasControlClass.settingsPanelObj.simStarttime.setEnabled(true);
						BiogasControlClass.simulationPanelObj.iteration.setText("0");
						BiogasControlClass.simulationPanelObj.activeElement.setText("");
						BiogasControlClass.simulationPanelObj.simulationLog.setText("");
						BiogasControlClass.simulationPanelObj.runtime.setText("00:00:00");
					}
					else {
						JFrame frame = new JFrame();
						frame.setLocationRelativeTo(BiogasControl.panel);
						frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						JOptionPane.showMessageDialog(frame,
						    "Not a valid path.",
						    "Information",
						    JOptionPane.INFORMATION_MESSAGE);
						System.out.println("Not a valid path!");
					}
						
				}
				catch(NullPointerException e) {
					System.out.print("Path not initialized!");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }); 
		
		clear_Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				clear_tree();
				BiogasControlClass.settingsPanelObj.simStarttime.setEnabled(true);
				BiogasControlClass.simulationPanelObj.iteration.setText("0");
				BiogasControlClass.simulationPanelObj.activeElement.setText("");
				BiogasControlClass.simulationPanelObj.simulationLog.setText("");
				BiogasControlClass.simulationPanelObj.runtime.setText("00:00:00");
				if(userDefined) {
					BiogasControlClass.simulationPanelObj.plantStructure.setText("User defined");
				} else {
					BiogasControlClass.simulationPanelObj.plantStructure.setText(BiogasControl.struct.name());	
				}		
			}
		});
		
		load_Btn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser dirChooser = new JFileChooser();
				dirChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = dirChooser.showOpenDialog(setupPanel);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedPath = dirChooser.getSelectedFile();
					File summary = new File(selectedPath, "simulation_summary.txt");
					load_environment(summary, selectedPath);					
				}
				else
					System.out.println("Invalid path!");
			}
		});
	}
	
	public void clear_tree() {
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("...");
		environment_tree_model.setRoot(root);
		environment_tree_model.reload();
		environment_ready = false;
		dir.setText("");
		BiogasControlClass.settingsPanelObj.simStarttime.setEnabled(true);
	}
	
	void load_environment(File summary, File path) {
		try {
		    boolean finished = false;
			Scanner myReader = new Scanner(summary);
			while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        if(data.startsWith("FINISHED") && data.contains("true")) {
		        	finished = true;
		        } else if(data.startsWith("STRUCTURE")) {
		        	String struct = data.substring(data.indexOf("=")+1, data.length());
		        	BiogasControlClass.simulationPanelObj.plantStructure.setText(struct);
		        } else if(data.startsWith("ENDTIME")) {
		        	String endtime = data.substring(data.indexOf("=")+1, data.length());
		        	BiogasControlClass.settingsPanelObj.simStarttime.setValue(Integer.valueOf(endtime));
		        	BiogasControlClass.settingsPanelObj.simStarttime.setEnabled(false);
		        }
			}
			myReader.close();
			if(finished) {
				System.out.println("Finished!");
				dir.setText(path.toString());
				BiogasControlClass.workingDirectory = path;
				BiogasControlClass.simulationPanelObj.workingDirectory.setText(path.toString());
				BiogasControlClass.simulationPanelObj.iteration.setText("0");
				BiogasControlClass.simulationPanelObj.activeElement.setText("");
				BiogasControlClass.simulationPanelObj.simulationLog.setText("** Environment loaded ... \n");
				BiogasControlClass.simulationPanelObj.runtime.setText("00:00:00");
			    this.environment_path = path;
			    environment_ready = true;
			    mergePreexisting = true;
			    DefaultMutableTreeNode root = new DefaultMutableTreeNode(path.getName());
			    String[] elementDirs = path.list(new FilenameFilter() {
			    	  @Override
			    	  public boolean accept(File current, String name) {
			    	    return new File(current, name).isDirectory();
			    	  }
		    	});
			    for(String e : elementDirs) {
			    	DefaultMutableTreeNode elem = new DefaultMutableTreeNode(e);
			    	root.add(elem);
			    	File element = new File(path, e);
			    	String[] timeDirs = element.list(new FilenameFilter() {
				    	  @Override
				    	  public boolean accept(File current, String name) {
				    	    return new File(current, name).isDirectory();
				    	  }
			    	});
			    	
			    	//Sort timesteps
			    	 Arrays.sort(timeDirs, new Comparator<String>() {
			    	        @Override
			    	        public int compare(String o1, String o2) {
			    	            return Integer.valueOf(o1).compareTo(Integer.valueOf(o2));
			    	        }
			    	});

			    	for(String time : timeDirs) {
			    		DefaultMutableTreeNode t = new DefaultMutableTreeNode(time);
			    		elem.add(t);
			    	}
			    }
				environment_tree_model.setRoot(root);
				environment_tree_model.reload();
			}
			else
				JOptionPane.showMessageDialog(setupPanel,
				    "The simulation from the selected environment seems not to have finished properly.",
				    "Warning",
				    JOptionPane.WARNING_MESSAGE);
		} catch (FileNotFoundException e) {
			JOptionPane.showMessageDialog(setupPanel,
				    "Path is not a valid biogas environment!",
				    "Error",
				    JOptionPane.ERROR_MESSAGE);
		}
	}
	
	void create_environment(File path, boolean userDefined) throws IOException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		File dir = new File(path, "biogasVRL_" + sdf.format(timestamp));
		BiogasControlClass.workingDirectory = dir;
		BiogasControlClass.simulationPanelObj.workingDirectory.setText(dir.toString());
		SetupPanel.mergePreexisting = false;
		
		DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode("biogasVRL_" + sdf.format(timestamp));
		File simulationFiles = new File(BiogasControlClass.projectPath, "simulation_files");
		
		if(!userDefined) {
			//Methane
			if(BiogasControl.struct.methane()) {
				File methaneDir = new File(dir, "methane");
				if (!methaneDir.exists()){
					methaneDir.mkdirs();
				}
				Files.copy(new File(simulationFiles, "methane.lua").toPath(),
						new File(methaneDir, "methane.lua").toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
				newRoot.add(new DefaultMutableTreeNode("methane"));
			}
			
			//Hydrolysis
			int num = BiogasControl.struct.numHydrolysis();
			for(int i=0; i<num; i++) {
				File hydroDir = new File(dir, "hydrolyse_" + i);
				if (!hydroDir.exists()){
					hydroDir.mkdirs();
				}	
				Files.copy(new File(simulationFiles, "hydrolyse.lua").toPath(), 
						new File(hydroDir, "hydrolysis_startfile.lua").toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
				newRoot.add(new DefaultMutableTreeNode("hydrolyse_" + i));
			}
			
			//Storage
			if(BiogasControl.struct.storage()) {
				File storageDir = new File(dir, "storage_hydrolyse");
				if (!storageDir.exists()){
					storageDir.mkdirs();
				}	
				newRoot.add(new DefaultMutableTreeNode("storage_hydrolyse"));
			}
		} else { //User Defined structure
			
			//Methane			
			File methaneDir = new File(dir, "methane");
			if (!methaneDir.exists()){
				methaneDir.mkdirs();
			}
			Files.copy(new File(simulationFiles, "methane.lua").toPath(),
					new File(methaneDir, "methane.lua").toPath(), 
					StandardCopyOption.REPLACE_EXISTING);
			newRoot.add(new DefaultMutableTreeNode("methane"));
			
			
			//Hydrolysis
			int num = BiogasUserControl.numHydrolysis;
			for(int i=0; i<num; i++) {
				File hydroDir = new File(dir, "hydrolyse_" + i);
				if (!hydroDir.exists()){
					hydroDir.mkdirs();
				}	
				Files.copy(new File(simulationFiles, "hydrolyse.lua").toPath(), 
						new File(hydroDir, "hydrolysis_startfile.lua").toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
				newRoot.add(new DefaultMutableTreeNode("hydrolyse_" + i));
			}
			
			//Storage
			File storageDir = new File(dir, "storage_hydrolyse");
			if (!storageDir.exists()){
				storageDir.mkdirs();
			}	
			newRoot.add(new DefaultMutableTreeNode("storage_hydrolyse"));
		}
		environment_tree_model.setRoot(newRoot);
		environment_tree_model.reload();
	}
}
