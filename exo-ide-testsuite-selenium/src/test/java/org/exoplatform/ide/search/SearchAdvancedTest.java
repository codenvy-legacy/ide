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

import static org.junit.Assert.*;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
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

   private static final String googleGadgetFileName = "Тестовый гаджет.xml";

   private final String googleGadgetFileContent =
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
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.EDITOR.waitTabPresent(0);
      IDE.WELCOME_PAGE.close();
      IDE.WELCOME_PAGE.waitClose();

      //step2
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.GOOGLE_GADGET_FILE);
      IDE.EDITOR.waitTabPresent(0);
      IDE.EDITOR.deleteLinesInEditor(0, 7);
      IDE.EDITOR.typeTextIntoEditor(0, googleGadgetFileContent);
      
      IDE.EDITOR.saveAs(0, googleGadgetFileName);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + googleGadgetFileName);
      IDE.PROJECT.EXPLORER.selectItem(PROJECT);

      //Step 3 Check search with different mismatched parameters
      IDE.SEARCH.performSearch("/" + PROJECT, "text", "");
      IDE.SEARCH.waitSearchResultsOpened();
      assertFalse(IDE.SEARCH.isFilePresent(PROJECT + "/" + googleGadgetFileName));

      //first mismatched parameter
      IDE.SEARCH.selectRootItem();
      IDE.SEARCH.performSearch("/" + PROJECT, "", "script/groovy");
      IDE.SEARCH.waitSearchResultsOpened();
      assertFalse(IDE.SEARCH.isFilePresent(PROJECT + "/" + googleGadgetFileName));

      //second  
      IDE.SEARCH.selectRootItem();
      IDE.SEARCH.performSearch("/" + PROJECT, "Привет, свет!", "script/groovy");
      IDE.SEARCH.waitSearchResultsOpened();
      assertFalse(IDE.SEARCH.isFilePresent(PROJECT + "/" + googleGadgetFileName));

      //third mismatched parameter
      IDE.SEARCH.selectRootItem();
      IDE.SEARCH.performSearch("/" + PROJECT, "Привет, свет!", "script/groovy");
      IDE.SEARCH.waitSearchResultsOpened();
      assertFalse(IDE.SEARCH.isFilePresent(PROJECT + "/" + googleGadgetFileName));

      //Step 3 Check search with different matching parameters
      IDE.SEARCH.selectRootItem();
      IDE.SEARCH.performSearch("/" + PROJECT, "", "");
      IDE.SEARCH.waitSearchResultsOpened();
      assertTrue(IDE.SEARCH.isFilePresent(PROJECT + "/" + googleGadgetFileName));

       
      IDE.SEARCH.selectRootItem();
      IDE.SEARCH.performSearch("/" + PROJECT + "/" + googleGadgetFileName, "Привет, свет! ", "");
      IDE.SEARCH.waitSearchResultsOpened();
      assertTrue(IDE.SEARCH.isFilePresent(PROJECT + "/" + googleGadgetFileName));

      IDE.SEARCH.selectRootItem();
      IDE.SEARCH.performSearch("/" + PROJECT + "/" + googleGadgetFileName, "Test", "application/x-google-gadget\n");
      
      IDE.SEARCH.waitSearchResultsOpened();
      assertTrue(IDE.SEARCH.isFilePresent(PROJECT + "/" + googleGadgetFileName));
   }

   @AfterClass
   public static void tearDown() throws IOException
   {
      VirtualFileSystemUtils.delete(WS_URL + PROJECT);
   }
}
