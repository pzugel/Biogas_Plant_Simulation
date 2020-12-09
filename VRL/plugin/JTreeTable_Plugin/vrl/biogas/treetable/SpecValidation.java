package vrl.biogas.treetable;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SpecValidation {
	
	private String validationMessage = "";
	private List<Integer> validationErrorParams;
	private boolean isValid = true;
	
	public SpecValidation(List<ValiTableEntry> parameters) {
		
		validationErrorParams = new ArrayList<Integer>();
		
		String isBool = "true|false";
		String isString = "\"[a-zA-Z0-9_.]+\"";
		String isStringArr = "\\{\"[a-zA-Z0-9_]+\"\\}";
		String isInt = "[0-9\\*]+";
		String isDouble = "[0-9E.\\*\\-]+";
		String isDoubleTimestamp = "\\{[0-9E.\\*\\-]+,[0-9E.\\*\\-]+\\}";
		String isIntTimestamp = "\\{[0-9\\*]+,[0-9\\*]+\\}";
		
		int index = 0;
		for(ValiTableEntry entry : parameters)
		{
			String type = entry.getType();
			type = type.toLowerCase();
			if(entry.isValueField)
			{
				if(type.equals("boolean"))
				{
					if(!Pattern.compile(isBool).matcher(entry.getSpecVal()).matches())
					{
						validationMessage += "Type ERROR: \"" + entry.getName() + "\" should be of type " +  type + "\n";
						validationErrorParams.add(index);
						isValid = false;
					}
				}
	
				else if(type.equals("double") || type.equals("number"))
				{
					if(!Pattern.compile(isDouble).matcher(entry.getSpecVal()).matches() &&
							!Pattern.compile(isDoubleTimestamp).matcher(entry.getSpecVal()).matches())
					{
						validationMessage += "Type ERROR: \"" + entry.getName() + "\" should be of type " +  type + "\n";
						validationErrorParams.add(index);
						isValid = false;
					}
					if(!entry.getRangeMin().isEmpty() && !entry.getRangeMax().isEmpty())
					{ 
						if(Pattern.compile(isDouble).matcher(entry.getSpecVal()).matches() 
								&& (Double.parseDouble(entry.getSpecVal())<Double.parseDouble(entry.getRangeMin()) 
										|| Double.parseDouble(entry.getSpecVal())>Double.parseDouble(entry.getRangeMax())))
						{
							validationMessage += "Range ERROR: "
								+ entry.getName()
								+ " should be in Range {" 
								+ entry.getRangeMin() + "," 
								+ entry.getRangeMax() + "}\n";
							validationErrorParams.add(index);
							isValid = false;
						}
					}
				}
	
				else if(type.equals("integer"))
				{
					if(!Pattern.compile(isInt).matcher(entry.getSpecVal()).matches() &&
							!Pattern.compile(isIntTimestamp).matcher(entry.getSpecVal()).matches())
					{
						validationMessage += "Type ERROR: \"" + entry.getName() + "\" should be of type " +  type + "\n";
						validationErrorParams.add(index);
						isValid = false;
					}
					if(!entry.getRangeMin().isEmpty() && !entry.getRangeMax().isEmpty())
					{ 
						if(Pattern.compile(isInt).matcher(entry.getSpecVal()).matches() 
								&& (Integer.parseInt(entry.getSpecVal())<Integer.parseInt(entry.getRangeMin()) 
										|| Integer.parseInt(entry.getSpecVal())>Integer.parseInt(entry.getRangeMax())))
						{
							validationMessage += "Range ERROR: "
								+ entry.getName()
								+ " should be in Range {" 
								+ entry.getRangeMin() + "," 
								+ entry.getRangeMax() + "}\n";
							validationErrorParams.add(index);
							isValid = false;
						}
					}
				}
	
				else if(type.equals("string"))
				{
					if(!Pattern.compile(isString).matcher(entry.getSpecVal()).matches())
					{
						validationMessage += "Type ERROR: \"" + entry.getName() + "\" should be of type " +  type + "\n";
						validationErrorParams.add(index);
						isValid = false;
					}
				}
	
				else if(type.equals("string[]"))
				{
					if(!Pattern.compile(isStringArr).matcher(entry.getSpecVal()).matches())
					{
						validationMessage += "Type ERROR: \"" + entry.getName() + "\" should be of type " +  type + "\n";
						validationErrorParams.add(index);
						isValid = false;
					}
				}
	
				else
				{
					validationMessage += "Type ERROR: \"" + entry.getName() + "\" has unknown type " +  type + "\n";
					validationErrorParams.add(index);
					isValid = false;	
				}	
			}
			++index;
		}
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public String getValMessage() {
		return validationMessage;
	}
	
	public List<Integer> getErrorParams(){
		return validationErrorParams;
	}
}
