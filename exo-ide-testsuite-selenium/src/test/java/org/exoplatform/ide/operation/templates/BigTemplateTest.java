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

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class BigTemplateTest extends BaseTest
{
   private final static String FILE_NAME = "Calculator.xml";

   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + FILE_NAME;

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/Calculator.xml";
      try
      {
         VirtualFileSystemUtils.put(filePath, MimeType.GOOGLE_GADGET, URL);
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

   @Test
   public void testBigTemplate() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);

      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE);
      
      TemplateUtils.checkSaveAsTemplateWindow(selenium);

      // ----------------------------
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideSaveAsTemplateFormDynamicForm\"]/item[name=ideSaveAsTemplateFormNameField||title=ideSaveAsTemplateFormNameField||index=3||Class=TextItem]/element",
            "Calc");
      // --------3--------------
      selenium.click("scLocator=//IButton[ID=\"ideSaveAsTemplateFormSaveButton\"]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      //check info dialog, that template crated successfully
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/header/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/"));
      assertTrue(selenium.isTextPresent("Template created successfully!"));

      //click OK button
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton");
      Thread.sleep(TestConstants.SLEEP);

      closeTab("0");

      createFileFromToolbar(MenuCommands.New.FROM_TEMPLATE);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      // check "Create file" dialog window
      TemplateUtils.checkCreateFileFromTemplateWindow(selenium);
      TemplateUtils.selectItemInTemplateList(selenium, "Calc");
      //click Create button
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);

      closeUnsavedFileAndDoNotSave("0");
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         cleanRegistry();
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
}
