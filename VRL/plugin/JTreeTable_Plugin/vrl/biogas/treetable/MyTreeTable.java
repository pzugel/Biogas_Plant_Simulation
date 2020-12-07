package vrl.biogas.treetable;
 
import java.awt.Dimension;
 
import javax.swing.JTable;
 
public class MyTreeTable extends JTable {
	private static final long serialVersionUID = 1L;
    private MyTreeTableCellRenderer tree;
 
 
    public MyTreeTable(MyAbstractTreeTableModel treeTableModel, int numEntries) {
        super();
 
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
 
        // Kein Grid anzeigen.
        setShowGrid(false);
 
        // Keine Abstaende.
        setIntercellSpacing(new Dimension(0, 0));
 
    }
}