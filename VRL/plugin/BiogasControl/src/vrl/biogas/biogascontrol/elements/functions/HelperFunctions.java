package vrl.biogas.biogascontrol.elements.functions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperFunctions {
	
	static String data_string;
	static String header_string;
	static ArrayList<ArrayList<ArrayList<String>>> values;
	
	static public boolean read_values_from_reactor(String dir) throws FileNotFoundException{	
		System.out.println("read_values_from_reactor: " + dir);
		ArrayList<ArrayList<String>> hydroFile = new ArrayList<ArrayList<String>>();
		
		File f = new File(dir);
		if(f.exists()){
			String vals = remove_header(new File(dir));
			
			Scanner lineIter = new Scanner(vals);
			while(lineIter.hasNextLine()) {
				String line = lineIter.nextLine();
				//System.out.println("LINE: " + line);
				
				ArrayList<String> valArr = new ArrayList<String>();
				Scanner valIter = new Scanner(line);
				while(valIter.hasNext()) {
					String val = valIter.next();
					valArr.add(val);
					//System.out.println("VAL: " + val);
				}
				valIter.close();
				hydroFile.add(valArr);
			}
			lineIter.close();
			values.add(hydroFile);
		}
		else {
			System.out.println("File does not exist!");
			return false;
		}
		return true;
	}
	
	static public String remove_header_from_string(String file_as_string)
	{
		data_string = "";
		header_string = "";
		
		Pattern p = Pattern.compile("(^#)|(^[a-zA-Z])");
		
		Scanner lineIter = new Scanner(file_as_string);
		while(lineIter.hasNextLine()) {
			String line = lineIter.nextLine();
			Matcher m = p.matcher(line);
			if(!m.find()) {
				data_string += line + "\n";
			} else {
				header_string += line + "\n";
			}
		}
		lineIter.close();
		return data_string;
	}
	
	static public String remove_header(File path) throws FileNotFoundException
	{
		data_string = "";		
		Pattern p = Pattern.compile("(^#)|(^[a-zA-Z])");		
		
		Scanner lineIter = new Scanner(path);
		while(lineIter.hasNextLine()) {
			String line = lineIter.nextLine();
			Matcher m = p.matcher(line);
			if(!m.find())
				data_string += line + "\n";
		}
		lineIter.close();
		return data_string;
	}
	
	static public String get_header(File path) throws FileNotFoundException
	{
		header_string = "";
		Pattern p = Pattern.compile("(^#)|(^[a-zA-Z])");
		
		Scanner lineIter = new Scanner(path);
		while(lineIter.hasNextLine()) {
			String line = lineIter.nextLine();
			Matcher m = p.matcher(line);
			if(m.find())
				header_string += line + "\n";
		}
		lineIter.close();
		return header_string;
	}

}
