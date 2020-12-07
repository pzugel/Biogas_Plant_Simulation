package vrl.biogas.treetable;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;

import java.awt.Container;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

@ComponentInfo(name="JTreeTablePlugin", 
category="JTreeTablePlugin", 
description="JTreeTable Component")
public class JTreeTablePlugin implements Serializable{
	private static final long serialVersionUID = 1L;
	
	static LoadFileData data;
	public void init(
			@ParamInfo(name = "Validation File:",
		      nullIsValid = true,
		      style = "load-dialog",
		      options = "endings=[\"lua\"]; invokeOnChange=false") File dataFilePath) throws FileNotFoundException {
		ValidationParser parser = new ValidationParser(dataFilePath);
		data = new LoadFileData(parser.getOutput());
		MyTreeTable myTreeTable = data.getTreeTable();
		
		JFrame frame = new JFrame();
        Container cPane = frame.getContentPane();
        cPane.add(new JScrollPane(myTreeTable));
        frame.setSize(800, 600);
        frame.setLocationRelativeTo(null);
	    frame.setTitle("Initializer");       
	    //frame.pack();  
	    frame.setVisible(true); 
	}
	
	
	public static void main(String args[]) throws FileNotFoundException{
		System.out.println("Main:");
		java.io.File path = new java.io.File("/home/paul/Schreibtisch/Test_valiTEST.lua");
		ValidationParser parser = new ValidationParser(path);
		List<ValiTableEntry> newList = parser.getOutput();
		for(ValiTableEntry entry : newList) {
			System.out.println("Name: " + entry.getName() 
			+ " Type: " + entry.getType() 
			+ " Val: " + entry.getDefaultVal());
		}
	}
	
}
