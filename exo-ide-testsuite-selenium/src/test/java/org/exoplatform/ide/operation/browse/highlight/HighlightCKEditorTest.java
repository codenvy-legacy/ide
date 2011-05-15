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

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

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
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Nov 15, 2010 $
 *
 */
public class HighlightCKEditorTest extends BaseTest
{
   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   private static String FOLDER_NAME = HighlightCKEditorTest.class.getSimpleName();

   private static String FILE_NAME = HighlightCKEditorTest.class.getSimpleName() + "File";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/edit/outline/HtmlCodeOutline.html", MimeType.TEXT_HTML,
            URL + FOLDER_NAME + "/" + FILE_NAME);
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

//   This test will fail until IDE-424
   @Test
   public void testHighlightCKEdditor() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium
         .isElementPresent("//div[@eventproxy='isc_BrowserForm_0'  and contains(@style, 'border: 3px solid rgb(122, 173, 224)')]/"));
      IDE.WORKSPACE.selectItem(URL + FOLDER_NAME + "/");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      //Thread.sleep(TestConstants.SLEEP);

      IDE.WORKSPACE.selectItem(URL + FOLDER_NAME+ "/" + FILE_NAME); 
      openFileFromNavigationTreeWithCkEditor(FILE_NAME,"HTML",false);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_PREVIEW);
      //Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium
         .isElementPresent("//div[@eventproxy='isc_PreviewForm_0'  and contains(@style, 'border: 3px solid rgb(122, 173, 224)')]/"));
      assertFalse(selenium
         .isElementPresent("//div[@eventproxy='isc_EditorTab$EditorView_1'  and contains(@style, 'border: 3px solid rgb(122, 173, 224)')]/"));

     IDE.EDITOR.clickOnEditor();

      assertTrue(selenium
         .isElementPresent("//div[@eventproxy='isc_EditorTab$EditorView_0'  and contains(@style, 'border: 3px solid rgb(122, 173, 224)')]/"));

      assertFalse(selenium
         .isElementPresent("//div[@eventproxy='isc_PreviewForm_0'  and contains(@style, 'border: 3px solid rgb(122, 173, 224)')]/"));

     IDE.EDITOR.closeTab(0);
   }

   @AfterClass
   public static void tierDown()
   {
      deleteCookies();
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
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

}
