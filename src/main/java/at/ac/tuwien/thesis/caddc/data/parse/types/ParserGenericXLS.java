package at.ac.tuwien.thesis.caddc.data.parse.types;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import at.ac.tuwien.thesis.caddc.data.format.Resource;
import at.ac.tuwien.thesis.caddc.data.parse.Parser;
import at.ac.tuwien.thesis.caddc.data.parse.exception.ParseException;

/**
 * 
 */
public class ParserGenericXLS implements Parser {

	private int sheetNumber;
	private int rowOffset;
	private Integer[] colIndices;
	
	public ParserGenericXLS(int sheetNumber, int rowOffset, Integer[] colIndices) {
		this.sheetNumber = sheetNumber;
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
		POIFSFileSystem fs = null;
	    HSSFWorkbook wb = null;
		try {
			fs = new POIFSFileSystem(new FileInputStream(resource.getFile()));
		    wb = new HSSFWorkbook(fs);
		    HSSFSheet sheet = wb.getSheetAt(sheetNumber);
		    HSSFRow row;
		    HSSFCell cell;

		    int rows; // No of rows
		    rows = sheet.getPhysicalNumberOfRows();
		    
		    System.out.println("NR ROWS = "+rows);

		    int cols = 0; // No of columns
		    int tmp = 0;

		    // This trick ensures that we get the data properly even if it doesn't start from first few rows
		    for(int i = 0; i < 10 || i < rows; i++) {
		        row = sheet.getRow(i);
		        if(row != null) {
		            tmp = sheet.getRow(i).getPhysicalNumberOfCells();
		            if(tmp > cols) cols = tmp;
		        }
		    }
		    
		    System.out.println("rows = "+rows+", cols = "+cols);

		    List<String> priceList = new ArrayList<String>();
		    for(int r = rowOffset; r < rows; r++) {
		        row = sheet.getRow(r);
		        StringBuilder rowData = new StringBuilder();
		        if(row != null) {
		        	// go through column indices given by parameter
		            for(int c : colIndices) {
		                cell = row.getCell(c);
		                if(cell != null) {
		                	rowData.append(cell);
		                	rowData.append(";");
		                }
		            }
		        }
		        priceList.add(rowData.toString());
		    }
		    wb.close();
		    return priceList;
		} catch(IOException ioe) {
		    throw new ParseException("IOException: "+ioe.getLocalizedMessage());
		} finally {
			try {
				wb.close();
			} catch (IOException e) {
				System.err.println("XLSParserGeneric: Could not close workbook");
			}
		}
	}
}
