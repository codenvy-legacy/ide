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
package org.exoplatform.ide.search;

import static org.junit.Assert.assertEquals;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class SearchByMimeTypeTest extends BaseTest
{

   private final String folder1Name = "Users";

   private final String jsFileName = "Example.js";
   
   private final String folder2Name = "Test";

   private final String jsFileMimeType = "application/javascript";

   private final String copyJsFileName = "Copy Of Example.js";

   private final String jsFileContent =
      "// CodeMirror main module" + "var CodeMirrorConfig = window.CodeMirrorConfig || {};\n"

      + "var CodeMirror = (function(){\n" + "function setDefaults(object, defaults) {\n"
         + "for (var option in defaults) {\n" + "if (!object.hasOwnProperty(option))\n"
         + "object[option] = defaults[option];\n" + "}\n" + "}\n" + "function forEach(array, action) {\n"
         + "for (var i = 0; i < array.length; i++)\n" + "action(array[i]);\n" + "}";

   /**
    * IDE-32:Searching file by Mime Type from subfolder test.
    *  
    */
   @Test
   public void testSearchByMimeType() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.SLEEP);
      createFolder(folder1Name);

      //Create and save 
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);
      Thread.sleep(TestConstants.SLEEP);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, jsFileContent);
      saveAsByTopMenu(jsFileName);
      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().assertItemPresent(WS_URL + jsFileName);

      selectRootOfWorkspaceTree();
      createFolder(folder2Name);

      saveAsUsingToolbarButton(copyJsFileName);
      Thread.sleep(TestConstants.SLEEP);
      IDE.editor().closeTab(0);

      IDE.navigator().selectItem(WS_URL + folder2Name + "/");

      performSearch("/" + folder2Name + "/", "", jsFileMimeType);

      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentSearchResultsTree(copyJsFileName);
      assertElementNotPresentSearchResultsTree(jsFileName);

      openFileFromSearchResultsWithCodeEditor(copyJsFileName);
      Thread.sleep(TestConstants.SLEEP);
      assertEquals(getItemNameFromWorkspaceTree(0) + "/" + folder2Name, getStatusbarText());

      //Clear created items
      selectWorkspaceTab();
      IDE.navigator().selectItem(WS_URL + folder1Name + "/");
      selenium.controlKeyDown();
      IDE.navigator().selectItem(WS_URL + folder2Name + "/");
      selenium.controlKeyUp();
      deleteSelectedItems();

      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().assertItemNotPresent(WS_URL + folder1Name + "/");
      IDE.navigator().assertItemNotPresent(WS_URL + folder2Name + "/");
   }
   
   @AfterClass
   public static void tearDown()
   {
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
