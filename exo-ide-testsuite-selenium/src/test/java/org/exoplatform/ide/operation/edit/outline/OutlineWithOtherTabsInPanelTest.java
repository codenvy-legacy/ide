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

import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Navigation;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

//http://jira.exoplatform.org/browse/IDE-417
/**
 * Test interaction of outline panel with other tabs:
 * try to open and close outline panel and versions panel.
 * 
 * Check, is panels has correctly behavior, when change current file.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Nov 23, 2010 $
 *
 */
public class OutlineWithOtherTabsInPanelTest extends BaseTest
{
   
   private final static String TEXT_FILE_NAME = "file-1.txt";

   private final static String HTML_FILE_NAME = "file-2.html";

   private final static String XML_FILE_NAME = "file-3.xml";

   private final static String FOLDER_NAME = OutlineWithOtherTabsInPanelTest.class.getSimpleName();

   @Before
   public void setUp()
   {
      final String textFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/sample-text.txt";
      final String htmlFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/sample-html.html";
      final String xmlFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/sample-xml.xml";

      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME);

         VirtualFileSystemUtils.put(textFilePath, MimeType.TEXT_PLAIN, WS_URL + FOLDER_NAME + "/" + TEXT_FILE_NAME);
         VirtualFileSystemUtils.put(xmlFilePath, MimeType.TEXT_XML, WS_URL + FOLDER_NAME + "/" + XML_FILE_NAME);
         VirtualFileSystemUtils.put(htmlFilePath, MimeType.TEXT_HTML, WS_URL + FOLDER_NAME + "/" + HTML_FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail("Can't create folder and files");
      }
   }

   @After
   public void tearDown()
   {
      deleteCookies();
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testOutlineWithOtherTabsInPanel() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      //----- 1 -------------
      //open xml file
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/" + XML_FILE_NAME);
      IDE.NAVIGATION.openSelectedFileWithEditor(Navigation.Editor.CODEMIRROR, false);
      //IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(XML_FILE_NAME, false);
      //Thread.sleep(TestConstants.SLEEP_SHORT);

      //----- 2 -------------
      //open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      waitForElementPresent("ideOutlineTreeGrid");
      //check outline visible
      IDE.OUTLINE.assertOutlineTreePresent();

      //----- 3 -------------
      //open html file
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/" + HTML_FILE_NAME);
      IDE.NAVIGATION.openSelectedFileWithEditor(Navigation.Editor.CODEMIRROR, false);
      //IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(HTML_FILE_NAME, false);

      //check outline visible
      IDE.OUTLINE.assertOutlineTreePresent();

      //----- 4 -------------
      //open text file
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/" + TEXT_FILE_NAME);
      IDE.NAVIGATION.openSelectedFileWithEditor(Navigation.Editor.CODEMIRROR, false);
      Thread.sleep(TestConstants.SLEEP_SHORT);
      //check outline is not visible
      IDE.OUTLINE.checkOtlineTreeIsNotPresent();

      //----- 5 -------------
      //type text to file and save (create new version)
      IDE.EDITOR.typeTextIntoEditor(2, "hello");
      saveCurrentFile();

      //----- 6 -------------
      //open version tab
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_VERSION_HISTORY);
      //check version tab is visible, outline tab is not visible

      IDE.OUTLINE.checkOutlinePanelIsNotActive();
      IDE.VERSIONS.checkVersionPanelIsActive();

      //----- 7 -------------
      //go to xml file
      IDE.EDITOR.selectTab(0);

      //version tab is closed, outline is visible
      IDE.OUTLINE.assertOutlineTreePresent();
      IDE.VERSIONS.checkViewVersionsListPanel(false);

      //----- 8 -------------
      //type text and save
      IDE.EDITOR.typeTextIntoEditor(0, "abc");
      saveCurrentFile();

      //----- 9 -------------
      //open versions tab
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.VIEW_VERSION_HISTORY);
      IDE.OUTLINE.assertOutlineTreePresent();
      IDE.VERSIONS.checkVersionPanelIsActive();

      //----- 10 -------------
      //close outline tab by clicking on close icon (x)
      IDE.OUTLINE.closeOutline();

      //outline panel is closed and versions tab is visible
      IDE.OUTLINE.assertOutlineTreeNotPresent();

      //----- 11 -------------
      //select text file
      IDE.EDITOR.selectTab(2);
      IDE.OUTLINE.assertOutlineTreeNotPresent();

      //----- 12 -------------
      //select html file
      IDE.EDITOR.selectTab(1);
      IDE.OUTLINE.assertOutlineTreeNotPresent();

   }

}
