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
package org.exoplatform.ide.operation.edit;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:dmitry.nochevnov@exoplatform.com">Dmytro Nochevnov</a>
 * @version $Id: Aug 26, 2010
 *
 */

/**
 * TestCase IDE-162
 */

public class HighlightCurrentLineTest extends BaseTest
{

   private static final String FILE_NAME = "HtmlTemplate.html";

   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FILE_NAME;

   private static final String LINE_HIGHLIGHTER_LOCATOR = "//div[@class='CodeMirror-line-highlighter']";

   private static final int LINE_HEIGHT = 16;

   private static final int EDITOR_TOP_OFFSET_POSITION = 93;

   private static final int EDITOR_LEFT_OFFSET_POSITION = 3;

   private Number linePositionLeft;

   private static final String scrollTopLocator =
      "document.getElementsByClassName('CodeMirror-wrapping')[0].childNodes[1].CodeMirror.editor.container.scrollTop";

   @BeforeClass
   public static void setUp()
   {

      String filePath =
         "src/test/resources/org/exoplatform/ide/operation/edit/highlightCurrentLineTest/HtmlTemplate.html";
      try
      {
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void highlightCurrentLine() throws Exception
   {
      //open HTML-file with required text
      Thread.sleep(TestConstants.SLEEP);
      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      waitForRootElement();

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FILE_NAME, false);

      // get line Position Left
      int contentPanelPositionLeft = selenium.getElementPositionLeft(IDE.EDITOR.getContentPanelLocator(0)).intValue();
      linePositionLeft = contentPanelPositionLeft + EDITOR_LEFT_OFFSET_POSITION;

      // test that new HTML file is opened in editor, first line is highlighted
      //lineHighlighterTest(1, 0);

      Number linePositionTop = EDITOR_TOP_OFFSET_POSITION + (1 - 1) * LINE_HEIGHT;

      // taking in mind vertical scrolling
      Integer scrollTop = getEditorScrollTop();
      if (scrollTop != null)
      {
         linePositionTop = linePositionTop.intValue() - scrollTop;
      }

      selenium.isElementPresent(LINE_HIGHLIGHTER_LOCATOR);

      assertEquals(selenium.getElementPositionLeft(IDE.EDITOR.getContentPanelLocator(0) + LINE_HIGHLIGHTER_LOCATOR),
         linePositionLeft);
      assertEquals(selenium.getElementPositionTop(IDE.EDITOR.getContentPanelLocator(0) + LINE_HIGHLIGHTER_LOCATOR),
         linePositionTop);

      // Press down arrow key on keyboard.
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.EDITOR.waitTabPresent(0);
      //Thread.sleep(TestConstants.SLEEP_SHORT);
      lineHighlighterTest(2, 0);

      // Press down arrow key on keyboard.
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.EDITOR.waitTabPresent(0);
      lineHighlighterTest(3, 0);

      // Move cursor "up" thirdly.
      for (int i = 0; i < 3; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
         Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
      }

      lineHighlighterTest(1, 0);

      // goto last line
      goToLine(7);
      lineHighlighterTest(7, 0);

      // Press "Enter" key.
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      lineHighlighterTest(8, 0);

      // remove last line
      IDE.MENU.runCommand(MenuCommands.Edit.EDIT_MENU, MenuCommands.Edit.DELETE_CURRENT_LINE);
      //      Thread.sleep(TestConstants.SLEEP_SHORT);

      lineHighlighterTest(8, 0);

      // Press down arrow key on keyboard.
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.EDITOR.waitTabPresent(0);
      lineHighlighterTest(8, 0);

      // Click in menu "File>New->REST Service".
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitTabPresent(1);
      lineHighlighterTest(1, 1);

      // Highlight line number 2 and verify bug [GWTX-47] In the Firefox cursor goes to the line 3 after pressing Enter key at the start of the first line of groovy script in the Code Editor.]
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      IDE.EDITOR.waitTabPresent(1);
      lineHighlighterTest(2, 1);
      assertEquals("2 : 1", getCursorPositionUsingStatusBar()); // verify cursor position in the status bar

      // Return to blank first line to verify bug with highlighting [IDE-135] in the Internet Explorer.
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_UP);
      IDE.EDITOR.waitTabPresent(1);
      lineHighlighterTest(1, 1);
      assertEquals("1 : 1", getCursorPositionUsingStatusBar()); // verify cursor position in the status bar      

      // goto line 2
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      IDE.EDITOR.waitTabPresent(1);

      // switch tab to previous file.
      IDE.EDITOR.selectTab(0);
      IDE.EDITOR.waitTabPresent(0);
      lineHighlighterTest(1, 0);

      // Return to new HTML file
      IDE.EDITOR.selectTab(1);
      IDE.EDITOR.waitTabPresent(1);
      lineHighlighterTest(1, 1);

      // switch tab to previous file.
      IDE.EDITOR.selectTab(0);
      IDE.EDITOR.waitTabPresent(0);

      // goto end of first line 
      IDE.EDITOR.clickOnEditor();
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PAGE_UP);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      lineHighlighterTest(1, 0);

      // test line highlighting with vertical scroll bar.
      if (getEditorScrollTop() != null)
      {
         // Press "Enter" key 37 times to appear scroll bar.
         for (int i = 0; i < 50; i++)
         {
            selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
            Thread.sleep(TestConstants.TYPE_DELAY_PERIOD);
         }

         lineHighlighterTest(51, 0);

         // Remember the current line with cursor and move scroll bar up and down.
         goToLine(4);
         lineHighlighterTest(4, 0);

         // goto last line 58 
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PAGE_DOWN);
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_PAGE_UP);
         Thread.sleep(TestConstants.SLEEP_SHORT);
         lineHighlighterTest(4, 0);
      }
   }

   /**
    * @param lineNumber.
    * @param tabIndex begins from 0.
    * @return true if line with line number in the Code Editor tab with tabIndex is highlighted, or false otherwise
    */
   private void lineHighlighterTest(int lineNumber, int tabIndex)
   {
      Number linePositionTop = EDITOR_TOP_OFFSET_POSITION + (lineNumber - 1) * LINE_HEIGHT;

      // taking in mind vertical scrolling
      Integer scrollTop = getEditorScrollTop();
      if (scrollTop != null)
      {
         linePositionTop = linePositionTop.intValue() - scrollTop;
      }

      selenium.isElementPresent(LINE_HIGHLIGHTER_LOCATOR);

      assertEquals(
         selenium.getElementPositionLeft(IDE.EDITOR.getContentPanelLocator(tabIndex) + LINE_HIGHLIGHTER_LOCATOR),
         linePositionLeft);
      assertEquals(
         selenium.getElementPositionTop(IDE.EDITOR.getContentPanelLocator(tabIndex) + LINE_HIGHLIGHTER_LOCATOR),
         linePositionTop);
   }

   /**
    * Return editor container scroll top. Method return <b>null</b> in Internet Explorer, because this browser doesn't support window.document.getElementsByClassName() method still. 
    * @return editor container scroll top.
    */
   private Integer getEditorScrollTop()
   {
      Integer scrollTop = null;

      try
      {
         // trying to read the property from Firefox         
         scrollTop =
            Integer.parseInt(selenium.getEval("var win = selenium.browserbot.getCurrentWindow(); win."
               + scrollTopLocator + ";"));
      }
      catch (NumberFormatException e)
      {
         return null;
      }

      return scrollTop;
   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      IDE.EDITOR.closeUnsavedFileAndDoNotSave(1);
      IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }

}
