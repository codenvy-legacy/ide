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

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Apr 17, 2012 1:16:23 PM anya $
 * 
 */
public class AddBlockCommentsTest extends ServicesJavaTextFuction
{
   private static final String PROJECT = AddBlockCommentsTest.class.getSimpleName();

   private static final String FILE_NAME = "JavaCommentsTest.java";

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/JavaCommentsTest.zip";

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
   public void addBlockComment() throws Exception
   {

      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src");
      openJavaCommenTest(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

      IDE.JAVAEDITOR.moveCursorDown(28);
      IDE.JAVAEDITOR.moveCursorRight(6);

      for (int i = 0; i < 5; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 20; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + Keys.SHIFT + "/");
      String content = IDE.JAVAEDITOR.getTextFromJavaEditor();
      assertTrue(content.contains("/*     numbers.add(1);"));
      assertTrue(content.contains("numbers.add(6);*/"));

   }

   @Test
   public void overrideBlockComment() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaCommentsTest.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaCommentsTest.java");
      waitJavaCommentTestIsReady(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

      IDE.JAVAEDITOR.moveCursorDown(30);
      IDE.JAVAEDITOR.moveCursorRight(6);

      // after adding ability show number of the string into new java -
      // editor, this block should be uncommenting
      // assertEquals("30 : 7", IDE.STATUSBAR.getCursorPosition());

      for (int i = 0; i < 2; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 15; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + Keys.SHIFT + "/");
      String content = IDE.JAVAEDITOR.getTextFromJavaEditor();
      assertTrue(content.contains("/*numbers.add(2);"));
      assertTrue(content.contains("numbers.add(4);*/"));

      // need for reparce in editor
      Thread.sleep(4000);
      IDE.GOTOLINE.goToLine(34);
      IDE.JAVAEDITOR.moveCursorRight(6);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.END.toString());

      // after fix problem with status bar should be uncomment
      // assertEquals("34 : 7", IDE.STATUSBAR.getCursorPosition());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + Keys.SHIFT + "/");

      content = IDE.JAVAEDITOR.getTextFromJavaEditor();
      Thread.sleep(10000);
      assertTrue(content.contains("/*numbers.add(5);"));
      assertTrue(content.contains("numbers.add(6);*/"));
      assertTrue(content.contains("/*numbers.add(2);"));
      assertTrue(content.contains("numbers.add(4);*/"));
   }
}
