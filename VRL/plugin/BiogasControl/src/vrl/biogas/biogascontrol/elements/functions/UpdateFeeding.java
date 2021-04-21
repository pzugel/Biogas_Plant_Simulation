package vrl.biogas.biogascontrol.elements.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.util.Pair;

public class UpdateFeeding {
	
	private static String feeding_timetable_string;
	
	/**
	 * Read the feeding timetable from a hydrolysis specification
	 * 
	 * @param hydrolysis_specfile: Path pointing the the specification
	 * @throws FileNotFoundException 
	 */
	private static void read_feeding_timetable(File hydrolysis_specfile) throws FileNotFoundException
	{
		boolean feeding = false;
		boolean feeding_timetable = false;
		boolean feeding_timestamp = false;
		feeding_timetable_string = "";
		
		String timestamp ="(\\s)*\\{(\\s)*[0-9eE\\.\\-\\*]+(\\s)*,(\\s)*[0-9eE\\.\\-\\*]+(\\s)*\\},(\\s)*";
		Scanner lineIter = new Scanner(hydrolysis_specfile);	
		while (lineIter.hasNextLine()) {
			String line = lineIter.nextLine();
			if(line.contains("feeding")) {
				feeding = true;
			}				
			if(line.contains("timetable") && feeding == true) {
				feeding_timetable = true;	
			}
				 			
			if(feeding_timetable)
			{
				Pattern p = Pattern.compile(timestamp);
				Matcher m = p.matcher(line);
				if(m.find())
				{
					feeding_timetable_string += line + "\n";
					feeding_timestamp = true;
				} 
				else if(!m.find() && feeding_timestamp)
				{
					feeding = false;
					feeding_timetable = false;
					feeding_timestamp = false;
				}
			}
		}
		lineIter.close();
	}

	/**
	 * Returns feeding timetable formatted into a CSV styls string
	 * to be loaded into LabView
	 * 
	 * @param hydrolysis_specfile: Path pointing the the specification
	 * @throws FileNotFoundException 
	 */
	public static ArrayList<Pair<String, String>> get_feeding_timetable(File hydrolysis_specfile) throws FileNotFoundException
	{
		ArrayList<Pair<String, String>> table = new ArrayList<Pair<String, String>>();
		read_feeding_timetable(hydrolysis_specfile);
		Scanner lineIter = new Scanner(feeding_timetable_string);			
		while (lineIter.hasNextLine()) {
			String line = lineIter.nextLine();
			line = line.replaceAll("\\s+","");
			int time = line.indexOf('{');
			int amount = line.indexOf(',');
			int end = line.indexOf('}');
			Pair<String, String> timeAmount = new Pair<String, String>(line.substring(time+1, amount), line.substring(amount+1, end));
			table.add(timeAmount);
		}
		lineIter.close();
		return table;
	}
	
	/**
	 * Updates the feeding timetable in a specification file 
	 * for a hydrolysis reacote
	 * 
	 * @param hydrolysis_specfile: Path pointing the the specification
	 * @param time: Timestamp time
	 * @param amount: Timestamp amount
	 * @param number_timestamps: size of arrays time and amount
	 * @throws IOException 
	 */
	public static void update_feeding_timetable(
		File hydrolysis_specfile,
		ArrayList<Pair<String, String>> table) throws IOException
	{	
		String specfile_string = "";
		Scanner lineIter = new Scanner(hydrolysis_specfile);		
		while (lineIter.hasNextLine()) {
			specfile_string += lineIter.nextLine() + "\n";
		}
		lineIter.close();
		
		read_feeding_timetable(hydrolysis_specfile);
		
		
		String new_timetable = "";
		
		Scanner tab_stream = new Scanner(feeding_timetable_string);
		String first_line = tab_stream.nextLine();
		tab_stream.close();
		int num_tabs = first_line.length() - first_line.replace("\t", "").length();
		
		String tabs = "";
		for(int i=0; i<num_tabs; i++) {
			tabs += "\t";
		}
		for(Pair<String, String> pair : table) {
			new_timetable += tabs + "{" + pair.getKey() + ", " + pair.getValue() + "},\n";
		}
		
		int timetable_pos = specfile_string.indexOf(feeding_timetable_string);	
		int timetable_end = timetable_pos + feeding_timetable_string.length();
		String new_specfile = specfile_string.substring(0, timetable_pos) + new_timetable + specfile_string.substring(timetable_end);
		
		//Write to file
		FileWriter myWriter = new FileWriter(hydrolysis_specfile);
		myWriter.write(new_specfile);
		myWriter.close();
	}
}
