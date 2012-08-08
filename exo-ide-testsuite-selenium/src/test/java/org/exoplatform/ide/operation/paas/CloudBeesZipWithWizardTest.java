package org.exoplatform.ide.operation.paas;

import com.gargoylesoftware.htmlunit.History;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;

import com.gargoylesoftware.htmlunit.History;
import com.gargoylesoftware.htmlunit.Page;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.javascript.background.JavaScriptJobManager;
import com.gargoylesoftware.htmlunit.WebWindow;
import org.exoplatform.ide.BaseTest;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.jsp.JspTagsTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;




public class CloudBeesZipWithWizardTest extends BaseTest
{
   private static final String PROJECT = JspTagsTest.class.getSimpleName();
   private static final String FOLDER_NAME = JspTagsTest.class.getSimpleName();
   
  
   
   
   @Test
   public void callForm() throws Exception
   {
      IDE.MENU.runCommand(MenuCommands.Project.PROJECT, MenuCommands.Project.NEW, MenuCommands.Project.CREATE_PROJECT);
      IDE.PROJECTMENU.waitOpened();
      IDE.PROJECTMENU.waitForProjectAppear("Java Web Project");
      IDE.PROJECTMENU.selectPoject("Java Web Project");
      IDE.PROJECTMENU.nextBtnClick();
      IDE.DEPLOY.waitOpened();
      IDE.DEPLOY.collapsePaasList();
      IDE.DEPLOY.waitPaasListOpened("CloudBees");
      IDE.DEPLOY.selectAndClickOnPaasInList("CloudBees");
      
     
      
      Thread.sleep(3000);
      
     
   }
   
}
