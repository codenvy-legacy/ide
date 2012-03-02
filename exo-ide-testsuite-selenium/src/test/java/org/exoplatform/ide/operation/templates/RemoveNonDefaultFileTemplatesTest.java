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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for deleting user file template.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class RemoveNonDefaultFileTemplatesTest extends BaseTest
{
   private final static String PROJECT = RemoveNonDefaultFileTemplatesTest.class.getSimpleName();

   private static final String FILE_TEMPLATE_NAME_1 = "test template";

   /**
    * File, where users templates are stored.
    */
   public static final String FILE_TEMPLATES_STORE = ENTRY_POINT_URL + WS_NAME_2 + "/ide-home/templates/fileTemplates";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         final String filePath =
            "src/test/resources/org/exoplatform/ide/operation/templates/RemoveNonDefaultFileTemplatesTest";
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_PLAIN, FILE_TEMPLATES_STORE);
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (Exception e)
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
      }
   }

   /**
    * @throws Exception
    */
   @Test
   public void testRemoveNonDefaultFileTemplates() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      // ------ 1 ----------
      // Click on "File->New->From Template..." topmenu item.
      assertTrue(IDE.TOOLBAR.isButtonFromNewPopupMenuEnabled(MenuCommands.New.FILE_FROM_TEMPLATE));
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      IDE.TEMPLATES.waitOpened();

      // check "Create file" dialog window
      assertTrue(IDE.TEMPLATES.isOpened());

      // ------ 2 ----------
      // In window select "test template", then click "Delete" button.
      IDE.TEMPLATES.selectTemplate(FILE_TEMPLATE_NAME_1);

      // click Delete button
      IDE.TEMPLATES.clickDeleteButton();

      // check warning dialog appeared
      IDE.ASK_DIALOG.waitOpened();

      // ------ 3 ----------
      // Click on button "Yes".
      IDE.ASK_DIALOG.clickYes();
      IDE.LOADER.waitClosed();
      IDE.ASK_DIALOG.waitClosed();
      IDE.TEMPLATES.waitForTemplateDeleted(FILE_TEMPLATE_NAME_1);

      assertFalse(IDE.TEMPLATES.isTemplatePresent(FILE_TEMPLATE_NAME_1));

      // ------ 4 ----------
      // Close "Create file" window, and all opened tabs in content panel.
      IDE.TEMPLATES.clickCancelButton();
   }
}