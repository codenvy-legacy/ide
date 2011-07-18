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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * IDE-11: Deleting files. 
 * 
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:musienko.maxim@gmail.com">Musienko Maxim</a>
 * @version $Id: ${date} ${time}
 * 
 */
public class DeletingFilesTest extends BaseTest
{

   private static String FOLDER_NAME = DeletingFilesTest.class.getSimpleName();

   private static String HTML_FILE_NAME = "newHtmlFile.html";

   private static String GROOVY_FILE_NAME = "newGroovyFile.groovy";

   private static String GOOGLE_GADGET_FILE_NAME = "newGoogleGadget.gadget";

   private static String JAVA_SCRIPT_FILE_NAME = "newJavaScriptFile.js";

   private static String XML_FILE_NAME = "newXMLFile.xml";

   private static String TEXT_FILE_NAME = "newTxtFile.txt";
   
   private static String CUR_TIME = String.valueOf(System.currentTimeMillis());

   private final static String STORAGE_URL = WS_URL + FOLDER_NAME + "/";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";

   private static String HTML_FILE_URL = STORAGE_URL + CUR_TIME + HTML_FILE_NAME;

   private static String GROOVY_FILE_URL = STORAGE_URL + CUR_TIME + GROOVY_FILE_NAME;

   private static String GOOGLE_GADGET_FILE_URL = STORAGE_URL + CUR_TIME + GOOGLE_GADGET_FILE_NAME;

   private static String JAVA_SCRIPT_FILE_URL = STORAGE_URL + CUR_TIME + JAVA_SCRIPT_FILE_NAME;

   private static String XML_FILE_URL = STORAGE_URL + CUR_TIME + XML_FILE_NAME;

   private static String TEXT_FILE_URL = STORAGE_URL + CUR_TIME + TEXT_FILE_NAME;

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(PATH + HTML_FILE_NAME, MimeType.TEXT_HTML, HTML_FILE_URL);
         VirtualFileSystemUtils.put(PATH + GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE, GROOVY_FILE_URL);
         VirtualFileSystemUtils.put(PATH + GOOGLE_GADGET_FILE_NAME, MimeType.GOOGLE_GADGET, GOOGLE_GADGET_FILE_URL);
         VirtualFileSystemUtils.put(PATH + JAVA_SCRIPT_FILE_NAME, MimeType.APPLICATION_JAVASCRIPT, JAVA_SCRIPT_FILE_URL);
         VirtualFileSystemUtils.put(PATH + XML_FILE_NAME, MimeType.APPLICATION_XML, XML_FILE_URL);
         VirtualFileSystemUtils.put(PATH + TEXT_FILE_NAME, MimeType.TEXT_PLAIN, TEXT_FILE_URL);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(STORAGE_URL);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   //IDE-11: Deleting files.
   @Test
   public void testDeletingFile() throws Exception
   {
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");
      
      IDE.WORKSPACE.doubleClickOnFile(GROOVY_FILE_URL);
      IDE.NAVIGATION.deleteSelectedItems();
      assertEquals(404, VirtualFileSystemUtils.get(GROOVY_FILE_URL).getStatusCode());
      assertFalse(selenium().isTextPresent(GROOVY_FILE_NAME));

      IDE.WORKSPACE.doubleClickOnFile(GOOGLE_GADGET_FILE_URL);
      IDE.NAVIGATION.deleteSelectedItems();
      assertEquals(404, VirtualFileSystemUtils.get(GOOGLE_GADGET_FILE_URL).getStatusCode());
      assertFalse(selenium().isTextPresent(CUR_TIME + GOOGLE_GADGET_FILE_NAME));

      IDE.WORKSPACE.doubleClickOnFile(JAVA_SCRIPT_FILE_URL);
      IDE.NAVIGATION.deleteSelectedItems();
      assertEquals(404, VirtualFileSystemUtils.get(JAVA_SCRIPT_FILE_URL).getStatusCode());
      assertFalse(selenium().isTextPresent(CUR_TIME + JAVA_SCRIPT_FILE_NAME));

      IDE.WORKSPACE.doubleClickOnFile(XML_FILE_URL);
      IDE.NAVIGATION.deleteSelectedItems();
      assertEquals(404, VirtualFileSystemUtils.get(XML_FILE_URL).getStatusCode());
      assertFalse(selenium().isTextPresent(CUR_TIME + XML_FILE_NAME));

      IDE.WORKSPACE.doubleClickOnFile(TEXT_FILE_URL);
      IDE.NAVIGATION.deleteSelectedItems();
      assertEquals(404, VirtualFileSystemUtils.get(TEXT_FILE_URL).getStatusCode());
      assertFalse(selenium().isTextPresent(CUR_TIME + TEXT_FILE_NAME));

      IDE.WORKSPACE.doubleClickOnFile(HTML_FILE_URL);
      IDE.NAVIGATION.deleteSelectedItems();
      assertEquals(404, VirtualFileSystemUtils.get(HTML_FILE_URL).getStatusCode());
      assertFalse(selenium().isTextPresent(CUR_TIME + HTML_FILE_NAME));
   }

}
