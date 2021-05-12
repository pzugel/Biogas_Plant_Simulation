package vrl.biogas.biogascontrol.specedit.treetable;
 
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;
import javax.swing.tree.TreePath;
 
/**
 * Parent class for the specification tree
 * @author Paul Zügel
 */
public class MyTreeTable extends JTable {
	private static final long serialVersionUID = 1L;
    private MyTreeTableCellRenderer tree;
    private List<TreePath> errorParams;
 
    public MyTreeTable(MyAbstractTreeTableModel treeTableModel, int numEntries) {
        super();
        errorParams = new ArrayList<TreePath>();
        //Construct JTree
        tree = new MyTreeTableCellRenderer(this, treeTableModel);
        for(int i=0; i<numEntries; i++) {
        	tree.expandRow(i);
        }
        super.setModel(new MyTreeTableModelAdapter(treeTableModel, tree));
 
        MyTreeTableSelectionModel selectionModel = new MyTreeTableSelectionModel();
        tree.setSelectionModel(selectionModel);
        setSelectionModel(selectionModel.getListSelectionModel());
 
        setDefaultRenderer(MyTreeTableModel.class, tree);
        setDefaultEditor(MyTreeTableModel.class, new MyTreeTableCellEditor(tree, this));
        
        setShowGrid(false);
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
        for(TreePath p : errorParams) {
        	System.out.println("Error Path: " + p.toString());
        	System.out.println("Error Row: " + tree.getRowForPath(p));
        	errorRows.add(tree.getRowForPath(p));
        }

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