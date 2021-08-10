package vrl.biogas.biogascontrol.outputloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reader for a biogas output file
 * @author Paul Zügel
 */
public class CSVReader {

	static private List<List<Double>> data;
	static private int colCount;
	static private int rowCount;
	
	public CSVReader(File path) throws FileNotFoundException {
		readFile(path);
	}
	
	private void readFile(File path) throws FileNotFoundException {
		data = new ArrayList<List<Double>>();
		Scanner scanner = new Scanner(path);
		Pattern p = Pattern.compile("(^#)|(^[a-zA-Z])");
		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			Matcher m = p.matcher(line);
			if(!m.find())
		        data.add(readRow(line));
		}
		scanner.close();
		
		rowCount = data.size();
		colCount = data.get(0).size();
	}
	
	static private List<Double> readRow(String line) {
		List<Double> values = new ArrayList<Double>();
	    Scanner rowScanner = new Scanner(line);
	    rowScanner.useDelimiter("\\s+");
		    
	    while (rowScanner.hasNext()) {
	    	values.add(Double.parseDouble(rowScanner.next()));
	    }
	    rowScanner.close();	
	    return values;
	}
	
	public List<Double> getCol(int column) {
		List<Double> col = new ArrayList<Double>();
		for(List<Double> l : data) {
			col.add(l.get(column));
		}
		return col;
	}
	
	public int getColumnSize() {
		return colCount;
	}
	
	public int getRowSize() {
		return rowCount;
	}
	

}
