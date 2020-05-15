package TestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.logging.LogType;
import org.openqa.selenium.logging.LoggingPreferences;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;

public class BrowserFactory {

	private static final Logger LOGGER = Logger.getLogger(BrowserFactory.class.getName());
	static String USER_DIR = System.getProperty("user.dir");
	private static Properties config = null;
	WebDriver driver;
	private static Properties OBJECT_REPOSITORY = null;
	private static String OS = null;
	private static String nodeURL = null;
	
	public static String getOS() {
		return OS;
	}

	private  String orPath = null;
	private  String CONFIG_PATH = null;
	public  String browser = null;

	public  Properties getConfig() {
		return config;
	}
	

	public  Properties getOBJECT_REPOSITORY() {
		return OBJECT_REPOSITORY;
	}

	public  void setOBJECT_REPOSITORY(Properties oBJECT_REPOSITORY) {
		OBJECT_REPOSITORY = oBJECT_REPOSITORY;
	}

	/**
	 * creates the browser driver specified in the system property "browser" if
	 * no property is set then a firefox browser driver is created. The allow
	 * properties are firefox, safari and chrome.
	 * @return WebDriver
	 */

	public  WebDriver getBrowser() {

		initConfig();

		browser = config.getProperty("BROWSER");
		nodeURL = config.getProperty("NodeURL");

		switch (browser.toLowerCase()) {

		case "chrome":
			LOGGER.log(Level.INFO, "Create chrome driver function called");
			driver = createChromeDriver();
			break;
		case "ie32":
			LOGGER.log(Level.INFO, "Trying to open IE32 driver");
			driver = createIE32Driver();
			break;
		case "ie64":
			LOGGER.log(Level.INFO, "Trying to open IE64 driver");
			driver = createIE64Driver();
			break;
		case "safari":
			LOGGER.log(Level.INFO, "Trying to open Safari driver");
			driver = createSafariDriver();
			break;
		case "firefox":
			driver = createFirefoxDriver();
			break;
		case "phantomjs":
			driver = createPhantomJSDriver();
			break;
		
		default:
			LOGGER.log(Level.INFO, "Trying to open Firefox driver");
			driver = createFirefoxDriver();
			break;
		}
		addAllBrowserSetup(driver);
		return driver;
	}

	public  void initConfig() {
		config = new Properties();
		OS = System.getProperty("os.name");
	//Set path for Config.properties 
		if (OS.toLowerCase().contains("windows")) {
			CONFIG_PATH = "\\src\\main\\java\\TestProperties\\Config.properties";
		} else {
			CONFIG_PATH = "/src/main/java/TestProperties/Config.properties";
		}
		
		File file = new File(USER_DIR + CONFIG_PATH);

		FileInputStream fileInput = null;
		try {
			fileInput = new FileInputStream(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
			try {
			config.load(fileInput);
		} catch (IOException e) {

			e.printStackTrace();
		}
			
		//Set path for OR.properties 

		if (OS.toLowerCase().contains("windows")) {
			orPath = "\\src\\main\\java\\TestProperties\\OR.properties";
		} else{
			orPath = "/src/main/java/TestProperties/OR.properties";
		}
		
		OBJECT_REPOSITORY = new Properties();
		String path = System.getProperty("user.dir");
		File fileOr = new File(path + orPath);

		FileInputStream orFileInput = null;
		try {
			orFileInput = new FileInputStream(fileOr);
			OBJECT_REPOSITORY.load(orFileInput);
		} catch (IOException ioException) {
			LOGGER.log(Level.SEVERE, "Exception occured during loading of object repository");
			ioException.printStackTrace();
		}

	}

	private  WebDriver createPhantomJSDriver() {

		String PHANTOMJS_DRIVER_PROP = "phantomjs.binary.path";
		String PATH_PHANTOM_DRIVER_EXE = "\\src\\main\\java\\TestResources\\phantomjs.exe";

		System.setProperty(PHANTOMJS_DRIVER_PROP, USER_DIR + PATH_PHANTOM_DRIVER_EXE);
		driver = new PhantomJSDriver();

		LOGGER.log(Level.INFO, "Trying to open PhantomJS driver");
		return driver;
	}

	private  WebDriver createFirefoxDriver() {
		String GECKO_DRIVER_PROP = "webdriver.gecko.driver";
		String PATH_GECKO_DRIVER = "/src/main/java/TestResources/geckodriver";

		if (OS.toLowerCase().contains("windows")) {
			PATH_GECKO_DRIVER = "\\src\\main\\java\\TestResources\\geckodriver.exe";
		} else if (OS.toLowerCase().contains("mac")) {
			PATH_GECKO_DRIVER = "/src/main/java/TestResources/geckodriver";
		}
		
		System.setProperty(GECKO_DRIVER_PROP, USER_DIR + PATH_GECKO_DRIVER);
			
		/*DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setBrowserName("firefox");
		capabilities.setCapability("os", "Windows");
		capabilities.setCapability("os_version", "10");
		capabilities.setCapability("browser", "Firefox");*/
		
		String downloadPath = System.getProperty("user.home")+"/Downloads/";
		
		String mimeTypes = "application/txt;charset=utf-8, application/txt, application/zip, application/javascript, application/octet-stream, image/jpeg, application/vnd.ms-outlook, text/plain, plain/text, text/html, image/png, text/txt, text/csv, text/comma-separated-values, application/download, application/vnd.ms-excel, application/pdf, application/vnd.ms-powerpoint, application/csv, binary/octet-stream, application/binary, application/ris, application/x-msexcel, application/excel, application/x-excel, application/msword, application/xml";
		
		FirefoxProfile profile = new FirefoxProfile();
		profile.setPreference("browser.download.dir", downloadPath);
		profile.setPreference("browser.download.folderList", 2);
		profile.setPreference("browser.download.manager.showWhenStarting", false);
		profile.setPreference("browser.helperApps.alwaysAsk.force", false);
		profile.setPreference("browser.helperApps.neverAsk.saveToDisk", mimeTypes);
		profile.setPreference("browser.helperApps.neverAsk.openFile", mimeTypes);
		//profile.setPreference("browser.helperApps.neverAsk.saveToDisk", "plain/text;, text/plain;, text/txt;, txt/text;,text/csv;");
		profile.setPreference("browser.download.manager.focusWhenStarting",false);
		profile.setPreference("browser.download.manager.useWindow", false);
		profile.setPreference("browser.download.manager.showAlertOnComplete", false);
		
		DesiredCapabilities capabilities = DesiredCapabilities.firefox();
		capabilities.setCapability(FirefoxDriver.PROFILE, profile);
		//capabilities.setCapability(CapabilityType.ELEMENT_SCROLL_BEHAVIOR, 1);
		/*profile.setPreference("dom.popup_maximum", 0);
		profile.setPreference("privacy.popups.showBrowserMessage", false);
		profile.setPreference("dom.disable_open_during_load", false);*/
		//capabilities.setCapability("firefox_profile", profile);
		LOGGER.info("Remote Webdriver capabilities set");
		try {
			driver = new RemoteWebDriver(new URL("http://localhost:5555/wd/hub"), capabilities);
			LOGGER.info("Remote WebDriver created");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		LOGGER.log(Level.INFO, "Trying to open Firefox driver");
		return driver;		
	}

	private static WebDriver createSafariDriver() {
		return new SafariDriver();
	}

	private  WebDriver createChromeDriver() {
		String CHROME_DRIVER_PROP = "webdriver.chrome.driver";
		String PATH_CHROME_DRIVER = "\\src\\main\\java\\TestResources\\chromedriver.exe";

		if (OS.toLowerCase().contains("windows")) {
			PATH_CHROME_DRIVER = "\\src\\main\\java\\TestResources\\chromedriver.exe";
		} else if (OS.toLowerCase().contains("mac")) {
			PATH_CHROME_DRIVER = "/src/main/java/TestResources/chromedriver";
		}
		
		System.setProperty(CHROME_DRIVER_PROP, USER_DIR + PATH_CHROME_DRIVER);
		/*DesiredCapabilities capabilities = DesiredCapabilities.chrome();*/
		/*capabilities.setBrowserName("chrome");*/
		LOGGER.info("Remote Webdriver capabilities set");
		try {
			ChromeOptions options = new ChromeOptions();
			//options.addArguments("--start-maximized");
			DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setCapability(ChromeOptions.CAPABILITY, options);
			capabilities.setJavascriptEnabled(true);
			LoggingPreferences logPreferences = new LoggingPreferences();
			logPreferences.enable(LogType.BROWSER, Level.ALL);
			capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPreferences);
			driver = new RemoteWebDriver(new URL("http://localhost:5555/wd/hub"), capabilities);
			/*DesiredCapabilities capabilities = DesiredCapabilities.chrome();
			capabilities.setJavascriptEnabled(true);
			LoggingPreferences logPreferences = new LoggingPreferences();
			logPreferences.enable(LogType.BROWSER, Level.ALL);
			capabilities.setCapability(CapabilityType.LOGGING_PREFS, logPreferences);
			driver =  new RemoteWebDriver(new URL("http://localhost:4444/wd/hub"),capabilities);*/
			LOGGER.info("Remote WebDriver created");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		LOGGER.log(Level.INFO, "Trying to open Chrome driver");
		return driver;
	}


	private static WebDriver createIE32Driver() {
		DesiredCapabilities capabilities = new DesiredCapabilities();
		capabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
		String IE_DRIVER_PROP = "webdriver.ie.driver";
		String PATH_IE_DRIVER_EXE = "\\src\\main\\java\\TestResources\\IEDriverServer.exe";
		System.setProperty(IE_DRIVER_PROP, USER_DIR + PATH_IE_DRIVER_EXE);
		return new InternetExplorerDriver();
	}

	private static WebDriver createIE64Driver() {

		String IE_DRIVER_PROP = "webdriver.ie.driver";
		String PATH_IE_DRIVER_EXE = "\\src\\main\\java\\TestResources\\IEDriverServer64.exe";

		System.setProperty(IE_DRIVER_PROP, USER_DIR + PATH_IE_DRIVER_EXE);
		return new InternetExplorerDriver();

	}

	private static void addAllBrowserSetup(WebDriver driver) {
		driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
		//driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
	}

}
