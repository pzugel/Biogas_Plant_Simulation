package vrl.biogas.treetable;

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
			if(index!=parameters.size()-1) {
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
					else if(entry.getName() == "timeTableContent") {
						printWriter.print(entry.getSpecVal() + ",\n");
					}	
					else {
						printWriter.print(entry.getName() + "=" + entry.getSpecVal() + ",\n");
					}			
				}

				if(entry.getIndent()>parameters.get(index+1).getIndent())
				{
					int num_closing_par = (entry.getIndent())-(parameters.get(index+1).getIndent());
					for(int j=0; j<num_closing_par; j++) {
						char[] closingParams = new char[entry.getIndent()-j-1];
					    Arrays.fill(closingParams, '\t');
					    printWriter.print((new String(closingParams)) + "},\n");
					}
				}	
			}
			else { //Last element
				for(int i=0; i<entry.getIndent()-1; i++) {
					char[] closingParams = new char[entry.getIndent()-i-1];
				    Arrays.fill(closingParams, '\t');
					printWriter.print((new String(closingParams)) + "},\n");
				}
				printWriter.print( "}");
			}
			
			++ index;
		}
		
		printWriter.close();
		
	}
}
