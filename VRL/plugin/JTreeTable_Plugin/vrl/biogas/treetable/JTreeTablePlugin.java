package vrl.biogas.treetable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

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
	
	public void loadValidation(
			@ParamInfo(name = "Validation File:",
		      nullIsValid = true,
		      style = "load-dialog",
		      options = "endings=[\"lua\"]; invokeOnChange=false") File dataFilePath) throws FileNotFoundException {
		
		parameters = (new ValidationParser(dataFilePath, new ArrayList<ValiTableEntry>())).getOutput();
		LoadFileData data = new LoadFileData(parameters);
		myTreeTable = data.getTreeTable();
		
		updateFrame();
	}
	
	public void loadSpecification(
			@ParamInfo(name = "Specification File:",
		      nullIsValid = true,
		      style = "load-dialog",
		      options = "endings=[\"lua\"]; invokeOnChange=false") File dataFilePath) throws FileNotFoundException {
		
		new SpecificationParser(dataFilePath, parameters);
		LoadFileData data = new LoadFileData(parameters);
		myTreeTable = data.getTreeTable();
		
		frame.dispose();
		updateFrame();
	}
	
	private void updateFrame() {
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
	}
	/*
	public static void main(String args[]) throws FileNotFoundException{
		System.out.println("Main:");
		java.io.File valiPath = new java.io.File("/home/paul/Schreibtisch/Biogas_plant_setup/example/Test_vali.lua");
		java.io.File specPath = new java.io.File("/home/paul/Schreibtisch/Biogas_plant_setup/example/Test.lua");
		ValidationParser valiparser = new ValidationParser(valiPath);
		List<ValiTableEntry> newList = valiparser.getOutput();
		System.out.println("#Lines: " + newList.size());
		SpecificationParser specparser = new SpecificationParser(specPath, newList);
		
		int index = 0;
		for(ValiTableEntry entry : newList) {
			System.out.println(index + " Name: " + entry.getName() 
			+ " Type: " + entry.getType() 
			+ " Default: " + entry.getDefaultVal() + " Spec:" + entry.getSpecVal());
			++index;
		}
	}*/
}
