package TestRunner;
import javax.swing.JTextPane;

import org.testng.Reporter;
public class TestReporter {
	public static void logTestStep(String stepDetails){
        Reporter.log(stepDetails, true);
        if (Reporter.getCurrentTestResult().getAttribute("steplog") != null) {
            String currentLog = Reporter.getCurrentTestResult().getAttribute("steplog").toString();
            Reporter.getCurrentTestResult().setAttribute("steplog", currentLog + "<br>" +  stepDetails);
        } else {
            Reporter.getCurrentTestResult().setAttribute("steplog", stepDetails);
        }
    }
    public static String getTestSteps(){
        String currentLog = Reporter.getCurrentTestResult().getAttribute("steplog").toString();
        return currentLog;
    }
    
    public static void logTestStepFailure(String stepDetails){
    	Reporter.log(stepDetails, true);
        if (Reporter.getCurrentTestResult().getAttribute("steplog") != null) {
            String currentLog = Reporter.getCurrentTestResult().getAttribute("steplog").toString();
            Reporter.getCurrentTestResult().setAttribute("steplog", currentLog + "<br> <strong style='color: red;'>"+stepDetails+"</strong>");
        } else {
            Reporter.getCurrentTestResult().setAttribute("steplog", "c");
        }
    }
    
    public static void logTestStepSuccess(String stepDetails){
    	Reporter.log(stepDetails, true);
    	String htmlText = new String("<strong style='color: green'>"+stepDetails+"</strong>");
    	JTextPane jTextPane =new JTextPane ();
    	jTextPane.setContentType("text/html");
    	jTextPane.setText(htmlText);
        if (Reporter.getCurrentTestResult().getAttribute("steplog") != null) {
            String currentLog = Reporter.getCurrentTestResult().getAttribute("steplog").toString();          
            Reporter.getCurrentTestResult().setAttribute("steplog", currentLog + "<br>" +htmlText); 
            Reporter.getCurrentTestResult().setAttribute("steplog", currentLog + "<strong style='color: red;'>"+stepDetails+"</strong>"); 
        } else {
            Reporter.getCurrentTestResult().setAttribute("steplog", htmlText);
        }
    }
}
