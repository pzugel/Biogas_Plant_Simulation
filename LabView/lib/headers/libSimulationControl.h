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

void set_validation_file(const char* val_file);
const char* get_validation_file();

void set_specification_file(const char* spec_file);
const char* get_specification_file();

void set_working_directory(const char* dir);
const char* get_working_directory();

void set_sim_starttime(double time);
double get_sim_starttime();

void set_sim_endtime(double time);
double get_sim_endtime();

void set_sim_currenttime(double time);
double get_sim_currenttime();

const char* load_outputFiles(const char* filepath);
