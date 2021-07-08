package vrl.biogas.biogascontrol.outputloader;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.border.LineBorder;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;

 
/**
 * Class representing the checkbox tree for outputFiles.lua
 * @author paul
 */
public class CheckBoxTree extends JTree {
	private static final long serialVersionUID = 1L;

	public CheckBoxTree() {
		setCellRenderer(new CheckBoxTreeNodeRenderer());
		setCellEditor(new CheckBoxTreeNodeEditor(this));     
		setEditable(true);
	}
}
 
class TreeNodeCheckBox extends JCheckBox {
	private static final long serialVersionUID = 1L;
	private OutputEntry entry;
   
   public TreeNodeCheckBox() {
      this("", false, null);
   }
 
   public TreeNodeCheckBox(final String text, final boolean selected, OutputEntry entry) {   
      this(text, null, selected, entry);
   }
 
   public TreeNodeCheckBox(final String text, final Icon icon, final boolean selected, OutputEntry entry) {
      super(text, icon, selected);
      this.entry = entry;
      setMargin(new Insets(1, 1, 1, 1));
   }
   
   public void setEntry(OutputEntry entry) {
	   this.entry = entry;
   }
   
   public OutputEntry getEntry() {
	   return this.entry;
   }
}
 
class CheckBoxTreeNodeRenderer implements TreeCellRenderer {
	Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;
	private TreeNodeCheckBox checkBoxRenderer = new TreeNodeCheckBox();
	private DefaultTreeCellRenderer defaultRenderer = new DefaultTreeCellRenderer();
 
	public CheckBoxTreeNodeRenderer() {
		Font fontValue;
		fontValue = UIManager.getFont("Tree.font");
		if (fontValue != null) {
			checkBoxRenderer.setFont(fontValue);
		}
		Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
		checkBoxRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));
 
		selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
		selectionForeground = UIManager.getColor("Tree.selectionForeground");
		selectionBackground = UIManager.getColor("Tree.selectionBackground");
		textForeground = UIManager.getColor("Tree.textForeground");
		textBackground = UIManager.getColor("Tree.textBackground");
	}
 
	protected TreeNodeCheckBox getCheckBoxRenderer() {
		return checkBoxRenderer;
	}
 
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
		Component component;
		if (leaf) {
			String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
			checkBoxRenderer.setText(stringValue);
			checkBoxRenderer.setSelected(false);
			checkBoxRenderer.setEnabled(tree.isEnabled());
			if (selected) {
				checkBoxRenderer.setBorder(new LineBorder(selectionBorderColor));
				checkBoxRenderer.setForeground(selectionForeground);
				checkBoxRenderer.setBackground(selectionBackground);
			} else {
				checkBoxRenderer.setBorder(null);
				checkBoxRenderer.setForeground(textForeground);
				checkBoxRenderer.setBackground(textBackground);
			}
			if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
				Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
				if (userObject instanceof TreeNodeCheckBox) {
					TreeNodeCheckBox node = (TreeNodeCheckBox) userObject;
					node.getEntry().setName(node.getText());
					checkBoxRenderer.setText(node.getText());
					checkBoxRenderer.setSelected(node.isSelected());
					checkBoxRenderer.setEntry(node.getEntry());
				}
			}
			component = checkBoxRenderer;
			} else {
				component = defaultRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
			}
		return component;
	}
}
 
class CheckBoxTreeNodeEditor extends AbstractCellEditor implements TreeCellEditor {
	private static final long serialVersionUID = 1L;
	CheckBoxTreeNodeRenderer renderer = new CheckBoxTreeNodeRenderer();
	private OutputEntry userObject;
	JTree tree;
 
	public CheckBoxTreeNodeEditor(JTree tree) {
		this.tree = tree;
	}
 
	public Object getCellEditorValue() {
		TreeNodeCheckBox checkBox = renderer.getCheckBoxRenderer();
		TreeNodeCheckBox checkBoxNode = new TreeNodeCheckBox(checkBox.getText(), checkBox.isSelected(), userObject);
		return checkBoxNode;
	}
 
	public boolean isCellEditable(EventObject event) {
		boolean editable = false;
		if (event instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) event;
			TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			if (path != null) {
				Object node = path.getLastPathComponent();
				if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
					editable = treeNode.isLeaf();
				}
			}
		}
		return editable;
	}
 
	public Component getTreeCellEditorComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row) {
	   
		if (((DefaultMutableTreeNode) value).getUserObject() instanceof OutputEntry) {			
			userObject = (OutputEntry) ((DefaultMutableTreeNode) value).getUserObject();
		} 
		
		if (((DefaultMutableTreeNode) value).getUserObject() instanceof TreeNodeCheckBox) {		
			TreeNodeCheckBox node = (TreeNodeCheckBox) ((DefaultMutableTreeNode) value).getUserObject();			
			userObject = node.getEntry();
		}
	   
		Component editor = renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);
		if (editor instanceof TreeNodeCheckBox) {
			((TreeNodeCheckBox) editor).addItemListener(new ItemListener() {
				public void itemStateChanged(ItemEvent itemEvent) {
					if (stopCellEditing()) {
						fireEditingStopped();
					}
				}
			});
		}
		return editor;
	}
}