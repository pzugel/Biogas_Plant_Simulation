package vrl.biogas.treetable;
 
import java.util.Collections;
import java.util.List;
 
public class MyDataNode {
 
    private String name;
    private String type;
    private String value;
 
    private List<MyDataNode> children;
 
    public MyDataNode(String name, String type, String value, List<MyDataNode> children) {
        this.name = name;
        this.type = type;
        this.value = value;
        this.children = children;
 
        if (this.children == null) {
            this.children = Collections.emptyList();
        }
    }
 
    public String getName() {
        return name;
    }
 
    public String getType() {
        return type;
    }
 
    public String getValue() {
        return value;
    }
    
    public void setName(String name) {
    	this.name = name;
    }
 
    public void setType(String type) {
    	this.type = type;
    }
 
    public void setValue(String value) {
    	this.value = value;
    }
 
    public List<MyDataNode> getChildren() {
        return children;
    }
 
    /**
     * Knotentext vom JTree.
     */
    public String toString() {
        return name;
    }
}