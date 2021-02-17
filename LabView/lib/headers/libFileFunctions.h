const char* remove_header(const char* path);
const char* get_header(const char* path);
const char* read_filenames(const char* path);
void update_outputFiles(const char* outputFiles_path);
void merge_all_hydrolysis(const char* working_dir, const char* reactor_names, int simulation_starttime, int current_starttime);
void merge_one_reactor(const char* working_dir, int simulation_starttime, int current_starttime);
void update_methane_inflow(const char* outflow_infile, const char* methane_specfile);
void update_hydrolysis_inflow(const char* outflow_infile, const char* hydrolysis_specfiles, double fractions[]);
void get_hydrolysis_PH(double (&ph_arr)[3], const char* reactor_state_files);
