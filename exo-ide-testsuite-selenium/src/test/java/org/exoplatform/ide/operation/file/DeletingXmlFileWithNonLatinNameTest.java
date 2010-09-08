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

import java.io.IOException;
import java.net.URLEncoder;

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

   private static final String STORAGE_URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/";

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
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }

   @Test
   public void testDeletingXmlFileWithNonLatinName() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(FILE_NAME);
      deleteSelectedItems();
      Thread.sleep(TestConstants.SLEEP);
      assertFalse(selenium.isElementPresent("scLocator=//TreeGrid[ID=\"ideItemTreeGrid\"]/body/row[name=" + FILE_NAME
         + "]/col[1]"));
      assertEquals(404, VirtualFileSystemUtils.get(STORAGE_URL + URLEncoder.encode(FILE_NAME,"UTF-8")).getStatusCode());
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(STORAGE_URL + URLEncoder.encode(FILE_NAME,"UTF-8"));
      }
      catch (IOException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         // TODO Auto-generated catch block
         e.printStackTrace();
      }
   }
}
