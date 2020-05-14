package TestRail;


import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.testng.ITestResult;
import TestProperties.TestRunSettings;

import java.io.IOException;
import java.net.MalformedURLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;


public class TestRail  {
	private static Properties config = null;
    public TestRail(){
    }
    public  Properties getConfig() {
		return config;
	}
    public void SendResult(String testRunID, ITestResult result){
        if (!TestRunSettings.IsLoggingOn())
        {
            //TODO Add logging to json locally
            System.out.println(testRunID);
            System.out.print("\nINFO: TESTRAIL LOGGING IS OFF\n");
        }
        else{
            SendResultToTestRail(testRunID, result);
        }
    }

    public String getRunID(String testName, String s){

        LocalDateTime currentTime = LocalDateTime.now();

        String testRunID="";
        if (TestRunSettings.IsLoggingOn()){
            testRunID=TestRunSettings.GetTestRunID();
            if(TestRunSettings.AddNewRun().equals("true")){
                APIClient testrailAPI = new APIClient(TestRunSettings.GetTestRailServer());
                testrailAPI.setUser(TestRunSettings.GetTestRailUser());
                testrailAPI.setPassword(TestRunSettings.GetTestRailAPIKey());

                Map runDetails = new HashMap();
                runDetails.put("suite_id", TestRunSettings.GetTestCases());
                runDetails.put("name", TestRunSettings.GetTestRunName() + " : " + currentTime.getDayOfMonth()+" "+ currentTime.getMonth() +" "+ currentTime.getYear());
                runDetails.put("description", "URL: "  + TestRunSettings.GetUrl() + " And " + TestRunSettings.GetTestRunDescription());
                runDetails.put("milestone_id", TestRunSettings.GetMileston());
                runDetails.put("include_all", false);
                runDetails.put("case_ids", getTestCaseIDsofallSections());

                try {
                    JSONObject r = (JSONObject) testrailAPI.sendPost("add_run/"+TestRunSettings.GetProjectID(), runDetails);
                    testRunID = r.get("id").toString();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (APIException e) {
                    e.printStackTrace();
                }
            }
        }
        return testRunID;
    }

    public void SendResultToTestRail(String testRunID, ITestResult result) {
        APIClient testrailAPI = new APIClient(TestRunSettings.GetTestRailServer());
        testrailAPI.setUser(TestRunSettings.GetTestRailUser());
        testrailAPI.setPassword(TestRunSettings.GetTestRailAPIKey());


        int statusID = getResultStatus(result.getStatus());

        String comments = "";
        if (result.getAttribute("steplog") != null) {
            comments = result.getAttribute("steplog").toString().replace("<br>", "\n");
        }
        if (result.getThrowable() != null) {
            comments = comments + "\n" + "Stack trace: " + result.getThrowable().getMessage();
        }


        Map resultDetails = new HashMap();
        resultDetails.put("status_id", new Integer(statusID));
        resultDetails.put("comment", comments);
       
        String[] testIDs =  result.getMethod().getDescription().replaceAll("\\s+","").split(",");
        for (String id : testIDs) {
            //remove the 'c'
            String testID = id.replace("C", "");

            System.out.print("OUTPUT -add_result_for_case/" + testRunID + "/" + testID + "|");

            try {
                testrailAPI.sendPost("add_result_for_case/" + testRunID + "/" + testID, resultDetails);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            }
        }
    }

    public int getResultStatus(int status) {
        int resultStatus;
        switch(status){
            case 1 :
                resultStatus = 1;
                break;
            case 2 :
                resultStatus = 5;
                break;
            default :
                resultStatus = 4;
        }
        return resultStatus;
    }
    
	public static List<String> getAllTestIDs(String sectionId){
		   List<String> testIDs = new ArrayList();
		   APIClient testrailAPI = new APIClient(TestRunSettings.GetTestRailServer());
	       testrailAPI.setUser(TestRunSettings.GetTestRailUser());
	       testrailAPI.setPassword(TestRunSettings.GetTestRailAPIKey());
	       try {
	    	   JSONArray r = (JSONArray) testrailAPI.sendGet("get_cases/"+TestRunSettings.GetProjectID()+"&suite_id="+TestRunSettings.GetTestCases()+"&section_id="+sectionId);
	    	   //r.get("id").toString();
	    	   for (int i=0; i<r.size(); i++) {
	             JSONObject k = (JSONObject) r.get(i);
	             String testid = k.get("id").toString();
	             testIDs.add(testid);
	    	   }	    	  
	   		} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (APIException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
		   return testIDs;
	   }
	
	public static List<String> getTestCaseIDsofallSections(){
		String[] allSections;
		List<String> alltestCaseIDs = new ArrayList();
			allSections =TestRunSettings.GetSections().split(",");						
		for(int i=0; i<allSections.length;i++){
			alltestCaseIDs.addAll(getAllTestIDs(allSections[i]));
		}
	return alltestCaseIDs;
	}
	
	// This method will update the result based on the TestID sent as parameter
	public void sendSpecificResultToTestRail(String testRunID, int statusID, String testIDs) {
        APIClient testrailAPI = new APIClient(TestRunSettings.GetTestRailServer());
        testrailAPI.setUser(TestRunSettings.GetTestRailUser());
        testrailAPI.setPassword(TestRunSettings.GetTestRailAPIKey());


       // int statusID = getResultStatus(result.getStatus());

        String comments = "";
      /*  if (result.getAttribute("steplog") != null) {
            comments = result.getAttribute("steplog").toString().replace("<br>", "\n");
        }
        if (result.getThrowable() != null) {
            comments = comments + "\n" + "Stack trace: " + result.getThrowable().getMessage();
        }*/


        Map resultDetails = new HashMap();
        resultDetails.put("status_id", new Integer(statusID));
        resultDetails.put("comment", comments);
       
            //remove the 'c'
            String testID = testIDs.replace("C", "");

            System.out.print("OUTPUT -add_result_for_case/" + testRunID + "/" + testID + "|");

            try {
                testrailAPI.sendPost("add_result_for_case/" + testRunID + "/" + testID, resultDetails);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (APIException e) {
                e.printStackTrace();
            }        
    }
}
