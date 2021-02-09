#pragma once 

static std::string data_string;
static std::string header_string;

extern "C"{
	const char* remove_header_from_string(const char* file_as_string);
	const char* remove_header(const char* path);
	const char* get_header(const char* path);
}
