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
package org.exoplatform.ide.operation.gadget;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Preview;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for preview gadget feature.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GoogleGadgetPreviewTest extends BaseTest
{

   private final static String FILE_NAME = "Calculator.xml";

   private final static String FOLDER = GoogleGadgetPreviewTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/" + FOLDER + "/";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/Calculator.xml";
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.GOOGLE_GADGET, TestConstants.NodeTypes.EXO_GOOGLE_GADGET, URL + FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testGadgetPreview() throws Exception
   {
      IDE.WORKSPACE.waitForItem(URL);
      IDE.WORKSPACE.doubleClickOnFolder(URL);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);

      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GADGET_PREVIEW);
      Thread.sleep(TestConstants.PAGE_LOAD_PERIOD);
      
      assertTrue(selenium.isElementPresent("//div[@class='LeftCalculator']"));

      assertTrue(selenium.isElementPresent("//div[@class='Display']"));

      assertTrue(selenium.isElementPresent("//div[@class='Number']"));

      IDE.EDITOR.clickOnEditor();
      IDE.EDITOR.deleteFileContent();

      String hello =
         "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" + "<Module>\n" + "  <ModulePrefs title=\"Hello World!\" />\n"
            + " <Content type=\"html\">\n" + "<![CDATA[     \n" + " <div>" + "Hello,world!" + "</div>\n" + "]]>\n"
            + "</Content>\n" + "</Module>\n";

      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, hello);
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.MENU.waitForMenuItemPresent(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GADGET_PREVIEW);
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GADGET_PREVIEW);
      waitForElementPresent(Preview.Locators.GADGET_PREVIEW);

      assertTrue(selenium.isElementPresent("//div[contains(text(), 'Hello,world!')]"));
      
      //close preview
      IDE.PREVIEW.close();
      waitForElementNotPresent(Preview.Locators.GADGET_PREVIEW);
      
      //and open again
      IDE.MENU.waitForMenuItemPresent(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GADGET_PREVIEW);
      IDE.MENU.runCommand(MenuCommands.Run.RUN, MenuCommands.Run.SHOW_GADGET_PREVIEW);
      waitForElementPresent(Preview.Locators.GADGET_PREVIEW);

      assertTrue(selenium.isElementPresent("//div[contains(text(), 'Hello,world!')]"));
      
      IDE.EDITOR.closeFile(0);
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
