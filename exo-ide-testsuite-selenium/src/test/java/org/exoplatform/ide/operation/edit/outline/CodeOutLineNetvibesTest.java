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
package org.exoplatform.ide.operation.edit.outline;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Outline.TokenType;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for code outline for netvibes files.
 * 
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class CodeOutLineNetvibesTest extends BaseTest
{

   private final static String PROJECT = CodeOutLineNetvibesTest.class.getSimpleName();

   private final static String FILE_NAME = "NetvibesCodeOutline.html";

   private final static String FOLDER_NAME = CodeOutLineNetvibesTest.class.getSimpleName() + "-dir";

   private OulineTreeHelper outlineTreeHelper;

   public CodeOutLineNetvibesTest()
   {
      this.outlineTreeHelper = new OulineTreeHelper();
   }

   @Before
   public void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/NetvibesCodeOutline.html";
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_NAME);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, WS_URL + PROJECT + "/" + FOLDER_NAME + "/"
            + FILE_NAME);
      }
      catch (IOException e)
      {
      }
   }

   @After
   public void tearDown()
   {
      deleteCookies();
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
      }
   }

   // IDE-473 Issue
   @Test
   public void testCodeOutLineNetvibes() throws Exception
   {
      // ------ 1 ------------
      // open file with text
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);

      // step 2 change memtype as UWA widget
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.RENAME);
      IDE.RENAME.waitOpened();
      IDE.RENAME.setMimeType("application/x-uwa-widget");
      IDE.RENAME.clickRenameButton();
      IDE.RENAME.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);

      // step 3 open the file, run outline and check tree
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.TOOLBAR.waitButtonPresentAtLeft(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      checkTreeCorrectlyCreated();

   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      // create initial outline tree map
      OulineTreeHelper.init();
      
      // check is tree created correctly      
      outlineTreeHelper.checkOutlineTree();

      // expand outline tree
      outlineTreeHelper.expandOutlineTree();

      // TODO issue IDE-1499
      IDE.GOTOLINE.goToLine(10);
      IDE.GOTOLINE.goToLine(15);

      // check html node
      outlineTreeHelper.addOutlineItem("html", 4, TokenType.TAG);

      // check head tag and sub-nodes head
      outlineTreeHelper.addOutlineItem("head", 6, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("meta", 7, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("meta", 8, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("meta", 9, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("meta", 10, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("meta", 11, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("link", 12, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("script", 14, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("title", 16, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("link", 17, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("widget:preferences", 20, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("style", 22, TokenType.TAG);
      
      // check script tag and sub-nodes script
      outlineTreeHelper.addOutlineItem("script", 26, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("YourWidgetName : Object", 31, TokenType.VARIABLE);
      outlineTreeHelper.addOutlineItem("function()", 37, TokenType.FUNCTION);
      outlineTreeHelper.addOutlineItem("function()", 44, TokenType.FUNCTION);

      // check body tag and subnodes body
      outlineTreeHelper.addOutlineItem("body", 50, TokenType.TAG);
      outlineTreeHelper.addOutlineItem("p", 51, TokenType.TAG);
      
      outlineTreeHelper.checkOutlineTree();
   }

}
