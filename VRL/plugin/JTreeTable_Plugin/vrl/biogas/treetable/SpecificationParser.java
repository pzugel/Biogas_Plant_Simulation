package vrl.biogas.treetable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpecificationParser {
	static String text = "";
	List<ValiTableEntry> data;
	
	public SpecificationParser(File path, List<ValiTableEntry> valiData) throws FileNotFoundException {
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

	private void modifyInput() {
		text = text.replaceAll("\\s","");
		
		String str_arr = "\\{\"[a-zA-Z0-9_]+\"\\}";
		String timestamp = "\\{[0-9E.\\-\\*]+,[0-9E.\\-\\*]+\\}";

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
			int pos = match.indexOf(',');
			int pos_end = match.indexOf('}');
			String replacement = "$" + match.substring(1, pos) + "#" + match.substring(pos+1,pos_end) + "?";
			text = text.replace(match, replacement);
		} 
		
		String param_open = "\\{";
		String param_close = "\\}$";
		String param_close_comma = "\\},$";
		String comma = ",";
		String eq = "=$";
		
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
		String timestamp = "\\{[0-9E.\\-\\*]+,[0-9E.\\-\\*]+\\}";
		
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
			   }
		   }
		   else
		   {
			   data.get(index).setSpecVal(line.substring(valuePos+1));
		   }
		   ++index;
		}
		scanner.close();
	}
}
