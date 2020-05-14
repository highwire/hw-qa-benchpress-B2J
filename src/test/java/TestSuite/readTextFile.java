package TestSuite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;

import TestUtils.TestUtil;

public class readTextFile {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		String home = System.getProperty("user.home");
		
		System.out.println(getTextfromFile(getTheNewestFile(home+"/Downloads/", "txt")));

	}
	
	
	public static File getTheNewestFile(String filePath, String ext) {
	    File theNewestFile = null;
	    File dir = new File(filePath);
	    FileFilter fileFilter = new WildcardFileFilter("*." + ext);
	    File[] files = dir.listFiles(fileFilter);

	    if (files.length > 0) {
	        /** The newest file comes first **/
	        Arrays.sort(files, LastModifiedFileComparator.LASTMODIFIED_REVERSE);
	        theNewestFile = files[0];
	    }

	    return theNewestFile;
	}
	
	public static String getTextfromFile(File fileName) throws IOException{
		String everything = "";
		try(BufferedReader br = new BufferedReader(new FileReader(fileName))) {
		    StringBuilder sb = new StringBuilder();
		    String line = br.readLine();

		    while (line != null) {
		        sb.append(line);
		        sb.append(System.lineSeparator());
		        line = br.readLine();
		    }
		    everything = sb.toString();		    
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
		return everything;
	}

}
