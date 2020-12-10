package vrl.biogas.treetable;

import javax.swing.tree.TreePath;

public class ValiTableEntry {
	int indent = 0;
	String name = "";
	String type = "";
	String defaultVal = "";
	String specVal = "";
	String rangeMin = "";
	String rangeMax = "";
	TreePath path;
	boolean isValueField;
	
	public ValiTableEntry(int indent, String name, String type, String defaultVal, String specVal,
			String rangeMin, String rangeMax){
		this.indent = indent;
		this.name = name;
		this.type = type;
		this.defaultVal = defaultVal;
		this.specVal = specVal;
		this.rangeMin = rangeMin;
		this.rangeMax = rangeMax;
		this.path = null;
	};
	
	public ValiTableEntry(int indent){
		this.indent = indent;
		this.name = "";
		this.type = "";
		this.defaultVal = "";
		this.specVal = "";
		this.rangeMin = "";
		this.rangeMax = "";
		this.path = null;
	}
	
	public void setIndent(int indent) {
		this.indent = indent;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public void setDefaultVal(String defaultVal) {
		this.defaultVal = defaultVal;
	}
	
	public void setSpecVal(String specVal) {
		this.specVal = specVal;
	}
	
	public void setRangeMin(String rangeMin) {
		this.rangeMin = rangeMin;
	}
	
	public void setRangeMax(String rangeMax) {
		this.rangeMax = rangeMax;
	}
	
	public void setPath(TreePath path) {
		this.path = path;
	}
	
	public int getIndent() {
		return indent;
	}
	
	public String getName() {
		return name;
	}
	
	public String getType() {
		return type;
	}
	
	public String getDefaultVal() {
		return defaultVal;
	}
	
	public String getSpecVal() {
		return specVal;
	}
	
	public String getRangeMin() {
		return rangeMin;
	}
	
	public String getRangeMax() {
		return rangeMax;
	}
	
	public TreePath getPath() {
		return path;
	}
	
	public void setValueField(boolean isVal) {
		this.isValueField = isVal;
	}
	
	public boolean isValueField() {
		return isValueField;
	}
}
