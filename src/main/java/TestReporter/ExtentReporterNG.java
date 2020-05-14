package TestReporter;
import com.relevantcodes.extentreports.ExtentReports;
import com.relevantcodes.extentreports.ExtentTest;
import com.relevantcodes.extentreports.LogStatus;

import org.testng.*;
import org.testng.xml.XmlSuite;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class ExtentReporterNG implements IReporter {
    private ExtentReports extent;



    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {

        for (ISuite suite : suites) {
            extent = new ExtentReports("test-output/" + suite.getName() + ".html", false);
            String ConfigDIR = "src/main/java/com/highwire/cochrane/testreporter/";
            extent.loadConfig(new File(ConfigDIR + "extent-config.xml"));
            Map<String, ISuiteResult> result = suite.getResults();

            for (ISuiteResult r : result.values()) {
                ITestContext context = r.getTestContext();
                buildTestNodes(context.getFailedTests(), LogStatus.FAIL, context.getName());
                buildTestNodes(context.getSkippedTests(), LogStatus.SKIP, context.getName());
                buildTestNodes(context.getPassedTests(), LogStatus.PASS, context.getName());
            }
        }
        extent.flush();
        extent.close();
    }


    private void buildTestNodes(IResultMap tests, LogStatus status, String category) {
        ExtentTest test;

        if (tests.size() > 0) {
            for (ITestResult result : tests.getAllResults()) {

                test = extent.startTest(result.getMethod().getMethodName());
                String currentName = test.getTest().getName()+"; "+ test.getTest().getDescription();
                test.getTest().setName(currentName + "(" + "" + ")" );
                test.getTest().setStartedTime(getTime(result.getStartMillis()));
                test.getTest().setEndedTime(getTime(result.getEndMillis()));
                test.assignCategory(category);


                String message = "Test " + status.toString().toLowerCase() + "ed";

                message = message + "<br>" + result.getAttribute("steplog");

                if (result.getThrowable() != null)
                    message = message + result.getThrowable().getMessage();
                test.log(status, message);
                extent.endTest(test);
            }
        }
    }


    private Date getTime(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar.getTime();
    }

}