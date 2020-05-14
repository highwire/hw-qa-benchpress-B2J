package TestUtils;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.comparator.LastModifiedFileComparator;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jsoup.Jsoup;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import TestProperties.TestRunSettings;
import TestRunner.TestReporter;

public class TestUtil {
	private static final int MAX_RETRY_COUNT = 1;
	private WebDriver driver = null;
	//private static final Logger LOGGER = Logger.getLogger(TestUtil.class.getName());
	private static int invalidLinksCount = 0;
	private static int invalidImgCount = 0;
	private BrowserDriver browserDriver = null;
	private BrowserFactory browserFactory = null;
	//public Properties CONFIG_REPO = null;	
	public Properties OBJ_REPO = null;

	public TestUtil() {
		if (browserFactory == null) {
			browserFactory = new BrowserFactory();
			browserFactory.initConfig();
			OBJ_REPO = browserFactory.getOBJECT_REPOSITORY();
			if (driver == null) {
				browserDriver = new BrowserDriver();
				driver = browserDriver.getCurrentDriver();
				driver.get(browserFactory.getConfig().getProperty("URL"));
				driver.manage().window().maximize();
				TestReporter.logTestStep("Browser Opened: Site loaded");
			}
		}

	}

	public WebDriver getDriver() {
		return driver;
	}
	
	public BrowserFactory getBrowserFactory() {
		return browserFactory;
	}
	
	public BrowserDriver getBrowserDriver() {
		return browserDriver;
	}


	/**
	 * Accepts xpath as an argument and clicks on the relevant button
	 * 
	 * @param xpathKey
	 */

	public By getObjectLocator(String locatorName) {		
		String locatorProperty = OBJ_REPO.getProperty(locatorName);
		String locatorType = locatorProperty.split(":")[0];
		String locatorValue = locatorProperty.split(":")[1];

		By locator = null;
		switch (locatorType) {
		case "Id":
			locator = By.id(locatorValue);
			break;
		case "Name":
			locator = By.name(locatorValue);
			break;
		case "CssSelector":
			locator = By.cssSelector(locatorValue);
			break;
		case "LinkText":
			locator = By.linkText(locatorValue);
			break;
		case "PartialLinkText":
			locator = By.partialLinkText(locatorValue);
			break;
		case "TagName":
			locator = By.tagName(locatorValue);
			break;
		case "Xpath":
			locator = By.xpath(locatorValue);
			break;
		}
		return locator;
	}
	
	public By getDynamicObjectLocator(String locatorName, String value) {		
		String locatorProperty = OBJ_REPO.getProperty(locatorName);
		String locatorType = locatorProperty.split(":")[0];
		String locatorValue = locatorProperty.split(":")[1];
		locatorValue = locatorValue.replace("dynamicValuetoReplace", value);

		By locator = null;
		switch (locatorType) {
		case "Id":
			locator = By.id(locatorValue);
			break;
		case "Name":
			locator = By.name(locatorValue);
			break;
		case "CssSelector":
			locator = By.cssSelector(locatorValue);
			break;
		case "LinkText":
			locator = By.linkText(locatorValue);
			break;
		case "PartialLinkText":
			locator = By.partialLinkText(locatorValue);
			break;
		case "TagName":
			locator = By.tagName(locatorValue);
			break;
		case "Xpath":
			locator = By.xpath(locatorValue);
			break;
		}
		return locator;
	}

	public synchronized void click(String locatorKey) {
		try {
			clickElementWithLocatorkey(locatorKey);
			wait(2000);
		} catch (Exception e) {
			for (int i = 0; i <= MAX_RETRY_COUNT; i++) {
				try {
					boolean exists = false;
					driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
					WebElement findElement = driver.findElement(getObjectLocator(locatorKey));
					exists = true;
					if (exists) {
						if (findElement.isDisplayed()) {
							WaitForElementToBeClickable(findElement);
							clickElement(findElement);
							//findElement.click();
							break;
						}
					}
					driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
				} catch (NoSuchElementException ex) {
					if (i == MAX_RETRY_COUNT) {
						throw e;
					}
				}
			}
		}

	}
	
	
	/**
	 * Accepts link text as a key and clicks on the relevant link
	 * 
	 * @param linkKey
	 */

	public synchronized void clickLink(String locatorKey) {
		try {
			driver.findElement(getObjectLocator(locatorKey)).click();
		} catch (Exception e) {
			TestReporter.logTestStep("Exception occured on clicking a link");
			e.printStackTrace();

		}
	}
	
	public synchronized String getElementText(String locatorKey) {
		try {
			return driver.findElement(getObjectLocator(locatorKey)).getText();
		} catch (Exception e) {
			TestReporter.logTestStep("Exception occured on clicking a link");
			e.printStackTrace();
			return null;
		}
	}

	public synchronized void clickandwait(String locatorKey) {

		try {
			WebDriverWait wait = new WebDriverWait(driver, 120);
			WebElement element = wait
					.until(ExpectedConditions.visibilityOfElementLocated(getObjectLocator(locatorKey)));
			element.click();
		} catch (TimeoutException timeout) {
			for (int i = 0; i <= MAX_RETRY_COUNT; i++) {
				try {
					boolean exists = false;
					driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
					WebElement findElement = driver.findElement(getObjectLocator(locatorKey));
					exists = true;
					if (exists) {
						findElement.click();
						break;
					}
					driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				} catch (NoSuchElementException e) {
					if (i == MAX_RETRY_COUNT) {
						throw e;
					}
				}
			}
		}

	}

	public synchronized void wait(int waitSec) {
		try {
			Thread.sleep(waitSec);
		} catch (InterruptedException e) {
			TestReporter.logTestStep("Exception occurred " + e.getMessage());
		}
	}

	public synchronized void waitForPageLoad() {
		ExpectedCondition<Boolean> pageLoadCondition = new ExpectedCondition<Boolean>() {
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").equals("complete");
			}
		};
		WebDriverWait wait = new WebDriverWait(driver, 60);
		wait.until(pageLoadCondition);
	}

	public synchronized void waitToBeVisible(String locatorKey, int waitSecs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.visibilityOfElementLocated(getObjectLocator(locatorKey)));

		} catch (TimeoutException timeout) {
			for (int i = 0; i <= MAX_RETRY_COUNT; i++) {
				try {
					boolean exists = false;
					driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
					driver.findElement(getObjectLocator(locatorKey));
					exists = true;
					if (exists) {
						break;
					}
					driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				} catch (NoSuchElementException e) {
					if (i == MAX_RETRY_COUNT) {
						throw e;
					}
				}
			}
		}

	}

	public synchronized void waitUntilVisbilityOfElementLocated(String locatorKey, int waitSecs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.visibilityOfElementLocated(getObjectLocator(locatorKey)));

		} catch (TimeoutException timeout) {
			for (int i = 0; i <= MAX_RETRY_COUNT; i++) {
				try {
					boolean exists = false;
					driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
					driver.findElement(getObjectLocator(locatorKey));
					exists = true;
					if (exists) {
						break;
					}
					driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				} catch (NoSuchElementException e) {
					if (i == MAX_RETRY_COUNT) {
						throw e;
					}
				}
			}
		}

	}

	public synchronized void waitToBeSelected(String locatorKey, int waitSecs) {

		waitUntilElementIsSelectable(locatorKey, waitSecs);
	}

	private synchronized void waitUntilElementIsSelectable(String locatorKey, int waitSecs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, waitSecs);
			wait.until(ExpectedConditions.elementToBeSelected(getObjectLocator(locatorKey)));

		} catch (TimeoutException timeout) {
			for (int i = 0; i <= MAX_RETRY_COUNT; i++) {
				try {
					boolean exists = false;
					driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
					driver.findElement(getObjectLocator(locatorKey));
					exists = true;
					if (exists) {
						break;
					}
					driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				} catch (NoSuchElementException e) {
					if (i == MAX_RETRY_COUNT) {
						throw e;
					}
				}
			}
		}
	}

	public synchronized void frameToBeAvailableAndSwitchToIt(String locatorKey, int waitSecs) {
		waitUntilFrameToBeAvailableAndSwitchToIt(locatorKey, waitSecs);
	}

	private void waitUntilFrameToBeAvailableAndSwitchToIt(String locatorKey, int waitSecs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, waitSecs);
			wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(getObjectLocator(locatorKey)));

		} catch (TimeoutException timeout) {
			for (int i = 0; i <= MAX_RETRY_COUNT; i++) {
				try {
					boolean exists = false;
					driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
					driver.findElement(getObjectLocator(locatorKey));
					exists = true;
					if (exists) {
						break;
					}
					driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				} catch (NoSuchElementException e) {
					if (i == MAX_RETRY_COUNT) {
						throw e;
					}
				}
			}
		}

	}

	public synchronized void waitForPresenceOfElements(String locatorKey, int waitSecs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, waitSecs);
			wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(getObjectLocator(locatorKey)));

		} catch (TimeoutException timeout) {
			for (int i = 0; i <= MAX_RETRY_COUNT; i++) {
				try {
					boolean exists = false;
					driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
					driver.findElement(getObjectLocator(locatorKey));
					exists = true;
					if (exists) {
						break;
					}
					driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				} catch (NoSuchElementException e) {
					if (i == MAX_RETRY_COUNT) {
						throw e;
					}
				}
			}
		}
	}

	public synchronized void waitForInvisibilityOfElement(String locatorKey, int waitSecs) {

		waitForInvisibilityOfElements(locatorKey, waitSecs);

	}

	private synchronized void waitForInvisibilityOfElements(String locatorKey, int waitSecs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, waitSecs);
			wait.until(ExpectedConditions.invisibilityOfElementLocated(getObjectLocator(locatorKey)));

		} catch (TimeoutException timeout) {
			for (int i = 0; i <= MAX_RETRY_COUNT; i++) {
				try {
					boolean exists = false;
					driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
					driver.findElement(getObjectLocator(locatorKey));
					exists = true;
					if (exists) {
						break;
					}
					driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				} catch (NoSuchElementException e) {
					if (i == MAX_RETRY_COUNT) {
						throw e;
					}
				}
			}
		}
	}

	public synchronized void waitForElementPresence(String locatorKey, int waitSecs) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, waitSecs);
			wait.until(ExpectedConditions.presenceOfElementLocated(getObjectLocator(locatorKey)));

		} catch (TimeoutException timeout) {
			for (int i = 0; i <= MAX_RETRY_COUNT; i++) {
				try {
					boolean exists = false;
					driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
					driver.findElement(getObjectLocator(locatorKey));
					exists = true;
					if (exists) {
						break;
					}

				} catch (NoSuchElementException e) {
					if (i == MAX_RETRY_COUNT) {
						throw e;
					}
				}
			}
		}
	}

	public synchronized void waitToBeClickable(String locatorKey, int waitSecs) {

		try {
			WebDriverWait wait = new WebDriverWait(driver, waitSecs);
			wait.until(ExpectedConditions.elementToBeClickable(getObjectLocator(locatorKey)));

		} catch (TimeoutException timeout) {
			for (int i = 0; i <= MAX_RETRY_COUNT; i++) {
				try {
					boolean exists = false;
					driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
					driver.findElement(getObjectLocator(locatorKey));
					exists = true;
					if (exists) {
						break;
					}
					driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				} catch (NoSuchElementException e) {
					if (i == MAX_RETRY_COUNT) {
						throw e;
					}
				}
			}
		}
	}

	public synchronized void fluentWait(String locatorKey, int waitSecs) {
		/*Wait<WebDriver> wait = new FluentWait<WebDriver>(driver).withTimeout(30, TimeUnit.SECONDS)
				.pollingEvery(5, TimeUnit.SECONDS).ignoring(NoSuchElementException.class);

		wait.until(new Function<WebDriver, WebElement>() {
			public WebElement apply(WebDriver driver) {
				return driver.findElement(getObjectLocator(locatorKey));
			}
		});*/
		
		try {
			WebDriverWait wait = new WebDriverWait(driver, 30);
			wait.until(ExpectedConditions.visibilityOfElementLocated(getObjectLocator(locatorKey)));

		} catch (TimeoutException timeout) {
			for (int i = 0; i <= MAX_RETRY_COUNT; i++) {
				try {
					boolean exists = false;
					driver.manage().timeouts().implicitlyWait(2, TimeUnit.SECONDS);
					driver.findElement(getObjectLocator(locatorKey));
					exists = true;
					if (exists) {
						break;
					}
					driver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
				} catch (NoSuchElementException e) {
					if (i == MAX_RETRY_COUNT) {
						throw e;
					}
				}
			}
		}
	}

	public synchronized boolean isClickable(String locatorKey) {
		try {
			WebDriverWait wait = new WebDriverWait(driver, 5);
			wait.until(ExpectedConditions.elementToBeClickable(getObjectLocator(locatorKey)));
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * Accepts xpath and the text to be provided in the input box
	 * 
	 * @param xpathKey
	 * @param text
	 */

	public synchronized void input(String locatorKey, String text) {
		try {
			driver.findElement(getObjectLocator(locatorKey)).sendKeys(text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public synchronized void clearText(String locatorKey){

		try{
			driver.findElement(getObjectLocator(locatorKey)).clear();
		} catch (Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * Accepts xpath as an argument and verifies if the element is present on
	 * the page
	 * 
	 * @param xpathKey
	 * @return
	 */

	public synchronized boolean isElementPresent(String locatorKey) {
		boolean isElementPresent = true;
		try {
			driver.findElement(getObjectLocator(locatorKey));
		} catch (Exception e) {
			isElementPresent = false;
			return isElementPresent;
		}
		return isElementPresent;
	}
	
	public synchronized boolean isDynamicElementPresent(String locatorKey, String dynamicText) {
		boolean isElementPresent = true;
		try {
			driver.findElement(getDynamicObjectLocator(locatorKey, dynamicText));
		} catch (Exception e) {
			isElementPresent = false;
			return isElementPresent;
		}
		return isElementPresent;
	}

	public synchronized boolean isElementFocused(String locatorKey) {
		boolean isElementFocused = false;
		try {
			isElementFocused = driver.findElement(getObjectLocator(locatorKey))
					.equals(driver.switchTo().activeElement());
		} catch (Exception e) {
			isElementFocused = false;
			return isElementFocused;
		}
		return isElementFocused;
	}

	public synchronized String getAttributeValue(String locatorKey, String attributeValue) {
		String attributeText = "";
		try {
			WebElement element = driver.findElement(getObjectLocator(locatorKey));
			waitForElementPresence(locatorKey, 20);
			attributeText = element.getAttribute(attributeValue);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return attributeText;
	}
	
	/**
	 * Verifies if the provided link is present on the page
	 * 
	 * @param linkText
	 * @return
	 */

	public synchronized boolean isLinkPresent(String locatorKey) {
		boolean isLinkPresent = true;
		WebDriverWait wait = new WebDriverWait(driver, 60);

		try {

			wait.until(ExpectedConditions.presenceOfElementLocated(getObjectLocator(locatorKey)));

		} catch (Exception e) {
			isLinkPresent = false;
			e.printStackTrace();
			return isLinkPresent;
		}
		return isLinkPresent;
	}

	public synchronized void switchToChildWindow(String locatorKey) {

		String parent = driver.getWindowHandle();
		WebDriverWait wait = new WebDriverWait(driver, 120);
		wait.until(ExpectedConditions.elementToBeClickable(getObjectLocator(locatorKey)));
		waitToBeClickable(locatorKey, 30);
		click(locatorKey);
		String noOfHandles = String.valueOf(driver.getWindowHandles().size());
		TestReporter.logTestStep("No of Window handles :" + noOfHandles);
		if (driver.getWindowHandles().size() == 2) {
			for (String window : driver.getWindowHandles()) {
				if (!window.equals(parent)) {
					driver.switchTo().window(window);
					break;
				}
			}
		}

		try {
			driver = waitForWindow(driver);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	public synchronized void switchToThirdWindow(String locatorKey) {
		ArrayList<String> allWindows = new ArrayList<String>(driver.getWindowHandles());
		driver.switchTo().window(allWindows.get(2));
		driver.switchTo().defaultContent();
	}

	public synchronized void switchToParentWindowFromFrame() {
		driver.switchTo().defaultContent();
	}

	public synchronized void switchToActiveElement() {
		driver.switchTo().activeElement();
	}

	public synchronized void switchToAlert() {
		driver.switchTo().alert();
	}

	public WebDriver waitForWindow(WebDriver driver2) throws InterruptedException {
		// wait until number of window handles become 2 or until 6 seconds are
		// completed.
		int timecount = 1;
		do {
			driver2.getWindowHandles();
			Thread.sleep(200);
			timecount++;
			if (timecount > 30) {
				break;
			}
		} while (driver2.getWindowHandles().size() != 2);

		return driver2;

	}

	public synchronized String getParentWindowHandle() {
		String myWindowHandle = driver.getWindowHandle();
		return myWindowHandle;
	}

	public synchronized void switchToWindow(String myWindowHandle) {
		driver.switchTo().window(myWindowHandle);
	}

	public synchronized void switchToFrame(String locatorKey) {
		driver.switchTo().defaultContent();
		driver.switchTo().frame(browserFactory.getOBJECT_REPOSITORY().getProperty(locatorKey));
	}

	public synchronized void switchToFrameByLocator(String locatorKey) {
		driver.switchTo().frame(driver.findElement(By.xpath(locatorKey)));
	}

	public synchronized void switchToNewWindow(String locatorKey) {
        for (String winHandle : driver.getWindowHandles()) {
            driver.switchTo().window(winHandle);
        }
    }

    public void gotToUrl(String url){
		driver.get(url);
	}

//    public synchronized void switchToPopUpWindow() {
//        for (String winHandle : driver.getWindowHandles()) {
//            String subWindowHandler = null;
//            Set<String> handles = driver.getWindowHandles(); // get all window handles
//            Iterator<String> iterator = handles.iterator();
//            while (iterator.hasNext()){
//                subWindowHandler = iterator.next();
//            }
//            driver.switchTo().window(subWindowHandler);
//        }
//    }

	public synchronized boolean isLinkDisplayed(String locatorKey) {
		boolean isLinkDisplayed = false;
		try {

			isLinkDisplayed = driver.findElement(getObjectLocator(locatorKey)).isDisplayed();

		} catch (Exception e) {
			e.printStackTrace();
			return isLinkDisplayed;
		}

		return isLinkDisplayed;
	}

	public synchronized boolean isElementDisplayed(String locatorKey) {
		boolean isELementDisplayed = false;
		try {
			isELementDisplayed = driver.findElement(getObjectLocator(locatorKey)).isDisplayed();
		} catch (org.openqa.selenium.NoSuchElementException e) {
			return isELementDisplayed;
		}
		return isELementDisplayed;
	}

	public synchronized boolean isElementsDisplayed(String locatorKey) {
		boolean isELementsDisplayed = false, result = false;
		try {
			List<WebElement> elementList = driver.findElements(getObjectLocator(locatorKey));
			for (WebElement element : elementList) {
				if (element.isDisplayed()) {
					result = true;
				} else {
					result = false;
				}
				return result;
			}
		} catch (org.openqa.selenium.NoSuchElementException e) {

			return isELementsDisplayed;
		}
		return result;
	}

	public synchronized int NoOfElementsDisplayed(String locatorKey) {
		int count = 0;
		try {
			List<WebElement> elementList = driver.findElements(getObjectLocator(locatorKey));
			for (int i = 0; i < elementList.size(); i++) {
				if (elementList.get(i).isDisplayed()) {
					count++;
				}
			}
		} catch (org.openqa.selenium.NoSuchElementException e) {

			return count;
		}
		return count;
	}

	/**
	 * Accepts a filename as an argument and creates a file with the screenshot
	 * 
	 * @param fileName
	 */

	/**
	 * loads the page with provided url
	 * 
	 * @param url
	 */

	public void loadPage(String path) {
		String BASE_URL = browserFactory.getConfig().getProperty("URL");

		TestReporter.logTestStep("Java version: " + Runtime.class.getPackage().getImplementationVersion());
		TestReporter.logTestStep("Directing browser to:" + path);
		driver.get(BASE_URL + browserFactory.getConfig().getProperty(path));
	}

	public void homePage() {
		String BASE_URL = browserFactory.getConfig().getProperty("URL");
		driver.get(BASE_URL);
	}

	/**
	 * returns the title of the page
	 * 
	 * @return
	 */

	public synchronized String getPageTitle() {
		return browserDriver.getCurrentDriver().getTitle();
	}

	/**
	 * reload the page with specified url
	 *
	 */
	public synchronized void reopenAndLoadPage(String url) {
        driver = browserDriver.getCurrentDriver();
        driver = null;
        loadPage(url);
    }

    public synchronized void refreshPage() {
        driver = browserDriver.getCurrentDriver();
        driver.navigate().refresh();
    }

	public synchronized String getcurrenturl() {

		String url = driver.getCurrentUrl();
		return url;
	}

	public WebElement getParent(WebElement element) {
		return element.findElement(By.xpath(".."));
	}

	/**
	 * returns the dropdown options for the specified webelement
	 * 
	 * @param webElement
	 * @return
	 */

	public List<WebElement> getDropDownOptions(WebElement webElement) {
		Select select = new Select(webElement);
		return select.getOptions();
	}

	/**
	 * 
	 * @param webElement
	 * @param value
	 * @return
	 */
	public WebElement getDropDownOption(WebElement webElement, String value) {
		WebElement option = null;
		List<WebElement> options = getDropDownOptions(webElement);
		for (WebElement element : options) {
			if (element.getAttribute("value").equalsIgnoreCase(value)) {
				option = element;
				break;
			}
		}
		return option;
	}

	/**
	 * 
	 * @param eidentifier
	 * @param type
	 * @return
	 */
	public synchronized WebElement getElementByIdentifier(String locatorKey) {

		WebElement findElement = null;
		findElement = driver.findElement(getObjectLocator(locatorKey));
		return findElement;

	}
	
	public synchronized WebElement getDynamicElementByIdentifier(String locatorKey, String value) {

		WebElement findElement = null;
		findElement = driver.findElement(getDynamicObjectLocator(locatorKey, value));
		return findElement;

	}

	public List<WebElement> getElementsByIdentifier(String locatorKey) {
		List<WebElement> spCollList = new ArrayList<WebElement>();
		spCollList = driver.findElements(getObjectLocator(locatorKey));
		return spCollList;
	}

	/**
	 * 
	 * @param element
	 * @param newElementCSS
	 * @param Identifier
	 */
	public void performMouseOver_and_moveToNewElement(WebElement element, String newElementCSS, String Identifier) {
		Actions action = new Actions(driver);
		action.moveToElement(element).moveToElement(getElementByIdentifier(newElementCSS)).click().build().perform();

	}

	/**
	 * 
	 * @param xpath
	 */

	public void performMouseClick_and_Hold(String locatorKey) {

		WebElement element = driver.findElement(getObjectLocator(locatorKey));
		Actions action = new Actions(driver);
		try {
			action.clickAndHold(element).perform();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public void performMouseClick(String locatorKey) {
		Actions action = new Actions(driver);
		WebElement findElement = driver.findElement(getObjectLocator(locatorKey));
		action.click(findElement).build().perform();
	}

	/**
	 * 
	 * @param ele
	 * @param val
	 */
	public void selectDropDownValue(String locatorKey, String val) {
		try {
			TestReporter.logTestStep("Entering the drop down value");
			TestReporter.logTestStep(locatorKey.toString() + val.toString());
			Select clickThis = new Select(driver.findElement(getObjectLocator(locatorKey)));
			clickThis.selectByVisibleText(val);
		} catch (Exception e) {
			TestReporter.logTestStep("Exception occurred " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void selectDropDownValueWIthJSExecutor(String locatorKey, String val, String elementId) {

		try {
			TestReporter.logTestStep("Entering the drop down value");
			TestReporter.logTestStep(locatorKey.toString()+ " : " + val.toString());
			String JS_SCRIPT = "jQuery('#" + elementId + "').css('display','block')";
			((JavascriptExecutor) driver).executeScript(JS_SCRIPT);
			Select clickThis = new Select(driver.findElement(getObjectLocator(locatorKey)));
			clickThis.selectByVisibleText(val);
		} catch (Exception e) {
			TestReporter.logTestStep("Exception occurred " + e.getMessage());
			e.printStackTrace();
		}

	}

	/**
	 * 
	 * @param table
	 * @return
	 */

	public WebElement readSelectElementFromTable(WebElement table) {
		List<WebElement> rows = table.findElements(By.tagName("select"));
		return rows.get(0);
	}

	/**
	 * 
	 * @param table
	 * @return
	 */

	public boolean playAudio(String locatorKey) {
		boolean isOffset = false;
		JavascriptExecutor js = (JavascriptExecutor) driver;
		Object executeScript = js.executeScript("new Audio('issue6_2010_sutures.mp3').play()");
		wait(15000);
		System.out.println(executeScript);
		Double offsetPixeldbl = 0d;
		Long offsetPixellong = 0l;
		if (executeScript instanceof Double) {
			offsetPixeldbl = (Double) executeScript;
			System.out.println("offsetPixeldbl" + offsetPixeldbl);
			if (offsetPixeldbl.intValue() > 0)
				isOffset = true;
		}
		if (executeScript instanceof Long) {
			offsetPixellong = (Long) executeScript;
			System.out.println("offsetPixellong" + offsetPixellong);
			if (offsetPixellong.intValue() > 0)
				isOffset = true;
		}
		return isOffset;
	}

	public void goToPreviousPage() {
		driver.navigate().back();
	}

	public int findBrokenLinks(String locatorKey) {
		try {
			invalidLinksCount = 0;
			List<WebElement> anchorTagsList = driver.findElements(getObjectLocator(locatorKey));
			String NoOfLinks = String.valueOf(anchorTagsList.size());
			TestReporter.logTestStep("Total no. of links are : " + NoOfLinks);
			for (WebElement anchorTagElement : anchorTagsList) {
				if (anchorTagElement != null) {
					String url = anchorTagElement.getAttribute("href");
					if (url != null && !url.contains("javascript")) {
						verifyURLStatus(url);
					} else {
						invalidLinksCount++;
					}
				}
			}

			TestReporter.logTestStep("Total no. of invalid links are : " + invalidLinksCount);

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(e.getMessage());
		}
		return invalidLinksCount;
	}

	public void verifyURLStatus(String URL) {

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(URL);
		try {
			HttpResponse response = client.execute(request);
			// verifying response code and The HttpStatus should be 200 if not,
			// increment invalid link count
			if (response.getStatusLine().getStatusCode() != 200) {
				invalidLinksCount++;
				TestReporter.logTestStep("Invalid URL : " + URL);
				TestReporter.logTestStep(String.valueOf(response.getStatusLine().getStatusCode()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public int findBrokenImages(String locatorKey) {
		invalidImgCount = 0;
		List<WebElement> imgTagsList = driver.findElements(getObjectLocator(locatorKey));
		String noOfLinks = String.valueOf(imgTagsList.size());
		TestReporter.logTestStep("Total no. of images are : " + noOfLinks);
		for (WebElement imgTagElement : imgTagsList) {
			if (imgTagElement != null) {
				String src = imgTagElement.getAttribute("src");
				if (src.equals("")) {
					TestReporter.logTestStep("Image Link  with no src on the page");
					src = null;
				}
				if (src != null) {
					verifySRCStatus(src);
					TestReporter.logTestStep("Image Link verified" + src);
				} else {
					invalidImgCount++;
				}
			}
		}
		return invalidImgCount;
	}

	public void verifySRCStatus(String SRC) {

		HttpClient client = HttpClientBuilder.create().build();
		HttpGet request = new HttpGet(SRC);
		try {
			HttpResponse response = client.execute(request);
			if (response.getStatusLine().getStatusCode() != 200) {
				invalidImgCount++;
				TestReporter.logTestStep("Invalid Image Link : " + SRC);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public boolean isTextPresentOnPage(String text) {
		boolean isTextPresent=false;
		String lowerCase = text.toLowerCase();
		String upperCAse = text.toUpperCase();
		String capitalised = upperCaseAllFirst(text);
		if (driver.getPageSource().contains(text) || driver.getPageSource().contains(upperCAse)
                || driver.getPageSource().contains(lowerCase)
                || driver.getPageSource().contains(capitalised)){
			isTextPresent=true;
			return isTextPresent;
		}
		return isTextPresent;
	}

	public String upperCaseAllFirst(String value) {
		//System.out.println(value);
		char[] array = value.toCharArray();
		// Uppercase first letter.
		//System.out.println(array);
		array[0] = Character.toUpperCase(array[0]);
		// Uppercase all letters that follow a whitespace character.
		for (int i = 1; i < array.length; i++) {
			if (Character.isWhitespace(array[i - 1])) {
				array[i] = Character.toUpperCase(array[i]);
			}
		}
		// Result.
		return new String(array).toString();
	}

	public void clickWithJavaScript(String locatorKey) {
		// JavascriptExecutor jsExecutor;
		// jsExecutor = (JavascriptExecutor) driver;
		WebElement element = driver.findElement(getObjectLocator(locatorKey));
		JavascriptExecutor executor = (JavascriptExecutor) driver;
		executor.executeScript("arguments[0].click();", element);
	}
	
	public void clickElementWithLocatorkey(String locatorKey) {
		// JavascriptExecutor jsExecutor;
		// jsExecutor = (JavascriptExecutor) driver;
		WebElement element = driver.findElement(getObjectLocator(locatorKey));
		waitForElementPresence(locatorKey, 20);
		WaitForElementToBeClickable(element);
		element.click();
	}
	
	public void clickDynamicElementWithLocatorkey(String locatorKey, String dynamicValue) {
		// JavascriptExecutor jsExecutor;
		// jsExecutor = (JavascriptExecutor) driver;
		WebElement element = driver.findElement(getDynamicObjectLocator(locatorKey, dynamicValue));
		WaitForElementToBeClickable(element);
		element.click();
	}

	public Set getCookies() {
		Set allCookies = driver.manage().getCookies();
		return allCookies;
	}

	public void setCookie(String name, String value) {
		Cookie cookie = new Cookie(name, value);
		driver.manage().addCookie(cookie);
	}



	public boolean isElementNotPresent(String locatorKey) {
		List<WebElement> listOfElements = driver.findElements(getObjectLocator(locatorKey));

		int numberOfElementsFound = listOfElements.size();

		if (numberOfElementsFound == 0) {
			return true;
		} else {
			return false;
		}
	}

	public boolean isListOfElementsPresent(String locatorKey) {
		List<WebElement> listOfElements = driver.findElements(getObjectLocator(locatorKey));

		int numberOfElementsFound = listOfElements.size();

		if (numberOfElementsFound > 0) {
			return true;
		} else {
			return false;
		}

	}

	public void closeWindow() {
		driver.close();
	}

	public void closeSession() {
		driver.quit();
	}

	public boolean isSelected(String locatorKey) {
		return driver.findElement(getObjectLocator(locatorKey)).isSelected();
	}

	public boolean isLinkWorking(String locatorKey) {
		boolean isOffset = false;
		try {
			driver.findElement(getObjectLocator(locatorKey)).click();
		} catch (Exception e) {
			TestReporter.logTestStep("Exception occurred on Click");
			e.printStackTrace();
		}
		JavascriptExecutor js = (JavascriptExecutor) getDriver();
		this.wait(3000);
		Object numberOfPixels = js.executeScript("return window.pageYOffset;");
		Double offsetPixeldbl = 0d;
		Long offsetPixellong = 0l;
		if (numberOfPixels instanceof Double) {
			offsetPixeldbl = (Double) numberOfPixels;
			if (offsetPixeldbl.intValue() > 0)
				isOffset = true;
		}
		if (numberOfPixels instanceof Long) {
			offsetPixellong = (Long) numberOfPixels;
			if (offsetPixellong.intValue() > 0)
				isOffset = true;
		}

		return isOffset;
	}

	public void closeNewTab() {
		ArrayList<String> allWindows = new ArrayList<String>(driver.getWindowHandles());
		int size = allWindows.size();
		int x = 0;
		for (int i = 0; i < size; i++) {
			if (i == x)
				continue;
			driver.switchTo().window(allWindows.get(i));
			driver.close();
			x++;

		}
		driver.switchTo().window(allWindows.get(0));
	}

	public void reopenBrowser(){
		String url = driver.getCurrentUrl();
		Set<Cookie> allCookies = driver.manage().getCookies();
		driver.close();
		browserDriver = new BrowserDriver();
		driver = browserDriver.getCurrentDriver();
		driver.get(browserFactory.getConfig().getProperty("URL"));
		for(Cookie cookie: allCookies){
			driver.manage().addCookie(cookie);
		}
		driver.get(url);

	}
	public String getBaseUrl() {
        if (System.getProperty("siteurl") == null) {
            return TestRunSettings.GetUrl();
        }
        return System.getProperty("siteurl");
    }
    public void ScrollPage(Integer scrollAmount) {
        JavascriptExecutor jse = (JavascriptExecutor) driver;
        jse.executeScript("window.scrollBy(0, " + scrollAmount.toString() + ")", "");
    }

    public void waitASec(int iWait) {
        try {
            Thread.sleep(iWait);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //################## VERIFICATIONS #########################
    protected void verifyCurrentURL(String expectedPageURL) {
        TestReporter.logTestStep("Verifying Page URL  is: '" + expectedPageURL + "' (" + GetCallingMethodName() + ").");

        Assert.assertEquals(driver.getCurrentUrl(), expectedPageURL, "ERROR: verifyCurrentURL() ");
    }

    protected void verifyCurrentURLContains(String expectedPageURL) {
        TestReporter.logTestStep("Verifying Page URL  Contains:'" + expectedPageURL + "' (" + GetCallingMethodName() + ").");

        waitASec(5000);
        Assert.assertTrue(driver.getCurrentUrl().contains(expectedPageURL), "ERROR: verifyCurrentURL() " + driver.getCurrentUrl());
    }

    protected boolean verifyCurrentURLContainsString(String expectedPageURL) {
        TestReporter.logTestStep("Verifying Page URL  Contains:'" + expectedPageURL + "' (" + GetCallingMethodName() + ").");
        waitASec(5000);
        boolean result = driver.getCurrentUrl().contains(expectedPageURL);
        return result;
    }
    
    public boolean verifyText(WebElement element, String expectedText) {
        TestReporter.logTestStep("Verifying text is '" + expectedText + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
       waitASec(1000); //added because of stale elements
        Assert.assertEquals(element.getText(), expectedText, "ERROR: verifyText() ");
        if(element.getText().equals(expectedText)){
        	return true;
        }
        else
        	return false;
    }

    protected void verifyPartialText(WebElement element, String expectedText) {
        TestReporter.logTestStep("Verifying partial text is '" + expectedText + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        refreshElement(element);
        assertTrue(element.getText().toLowerCase().contains(expectedText.toLowerCase()), "ERROR: verifyPartialText() Expecting: " + element.getText() + " to contain:  " + expectedText);
    }
    protected void verifyTextOptions(WebElement element, String expectedText,String expectedText2) {
        TestReporter.logTestStep("Verifying partial text is '" + expectedText + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        refreshElement(element);
        assertTrue(element.getText().toLowerCase().contains(expectedText.toLowerCase()) ||element.getText().toLowerCase().contains(expectedText2.toLowerCase())  , "ERROR: verifyPartialText() Expecting: " + element.getText() + " to contain:  " + expectedText);
    }


    protected void VerifySelectedOption(WebElement element, String expectedText) {
        TestReporter.logTestStep("Verifying selected option is '" + expectedText + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");

        Assert.assertEquals(element.getText(), expectedText, "ERROR: VerifySelectedOption() ");
    }

    protected void verifyCount(int actualCount, int expectedCount) {
        TestReporter.logTestStep("Verifying count is '" + actualCount + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");

        Assert.assertEquals(actualCount, expectedCount, "ERROR: verifyCount() ");
    }

    protected void VerifyCountOver(int actualCount, int minimumCount) {
        TestReporter.logTestStep("Verifying count is'" + actualCount + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");


        Assert.assertEquals((actualCount > minimumCount), true, "ERROR: VerifyCountOver() ");
    }

    protected void VerifyTimeFormat(WebElement element, String matchText) {
        TestReporter.logTestStep("Verifying Time format is '" + matchText + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");

        assertTrue(element.getText().matches(matchText));

        //String Time = "Wed Jul 20 01:49:16 PDT 2016";
        //Assert.assertTrue(Time.matches("(\\D{3}) (\\D{3}) (\\d{2}) (\\d{2}):(\\d{2}):(\\d{2}) PDT (\\d{4})"));
    }

    protected void VerifyListNotEmpty(List<WebElement> element) {
        TestReporter.logTestStep("Verifying list is not empty " + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        assertTrue(!element.isEmpty(), "ERROR: VerifyListNotEmpty() ");
    }

    protected String getNumbersFromString(String text) {

        return text.replaceAll("\\D+","");
    }


    protected void VerifyListIsEmpty(List<WebElement> element) {
        TestReporter.logTestStep("Verifying list is not empty " + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        assertTrue(element.isEmpty(), "ERROR: VerifyListIsEmpty() ");
    }

    protected void VerifyListsSizeEqual(List<WebElement> element1, List<WebElement> element2) {
        TestReporter.logTestStep("Verifying lists are the same size  " + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]" + "]");
        Assert.assertEquals(element1.size(), element2.size(), "ERROR: VerifyListsSizeEqual() ");
    }

    public void VerifyElementDisplayed(WebElement element) {
        refreshElement(element);
        TestReporter.logTestStep("Verifying element displayed  " + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        String elementStyle = element.getAttribute("style");
        boolean isVisible = !(elementStyle.equals("display: none;") || elementStyle.equals("visibility: hidden;"));
        assertTrue((isVisible), "ERROR: VerifyElementDisplayed() ");
    }

    protected void VerifyElementEnabled(WebElement element) {
        TestReporter.logTestStep("Verifying element clickable  " + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        assertTrue(element.isEnabled(), "ERROR: VerifyElementEnabled() ");
    }

    protected void VerifyElementPresent(WebElement element) {
        TestReporter.logTestStep("Verifying element is present  " + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");

        assertTrue(isElementPresent(element), "Element Is not Displayed");

    }


    protected boolean isElementPresent(WebElement element) {
        TestReporter.logTestStep("Verifying element is present  " + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        try {
            element.getTagName();
            return true;
        } catch (NoSuchElementException e) {
            e.printStackTrace();
            return false;
        }
    }


    protected void VerifyPictureDisplayed(WebElement element) {
        TestReporter.logTestStep("Verifying picture displayed  " + "' (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        Boolean ImagePresent = (Boolean) ((JavascriptExecutor) driver).executeScript("return arguments[0].complete && typeof arguments[0].naturalWidth != \"undefined\" && arguments[0].naturalWidth > 0", element);
        assertTrue(ImagePresent, "ERROR: VerifyPictureDisplayed() ");
    }


    //########################## ACTIONS ######################
    protected void clickElement(WebElement element) {
        int count = 0;
        int maxTries = 3;
       JavascriptExecutor js = ((JavascriptExecutor) driver);
     TestReporter.logTestStep("Clicking Button" + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
      waitForJSandJQueryToLoad();
       while (true) {
          try {
        	  //WebDriverWait wait3 = new WebDriverWait(driver, 10);
        	 // wait3.until(ExpectedConditions.visibilityOfElementLocated(element);
        	 // WaitForElementToBeClickable(element);
              //  element.click();
        	  js.executeScript("arguments[0].click();", element);
               break;
          } catch (StaleElementReferenceException e) {
              refreshElement(element);
              if (++count == maxTries) throw e;
              System.out.println("Trying to recover from a stale element :" + e.getMessage());

          } catch (WebDriverException e) {
                js.executeScript("arguments[0].scrollIntoView(true);", element);
                if (++count == maxTries) throw e;           }
      }
    }


    protected void clickElementinList(List<WebElement> element, int i) {
        TestReporter.logTestStep("Clicking Element in list" + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        element.get(i).click();
    }

    protected void VerifyCurrentUrlMatches(String expected) {
        TestReporter.logTestStep("Verifying current url matches " + expected + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        String URL = driver.getCurrentUrl();
        Assert.assertEquals(URL, expected, "Expected URL: " + expected + "Current URL: " + URL);

    }

    protected String verifyElementUrlContains(WebElement element, String expected) {
        TestReporter.logTestStep("Verifying  element url contains href " + expected + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        String elementUrl = element.getAttribute("href");
        assertTrue(elementUrl.contains(expected));
        return elementUrl;

    }

    protected void clickElementJS(WebElement element) {
        TestReporter.logTestStep("Clicking Element JS" + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    public static WebElement refreshElement(WebElement elem){
        if(!isElementStale(elem))
            return elem;
        Object lastObject = null;
        try{
            String[] arr = elem.toString().split("->");
            for(String s:arr){
                String newstr = s.trim().replaceAll("^\\[+", "").replaceAll("\\]+$","");
                String[] parts = newstr.split(": ");
                String key = parts[0];
                String value = parts[1];
                int leftBracketsCount = value.length() - value.replace("[", "").length();
                int rightBracketscount = value.length() - value.replace("]", "").length();
                if(leftBracketsCount-rightBracketscount==1)
                    value = value + "]";
                if(lastObject==null){

                }else{
                    lastObject = getWebElement(lastObject, key, value);
                }
            }
        }catch(Exception e){
            TestReporter.logTestStep("Error in Refreshing the stale Element.");
        }
        return (WebElement)lastObject;
    }
    private static WebElement getWebElement(Object lastObject, String key, String value){
        WebElement element = null;
        try {
            By by = getBy(key,value);
            Method m = getCaseInsensitiveDeclaredMethod(lastObject,"findElement");
            element = (WebElement) m.invoke(lastObject,by);
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return element;
    }
    private static By getBy(String key, String value) throws InvocationTargetException, IllegalAccessException {
        By by = null;
        Class clazz = By.class;
        String methodName = key.replace(" ","");
        Method m = getCaseInsensitiveStaticDeclaredMethod(clazz,methodName);
        return (By) m.invoke(null,value);
    }
    private static Method getCaseInsensitiveDeclaredMethod(Object obj, String methodName) {
        Method[] methods = obj.getClass().getMethods();
        Method method = null;
        for (Method m : methods) {
            if (m.getName().equalsIgnoreCase(methodName)) {
                method = m;
                break;
            }
        }
        if (method == null) {
            throw new IllegalStateException(String.format("%s Method name is not found for this Class %s", methodName, obj.getClass().toString()));
        }
        return method;
    }
    private static Method getCaseInsensitiveStaticDeclaredMethod(Class clazz, String methodName) {
        Method[] methods = clazz.getMethods();
        Method method = null;
        for (Method m : methods) {
            if (m.getName().equalsIgnoreCase(methodName)) {
                method = m;
                break;
            }
        }
        if (method == null) {
            throw new IllegalStateException(String.format("%s Method name is not found for this Class %s", methodName, clazz.toString()));
        }
        return method;
    }

    public static boolean isElementStale(WebElement e){
        try{
            e.isDisplayed();
            return false;
        }catch(StaleElementReferenceException ex){
            return true;
        }
    }


    public void setElementText(WebElement element, String text) {
        TestReporter.logTestStep("Entering text "  + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        clickElementJS(element);
        waitASec(1000);
        element.clear();
        waitASec(1000);
        element.sendKeys(text);
    }

    public void screenShot() throws IOException, InterruptedException {

        TestReporter.logTestStep("Taking a screenshot on " + driver.getCurrentUrl());
        File scr = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);

        File dest = new File("screenshot_" + timestamp() + ".png");
        FileUtils.copyFile(scr, dest);
        Thread.sleep(10000);
    }

    public String timestamp() {
        return new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date());
    }


    protected void WaitForElementToBeVisible(WebElement elementToWait) {
        TestReporter.logTestStep("Waiting for element to be visible" + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
      refreshElement(elementToWait);
        WebElement element = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.visibilityOf(elementToWait));

    }

    protected void WaitForElementToBeClickable(WebElement elementToWait) {
       // TestReporter.logTestStep("Waiting for Element to be clickable" + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        WebElement element = (new WebDriverWait(driver, 10))
                .until(ExpectedConditions.elementToBeClickable(elementToWait));

    }

    public void WriteToReport(String textToLog) {
        TestReporter.logTestStep(textToLog + " (" + GetCallingMethodName() + ").");
    }

    public boolean waitForJSandJQueryToLoad() {
        TestReporter.logTestStep("Waiting for page objects to Load " + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        WebDriverWait wait = new WebDriverWait(driver, 30);

        // wait for jQuery to load
        ExpectedCondition<Boolean> jQueryLoad = driver -> {
            try {
                return ((Long) ((JavascriptExecutor) driver).executeScript("return jQuery.active") == 0);
            } catch (Exception e) {
                // no jQuery present
                return true;
            }
        };

        // wait for Java script to load
        ExpectedCondition<Boolean> jsLoad = driver -> ((JavascriptExecutor) driver).executeScript("return document.readyState")
                .toString().equals("complete");

        return wait.until(jQueryLoad) && wait.until(jsLoad);
    }

    public String GetCallingMethodName() {
        return new Exception().getStackTrace()[2].getMethodName();
    }

    protected void switchToNewWindowAndBack(WebElement element) {
        TestReporter.logTestStep("Switching to new window and back " + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        List<String> browserTabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(1));
        TestReporter.logTestStep("Switching to new window");
        VerifyElementPresent(element);
        driver.close();
        TestReporter.logTestStep("Closed first window and switching back to the first window.. ");
        driver.switchTo().window(browserTabs.get(0));
    }
    protected void switchToNewWindowAndBackCheckUrl(String url) {
        TestReporter.logTestStep("Switching to new window and back " + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        List<String> browserTabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(1));
        TestReporter.logTestStep("Switching to new window");
        verifyCurrentURLContains(url);
        driver.close();
        TestReporter.logTestStep("Closed first window and switching back to the first window.. ");
        driver.switchTo().window(browserTabs.get(0));
    }

    protected void switchToNewWindowAndBack() {
        TestReporter.logTestStep("Switching to new window and back " + " (" + GetCallingMethodName());
        List<String> browserTabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(1));
        TestReporter.logTestStep("Switching to new window");
        driver.close();
        TestReporter.logTestStep("Closed first window and switching back to the first window.. ");
        driver.switchTo().window(browserTabs.get(0));
    }

  /*  protected void verifyResponseCode200(String url) {
        TestReporter.logTestStep("Checking response code is 200 or 302.. " + url);
        Response resp = given().
                contentType(ContentType.JSON).
                when().
                redirects().follow(false)
                .get(url);


        System.out.println(resp.getStatusCode());
        Assert.assertTrue(resp.getStatusCode() == 200 || resp.getStatusCode() == 302 || resp.getStatusCode() == 301);

    }*/

    protected void goBackAPage() {
        TestReporter.logTestStep("Going back a page ");
        driver.navigate().back();
        TestReporter.logTestStep("Now at url: " + "[ " + driver.getCurrentUrl());
    }

    protected void verifyTitleContains(String title) {
        TestReporter.logTestStep("Verifying title contains " + title + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        assertTrue(driver.getTitle().contains(title));
    }

    protected void verifyListOFArticleLinksAndTitle(List<WebElement> listelements) {

        TestReporter.logTestStep("Verifying links of articles on TOC have the the same title in the article " + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        Map<String, String> hashMap = new HashMap<String, String>();
        for (WebElement link : listelements) {
            hashMap.put(link.getText(), link.getAttribute("href"));
        }

        for (Map.Entry<String, String> elements : hashMap.entrySet()) {
            driver.navigate().to(elements.getValue());
            verifyTitleContains(elements.getKey());
        }
    }

    public static void waitForAjax(WebDriver driver, String action) {
        TestReporter.logTestStep("Waiting for ajax response to complete: " + action);
        driver.manage().timeouts().setScriptTimeout(5, TimeUnit.SECONDS);
        ((JavascriptExecutor) driver).executeAsyncScript(
                "var callback = arguments[arguments.length - 1];" +
                        "var xhr = new XMLHttpRequest();" +
                        "xhr.open('POST', '/" + action + "', true);" +
                        "xhr.onreadystatechange = function() {" +
                        "  if (xhr.readyState == 4) {" +
                        "    callback(xhr.responseText);" +
                        "  }" +
                        "};" +
                        "xhr.send();");
    }

    public boolean IsElementDisplayed(WebElement element) {
        TestReporter.logTestStep("Verifying if element is displayed: " + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            //If the element is present at all we get the NoSuchElementExpcetion and return false;
            return false;
        }

    }

    public void verifyElementIsNotDisplayed(WebElement element) {
        TestReporter.logTestStep("Verifying element is not displayed  " + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        assertFalse(IsElementDisplayed(element), "Element Is Displayed");
    }

    public void selectElementFromDropdownByText(WebElement element, String s) {
        TestReporter.logTestStep("Selecting element from dropdown with the text:  " + s + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        Select dropdown = new Select(element);
        dropdown.selectByVisibleText(s);
    }

    public void VerifyListOfArticleSize(List list, int i) {
        Assert.assertTrue(list.size() == i);
    }

    public void selectElement(WebElement element) {

        if (!element.isSelected()) {
            clickElement(element);
        }
    }

    public void clickRandomElementInList(List<WebElement> element) {
        try{
        if (element.size() == 0) {
            clickElement(element.get(0));
        } else {
            WebElement random = element.get(new Random().nextInt(element.size()));
            clickElement(random);
        }
    } catch (IndexOutOfBoundsException e) {
            throw new SkipException("List Empty");
        }

    }

    public boolean makeSureDateIsWithinRange(Date min, Date max, Date current) {
        TestReporter.logTestStep("Article URL " + driver.getCurrentUrl());
        TestReporter.logTestStep("Article creation date  is " + current.toString());
        return current.after(min) && current.before(max);

    }

    public String getAtomUrl() {
        WebElement metaDate = driver.findElement(By.xpath("//meta[@name = 'HW.identifier']"));
        String atompath = "http://sassfs.highwire.org/" + metaDate.getAttribute("content");
        return atompath;
    }

    public Date getAtomDateCreated() {
        String articleCreationDate;
        Elements a;
        Date date = null;
        org.jsoup.nodes.Document doc = null;

        try {
            doc = Jsoup.parse(new URL(getAtomUrl()).openStream(), "UTF-8", "", Parser.xmlParser());
        } catch (IOException e) {
            e.printStackTrace();
        }

        a = doc.select("nlm|pub-date[pub-type=epub]");
        articleCreationDate = a.attr("hwp:start");
        if(!articleCreationDate.equals("")){
            try {
            	//	String tmp = new SimpleDateFormat("dd/MM/yyyy").format((articleCreationDate.toString().substring(0,10)));
            	date = convertDate(articleCreationDate.toString().substring(0,10));
                //date = formatter.parse(articleCreationDate.toString().substring(0,10));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            a = doc.select("nlm|pub-date[pub-type=epub-version]");
            articleCreationDate = a.attr("hwp:start");
            date = Date.from(ZonedDateTime.parse(articleCreationDate, DateTimeFormatter.ISO_DATE_TIME).toInstant());
        }
        System.out.println(articleCreationDate);       
        return date;
    }
    
    public Date convertDate(String dateString) throws ParseException {
        System.out.println("Given date is " + dateString); 
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = sdf.parse(dateString);  
        return date;
   }

    public void verifyAtomValues(String atomXpath, String expected) {
        DocumentBuilderFactory domFactory = DocumentBuilderFactory
                .newInstance();
        domFactory.setNamespaceAware(true);
        DocumentBuilder builder = null;
        try {
            builder = domFactory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;
        try {
            assert builder != null;
            doc = builder.parse(new InputSource(new URL(getAtomUrl()).openStream()));
        } catch (SAXException | IOException e) {
            e.printStackTrace();
        }
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext(new NamespaceContext() {

            @Override
            public Iterator getPrefixes(String arg0) {
                return null;
            }

            @Override
            public String getPrefix(String arg0) {
                return null;
            }

            @Override
            public String getNamespaceURI(String arg0) {
                if ("atom".equals(arg0)) {
                    return "http://www.w3.org/2005/Atom";
                }
                if ("nlm".equals(arg0)) {
                    return "http://schema.highwire.org/NLM/Journal";
                }
                return null;
            }
        });
        // XPath Query for showing all nodes value

        try {
            XPathExpression expr = xpath
                    .compile(atomXpath);
            Object result = expr.evaluate(doc, XPathConstants.NODESET);
            NodeList nodes = (NodeList) result;
            System.out.println("Got " + nodes.getLength() + " nodes");
            String nodetext = nodes.item(0).getTextContent();
            System.out.println(nodetext);
            Assert.assertTrue(nodetext.contains(expected));
        } catch (Exception E) {
            System.out.println(E);
        }
    }

    public void selectAnOptionFromList(List<WebElement> elements, String option) {

        for (WebElement e : elements) {
            System.out.println(e.getText());
            if (e.getText().contains(option)) {
                clickElement(e);
                break;
            }
        }
    }



    public void clickElementinListWithText(List<WebElement> listOfElements, String s) {
        for (int i = 0; i < listOfElements.size(); i++) {
            if (listOfElements.get(i).getText().toLowerCase().contains(s.toLowerCase())) {
                clickElementJS(listOfElements.get(i));
                break;
            }
        }
    }

    public boolean isElementInListWithText(List<WebElement> listOfElements, String s) {
        TestReporter.logTestStep("Checking if " + s + " is in list");
        boolean elementpresent = false;
        for (int i = 0; i < listOfElements.size(); i++) {
            System.out.println(listOfElements.get(i).getText().toLowerCase());
            if (listOfElements.get(i).getText().toLowerCase().contains(s.toLowerCase())) {
                elementpresent = true;
            }
        }
        return elementpresent;
    }
    public boolean isDateValidFormat(String format, String value) {
        Date date = null;
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            date = sdf.parse(value);
            System.out.println(value);
            System.out.println(sdf.format(date));
            if (!value.equals(sdf.format(date))) {
                date = null;
            }
        } catch (ParseException ex) {
            ex.printStackTrace();
        }
        return date != null;
    }
    public void verifyListLessThan(List<WebElement> listOfElements,int expectedSize) {
        Assert.assertTrue(listOfElements.size()<= expectedSize,"List not less than " + expectedSize);
    }

    public void verifyBackToTopLink() {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        Long value = (Long) executor.executeScript("return window.pageYOffset;");
        assertTrue(value == 0);
    }
    public void hideIFrames(){
        JavascriptExecutor jse = (JavascriptExecutor) driver;
       List <WebElement> iframes = driver.findElements(By.tagName("iframe"));
       for (WebElement element: iframes){
           jse.executeScript("arguments[0].style.visibility='hidden'", element);

       }
    }
   /* public void downloadFileChecks(String url, String contentType) throws Exception {
        Reporter.log("Checking " + url + "citation download is not empty");
        RestAssured.baseURI = url;
        RequestSpecification httpRequest = RestAssured.given().auth().preemptive().basic(TestRunSettings.GetDdtUsername(), TestRunSettings.GetDdtPass());
        Response response = httpRequest.get("");

        // Get the headers and then check if the file is downloading by g that it is not empty
        String resContentLength = response.header("Content-Length");
        String resContentType = response.header("Content-Type");

        Assert.assertNotEquals(resContentLength, "0", "Checking that he content length is greater than 0 but is " + resContentLength);
        Assert.assertTrue(resContentType.contains(contentType), "The response does not contain the content type " + contentType);

        System.out.println(contentType + " received" );
    }*/
    public boolean switchToNewWindowCheckUrlAndNavigateBack(String url) {
    	boolean isUrlPresent = false;
       // TestReporter.logTestStep("Switching to new window and back " + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        List<String> browserTabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(1));
        TestReporter.logTestStep("Switching to new window");
        if(driver.getCurrentUrl().contains(url)){
        	isUrlPresent = true;
        }
        driver.close();
        TestReporter.logTestStep("Closed current window and switching back to the previous window.. ");
        driver.switchTo().window(browserTabs.get(0));
        return isUrlPresent;
    }
    
    public boolean switchingToNewWindow(String url) {
    	boolean isUrlPresent = false;
    	TestReporter.logTestStep("Switching to new window " + " (" + GetCallingMethodName() + "). [Current URL: " + driver.getCurrentUrl() + "]");
        List<String> browserTabs = new ArrayList<String>(driver.getWindowHandles());
        driver.switchTo().window(browserTabs.get(1));
        TestReporter.logTestStep("Switching to new window");
        if(driver.getCurrentUrl().contains(url)){
        	isUrlPresent = true;
        }       
        return isUrlPresent;
    }
    
           
    public int getNoOfFilesInDownloads() {
    int filecount = 0;
	try{
		String home = System.getProperty("user.home");
		File Folder = new File(home+"/Downloads/");
		File[] listofFiles = Folder.listFiles();
		filecount = listofFiles.length;
		System.out.println("No.of files in download folder are: " + filecount);
	}
	catch(Exception ex){
		ex.printStackTrace();
		}
		return filecount;
    }
    
    public boolean openinNewTab(WebElement ele){
    	boolean istabOpened = false;
    	try{
    		List<String> beforebrowserTabs = new ArrayList<String>(driver.getWindowHandles());
    		Actions newTab = new Actions(driver); 
    		newTab.keyDown(Keys.CONTROL).click(ele).keyUp(Keys.CONTROL).build().perform();
    		List<String> afterbrowserTabs = new ArrayList<String>(driver.getWindowHandles());
    		if(afterbrowserTabs.size()==beforebrowserTabs.size()-1){
    			istabOpened = true;
    		}
    	}
    	catch(Exception ex){
    		ex.printStackTrace();
    	}
		return istabOpened;
    }
    
    public String getCurrentWindowID(){
		try{
			String winId = driver.getWindowHandle();
			return winId;			
		}catch(Exception e){
			return null;
		}
		
	}
	
	public Set<String> getAllWindowIDs(){
		try{
			return driver.getWindowHandles();
		}catch(Exception e){
			return null;
		}		
	}
	
	public void switchToNewWindow(Set<String> alreadyOpenedWinIDs){
		try{
			
			Set<String> availableWindows = driver.getWindowHandles();
			
			for(String winId: availableWindows){
				if(!alreadyOpenedWinIDs.contains(winId)){
					driver.switchTo().window(winId);
				}
			}
			waitForPageLoad();
		}catch(Exception e){
			e.printStackTrace();
		}
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
