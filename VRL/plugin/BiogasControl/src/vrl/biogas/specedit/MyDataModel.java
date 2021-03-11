package vrl.biogas.specedit;

public class MyDataModel extends MyAbstractTreeTableModel {
    // Spalten Name.
    static protected String[] columnNames = { "Parameter", "Type", "Value"};
 
    // Spalten Typen.
    static protected Class<?>[] columnTypes = { MyTreeTableModel.class, String.class, String.class};
 
    public MyDataModel(MyDataNode rootNode) {
        super(rootNode);
        root = rootNode;
    }
 
    public Object getChild(Object parent, int index) {
        return ((MyDataNode) parent).getChildren().get(index);
    }
 
 
    public int getChildCount(Object parent) {
        return ((MyDataNode) parent).getChildren().size();
    }
 
 
    public int getColumnCount() {
        return columnNames.length;
    }
 
 
    public String getColumnName(int column) {
        return columnNames[column];
    }
 
 
    public Class<?> getColumnClass(int column) {
        return columnTypes[column];
    }
 
    public Object getValueAt(Object node, int column) {
        switch (column) {
        case 0:
            return ((MyDataNode) node).getName();
        case 1:
            return ((MyDataNode) node).getType();
        case 2:
            return ((MyDataNode) node).getValue();
        default:
            break;
        }
        return null;
    }
 
    public boolean isCellEditable(Object node, int column) {
    	if(column == 1)
    	{
    		return false;
    	}
        return true; // Important to activate TreeExpandListener
    }
 
    public void setValueAt(Object aValue, Object node, int column) {
    	switch (column) {
        case 2:
            ((MyDataNode) node).setValue((String) aValue);
        default:
            break;
        }
    }
 
}