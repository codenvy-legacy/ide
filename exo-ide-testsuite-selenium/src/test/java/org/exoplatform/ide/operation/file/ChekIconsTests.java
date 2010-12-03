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
package org.exoplatform.ide.operation.file;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * 
 *
 */
public class ChekIconsTests extends BaseTest
{

   private static String FOLDER_NAME = OpeningSavingAndClosingFilesTest.class.getSimpleName();

   private static String HTML_FILE_NAME = "newHtmlFile.html";

   private static String CSS_FILE_NAME = "newCssFile.css";

   private static String JS_FILE_NAME = "newJavaScriptFile.js";

   private static String GADGET_FILE_NAME = "newGoogleGadget.gadget";

   private static String GROOVY_FILE_NAME = "newGroovyFile.groovy";

   private static String XML_FILE_NAME = "newXMLFile.xml";

   private static String TXT_FILE_NAME = "newTextFile.txt";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";

   private final static String STORAGE_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/"
      + WS_NAME + "/" + FOLDER_NAME + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(STORAGE_URL);
         VirtualFileSystemUtils.put(PATH + HTML_FILE_NAME, MimeType.TEXT_HTML, STORAGE_URL + HTML_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + CSS_FILE_NAME, MimeType.TEXT_CSS, STORAGE_URL + CSS_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + JS_FILE_NAME, MimeType.APPLICATION_JAVASCRIPT, STORAGE_URL + JS_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + GADGET_FILE_NAME, MimeType.GOOGLE_GADGET, STORAGE_URL + GADGET_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE, STORAGE_URL + GROOVY_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + XML_FILE_NAME, MimeType.TEXT_XML, STORAGE_URL + XML_FILE_NAME);
         VirtualFileSystemUtils.put(PATH + TXT_FILE_NAME, MimeType.TEXT_PLAIN, STORAGE_URL + TXT_FILE_NAME);
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

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(STORAGE_URL + HTML_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + CSS_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + JS_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + GADGET_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + GROOVY_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + XML_FILE_NAME);
         VirtualFileSystemUtils.delete(STORAGE_URL + TXT_FILE_NAME);
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
   public void testOpeningSavingAndClosingTabsWithFile() throws Exception
   {

//      //------------3---------------------    
      // Refresh Workspace:
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      selectItemInWorkspaceTree(FOLDER_NAME);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      // check icons
      checkIcons();

   }

   public void checkIcons() throws InterruptedException, Exception
   {
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      selectItemInWorkspaceTree(WS_NAME);
      Thread.sleep(TestConstants.EDITOR_OPEN_PERIOD);
      assertTrue(selenium.isElementPresent("//table[@class='listTable']/tbody/tr[3]//img[contains(@src, 'css.png')]"));
      assertTrue(selenium.isTextPresent(CSS_FILE_NAME));
      assertTrue(selenium
         .isElementPresent("//table[@class='listTable']/tbody/tr[4]//img[contains(@src, 'gadget.png')]"));
      assertTrue(selenium.isTextPresent(GADGET_FILE_NAME));
      assertTrue(selenium
         .isElementPresent("//table[@class='listTable']/tbody/tr[5]//img[contains(@src, 'rest.png')]"));
      assertTrue(selenium.isTextPresent(GROOVY_FILE_NAME));
      assertTrue(selenium.isElementPresent("//table[@class='listTable']/tbody/tr[6]//img[contains(@src, 'html.png')]"));
      assertTrue(selenium.isTextPresent(HTML_FILE_NAME));
      assertTrue(selenium
         .isElementPresent("//table[@class='listTable']/tbody/tr[7]//img[contains(@src, 'javascript.gif')]"));
      assertTrue(selenium.isTextPresent(JS_FILE_NAME));
      assertTrue(selenium.isElementPresent("//table[@class='listTable']/tbody/tr[8]//img[contains(@src, 'txt.png')]"));
      assertTrue(selenium.isTextPresent(TXT_FILE_NAME));
      assertTrue(selenium.isElementPresent("//table[@class='listTable']/tbody/tr[9]//img[contains(@src, 'xml.png')]"));
      assertTrue(selenium.isTextPresent(XML_FILE_NAME));
   }

}
