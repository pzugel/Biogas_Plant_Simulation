package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import vrl.biogas.biogascontrol.BiogasControlClass;
import vrl.biogas.biogascontrol.panels.SetupPanel;
import vrl.biogas.biogascontrol.panels.SimulationPanel;
import vrl.biogas.biogascontrol.structures.Structure;

@ComponentInfo(name="Stop", 
	category="Biogas_Elements", 
	description="Stop element")
public class Stop implements SimulationElement, Serializable{	
	private static final long serialVersionUID = 1L;
	private Structure structure;
	
	public Stop(Structure struct) {
		this.structure = struct;
	}
	
	@Override
	public File path() {
		return null;
	}
	
	@Override
	public String name() {
	    return "Stop";
	}
	
	@Override
	public void run() throws IOException {
		System.out.println("Stop here!");
		SimulationPanel simPanel = BiogasControlClass.simulationPanelObj;
		
		simPanel.activeElement.setText("Stop");
		String logStart = simPanel.simulationLog.getText();
		simPanel.simulationLog.setText(logStart + "** Stop ... ");
		int endtime = (Integer) BiogasControlClass.settingsPanelObj.simEndtime.getValue();
		
		if(!structure.wasCancelled()) {
			if(BiogasControlClass.stopBtn.isSelected()) {
				writeSummary(BiogasControlClass.workingDirectory, true);
				BiogasControlClass.running.setSelected(false);
				System.out.println("Simulation stopped!");
				
				String logEnd = simPanel.simulationLog.getText();
				simPanel.simulationLog.setText(logEnd + "Stopped!\n");
			}
			else {
				if(structure.currentTime() < endtime-1) {
					
					String logEnd = simPanel.simulationLog.getText();
					simPanel.simulationLog.setText(logEnd + "Continue!\n");
					++ BiogasControlClass.iteration;					
					BiogasControlClass.simulationPanelObj.iteration.setText(String.valueOf(BiogasControlClass.iteration));
					structure.incrementCurrentTime();
								
					
					Runnable runnable = new Runnable()
			        {
			            @Override
			            public void run() 
			            {
			            	try {
								structure.run(structure.currentTime());
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
			            }
			        };
			        
			        System.out.println("Endtime not reached!");
					System.out.println("\tendtime: " + endtime);
					System.out.println("\tcurrent: " + structure.currentTime());
					
			        Thread threadObject = new Thread(runnable);
			        threadObject.start();			        
					
				}
				else {
					writeSummary(BiogasControlClass.workingDirectory, true);
					SetupPanel.mergePreexisting = true;
					BiogasControlClass.running.setSelected(false);
					String logEnd = simPanel.simulationLog.getText();
					simPanel.simulationLog.setText(logEnd + "Simulation finished!\n");
					
				}
			}
		}
		else {
			writeSummary(BiogasControlClass.workingDirectory, false);
			String logEnd = simPanel.simulationLog.getText();
			simPanel.simulationLog.setText(logEnd + "Cancelled!\n");
		}
	}
	
	private void writeSummary(File dir, boolean finished) throws IOException {
		String summary = "FINISHED=" + finished + "\n";
		summary += "WORKING_DIR=" + dir + "\n";
		summary += "STRUCTURE=" + structure.name() + "\n";
		summary += "NUM_HYDROLYSIS=" + structure.numHydrolysis() + "\n";
		summary += "STARTTIME=" + BiogasControlClass.settingsPanelObj.simStarttime.getValue() + "\n";
		int endtime = structure.currentTime()+1;
		summary += "ENDTIME=" + endtime + "\n";
		summary += "PREEXISTING=" + SetupPanel.mergePreexisting + "\n";
		summary += "RUNTIME=" + BiogasControlClass.simulationPanelObj.runtime.getText() + "\n";
		
		File summaryFile = new File(dir, "simulation_summary.txt");		
		
		//File summaryFile = new File(summary);
		if (!summaryFile.exists()) { //Create summary
			FileWriter summaryWriter = new FileWriter(summaryFile);
			summaryWriter.write(summary);
			summaryWriter.close();
		} else { //Update summary
			String existingSummary = "";
			Scanner lineIter = new Scanner(summaryFile);		
			while (lineIter.hasNextLine()) {
				existingSummary += lineIter.nextLine() + "\n";
			}
			lineIter.close();
			existingSummary += "RUNTIME=" + BiogasControlClass.simulationPanelObj.runtime.getText() + "\n";
			
			String endtimeString = "";
			Pattern pEndtime = Pattern.compile("ENDTIME=[0-9]+");
			Matcher mEndtime = pEndtime.matcher(existingSummary);
			if(mEndtime.find()) {				
				System.out.println("FOUND ENDTIME ENTRY");
				endtimeString = mEndtime.group(0);
			}
			System.out.println("Old endtime: " + endtime);
			int newEndtime = structure.currentTime()+1;
			System.out.println("New endtime: " + newEndtime);
			existingSummary = existingSummary.replace(endtimeString, "ENDTIME=" + newEndtime);
			
			String preexisting = "";
			Pattern pPreexisting = Pattern.compile("PREEXISTING=(true|false)");
			Matcher mPreexisting = pPreexisting.matcher(existingSummary);
			if(mPreexisting.find()) {	
				System.out.println("FOUND PREEXISTING ENTRY");
				preexisting = mPreexisting.group(0);
			}
			existingSummary = existingSummary.replace(preexisting, "PREEXISTING=true");
			
			FileWriter summaryWriter = new FileWriter(summaryFile, false);
			summaryWriter.write(existingSummary);
			summaryWriter.close();
		}
		
		
	}
}
