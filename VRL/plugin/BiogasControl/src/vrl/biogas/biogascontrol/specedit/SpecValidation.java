package vrl.biogas.biogascontrol.specedit;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.tree.TreePath;

/**
 * Validation methods for lua specification files
 * @author Paul Zügel
 */
public class SpecValidation {
	
	private String validationMessage = "";
	//private List<Integer> validationErrorParams;
	private List<TreePath> validationErrorParams;
	private boolean isValid = true;
	
	public SpecValidation(List<ValiTableEntry> parameters) {
		
		validationErrorParams = new ArrayList<TreePath>();
		
		String isBool = "(\\s)*true(\\s)*|(\\s)*false(\\s)*";
		String isString = "\"(\\s)*[a-zA-Z0-9_.(\\s)*]+(\\s)*\"";
		String isStringArr = "(\\s)*\\{(\\s)*(\"[a-zA-Z0-9_(\\s)*]+\"(\\s)*(,)?(\\s)*)*(\\s)*\\}(\\s)*";
		String isInt = "(\\s)*[0-9\\*]+(\\s)*";
		String isDouble = "(\\s)*[0-9Ee.\\*\\-]+(\\s)*";
		String isDoubleTimestamp = "(\\s)*\\{(\\s)*([0-9Ee.\\*\\-]+(\\s)*,(\\s)*)+[0-9Ee.\\*\\-]+(\\s)*\\}(\\s)*";
		String isIntTimestamp = "(\\s)*\\{(\\s)*([0-9\\*]+(\\s)*,(\\s)*)+[0-9\\*]+(\\s)*\\}(\\s)*";
		
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
						validationErrorParams.add(entry.getPath());
						isValid = false;
					}
				}
	
				else if(type.equals("double") || type.equals("number"))
				{
					if(!Pattern.compile(isDouble).matcher(entry.getSpecVal()).matches() &&
							!Pattern.compile(isDoubleTimestamp).matcher(entry.getSpecVal()).matches())
					{
						validationMessage += "Type ERROR: \"" + entry.getName() + "\" should be of type " +  type + "\n";
						validationErrorParams.add(entry.getPath());
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
							validationErrorParams.add(entry.getPath());
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
						validationErrorParams.add(entry.getPath());
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
							validationErrorParams.add(entry.getPath());
							isValid = false;
						}
					}
				}
	
				else if(type.equals("string"))
				{
					if(!Pattern.compile(isString).matcher(entry.getSpecVal()).matches())
					{
						validationMessage += "Type ERROR: \"" + entry.getName() + "\" should be of type " +  type + "\n";
						validationErrorParams.add(entry.getPath());
						isValid = false;
					}
				}
	
				else if(type.equals("string[]"))
				{
					String spec = entry.getSpecVal();
					spec.replaceAll(" ", "");
					if(!Pattern.compile(isStringArr).matcher(spec).matches())
					{
						validationMessage += "Type ERROR: \"" + entry.getName() + "\" should be of type " +  type + "\n";
						validationErrorParams.add(entry.getPath());
						isValid = false;
					}
				}
	
				else
				{
					validationMessage += "Type ERROR: \"" + entry.getName() + "\" has unknown type " +  type + "\n";
					validationErrorParams.add(entry.getPath());
					isValid = false;	
				}	
			}
		}
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public String getValMessage() {
		return validationMessage;
	}
	
	public List<TreePath> getErrorParams(){
		return validationErrorParams;
	}
}
