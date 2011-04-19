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
package org.exoplatform.ide.operation.gadget;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 11, 2010
 *
 */
public class GadgetDevelopmentTest extends BaseTest
{
   /**
    * 
    */
   private static final String FILE_NAME = "Test Gadget File.xml";
   
   private static final String FOLDER_NAME = GadgetDevelopmentTest.class.getSimpleName();
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME + "/";
   
   
   //IDE-78
   @Test
   public void createGadgetFromTemplate() throws Exception
   {

      //      Click on "New->From Template" button.
      VirtualFileSystemUtils.mkcol(URL);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);      
      
      Thread.sleep(TestConstants.SLEEP);
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/headerLabel/"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //    Select "Google Gadget" in the central column, change "File Name" field text on "Test Gadget File" name, click on "Create" button.
      selenium.click("scLocator=//ListGrid[ID=\"ideCreateFileFromTemplateFormTemplateListGrid\"]/body/row[3]/col[1]");

      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium
         .type(
            "scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField||index=0]/element",
            "Test Gadget File");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/");
      Thread.sleep(TestConstants.SLEEP_SHORT);
      assertTrue(selenium.getText("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=1]/title").matches(
         "^Test Gadget File [\\s\\S]*$"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //      Click on "Save As" button and save file "Test Gadget File" with default name.
      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);
      
      assertEquals(HTTPStatus.OK, VirtualFileSystemUtils.get(URL + FILE_NAME).getStatusCode());    
      
      IDE.editor().closeTab(0);

      

      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(FILE_NAME, selenium.getText("scLocator=//TabSet[ID=\"ideEditorFormTabSet\"]/tab[index=0]/title"));
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      //     Remove created folder with file.
      IDE.navigator().deleteSelectedItems();
   }

   @AfterClass
   public static void tearDown()
   {
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }

}