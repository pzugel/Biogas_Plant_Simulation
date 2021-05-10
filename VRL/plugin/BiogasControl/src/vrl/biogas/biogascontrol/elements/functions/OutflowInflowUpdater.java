package vrl.biogas.biogascontrol.elements.functions;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OutflowInflowUpdater {
	
	private static ArrayList<String> outflow_input_header;
	private static ArrayList<ArrayList<String>> outflow_input_values;
	private static ArrayList<String> spec_inflowData_vec;
	private static String inflow_timetable_string;
	private static ArrayList<ArrayList<String>> output_timetable;
	private static String timetable_replacement;
	
	private static void parse_spec_file(File spec) throws IOException
	{ 
		System.out.println("parse_spec_file: " + spec);
		spec_inflowData_vec = new ArrayList<String>();
		//String content = Files.readString(spec.toPath(), StandardCharsets.US_ASCII); //Not compatible with groovy
		String content = "";
		Scanner lineIter = new Scanner(spec);		
		while (lineIter.hasNextLine()) {
			content += lineIter.nextLine() + "\n";
		}
		lineIter.close();
		System.out.println("content: " + content);
		
		Pattern p = Pattern.compile("inflow(\\s)*=(\\s)*\\{(\\s)*data(\\s)*=(\\s)*\\{[a-zA-Z,\"\\s]*\\},(\\s)*timetable(\\s)*=(\\s)*\\{(\\s)*(\\{.*\\},?(\\s)*)*\\},(\\s)*\\},?");
		Matcher m = p.matcher(content);
		if(m.find()) {
			
			String inflow = m.group(0);
			System.out.println("was found: " + inflow);
			p = Pattern.compile("\"[a-zA-Z0-9\\s]+\"");
			m = p.matcher(inflow);		
			while (m.find()) {
				System.out.println("data m.group(0): " + m.group(0));
				spec_inflowData_vec.add(m.group(0));
			}
			
			p = Pattern.compile("timetable(\\s)*=(\\s)*\\{(\\s)*(\\{.*\\},?(\\s)*)*\\},");
			m = p.matcher(inflow);
			if(m.find()) {
				System.out.println("timetable m.group(0): " + m.group(0));
				inflow_timetable_string = m.group(0);
			}	
		}
	}
	
	private static void read_outflow_header(String header)
	{		
		outflow_input_header = new ArrayList<String>();
		Pattern p = Pattern.compile("\\w[\\s\\w]*\\[[a-zA-Z0-9/-]+\\]");
		Matcher m = p.matcher(header);		
		while (m.find()) {
		    outflow_input_header.add(m.group(0));   
		}
	}
	
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
	
	private static void write_new_timetable(double fraction, boolean isMethane)
	{
		System.out.println("write_new_timetable: " + fraction);
		output_timetable = new ArrayList<ArrayList<String>>();

		// Adding parameters "Time" and "All Liquid"
		int column = 0;
		for(String val : outflow_input_header) {
			ArrayList<String> colList = new ArrayList<String>();
			if(val.contains("Time") || (val.contains("All") && val.contains("Liquid"))) {
				System.out.println("val: " + val);
				for(int k=0; k<outflow_input_values.size(); k++)
				{
					/*
					 * If we update the specification inflow for the methane we want the hydrolysis outflow to be
					 * present in the current timestep
					 * 
					 * e.g. If we compute the timestep 2.0 -> 3.0 in the hydrolysis reactors we want the outflow
					 * at timestamp "3.0" to be present in the methane reactor at timestamp "2.0" since we still 
					 * need to compute timestep 2.0 -> 3.0 in the methane reactor
					 */
					if(val.contains("Time") && isMethane) {
						double previousTimestep = Double.parseDouble(outflow_input_values.get(k).get(column))-1;
						colList.add(String.valueOf(previousTimestep));
					} else {
						colList.add(outflow_input_values.get(k).get(column));
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
						if(k == outflow_input_values.size() - 1) { //only split at last value
							value = value*fraction;
						}
						colList.add(String.valueOf(value));
					}	
					output_timetable.add(colList);		
				}
				++ column;
			}		
		}
	}
	
	private static void write_new_timetable_string(File spec) throws IOException
	{
		System.out.println("write_new_timetable_string: " + spec);
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
		 * We might want to keep this. This old version adds all old inflow values to the specification aswell.
		 * BUT it does not split on old (history) values
		 */
		/*
		for(int i=0; i<output_timetable.get(0).size(); i++){
			timetable_replacement += tabs + "{";
			for(int j=0; j<output_timetable.size(); j++)
			{
				System.out.println("output_timetable.get(" + j + ").get(" + i + "): " + output_timetable.get(j).get(i));
				timetable_replacement += output_timetable.get(j).get(i);
				if(j!=output_timetable.size()-1)
					timetable_replacement += ", ";
			}
			timetable_replacement += "},\n";
		}
		*/
		
		/*
		 * This newer version only adds the inflow of the current timestep to the specification.
		 * This might suffice.
		 */
		timetable_replacement += tabs + "{";
		int numLines = output_timetable.get(0).size();
		int numEntries = output_timetable.size();
		System.out.println("numLines: " + numLines);
		System.out.println("numEntries: " + numEntries);
		for(int i=0; i<numEntries; i++){
			System.out.println("output_timetable.get(i).get(numLines): " + output_timetable.get(i).get(numLines-1));
			timetable_replacement += output_timetable.get(i).get(numLines-1);
			if(i!=numEntries-1)
				timetable_replacement += ", ";
		}
		timetable_replacement += "},\n";
		//NEW VERSION END
		
		tabs = tabs.substring(1);
		timetable_replacement += tabs + "},";
		
	}
	
	public static void write_methane_inflow(
			File outflow_infile,
			File methane_specfile) throws IOException
	{
		String header = HelperFunctions.get_header(outflow_infile);
		String data = HelperFunctions.remove_header(outflow_infile);
		parse_spec_file(methane_specfile);
		read_outflow_header(header);
		read_outflow_values(data);
		write_new_timetable(1.0, true);		
		write_new_timetable_string(methane_specfile);
		
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
	
	public static void write_hydrolysis_inflow(
			File outflow_infile,
			File[] hydrolysis_specfiles,
			double[] fractions) throws IOException
	{	
		String header = HelperFunctions.get_header(outflow_infile);
		String data = HelperFunctions.remove_header(outflow_infile);
		read_outflow_header(header);
		read_outflow_values(data);
		System.out.println("write_hydrolysis_inflow");
		int specNum = 0;
		for(File spec : hydrolysis_specfiles) {
			parse_spec_file(spec);
			write_new_timetable(fractions[specNum], false);
			write_new_timetable_string(spec);
			System.out.println("spec: " + spec);
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
		File outflow_infile = new File("/home/paul/Schreibtisch/smalltestmethane/LabVIEW/biogas_20210510_170729@1_STAGE/methane/outflow_integratedSum_fullTimesteps.txt");
		File spec = new File("/home/paul/Schreibtisch/smalltestmethane/LabVIEW/biogas_20210510_170729@1_STAGE/hydrolysis_0/0/hydrolysis_checkpoint.lua");
		File[] hydrolysis_specfiles = {spec};
		double[] fractions = {1.0};
		write_hydrolysis_inflow(outflow_infile, hydrolysis_specfiles, fractions);		
	}

}
