package TestProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class TestRunSettings {
  
	public static String GetUrl(){
		 if (System.getProperty("URL") == null) {
			 return GetProperty("URL");
	      }
	        return System.getProperty("URL");	
	}
    public static String GetTestRailServer(){
        return GetProperty("serverURL");
    }
    public static String GetTestRailAPIKey(){
        return GetProperty("APIKey");
    }
    public static String GetTestRailUser(){
        return GetProperty("username");
    }
    public static String GetTestRunID() { return GetProperty("testRunID"); }

    public static Boolean IsLoggingOn() { return Boolean.parseBoolean(GetProperty("logResultsToTR")); }

   // public static String GetTestSites() { return GetProperty("sitesToTest"); }

    public static int GetGlobalTimeout() { return Integer.parseInt(GetProperty("GlobalTimeout")); }

    public static String AddNewRun() { return GetProperty("addNewRun"); }
    public static String GetProjectID() { return GetProperty("projectID"); }
    public static String GetTestCases() { return GetProperty("testCases"); }
    public static String GetTestRunName() { return GetProperty("testRunName"); }
    public static String GetTestRunDescription() { return GetProperty("testRunDescription"); }
    public static String GetMileston() { return GetProperty("mileston"); }
   // public static String GetLdapUsername() { return GetProperty("ldapUserName"); }
   // public static String GetLdapPassword() { return GetProperty("ldapPassword"); }
   // public static String GetEmail() { return GetProperty("email"); }
    public static String GetSections() { return GetProperty("sections");}
    public static String GetBrowser(){ return GetProperty("BROWSER");}
    
    
    
    public static String GetProperty(String propertyName) {
        Properties testsettings = new Properties();
        InputStream input = null;

        try {
        	/*String OS = System.getProperty("os.name");
        	String PATH = null;
        	//= "src/main/java/TestProperties/Config.properties";
            if (OS.toLowerCase().contains("windows")) {
    			PATH = "\\src\\main\\java\\TestProperties\\Config.properties";
    		} else if (OS.toLowerCase().contains("mac")) {
    			PATH= "/src/main/java/TestProperties/Config.properties";
    		}
            input = new FileInputStream(PATH);*/
            
        	input = new FileInputStream("src/main/java/TestProperties/Config.properties");
            // load a properties file
            testsettings.load(input);

            // get the property value and print it out
            return testsettings.getProperty(propertyName);

        } catch (IOException ex) {
            ex.printStackTrace();
        } finally {
            if (input != null) {
                try {
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "TESTRUNPROPERTY '" + propertyName + "' DOES NOT EXIST";
    }
    // Returns the site to be used for testing

    public TestRunSettings() throws FileNotFoundException {

    }
}
