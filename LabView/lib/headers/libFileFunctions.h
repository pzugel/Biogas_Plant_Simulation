const char* remove_header(const char* path);
const char* get_header(const char* path);
const char* read_filenames(const char* path);
void merge_all_hydrolysis(const char* working_dir, const char* reactor_names, int simulation_starttime, int current_starttime);
const char* merge_hydrolysis_files(const char* dir, const char* reactor_names, const char* filename, int current_starttime);
