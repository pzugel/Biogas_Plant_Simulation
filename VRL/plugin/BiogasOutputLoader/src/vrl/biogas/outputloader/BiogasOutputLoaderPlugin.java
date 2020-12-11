package vrl.biogas.outputloader;

import java.awt.BorderLayout;
import java.awt.Container;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;

@ComponentInfo(name="BiogasOutputLoader", 
category="Biogas", 
description="BiogasOutputLoader Component")
public class BiogasOutputLoaderPlugin implements Serializable{
	private static final long serialVersionUID = 1L;
	
	@MethodInfo(name="Load", hide=false, 
			hideCloseIcon=true, num=1)
	public static void load(
			@ParamInfo(name = " ",
				      nullIsValid = false,
				      style = "load-dialog",
				      options = "endings=[\"lua\"]; invokeOnChange=true") File path) 
				    		  throws FileNotFoundException {
		OutputLoader loader = new OutputLoader(path);
		loader.load();
		List<OutputEntry> data = loader.getData();
		for(OutputEntry entry: data)
			System.out.println(entry.getIndent() + " " 
					+ entry.getName() + entry.getUnit() + " "
					+ entry.getFilename());
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Parameters");
		DefaultMutableTreeNode lastParent = null;
		
		for(OutputEntry entry: data) {
			if(entry.getIndent() == 0) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getName());
				root.add(node);
				lastParent = node;
			}
			else {
				DefaultMutableTreeNode param = new DefaultMutableTreeNode(entry.getName());
				lastParent.add(param);
			}
		}
		
		JFrame frame = new JFrame();
		frame.setTitle("Plotter"); 
		frame.setLocationRelativeTo(null);
		frame.setSize(300, 450);
		
		JPanel topPanel = new JPanel();
	    JPanel btnPanel = new JPanel();
	    
	    topPanel.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);
		
		JTree tree = new JTree(root);	
	    JScrollPane scrollPane = new JScrollPane(tree);
	    topPanel.add(scrollPane,BorderLayout.CENTER);
	    
	    JButton plotButton = new JButton("Plot");
	    btnPanel.add(plotButton);

		frame.setVisible(true);
	}
	/*
	public static void main(String args[]) throws IOException{
		File path = new File("/home/paul/Schreibtisch/outputFiles.lua");
		OutputLoader loader = new OutputLoader(path);
		loader.load();
		List<OutputEntry> data = loader.getData();
		for(OutputEntry entry: data)
			System.out.println(entry.getIndent() + " " 
					+ entry.getName() + entry.getUnit() + " "
					+ entry.getFilename());
		
		DefaultMutableTreeNode root = new DefaultMutableTreeNode("Parameters");
		DefaultMutableTreeNode lastParent = null;
		
		for(OutputEntry entry: data) {
			if(entry.getIndent() == 0) {
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(entry.getName());
				root.add(node);
				lastParent = node;
			}
			else {
				DefaultMutableTreeNode param = new DefaultMutableTreeNode(entry.getName());
				lastParent.add(param);
			}
		}
		JFrame frame = new JFrame();
		frame.setTitle("Plotter"); 
		frame.setLocationRelativeTo(null);
		frame.setSize(300, 450);
		
		JPanel topPanel = new JPanel();
	    JPanel btnPanel = new JPanel();
	    
	    topPanel.setLayout(new BorderLayout());
        frame.add(topPanel, BorderLayout.CENTER);
        frame.add(btnPanel, BorderLayout.SOUTH);
		
		JTree tree = new JTree(root);	
	    JScrollPane scrollPane = new JScrollPane(tree);
	    topPanel.add(scrollPane,BorderLayout.CENTER);
	    
	    JButton plotButton = new JButton("Plot");
	    btnPanel.add(plotButton);

		frame.setVisible(true);
	}
	*/
}
