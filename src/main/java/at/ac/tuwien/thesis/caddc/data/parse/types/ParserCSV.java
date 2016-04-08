package at.ac.tuwien.thesis.caddc.data.parse.types;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.opencsv.CSVReader;

import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.Parser;
import at.ac.tuwien.thesis.caddc.data.parse.exception.ParseException;

/**
 * Class to fetch an XLS file containing real time (RT) 
 * prices from energy Market ISO-NE, location Maine
 */
public class ParserCSV implements Parser {
	
	private int rowOffset;
	private Integer[] colIndices;
	
	/**
	 * Get default values
	 */
	public ParserCSV(int rowOffset, Integer[] colIndices) {
		this.rowOffset = rowOffset;
		this.colIndices = colIndices;
	}

	/**
	 * Method to parse the prices arranged in a XLS file
	 * @param resource the resource to parse
	 * @see at.ac.tuwien.thesis.caddc.data.parse.Parser#parse()
	 */
	@Override
	public List<String> parse(Resource resource) throws ParseException {
		
		try {
			List<String> rowData = new ArrayList<String>();
			CSVReader reader = new CSVReader(new FileReader(resource.getFile()));
			
			String [] nextLine;
			int count = 0;
		    while ((nextLine = reader.readNext()) != null) {
		    	if(count++ < rowOffset) {
		    		continue;
		    	}
		    	
		    	StringBuilder builder = new StringBuilder();
		    	for(Integer idx : colIndices) {
		    		if(idx < nextLine.length && !nextLine[idx].trim().isEmpty()) {
		    			builder.append(nextLine[idx]);
		    			builder.append(Parser.SEPARATOR);
		    		}
		    	}
		        rowData.add(builder.toString());
		    }
		    return rowData;
		} catch (IOException e) {
			throw new ParseException(getClass().getSimpleName() + ": CSV file "+resource.getFile().getName()+" could not be parsed");
		}
	}
	
}
