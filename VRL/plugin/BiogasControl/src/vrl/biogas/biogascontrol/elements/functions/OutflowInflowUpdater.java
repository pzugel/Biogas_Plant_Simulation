package vrl.biogas.biogascontrol.elements.functions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Updates the inflow values in specification files.
 * @author Paul Zügel
 */
public class OutflowInflowUpdater {
	
	private static ArrayList<String> outflow_input_header;
	private static ArrayList<ArrayList<String>> outflow_input_values;
	private static ArrayList<String> spec_inflowData_vec;
	private static String inflow_timetable_string;
	private static ArrayList<ArrayList<String>> output_timetable;
	private static String timetable_replacement;
	private static int sim_starttime;
	private static double flow;
	
	/**
	 * Parse a specfile and find the "inflow" entry. Write the inflow components to "spec_inflowData_vec"
	 * and the values into "inflow_timetable_string"
	 * @param spec
	 * @throws IOException
	 */
	private static void parse_spec_file(File spec) throws IOException
	{ 
		spec_inflowData_vec = new ArrayList<String>();
		
		//String content = Files.readString(spec.toPath(), StandardCharsets.US_ASCII); //Not compatible with groovy
		String content = "";
		Scanner lineIter = new Scanner(spec);		
		while (lineIter.hasNextLine()) {
			content += lineIter.nextLine() + "\n";
		}
		lineIter.close();
		
		Pattern p = Pattern.compile("inflow(\\s)*=(\\s)*\\{(\\s)*data(\\s)*=(\\s)*\\{[a-zA-Z,\"\\s]*\\},(\\s)*timetable(\\s)*=(\\s)*\\{(\\s)*(\\{.*\\},?(\\s)*)*\\},(\\s)*\\},?");
		Matcher m = p.matcher(content);
		if(m.find()) {
			
			String inflow = m.group(0);
			p = Pattern.compile("\"[a-zA-Z0-9\\s]+\"");
			m = p.matcher(inflow);		
			while (m.find()) {
				spec_inflowData_vec.add(m.group(0));
			}
			
			p = Pattern.compile("timetable(\\s)*=(\\s)*\\{(\\s)*(\\{.*\\},?(\\s)*)*\\},");
			m = p.matcher(inflow);
			if(m.find()) {
				inflow_timetable_string = m.group(0);
			}	
		}
		
		//Find sim_starttime
		Pattern starttimePattern = Pattern.compile("sim_starttime(\\s)*=(\\s)*[0-9.]+");
		Matcher starttimeMatcher = starttimePattern.matcher(content);
		if(starttimeMatcher.find()) {
			String starttimeString = starttimeMatcher.group(0);
			int startInd = starttimeString.indexOf('=');
			starttimeString = starttimeString.substring(startInd+1);
			sim_starttime = Integer.valueOf(starttimeString);
		} else {
			sim_starttime = 0;
			System.out.println("Could not find sim_starttime entry in specfile!");
		}
	}
	
	/**
	 * Parses header from an integrated "outflow" file and writes them to "outflow_input_header"
	 * @param header 
	 */
	private static void read_outflow_header(String header)
	{		
		outflow_input_header = new ArrayList<String>();
		Pattern p = Pattern.compile("\\w[\\s\\w]*\\[[a-zA-Z0-9/-]+\\]");
		Matcher m = p.matcher(header);		
		while (m.find()) {
		    outflow_input_header.add(m.group(0));   
		}
	}
	
	/**
	 * Parses the data from an integrated "outflow" file (without header) and write values to "outflow_input_values"
	 * @param data
	 */
	private static void read_outflow_values(String data)
	{
		outflow_input_values = new ArrayList<ArrayList<String>>();
		Scanner lineIter = new Scanner(data);		
		while (lineIter.hasNextLine()) {
			String line = lineIter.nextLine();
			Scanner itemIter = new Scanner(line);	
			ArrayList<String> itemList = new ArrayList<String>();
			while (itemIter.hasNext()) {
				String item = itemIter.next();
				itemList.add(item);
			}
			outflow_input_values.add(itemList);
			itemIter.close();
		}
		lineIter.close();
	}
	
	/**
	 * Constructing the new inflow timetable "output_timetable" by matching the values from 
	 * the integrated "outflow" file and the parameters defined in the "inflow" timetable from the specification.
	 * @param fraction
	 * @param isMethane
	 */
	private static void write_new_timetable(double fraction, boolean isMethane)
	{
		output_timetable = new ArrayList<ArrayList<String>>();

		// Adding parameters "Time" and "All Liquid"
		int column = 0;
		for(String val : outflow_input_header) {
			ArrayList<String> colList = new ArrayList<String>();
			if(val.contains("Time") || (val.contains("All") && val.contains("Liquid"))) {
				for(int k=0; k<outflow_input_values.size(); k++)
				{
					/*
					 * Here we need to distinguish methane and hydrolysis reactors
					 * 
					 * e.g. If we compute the timestep 2.0 -> 3.0 in any reactor we need to set the 
					 * inflow timestep to "3.0". Now if we take the methane outflow of a computed step from
					 * 2.0 -> 3.0 we need to add one to the timestep when feeding it back to the hydrolysis reactor
					 * to be present at time 4.0 in the hydrolysis.
					 */
					if(val.contains("Time")) {
						if(isMethane) {
							double previousTimestep = Double.parseDouble(outflow_input_values.get(k).get(column));
							colList.add(String.valueOf(previousTimestep));
						} else {
							double timeOffset = Double.parseDouble(outflow_input_values.get(k).get(column))+1;
							colList.add(String.valueOf(timeOffset));
						}
						
					} else {
						//Fractional "all liquid"
						double allLiquidFraction = flow*fraction;
						
						//Old version - Gets flow from previous "all liquid" outflow value
						//double allLiquidFraction = Double.parseDouble(outflow_input_values.get(k).get(column))*fraction;
						colList.add(String.valueOf(allLiquidFraction));
					}
				}
				output_timetable.add(colList);	
			}		
			++column;
		}
		
		//Adding parameters defined in data={"...", "..."}
		
		for(String dataVal : spec_inflowData_vec) {
			column = 0;
			dataVal = dataVal.replaceAll("\"", "");
			
			// Match parameter from "data" with the currect column 
			// from the "outflow.txt" files header
			for(String headerVal : outflow_input_header) {
				ArrayList<String> colList = new ArrayList<String>();
				if(headerVal.contains(dataVal)) {
					for(int k=0; k<outflow_input_values.size(); k++)
					{
						double value = Double.valueOf(outflow_input_values.get(k).get(column));
						/*
						TODO Should we split here? Doesnt make sense because values are in [g/h].
						If we only split the inflow amount (in [L/h]) it should suffice.
						
						See for alternative:
						*/
						/*
						if(k == outflow_input_values.size() - 1) { //only split at last value
							value = value*fraction;
						}
						*/
						colList.add(String.valueOf(value));
					}	
					output_timetable.add(colList);		
				}
				++ column;
			}		
		}
	}
	
	/**
	 * Turns the "output_timetable" constructed by the write_new_timetable() method into string
	 * @param spec
	 * @throws IOException
	 */
	private static void write_new_timetable_string(File spec, boolean isMethane) throws IOException
	{
		/*
		 * Scan indentation
		 */
		Scanner lineIter = new Scanner(spec);	
		String tabs = "";
		while (lineIter.hasNextLine()) {
			String line = lineIter.nextLine();			
			if(line.contains("inflow")) {		
				for (int i = 0; i < line.length(); i++) {
				    if (line.charAt(i) == '\t') {
				    	tabs += "\t";
				    }
				}
			}
			
		}
		lineIter.close();
		
		tabs += "\t\t";
		timetable_replacement = "timetable={\n";

		
		/*
		 * Adds the inflow of the current timestep to the specification.
		 */
		timetable_replacement += tabs + "{";
		int numLines = output_timetable.get(0).size();
		int numEntries = output_timetable.size();

		for(int i=0; i<numEntries; i++){
			timetable_replacement += output_timetable.get(i).get(numLines-1);
			
			if(i!=numEntries-1)
				timetable_replacement += ", ";
		}
		timetable_replacement += "},\n";
		//NEW VERSION END
		
		tabs = tabs.substring(1);
		timetable_replacement += tabs + "},";
		
	}
	
	/**
	 * Function called by the methane element to update the inflow in the methane specification by
	 * reading out the integrated outflow file from the hydrolysis reactor.
	 * @param outflow_infile - Path pointing to the hydrolysis storage outflow file
	 * @param methane_specfile
	 * @param flowVal
	 * @throws IOException
	 */
	public static void write_methane_inflow(
			File outflow_infile,
			File methane_specfile,
			double flowVal) throws IOException
	{
		System.out.println("\t Write Methane inflow");
		flow = flowVal;
		String header = HelperFunctions.get_header(outflow_infile);
		String data = HelperFunctions.remove_header(outflow_infile);
		parse_spec_file(methane_specfile);
		read_outflow_header(header);
		read_outflow_values(data);
		write_new_timetable(1.0, true);		
		write_new_timetable_string(methane_specfile, true);
		
		//Replacement in spec file	
		String specString = "";
		Scanner lineIter = new Scanner(methane_specfile);	
		while (lineIter.hasNextLine()) {
			specString += lineIter.nextLine() + "\n";
		}
		lineIter.close();
		specString = specString.replace(inflow_timetable_string, timetable_replacement);
		
		//Write to file
		FileWriter myWriter = new FileWriter(methane_specfile);
		myWriter.write(specString);
		myWriter.close();
	}
	
	
	/**
	 * Change the inflow time to sim_starttime
	 * @param hydrolysis_specfile
	 */
	private static void write_new_initial_timetable_string()
	{		
		String inflowString = inflow_timetable_string;
		
		Pattern p = Pattern.compile("(\\s)*\\{[0-9.,(\\s)eE-]+\\}(,)?");
		Matcher m = p.matcher(inflowString);
		if(m.find()) {
			String found = m.group(0);
			int indexStart = found.indexOf("{");
			int indexEnd = found.indexOf(",", found.indexOf(",") + 1); //Second occurence of ","
			
			if(indexStart > 0 && indexEnd > 0) {
				String newStart = found.substring(0, indexStart+1) 
						+ String.valueOf(sim_starttime+1)
						+ ", "
						+ flow
						+ found.substring(indexEnd);
				inflowString = inflowString.replace(found, newStart);
			}
			timetable_replacement = inflowString;
		}
	}
	
	/**
	 * Function called only once at the beginning of the simulation to set the initial
	 * inflow according to the sim_starttime
	 * @param hydrolysis_specfiles
	 * @param flowVal
	 * @throws IOException 
	 */
	public static void write_inital_hydrolysis_inflow(
			File[] hydrolysis_specfiles,
			double flowVal) throws IOException 
	{
		System.out.println("\t Write initial Hydrolysis inflow");
		flow = flowVal;
		for(File spec : hydrolysis_specfiles) {
			parse_spec_file(spec);
			write_new_initial_timetable_string();
			String specString = "";
			Scanner lineIter = new Scanner(spec);		
			while (lineIter.hasNextLine()) {
				specString += lineIter.nextLine() + "\n";
			}
			lineIter.close();
			specString = specString.replace(inflow_timetable_string, timetable_replacement);
			
			//Write to file
			FileWriter myWriter = new FileWriter(spec);
			myWriter.write(specString);
			myWriter.close();
		}
	}
	
	/**
	 * Function called by the hydrolysis (setup) element to update the inflow in the hydrolysis 
	 * specification by reading out the integrated outflow file from the methane reactor.
	 * @param outflow_infile - Path pointing to the methane outflow file
	 * @param hydrolysis_specfiles
	 * @param fractions
	 * @param flowVal
	 * @throws IOException
	 */
	public static void write_hydrolysis_inflow(
			File outflow_infile,
			File[] hydrolysis_specfiles,
			double[] fractions,
			double flowVal) throws IOException
	{	
		System.out.println("\t Write Hydrolysis inflow");
		flow = flowVal;
		String header = HelperFunctions.get_header(outflow_infile);
		String data = HelperFunctions.remove_header(outflow_infile);
		read_outflow_header(header);
		read_outflow_values(data);
		
		int specNum = 0;
		for(File spec : hydrolysis_specfiles) {
			parse_spec_file(spec);
			write_new_timetable(fractions[specNum], false);
			write_new_timetable_string(spec, false);
			//Replacement in spec file	
			String specString = "";
			Scanner lineIter = new Scanner(spec);		
			while (lineIter.hasNextLine()) {
				specString += lineIter.nextLine() + "\n";
			}
			lineIter.close();
			specString = specString.replace(inflow_timetable_string, timetable_replacement);
			
			//Write to file
			FileWriter myWriter = new FileWriter(spec);
			myWriter.write(specString);
			myWriter.close();
			++specNum;
		}
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{
		/*
		File outflow_infile = new File("/home/paul/Schreibtisch/Simulations/VRL/Demo/biogasVRL_20210708_125847/methane/outflow_integratedSum_fullTimesteps.txt");
		
		File spec0 = new File("/home/paul/Schreibtisch/Simulations/VRL/Demo/biogasVRL_20210708_125847/hydrolysis_0/2/hydrolysis_checkpoint.lua");
		double[] fractions = {1.00};
		File[] hydrolysis_specfiles = {spec0};
		write_hydrolysis_inflow(outflow_infile, hydrolysis_specfiles, fractions);
		*/
		File spec0 = new File("/home/paul/Schreibtisch/Simulations/VRL/Full/biogasVRL_20210811_120506/hydrolysis_0/0/hydrolysis_checkpoint.lua");
		File[] hydrolysis_specfiles = {spec0};
		write_inital_hydrolysis_inflow(hydrolysis_specfiles, 15);
	}
}
