package vrl.biogas.biogascontrol;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JComponent;
import javax.swing.JPanel;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.TypeInfo;
import eu.mihosoft.vrl.reflection.TypeRepresentationBase;
import eu.mihosoft.vrl.visual.TransparentPanel;

/**
 * Type representation in VRL for the main panels {@link vrl.biogas.biogascontrol.BiogasControl}/{@link vrl.biogas.biogascontrol.BiogasUserControl}. 
 * 
 * @author Michael Hoffer, Paul Zügel
 */
@ComponentInfo(name="MainPanelContainerType", category="Biogas", description="MainPanelContainerType")
@TypeInfo(type = JComponent.class, input = true, output = true, style = "default")
public class MainPanelContainerType extends TypeRepresentationBase implements java.io.Serializable {

    private static final long serialVersionUID = 1;
    private JPanel componentContainer;
    private JComponent jComponent;
    
    public MainPanelContainerType() {
    	setValueName(""); // name of the visualization

        // Set layout
    	this.setLayout(new BorderLayout());
        this.setPreferredSize(new Dimension(200, 200));
        this.setMinimumSize(new Dimension(100, 100));

        // Get the panel from the xychart
        componentContainer = new TransparentPanel();
        
        // Set layout
        componentContainer.setLayout(new BorderLayout());

        add(componentContainer);
                
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
