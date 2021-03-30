package vrl.biogas.biogascontrol.outputloader;

public class OutputEntry {
	OutputEntry(){};

	private int indent = 0;
	//int glyph = 0;
	private boolean isValue = true;
	private String name = "";
	private String filename = "";
	private String unit = ""; 
	private int column = -1;
	
	private String xValueName = "";
	private String xValueUnit = ""; 
	private int xValueColumn = -1;
	
	public String toString() {
		return this.name;	
	}
	
	public int getIndent() {
		return this.indent;
	}
	public void setIndent(int ind) {
		indent = ind;
	}

	
	public boolean isValue() {
		return this.isValue;
	}
	public void setIsValue(boolean isVal) {
		isValue = isVal;
	}

	
	public String getName() {
		return this.name;
	}
	public void setName(String n) {
		name = n;
	}
	
	
	public String getFilename() {
		return this.filename;
	}
	public void setFilename(String file) {
		filename = file;
	}
	
	
	public int getColumn() {
		return this.column;
	}
	public void setColumn(int col) {
		column = col;
	}
	
	
	public String getUnit() {
		return this.unit;
	}
	public void setUnit(String u) {
		unit = u;
	}
	
	
	public int getXValueColumn() {
		return this.xValueColumn;
	}
	public void setXValueColumn(int xcol) {
		xValueColumn = xcol;
	}
	
	
	public String getXValueUnit() {
		return this.xValueUnit;
	}
	public void setXValueUnit(String unit) {
		xValueUnit = unit;
	}
	
	
	public String getXValueName() {
		return this.xValueName;
	}
	public void setXValueName(String name) {
		xValueName = name;
	}
};
