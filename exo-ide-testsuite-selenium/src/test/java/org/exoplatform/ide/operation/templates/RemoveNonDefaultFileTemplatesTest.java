/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.exoplatform.ide.operation.templates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RemoveNonDefaultFileTemplatesTest extends BaseTest
{
   private final static String FILE_NAME = "HtmlTemplate.html";

   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + FILE_NAME;
   
   private static final String TEMPLATE_NAME = "test template";
   
   @BeforeClass
   public static void setUp()
   {
      
      String filePath ="src/test/resources/org/exoplatform/ide/operation/templates/HtmlTemplate.html";
      try
      {
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
   
   @AfterClass
   public static void tearDown()
   {
      cleanRegistry();
      try
      {
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
   
   
   //IDE-163:Remove non-default file templates
   @Test
   public void testRemoveNonDefaultFileTemplates() throws Exception
   {
      //---- 1-3 -----------------
      //open file with text
      Thread.sleep(TestConstants.SLEEP);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      
      // -------3--------
      //Click on "File -> Save As Template..." on topmenu item. 
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE);
      Thread.sleep(TestConstants.SLEEP);
      // check Save As Template window
      TemplateUtils.checkSaveAsTemplateWindow(selenium);
      
      // --------4-------------------
      //Type "test template" in name field, and click on button "Save".
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormNameField||title=ideSaveAsTemplateFormNameField||index=3||Class=TextItem]/element",
            TEMPLATE_NAME);
      selenium.click("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]");
      // check template created dialog window
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
      assertTrue(selenium.isTextPresent("Info"));
      assertTrue(selenium.isTextPresent("Template created successfully!"));
      
      // --------5-------------
      //Click "OK" button in info dialog.
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton");
      Thread.sleep(TestConstants.SLEEP);
      
      // --------6--------
      //Click on "File->New->From Template..." topmenu item.
      runCommandFromMenuNewOnToolbar(MenuCommands.New.FILE_FROM_TEMPLATE);
      Thread.sleep(TestConstants.SLEEP);
      
      // check "Create file" dialog window
      TemplateUtils.checkCreateFileFromTemplateWindow(selenium);
      
      // --------7--------
      // In "Create file"  window select "test template", then click "Delete" button.
      TemplateUtils.selectItemInTemplateList(selenium, TEMPLATE_NAME);
      
      //click Delete button
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/");
      // check warning dialog appeared
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      
      // ------8-------
      //Click on button "Yes".
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      
      // must shown dialog that informs that template was deleted.
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/"));
      assertTrue(selenium.isTextPresent("Template test template deleted."));
      
      // -------9---------
      // Click on button "Ok".
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      
      //"Create file" window should contain only default("red") templates.
      assertFalse(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[text()='" + TEMPLATE_NAME + "']"));
      
      // -------10-------
      // Close "Create file" window, and all opened tabs in content panel.
      selenium
         .click("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/member[Class=Canvas||index=0||length=3||classIndex=0||classLength=1]/");
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/");
      closeTab("0");
      Thread.sleep(TestConstants.SLEEP);
   }
}