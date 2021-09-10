package vrl.biogas.biogascontrol.panels;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.WindowConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileNameExtensionFilter;

import layout.TableLayout;
import layout.TableLayoutConstants;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.specedit.LUATableViewer;

/**
 * JTabbedPane: Settings tab <br>
 * Displayed in the {@link vrl.biogas.biogascontrol.BiogasControl}/{@link vrl.biogas.biogascontrol.BiogasUserControl} panel
 * @author Paul Zügel
 *
 */
public class SettingsPanel {
	
	public JPanel settingsPanel;
	public JSpinner simStarttime;
	public JSpinner simEndtime;
	public JSpinner flowValue;
	public JCheckBox autoCleanup;
	
	public JTextField hydrolysis_path;
	public JTextField methane_path;
	public JTextField simulation_path;
	
	public JButton open_hydrolysis_edit;
	public JButton open_methane_edit;
	public JButton open_simulation_edit;
	
	public File simulationFilesPath;
	
	public SettingsPanel() {
		settingsPanel = new JPanel();
		createPanel(false);
	}
	
	public SettingsPanel(boolean userDefined) {
		settingsPanel = new JPanel();
		createPanel(userDefined);
	}
	
	public JPanel getPanel() {
		return settingsPanel;
	}
	
	private void createPanel(final boolean userDefined) {
		SpinnerModel startModel = new SpinnerNumberModel(0, 0, 1000, 1);
		SpinnerModel endModel = new SpinnerNumberModel(1, 1, 1000, 1);
		SpinnerModel flowModel = new SpinnerNumberModel(10, 5, 1000, 0.01); 
		
		simStarttime = new JSpinner(startModel);
		simEndtime = new JSpinner(endModel);
		flowValue = new JSpinner(flowModel);
		autoCleanup = new JCheckBox("Off/On");
		
		
		JLabel simStarttimeLabel = new JLabel("Starttime");
		simStarttimeLabel.setLabelFor(simStarttime);
		
		JLabel simEndtimeLabel = new JLabel("Endtime");
		simEndtimeLabel.setLabelFor(simEndtime);
		
		JLabel flowLabel = new JLabel("Flow [L/h]");
		flowLabel.setLabelFor(flowValue);
		
		JLabel autoCleanupLabel = new JLabel("Auto cleanup?");
		autoCleanupLabel.setLabelFor(autoCleanup);
		
		JLabel text = new JLabel("<html><body>If you want the hydrolysis reactors to start with different"
				+ "<br>initial values, you need to set up a working environtment first.</body></html>");
		
        double header_size[][] =
            {{0.06, 0.18, 0.04, 0.18, 0.04, 0.18, 0.04, 0.18, TableLayoutConstants.FILL},
             {0.06, 
            	0.05, //Label 1
            	0.06, //Spinner 2
            	0.08, 
            	0.15, //Text 4 
            	0.02,
            	0.41, //Elements Panel 6 
            	TableLayoutConstants.FILL}};
        settingsPanel.setLayout(new TableLayout(header_size));
        settingsPanel.setBorder(BiogasControlClass.BORDER);
        
        JPanel line = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);   
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(2));
                g2.drawLine(0, 20, (int) (settingsPanel.getWidth()*0.85), 20);
            }
        };
        
        //Labels
		settingsPanel.add(simStarttimeLabel, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		settingsPanel.add(simEndtimeLabel, new TableLayoutConstraints(3, 1, 3, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		settingsPanel.add(flowLabel, new TableLayoutConstraints(5, 1, 5, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		settingsPanel.add(autoCleanupLabel, new TableLayoutConstraints(7, 1, 7, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		
		//Fields
		settingsPanel.add(simStarttime, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		settingsPanel.add(simEndtime, new TableLayoutConstraints(3, 2, 3, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		settingsPanel.add(flowValue, new TableLayoutConstraints(5, 2, 5, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		settingsPanel.add(autoCleanup, new TableLayoutConstraints(7, 2, 7, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		
		settingsPanel.add(text, new TableLayoutConstraints(1, 4, 8, 4, TableLayoutConstants.CENTER, TableLayoutConstants.FULL));
		settingsPanel.add(line, new TableLayoutConstraints(1, 3, 8, 3, TableLayoutConstants.CENTER, TableLayoutConstants.CENTER));
		
		JPanel elementsPanel = new JPanel();
        double elements_size[][] =
            {{0.09, 0.03, 0.44, 0.01, 0.06, 0.05, 0.2, TableLayoutConstants.FILL},
             {0.18, //Methane
            	0.08, 
            	0.18, //Hydrolysis
            	0.08, 
            	0.18, //Simulation
            	TableLayoutConstants.FILL}};
        elementsPanel.setLayout(new TableLayout(elements_size));
        simulationFilesPath = new File(BiogasControlClass.projectPath, "simulation_files");
        System.out.println("simulationFilesPath: " + simulationFilesPath);
        JButton methane_edit = new JButton("Edit");
        methane_edit.setBackground(BiogasControlClass.BUTTON_BLUE);
        methane_path = new JTextField(5);
        methane_path.setText(new File(simulationFilesPath, "methane.lua").toString());
        methane_path.setEditable(false);
        open_methane_edit = new JButton("...");
        open_methane_edit.setBackground(BiogasControlClass.BUTTON_BLUE);
        
        JButton hydrolysis_edit = new JButton("Edit");
        hydrolysis_edit.setBackground(BiogasControlClass.BUTTON_BLUE);
        hydrolysis_path = new JTextField(5);
        hydrolysis_path.setText(new File(simulationFilesPath, "hydrolysis.lua").toString());
        hydrolysis_path.setEditable(false);
        open_hydrolysis_edit = new JButton("...");
        open_hydrolysis_edit.setBackground(BiogasControlClass.BUTTON_BLUE);
        
        simulation_path = new JTextField(5);
        simulation_path.setText(new File(simulationFilesPath, "Biogas.lua").toString());
        simulation_path.setEditable(false);
        open_simulation_edit = new JButton("...");
        open_simulation_edit.setBackground(BiogasControlClass.BUTTON_BLUE);
        
        //ICONS
		File iconPath = new File(BiogasControlClass.projectPath, "icons");
		
		File hydroIcon_path = new File(iconPath, "hydrolysis_reactor.png");
		File methIcon_path = new File(iconPath, "methane_reactor.png");
		File runIcon_path = new File(iconPath, "run.png");
        ImageIcon hydroIcon = new ImageIcon(hydroIcon_path.toString());
        ImageIcon methIcon = new ImageIcon(methIcon_path.toString());
        ImageIcon runIcon = new ImageIcon(runIcon_path.toString());
        
        elementsPanel.add(new JLabel(hydroIcon), new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        elementsPanel.add(hydrolysis_path, new TableLayoutConstraints(2, 0, 2, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        elementsPanel.add(open_hydrolysis_edit, new TableLayoutConstraints(4, 0, 4, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        elementsPanel.add(hydrolysis_edit, new TableLayoutConstraints(6, 0, 6, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        
        elementsPanel.add(new JLabel(methIcon), new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        elementsPanel.add(methane_path, new TableLayoutConstraints(2, 2, 2, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        elementsPanel.add(open_methane_edit, new TableLayoutConstraints(4, 2, 4, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        elementsPanel.add(methane_edit, new TableLayoutConstraints(6, 2, 6, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        
        elementsPanel.add(new JLabel(runIcon), new TableLayoutConstraints(0, 4, 0, 4, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        elementsPanel.add(simulation_path, new TableLayoutConstraints(2, 4, 2, 4, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        elementsPanel.add(open_simulation_edit, new TableLayoutConstraints(4, 4, 4, 4, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        
        settingsPanel.add(elementsPanel, new TableLayoutConstraints(1, 6, 8, 6, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
        
		simStarttime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if((Integer) simEndtime.getValue() <= (Integer) simStarttime.getValue()) {
					simEndtime.setValue((Integer) simStarttime.getValue() + 1);
				}
					
			}
	    }); 
		
		simEndtime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent arg0) {
				if((Integer) simEndtime.getValue() <= (Integer) simStarttime.getValue()) {
					simEndtime.setValue((Integer) simEndtime.getValue() + 1);
				}
				
				if(BiogasControlClass.running.isSelected()) {
					int cTime;
					if(userDefined) {
						cTime = BiogasControlClass.currentTime;
					} else {
						cTime = BiogasControl.struct.currentTime();
					}
					
					if((Integer) simEndtime.getValue() < cTime+1) {
						simEndtime.setValue(cTime+1);
					}
				}
					
			}
	    }); 
		

		hydrolysis_edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				System.out.println("Click hydro edit");
				if(BiogasControlClass.setupPanelObj.environment_ready) { //Open Via Selector
					int numHydrolysis;
					if(userDefined) {
						numHydrolysis = BiogasUserControl.numHydrolysis;
					} else {
						numHydrolysis = BiogasControl.struct.numHydrolysis();
					}
					final HydrolysisSelector selector = new HydrolysisSelector();
					selector.showSelector(numHydrolysis, userDefined);
					selector.okBtn.addActionListener(new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent arg0) {
							String reactor = (String) selector.reactorList.getSelectedItem();
							System.out.println("Selected: " + reactor);
							try {
								
								File hydrolysisPath = new File(BiogasControlClass.workingDirectory, reactor);
								
								/*
								 * If the simulation is running we check whether a chekpoint has already been created.
								 * If yes we edit the chkpoint, if not we edit the startfile.
								 */
								if(BiogasControlClass.running.isSelected()) {
									JOptionPane.showMessageDialog(settingsPanel,
										    "It is advised to pause the simulation before making changes.",
										    "Warning",
										    JOptionPane.WARNING_MESSAGE);
									
									File hydrolysisTimePath;
									
									if(userDefined) {
										hydrolysisTimePath = new File(hydrolysisPath, String.valueOf(BiogasControlClass.currentTime));
									} else {
										hydrolysisTimePath = new File(hydrolysisPath, String.valueOf(BiogasControl.struct.currentTime()));	
									}
														
									File specFile = new File(hydrolysisTimePath, "hydrolysis_checkpoint.lua");								
									if(specFile.exists()) {
										LUATableViewer.specFile = specFile;
									} else {
										LUATableViewer.specFile = new File(hydrolysisPath, "hydrolysis_startfile.lua");
									}
									
								} else {
									System.out.println("Not running");
									/*
									 * If we load an environment we would like the editor to display the checkpoint
									 * file from the previous timestep.
									 * Otherwise we take the startfile.
									 */
									if(BiogasControlClass.setupPanelObj.mergePreexisting) {
										System.out.println("Merge preexisting");
										int previousTimestep = (Integer) BiogasControlClass.settingsPanelObj.simStarttime.getValue() - 1;
										File hydrolysisTimePath = new File(hydrolysisPath, String.valueOf(previousTimestep));
										System.out.println("hydrolysisTimePath: " + hydrolysisTimePath);
										LUATableViewer.specFile = new File(hydrolysisTimePath, "hydrolysis_checkpoint.lua");
									} else {
										LUATableViewer.specFile = new File(hydrolysisPath, "hydrolysis_startfile.lua");
									}										
								}
															
								LUATableViewer.editor();
								JFrame frame = new JFrame("Hydrolysis reactor");
								frame.add(LUATableViewer.panel);
								frame.setSize(700, 600);
								frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
								frame.setLocationRelativeTo(settingsPanel);
								frame.setVisible(true);
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}	
						}
					});
				}
				else { //Load Base File
					try {
						LUATableViewer.specFile = new File(hydrolysis_path.getText());
						
						JOptionPane.showMessageDialog(settingsPanel,
								"You are about to change the base specification file. Please proceed with care.",
							    "Warning",
							    JOptionPane.WARNING_MESSAGE);
						
						LUATableViewer.editor();
						JFrame frame = new JFrame("Hydrolysis base file");
						frame.add(LUATableViewer.panel);
						frame.setSize(700, 600);
						frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						frame.setLocationRelativeTo(settingsPanel);
						frame.setVisible(true);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}		
				}


			}
	    }); 	
		
		methane_edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(BiogasControlClass.setupPanelObj.environment_ready) {
					File methanePath = new File(BiogasControlClass.workingDirectory, "methane");
					
					/*
					 * If the simulation is running we check whether a chekpoint has already been created.
					 * If yes we edit the chkpoint, if not we edit the startfile.
					 */
					if(BiogasControlClass.running.isSelected()) {
						JOptionPane.showMessageDialog(settingsPanel,
							    "It is advised to pause the simulation before making changes.",
							    "Warning",
							    JOptionPane.WARNING_MESSAGE);
						
						File methaneTimePath;
						
						if(userDefined) {
							methaneTimePath = new File(methanePath, String.valueOf(BiogasControlClass.currentTime));
						} else {
							methaneTimePath = new File(methanePath, String.valueOf(BiogasControl.struct.currentTime()));	
						}
											
						File specFile = new File(methaneTimePath, "methane_checkpoint.lua");								
						if(specFile.exists()) {
							LUATableViewer.specFile = specFile;
						} else {
							LUATableViewer.specFile = new File(methanePath, "methane_startfile.lua");
						}
						
					} else {
						/*
						 * If we load an environment we would like the editor to display the checkpoint
						 * file from the previous timestep.
						 * Otherwise we take the startfile.
						 */
						if(BiogasControlClass.setupPanelObj.mergePreexisting) {						
							int previousTimestep = (Integer) BiogasControlClass.settingsPanelObj.simStarttime.getValue() - 1;
							File methaneTimePath = new File(methanePath, String.valueOf(previousTimestep));							
							LUATableViewer.specFile = new File(methaneTimePath, "methane_checkpoint.lua");
						} else {
							LUATableViewer.specFile = new File(methanePath, "methane_startfile.lua");
						}	
					}
					
					try {
						LUATableViewer.editor();
						JFrame frame = new JFrame("Methane reactor");
						frame.add(LUATableViewer.panel);
						frame.setSize(700, 600);
						frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						frame.setLocationRelativeTo(settingsPanel);
						frame.setVisible(true);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}
				else { //Load Base File
					try {
						LUATableViewer.specFile = new File(methane_path.getText());
						
						JOptionPane.showMessageDialog(settingsPanel,
								"You are about to change the base specification file. Please proceed with care.",
							    "Warning",
							    JOptionPane.WARNING_MESSAGE);
						
						LUATableViewer.editor();
						JFrame frame = new JFrame("Methane base file");
						frame.add(LUATableViewer.panel);
						frame.setSize(700, 600);
						frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
						frame.setLocationRelativeTo(settingsPanel);
						frame.setVisible(true);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}		
				}
			}
		});
			
		open_hydrolysis_edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select new hydrolysis base file");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);	
				File startDir;
				if(userDefined) {
					startDir = new File(BiogasUserControl.projectPath, "simulation_files");
				} else {
					startDir = new File(BiogasControl.projectPath, "simulation_files");
				}
				fileChooser.setCurrentDirectory(startDir);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("LUA FILES", "*.lua", "lua");
				fileChooser.setFileFilter(filter);
				
				int result = fileChooser.showOpenDialog(settingsPanel);
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedPath = fileChooser.getSelectedFile();
				    System.out.println("New hydrolysis base file: " + selectedPath.getAbsolutePath());
				    hydrolysis_path.setText(new File(selectedPath.getAbsolutePath()).toString());
				}
				else
					System.out.println("Invalid file!");
			}
		});
		
		
		open_methane_edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select new methane base file");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				File startDir;
				if(userDefined) {
					startDir = new File(BiogasUserControl.projectPath, "simulation_files");
				} else {
					startDir = new File(BiogasControl.projectPath, "simulation_files");
				}
				fileChooser.setCurrentDirectory(startDir);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("LUA FILES", "*.lua", "lua");
				fileChooser.setFileFilter(filter);
				
				int result = fileChooser.showOpenDialog(settingsPanel);
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedPath = fileChooser.getSelectedFile();
				    System.out.println("New methane base file: " + selectedPath.getAbsolutePath());
				    methane_path.setText(new File(selectedPath.getAbsolutePath()).toString());
				}
				else
					System.out.println("Invalid file!");
			}
		});
		
		open_simulation_edit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				JFileChooser fileChooser = new JFileChooser();
				fileChooser.setDialogTitle("Select new simulation file");
				fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				File startDir;
				if(userDefined) {
					startDir = new File(BiogasUserControl.projectPath, "simulation_files");
				} else {
					startDir = new File(BiogasControl.projectPath, "simulation_files");
				}
				fileChooser.setCurrentDirectory(startDir);
				FileNameExtensionFilter filter = new FileNameExtensionFilter("LUA FILES", "*.lua", "lua");
				fileChooser.setFileFilter(filter);
				
				int result = fileChooser.showOpenDialog(settingsPanel);
				if (result == JFileChooser.APPROVE_OPTION) {
				    File selectedPath = fileChooser.getSelectedFile();
				    System.out.println("New simulation file: " + selectedPath.getAbsolutePath());
				    simulation_path.setText(new File(selectedPath.getAbsolutePath()).toString());
				}
				else
					System.out.println("Invalid file!");
			}
		});
	}
}
