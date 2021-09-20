package vrl.biogas.biogascontrol.specedit;

import javax.swing.JFrame;

import vrl.biogas.biogascontrol.specedit.treetable.MyAbstractTreeTableModel;
import vrl.biogas.biogascontrol.specedit.treetable.MyDataModel;
import vrl.biogas.biogascontrol.specedit.treetable.MyDataNode;
import vrl.biogas.biogascontrol.specedit.treetable.MyTreeTable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Loads the validation data into the tree 
 * @author Paul Zügel
 */
public class LoadFileData extends JFrame implements Serializable{
	private static final long serialVersionUID=1; 
	public MyAbstractTreeTableModel treeTableModel;
	static ArrayList<ValiTableEntry> listData;
	static MyTreeTable myTreeTable;
	
	public LoadFileData(ArrayList<ValiTableEntry> data) {
		listData = data;
		treeTableModel = new MyDataModel(generate(0, null));
		myTreeTable = new MyTreeTable(treeTableModel, listData.size());
	}
	
	public MyTreeTable getTreeTable() {
		return myTreeTable;
	}
	
	public MyAbstractTreeTableModel getModel() {
		return treeTableModel;
	}
	
 	private List<Integer> getChildren(int index){
		if(listData.get(index).isValueField)
			return java.util.Collections.emptyList();
		List<Integer> children = new ArrayList<Integer>();
		int indent = listData.get(index).getIndent();
		for(int i=index+1; i<listData.size(); i++) {
			if(listData.get(i).getIndent()==indent) {
				break;
			}
			if(listData.get(i).getIndent()==indent+1) {
				children.add(i);
			}
		}
		return children;
	}
	
	private MyDataNode generate(int index, List<MyDataNode> children){
		if(listData.get(index).isValueField)
		{
			children.add(new MyDataNode(
					listData.get(index).getName(), 
					listData.get(index).getType(), 
					listData.get(index).getSpecVal(), 
					null));	
		}
		else
		{
			if(index == 0) {
				List<MyDataNode> rootNodes = new ArrayList<MyDataNode>();
				MyDataNode root = new MyDataNode(
						listData.get(index).getName(), 
			    		"", 
			    		"", 
			    		rootNodes);
				for(int i : getChildren(index)) {
					generate(i, rootNodes);
				}	
				return root;
			}
			else
			{
				List<MyDataNode> myChildren = new ArrayList<MyDataNode>();
				children.add(new MyDataNode(
						listData.get(index).getName(), 
						"", 
						"", 
						myChildren));
				for(int i : getChildren(index)) {
					generate(i, myChildren);
				}		
			}
		}	
		return null;
	}

}
