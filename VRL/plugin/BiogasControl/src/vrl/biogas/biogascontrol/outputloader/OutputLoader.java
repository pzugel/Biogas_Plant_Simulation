package vrl.biogas.biogascontrol.outputloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for outputFiles.lua file
 * @author paul
 */
public class OutputLoader {

	static String input;
	static File filepath;
	static List<OutputEntry> entries;
	
	public OutputLoader(File file) 
	{
		filepath = file;
	}
	
	public void load() throws FileNotFoundException
	{
		input = "";

		Scanner scanner = new Scanner(filepath);
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			line = line.replaceAll("\\s","");

			if(!line.startsWith("--"))
			{
				if(line.indexOf("--")!=-1)
				{
					line = line.substring(0, line.indexOf("--"));
				}
				input += line + "\n";	  
			}
		}
		scanner.close();
		input = input.replaceAll("\\s","");
		readOutputFiles();
	}

	private void readXValues(List<String> x_Names, List<String> x_Units, List<Integer> x_Cols){
		String x_value = "x=\\{[a-zA-Z0-9_]+=\\{unit=\"[a-zA-Z0-9\\^\\[\\]]+\",col=[0-9]+";

		List<String> allMatches = new ArrayList<String>();
		Matcher match_x_values = Pattern.compile(x_value).matcher(input);
		while (match_x_values.find()) {
			   allMatches.add(match_x_values.group(0));
		}
		
		for(String match : allMatches) 
		{
			match = match.replaceAll("\\{", "");
			
			int col_pos = match.indexOf(",col=");
			int new_col = Integer.parseInt(match.substring(col_pos+5)) -1;
			x_Cols.add(new_col);
			
			int unit_pos = match.indexOf("unit=");
			String new_unit = match.substring(unit_pos+6, col_pos-1);
			x_Units.add(new_unit);
			
			int name_pos = match.indexOf("x=");
			String new_name = match.substring(name_pos+2, unit_pos-1);
			x_Names.add(new_name);
		}
	}
	
	private void modifyInput() {
		String x_value = "x=\\{[a-zA-Z0-9_]+=\\{unit=\"[a-zA-Z0-9\\^\\[\\]]+\",col=[0-9]+";
		String keys = "keys=\\{";
		String outputFiles = "outputFiles=\\{";

		input = input.replaceAll(x_value, "");
		input = input.replaceAll(keys, "");
		input = input.replaceAll(outputFiles, "");
		input = input.replaceAll("\\{", "\\{\n");
		input = input.replaceAll(",", ",\n");
	}
	
	private void readOutputFiles()
	{	
		entries = new ArrayList<OutputEntry>();
		
		List<String> x_Names = new ArrayList<String>();
		List<String> x_Units = new ArrayList<String>();
		List<Integer> x_Cols = new ArrayList<Integer>();

		readXValues(x_Names, x_Units, x_Cols);

		String param = "^[a-zA-Z0-9_\\[\\]\\s\"]+=";
		String filename = "^filename=";
		String col = "^col=";
		String unit = "^unit=";
		String names = "^[a-zA-Z0-9_\\[\\]\\s\"]+=\\{";
		String y_value = "^y=\\{";

		modifyInput();
		
		String lastFileName = "";
		int lastXCol = -1;
		String lastXName = "";
		String lastXUnit = "";

		int ind = -1;
		Scanner scanner = new Scanner(input);
		while (scanner.hasNextLine()) 
		{
			String line = scanner.nextLine();
			Matcher param_matcher = Pattern.compile(param).matcher(line);
			if(param_matcher.find())
			{
				Matcher names_matcher = Pattern.compile(names).matcher(line);
				Matcher filename_matcher = Pattern.compile(filename).matcher(line);
				Matcher col_matcher = Pattern.compile(col).matcher(line);
				Matcher unit_matcher = Pattern.compile(unit).matcher(line);
				
				if(names_matcher.find()) 
				{
					Matcher y_value_mather = Pattern.compile(y_value).matcher(line);
					if(y_value_mather.find()) 
					{
						entries.get(ind).setIndent(0);
						entries.get(ind).setIsValue(false);
						//entries.get(ind).set
					}
					else
					{
						
						ind += 1;
						// Contruct new Element
						OutputEntry newEntry = new OutputEntry();
						entries.add(newEntry);

						entries.get(ind).setIndent(1);
						entries.get(ind).setIsValue(true);
						
						line = line.replaceAll("=\\{", "");
						System.out.println("line: " + line);
						if(line.contains("AllLiquid")) {
							line = "All Liquid";
						}
						entries.get(ind).setName(line);
						entries.get(ind).setFilename(lastFileName);
					}
				}
				else if(filename_matcher.find())
				{
					
					line = line.replaceAll(",", "");
					line = line.replaceAll("\"", "");
					line = line.replaceAll("filename=", "");
					lastFileName = line;				
					
					entries.get(ind).setFilename(line);
					entries.get(ind).setColumn(-1);
					entries.get(ind).setUnit("");	
					
					lastXCol = x_Cols.get(0);
					entries.get(ind).setXValueColumn(lastXCol);
					x_Cols.remove(0);
					
					lastXUnit = x_Units.get(0);
					lastXUnit = lastXUnit.replaceAll("\"", "");
					entries.get(ind).setXValueUnit(lastXUnit);
					x_Units.remove(0);
					
					lastXName = x_Names.get(0);
					entries.get(ind).setXValueName(lastXName);
					x_Names.remove(0);
				}
				else if(col_matcher.find())
				{
					line = line.replaceAll("col=", "");
					line = line.replaceAll("\\}", "");
					line = line.replaceAll(",", "");
					int thiscol = Integer.parseInt(line) -1;
					entries.get(ind).setColumn(thiscol);
					entries.get(ind).setXValueColumn(lastXCol);
					entries.get(ind).setXValueName(lastXName);
					entries.get(ind).setXValueUnit(lastXUnit);
				}
				else if(unit_matcher.find())
				{
					line = line.replaceAll("unit=", "");
					line = line.replaceAll("\\}", "");
					line = line.replaceAll(",", "");
					line = line.replaceAll("\"", "");
					entries.get(ind).setUnit(line);	

				}
			}
		}
		scanner.close();
	}

	public List<OutputEntry> getData(){
		return entries;
	}
}
