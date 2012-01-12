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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
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
public class SearchByMimeTypeTest extends BaseTest
{

   private static final String FOLDER_NAME_1 = "Users";

   private static final String FILE_NAME_1 = "Example.js";

   private static final String FOLDER_NAME_2 = "Test";

   private static final String FILE_NAME_2 = "CopyOfExample.js";

   private static final String FILE_CONTENT = "// CodeMirror main module"
      + "var CodeMirrorConfig = window.CodeMirrorConfig || {};\n"

      + "var CodeMirror = (function(){\n" + "function setDefaults(object, defaults) {\n"
      + "for (var option in defaults) {\n" + "if (!object.hasOwnProperty(option))\n"
      + "object[option] = defaults[option];\n" + "}\n" + "}\n" + "function forEach(array, action) {\n"
      + "for (var i = 0; i < array.length; i++)\n" + "action(array[i]);\n" + "}";
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME_1);
         VirtualFileSystemUtils.put(FILE_CONTENT.getBytes(), MimeType.APPLICATION_JAVASCRIPT, WS_URL + FOLDER_NAME_1
            + "/" + FILE_NAME_1);
         
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME_2);
         VirtualFileSystemUtils.put(FILE_CONTENT.getBytes(), MimeType.APPLICATION_JAVASCRIPT, WS_URL + FOLDER_NAME_2
            + "/" + FILE_NAME_2);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * IDE-32:Searching file by Mime Type from subfolder test.
    *  
    */
   @Test
   public void testSearchByMimeType() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME_2 + "/");
      final String rootFolderName = IDE.NAVIGATION.getRowTitle(1);
      
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME_2 + "/");
      IDE.SEARCH.performSearch("/" + FOLDER_NAME_2 + "/", "", MimeType.APPLICATION_JAVASCRIPT);
      IDE.SEARCH.waitSearchResultsOpened();
      
      IDE.NAVIGATION.assertItemVisibleInSearchTree(WS_URL + FOLDER_NAME_2 + "/" + FILE_NAME_2);
      IDE.NAVIGATION.assertItemNotVisibleInSearchTree(WS_URL + FOLDER_NAME_1 + "/" + FILE_NAME_1);

      IDE.SEARCH.doubleClickOnFile(WS_URL + FOLDER_NAME_2 + "/" + FILE_NAME_2);
      IDE.EDITOR.waitTabPresent(0);
      assertEquals(rootFolderName + "/" + FOLDER_NAME_2, IDE.STATUSBAR.getNavigationStatus());
   }

   @AfterClass
   public static void tearDown() throws IOException
   {
      VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME_1);
      VirtualFileSystemUtils.delete(WS_URL + FOLDER_NAME_2);
   }
}
