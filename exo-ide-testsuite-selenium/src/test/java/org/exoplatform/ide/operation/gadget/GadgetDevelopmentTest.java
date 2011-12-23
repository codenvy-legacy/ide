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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Response;
import org.exoplatform.ide.core.Templates;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.URLEncoder;

/**
 * Test for creating gadget from template.
 * 
 * @author <a href="mailto:roman.iyvshyn@exoplatform.com">Roman Iyvshyn</a>
 * @version $Id: Aug 11, 2010
 *
 */
public class GadgetDevelopmentTest extends BaseTest
{
   private static final String PROJECT = GadgetDevelopmentTest.class.getSimpleName();

   private static final String FILE_NAME = "Test Gadget File";

   private static final String FILE_NAME_FULL = "Test Gadget File.xml";

   @BeforeClass
   public static void setUp() throws IOException
   {
      VirtualFileSystemUtils.createDefaultProject(PROJECT);
   }
   
   @AfterClass
   public static void tearDown() throws IOException
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void createGadgetFromTemplate() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);
      
      Assert.assertTrue(IDE.TOOLBAR.isButtonFromNewPopupMenuEnabled(MenuCommands.New.FILE_FROM_TEMPLATE));
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      IDE.TEMPLATES.waitOpened();

      //Select "Google Gadget" in the central column, change "File Name" field text on "Test Gadget File" name, click on "Create" button.
      IDE.TEMPLATES.selectTemplate(Templates.GADGET_TEMPLATE);

      IDE.TEMPLATES.setFileName(FILE_NAME);
      IDE.TEMPLATES.clickCreateButton();
      IDE.TEMPLATES.waitClosed();
      IDE.EDITOR.waitTabPresent(1);
      
      assertEquals(FILE_NAME + " *", IDE.EDITOR.getTabTitle(1));

      //Click on "Save As" button and save file "Test Gadget File" with default name.
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.SAVE_AS, true);
      IDE.EDITOR.saveAs(1, FILE_NAME_FULL);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME_FULL);

      //check file created on server
      final String fileUrl = WS_URL + PROJECT + "/" + URLEncoder.encode(FILE_NAME_FULL, "UTF-8");
      Response response = VirtualFileSystemUtils.get(fileUrl);
      assertEquals(200, response.getStatusCode());

      IDE.EDITOR.closeFile(FILE_NAME_FULL);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME_FULL);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME_FULL);
      
      IDE.EDITOR.waitTabPresent(1);
      //new file with appropriate titles and highlighting should be opened in the Content Panel
      assertEquals(FILE_NAME_FULL, IDE.EDITOR.getTabTitle(1));

      IDE.EDITOR.closeFile(FILE_NAME_FULL);
   }

}