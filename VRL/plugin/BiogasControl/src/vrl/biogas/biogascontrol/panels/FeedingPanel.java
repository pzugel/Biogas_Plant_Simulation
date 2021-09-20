package vrl.biogas.biogascontrol.panels;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import javafx.util.Pair;
import layout.TableLayout;
import layout.TableLayoutConstants;
import layout.TableLayoutConstraints;
import vrl.biogas.biogascontrol.BiogasControl;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.elements.functions.UpdateFeeding;

/**
 * JTabbedPane: Feeding tab <br>
 * Displayed in the {@link vrl.biogas.biogascontrol.BiogasControl}/{@link vrl.biogas.biogascontrol.BiogasUserControl} panel
 * @author Paul Zügel
 *
 */
public class FeedingPanel{

	private JPanel feedingPanel;
	private JComboBox<String> comboBox;
	private JButton addBtn;
	private JButton delBtn;
	private JButton saveBtn;
	private JButton loadBtn;
	private boolean userDef;
	private File loadedSpec;
	
	public JTextField nextTimestep;
	public JTable feedingTable;
	
	public FeedingPanel() {
		feedingPanel = new JPanel();
		createPanel(false);
	}
	
	public FeedingPanel(boolean userDefined) {
		feedingPanel = new JPanel();
		createPanel(userDefined);
	}
	
	public JPanel getPanel() {
		return feedingPanel;
	}
	
	public void createPanel(final boolean userDefined) {
		userDef = userDefined;
		double size[][] =
            {{0.06, 0.30, 0.2, 0.05, 0.34, TableLayoutConstants.FILL},
             {0.06, 
            	0.1, //Text 1
            	0.06, //Labels 2
            	0.06, //ComboBox 3
            	0.03,
            	0.50, //Table 5
            	0.06, //Button 6
            	TableLayoutConstants.FILL,
             }};
		feedingPanel.setLayout(new TableLayout(size));
		feedingPanel.setBorder(BiogasControlClass.BORDER);
		
		comboBox = new JComboBox<String>();
		
		nextTimestep = new JTextField("0");
		nextTimestep.setBackground(Color.WHITE);
		nextTimestep.setEditable(false);
		nextTimestep.setEnabled(false);
		nextTimestep.setHorizontalAlignment(JTextField.RIGHT);
		
		String data[][]={};    
		String column[]={"Time","Amount"};  
		feedingTable = new JTable(new DefaultTableModel(data, column));
		
		feedingTable.setBackground(Color.WHITE);
		JScrollPane feedingScrollPane = new JScrollPane(feedingTable);  
		
		JLabel text = new JLabel("<html><body>Add feeding hours and amount for a hydrolysis reactor.</body></html>");
		loadBtn = new JButton("Load");
		feedingPanel.add(text, new TableLayoutConstraints(1, 1, 2, 1, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		feedingPanel.add(new JLabel("Reactor"), new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		feedingPanel.add(new JLabel("Next timestep"), new TableLayoutConstraints(2, 2, 2, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		
		feedingPanel.add(comboBox, new TableLayoutConstraints(1, 3, 1, 3, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		feedingPanel.add(nextTimestep, new TableLayoutConstraints(2, 3, 2, 3, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		
		feedingPanel.add(feedingScrollPane, new TableLayoutConstraints(1, 5, 2, 5, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		feedingPanel.add(loadBtn, new TableLayoutConstraints(1, 6, 2, 6, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		
		JPanel leftSide = new JPanel();
		double leftSize[][] =
            {{0.33, 0.33, TableLayoutConstants.FILL},
             {0.12, //Add 0
            	0.05, 
            	0.12, //Delete 2
            	0.05, 
            	0.12, //Save 4
            	0.03, 
            	TableLayoutConstants.FILL //Text 6
         	}};
		leftSide.setLayout(new TableLayout(leftSize));
		addBtn = new JButton("Add");
		delBtn = new JButton("Delete");
		saveBtn = new JButton("Save");
		final JFormattedTextField timeField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		final JFormattedTextField amountField = new JFormattedTextField(NumberFormat.getNumberInstance(Locale.US));
		timeField.setText("0");
		amountField.setText("0");
		
		leftSide.add(addBtn, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		leftSide.add(timeField, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		leftSide.add(amountField, new TableLayoutConstraints(2, 0, 2, 0, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		leftSide.add(delBtn, new TableLayoutConstraints(0, 2, 2, 2, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		leftSide.add(saveBtn, new TableLayoutConstraints(0, 4, 2, 4, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		leftSide.add(new JScrollPane(), new TableLayoutConstraints(0, 6, 2, 6, TableLayoutConstants.FULL, TableLayoutConstants.FULL));
		feedingPanel.add(leftSide, new TableLayoutConstraints(4, 5, 4, 6, TableLayoutConstants.FULL, TableLayoutConstants.FULL));		
		
		addBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				int time = Integer.parseInt(timeField.getText());
				double amount = Double.parseDouble(amountField.getText());
				addFeeding(time, amount);
			}
	    });    
		
		delBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DefaultTableModel model = (DefaultTableModel) feedingTable.getModel();
				int row = feedingTable.getSelectedRow();
				if(row > -1) {
					model.removeRow(row);	
				}			
			}
	    }); 
		
		loadBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String reactor = (String) comboBox.getSelectedItem();
				File reactorPath = new File(BiogasControlClass.workingDirectory, reactor);	
				File specPath;
				
				if(BiogasControlClass.running.isSelected()) {
					File hydrolysisTimePath;
					
					if(userDefined) {
						hydrolysisTimePath = new File(reactorPath, String.valueOf(BiogasControlClass.currentTime));
					} else {
						hydrolysisTimePath = new File(reactorPath, String.valueOf(BiogasControl.struct.currentTime()));	
					}
										
					File testSpecPath = new File(hydrolysisTimePath, "hydrolysis_checkpoint.lua");								
					if(testSpecPath.exists()) {
						specPath = testSpecPath;
					} else {
						specPath = new File(reactorPath, "hydrolysis_startfile.lua");
					}
				} else {
					specPath = new File(reactorPath, "hydrolysis_startfile.lua");
				}
				loadedSpec = specPath;
				try {
					ArrayList<Pair<String, String>> table = UpdateFeeding.get_feeding_timetable(specPath);
					DefaultTableModel model = (DefaultTableModel) feedingTable.getModel();
					model.setRowCount(0);
					for(Pair<String, String> pair : table) {
						model.addRow(new Object[]{pair.getKey(), pair.getValue()});
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
	    }); 
		
		saveBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DefaultTableModel model = (DefaultTableModel) feedingTable.getModel();
				int rows = model.getRowCount();
				ArrayList<Pair<String, String>> table = new ArrayList<Pair<String, String>>();
				for(int i=0; i<rows; i++) {
					String time = String.valueOf(model.getValueAt(i, 0));
					String amount = String.valueOf(model.getValueAt(i, 1));
					Pair<String, String> timeAmount = new Pair<String, String>(time, amount);
					table.add(timeAmount);
				}
				try {
					UpdateFeeding.update_feeding_timetable(loadedSpec, table);
					JOptionPane.showMessageDialog(feedingPanel,
						    "Saved.",
						    "",
						    JOptionPane.INFORMATION_MESSAGE);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
	    }); 
	    
		setControls(false);
	}
	
	/**
	 * Add a new row to the feeding times.
	 * @param time
	 * @param amount
	 */
	private void addFeeding(int time, double amount) {
		DefaultTableModel model = (DefaultTableModel) feedingTable.getModel();
		int row = feedingTable.getSelectedRow();
		System.out.println("row: " + row);
		if(row > -1) {
			model.insertRow(row+1, new Object[]{time, amount});
		} else {
			model.addRow(new Object[]{time, amount});
		}
		
		System.out.println("time: " + time);
		System.out.println("amount: " + amount);
	}
	
	/**
	 * Activate controls once an environment is loaded.
	 * @param isActive
	 */
	public void setControls(boolean isActive) {
		addBtn.setEnabled(isActive);
		delBtn.setEnabled(isActive);
		saveBtn.setEnabled(isActive);
		loadBtn.setEnabled(isActive);
		
		//Create Bombo Box
		int numHydrolysis;
		if(userDef) {
			numHydrolysis = BiogasUserControl.numHydrolysis;
		} else {
			numHydrolysis = BiogasControl.struct.numHydrolysis();
		}
		
		String[] reactors = new String[numHydrolysis];
		for(int i=0; i<numHydrolysis; i++) {
			if(userDef) {
				reactors[i] = "hydrolysis_" + i;	
			} else {
				reactors[i] = BiogasControl.struct.hydrolysisNames()[i];
			}
			
		}
			
		if(isActive) {
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>(reactors);
			comboBox.setModel(model);
		} else {
			DefaultComboBoxModel<String> model = new DefaultComboBoxModel<String>();
			comboBox.setModel(model);
		}
	}
}
