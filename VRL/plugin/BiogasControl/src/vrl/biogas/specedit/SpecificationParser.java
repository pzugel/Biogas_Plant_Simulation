package vrl.biogas.specedit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecificationParser {
	static String text = "";
	ArrayList<ValiTableEntry> data;
	static int number_of_entries;
	
	public SpecificationParser(File path, ArrayList<ValiTableEntry> valiData) throws FileNotFoundException {
		this.data = valiData;
		readFile(path);
		modifyInput();
		generateSpecs();
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
	
	private void generateSpecIndents(String input) {	
		String param_open = "\\{";
		String comma = ",";

		input = input.replaceAll(param_open, "{\n");
		input = input.replaceAll(comma, "\n");
		
		//std::cout << input << std::endl;
		
		  
		Scanner lineIter = new Scanner(input);
		int ind = 0;
		while (lineIter.hasNextLine()) {
			String line = lineIter.nextLine();
			System.out.println("line: " + line);
			int valuePos = line.indexOf('=');
			int timeSt = line.lastIndexOf("$");
			if (valuePos != -1 || timeSt == 0) {
				ValiTableEntry newEntry = new ValiTableEntry(ind);
				data.add(newEntry);
			}
			if (line.indexOf('{') != -1) {
				ind += 1;
			}
			if (line.indexOf('}') != -1) {
				ind -= 1;	
			}
		}
		lineIter.close();
		
		number_of_entries = data.size();
		System.out.println("number_of_entries: " + number_of_entries);
	}

	private void modifyInput() {
		text = text.replaceAll("\\s","");
		
		String str_arr = "\\{\"[a-zA-Z0-9_]+\"\\}";
		String timestamp = "\\{([0-9E.\\-\\*]+,)*[0-9E.\\-\\*]+\\}";

		Matcher strArrMatcher = Pattern.compile(str_arr).matcher(text); //Match types Str[]
		while (strArrMatcher.find()) 
		{
			String match = strArrMatcher.group();
			String matched_str_arr = match.substring(1,match.length()-1);
			text = text.replace(match, "$"+matched_str_arr+"?");
		} 
		
		Matcher timestampMatcher = Pattern.compile(timestamp).matcher(text); //Match timestamps
		while (timestampMatcher.find()) 
		{
			
			String match = timestampMatcher.group();
			System.out.println("timestampMatch: " + match);
			String replacement = match;
			//int pos = match.indexOf(',');
			//int pos_end = match.indexOf('}');
			//String replacement = "$" + match.substring(1, pos) + "#" + match.substring(pos+1,pos_end) + "?";
			replacement = replacement.replaceAll("\\{", "\\$");
			replacement = replacement.replaceAll("\\}", "?");
			replacement = replacement.replaceAll(",", "#");
			System.out.println("timestampReplacement: " + replacement);
			text = text.replace(match, replacement);
		} 
		
		String param_open = "\\{";
		String param_close = "\\}$";
		String param_close_comma = "\\},$";
		String comma = ",";
		String eq = "=$";
		
		generateSpecIndents(text);
		
		text = text.replaceAll(param_open, "\n");
		text = text.replaceAll(comma, "\n");

		String tmp = "";
		
		Scanner scanner = new Scanner(text);
		while (scanner.hasNextLine()) {
		   String line = scanner.nextLine();
		   line = line.replaceAll(param_close_comma, "");
		   line = line.replaceAll(param_close, "");
		   line = line.replaceAll(comma, "");
		   line = line.replaceAll(eq, "");

			if(!line.isEmpty())
				tmp += line + "\n";
		}
		scanner.close();
		text = tmp;

		text = text.replaceAll("\\$", "{");
		text = text.replaceAll("\\?", "}");
		text = text.replaceAll("#", ",");
	}

	//Should be expanded by testValidationMatch()
	private void generateSpecs() {
		String timestamp = "\\{([0-9E.\\-\\*]+,)*[0-9E.\\-\\*]+\\}";
		
		int index = 0;
		Scanner scanner = new Scanner(text);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
		   
			int valuePos = line.indexOf('=');
			if (valuePos == -1)
			{
				if(line.matches(timestamp))
				{
					data.get(index).setSpecVal(line);
					data.get(index).setName("timeStamp");
					data.get(index).setType("NULL");
					data.get(index).setValueField(true);
				}
				else {
					data.get(index).setName(line);
					data.get(index).setValueField(false);
					data.get(index).setType(" ");
			   }
			}
			else
			{
			   data.get(index).setValueField(true);
			   data.get(index).setType("NULL");
			   data.get(index).setName(line.substring(0, valuePos));		
			   data.get(index).setSpecVal(line.substring(valuePos+1));
			}
			
			System.out.println(data.get(index).getName() + " " + data.get(index).getType() + " " + data.get(index).getSpecVal());
			++index;
		}
		scanner.close();
		
	}
}
