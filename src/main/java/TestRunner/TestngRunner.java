package TestRunner;


	import java.io.FileNotFoundException;
	import java.io.IOException;
	import java.util.List;
	import javax.xml.parsers.ParserConfigurationException;
	import org.testng.xml.Parser;
	import org.testng.xml.XmlSuite;
	import org.testng.TestNG;
	import org.xml.sax.SAXException;

	public class TestngRunner{

		public static void main(String[] args) throws FileNotFoundException, ParserConfigurationException, SAXException, IOException {
			TestNG testng = new TestNG(); 
			testng.setXmlSuites((List <XmlSuite>)(new Parser("C:\\Users\\bkovuri\\git\\Cochrane QA2\\Cochrane-automation-grid\\AllSuites.xml").parse()));		
			testng.setSuiteThreadPoolSize(5);
			testng.run();
	    }
	}

