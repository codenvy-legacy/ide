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

import static org.junit.Assert.assertFalse;

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
public class RemoveBlockCommentsTest extends ServicesJavaTextFuction
{

   // JavaRemoveCommentsTest.java
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

   @After
   public final void closeEditor()
   {
      try
      {
         IDE.EDITOR.forcedClosureFile(1);
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
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src");
      openJavaRemoveCommenTest(PROJECT);
      IDE.GOTOLINE.goToLine(30);
      // after fix problem in status bar uncomment
      // assertEquals("30 : 6", IDE.STATUSBAR.getCursorPosition());

      for (int i = 0; i < 3; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 23; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + Keys.SHIFT + "\\");
      // need wait for reparce
      Thread.sleep(4000);
      String content = IDE.JAVAEDITOR.getTextFromJavaEditor();
      assertFalse(content.contains("/*numbers.add(2);"));
      assertFalse(content.contains("numbers.add(5);*/"));

      IDE.EDITOR.closeTabIgnoringChanges(1);

   }

   @Test
   public void removeBlockCommentByInnerSelection() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaRemoveCommentsTest.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaRemoveCommentsTest.java");
      waitJavaRemoveCommentTestIsReady(PROJECT);

      IDE.GOTOLINE.goToLine(31);
      IDE.JAVAEDITOR.moveCursorRight(6);
      // after fix problem in status bar uncomment
      // assertEquals("31 : 7", IDE.STATUSBAR.getCursorPosition());

      for (int i = 0; i < 1; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 22; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + Keys.SHIFT + "\\");
      // need wait for reparce
      Thread.sleep(4000);
      String content = IDE.JAVAEDITOR.getTextFromJavaEditor();

      assertFalse(content.contains("/*numbers.add(2);"));
      assertFalse(content.contains("numbers.add(5);*/"));

   }

   @Test
   public void removeBlockCommentByStartSelection() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaRemoveCommentsTest.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaRemoveCommentsTest.java");
      waitJavaRemoveCommentTestIsReady(PROJECT);

      IDE.GOTOLINE.goToLine(29);
      IDE.JAVAEDITOR.moveCursorRight(6);
      // after fix problem in status bar uncomment
      // assertEquals("29 : 7", IDE.STATUSBAR.getCursorPosition());

      for (int i = 0; i < 2; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 22; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + Keys.SHIFT + "\\");
      String content = IDE.JAVAEDITOR.getTextFromJavaEditor();

      assertFalse(content.contains("/*numbers.add(2);"));
      assertFalse(content.contains("numbers.add(5);*/"));

   }

   @Test
   public void removeBlockCommentByEndSelection() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaRemoveCommentsTest.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaRemoveCommentsTest.java");
      waitJavaRemoveCommentTestIsReady(PROJECT);

      IDE.GOTOLINE.goToLine(32);
      IDE.JAVAEDITOR.moveCursorRight(6);
      // after fix problem in status bar uncomment
      // assertEquals("32 : 7", IDE.STATUSBAR.getCursorPosition());

      for (int i = 0; i < 2; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 22; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.CONTROL.toString() + Keys.SHIFT + "\\");
      String content = IDE.JAVAEDITOR.getTextFromJavaEditor();

      assertFalse(content.contains("/*numbers.add(2);"));
      assertFalse(content.contains("numbers.add(5);*/"));

   }

}
