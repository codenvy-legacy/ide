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

import static org.junit.Assert.assertFalse;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class SearchAdvancedTest extends BaseTest
{
   private static final String PROJECT = SearchAdvancedTest.class.getSimpleName();

   private static final String openSocialGadgetFileName = "Тестовый гаджет.xml";

   private final String openSocialGadgetFileContent =

   "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n" + "<ModulePrefs title=\"Hello World!\" />\n"
      + "<Content type=\"html\">\n" + "<![CDATA[ Привет, свет! Test]]></Content></Module>";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.createDefaultProject(PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * IDE-34:Advanced search test.
    * 
    * @throws Exception
    */
   @Test
   public void testAdvancedSearch() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);

      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.OPENSOCIAL_GADGET_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/Untitled file.gadget");
      IDE.EDITOR.deleteFileContent(0);
      IDE.EDITOR.typeTextIntoEditor(0, openSocialGadgetFileContent);
      IDE.EDITOR.saveAs(1, openSocialGadgetFileName);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + openSocialGadgetFileName);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      // Step 5
      IDE.SEARCH.performSearch("/" + PROJECT, "text", "");
      IDE.LOADER.waitClosed();
      IDE.SEARCH_RESULT.waitOpened();
      IDE.SEARCH_RESULT.waitForItem(PROJECT);
      assertFalse(IDE.SEARCH_RESULT.isItemPresent(PROJECT + "/" + openSocialGadgetFileContent));
      IDE.SEARCH_RESULT.close();
      IDE.SEARCH_RESULT.waitClosed();

      // Step 6
      IDE.SEARCH.performSearch("/" + PROJECT, "", "script/groovy");
      IDE.LOADER.waitClosed();
      IDE.SEARCH_RESULT.waitForItem(PROJECT);
      assertFalse(IDE.SEARCH_RESULT.isItemPresent(PROJECT + "/" + openSocialGadgetFileContent));
      IDE.SEARCH_RESULT.close();
      IDE.SEARCH_RESULT.waitClosed();

      // Step 7
      IDE.SEARCH.performSearch("/" + PROJECT, "Привет, свет", "script/groovy");
      IDE.LOADER.waitClosed();
      IDE.SEARCH_RESULT.waitForItem(PROJECT);
      assertFalse(IDE.SEARCH_RESULT.isItemPresent(PROJECT + "/" + openSocialGadgetFileContent));
      IDE.SEARCH_RESULT.close();
      IDE.SEARCH_RESULT.waitClosed();

      // Step 8
      IDE.SEARCH.performSearch("/" + PROJECT, "", "");
      IDE.LOADER.waitClosed();
      IDE.SEARCH_RESULT.waitForItem(PROJECT + "/" + openSocialGadgetFileName);
      IDE.SEARCH_RESULT.close();
      IDE.SEARCH_RESULT.waitClosed();

      // Step 9
      IDE.SEARCH.performSearch("/" + PROJECT, "Привет, свет", "");
      IDE.LOADER.waitClosed();
      IDE.SEARCH_RESULT.waitForItem(PROJECT + "/" + openSocialGadgetFileName);
      IDE.SEARCH_RESULT.close();
      IDE.SEARCH_RESULT.waitClosed();

      // Step 10
      IDE.SEARCH.performSearch("/" + PROJECT, "", "application/x-google-gadget");
      IDE.LOADER.waitClosed();
      IDE.SEARCH_RESULT.waitForItem(PROJECT + "/" + openSocialGadgetFileName);
      IDE.SEARCH_RESULT.close();
      IDE.SEARCH_RESULT.waitClosed();

      // Step 11
      IDE.SEARCH.performSearch("/" + PROJECT, "Test", "application/x-google-gadget");
      IDE.LOADER.waitClosed();
      IDE.SEARCH_RESULT.waitForItem(PROJECT + "/" + openSocialGadgetFileName);
   }

   @AfterClass
   public static void tearDown() throws IOException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }
}
