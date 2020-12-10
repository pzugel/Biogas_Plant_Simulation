package vrl.biogas.treetable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import java.awt.BorderLayout;
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
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.tree.TreePath;

@ComponentInfo(name="JTreeTablePlugin", 
category="JTreeTablePlugin", 
description="JTreeTable Component")
public class JTreeTablePlugin implements Serializable{
	private static final long serialVersionUID = 1L;
	
	//static LoadFileData data;
	//static ValidationParser valiParser; 
	static List<ValiTableEntry> parameters;
	static JFrame frame;
	static MyTreeTable myTreeTable;
	
	public static void loadValidation(
			@ParamInfo(name = "Validation File:",
		      nullIsValid = true,
		      style = "load-dialog",
		      options = "endings=[\"lua\"]; invokeOnChange=false") File dataFilePath) throws FileNotFoundException {
		
		parameters = (new ValidationParser(dataFilePath, new ArrayList<ValiTableEntry>())).getOutput();
		LoadFileData data = new LoadFileData(parameters);
		myTreeTable = data.getTreeTable();
		
		MyTreeTableCellRenderer render = myTreeTable.getTreeTableRenderer();
		for(int i=0; i<render.getRowCount(); i++)
			parameters.get(i).setPath(render.getPathForRow(i));
		
		updateFrame();
	}
	
	public static void loadSpecification(
			@ParamInfo(name = "Specification File:",
		      nullIsValid = true,
		      style = "load-dialog",
		      options = "endings=[\"lua\"]; invokeOnChange=false") File dataFilePath) throws FileNotFoundException {
		
		new SpecificationParser(dataFilePath, parameters);
		LoadFileData data = new LoadFileData(parameters);
		myTreeTable = data.getTreeTable();

		MyTreeTableCellRenderer render = myTreeTable.getTreeTableRenderer();
		for(int i=0; i<render.getRowCount(); i++)
			parameters.get(i).setPath(render.getPathForRow(i));

		frame.dispose();
		updateFrame();
	}
	
	private static void updateFrame() {
		frame = new JFrame();
		frame.setTitle("Initializer"); 
		frame.setSize(800, 600);
		frame.setLocationRelativeTo(null);
		
	    JPanel topPanel = new JPanel();
	    JPanel btnPanel = new JPanel();
	    
	    topPanel.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);
		
        JScrollPane scrollPane = new JScrollPane(myTreeTable);
        topPanel.add(scrollPane,BorderLayout.CENTER);
        
        JButton saveButton = new JButton("Save");
        JButton valButton = new JButton("Validate");
        JButton closeButton = new JButton("Close");
        
        btnPanel.add(saveButton);
        btnPanel.add(valButton);
        btnPanel.add(closeButton);
        //myTreeTable.getModel()
        
        AbstractAction action = new AbstractAction()
        {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e)
            {
                TableCellListener tcl = (TableCellListener)e.getSource();
                parameters.get(tcl.getRow()).setSpecVal((String) tcl.getNewValue());
                System.out.println("Row   : " + tcl.getRow());
                System.out.println("Column: " + tcl.getColumn());
                System.out.println("Old   : " + tcl.getOldValue());
                System.out.println("New   : " + tcl.getNewValue());
            }
        };

        new TableCellListener(myTreeTable, action);

        frame.setVisible(true);    

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
        
        closeButton.addActionListener(new ActionListener() {
        	@Override
	        public void actionPerformed(ActionEvent e) {
        		frame.dispose();
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
        			frame.repaint();
        		}
        		else {
        			JOptionPane.showMessageDialog(null, specVal.getValMessage(), "Validation", 
        					JOptionPane.ERROR_MESSAGE);
        			myTreeTable.setErrorParams(specVal.getErrorParams());
        			frame.repaint();
        		}
        	}
        });
	}
	/*
	public static void main(String args[]) throws IOException{

		System.out.println("Main:");
		
		File valiPath = new File("/home/paul/Schreibtisch/Biogas_plant_setup/example/Test_vali.lua");
		File specPath = new File("/home/paul/Schreibtisch/Biogas_plant_setup/example/Test.lua");
		
		loadValidation(valiPath);
		loadSpecification(specPath);
		
		System.out.println(parameters.get(2).getName());
		parameters.get(2).setSpecVal("500.12");
		System.out.println("Spec: " + parameters.get(2).getSpecVal());
		System.out.println("Min: " + parameters.get(2).getRangeMin());
		System.out.println("Max: " + parameters.get(2).getRangeMax());

		SpecValidation specVal = new SpecValidation(parameters);
		TreePath p = specVal.getErrorParams().get(0);
		System.out.println("Error Path: " + p.toString());
		System.out.println("Error Line: " + myTreeTable.getTreeTableRenderer().getRowForPath(p));
		System.out.println("Is valid?: " + specVal.isValid());
		System.out.println(specVal.getValMessage());
	}
	*/
}
