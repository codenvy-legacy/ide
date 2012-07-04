/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import java.util.Map;

public class CheckInformUserAfterCloseNoneSavedFile extends BaseTest
{

   private static String PROJECT = ClosingAndSaveAsFileTest.class.getSimpleName();

   private static String TEST_FILE = "newXMLFile.xml";

   private static String ALERT_FIREFOX_LABEL =
      "This page is asking you to confirm that you want to leave - data you have entered may not be saved.";

   private static String ALERT_CHROME_LABEL = "You have unsaved files, that may be lost!" + "\n" + "\n"
      + "Вы действительно хотите покинуть эту страницу?";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);

         Link link = project.get(Link.REL_CREATE_FILE);

         VirtualFileSystemUtils.createFileFromLocal(link, TEST_FILE, MimeType.APPLICATION_XML, filePath
            + "newXMLFile.xml");

      }
      catch (Exception e)
      {
         fail("Cant create project ");
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void checkInformAfterRefresh() throws Exception
   {
      //step one, open file, change content and does not save 
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.LOADER.waitClosed();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE);
      IDE.WELCOME_PAGE.close();
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FILE);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + TEST_FILE);
      IDE.EDITOR.deleteFileContent(0);
      //step two, check inform after refresh browser
      driver.navigate().refresh();
      IDE.POPUP.waitOpened();
     
      //in chrome and firefox browsers pop up messages is different
      if (IDE_SETTINGS.getString("selenium.browser.commad").equals("CHROME"))
      {
         assertEquals(IDE.POPUP.getTextFromAlert(), ALERT_FIREFOX_LABEL);
      }
      else
      {
         assertEquals(IDE.POPUP.getTextFromAlert(), ALERT_CHROME_LABEL);
      }
      IDE.POPUP.acceptAlert();
   }

   @Test
   public void checkInformAfterClose() throws Exception
   {
      //step one, reopen file, change content and does not save
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + TEST_FILE);
      // IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + TEST_FILE);
      IDE.EDITOR.typeTextIntoEditor(0, "<?xml version='1.0' encoding='UTF-8'?>");
      IDE.EDITOR.waitFileContentModificationMark("newXMLFile.xml");
      //step two, try closed not saved file, check inform in pop up window
      IDE.driver().close();
      IDE.POPUP.waitOpened();
      //in chrome and firefox browsers pop up messages is different
      if (IDE_SETTINGS.getString("selenium.browser.commad").equals("CHROME"))
      {
         assertEquals(IDE.POPUP.getTextFromAlert(), ALERT_FIREFOX_LABEL);
      }
      else
      {
         assertEquals(IDE.POPUP.getTextFromAlert(), ALERT_CHROME_LABEL);
      }
      IDE.POPUP.dismissAlert();
   }
}
