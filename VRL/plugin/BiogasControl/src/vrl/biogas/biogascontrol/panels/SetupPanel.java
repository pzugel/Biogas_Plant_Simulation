package vrl.biogas.biogascontrol.panels;

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
import javax.swing.ScrollPaneConstants;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * JTabbedPane: Setup tab <br>
 * Displayed in the {@link vrl.biogas.biogascontrol.BiogasControl}/{@link vrl.biogas.biogascontrol.BiogasUserControl} panel
 * @author Paul Zügel
 *
 */
public class SetupPanel {
	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
	
	private JPanel setupPanel;
	private JTree environment_tree;
	private DefaultTreeModel environment_tree_model;
	private File environment_path;
	private JTextField dir;
		
	public JButton clearBtn;
	public JButton openBtn;
	public JButton createBtn;
	public JButton loadBtn;
	public boolean environment_ready;		
	public boolean mergePreexisting;	
	
	public SetupPanel() {
		setupPanel = new JPanel();
		createPanel(false);
	}
	
	public SetupPanel(boolean userDefined) {		
		setupPanel = new JPanel();
		createPanel(userDefined);
	}

	public JPanel getPanel() {
		return setupPanel;
	}
	
	private void createPanel(final boolean userDefined) {
		environment_ready = false;
		mergePreexisting = false;
		
		openBtn = new JButton("...");
		createBtn = new JButton("Create");
		createBtn.setBackground(Color.WHITE);
		clearBtn = new JButton("Clear");
		clearBtn.setForeground(Color.RED);
		clearBtn.setBackground(Color.WHITE);
		loadBtn = new JButton("Load");
		loadBtn.setBackground(Color.WHITE);
		
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
            	TableLayoutConstants.FILL,
            	0.06}};
            	
        setupPanel.setLayout(new TableLayout(size));
        setupPanel.setBorder(BiogasControlClass.BORDER);
        
        setupPanel.add(text, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstants.LEFT, TableLayoutConstants.CENTER));
        setupPanel.add(environment_tree_pane, new TableLayoutConstraints(1, 1, 1, 8, TableLayoutConstants.CENTER, TableLayoutConstants.CENTER));
        setupPanel.add(dir, new TableLayoutConstraints(3, 1, 3, 1, TableLayoutConstants.FULL, TableLayoutConstants.TOP));
        setupPanel.add(openBtn, new TableLayoutConstraints(4, 1, 4, 1, TableLayoutConstants.FULL, TableLayoutConstants.TOP));
        setupPanel.add(createBtn, new TableLayoutConstraints(3, 3, 4, 3, TableLayoutConstants.CENTER, TableLayoutConstants.TOP));
        setupPanel.add(clearBtn, new TableLayoutConstraints(3, 5, 4, 5, TableLayoutConstants.CENTER, TableLayoutConstants.TOP));
        setupPanel.add(loadBtn, new TableLayoutConstraints(3, 7, 4, 7, TableLayoutConstants.CENTER, TableLayoutConstants.TOP));
        
        //Register Events
		openBtn.addActionListener(new ActionListener() {
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
	    		
		createBtn.addActionListener(new ActionListener() {
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
						JOptionPane.showMessageDialog(setupPanel,
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
	    		
		clearBtn.addActionListener(new ActionListener() {
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
				
		loadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser dirChooser = new JFileChooser();
				dirChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
				dirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int result = dirChooser.showOpenDialog(setupPanel);
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedPath = dirChooser.getSelectedFile();
					File summary = new File(selectedPath, "simulation_summary.txt");
					try {
						load_environment(summary, selectedPath, userDefined);
					} catch (IOException e) {
						e.printStackTrace();
					}					
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
		BiogasControlClass.feedingPanelObj.setControls(false);
		BiogasControlClass.settingsPanelObj.simStarttime.setEnabled(true);
	}
	
	public void update_tree(File path) {
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
	
	//TODO Will the strucutre be correct (when loading from LabView project)? What if project was userDefined?
	void load_environment(File summary, File path, boolean userDefined) throws IOException {
		String endtime = "";
		try {
		    boolean finished = false;
		    boolean correctStructure = true;
		    String loadStruct = "";
		    String struct = "";
			Scanner myReader = new Scanner(summary);			
			while (myReader.hasNextLine()) {
		        String data = myReader.nextLine();
		        if(data.startsWith("FINISHED") && data.contains("true")) {
		        	finished = true;
		        } else if(data.startsWith("STRUCTURE")) {
		        	loadStruct = data.substring(data.indexOf("=")+1, data.length());
		        	struct = BiogasControlClass.simulationPanelObj.plantStructure.getText();
		        	if(!loadStruct.equals(struct)) {
		        		correctStructure = false;
		        	}
		        } else if(data.startsWith("ENDTIME")) {
		        	endtime = data.substring(data.indexOf("=")+1, data.length());
		        }
			}
			myReader.close();
			if(finished) {
				if(correctStructure) {
					System.out.println("Finished!");
					dir.setText(path.toString());
					BiogasControlClass.workingDirectory = path;
					BiogasControlClass.simulationPanelObj.workingDirectory.setText(path.toString());
					BiogasControlClass.simulationPanelObj.iteration.setText("0");
					BiogasControlClass.simulationPanelObj.activeElement.setText("");
					BiogasControlClass.simulationPanelObj.simulationLog.setText("** Environment loaded ... \n");
					BiogasControlClass.simulationPanelObj.runtime.setText("00:00:00");
					BiogasControlClass.feedingPanelObj.setControls(true);
					BiogasControlClass.feedingPanelObj.nextTimestep.setText(String.valueOf(endtime));
					BiogasControlClass.settingsPanelObj.simStarttime.setValue(Integer.valueOf(endtime));
		        	BiogasControlClass.settingsPanelObj.simStarttime.setEnabled(false);
				    this.environment_path = path;
				    this.environment_ready = true;
				    this.mergePreexisting = true;
				    update_tree(path);
				}
				else {
					if(userDefined) {
						JOptionPane.showMessageDialog(setupPanel,
							    "Please make sure the simulation you are trying to load uses the same plant structure.",
							    "Information",
							    JOptionPane.INFORMATION_MESSAGE);
					}
					else {
						JOptionPane.showMessageDialog(setupPanel,
							    "The simulation you are trying to load uses a different plant structure:\n -->" 
							    		+ struct 
							    		+ "\n-->"
							    		+ loadStruct,
							    "Incorrect Structure",
							    JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			else {
				int doRepair = JOptionPane.showConfirmDialog(setupPanel,
				    "The simulation from the selected environment seems not to have finished properly.\n"
				    + "Do you wish to try and repair it?",
				    "Broken environment ERROR",
				    JOptionPane.ERROR_MESSAGE,
				    JOptionPane.YES_NO_OPTION);
			
				if (doRepair == JOptionPane.YES_OPTION) {
				    repair_environment(summary, path);
				} 
			}
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
		BiogasControlClass.feedingPanelObj.setControls(true);
		this.mergePreexisting = false;
		
		DefaultMutableTreeNode newRoot = new DefaultMutableTreeNode("biogasVRL_" + sdf.format(timestamp));
		//File simulationFiles = new File(BiogasControlClass.projectPath, "simulation_files");
		
		if(!userDefined) {
			//Methane
			if(BiogasControl.struct.methane()) {
				File methaneDir = new File(dir, "methane");
				if (!methaneDir.exists()){
					methaneDir.mkdirs();
				}
				File methaneSpecfile = new File(BiogasControl.settingsPanelObj.methane_path.getText());
				Files.copy(methaneSpecfile.toPath(),
						new File(methaneDir, "methane_startfile.lua").toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
				newRoot.add(new DefaultMutableTreeNode("methane"));
			}
			
			//Hydrolysis
			int num = BiogasControl.struct.numHydrolysis();
			String[] names = BiogasControl.struct.hydrolysisNames();
			for(int i=0; i<num; i++) {
				File hydroDir = new File(dir, names[i]);
				if (!hydroDir.exists()){
					hydroDir.mkdirs();
				}	
				File hydrolysisSpecfile = new File(BiogasControl.settingsPanelObj.hydrolysis_path.getText());
				Files.copy(hydrolysisSpecfile.toPath(), 
						new File(hydroDir, "hydrolysis_startfile.lua").toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
				newRoot.add(new DefaultMutableTreeNode(names[i]));
			}
			
			//Storage Hydrolysis
			if(BiogasControl.struct.storage()) {
				File storageDir = new File(dir, "storage_hydrolysis");
				if (!storageDir.exists()){
					storageDir.mkdirs();
				}	
				newRoot.add(new DefaultMutableTreeNode("storage_hydrolysis"));
			}
		} else { //User Defined structure
			
			//Methane			
			File methaneDir = new File(dir, "methane");
			if (!methaneDir.exists()){
				methaneDir.mkdirs();
			}
			File methaneSpecfile = new File(BiogasUserControl.settingsPanelObj.methane_path.getText());
			Files.copy(methaneSpecfile.toPath(),
					new File(methaneDir, "methane_startfile.lua").toPath(), 
					StandardCopyOption.REPLACE_EXISTING);
			newRoot.add(new DefaultMutableTreeNode("methane"));
			
			
			//Hydrolysis
			int num = BiogasUserControl.numHydrolysis;
			for(int i=0; i<num; i++) {
				File hydroDir = new File(dir, "hydrolysis_" + i);
				if (!hydroDir.exists()){
					hydroDir.mkdirs();
				}	
				File hydrolysisSpecfile = new File(BiogasUserControl.settingsPanelObj.hydrolysis_path.getText());
				Files.copy(hydrolysisSpecfile.toPath(), 
						new File(hydroDir, "hydrolysis_startfile.lua").toPath(), 
						StandardCopyOption.REPLACE_EXISTING);
				newRoot.add(new DefaultMutableTreeNode("hydrolysis_" + i));
			}
			
			//Storage Hydrolysis
			File hydrolysisStorageDir = new File(dir, "storage_hydrolysis");
			if (!hydrolysisStorageDir.exists()){
				hydrolysisStorageDir.mkdirs();
			}	
			newRoot.add(new DefaultMutableTreeNode("storage_hydrolysis"));
			
		}
		environment_tree_model.setRoot(newRoot);
		environment_tree_model.reload();
	}
	
	void repair_environment(File summary, File path) throws IOException {
		String[] elements = path.list(new FilenameFilter() {
			@Override
			public boolean accept(File current, String name) {
				return new File(current, name).isDirectory();
			}
		});
		System.out.println("Elements: " + Arrays.toString(elements));
		
		/*
		 * Check at which timesteps every element finished and get the minimum
		 * Determin the endtime
		 */
		int numTimesteps = Integer.MAX_VALUE;
		int endtime = Integer.MAX_VALUE;
		for(String elem : elements) {
			File elemPath = new File(path, elem);
			String[] elementFolders = elemPath.list(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});
			int numFolders = elementFolders.length;
			
			int maxFolder = Integer.MIN_VALUE;
			for(String folderName : elementFolders) {
				maxFolder = Math.max(maxFolder, Integer.valueOf(folderName));
			}
			maxFolder += 1;
			
			if(elem.contains("hydrolysis") && !elem.contains("storage")){
				numTimesteps = Math.min(numTimesteps, numFolders);
				endtime = Math.min(endtime, maxFolder);
			} else if(elem.contains("methane")) {
				numTimesteps = Math.min(numTimesteps, numFolders);
				endtime = Math.min(endtime, maxFolder);
			}
			
			
		}
		System.out.println("All elements should have " + numTimesteps + " timesteps with endtime at " + endtime);
		
		/*
		 * Remove timesteps from elements that surpassed the minimum timestep 
		 */
		int maxFolderNumber = endtime - 1;
		for(String elem : elements) {
			File elemPath = new File(path, elem);
			String[] elementFolders = elemPath.list(new FilenameFilter() {
				@Override
				public boolean accept(File current, String name) {
					return new File(current, name).isDirectory();
				}
			});
			int numFolders = elementFolders.length;
			if(numFolders > numTimesteps) {
				System.out.println("Element " + elem + " has too many timesteps!");
				//int maxTime = Integer.MIN_VALUE;
				for(String timeFolder : elementFolders) {
					if(Integer.valueOf(timeFolder) > maxFolderNumber) {
						File timePath = new File(elemPath, timeFolder);
						System.out.println("Delete Folder: " + timePath);
						String[] subFiles = timePath.list();
						for(String file : subFiles){
						    File currentFile = new File(timePath.getPath(), file);
						    currentFile.delete();
						}
						timePath.delete();
					}
				}				
			}
		}
		
		/*
		 * Remove timesteps from all files that surpassed the predetermined endtime 
		 */
		for(String elem : elements) {
			System.out.println("Checking " + elem + " files: ");
			File elemPath = new File(path, elem);
			String[] pathnames = elemPath.list();
			for (String pathname : pathnames) {
				int etxPos = pathname.lastIndexOf('.');
				if(pathname.substring(etxPos+1).equals("txt")) {
					System.out.println("--> Checking " + pathname);
					File filePath = new File(elemPath, pathname);
					if(filePath.exists()) {
						Scanner lineIter = new Scanner(filePath);
						String fileString = "";
						while(lineIter.hasNextLine()) {
							
							String line = lineIter.nextLine();
							Pattern p = Pattern.compile("^[0-9.]+");
							Matcher m = p.matcher(line);
							if(m.find()) {
								String timeStamp = m.group(0); 
								if(!(Double.valueOf(timeStamp)>endtime)) {
									fileString += line + "\n";
								} else {
									System.out.println("\t --> Removed timestamp " + timeStamp);
								}
							} else {
								fileString += line + "\n";
							}
						}
						lineIter.close();
						
						Writer output = new BufferedWriter(new FileWriter(filePath));
						output.append(fileString);
						output.close();
					}
					
				}
			}
		}
		
		//TODO Fix simulationSummary
		
		JOptionPane.showMessageDialog(setupPanel,
			    "Repair precedure done, try to reload the environment!",
			    "Done",
			    JOptionPane.INFORMATION_MESSAGE);
		
	}
	
	void check_environment(File path) {
		
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{
		File path = new File("/home/paul/Schreibtisch/Simulations/VRL/Full/2STAGE");
		File summary = new File(path, "simulation_summary.txt");
		SetupPanel p = new SetupPanel();
		p.repair_environment(summary, path);

	}
}
