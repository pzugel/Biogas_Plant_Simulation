package vrl.biogas.biogascontrol.specedit.treetable;
 
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreeModel;
 
/**
 * Implementation of the TreeTable from http://www.hameister.org/JavaSwingTreeTable.html
 * @author Jörn Hameisterl
 *
 */
public class MyTreeTableCellRenderer extends JTree implements TableCellRenderer {
	private static final long serialVersionUID = 1L;

    protected int visibleRow;
 
    private MyTreeTable treeTable;
 
    public MyTreeTableCellRenderer(MyTreeTable treeTable, TreeModel model) {
        super(model);
        this.treeTable = treeTable;
 
        setRowHeight(getRowHeight());
    }
 
    @Override
	public void setRowHeight(int rowHeight) {
        if (rowHeight > 0) {
            super.setRowHeight(rowHeight);
            if (treeTable != null && treeTable.getRowHeight() != rowHeight) {
                treeTable.setRowHeight(getRowHeight());
            }
        }
    }
 
    @Override
	public void setBounds(int x, int y, int w, int h) {
        super.setBounds(x, 0, w, treeTable.getHeight());
    }
 
    @Override
	public void paint(Graphics g) {
        g.translate(0, -visibleRow * getRowHeight());
 
        super.paint(g);
    }
 
    @Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {	     
    	if (isSelected) {
    		setBackground(table.getSelectionBackground());		
    	}
        else {
        	setBackground(table.getBackground());
        }

        visibleRow = row;
        return this;
    }
}