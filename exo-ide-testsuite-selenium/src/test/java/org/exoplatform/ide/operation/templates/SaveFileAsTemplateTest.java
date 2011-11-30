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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class SaveFileAsTemplateTest extends BaseTest
{
   private final static String PROJECT = SaveFileAsTemplateTest.class.getSimpleName();
   
   private static final String FILE_NAME = "RestServiceTemplate.groovy";

   private static final String REST_SERVICE_TEMPLATE_NAME = "test REST template";
   
   private static final String REST_SERVICE_TEMPLATE_DESCRIPTION = "test REST Service template description";
   
   private static final String REST_SERVICE_FILE_NAME = "abc";
   
   private static final String TEXT = "// test groovy file template";
   
   /**
    * File, where users templates are stored.
    */
   public static final String FILE_TEMPLATES_STORE = ENTRY_POINT_URL + WS_NAME_2 + "/ide-home/templates/fileTemplates";
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         final String filePath ="src/test/resources/org/exoplatform/ide/operation/templates/RestServiceTemplate.groovy";
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GROOVY_SERVICE, filePath);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
         VirtualFileSystemUtils.delete(FILE_TEMPLATES_STORE);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testSaveFileAsTemplate() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      
      //-------- 1 ----------
      //open file with text
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);
      
      //--------- 2 --------
      //Click on "File->Save As Template" top menu item, 
      //set "Name" field on "test REST template", 
      //"Description" field on "test REST Service template description", and then click on "Save" button.
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE));
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE);
      IDE.SAVE_AS_TEMPLATE.waitOpened();
      // check "Save file as template" dialog window
      assertTrue(IDE.SAVE_AS_TEMPLATE.isOpened());
      
      //check save button disabled
      assertFalse(IDE.SAVE_AS_TEMPLATE.isSaveButtonEnabled());
      
      //type some text to name field
      IDE.SAVE_AS_TEMPLATE.setName("a");
      assertTrue(IDE.SAVE_AS_TEMPLATE.isSaveButtonEnabled());
      
      //remove text from name field
      IDE.SAVE_AS_TEMPLATE.setName("");
      assertFalse(IDE.SAVE_AS_TEMPLATE.isSaveButtonEnabled());
      
      IDE.SAVE_AS_TEMPLATE.setName(REST_SERVICE_TEMPLATE_NAME);
      IDE.SAVE_AS_TEMPLATE.setDescription(REST_SERVICE_TEMPLATE_DESCRIPTION);
      IDE.SAVE_AS_TEMPLATE.clickSaveButton();
      IDE.SAVE_AS_TEMPLATE.waitClosed();
      
      IDE.INFORMATION_DIALOG.waitOpened("Template created successfully!");
      IDE.INFORMATION_DIALOG.clickOk();
      IDE.INFORMATION_DIALOG.waitClosed();
      
      //------------ 3 ----------
      //Click on "New->From Template" button and then click on "test groovy template" item.
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      IDE.TEMPLATES.waitOpened();
      
      // check "Create file" dialog window
      assertTrue(IDE.TEMPLATES.isOpened());
      IDE.TEMPLATES.selectTemplate(REST_SERVICE_TEMPLATE_NAME);
      IDE.TEMPLATES.waitForNameFieldEnabled();
      
      //------------ 4 ----------
      //Change "File Name.groovy" field text on "Test Groovy File.groovy" name, click on "Create" button.
      IDE.TEMPLATES.setFileName(REST_SERVICE_FILE_NAME);
      //click Create button
      IDE.TEMPLATES.clickCreateButton();
      IDE.TEMPLATES.waitClosed();
      IDE.EDITOR.waitTabPresent(2);
      //there should be new tab with title "Test Groovy File.groovy", 
      //first line "// test groovy file template" in content and with "Groovy" 
      //highlighting opened in the Content Panel.
      assertEquals(REST_SERVICE_FILE_NAME + " *", IDE.EDITOR.getTabTitle(2));
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(TEXT));
   }
   
}