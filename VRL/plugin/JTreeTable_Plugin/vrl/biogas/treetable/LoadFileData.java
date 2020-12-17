package vrl.biogas.treetable;

import javax.swing.JFrame;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class LoadFileData extends JFrame implements Serializable{
	private static final long serialVersionUID=1; 
	public MyAbstractTreeTableModel treeTableModel;
	static List<ValiTableEntry> listData;
	static MyTreeTable myTreeTable;
	static boolean showVali;
	
	public LoadFileData(List<ValiTableEntry> data, boolean showValidation) {
		listData = data;
		showVali = showValidation;
		treeTableModel = new MyDataModel(generate(0, null));
		//treeTableModel.listenerList.add(arg0, arg1);
		myTreeTable = new MyTreeTable(treeTableModel, listData.size());
	}
	
	public MyTreeTable getTreeTable() {
		return myTreeTable;
	}
	
	public MyAbstractTreeTableModel getModel() {
		return treeTableModel;
	}
	
 	private List<Integer> getChildren(int index){
		if(((ValiTableEntry) listData.get(index)).isValueField)
			return java.util.Collections.emptyList();
		List<Integer> children = new ArrayList<Integer>();
		int indent = ((ValiTableEntry) listData.get(index)).getIndent();
		for(int i=index+1; i<listData.size(); i++) {
			if(((ValiTableEntry) listData.get(i)).getIndent()==indent) {
				break;
			}
			if(((ValiTableEntry) listData.get(i)).getIndent()==indent+1) {
				children.add(i);
			}
		}
		return children;
	}
	
	private MyDataNode generate(int index, List<MyDataNode> children){
		if(((ValiTableEntry) listData.get(index)).isValueField)
		{
			if(showVali) {
				children.add(new MyDataNode(
						((ValiTableEntry) listData.get(index)).getName(), 
						((ValiTableEntry) listData.get(index)).getType(), 
						((ValiTableEntry) listData.get(index)).getDefaultVal(), 
						null));	
			}
			else {
				children.add(new MyDataNode(
						((ValiTableEntry) listData.get(index)).getName(), 
						((ValiTableEntry) listData.get(index)).getType(), 
						((ValiTableEntry) listData.get(index)).getSpecVal(), 
						null));	
			}
		}
		else
		{
			if(index == 0) {
				List<MyDataNode> rootNodes = new ArrayList<MyDataNode>();
				MyDataNode root = new MyDataNode(
						((ValiTableEntry) listData.get(index)).getName(), 
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
						((ValiTableEntry) listData.get(index)).getName(), 
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
