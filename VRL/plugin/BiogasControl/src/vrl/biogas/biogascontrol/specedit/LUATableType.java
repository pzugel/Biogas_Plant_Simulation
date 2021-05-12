package vrl.biogas.biogascontrol.specedit;

import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.ResizableContainer;
import eu.mihosoft.vrl.visual.TransparentPanel;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JComponent;

/**
 * Parent type for lua table typerepresentations. 
 * @author Paul Zügel
 */
@TypeInfo(type = JComponent.class, input = true, output = true, style = "default")
public class LUATableType extends TypeRepresentationBase implements java.io.Serializable {

    private static final long serialVersionUID = 1;
    private JPanel componentContainer;
    private JComponent jComponent;
    
    public LUATableType() {
    	setValueName(""); // name of the visualization

        // Set layout
        this.setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
        //this.setPreferredSize(new Dimension(300, 300));
        this.setMinimumSize(new Dimension(200, 120));

        // Get the panel from the xychart
        componentContainer = new TransparentPanel();
        
        // Set layout
        componentContainer.setLayout(new BorderLayout());

        // Add ruler
        ResizableContainer container = new ResizableContainer(componentContainer);

        add(container);

        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                setValueOptions("width=" + getWidth() + ";"
                        + "height=" + getHeight());
            }
        });

    }
    
    @Override
    public void setViewValue(Object o) {

        if (o instanceof JComponent) {

            final JComponent chart = (JComponent) o;

            updatePanel(componentContainer, chart);

        }
    }
    
    protected void updatePanel(
            Container container,
            final JComponent panel) {

        if (jComponent != null) {
            componentContainer.remove(jComponent);
        }
        
        componentContainer.removeAll();

        componentContainer.add(panel);

        revalidate();
    }

}
