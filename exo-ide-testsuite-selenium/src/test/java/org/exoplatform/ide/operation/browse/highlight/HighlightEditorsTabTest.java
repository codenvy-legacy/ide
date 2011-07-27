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
package org.exoplatform.ide.operation.browse.highlight;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 15, 2010 $
 * 
 */
public class HighlightEditorsTabTest extends BaseTest
{

   private static String FOLDER_NAME = HighlightEditorsTabTest.class.getSimpleName();

   private static String FILE_NAME = HighlightEditorsTabTest.class.getSimpleName() + "File";

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/edit/outline/HtmlCodeOutline.html", MimeType.TEXT_HTML,
            WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
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
   public void testHighlightEditorTab() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();      
      IDE.PERSPECTIVE.checkViewIsActive("ideWorkspaceView");

      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);
      waitForElementPresent("//div[@panel-id='editor']");
      IDE.PERSPECTIVE.checkViewIsActive("editor-0");

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      waitForElementPresent("ideOutlineTreeGrid");
      IDE.PERSPECTIVE.checkViewIsActive("ideOutlineView");
      IDE.PERSPECTIVE.checkViewIsNotActive("editor-0");

      IDE.OUTLINE.closeOutline();
      waitForElementNotPresent("ideOutlineTreeGrid");
      IDE.PERSPECTIVE.checkViewIsNotPresent("ideOutlineView");
      IDE.PERSPECTIVE.checkViewIsActive("editor-0");

      IDE.EDITOR.closeFile(0);

      openFileFromNavigationTreeWithCkEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, "HTML", false);
      waitForElementPresent("//div[@panel-id='editor']");
      IDE.PERSPECTIVE.checkViewIsActive("editor-1");

      //TODO should be compled should be completed after fix problem issue IDE-804
      //IDE.EDITOR.closeFile(0);
      //waitForElementNotPresent("//div[@panel-id='editor']");
      
      //TODO fix problem return highlighter in workspace
      //IDE.PERSPECTIVE.checkViewIsActive("ideWorkspaceView");
   }

}
