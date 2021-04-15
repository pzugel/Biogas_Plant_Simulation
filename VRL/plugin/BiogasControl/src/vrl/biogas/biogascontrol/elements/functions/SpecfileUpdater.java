package vrl.biogas.biogascontrol.elements.functions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecfileUpdater {

	public static void update_starttime(File spec_dir, int starttime) throws IOException
	{
		String output_spec = "";
		
		Scanner lineIter = new Scanner(spec_dir);		
		while (lineIter.hasNextLine()) {
			
			String line = lineIter.nextLine();
			Pattern starttimeRegex = Pattern.compile("sim_starttime(\\s)*=(\\s)*[0-9.E\\-\\*]*(\\s)*,?");
		    Matcher m = starttimeRegex.matcher(line);
		    
			if(m.find())
			{
				String newLine = "";
				for (int i = 0; i < line.length(); i++) {
				    if (line.charAt(i) == '\t') {
				    	newLine += "\t";
				    }
				}

				String starttime_string = String.valueOf(starttime);
				newLine += "sim_starttime=" + starttime_string + ",";
				output_spec += newLine + "\n";
			}
			else {
				output_spec += line + "\n";
			}
		}
		lineIter.close();	
		
		FileWriter myWriter = new FileWriter(spec_dir);
		myWriter.write(output_spec);
		myWriter.close();		
	}
	
	public static void update_endtime(File spec_dir, int endtime) throws IOException
	{
		String output_spec = "";
		
		Scanner lineIter = new Scanner(spec_dir);		
		while (lineIter.hasNextLine()) {
			
			String line = lineIter.nextLine();
			Pattern endtimeRegex = Pattern.compile("sim_endtime(\\s)*=(\\s)*[0-9.E\\-\\*]*(\\s)*,?");
		    Matcher m = endtimeRegex.matcher(line);
		    
			if(m.find())
			{
				String newLine = "";
				for (int i = 0; i < line.length(); i++) {
				    if (line.charAt(i) == '\t') {
				    	newLine += "\t";
				    }
				}

				String endtime_string = String.valueOf(endtime);
				newLine += "sim_endtime=" + endtime_string + ",";
				output_spec += newLine + "\n";
			}
			else {
				output_spec += line + "\n";
			}
		}
		lineIter.close();	
		
		FileWriter myWriter = new FileWriter(spec_dir);
		myWriter.write(output_spec);
		myWriter.close();		
	}
	
	public static void update_read_checkpoint(File spec_dir, File checkpoint_dir) throws IOException
	{
		String checkpointString = checkpoint_dir.toString() + File.separator;
		
		Pattern checkpointDirRegex = Pattern.compile("checkpointDir");
		Scanner lineIter = new Scanner(spec_dir);	
		String input_no_dir = "";
		while (lineIter.hasNextLine()) {
			String line = lineIter.nextLine();
			Matcher m = checkpointDirRegex.matcher(line);
			if(!m.find()) {
				input_no_dir += line + "\n";
			}
		}
		lineIter.close();
	    
		Pattern checkpointRegex = Pattern.compile("doReadCheckpoint(\\s)*=(\\s)*(true|false)(\\s)*,?");
		String output_spec = "";
		Scanner lineIterSpec = new Scanner(input_no_dir);
		//std::stringstream spec_string_stream(input_no_dir);
		while (lineIterSpec.hasNextLine()) {
			String line = lineIterSpec.nextLine();
			Matcher m = checkpointRegex.matcher(line);
			if(m.find())
			{
				String newLine = "";
				String tabs = "";
				for (int i = 0; i < line.length(); i++) {
				    if (line.charAt(i) == '\t') {
				    	tabs += "\t";
				    }
				}
				newLine += tabs + "doReadCheckpoint=true,\n";
				newLine += tabs + "checkpointDir=\"" + checkpointString + "\",";
				output_spec += newLine + "\n";
			}
			else {
				output_spec += line + "\n";
			}
		}
		lineIterSpec.close();
		
		FileWriter myWriter = new FileWriter(spec_dir);
		myWriter.write(output_spec);
		myWriter.close();	
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{ 
		//File spec_dir = new File("/home/paul/Schreibtisch/smalltestmethane/biogasVRL_20210319_144634/hydrolyse_0/1/hydrolysis_checkpoint.lua");
		//update_starttime(spec_dir, 1);
		//update_endtime(spec_dir, 2);
		//File checkpointDir = new File("/home/paul/Schreibtisch/smalltestmethane/biogasVRL_20210319_144634/hydrolyse_0/0");
		//update_read_checkpoint(spec_dir, checkpointDir);
	}
}
