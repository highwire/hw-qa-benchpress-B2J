package PageObjects;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;

import TestRunner.TestReporter;
import TestUtils.TestUtil;

public class TestMethods extends TestUtil{
	public Properties CONFIG_REPO = null;	
	public TestMethods(){
		CONFIG_REPO = getBrowserFactory().getConfig();
	}
	
	public boolean Login() {
		boolean result = false;
		try{
		waitToBeVisible("LOGIN_USER_NAME_FIELD", 60);
		String username = CONFIG_REPO.getProperty("USER_ID");
		String password = CONFIG_REPO.getProperty("PASSWORD");
		input("LOGIN_USER_NAME_FIELD", username);
	    input("LOGIN_PASSWORD_FIELD", password);
		click("LOGIN_SUBMIT_BUTTON");
		wait(2000);
		waitForJSandJQueryToLoad();
		String userLogin = getElementByIdentifier("LOGIN_USER_DETAILS").getText();
		if (userLogin.contains(CONFIG_REPO.getProperty("USER_NAME"))) {
			TestReporter.logTestStep("Login successful");
			result = true;
		} else {
			TestReporter.logTestStep("Login Failed");
			throw new Exception("Failed to login to Bench Press site");
			
		}
		}
		catch(Exception ex){
			ex.printStackTrace();
			return result = false;
		}
		return result;
	}
	
	public String submitAJournal(){
		String manuScriptID = "";
		try{
		click("SUBMIT_PREPRINT_PAPER_LINK");
		wait(2000);
		String JournalName = "";
		 if (System.getProperty("PrePrintJournal") == null) {
			 JournalName = System.getProperty("PrePrintJournal");
		 }
		 else{
			 JournalName = CONFIG_REPO.getProperty("PREPRINT_JOURNAL");
		 }
		int noofManuScripts = getElementsByIdentifier("DROPDOWN_JOURNAL").size();
		List<WebElement> dropdownselect = getElementsByIdentifier("COMBO_BOX_OPTION_VALUES");
		List<WebElement> submitBtn = getElementsByIdentifier("COMBO_BOX_OPTION_SUBMIT");
		
		for (int i=0;i<noofManuScripts; i++){		
			int temp = 0;
			List <WebElement> manuScriptDropDown = getElementsByIdentifier("DROPDOWN_JOURNAL");
			Select manuScriptItems = new Select(manuScriptDropDown.get(i));
			List<WebElement> options = manuScriptItems.getOptions();
			 for(WebElement item:options) 
		        { 	        
		             System.out.println("Dropdown values are "+ item.getAttribute("innerText"));
		             if(item.getAttribute("innerText").equals(JournalName)){
		            	 dropdownselect.get(i).sendKeys(JournalName);
		            	 submitBtn.get(i).click();	
		            	 temp=1;
		            	 break;
		         }
		        }
			 if(temp!=0)
				 break;
		}
		click("COMPLETE_THIS_ACTION_BUTTON");
		wait(1000);
		String manuscriptDetails = getElementsByIdentifier("MANUSCRIPT_ID_DETAILS_TEXT").get(0).getText();
		System.out.println("Updated Manuscript Details are :"+manuscriptDetails);
		if(!(manuscriptDetails.contains(JournalName) && manuscriptDetails.contains("completed successfully"))){
			throw new Exception("manuscriptDetails doesn't contain the JournalName :"+JournalName);
		}
		manuScriptID = manuscriptDetails.split("For Manuscript ")[1].substring(0,19).trim();
		}
		catch(Exception ex){
			ex.printStackTrace();
		}		
		return manuScriptID;
	}
	
	public boolean verifyJournalForSelectedManuscript(String manuScriptID){
		boolean result = false;
		try{
			String JournalName = "";
			 if (System.getProperty("PrePrintJournal") == null) {
				 JournalName = System.getProperty("PrePrintJournal");
			 }
			 else{
				 JournalName = CONFIG_REPO.getProperty("PREPRINT_JOURNAL");
			 }
			for(int i=0; i<180; i++){				
				Thread.sleep(10000);
				click("LINK_AUTHOR_AREA");
				waitForJSandJQueryToLoad();
				click("SUBMIT_PREPRINT_PAPER_LINK");
				waitForJSandJQueryToLoad();
				try{
					if(getDynamicElementByIdentifier("DD_MANUSCRIPT_ITEMS_MANUID", manuScriptID).getAttribute("name").length()!=0){
						break;
					}								
				}
				catch(Exception ex){					
				}				
			}
			Select manuScriptItems = new Select(getDynamicElementByIdentifier("DD_MANUSCRIPT_ITEMS_MANUID", manuScriptID));
			List<WebElement> options = manuScriptItems.getOptions();
			 for(WebElement item:options) 
		        { 	        
		             System.out.println("Dropdown values are "+ item.getAttribute("innerText"));
		             if(item.getAttribute("innerText").equals(JournalName)){
		            	result = false;
		            	throw new Exception("Dropdown values are "+ item.getAttribute("innerText"));		            	
		             }
		             else{
		            	 System.out.println("The selected Journal name is not listed under listed journals dropdown as expected");
		            	 result = true;
		         }
		        }
		}
		catch(Exception ex){
			ex.printStackTrace();
			result = false;
		}
		return result;
	}
	
	public boolean changeUserRoles(String manuScriptID){
		boolean result = false;
		try{
		click("LINK_AFFILIATE_AREA");
		waitForJSandJQueryToLoad();
		click("LINK_AFFILIATE");
		click("LINK_JOURNAL_STAFF");
		wait(1000);
		waitForJSandJQueryToLoad();
		input("QUICK_SEARCH",manuScriptID);
		click("FIND_IN_QUEUES_BUTTON");
		waitToBeVisible("FULL_MS_INFO_LINK", 60);
		click("FULL_MS_INFO_LINK");
		wait(2000);
		//waitForJSandJQueryToLoad();
		String transHistoryText1 = "";
		 if (System.getProperty("TransText1") == null) {
			 transHistoryText1 = CONFIG_REPO.getProperty("TRANSACTION_HISTORY_DETAILS_TEXT1");
	        } else {
		 transHistoryText1 =  System.getProperty("TransText1");
	        }
		 String transHistoryText2 = "";
		 if (System.getProperty("TransText2") == null) {
			 transHistoryText2 = CONFIG_REPO.getProperty("TRANSACTION_HISTORY_DETAILS_TEXT2");
	        } else {
		 transHistoryText2 =  System.getProperty("TransText2");
	        }
		 String transHistoryText3 = "";
		 if (System.getProperty("TransText3") == null) {
			 transHistoryText3 = CONFIG_REPO.getProperty("TRANSACTION_HISTORY_DETAILS_TEXT3");
	        } else {
		 transHistoryText3 =  System.getProperty("TransText3");
	        }
		 String transHistoryText4 = "";
		 if (System.getProperty("TransText4") == null) {
			 transHistoryText4 = CONFIG_REPO.getProperty("TRANSACTION_HISTORY_DETAILS_TEXT4");
	        } else {
		 transHistoryText4 =  System.getProperty("TransText4");
	        }
		if (getDriver().findElement(By.xpath("//td[contains(text(),'" + transHistoryText1 + "')]")).isDisplayed()) {
			if (getDriver().findElement(By.xpath("//td[contains(text(),'" + transHistoryText1 + "')]/parent::tr/following-sibling::tr[1]/td[1]")).getText().equals(transHistoryText2)
					&& getDriver().findElement(By.xpath("//td[contains(text(),'" + transHistoryText1+ "')]/parent::tr/following-sibling::tr[2]/td[1]")).getText().equals(transHistoryText3)
					&& getDriver().findElement(By.xpath("//td[contains(text(),'" + transHistoryText1+ "')]/parent::tr/following-sibling::tr[3]/td[1]")).getText().equals(transHistoryText4)) {
				result = true;
			}
		}
 	
		/*result = isTextPresentOnPage(transHistoryText1) &&
				 isTextPresentOnPage(transHistoryText2) &&
				 isTextPresentOnPage(transHistoryText3) &&
				 isTextPresentOnPage(transHistoryText4);*/
		if (result) {
			System.out.println("The given transaction details are present on the page for manuscript id:"+manuScriptID);
		} else {
			System.out.println("The given transaction details are missing on the page for manuscript id:"+manuScriptID);
			throw new Exception("The given transaction details are missing on the page for manuscript id:"+manuScriptID);
		}		
		getDynamicElementByIdentifier("VIEW_CORRESPONDENCE_LINK", transHistoryText4).click();
		waitForJSandJQueryToLoad();
		String linkText = "Edit  "+transHistoryText4+" Template";
		result = getDynamicElementByIdentifier("LINK_CORRESPONDENCE_FORJOURNAL", linkText).isDisplayed();
		System.out.println("Correspondence link is displayed correctly for the given manuscript");
		}catch(Exception ex) {
			ex.printStackTrace();
			return result=false;
		}
		return result;
	}
	
	public boolean exportManuscriptReport() {
		boolean result = false;
		try{		
		click("LINK_REPORTS_AREA");
		waitForJSandJQueryToLoad();
		click("LINK_REPORTS_NEW_EXPORTS");
		click("LINK_EXPORT_MANUSCRIPT_DATA");
		System.out.println("Control moved to export manuscript data successfully");
		wait(5000);
		click("MOST_RECENT_DESTINATION_JOURNAL_CHECKBOX");
		wait(1000);
		click("PREPRINT_SENTTO_ANOTHER_JOURNAL_CHECKBOX");
		wait(1000);
		click("DEFINE_CRITERIA_BUTTON");
		wait(3000);
		waitForJSandJQueryToLoad();
		String jCode = "";
		 if (System.getProperty("JournalCode") == null) {
			 jCode = CONFIG_REPO.getProperty("JOURNAL_CODE");
	        } else {
		 jCode =  System.getProperty("JournalCode");
	        } 
		getDynamicElementByIdentifier("JOURNAL_CODE_VALUE", jCode).click();
		click("SELECT_FIELDS_TO_DISPLAY_BUTTON");
		wait(2000);
		waitForJSandJQueryToLoad();
		click("AUTHOR_EMAIL_ADDRESS_CHECKBOX");
		click("AUTHOR_NAME_CHECKBOX");
		String sentToJournal = "flag_sent_to_"+jCode;
		String dateSentToJournal = "flag_sent_to_"+jCode+"_dt";
		getDynamicElementByIdentifier("SENT_TO_JOURNAL_CHECKBOX", sentToJournal).click();
		getDynamicElementByIdentifier("SENT_TO_JOURNAL_CHECKBOX", dateSentToJournal).click();
		click("BEGIN_DATA_EXPORT_BUTTON");
		wait(10000);
		waitForJSandJQueryToLoad();
		for (int i=0; i<10; i++) {
			try{
				if(getElementByIdentifier("DOWNLOAD_MANUSCRIPT_DATA_FILE").isDisplayed()){
					break;
				}								
			}
			catch(Exception ex){
			}
			wait(1000);
			getDriver().navigate().refresh();
		}
		int beforeExportManuscript = getNoOfFilesInDownloads();
		System.out.println("The files in downloads before file export :"+beforeExportManuscript );
		click("DOWNLOAD_MANUSCRIPT_DATA_FILE");
		wait(1000);
		int afterExportManuscript = getNoOfFilesInDownloads();
		System.out.println("The files in downloads after file export :"+afterExportManuscript );
		result = afterExportManuscript == (beforeExportManuscript+1);
		if (result) {
			System.out.println("The manuscript data file is downloaded successfully");
		} else {
			System.out.println("The manuscript data file is not downloaded");
			throw new Exception("The manuscript data file is not downloaded");
		}
		}catch(Exception ex){
			ex.printStackTrace();
			return result = false;
		}
		
		return result;
	}
	
	public String getTextfromLatestDownloads(){
		String text = "";
		String home = System.getProperty("user.home");	
		try {
			text = (getTextfromFile(getTheNewestFile(home+"/Downloads/", "txt")));
			System.out.println(text);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}
	
}	