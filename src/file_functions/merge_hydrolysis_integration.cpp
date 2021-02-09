/*
 * Copyright (c) 2020:  G-CSC, Goethe University Frankfurt
 * Author: Rebecca Pech, Paul Zügel
 * 
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 */

#include "functions.h"

#include <sstream>
#include <regex>
#include <boost/algorithm/string.hpp>

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

using namespace std;

static string gesamtdatei_string;

static const int timestep = 1; //hourly

/**
 * Integrates over multiple files as stated in "output_names" and merges
 * them into one file. This file will be returned as string.
 * 
 * @param x: starttime of the simulation
 * @param anzahl: number of reactors 
 * @param output_names: Path to all files to merge
 * @return File to write out as string
 */
string merge_hydrolysis_files_integration(
	float x,
	int anzahl,
	std::string output_names)
{
	gesamtdatei_string = "";
	std::istringstream output(output_names);
	float y = x + timestep;
	
    // Dateien, die eingelesen werden sollen:
    string file_name;
    istringstream file_output1;
    istringstream file_output2;
    ifstream file_stream_output1;
    ifstream file_stream_output2;
    
    // Pro Runde eine Outflow-Datei zur aktuellen Datei dazuaddieren:
    for (int runde=1; runde<anzahl; runde++){	
        //Dateien lesen:
        if (runde == 1){	
				
            getline(output,file_name);
            file_stream_output1 = ifstream(file_name);
			std::string output1_content((std::istreambuf_iterator<char>(file_stream_output1)),
				(std::istreambuf_iterator<char>()));
            file_output1 = istringstream(output1_content);

            getline(output, file_name);
            file_stream_output2 = ifstream(file_name);
			std::string output2_content((std::istreambuf_iterator<char>(file_stream_output2)),
				(std::istreambuf_iterator<char>()));
            file_output2 = istringstream(output2_content);
        }
        else{        
            
            getline(output,file_name);
            file_stream_output1 = ifstream(file_name);
			std::string content((std::istreambuf_iterator<char>(file_stream_output1)),
				(std::istreambuf_iterator<char>()));
            file_output1 = istringstream(content);
            
            file_output2 = istringstream(gesamtdatei_string);
        }
        
        // Fehler abfangen: Falls Dateien nicht vorhanden sind
		if(!file_stream_output1){
			return "File not found (output1).";
		}
		else if(!file_stream_output2){
			return "File not found (output2).";
		}
		
		string zahl1;
		string zahl2;

		// Listen zum Abspeichern aller Zahlen:
		vector<string> liste1;
		vector<string> liste2;

		// Liste zum Abspeichern der Oberbegriffe:
		vector<string> legende;
		bool legendevorbei = false;

		while(file_output1 >> zahl1)  //Zahlenweise durchgehen
		{
			// string zu char umwandeln
			int n = zahl1.length();
			char char_array[n + 1];
			strcpy(char_array, zahl1.c_str());

			// Auf Zahl testen (alle Zahlen in den Vektor 'liste1' schreiben, die Oberbegriffe in 'legende') //strchr(char_array, '4')->Sonderfall!
			if (strchr(char_array, '0')|| strchr(char_array, '1')|| strchr(char_array, '2')|| strchr(char_array, '3')||  strchr(char_array, '5')|| strchr(char_array, '6')|| strchr(char_array, '7')|| strchr(char_array, '8')|| strchr(char_array, '9')){
				liste1.push_back(char_array);
				legendevorbei = true;
			}
			else if (!legendevorbei){

				legende.push_back(char_array);
			}
			//Fehler abfangen: kein Wert als Eingabe
			else{
				return "falscher Wert in 1.Outflow-Datei (keine Zahl)";
			}
		}

		bool legendevorbei2 = false;
		while(file_output2 >> zahl2)  //Zahlenweise durchgehen
		{
			// string zu char umwandeln
			int n = zahl2.length();
			char char_array2[n + 1];
			strcpy(char_array2, zahl2.c_str());

			// Auf Zahl testen (alle Zahlen in den Vektor 'liste2' schreiben)//strchr(char_array2, '4')||
			if (strchr(char_array2, '0')|| strchr(char_array2, '1')|| strchr(char_array2, '2')|| strchr(char_array2, '3')|| strchr(char_array2, '5')|| strchr(char_array2, '6')|| strchr(char_array2, '7')|| strchr(char_array2, '8')|| strchr(char_array2, '9')){
				liste2.push_back(char_array2);
				legendevorbei2 = true;
			}
			//Fehler abfangen: kein Wert als Eingabe
			else if (legendevorbei2 == true){
				return "falscher Wert in 2.Outflow-Datei (keine Zahl)";
			}
		}

		// Eingaben für "data" sammeln und zählen:
		int z = 0; //Spaltenanzahl für data
		int k = 0;
		int l = 0;
		vector<string> data_eingabe;

		bool endung = false;
		while (k<legende.size()){
			string test1 = legende[k];
			if (test1.find("]")!= std::string::npos){
				z = z + 1;
				k = k + 1;
				while(k<legende.size()){
					string test2 = legende[k];
					if (test2.find("]")!= std::string::npos){ //dritte Spalte gefunden
						z = z + 1;
						k = k + 1;
						while(k<legende.size()){
							string test3 = legende[k];
							if (test3.find("]")!= std::string::npos){ //Einheiten nicht übernehmen
								z = z + 1;
								k = k + 1;
								data_eingabe.push_back("0"); //Trennzeichen
								endung = true; //Danach dürfen die Zahlen beginnen
							}
							else{
								data_eingabe.push_back(test3);
								k = k + 1;
								endung = false;
							}
						}
					}
					else{
						k = k+1;
					}
				}
			}
			else {
				k = k+1;
			}
		}
		
		if (endung == 0){
			return "Einheiten-Angabe fehlt in Eingabedatei";
		}

		int intervall = y-x; //Endwert-Startwert
		vector<long double> timestamps;
		for (int ti=x+1;ti<=intervall;ti=ti+timestep){
			timestamps.push_back(ti);
		}
		timestamps.push_back(y); //letzten Zeitpunkt einbeziehen
		int time_size = timestamps.size();
		string liste[time_size*z]; //zum Abspeichern der integrierten Werte
		for (int li=0;li<(time_size*z);++li){ //Werte auf 0 setzen
			liste[li] = '0';
		}

		vector<string> time1; //zum Abspeichern der Zeitintervalle (Time[h])
		// erste Liste: Zeitstempel rausschreiben
		int i1 = 0; //Elementangabe
		int k11 = -1; //Zeilenangabe
		int l1 = 0; //Spaltenangabe

		long double wert2 = 0;
		long double wert4 = 0;
		long double wert4_b = 0;

		for (int times1=0;times1<time_size;++times1){
			int aktueller_timestamp1 = timestamps[times1]; //aktueller Stoppwert (bis zu dem integriert wird)
			liste[times1*z] = to_string(aktueller_timestamp1); //Zeitwerte setzen mit timestamps
			int i1 = 0; //Elementangabe
			int k1 = -1; //Zeilenangabe
			int l1 = 0; //Spaltenangabe
			long double wert1 = 0; //aktueller Zeitwert

			while ((i1<liste1.size()) && (wert1 <aktueller_timestamp1)){ //solange noch Elemente vorhanden sind und der aktuelle Zeitstempel nicht erreicht wurde
				for (int j1=0; j1<z; ++j1){
					if (i1!=0 && j1==0){ //jeder Zeitpunkt (außer erster)
						
						string neu1 = liste1[i1]; //aktueller Zeitwert
						//long double wert1;
						wert1 = dot_conversion_regex(neu1);

						string neu2 = liste1[i1-z]; //vorheriger Zeitwert
						//long double wert2;
						wert2 = dot_conversion_regex(neu2);

						//Fehlerbehandlung: Zeit wird wieder kleiner
						if (wert1<wert2){
							return "Zeitangabe in 1.Outflow-Datei falsch: Zeit darf nicht kleiner werden";
						}
						if (wert1<=aktueller_timestamp1){
							string neu22 = to_string(wert1-wert2); //Zeitintervall
							time1.push_back(neu22);
						}
						else{
							string neu22_i = to_string(aktueller_timestamp1-wert2); //Zeitintervall bei Zwischenzeitwert
							time1.push_back(neu22_i);
						}
						k1 = k1 + 1;
						k11 = k11 + 1;
						l1 = l1 + 1;
					}
					else if (i1==0){ //erster Zeitpunkt
						/*//cout << liste1[j1] << endl;
						string Startwert1 = liste1[j1];
						stringstream sw1;
						long double Start1;
						sw1<<Startwert1;
						sw1>>Start1;
						double startwert1 = Start1 - 0.2;
						time1.push_back(to_string(Start1-startwert1)); //Zeitpunkt "t1-Anfangswert"*/
						time1.push_back(to_string(0.2)); //Zeitpunkt "t1-Anfangswert"
						k1 = k1 + 1;
						k11 = k11 + 1;
					}
					//////////////////////////////////////////////////////////////////////
					else if (i1<z){ //erste Zeile mit Werten
					}
					else {
						string neu3 = time1[k11]; //Zeitintervall
						long double wert3 = dot_conversion_regex(neu3);
						
						if (wert1<=aktueller_timestamp1 && (k1*z+j1<liste1.size())){

							string neu4 = liste1[(k1)*z+j1]; //aktueller Wert
							//long double wert4;
							wert4 = dot_conversion_regex(neu4);

							string neu4_b = liste1[(k1)*z+j1-z]; //letzter Wert
							//long double wert4_b;
							wert4_b = dot_conversion_regex(neu4_b);
						}
						else if (wert1>aktueller_timestamp1 && (k1*z+j1<liste1.size())) { //lineare Interpolation

							string neu4_g = liste1[k1*z+j1]; //aktueller Wert
							long double wert4_g = dot_conversion_regex(neu4_g);

							string neu4_b = liste1[k1*z+j1-z]; //letzter Wert
							long double wert4_b = dot_conversion_regex(neu4_b);

							long double wert4 = ((wert4_g - wert4_b)/(wert1-wert2))*(aktueller_timestamp1-wert2);

						}

						string neu5 = liste[times1*z + (l1%z)]; //alten Wert zum Verrechnen abspeichern
						long double wert5 = dot_conversion_regex(neu5);					
						liste[times1*z + (l1%z)] = to_string(wert3*(0.5)*(wert4+wert4_b) + wert5); //Zeitintervall*Mittelwert + alter_Wert
						l1 = l1 + 1;
					}
					i1 = i1+1;
				}
			}
		}

		// analog für die 2.outflow-Datei
		vector<string> time2; //zum Abspeichern der Zeitpunkte (Time[h])
		// erste Liste: Zeitstempel rausschreiben
		int i2 = 0;
		int k22 = -1;
		int l2 = 0;

		long double wert2_2 = 0;
		long double wert4_2 = 0;
		long double wert4_2_b = 0;

		for (int times2=0;times2<time_size;++times2){
			int aktueller_timestamp2 = timestamps[times2]; //aktueller Stoppwert (bis zu dem integriert wird)
			liste[times2*z] = to_string(aktueller_timestamp2); //Zeitwerte setzen mit timestamps
			int i2 = 0; //Elementangabe
			int k2 = -1; //Zeilenangabe
			int l2 = 0; //Spaltenangabe
			long double wert1_2 = 0; //aktueller Zeitwert
			while (i2<liste2.size()&& (wert1_2 <aktueller_timestamp2)){
				for (int j2=0; j2<z; ++j2){
					if ((i2!=0 && j2==0)){ //jeder Zeitpunkt (außer erster)

						string neu1_2 = liste2[i2]; //aktueller Zeitwert
						//long double wert1_2;
						wert1_2 = dot_conversion_regex(neu1_2);

						string neu2_2 = liste2[i2-z]; //vorheriger Zeitwert
						//long double wert2_2;
						wert2_2 = dot_conversion_regex(neu2_2);

						//Fehlerbehandlung: Zeit wird wieder kleiner
						if (wert1_2<wert2_2){
							return "Zeitangabe in 2.Outflow-Datei falsch: Zeit darf nicht kleiner werden";
						}
						if (wert1_2<=aktueller_timestamp2){
							string neu22_2 = to_string(wert1_2-wert2_2); //Zeitintervall
							time2.push_back(neu22_2);
						}
						else{
							string neu22_i_2 = to_string(aktueller_timestamp2-wert2_2); //Zeitintervall bei Zwischenzeitwert
							time2.push_back(neu22_i_2);
						}
						k2 = k2 + 1;
						k22 = k22 + 1;
						l2 = l2 + 1;
						//liste[0] = liste2[i2]; //letzter Zeitpunkt
					}
					else if (i2==0){ //erster Zeitpunkt
						//time2.push_back(liste2[j2]); //Zeitpunkt "t1-0"
						time2.push_back(to_string(0.2)); //Zeitpunkt "t1-Startwert"
						k2 = k2 + 1;
						k22 = k22 + 1;
					}
					else if (i2<z){ //erste Zeile

					}
					else {
						string neu3_2 = time2[k22]; //Zeitintervall
						long double wert3_2 = dot_conversion_regex(neu3_2);

						if (wert1_2<=aktueller_timestamp2 && (k2*z+j2<liste2.size())){

							string neu4_2 = liste2[k2*z+j2]; //aktueller Wert
							//long double wert4_2;
							wert4_2 = dot_conversion_regex(neu4_2);

							string neu4_2_b = liste1[k2*z+j2-z]; //letzter Wert
							//long double wert4_2_b;
							wert4_2_b = dot_conversion_regex(neu4_2_b);

						}
						else if (wert1_2>aktueller_timestamp2 && k2*z+j2<liste2.size()){ //lineare Interpolation

							string neu4_g_2 = liste1[k2*z+j2]; //aktueller Wert
							long double wert4_g_2 = dot_conversion_regex(neu4_g_2);

							string neu4_2_b = liste2[k2*z+j2-z]; //letzter Wert
							long double wert4_2_b = dot_conversion_regex(neu4_2_b);

							long double wert4_2 = ((wert4_g_2 - wert4_2_b)/(wert1_2-wert2_2))*(aktueller_timestamp2-wert2_2);
						}

						string neu5_2 = liste[times2*z + (l2%z)]; //alten Wert zum Verrechnen abspeichern
						long double wert5_2 = dot_conversion_regex(neu5_2);
						liste[times2*z + (l2%z)] = to_string(0.5*wert3_2*(wert4_2+wert4_2_b) + wert5_2); //Zeitintervall*Wert
						l2 = l2 + 1;
					}
					i2 = i2+1;
				}
			}
		}

		//integrierte und addierte Werte in neue txt-Datei schreiben/Datei überschreiben
		gesamtdatei_string = "";
		
		//Legende in gesamtdatei schreiben:
		for (int i=0;i<legende.size();++i){
			gesamtdatei_string += legende[i] + " ";
		}
		gesamtdatei_string += " \n";

		//Werte in gesamtdatei schreiben:
		if (y>1){
			for (int i2=0;i2<time_size*z;++i2){
				if (i2%z==z-1){
					gesamtdatei_string += liste[i2] + "\n";
				}
				else{
					gesamtdatei_string += liste[i2] + "\t";
				}
			}
		}
		else{
			for (int i2=0;i2<z;++i2){
				if (i2%z==z-1){
					gesamtdatei_string += liste[i2] + "\n";
				}
				else{
					gesamtdatei_string += liste[i2] + "\t";
				}
			}
		}
		boost::replace_all(gesamtdatei_string, ",", ".");
	}
    
    /*-------------------------------------------------------------------------*/
    // Sonderfall: ein Reaktor (Aufintegrieren von einer Datei)
    if (anzahl == 1){
        //Datei lesen (nur eine Inputdatei):       
        getline(output,file_name);
        file_stream_output1 = ifstream(file_name);
		std::string output1_content((std::istreambuf_iterator<char>(file_stream_output1)),
				(std::istreambuf_iterator<char>()));
        file_output1 = istringstream(output1_content);           

		
        // auf Existenz prüfen:
        if(!file_stream_output1){
            return "File not found (output1).";
        }
		
        string zahl1;
        string zahl2;

        // Listen zum Abspeichern aller Zahlen:
        vector<string> liste1;
        vector<string> liste2;

        // Liste zum Abspeichern der Oberbegriffe:
        vector<string> legende;
        bool legendevorbei = false;

        while(file_output1 >> zahl1){ //Zahlenweise durchgehen
            // string zu char umwandeln
            int n = zahl1.length();
            char char_array[n + 1];
            strcpy(char_array, zahl1.c_str());

            // Auf Zahl testen (alle Zahlen in den Vektor 'liste1' schreiben, die Oberbegriffe in 'legende') //strchr(char_array, '4')->Sonderfall!
            if (strchr(char_array, '0')|| strchr(char_array, '1')|| strchr(char_array, '2')|| strchr(char_array, '3')||  strchr(char_array, '5')|| strchr(char_array, '6')|| strchr(char_array, '7')|| strchr(char_array, '8')|| strchr(char_array, '9')){
                liste1.push_back(char_array);
                legendevorbei = true;
            }
            else if (!legendevorbei){
                legende.push_back(char_array);
            }
            //Fehler abfangen: kein Wert als Eingabe
            else{
                return "falscher Wert in Outflow-Datei (keine Zahl)";
            }
        }

        // Eingaben für "data" sammeln und zählen:
        int z = 0; //Spaltenanzahl für data
        int k = 0;
        int l = 0;
        vector<string> data_eingabe;

        bool endung = false;
        while (k<legende.size()){
            string test1 = legende[k];
            if (test1.find("]")!= std::string::npos){
                z = z + 1;
                k = k + 1;
                while(k<legende.size()){
                    string test2 = legende[k];
                    if (test2.find("]")!= std::string::npos){ //dritte Spalte gefunden
                        z = z + 1;
                        k = k + 1;
                        while(k<legende.size()){
                            string test3 = legende[k];
                            if (test3.find("]")!= std::string::npos){ //Einheiten nicht übernehmen
                                z = z + 1;
                                k = k + 1;
                                data_eingabe.push_back("0"); //Trennzeichen
                                endung = true; //Danach dürfen die Zahlen beginnen
                            }
                            else{
                                data_eingabe.push_back(test3);
                                k = k + 1;
                                endung = false;
                            }
                        }
                    }
                    else{
                        k = k+1;
                    }
                }
            }
            else {
                k = k+1;
            }
        }

        if (endung == 0){
            return "Einheiten-Angabe fehlt in Eingabedatei";
        }

        int intervall = y-x; //Endwert-Startwert
        vector<long double> timestamps;
        for (int ti=x+1;ti<=intervall;ti=ti+5){ //TODO: Variable Schrittweite?
            timestamps.push_back(ti);
        }
        timestamps.push_back(y); //letzten Zeitpunkt einbeziehen
        int time_size = timestamps.size();
        string liste[time_size*z]; //zum Abspeichern der integrierten Werte
        for (int li=0;li<(time_size*z);++li){ //Werte auf 0 setzen
            liste[li] = '0';
        }

        vector<string> time1; //zum Abspeichern der Zeitintervalle (Time[h])
        // erste Liste: Zeitstempel rausschreiben
        int i1 = 0; //Elementangabe
        int k11 = -1; //Zeilenangabe
        int l1 = 0; //Spaltenangabe

        long double wert2 = 0;
        long double wert4 = 0;
        long double wert4_b = 0;

        for (int times1=0;times1<time_size;++times1){
            int aktueller_timestamp1 = timestamps[times1]; //aktueller Stoppwert (bis zu dem integriert wird)
            liste[times1*z] = to_string(aktueller_timestamp1); //Zeitwerte setzen mit timestamps
            int i1 = 0; //Elementangabe
            int k1 = -1; //Zeilenangabe
            int l1 = 0; //Spaltenangabe
            long double wert1 = 0; //aktueller Zeitwert

            while ((i1<liste1.size()) && (wert1 <aktueller_timestamp1)){ //solange noch Elemente vorhanden sind und der aktuelle Zeitstempel nicht erreicht wurde
                for (int j1=0; j1<z; ++j1){
                    if (i1!=0 && j1==0){ //jeder Zeitpunkt (außer erster)

                        string neu1 = liste1[i1]; //aktueller Zeitwert
                        //long double wert1;
                        wert1 = dot_conversion_regex(neu1);
                        
                        string neu2 = liste1[i1-z]; //vorheriger Zeitwert
                        //long double wert2;
                        wert2 = dot_conversion_regex(neu2);
                        
                        //Fehlerbehandlung: Zeit wird wieder kleiner
                        if (wert1<wert2){
                            return "Zeitangabe in 1.Outflow-Datei falsch: Zeit darf nicht kleiner werden";
                        }
                        if (wert1<=aktueller_timestamp1){
                            string neu22 = to_string(wert1-wert2); //Zeitintervall
                            time1.push_back(neu22);
                        }
                        else{
                            string neu22_i = to_string(aktueller_timestamp1-wert2); //Zeitintervall bei Zwischenzeitwert
                            time1.push_back(neu22_i);
                        }
                        k1 = k1 + 1;
                        k11 = k11 + 1;
                        l1 = l1 + 1;
                    }
                    else if (i1==0){ //erster Zeitpunkt
                        time1.push_back(to_string(0.2)); //Zeitpunkt "t1-Anfangswert"
                        k1 = k1 + 1;
                        k11 = k11 + 1;
                    }
                    //////////////////////////////////////////////////////////////////////
                    else if (i1<z){ //erste Zeile mit Werten
                    }
                    else {
						
                        string neu3 = time1[k11]; //Zeitintervall
                        long double wert3 = dot_conversion_regex(neu3);
                        
                        if (wert1<=aktueller_timestamp1 && (k1*z+j1<liste1.size())){

                            string neu4 = liste1[(k1)*z+j1]; //aktueller Wert
                            //long double wert4;
							wert4 = dot_conversion_regex(neu4);
							
                            string neu4_b = liste1[(k1)*z+j1-z]; //letzter Wert
                            //long double wert4_b;
                            wert4_b = dot_conversion_regex(neu4_b);
                        }
                        else if (wert1>aktueller_timestamp1 && (k1*z+j1<liste1.size())) { //lineare Interpolation

                            string neu4_g = liste1[k1*z+j1]; //aktueller Wert
                            long double wert4_g = dot_conversion_regex(neu4_g);

                            string neu4_b = liste1[k1*z+j1-z]; //letzter Wert
                            long double wert4_b = dot_conversion_regex(neu4_b);

                            long double wert4 = ((wert4_g - wert4_b)/(wert1-wert2))*(aktueller_timestamp1-wert2);

                        }

                        string neu5 = liste[times1*z + (l1%z)]; //alten Wert zum Verrechnen abspeichern
                        long double wert5 = dot_conversion_regex(neu5);
                        
                        liste[times1*z + (l1%z)] = to_string(wert3*(0.5)*(wert4+wert4_b) + wert5); //Zeitintervall*Mittelwert + alter_Wert
                        l1 = l1 + 1;
                    }
                    i1 = i1+1;
                }
            }
        }

        //integrierte und addierte Werte in neue txt-Datei schreiben/Datei überschreiben:
		gesamtdatei_string = "";
		
        //Legende in gesamtdatei schreiben:
        for (int i=0;i<legende.size();++i){
            gesamtdatei_string += legende[i] + "\t";
        }
		gesamtdatei_string += "\n";
		
        //Werte in gesamtdatei schreiben:
        if (y>1){
            for (int i2=0;i2<time_size*z;++i2){
                if (i2%z==z-1){
                    gesamtdatei_string += liste[i2] + "\n";
                }
                else{
                    gesamtdatei_string += liste[i2] + "\t";
                }
            }
        }
        else{
            for (int i2=0;i2<z;++i2){
                if (i2%z==z-1){
                    gesamtdatei_string += liste[i2] + "\n";
                }
                else{
                    gesamtdatei_string += liste[i2] + "\t";
                }
            }
        }
        
        boost::replace_all(gesamtdatei_string, ",", ".");
    }
    
    return gesamtdatei_string;
}
