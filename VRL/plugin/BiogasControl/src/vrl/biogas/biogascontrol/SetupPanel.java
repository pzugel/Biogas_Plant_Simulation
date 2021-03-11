package vrl.biogas.biogascontrol;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import layout.TableLayout;
import layout.TableLayoutConstraints;

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
	static boolean environment_ready;
	static File environment_path;
	static JTree environment_tree;
	static DefaultTreeModel environment_tree_model;
	static JTextField dir;
	
	public SetupPanel() {
		environment_ready = false;
		setupPanel = new JPanel();
		createPanel();
	}
	
	JPanel getPanel() {
		return setupPanel;
	}
	
	void createPanel() {
		
		JButton open_Btn = new JButton("...");
		JButton create_Btn = new JButton("Create");
		create_Btn.setBackground(Color.WHITE);
		JButton clear_Btn = new JButton("Clear");
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
			      JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
			      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		environment_tree_pane.setPreferredSize(new Dimension(280,290));
		
		JLabel text = new JLabel("<html><body>Create a working environtment for your<br>selected plant structure:</body></html>");

        double size[][] =
            {{0.1, 0.5, 0.01, 0.22, 0.07, TableLayout.FILL},
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
            	TableLayout.FILL,
            	0.06}};
        setupPanel.setLayout(new TableLayout(size));
        
        //setupPanel.add(new JLabel("0,0"), new TableLayoutConstraints(0, 0, 0, 0, TableLayout.LEFT, TableLayout.TOP));
        //setupPanel.add(new JLabel("0,1"), new TableLayoutConstraints(0, 1, 0, 1, TableLayout.LEFT, TableLayout.TOP));
        //setupPanel.add(new JLabel("0,3"), new TableLayoutConstraints(0, 3, 0, 3, TableLayout.LEFT, TableLayout.TOP));
        //setupPanel.add(new JLabel("0,5"), new TableLayoutConstraints(0, 5, 0, 5, TableLayout.LEFT, TableLayout.TOP));
        setupPanel.add(text, new TableLayoutConstraints(1, 0, 1, 0, TableLayout.LEFT, TableLayout.CENTER));
        setupPanel.add(environment_tree_pane, new TableLayoutConstraints(1, 1, 1, 8, TableLayout.CENTER, TableLayout.CENTER));
        //setupPanel.add(new JLabel("2"), new TableLayoutConstraints(2, 0, 2, 0, TableLayout.LEFT, TableLayout.TOP));
        //setupPanel.add(new JLabel("3,0"), new TableLayoutConstraints(3, 0, 3, 0, TableLayout.LEFT, TableLayout.TOP));
        //setupPanel.add(new JLabel("4,0"), new TableLayoutConstraints(4, 0, 4, 0, TableLayout.LEFT, TableLayout.TOP));
        //setupPanel.add(new JLabel("5,0"), new TableLayoutConstraints(5, 0, 5, 0, TableLayout.LEFT, TableLayout.TOP));
        setupPanel.add(dir, new TableLayoutConstraints(3, 1, 3, 1, TableLayout.FULL, TableLayout.TOP));
        setupPanel.add(open_Btn, new TableLayoutConstraints(4, 1, 4, 1, TableLayout.FULL, TableLayout.TOP));
        setupPanel.add(create_Btn, new TableLayoutConstraints(3, 3, 4, 3, TableLayout.CENTER, TableLayout.TOP));
        setupPanel.add(clear_Btn, new TableLayoutConstraints(3, 5, 4, 5, TableLayout.CENTER, TableLayout.TOP));
        setupPanel.add(load_Btn, new TableLayoutConstraints(3, 7, 4, 7, TableLayout.CENTER, TableLayout.TOP));
        
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
						create_environment(environment_path);
						environment_ready = true;
						SettingsPanel.simStarttime.setEnabled(true);
					}
					else
						System.out.println("Not a valid path!");
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
				DefaultMutableTreeNode root = new DefaultMutableTreeNode("...");
				environment_tree_model.setRoot(root);
				environment_tree_model.reload();
				environment_ready = false;
				dir.setText("");
				SettingsPanel.simStarttime.setEnabled(true);
				SimulationPanel.plantStructure.setText(BiogasControlPlugin.struct.name());
				SimulationPanel.workingDirectory.setText("");
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
		        	SimulationPanel.plantStructure.setText(struct);
		        } else if(data.startsWith("ENDTIME")) {
		        	String endtime = data.substring(data.indexOf("=")+1, data.length());
		        	SettingsPanel.simStarttime.setValue(Integer.valueOf(endtime)+1);
		        	SettingsPanel.simStarttime.setEnabled(false);
		        }
			}
			myReader.close();
			if(finished) {
				System.out.println("Finished!");
				dir.setText(path.toString());
				SimulationPanel.workingDirectory.setText(path.toString());
			    environment_path = path;
			    environment_ready = true;
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
	
	void create_environment(File path) throws IOException {
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		File dir = new File(path, "biogasVRL_" + sdf.format(timestamp));
		BiogasControlPlugin.workingDirectory = dir;
		SimulationPanel.workingDirectory.setText(dir.toString());
		
		DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode("biogasVRL_" + sdf.format(timestamp));
		File simulationFiles = new File(BiogasControlPlugin.projectPath, "simulation_files");
		//Methane
		if(BiogasControlPlugin.struct.methane()) {
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
		int num = BiogasControlPlugin.struct.numHydrolysis();
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
		if(BiogasControlPlugin.struct.storage()) {
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
