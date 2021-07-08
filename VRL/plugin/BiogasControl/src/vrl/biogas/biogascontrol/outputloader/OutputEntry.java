package vrl.biogas.biogascontrol.outputloader;

/**
 * Class representing a file entry in the outputFiles.lua file
 * @author paul
 */
public class OutputEntry {
	public OutputEntry(){};

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
	
	@Override
	public String toString() {
		return this.name;	
	}
	
	public int getIndent() {
		return this.indent;
	}
	public void setIndent(int ind) {
		this.indent = ind;
	}

	
	public boolean isValue() {
		return this.isValue;
	}
	public void setIsValue(boolean isVal) {
		this.isValue = isVal;
	}

	
	public String getName() {
		return this.name;
	}
	public void setName(String n) {
		this.name = n;
	}
	
	
	public String getFilename() {
		return this.filename;
	}
	public void setFilename(String file) {
		this.filename = file;
	}
	
	
	public int getColumn() {
		return this.column;
	}
	public void setColumn(int col) {
		this.column = col;
	}
	
	
	public String getUnit() {
		return this.unit;
	}
	public void setUnit(String u) {
		this.unit = u;
	}
	
	
	public int getXValueColumn() {
		return this.xValueColumn;
	}
	public void setXValueColumn(int xcol) {
		this.xValueColumn = xcol;
	}
	
	
	public String getXValueUnit() {
		return this.xValueUnit;
	}
	public void setXValueUnit(String unit) {
		this.xValueUnit = unit;
	}
	
	
	public String getXValueName() {
		return this.xValueName;
	}
	public void setXValueName(String name) {
		this.xValueName = name;
	}
};
