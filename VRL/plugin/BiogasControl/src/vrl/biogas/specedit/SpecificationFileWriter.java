package vrl.biogas.specedit;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class SpecificationFileWriter {

	public SpecificationFileWriter(File path, List<ValiTableEntry> parameters) throws IOException {
		
		FileWriter fileWriter = new FileWriter(path);
	    PrintWriter printWriter = new PrintWriter(fileWriter);    
	    
		//if(validateSpecs(specs)) TODO
	    
	    int index = 0;
		for(ValiTableEntry entry : parameters)
		{
			System.out.println(index + " " + entry.getName() + " " + entry.getSpecVal());
			char[] indents = new char[entry.getIndent()];
		    Arrays.fill(indents, '\t');
		    printWriter.print(new String(indents));
			if(!entry.isValueField) {
				printWriter.print(entry.getName() + "={\n");
			}	
			else
			{
				if (entry.getName().startsWith("\"")) {
					printWriter.print("[" + entry.getName() + "]=" + entry.getSpecVal() + ",\n");
				}	
				else if(entry.getName() == "timeStamp") {
					printWriter.print(entry.getSpecVal() + ",\n");
				}	
				else {
					printWriter.print(entry.getName() + "=" + entry.getSpecVal() + ",\n");
				}			
			}
			
			if(index<parameters.size()-1) {
				if(entry.indent>parameters.get(index+1).indent)
				{
					int num_closing_par = (entry.indent)-(parameters.get(index+1).indent);
					for(int j=0; j<num_closing_par; j++) {
						for(int k=0; k<(entry.indent-j-1); k++) {
							printWriter.print('\t');
						}
						printWriter.print("},\n");
					}
				}
			} 
			
			if(index == parameters.size()-1) { //Last Iteration
				int num_closing_par = entry.indent;
				String lastEntry = "";
				for(int j=0; j<num_closing_par; j++) {
					for(int k=0; k<(entry.indent-j-1); k++) {
						lastEntry += '\t';
					}
					lastEntry += "},\n";
				}
					
				lastEntry = lastEntry.substring(0, lastEntry.length() - 2);
				printWriter.print(lastEntry);
			}
			++ index;
		}
		
		printWriter.close();
		
	}
}
