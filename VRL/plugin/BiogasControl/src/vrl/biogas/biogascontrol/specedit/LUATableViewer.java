package vrl.biogas.biogascontrol.specedit;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControl;

import java.awt.BorderLayout;
import java.awt.Color;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;
import javax.swing.tree.TreePath;

@ComponentInfo(name="Initializer", 
category="Biogas", 
description="JTreeTable Component")
public class LUATableViewer implements Serializable{
	private static final long serialVersionUID = 1L;
	
	static ArrayList<ValiTableEntry> parameters;
	static public JPanel panel;
	static MyTreeTable myTreeTable;
	static MyAbstractTreeTableModel myTreeTableModel; //Maybe not needed
	static public File valFile;
	static public File specFile;
	static JScrollPane scrollPane;
	
	public static void editor() throws FileNotFoundException {		
		//parameters = (new ValidationParser(valFile, new ArrayList<ValiTableEntry>())).getOutput();
		parameters = new ArrayList<ValiTableEntry>();
		new SpecificationParser(specFile, parameters);
		
		LoadFileData data = new LoadFileData(parameters);
		myTreeTable = data.getTreeTable();
		myTreeTableModel = data.getModel();
		
		MyTreeTableCellRenderer render = myTreeTable.getTreeTableRenderer();
		for(int i=0; i<render.getRowCount(); i++)
			parameters.get(i).setPath(render.getPathForRow(i));	
		updatePanel();
		//LUATableType container = new LUATableType();
		//container.setViewValue(panel);
	}
	
	private static void updatePanel() {
		myTreeTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		myTreeTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		myTreeTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		panel = new JPanel(new BorderLayout());
		
	    JPanel btnPanel = new JPanel();
	    scrollPane = new JScrollPane(myTreeTable);

	    panel.add(scrollPane, BorderLayout.CENTER);
	    panel.add(btnPanel, BorderLayout.SOUTH);
		
        JButton saveBtn = new JButton("Save");
        JButton saveAsBtn = new JButton("Save As...");
        JButton loadValBtn = new JButton("Load Validation");
        JButton valBtn = new JButton("Validate");
        
        saveBtn.setBackground(BiogasControl.BUTTON_BLUE);
        saveAsBtn.setBackground(BiogasControl.BUTTON_BLUE);
        loadValBtn.setBackground(BiogasControl.BUTTON_BLUE);
        valBtn.setBackground(BiogasControl.BUTTON_BLUE);
        valBtn.setForeground(Color.decode("#008000"));
        
        btnPanel.add(saveBtn);
        btnPanel.add(saveAsBtn);
        btnPanel.add(loadValBtn);
        btnPanel.add(valBtn);
        
        addTreeListener();           
        
        saveAsBtn.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	JFileChooser fileChooser = new JFileChooser();
	            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	            JFrame parent = new JFrame();
	            
	            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
	            LocalDateTime now = LocalDateTime.now();
	            String dateTime = dtf.format(now);
	            
	            fileChooser.setSelectedFile(new File("specificationFile_" + dateTime + ".lua"));
	            int result = fileChooser.showSaveDialog(parent);
	            if (result == JFileChooser.APPROVE_OPTION) {
	                File selectedFile = fileChooser.getSelectedFile();
	                try {
						new SpecificationFileWriter(selectedFile, parameters);
					} catch (IOException e1) {
						e1.printStackTrace();
					}
	                System.out.println("Selected file: " + selectedFile.getAbsolutePath());;
	            }	
	        }
        });

        saveBtn.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	try {
					new SpecificationFileWriter(specFile, parameters);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	        }
	    });
        
        loadValBtn.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	JFileChooser fileChooser = new JFileChooser();
	            fileChooser.setCurrentDirectory(new File(System.getProperty("user.home")));
	            JFrame parent = new JFrame();
	            int result = fileChooser.showOpenDialog(parent);
	            if (result == JFileChooser.APPROVE_OPTION) {
	                File selectedFile = fileChooser.getSelectedFile();
	                valFile = selectedFile;
	                try {
						new ValidationParser(valFile, parameters);
												
						LoadFileData data = new LoadFileData(parameters);
						myTreeTable = data.getTreeTable();
						myTreeTable.getColumnModel().getColumn(0).setPreferredWidth(200);
						myTreeTable.getColumnModel().getColumn(1).setPreferredWidth(100);
						myTreeTable.getColumnModel().getColumn(2).setPreferredWidth(100);
						myTreeTableModel = data.getModel();
						MyTreeTableCellRenderer render = myTreeTable.getTreeTableRenderer();
						for(int i=0; i<render.getRowCount(); i++)
							parameters.get(i).setPath(render.getPathForRow(i));	
						panel.remove(scrollPane);	
						scrollPane = new JScrollPane(myTreeTable);
						panel.add(scrollPane);
						addTreeListener();
						panel.revalidate();
						panel.repaint();
						
						//updatePanel();
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
	            }
	        }
        });

        valBtn.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String name = parameters.get(12).getName(); 
        		String type = parameters.get(12).getType(); 
        		String val = parameters.get(12).getSpecVal();
        		System.out.println("Row 12: " + name + " " + type + " " + val);
        		SpecValidation specVal = new SpecValidation(parameters);
        		if(specVal.isValid()) {
        			JOptionPane.showMessageDialog(null, "Specification is valid!", "Validation", 
        					JOptionPane.INFORMATION_MESSAGE);
        			myTreeTable.setErrorParams(new ArrayList<TreePath>());
        			panel.repaint();
        		}
        		else {
        			JOptionPane.showMessageDialog(null, specVal.getValMessage(), "Validation", 
        					JOptionPane.ERROR_MESSAGE);
        			myTreeTable.setErrorParams(specVal.getErrorParams());
        			panel.repaint();
        		}
        	}
        });
	}
	
	/*
	@MethodInfo(name="Files", valueStyle = "multi-out", hide=false,
			 hideCloseIcon=true, num=1)
	@OutputInfo(style = "multi-out",
	            elemNames = {"SimFile", "Parameters"},
	            elemTypes = {File.class, List.class})
	public Object[] loadFiles(
			 @ParamGroupInfo(group = "Files")
			 @ParamInfo(name = "Validation File",nullIsValid = false,style = "load-dialog",
		  		options = "endings=[\"lua\"]; invokeOnChange=true") java.io.File valfile,
			 @ParamGroupInfo(group = "Files")
			 @ParamInfo(name = "Specification File",nullIsValid = true,style = "load-dialog",
		  		options = "endings=[\"lua\"]; invokeOnChange=true") java.io.File specfile,
			 @ParamGroupInfo(group = "Files")
			 @ParamInfo(name = "Simulation File",nullIsValid = false,style = "load-dialog",
		  		options = "endings=[\"lua\"]; invokeOnChange=true") java.io.File simfile,
			 @ParamInfo(name = "Show Validation") boolean showvali){
		 valFile = valfile;
		 specFile = specfile;
		 simFile = simfile;
		 showVali = showvali;
		 return new Object[] {simFile, parameters};
	}
	*/
	
	private static void addTreeListener() {
		AbstractAction action = new AbstractAction()
        {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e)
            {
				
                TableCellListener tcl = (TableCellListener)e.getSource();
                TreePath p = myTreeTable.getTreeTableRenderer().getPathForRow(tcl.getRow());
                System.out.println("Value Change " + p.toString());
                System.out.println("Row   : " + tcl.getRow());
                System.out.println("Column: " + tcl.getColumn());
                System.out.println("Old   : " + tcl.getOldValue());
                System.out.println("New   : " + tcl.getNewValue());
                
                //TODO: Not nice but works
                String pathString = p.toString();
                for(ValiTableEntry entry : parameters) { 
                	if(entry.getPath().toString().equals(pathString)) {
                		entry.setSpecVal((String) tcl.getNewValue());
                	}
          
                }
            }
        };
        new TableCellListener(myTreeTable, action);
	}
	
	public static void main(String args[]) throws IOException{

		System.out.println("Main:");
		
		//valFile = new File("/home/paul/Schreibtisch/Biogas_plant_setup/VRL/simulation_files/hydro_vali.lua");
		specFile = new File("/home/paul/Schreibtisch/Biogas_plant_setup/VRL/simulation_files/hydrolyse.lua");
		editor();
		
		JFrame frame = new JFrame("");
		frame.add(panel);
		frame.setSize(200, 300);
		frame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
	
}
