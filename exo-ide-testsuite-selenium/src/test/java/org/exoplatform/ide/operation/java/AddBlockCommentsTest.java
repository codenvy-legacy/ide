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
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
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
public class AddBlockCommentsTest extends BaseTest
{
   private static final String PROJECT = AddBlockCommentsTest.class.getSimpleName();

   private static final String FILE_NAME = "JavaCommentsTest.java";

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/java/" + FILE_NAME;

      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.APPLICATION_JAVA, filePath);
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
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.EDITOR.moveCursorDown(0, 28);
      IDE.EDITOR.moveCursorRight(0, 6);
      assertEquals("29 : 7", IDE.STATUSBAR.getCursorPosition());

      for (int i = 0; i < 5; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 15; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + Keys.SHIFT + "/");
      String content = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(content.contains("/*numbers.add(1);"));
      assertTrue(content.contains("numbers.add(6);*/"));
   }

   @Test
   public void overrideBlockComment() throws Exception
   {
      driver.navigate().refresh();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.EDITOR.moveCursorDown(0, 29);
      IDE.EDITOR.moveCursorRight(0, 6);
      assertEquals("30 : 7", IDE.STATUSBAR.getCursorPosition());

      for (int i = 0; i < 2; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 15; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + Keys.SHIFT + "/");
      String content = IDE.EDITOR.getTextFromCodeEditor(0);

      assertTrue(content.contains("/*numbers.add(2);"));
      assertTrue(content.contains("numbers.add(4);*/"));

      IDE.GOTOLINE.goToLine(29);
      IDE.EDITOR.moveCursorRight(0, 6);
      assertEquals("29 : 7", IDE.STATUSBAR.getCursorPosition());

      for (int i = 0; i < 5; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_DOWN);
      }

      for (int i = 0; i < 15; i++)
      {
         IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.ARROW_RIGHT);
      }
      IDE.EDITOR.typeTextIntoEditor(0, Keys.CONTROL.toString() + Keys.SHIFT + "/");
      content = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(content.contains("/*numbers.add(1);"));
      assertTrue(content.contains("numbers.add(6);*/"));
      assertFalse(content.contains("/*numbers.add(2);"));
      assertFalse(content.contains("numbers.add(4);*/"));
   }
}
