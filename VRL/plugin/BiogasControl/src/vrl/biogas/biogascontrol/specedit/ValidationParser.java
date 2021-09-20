package vrl.biogas.biogascontrol.specedit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for validation files
 * @author Paul Zügel
 */
public class ValidationParser {
	static String text = "";
	private List<ValiTableEntry> parameters;
	
	public ValidationParser(File path, List<ValiTableEntry> inputParams) throws FileNotFoundException {
		parameters = inputParams;
		readFile(path);
		modifyInput();
		genereteValues();
	}
	
	/**
	 * Load file and remove all comments
	 * @param path Path to specification file
	 * @throws FileNotFoundException
	 */
	private void readFile(File path) throws FileNotFoundException{
		text = "";
		Scanner scanner = new Scanner(path);
		while (scanner.hasNextLine()) {
		   String line = scanner.nextLine();
		   if(!line.startsWith("--"))
		   {
			   if(line.indexOf("--")!=-1)
			   {
				   line = line.substring(0, line.indexOf("--"));
			   }
			   text += line + "\n";
			  
		   }
		}
		scanner.close();
	}
	
	/**
	 * Transform the validation input
	 *
	 * Modifies the validation file it in such
	 * a way that the "generateValues()" method can 
	 * get easier access to the data.
	 */
	private void modifyInput() {
		String range_value = "(range=\\{values=\\{[0-9\\.]+,[0-9\\.]+\\}\\})|(range=\\{[a-zA-Z0-9=\\.]+,[a-zA-Z0-9=\\.]+\\})";
		String table_content = "tableContent=\\{values=\\{[\"a-zA-Z0-9_,]+\\}\\}";
		String time_table_content = "timeTableContent=\\{numberEntries=[0-9]+\\}";
		String table_element = "\"[a-zA-Z0-9_]+\"";
		String number = "[0-9]+";
		String character = "[a-zA-Z]+";
		
		text = text.replaceAll("\\s","");

		Matcher rangeMatcher = Pattern.compile(range_value).matcher(text);
		while (rangeMatcher.find()) {
			String match = rangeMatcher.group();
			match = match.replaceAll(character, "");
			match = match.replaceAll("\\{", "[");
			match = match.replaceAll( "\\}", "]");
			match =  match.replaceAll( "\\=", "");
			match =  match.replaceAll( "\\[\\[", "\\[");
			match = match.replaceAll( "\\]\\]", "\\]");
			match = match.replaceAll( "\\,", "\\-");

			String replacement = "range=" + match;
			text = text.replace(rangeMatcher.group(), replacement);
	 	}
		 
		Matcher tableMatcher = Pattern.compile(table_content).matcher(text);
		while (tableMatcher.find()) {
			String match = tableMatcher.group();
			String replacement = "";
			Matcher tableEntryMatcher = Pattern.compile(table_element).matcher(match);
			while (tableEntryMatcher.find()) {
				replacement += tableEntryMatcher.group() + "={}, ";
			}
			text = text.replace(match, replacement);
		}
		 
		Matcher timeTableMatcher = Pattern.compile(time_table_content).matcher(text);
		while (timeTableMatcher.find()) {
			String match = timeTableMatcher.group();
			Matcher numEntriesMatcher = Pattern.compile(number).matcher(match);
			String replacement = "";
			int numEntries = 0;
			if(numEntriesMatcher.find())
			{
				numEntries = Integer.parseInt(numEntriesMatcher.group());
			}
			for (int i=0; i<numEntries+1;i++)
			{
				replacement += "timeTableContent={}, ";
			}	 
			text = text.replace(match, replacement);
		}
	}
	
	/**
	 * Generate all validation data  
	 *
	 * Parses the validation file and searches for keywords
	 * such as "type", "default" or "range" and their names. 
	 * The values are then saved in the "parameters"
	 * data structure. The input string "text" has 
	 * been prepared by the "modifyInput()" method for 
	 * easier parsing.
	 */
	private void genereteValues() {
		text = text.replaceAll("\\{", "\\{\n");
		text = text.replaceAll("\\}", "\n}\n");
		text = text.replaceAll(",", "\n");
		text = text.replaceAll(" ", "");

		String names_re = "^[a-zA-Z0-9_\"]+=\\{";
		String table_entry = "^((\")|(timeTableContent))";
		String type_re = "^type=";
		String default_re = "^default=";
		String range_re = "^range=";
		
		
		String last_type = "";
		String last_default = "";
		int index = -1;
		Scanner scanner = new Scanner(text);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			Matcher m;
		   	
			m = Pattern.compile(names_re).matcher(line); //Match names
			if(m.find())
			{
				++index;
				Matcher m_table = Pattern.compile(table_entry).matcher(line);
				if(m_table.find())
				{
					parameters.get(index).setType(last_type);	
					parameters.get(index).setDefaultVal(last_default);
				}
			}
			
			m = Pattern.compile(type_re).matcher(line); //Match types
			if(m.find())
			{
				last_type = line.substring(6);
				last_type = last_type.replaceAll("\"", "");
					
				if(parameters.get(index).isValueField()) {
					parameters.get(index).setType(last_type);
				}
			}
			
			m = Pattern.compile(default_re).matcher(line); //Match defaults
			if(m.find())
			{
				String tmpline = line;
				tmpline = tmpline.replaceAll("\"", "");
				last_default = tmpline.substring(8);
				if(parameters.get(index).isValueField()) {
					parameters.get(index).setDefaultVal(last_default);
				}
			}
			
			m = Pattern.compile(range_re).matcher(line); //Match ranges
			if(m.find())
			{
				int min_pos = line.indexOf('[');
				int max_pos = line.indexOf('-');
				int end_pos = line.indexOf(']');
				
				parameters.get(index).setRangeMin(line.substring(min_pos+1,max_pos));
				parameters.get(index).setRangeMax(line.substring(max_pos+1,end_pos));
			}
		}
		scanner.close();
	}
}
