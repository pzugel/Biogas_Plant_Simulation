package vrl.biogas.outputloader;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.JFrame;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JButton;

/**
 * Parent type for plot typerepresentations. Can also be used directly as type
 * representation by returning <code>JFreeChart</code> objects.
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@TypeInfo(type = JFrame.class, input = false, output = true, style = "default")
public class OutputContainerType extends TypeRepresentationBase implements java.io.Serializable {

    private static final long serialVersionUID = 1;
    
    public OutputContainerType() {
        setValueName("Plot"); // name of the visualization
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        this.setPreferredSize(new Dimension(300, 200));
        this.setMinimumSize(new Dimension(200, 120));
        
        String data[][]={ {"101","Amit","670000"},    
                {"102","Jai","780000"},    
                {"101","Sachin","700000"}};    
		String column[]={"ID","NAME","SALARY"};         
		JTable jt=new JTable(data,column);    
		
		JPanel topPanel = new JPanel();
	    JPanel btnPanel = new JPanel();
	    
	    topPanel.setLayout(new BorderLayout());
        add(topPanel, BorderLayout.CENTER);
        add(btnPanel, BorderLayout.SOUTH);
			
	    JScrollPane scrollPane = new JScrollPane(jt);
	    topPanel.add(scrollPane,BorderLayout.CENTER);
	    
	    JButton plotButton = new JButton("Plot");
	    btnPanel.add(plotButton);
	    
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setValueOptions("width=" + getWidth() + ";"
                        + "height=" + getHeight());
            }
        });
        
    }
}
