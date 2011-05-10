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
   
   private final String googleGadgetFileName = "Ð¢ÐµÑ�Ñ‚Ð¾Ð²Ñ‹Ð¹ Ð³Ð°Ð´Ð¶ÐµÑ‚.xml";

   private final String googleGadgetFileContent =

      "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n" + "<ModulePrefs title=\"Hello World!\" />\n"
         + "<Content type=\"html\">\n" + "<![CDATA[ ÐŸÑ€Ð¸Ð²ÐµÑ‚, Ñ�Ð²ÐµÑ‚! Test]]></Content></Module>";
   
   /**
    * IDE-34:Advanced search test.
    * 
    * @throws Exception
    */
   @Test
   public void testAdvancedSearch() throws Exception
   {
      waitForRootElement();

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.deleteLinesInEditor(7);
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, googleGadgetFileContent);
      
      saveAsByTopMenu(googleGadgetFileName);
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.selectItem(WS_URL + googleGadgetFileName);
      IDE.NAVIGATION.selectRootOfWorkspace();
      
      //Step 5
      IDE.SEARCH.performSearch("/", "text", "");
      IDE.SEARCH.waitSearchResultsPresent();
      IDE.NAVIGATION.assertItemNotVisibleInSearchTree(WS_URL + googleGadgetFileName);
      
      //Step 6
      IDE.NAVIGATION.selectItemInSearchTree(WS_URL);
      IDE.SEARCH.performSearch("/", "", "script/groovy");
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemNotVisibleInSearchTree(WS_URL + googleGadgetFileName);
      
      //Step 7
      IDE.NAVIGATION.selectItemInSearchTree(WS_URL);
      IDE.SEARCH.performSearch("/", "ÐŸÑ€Ð¸Ð²ÐµÑ‚, Ñ�Ð²ÐµÑ‚!", "script/groovy");
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemNotVisibleInSearchTree(WS_URL + googleGadgetFileName);

      //Step 8
      IDE.NAVIGATION.selectItemInSearchTree(WS_URL);
      IDE.SEARCH.performSearch("/", "", "");
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + googleGadgetFileName);
      
      //Step 9
      IDE.NAVIGATION.selectItemInSearchTree(WS_URL);
      IDE.SEARCH.performSearch("/", "ÐŸÑ€Ð¸Ð²ÐµÑ‚, Ñ�Ð²ÐµÑ‚!", "");
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + googleGadgetFileName);
      
      //Step 10
      IDE.NAVIGATION.selectItemInSearchTree(WS_URL);
      IDE.SEARCH.performSearch("/", "", "application/x-google-gadget");
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + googleGadgetFileName);
      
      //Step 11
      IDE.NAVIGATION.selectItemInSearchTree(WS_URL);
      IDE.SEARCH.performSearch("/", "Test", "application/x-google-gadget");
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + googleGadgetFileName);

      //Clear test items
      selectWorkspaceTab();
      IDE.NAVIGATION.selectItem(WS_URL + googleGadgetFileName);
      IDE.NAVIGATION.deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.assertItemNotVisible(WS_URL + googleGadgetFileName);
   }
   
   @AfterClass
   public static void tearDown()
   {
      cleanRepository(REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/");
   }
}
