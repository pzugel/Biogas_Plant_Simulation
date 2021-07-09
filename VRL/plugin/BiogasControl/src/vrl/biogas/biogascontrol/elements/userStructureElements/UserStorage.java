package vrl.biogas.biogascontrol.elements.userStructureElements;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import javax.imageio.ImageIO;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import vrl.biogas.biogascontrol.BiogasUserControl;
import vrl.biogas.biogascontrol.elements.functions.ElementHelperFunctions;
import vrl.biogas.biogascontrol.elements.functions.MergeFunctions;
import vrl.biogas.biogascontrol.panels.SimulationPanel;

/**
 * Biogas VRL element: Storage Hydrolysis
 * @author Paul Zügel
 *
 */
@ComponentInfo(name="Storage", 
	category="Biogas_UserElements")
public class UserStorage extends ElementHelperFunctions implements Serializable{
	private static final long serialVersionUID=1L;
	
	@MethodInfo(name="Icon", hide=false, hideCloseIcon=false, interactive=true, num=1)
	public BufferedImage icon() {
		File iconPath = new File(BiogasUserControl.projectPath, "icons");
		File hydroIcon_path = new File(iconPath, "storage_muha.png");
		
		BufferedImage img = null;
		try {
		    img = ImageIO.read(hydroIcon_path);
		} catch (IOException e) {
		}

		return img; 
	}
	
	@MethodInfo(name="Main", hide=false, hideCloseIcon=true, interactive=false, num=1)
	public void run() throws IOException{
		System.out.println("--> Storage");
		if(!BiogasUserControl.wasCancelled) {
			
			boolean firstTimestep = ((Integer) BiogasUserControl.settingsPanelObj.simStarttime.getValue() == BiogasUserControl.currentTime);
			SimulationPanel simPanel = BiogasUserControl.simulationPanelObj;
			
			simPanel.activeElement.setText("Storage");
			log("** Storage ... ");
			File directory = BiogasUserControl.workingDirectory;
			File storageDirectory = new File(directory, "storage_hydrolysis");
			String[] reactors = BiogasUserControl.hydrolysisNames;
			MergeFunctions.merge_all_hydrolysis(storageDirectory, directory, reactors);
			if(firstTimestep) {
				String hydrolysisName = reactors[0];
				MergeFunctions.copy_outputFiles(directory, hydrolysisName);				
				MergeFunctions.update_outputFiles(storageDirectory);
				MergeFunctions.update_outputFiles_integration(storageDirectory);
			}
			
			String logEnd = simPanel.simulationLog.getText();
			simPanel.simulationLog.setText(logEnd + "Done!\n");
		}
	}
}
