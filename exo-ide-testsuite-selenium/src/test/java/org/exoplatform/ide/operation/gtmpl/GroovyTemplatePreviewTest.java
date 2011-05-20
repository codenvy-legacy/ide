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
package org.exoplatform.ide.operation.gtmpl;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Preview;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for preview of groovy template.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/

public class GroovyTemplatePreviewTest extends BaseTest
{

   private final static String FILE_NAME = "GroovyTemplatePreviewTest.gtmpl";

   private final static String TEST_FOLDER = GroovyTemplatePreviewTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + TEST_FOLDER + "/";

   private static String GTMPL = "<html><body><% import org.exoplatform.services.security.Identity\n"
      + " import org.exoplatform.services.security.ConversationState\n "
      + " ConversationState curentState = ConversationState.getCurrent();\n"
      + " if (curentState != null){ Identity identity = curentState.getIdentity();\n"
      + " 3.times { println \"Hello \" + identity.getUserId()}}%><br></body></html>";

   @BeforeClass
   public static void setUp()
   {

      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(GTMPL.getBytes(), MimeType.GROOVY_TEMPLATE, URL + FILE_NAME);
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
   public void testGtmplPreview() throws Exception
   {
      waitForRootElement();
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.waitForItem(WS_URL + TEST_FOLDER + "/" + FILE_NAME);
      //open file
      IDE.WORKSPACE.doubleClickOnFile(WS_URL + TEST_FOLDER + "/" + FILE_NAME);
      IDE.EDITOR.waitTabPresent(0);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GROOVY_TEMPLATE_PREVIEW);
      waitForElementPresent(Preview.Locators.GROOVY_TEMPLATE_PREVIEW);
      IDE.PREVIEW.selectPreviewIFrame();
      assertTrue(selenium.isTextPresent("root"));
      IDE.selectMainFrame();
      
      //close preview tab and open again
      IDE.PREVIEW.close();
      waitForElementNotPresent(Preview.Locators.GROOVY_TEMPLATE_PREVIEW);
      assertFalse(IDE.PREVIEW.isGroovyTemplateVisible());
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GROOVY_TEMPLATE_PREVIEW);
      waitForElementPresent(Preview.Locators.GROOVY_TEMPLATE_PREVIEW);
      IDE.PREVIEW.selectPreviewIFrame();
      assertTrue(selenium.isTextPresent("root"));
      IDE.selectMainFrame();
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL);
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
