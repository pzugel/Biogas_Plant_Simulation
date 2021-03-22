package vrl.biogas.biogascontrol.elements;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
		spec_inflowData_vec = new ArrayList<String>();
		String content = Files.readString(spec.toPath(), StandardCharsets.US_ASCII);
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
	
	private static void write_new_timetable(double fraction)
	{
		output_timetable = new ArrayList<ArrayList<String>>();

		// Adding parameters "Time" and "All Liquid"
		int column = 0;
		for(String val : outflow_input_header) {
			ArrayList<String> colList = new ArrayList<String>();
			if(val.contains("Time") || (val.contains("All") && val.contains("Liquid"))) {
				for(int k=0; k<outflow_input_values.size(); k++)
				{
					colList.add(outflow_input_values.get(k).get(column));
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
		String content = Files.readString(spec.toPath(), StandardCharsets.US_ASCII);
		Scanner lineIter = new Scanner(content);	
		String tabs = "";
		while (lineIter.hasNextLine()) {
			String line = lineIter.nextLine();
			
			if(line.contains("inflow")) {		
				for (int i = 0; i < line.length(); i++) {
				    if (line.charAt(i) == '\t') {
				    	System.out.println("IS TAB");
				    	tabs += "\t";
				    }
				}
			}
			
		}
		lineIter.close();
		
		System.out.println("tab:" + tabs);
		tabs += "\t\t";
		timetable_replacement = "timetable={\n";
		for(int i=0; i<output_timetable.get(0).size(); i++){
			timetable_replacement += tabs + "{";
			for(int j=0; j<output_timetable.size(); j++)
			{
				timetable_replacement += output_timetable.get(j).get(i);
				if(j!=output_timetable.size()-1)
					timetable_replacement += ", ";
			}
			timetable_replacement += "},\n";
		}
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
		write_new_timetable(1.0);		
		write_new_timetable_string(methane_specfile);
		
		//Replacement in spec file	
		String specString = Files.readString(methane_specfile.toPath(), StandardCharsets.US_ASCII);
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
		
		int specNum = 0;
		for(File spec : hydrolysis_specfiles) {
			parse_spec_file(spec);
			write_new_timetable(fractions[specNum]);
			write_new_timetable_string(spec);
			
			//Replacement in spec file	
			String specString = Files.readString(spec.toPath(), StandardCharsets.US_ASCII);
			specString = specString.replace(inflow_timetable_string, timetable_replacement);
			
			//Write to file
			FileWriter myWriter = new FileWriter(spec);
			myWriter.write(specString);
			myWriter.close();
			++specNum;
		}
	}
	
	public static void main(String args[]) throws IOException, InterruptedException{  
		File outflow_infile = new File("/home/paul/Schreibtisch/smalltest/biogas_20h_1_STAGE/storage_hydrolyse/outflow.txt");
		File methane_specfile = new File("/home/paul/Schreibtisch/smalltest/biogas_20h_1_STAGE/methane/20/methane_checkpoint.lua");
		write_methane_inflow(outflow_infile, methane_specfile);
		
		
	}

}
