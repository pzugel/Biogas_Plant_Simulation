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
#include <iostream>

/**
 * Write a new specification file
 *
 * Method writes a new specification file into the
 * "outputSpecs" String.
 *
 * @param specs: Specifications committed by LabView
 * @return Bool whether the specs are valid
 */
void BiogasSpecValiReader::
writeOutputSpecs(std::string specs)
{
	std::cout << "writeOutputSpecs" << std::endl;
	this->outputSpecs = "";
	std::vector<std::string> inputSpecs = {};
	std::istringstream lineIter(specs);
	for (std::string line; std::getline(lineIter, line); )
	{
		inputSpecs.push_back(line);
	}
	for(int i=0; i<number_of_entries; i++)
	{
		this->outputSpecs += std::string(this->entries[i].indent, '\t');
		if(this->entries[i].glyph == 15)
			this->outputSpecs += this->entries[i].leftCell + "={\n";
		else
		{
			if (this->entries[i].leftCell.rfind("\"", 0) == 0)
				this->outputSpecs += "[" + this->entries[i].leftCell + "]=" + inputSpecs[i] + ",\n";
			else if(this->entries[i].leftCell == "timeStamp")
				this->outputSpecs += inputSpecs[i] + ",\n";
			else
				this->outputSpecs += this->entries[i].leftCell + "=" + inputSpecs[i] + ",\n";
		}
		
		if(i<number_of_entries-1)
		{
			if(this->entries[i].indent>this->entries[i+1].indent)
			{
				int num_closing_par = (this->entries[i].indent)-(this->entries[i+1].indent);
				for(int j=0; j<num_closing_par; j++)
					this->outputSpecs += std::string(this->entries[i].indent-j-1,'\t') + "},\n";
			}
		}
		
		if(i == inputSpecs.size()-1) //lastIteration
		{
			int num_closing_par = this->entries[i].indent;
			for(int j=0; j<num_closing_par; j++)
					this->outputSpecs += std::string(this->entries[i].indent-j-1,'\t') + "},\n";
			while(this->outputSpecs.back() != '}')
				this->outputSpecs = this->outputSpecs.substr(0, this->outputSpecs.size()-1);
		}
	}
}

