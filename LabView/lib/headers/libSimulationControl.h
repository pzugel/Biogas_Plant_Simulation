const char* get_console_output();
void set_console_output(const char* cmd);
void add_console_output(const char* cmd);

const char* get_sim_log();
void set_sim_log(const char* log);
void add_to_sim_log(const char* log);

const char* get_plant_structure();
void set_plant_structure(const char* structure);

const char* get_active_element();
void set_active_element(const char* elem);

int get_iteration();
void set_iteration(int iter);
void set_interation_log(int iter);

void set_simulation_file(const char* sim_file);
const char* get_simulation_file();

void set_hydrolyse_specification(const char* hydrolyse_spec_file);
const char* get_hydrolyse_specification();

void set_methane_specification(const char* methane_spec_file);
const char* get_methane_specification();

void set_working_directory(const char* dir);
const char* get_working_directory();

void set_sim_starttime(double time);
double get_sim_starttime();

void set_sim_endtime(double time);
double get_sim_endtime();

void set_sim_currenttime(double time);
double get_sim_currenttime();

void reset_paused_time();
void add_paused_time(double time);
double get_paused_time();

const char* load_outputFiles(const char* filepath);

void set_num_hydrolysis_reactors(int num);
int get_num_hydrolysis_reactors();

void set_hydrolysis_fractions(const char* fract);
const char* get_hydrolysis_fractions();


