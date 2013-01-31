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

import java.io.IOException;
import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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

   private final static String PROJECT = BigTemplateTest.class.getSimpleName();

   public static final String FILE_TEMPLATES_STORE = ENTRY_POINT_URL + WS_NAME_2 + "/ide-home/templates/fileTemplates";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/Calculator.xml";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.GOOGLE_GADGET, filePath);
      }
      catch (IOException e)
      {
      }
   }

   @Test
   public void testBigTemplate() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile();

      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE_AS_TEMPLATE);
      IDE.SAVE_AS_TEMPLATE.waitOpened();
      IDE.SAVE_AS_TEMPLATE.isOpened();
      IDE.SAVE_AS_TEMPLATE.setName(TEMPLATE_NAME);
      IDE.SAVE_AS_TEMPLATE.clickSaveButton();
      IDE.SAVE_AS_TEMPLATE.waitClosed();

      IDE.INFORMATION_DIALOG.waitOpened("Template created successfully!");

      // click OK button
      IDE.INFORMATION_DIALOG.clickOk();
      IDE.INFORMATION_DIALOG.waitClosed();

      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      IDE.TEMPLATES.waitOpened();

      // check "Create file" dialog window
      IDE.TEMPLATES.waitOpenedDialog();
      IDE.TEMPLATES.selectTemplate(TEMPLATE_NAME);
      // click Create button
      IDE.TEMPLATES.clickCreateButton();
      IDE.TEMPLATES.waitClosed();

      IDE.EDITOR.waitActiveFile();
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
}
