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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.net.URLEncoder;

/**
 * IDE-49: Deleting XML file with non-latin name.
 * 
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */

//IDE-49: Deleting XML file with non-latin name
public class DeletingXmlFileWithNonLatinNameTest extends BaseTest
{
   private static final String FILE_NAME = System.currentTimeMillis() + "ТестовыйФайл.xml";

   private static final String STORAGE_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   private static String XML_CONTENT =
      "<?xml version='1.0' encoding='UTF-8'?>\n" + "<test>\n" + "<settings>param</settings>\n" + "<bean>\n"
         + "<name>MineBean</name>\n" + "</bean>\n" + "</test>";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.put(XML_CONTENT.getBytes(), MimeType.TEXT_XML, STORAGE_URL + URLEncoder.encode(FILE_NAME,"UTF-8"));
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
         VirtualFileSystemUtils.delete(STORAGE_URL + URLEncoder.encode(FILE_NAME,"UTF-8"));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   
   /**
    * Test added to Ignore, because at the moment not solved a problem with encoding Cyrillic characters to URL.
    * For example: create new file with cyrillic name, save him, and get URL in IDE. In URL IDE we  shall see 
    * encoding characters in file name
    * @throws Exception
    */
   @Ignore
   @Test
   public void testDeletingXmlFileWithNonLatinName() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      
      IDE.WORKSPACE.waitForItem(WS_URL + FILE_NAME);
      IDE.WORKSPACE.selectItem(WS_URL + FILE_NAME);
      IDE.NAVIGATION.deleteSelectedItems();
      IDE.WORKSPACE.waitForItemNotPresent(WS_URL + FILE_NAME);
      
      assertEquals(404, VirtualFileSystemUtils.get(STORAGE_URL + URLEncoder.encode(FILE_NAME,"UTF-8")).getStatusCode());
   }
   
}
