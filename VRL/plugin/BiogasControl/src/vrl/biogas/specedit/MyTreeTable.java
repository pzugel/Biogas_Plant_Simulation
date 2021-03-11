package vrl.biogas.specedit;
 
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;
 
public class MyTreeTable extends JTable {
	private static final long serialVersionUID = 1L;
    private MyTreeTableCellRenderer tree;
    private List<TreePath> errorParams;
 
    public MyTreeTable(MyAbstractTreeTableModel treeTableModel, int numEntries) {
        super();
        errorParams = new ArrayList<TreePath>();
        // JTree erstellen.
        tree = new MyTreeTableCellRenderer(this, treeTableModel);
        for(int i=0; i<numEntries; i++) {
        	tree.expandRow(i);
        }
        // Modell setzen.
        super.setModel(new MyTreeTableModelAdapter(treeTableModel, tree));
 
        // Gleichzeitiges Selektieren fuer Tree und Table.
        MyTreeTableSelectionModel selectionModel = new MyTreeTableSelectionModel();
        tree.setSelectionModel(selectionModel); //For the tree
        setSelectionModel(selectionModel.getListSelectionModel()); //For the table
 
        // Renderer fuer den Tree.
        setDefaultRenderer(MyTreeTableModel.class, tree);
        // Editor fuer die TreeTable
        setDefaultEditor(MyTreeTableModel.class, new MyTreeTableCellEditor(tree, this));
        //setBackground(Color.RED);
        
        // Kein Grid anzeigen.
        setShowGrid(false);
 
        // Keine Abstaende.
        setIntercellSpacing(new Dimension(0, 0));
 
    }
    
    public void setErrorParams(List<TreePath> params) {
    	errorParams = params;
    }
    
    public MyTreeTableCellRenderer getTreeTableRenderer() {
    	return tree;
    }
     
    @Override
    public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
    	final Color VERY_LIGHT_RED = new Color(255,102,102);
        Component comp = super.prepareRenderer(renderer, row, col);

        List<Integer> errorRows = new ArrayList<Integer>();   
        for(TreePath p : errorParams)
        	errorRows.add(tree.getRowForPath(p));

        //Object value = getModel().getValueAt(row, 0);
        if (getSelectedRow() == row) {
        	if (errorRows.contains(row) && col == 2) {
                comp.setBackground(VERY_LIGHT_RED);
        	} else {
        		comp.setBackground(getSelectionBackground());
            }
        }
        else {
            if (errorRows.contains(row) && col == 2) {
                comp.setBackground(VERY_LIGHT_RED);
            } else {
                comp.setBackground(Color.white);
            }
        }
        return comp;
    }
}