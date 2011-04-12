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

import static org.junit.Assert.*;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 27, 2010 4:31:27 PM evgen $
 *
 */
// http://jira.exoplatform.org/browse/IDE-486
public class HighlightOutlineTest extends BaseTest
{

   private final static String FILE_NAME = "RESTCodeOutline.groovy";

   private final static String FOLDER_NAME = HighlightOutlineTest.class.getName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER_NAME;

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/" + FILE_NAME;
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.GROOVY_SERVICE, URL + "/" + FILE_NAME);
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

   @Test
   public void testHighlightOutline() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().selectItem(URL + "/");
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);

      // open outline panel
      IDE.toolbar().runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);

      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      Thread.sleep(TestConstants.IDE_LOAD_PERIOD);

      assertTrue(selenium
         .isElementPresent("//div[@eventproxy='isc_OutlineForm_0'  and contains(@style, 'border: 3px solid rgb(122, 173, 224)')]/"));

      String content = getTextFromCodeEditor(0);

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_F);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_O);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_O);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_B);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_A);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_R);

      assertEquals(content, getTextFromCodeEditor(0));

   }

   @AfterClass
   public static void tearDown() throws Exception
   {
      deleteCookies();
      cleanDefaultWorkspace();
   }

}
