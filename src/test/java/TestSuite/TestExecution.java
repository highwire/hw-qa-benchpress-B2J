package TestSuite;

import java.util.ArrayList;

import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import PageObjects.TestMethods;

public class TestExecution extends BasePageTest {

	private TestMethods TestMethodsObj;
	boolean result = false;
	
	@BeforeMethod
    public void setup() {
		TestMethodsObj = new TestMethods();
    }

    @AfterMethod
    public void tearDown() {
    	if(TestMethodsObj.getDriver()!=null){
    		TestMethodsObj.getDriver().quit();			
    		TestMethodsObj.getBrowserDriver().close();
		}
    }

    @Test
    public void testSubmitAManuscript() {
    	 ArrayList<Boolean> result = new ArrayList<Boolean>();
    	 result.add(TestMethodsObj.Login());
         String ManuScriptId = TestMethodsObj.submitAJournal();
         Assert.assertTrue(ManuScriptId.length()!=0);
         result.add(TestMethodsObj.verifyJournalForSelectedManuscript(ManuScriptId));
         result.add(TestMethodsObj.changeUserRoles(ManuScriptId));
         result.add(TestMethodsObj.exportManuscriptReport());
         if(TestMethodsObj.getTextfromLatestDownloads().length()>0){
        	 result.add(true);
         }
         System.out.println(result);
         if(result.contains(false)){
          	 Assert.assertFalse(true);
           } else {
           	System.out.println("The test is successful");
           }
     }
}