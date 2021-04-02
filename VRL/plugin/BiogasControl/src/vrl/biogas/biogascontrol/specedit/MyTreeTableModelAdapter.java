package vrl.biogas.biogascontrol.specedit;
 
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;
 
public class MyTreeTableModelAdapter extends AbstractTableModel {
	private static final long serialVersionUID = 1L;
    JTree tree;
    MyAbstractTreeTableModel treeTableModel;
 
    public MyTreeTableModelAdapter(MyAbstractTreeTableModel treeTableModel, JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;
 
        tree.addTreeExpansionListener(new TreeExpansionListener() {
            @Override
			public void treeExpanded(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
 
            @Override
			public void treeCollapsed(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
        });
    }
 
 
 
    @Override
	public int getColumnCount() {
        return treeTableModel.getColumnCount();
    }
 
    @Override
	public String getColumnName(int column) {
        return treeTableModel.getColumnName(column);
    }
 
    @Override
	public Class<?> getColumnClass(int column) {
        return treeTableModel.getColumnClass(column);
    }
 
    @Override
	public int getRowCount() {
        return tree.getRowCount();
    }
 
    protected Object nodeForRow(int row) {
        TreePath treePath = tree.getPathForRow(row);
        return treePath.getLastPathComponent();
    }
 
    @Override
	public Object getValueAt(int row, int column) {
        return treeTableModel.getValueAt(nodeForRow(row), column);
    }
 
    @Override
	public boolean isCellEditable(int row, int column) {
        return treeTableModel.isCellEditable(nodeForRow(row), column);
    }
 
    @Override
	public void setValueAt(Object value, int row, int column) {
        treeTableModel.setValueAt(value, nodeForRow(row), column);
    }
}