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
public class SearchAdvancedTest extends BaseTest
{
   
   private final String googleGadgetFileName = "Тестовый гаджет.xml";

   private final String googleGadgetFileContent =

      "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n" + "<ModulePrefs title=\"Hello World!\" />\n"
         + "<Content type=\"html\">\n" + "<![CDATA[ Привет, свет! Test]]></Content></Module>";
   
   /**
    * IDE-34:Advanced search test.
    * 
    * @throws Exception
    */
   @Test
   public void testAdvancedSearch() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.SLEEP);
      String workspace = getItemNameFromWorkspaceTree(0);

      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      Thread.sleep(TestConstants.SLEEP);
      IDE.editor().deleteLinesInEditor(7);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, googleGadgetFileContent);
      saveAsByTopMenu(googleGadgetFileName);
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentInWorkspaceTree(googleGadgetFileName);
      selectRootOfWorkspaceTree();
      //Step 5
      performSearch("/", "text", "");
      Thread.sleep(TestConstants.SLEEP);
      assertElementNotPresentSearchResultsTree(googleGadgetFileName);
      //Step 6
      selectItemInSearchResultsTree(workspace);
      performSearch("/", "", "script/groovy");
      Thread.sleep(TestConstants.SLEEP);
      assertElementNotPresentSearchResultsTree(googleGadgetFileName);
      //Step 7
      selectItemInSearchResultsTree(workspace);
      performSearch("/", "Привет, свет!", "script/groovy");
      Thread.sleep(TestConstants.SLEEP);
      assertElementNotPresentSearchResultsTree(googleGadgetFileName);

      //Step 8
      selectItemInSearchResultsTree(workspace);
      performSearch("/", "", "");
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentSearchResultsTree(googleGadgetFileName);
      //Step 9
      selectItemInSearchResultsTree(workspace);
      performSearch("/", "Привет, свет!", "");
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentSearchResultsTree(googleGadgetFileName);
      //Step 10
      selectItemInSearchResultsTree(workspace);
      performSearch("/", "", "application/x-google-gadget");
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentSearchResultsTree(googleGadgetFileName);
      //Step 11
      selectItemInSearchResultsTree(workspace);
      performSearch("/", "Test", "application/x-google-gadget");
      Thread.sleep(TestConstants.SLEEP);
      assertElementPresentSearchResultsTree(googleGadgetFileName);

      //Clear test items
      selectWorkspaceTab();
      selectItemInWorkspaceTree(googleGadgetFileName);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertElementNotPresentInWorkspaceTree(googleGadgetFileName);
   }
   
   @AfterClass
   public static void tearDown()
   {
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
