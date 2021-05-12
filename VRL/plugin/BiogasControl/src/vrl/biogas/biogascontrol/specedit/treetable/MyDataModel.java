package vrl.biogas.biogascontrol.specedit.treetable;

/**
 * Implementation of the TreeTable from http://www.hameister.org/JavaSwingTreeTable.html
 * @author Jörn Hameister
 *
 */
public class MyDataModel extends MyAbstractTreeTableModel {
    // Row name.
    static protected String[] columnNames = { "Parameter", "Type", "Value"};
 
    // Row types.
    static protected Class<?>[] columnTypes = { MyTreeTableModel.class, String.class, String.class};
 
    public MyDataModel(MyDataNode rootNode) {
        super(rootNode);
        root = rootNode;
    }
 
    @Override
	public Object getChild(Object parent, int index) {
        return ((MyDataNode) parent).getChildren().get(index);
    }
 
 
    @Override
	public int getChildCount(Object parent) {
        return ((MyDataNode) parent).getChildren().size();
    }
 
 
    @Override
	public int getColumnCount() {
        return columnNames.length;
    }
 
 
    @Override
	public String getColumnName(int column) {
        return columnNames[column];
    }
 
 
    @Override
	public Class<?> getColumnClass(int column) {
        return columnTypes[column];
    }
 
    @Override
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
 
    @Override
	public boolean isCellEditable(Object node, int column) {
    	if(column == 1)
    	{
    		return false;
    	}
        return true; // Important to activate TreeExpandListener
    }
 
    @Override
	public void setValueAt(Object aValue, Object node, int column) {
    	switch (column) {
        case 2:
            ((MyDataNode) node).setValue((String) aValue);
        default:
            break;
        }
    }
 
}