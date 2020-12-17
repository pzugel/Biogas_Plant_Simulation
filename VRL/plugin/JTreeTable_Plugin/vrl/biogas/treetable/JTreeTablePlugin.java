package vrl.biogas.treetable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamGroupInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;

@ComponentInfo(name="Initializer", 
category="Biogas", 
description="JTreeTable Component")
public class JTreeTablePlugin implements Serializable{
	private static final long serialVersionUID = 1L;
	
	static List<ValiTableEntry> parameters;
	static JPanel panel;
	static MyTreeTable myTreeTable;
	static MyAbstractTreeTableModel myTreeTableModel; //Maybe not needed
	static File valFile;
	static File specFile;
	static File simFile;
	
	static boolean showVali;
	
	@MethodInfo(name="Editor", hide=false, valueStyle = "multi-out",
			hideCloseIcon=true, num=1)
	@OutputInfo(style = "multi-out",
    	elemNames = {"SimFile", "Parameters"},
    	elemTypes = {JComponent.class, List.class})
	public static Object[] editor() throws FileNotFoundException {		
		parameters = (new ValidationParser(valFile, new ArrayList<ValiTableEntry>())).getOutput();
		new SpecificationParser(specFile, parameters);
		
		LoadFileData data = new LoadFileData(parameters, showVali);
		myTreeTable = data.getTreeTable();
		myTreeTableModel = data.getModel();
		
		MyTreeTableCellRenderer render = myTreeTable.getTreeTableRenderer();
		for(int i=0; i<render.getRowCount(); i++)
			parameters.get(i).setPath(render.getPathForRow(i));	
		updatePanel();
		LUATableType container = new LUATableType();
		container.setViewValue(panel);
		return new Object[]{container, parameters};
	}
	
	private static void updatePanel() {
		myTreeTable.getColumnModel().getColumn(0).setPreferredWidth(200);
		myTreeTable.getColumnModel().getColumn(1).setPreferredWidth(100);
		myTreeTable.getColumnModel().getColumn(2).setPreferredWidth(100);
		panel = new JPanel(new BorderLayout());
		
	    JPanel topPanel = new JPanel();
	    JPanel btnPanel = new JPanel();
	    
	    topPanel.setLayout(new BorderLayout());
	    panel.add(topPanel, BorderLayout.CENTER);
	    panel.add(btnPanel, BorderLayout.SOUTH);
		
        JScrollPane scrollPane = new JScrollPane(myTreeTable);
        scrollPane.setPreferredSize(new Dimension(400, 500));
        topPanel.add(scrollPane,BorderLayout.CENTER);
        
        JButton saveButton = new JButton("Save");
        JButton valButton = new JButton("Validate");
        
        btnPanel.add(saveButton);
        btnPanel.add(valButton);
        
        AbstractAction action = new AbstractAction()
        {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
            {
				
                TableCellListener tcl = (TableCellListener)e.getSource();
                System.out.println("Row   : " + tcl.getRow());
                System.out.println("Column: " + tcl.getColumn());
                System.out.println("Old   : " + tcl.getOldValue());
                System.out.println("New   : " + tcl.getNewValue());
                TreePath p = myTreeTable.getTreeTableRenderer().getPathForRow(tcl.getRow());
                //TODO: Not nice but works
                for(ValiTableEntry entry : parameters) { 
                	if(entry.getPath().equals(p)) {
                		entry.setSpecVal((String) tcl.getNewValue());
                	}
          
                }
            }
        };
        
        new TableCellListener(myTreeTable, action);    

        saveButton.addActionListener(new ActionListener() {
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

        valButton.addActionListener(new ActionListener() {
        	@Override
        	public void actionPerformed(ActionEvent e) {
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
	
	/*
	public static void main(String args[]) throws IOException{

		System.out.println("Main:");
		
		valFile = new File("/home/paul/Schreibtisch/Biogas_plant_setup/example/Test_vali.lua");
		specFile = new File("/home/paul/Schreibtisch/Biogas_plant_setup/example/Test.lua");
		showVali = false;
		editor();
		
		JFrame frame = new JFrame("");
		frame.add(panel);
		frame.setSize(200, 300);
		frame.setVisible(true);
	}
	*/
}
