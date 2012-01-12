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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for creating template from big file.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class BigTemplateTest extends BaseTest
{
   private final static String FILE_NAME = "Calculator.xml";

   private final static String TEMPLATE_NAME = "Calc";

   private final static String FOLDER = BigTemplateTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/Calculator.xml";
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.GOOGLE_GADGET, URL + FILE_NAME);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testBigTemplate() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL);
      IDE.WORKSPACE.selectRootItem();
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER + "/");
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER + "/" + FILE_NAME);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER + "/" + FILE_NAME, false);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE);
      IDE.SAVE_AS_TEMPLATE.waitOpened();
      assertTrue(IDE.SAVE_AS_TEMPLATE.isOpened());
      IDE.SAVE_AS_TEMPLATE.setName(TEMPLATE_NAME);
      IDE.SAVE_AS_TEMPLATE.clickSaveButton();
      IDE.SAVE_AS_TEMPLATE.waitClosed();

      IDE.INFORMATION_DIALOG.waitOpened("Template created successfully!");

      //click OK button
      IDE.INFORMATION_DIALOG.clickOk();
      IDE.INFORMATION_DIALOG.waitClosed();

      IDE.EDITOR.closeFile(0);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      IDE.TEMPLATES.waitOpened();

      // check "Create file" dialog window
      assertTrue(IDE.TEMPLATES.isOpened());
      IDE.TEMPLATES.selectTemplate(TEMPLATE_NAME);
      //click Create button
      IDE.TEMPLATES.clickCreateButton();
      IDE.TEMPLATES.waitClosed();
      
      IDE.EDITOR.waitTabPresent(0);

      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      IDE.EDITOR.closeTabIgnoringChanges(0);
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL);
      }
      catch (IOException e)
      {
      }
   }
}
