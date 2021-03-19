package vrl.biogas.biogascontrol.specedit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidationParser {
	static String text = "";
	private List<ValiTableEntry> parameters;
	
	public ValidationParser(File path, List<ValiTableEntry> inputParams) throws FileNotFoundException {
		parameters = inputParams;
		readFile(path);
		modifyInput();
		genereteValues();
	}
	
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

	private void genereteValues() {
		text = text.replaceAll("\\{", "\\{\n");
		text = text.replaceAll("\\}", "\n}\n");
		text = text.replaceAll(",", "\n");
		text = text.replaceAll(" ", "");

		//System.out.println(text);
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
				//((ValiTableEntry) parameters.get(index)).setName(line.substring(0, line.length()-2));
				Matcher m_table = Pattern.compile(table_entry).matcher(line);
				if(m_table.find())
				{
					((ValiTableEntry) parameters.get(index)).setType(last_type);	
					((ValiTableEntry) parameters.get(index)).setDefaultVal(last_default);
					//((ValiTableEntry) parameters.get(index)).setSpecVal(last_default);
				}
			}
			
			m = Pattern.compile(type_re).matcher(line); //Match types
			if(m.find())
			{
				last_type = line.substring(6);
				last_type = last_type.replaceAll("\"", "");
					
				if(((ValiTableEntry) parameters.get(index)).isValueField()) {
					((ValiTableEntry) parameters.get(index)).setType(last_type);
				}
			}
			
			m = Pattern.compile(default_re).matcher(line); //Match defaults
			if(m.find())
			{
				String tmpline = line;
				tmpline = tmpline.replaceAll("\"", "");
				last_default = tmpline.substring(8);
				if(((ValiTableEntry) parameters.get(index)).isValueField()) {
					((ValiTableEntry) parameters.get(index)).setDefaultVal(last_default);
					//((ValiTableEntry) parameters.get(index)).setSpecVal(last_default);
				}
			}
			
			m = Pattern.compile(range_re).matcher(line); //Match ranges
			if(m.find())
			{
				int min_pos = line.indexOf('[');
				int max_pos = line.indexOf('-');
				int end_pos = line.indexOf(']');
				
				((ValiTableEntry) parameters.get(index)).setRangeMin(line.substring(min_pos+1,max_pos));
				((ValiTableEntry) parameters.get(index)).setRangeMax(line.substring(max_pos+1,end_pos));
			}
		}
		scanner.close();
	}
	/*
	public List<ValiTableEntry> getOutput(){
		return parameters;
	}
	*/
}
