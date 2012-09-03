/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.operation.java;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 17, 2012 1:16:23 PM anya $
 * 
 */
public class RemoveBlockCommentsTest extends ServicesJavaTextFuctionTest
{

   //JavaRemoveCommentsTest.java
   private static final String PROJECT = RemoveBlockCommentsTest.class.getSimpleName();

   private static final String FILE_NAME = "JavaRemoveCommentsTest.java";

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/JavaRemoveCommentsTest.zip";

      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.importZipProject(PROJECT, filePath);
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void closeTab()
   {
      try
      {
         IDE.EDITOR.closeTabIgnoringChanges(1);

      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
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
   public void removeBlockComment() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      openJavaRemoveCommenTest(PROJECT);

      //-------on this moment go to line does not work in java editor
      // after fix this block with GOTOLINE method can be uncomment
      //on this moment we go to line 30 with cursor

      //IDE.GOTOLINE.goToLine(30);

      IDE.EDITOR.moveCursorDown(0, 30);
      IDE.EDITOR.moveCursorRight(0, 5);
      Thread.sleep(10000);
      //   assertEquals("30 : 6", IDE.STATUSBAR.getCursorPosition());

      //      for (int i = 0; i < 3; i++)
      //      {
      //         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      //      }
      //
      //      for (int i = 0; i < 17; i++)
      //      {
      //         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      //      }
      //      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + Keys.SHIFT + "\\");
      //
      //      String content = IDE.EDITOR.getTextFromCodeEditor(0);
      //      assertFalse(content.contains("/*numbers.add(2);"));
      //      assertFalse(content.contains("numbers.add(5);*/"));
      //      IDE.EDITOR.closeTabIgnoringChanges(1);

   }

   // @Test
   public void removeBlockCommentByInnerSelection() throws Exception
   {
      driver.navigate().refresh();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.GOTOLINE.goToLine(31);
      IDE.EDITOR.moveCursorRight(0, 6);
      assertEquals("31 : 7", IDE.STATUSBAR.getCursorPosition());

      for (int i = 0; i < 1; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 15; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + Keys.SHIFT + "\\");
      String content = IDE.EDITOR.getTextFromCodeEditor(0);

      assertFalse(content.contains("/*numbers.add(2);"));
      assertFalse(content.contains("numbers.add(5);*/"));
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   //@Test
   public void removeBlockCommentByStartSelection() throws Exception
   {
      driver.navigate().refresh();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.GOTOLINE.goToLine(29);
      IDE.EDITOR.moveCursorRight(0, 6);
      assertEquals("29 : 7", IDE.STATUSBAR.getCursorPosition());

      for (int i = 0; i < 2; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 15; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + Keys.SHIFT + "\\");
      String content = IDE.EDITOR.getTextFromCodeEditor(0);

      assertFalse(content.contains("/*numbers.add(2);"));
      assertFalse(content.contains("numbers.add(5);*/"));
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

   // @Test
   public void removeBlockCommentByEndSelection() throws Exception
   {
      driver.navigate().refresh();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.GOTOLINE.goToLine(32);
      IDE.EDITOR.moveCursorRight(0, 6);
      assertEquals("32 : 7", IDE.STATUSBAR.getCursorPosition());

      for (int i = 0; i < 2; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 15; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + Keys.SHIFT + "\\");
      String content = IDE.EDITOR.getTextFromCodeEditor(0);

      assertFalse(content.contains("/*numbers.add(2);"));
      assertFalse(content.contains("numbers.add(5);*/"));
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

}
