/*
 * Copyright (c) 2020:  G-CSC, Goethe University Frankfurt
 * Author: Paul Zügel
 * 
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */

#include "biogas_spec_vali_reader.h"
#include <string>
#include <vector>
#include <fstream>	
#include <regex>

/**
 * Validates specifications
 *
 * Verifies if given specifications are the correct data
 * type and if they suit the range restrictions.
 * Parameters where the validation failed are added into the
 * "validationErrorParams" and a corresponding "validationMessage"
 * to display in LabView is generated.
 *
 * @param specs: Specifications committed by LabView
 * @return Bool whether the specs are valid
 */
bool BiogasSpecValiReader::
validateSpecs(std::string specs)
{
	std::vector<std::string> inputSpecs = {};
	std::istringstream lineIter(specs);
	for (std::string line; std::getline(lineIter, line); )
	{
		inputSpecs.push_back(line);
	}
	
	this->validationMessage = "";
	this->validationErrorParams = "";
	std::regex isBool ("(\\s)*true(\\s)*|(\\s)*false(\\s)*");
	std::regex isString ("\"(\\s)*[a-zA-Z0-9_.(\\s)*]+(\\s)*\"");
	std::regex isStringArr ("(\\s)*\\{(\\s)*(\"[a-zA-Z0-9_(\\s)*]+\"(\\s)*(,)?(\\s)*)*(\\s)*\\}(\\s)*");
	std::regex isInt ("(\\s)*[0-9\\*]+(\\s)*");
	std::regex isDouble ("(\\s)*[0-9Ee.\\*\\-]+(\\s)*");
	std::regex isDoubleTimestamp ("(\\s)*\\{(\\s)*([0-9Ee.\\*\\-]+(\\s)*,(\\s)*)+[0-9Ee.\\*\\-]+(\\s)*\\}(\\s)*");
	std::regex isIntTimestamp ("(\\s)*\\{(\\s)*([0-9\\*]+(\\s)*,(\\s)*)+[0-9\\*]+(\\s)*\\}(\\s)*");

	bool isValid = true;
	for(int i=0; i<this->number_of_entries; i++)
	{
		std::string type = this->entries[i].type;
		std::for_each(type.begin(), type.end(), [](char & c) {
        	c = ::tolower(c);
    	});
    	
		if(this->entries[i].glyph != 15)
		{
			if(type == "boolean")
			{
				if(!std::regex_match(inputSpecs[i], isBool))
				{
					this->validationMessage += "Type ERROR: \"" + this->entries[i].leftCell + "\" should be of type " +  type + "\n";
					this->validationErrorParams += std::to_string(i) + "\n";
					isValid = false;
				}
			}

			else if(type == "double" || type == "number")
			{
				if(!std::regex_match(inputSpecs[i], isDouble) &&
						!std::regex_match(inputSpecs[i], isDoubleTimestamp))
				{
					this->validationMessage += "Type ERROR: \"" + this->entries[i].leftCell + "\" should be of type " +  type + "\n";
					this->validationErrorParams += std::to_string(i) + "\n";
					isValid = false;
				}
				if(!this->entries[i].rangeMin.empty() && !this->entries[i].rangeMax.empty())
				{ 
					if(std::regex_match(inputSpecs[i], isDouble) 
							&& (std::stod(inputSpecs[i])<std::stod(this->entries[i].rangeMin) || std::stod(inputSpecs[i])>std::stod(this->entries[i].rangeMax)))
					{
						this->validationMessage += "Range ERROR: "
							+ this->entries[i].leftCell
							+ " should be in Range {" 
							+ this->entries[i].rangeMin + "," 
							+ this->entries[i].rangeMax + "}\n";
						this->validationErrorParams += std::to_string(i) + "\n";
						isValid = false;
					}
				}
			}

			else if(type == "integer")
			{
				if(!std::regex_match(inputSpecs[i], isInt) &&
						!std::regex_match(inputSpecs[i], isIntTimestamp))
				{
					this->validationMessage += "Type ERROR: \"" + this->entries[i].leftCell + "\" should be of type " +  type + "\n";
					this->validationErrorParams += std::to_string(i) + "\n";
					isValid = false;
				}
				if(!this->entries[i].rangeMin.empty() && !this->entries[i].rangeMax.empty())
				{ 
					if(std::regex_match(inputSpecs[i], isInt) 
							&& (std::stoi(inputSpecs[i])<std::stoi(this->entries[i].rangeMin) || std::stoi(inputSpecs[i])>std::stoi(this->entries[i].rangeMax)))
					{
						this->validationMessage += "Range ERROR: "
							+ this->entries[i].leftCell
							+ " should be in Range {" 
							+ this->entries[i].rangeMin + "," 
							+ this->entries[i].rangeMax + "}\n";
						this->validationErrorParams += std::to_string(i) + "\n";
						isValid = false;
					}
				}
			}

			else if(type == "string")
			{
				if(!std::regex_match(inputSpecs[i], isString))
				{
					this->validationMessage += "Type ERROR: \"" + this->entries[i].leftCell + "\" should be of type " +  type + "\n";
					this->validationErrorParams += std::to_string(i) + "\n";
					isValid = false;
				}
			}

			else if(type == "string[]")
			{
				std::string spec = inputSpecs[i];
				spec.erase(remove_if(spec.begin(), spec.end(), isspace), spec.end());
				if(!std::regex_match(spec, isStringArr))
				{
					this->validationMessage += "Type ERROR: \"" + this->entries[i].leftCell + "\" should be of type " +  type + "\n";
					this->validationErrorParams += std::to_string(i) + "\n";
					isValid = false;
				}
			}

			else
			{
				this->validationMessage += "Type ERROR: \"" + this->entries[i].leftCell + "\" has unknown type " +  type + "\n";
				this->validationErrorParams += std::to_string(i) + "\n";
				isValid = false;	
			}	
		}
	}
	
	return isValid;
}
