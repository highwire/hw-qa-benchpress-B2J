package TestSuite;

import java.io.IOException;
import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeTest;
import TestProperties.TestRunSettings;
import TestRail.TestRail;

public class BasePageTest {

	public WebDriver driver;
	public String testRunID;
    @BeforeTest
    public void createTestRailRun(ITestContext ctx) {
        TestRail testRail = new TestRail();
        String testName= ctx.getName();
        testRunID = testRail.getRunID(testName, TestRunSettings.GetUrl());
        ctx.setAttribute("testRunID", testRunID);
    }
    
    @AfterMethod
    public void afterMethod(ITestResult result, ITestContext ctx) throws IOException, InterruptedException {
        TestRail testRail = new TestRail();
        String runID=(String) ctx.getAttribute("testRunID");
        testRail.SendResult(runID, result);
    }
    
   /* public void updateTestRailStatus(ITestContext ctx) {
        TestRail testRail = new TestRail();
        String testName= ctx.getName();
        String testRunID = testRail.getRunID(testName, TestRunSettings.GetUrl());
        //String runID=(String) ctx.getAttribute(testRunID);
        //result = getResultStatus();
        testRail.SendResult(testRunID, testRail.getResultStatus(0));
    }*/
}