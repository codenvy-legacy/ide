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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Map;

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

   private final static String PROJECT = OutlineWithOtherTabsInPanelTest.class.getSimpleName();

   private static final String EMPTY_ZIP_PATH = "src/test/resources/org/exoplatform/ide/git/empty-repository.zip";

   @Before
   public void setUp()
   {
      final String textFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/sample-text.txt";
      final String htmlFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/sample-html.html";
      final String xmlFilePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/sample-xml.xml";

      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.importZipProject(PROJECT, EMPTY_ZIP_PATH);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, TEXT_FILE_NAME, MimeType.TEXT_PLAIN, textFilePath);
         VirtualFileSystemUtils.createFileFromLocal(link, XML_FILE_NAME, MimeType.TEXT_XML, xmlFilePath);
         VirtualFileSystemUtils.createFileFromLocal(link, HTML_FILE_NAME, MimeType.TEXT_HTML, htmlFilePath);
      }
      catch (Exception e)
      {
         fail("Can't create folder and files");
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
      catch (Exception e)
      {
      }
   }

   @Test
   public void testOutlineWithOtherTabsInPanel() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEXT_FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + HTML_FILE_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + XML_FILE_NAME);

      //Open XML file
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + XML_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + XML_FILE_NAME);

      //Open Outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      assertTrue(IDE.OUTLINE.isOutlineTreePresent());

      //Open HTML file
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + HTML_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + HTML_FILE_NAME);
      IDE.OUTLINE.waitOpened();
      //Check Outline visible
      assertTrue(IDE.OUTLINE.isOutlineTreePresent());

      //Open text file
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEXT_FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEXT_FILE_NAME);
      IDE.OUTLINE.waitNotAvailable();

      //Type text to file and save (create new version)
      IDE.EDITOR.typeTextIntoEditor(2, "hello");
      IDE.EDITOR.waitFileContentModificationMark(TEXT_FILE_NAME);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(TEXT_FILE_NAME);

      //Open version tab
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      //check history is visible, outline tab is not visible
      IDE.GIT.SHOW_HISTORY.waitOpened();
      assertFalse(IDE.OUTLINE.isActive());
      assertTrue(IDE.GIT.SHOW_HISTORY.isActive());

      //Go to XML file
      IDE.EDITOR.selectTab(XML_FILE_NAME);
      IDE.GIT.SHOW_HISTORY.closeView();

      //history tab is closed, outline is visible
      IDE.OUTLINE.waitOutlineTreeVisible();
      assertTrue(IDE.OUTLINE.isActive());

      //Type text and save
      IDE.EDITOR.typeTextIntoEditor(0, "abc");
      IDE.EDITOR.waitFileContentModificationMark(XML_FILE_NAME);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.SAVE);
      IDE.EDITOR.waitNoContentModificationMark(XML_FILE_NAME);

      //Open history tab
      IDE.MENU.runCommand(MenuCommands.Git.GIT, MenuCommands.Git.SHOW_HISTORY);
      IDE.GIT.SHOW_HISTORY.waitOpened();
      assertTrue(IDE.OUTLINE.isOutlineTreePresent());
      assertTrue(IDE.GIT.SHOW_HISTORY.isActive());

      //Close outline tab by clicking on close icon (x)
      IDE.OUTLINE.closeOutline();
      IDE.OUTLINE.waitClosed();
      assertFalse(IDE.OUTLINE.isOutlineTreePresent());
   }

}
