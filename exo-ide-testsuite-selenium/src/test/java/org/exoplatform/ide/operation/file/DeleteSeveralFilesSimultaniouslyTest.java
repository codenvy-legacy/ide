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
import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

public class DeleteSeveralFilesSimultaniouslyTest extends BaseTest
{

   private static String PROJECT = DeleteSeveralFilesSimultaniouslyTest.class.getSimpleName();

   private static String HTML_FILE_NAME = "newHtmlFile.html";

   private static String GROOVY_FILE_NAME = "newGroovyFile.groovy";

   private static String XML_FILE_NAME = "newXMLFile.xml";

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/file/";

   /**
    * 
    */
   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, HTML_FILE_NAME, MimeType.TEXT_HTML, PATH + HTML_FILE_NAME);
         VirtualFileSystemUtils.createFileFromLocal(link, GROOVY_FILE_NAME, MimeType.GROOVY_SERVICE, PATH
            + GROOVY_FILE_NAME);
         VirtualFileSystemUtils
            .createFileFromLocal(link, XML_FILE_NAME, MimeType.APPLICATION_XML, PATH + XML_FILE_NAME);
      }
      catch (Exception e)
      {
      }
   }

   /**
    * 
    */
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

   /**
    * @throws Exception
    */
   @Test
   public void testDeleteSeveralFilesSimultaniously() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + GROOVY_FILE_NAME);

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + GROOVY_FILE_NAME);
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.DELETE));
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + GROOVY_FILE_NAME);
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + GROOVY_FILE_NAME).getStatusCode());

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + XML_FILE_NAME);
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.DELETE));
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + XML_FILE_NAME);
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + XML_FILE_NAME).getStatusCode());

      IDE.PROJECT.EXPLORER.selectItem(PROJECT + "/" + HTML_FILE_NAME);
      assertTrue(IDE.MENU.isCommandEnabled(MenuCommands.File.FILE, MenuCommands.File.DELETE));
      assertTrue(IDE.TOOLBAR.isButtonEnabled(ToolbarCommands.File.DELETE));
      IDE.DELETE.deleteSelectedItems();
      IDE.PROJECT.EXPLORER.waitForItemNotPresent(PROJECT + "/" + HTML_FILE_NAME);
      assertEquals(404, VirtualFileSystemUtils.get(WS_URL + PROJECT + "/" + HTML_FILE_NAME).getStatusCode());
   }
}
