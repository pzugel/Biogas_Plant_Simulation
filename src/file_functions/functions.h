#pragma once 

static std::string data_string;
static std::string header_string;
static std::string ph_string;

extern "C"{
	const char* remove_header_from_string(const char* file_as_string);
	const char* remove_header(const char* path);
	const char* get_header(const char* path);
}

/**
 * Helper function to correcly convert doube to string 
 * (keeping the scientific notation)
 * 
 * @param d: Double value
 * @return double in string notation
 */
std::string conv_to_string(double d){
	std::ostringstream out;
    out << std::setprecision(14) << d;
    return out.str();
}

/**
 * Helper function to correcly read out doubles in dot representation
 * "311.15" -> 311,15 
 * 
 * @param s: Double as string
 * @return double
 */
double dot_conversion(std::string s){
	std::istringstream valStream(s);
	double out;
	while (valStream)
		valStream >> std::setprecision(14) >> out;
	return out;
}

/**
 * Helper function to correcly read out doubles in dot representation
 * 
 * @param s: Double as string
 * @return long double
 */
long double dot_conversion_regex(std::string s){
	std::string d = s;
	d = std::regex_replace(d, (std::regex) ",", ".");
	return dot_conversion(d);
}
