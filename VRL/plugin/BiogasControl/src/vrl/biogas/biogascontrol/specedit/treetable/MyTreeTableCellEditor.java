package vrl.biogas.biogascontrol.specedit.treetable;
 
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
 
import javax.swing.AbstractCellEditor;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.table.TableCellEditor;

/**
 * Implementation of the TreeTable from http://www.hameister.org/JavaSwingTreeTable.html
 * @author Jörn Hameister
 *
 */
public class MyTreeTableCellEditor extends AbstractCellEditor implements TableCellEditor {
	private static final long serialVersionUID = 1L;
    private JTree tree;
    private JTable table;
 
    public MyTreeTableCellEditor(JTree tree, JTable table) {
        this.tree = tree;
        this.table = table;
    }
 
    @Override
	public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int r, int c) {
        System.out.println("getTableCellEditorComponent");
    	return tree;
    }
 
    @Override
	public boolean isCellEditable(EventObject e) {
        if (e instanceof MouseEvent) {
            int colunm1 = 0;
            MouseEvent me = (MouseEvent) e;
            int doubleClick = 2;
            @SuppressWarnings("deprecation")
			MouseEvent newME = new MouseEvent(tree, me.getID(), me.getWhen(), me.getModifiers(), me.getX() - table.getCellRect(0, colunm1, true).x, me.getY(), doubleClick, me.isPopupTrigger());
            tree.dispatchEvent(newME);
        }
        return false;
    }
 
    @Override
    public Object getCellEditorValue() {
        return null;
    }
 
}