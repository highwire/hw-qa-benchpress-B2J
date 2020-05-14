package TestUtils;

import java.util.logging.Logger;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.UnreachableBrowserException;

public class BrowserDriver {

	private static final Logger LOGGER = Logger.getLogger(BrowserDriver.class.getName());

	private  WebDriver mDriver;
	
	public synchronized WebDriver getCurrentDriver() {


		if (mDriver == null) {
			BrowserFactory factory = new BrowserFactory();
			try {
				mDriver = factory.getBrowser();
			} catch (UnreachableBrowserException e) {
				e.printStackTrace();

			} catch (WebDriverException e) {
				e.printStackTrace();

			} finally {
				//Runtime.getRuntime().addShutdownHook(new Thread(new BrowserCleanup(this)));
			}
		}
		return mDriver;
	}

	public void close() {
		try {
			getCurrentDriver().quit();
			mDriver = null;
			LOGGER.info("closing the browser");
		} catch (UnreachableBrowserException e) {
			LOGGER.info("cannot close browser: unreachable browser");
		}
	}

	private static class BrowserCleanup implements Runnable {
		BrowserDriver bDriver;
		public BrowserCleanup(BrowserDriver browserDriver) {
			 bDriver = browserDriver;
		}

		public void run() {
			bDriver.close();
		}
	}

}
