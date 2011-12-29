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
package org.exoplatform.ide.operation.edit.outline;

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @version $Id:
 *
 */

public class CodeOutlineXmlTest extends BaseTest
{
   private final static String FILE_NAME = "XmlCodeOutline.xml";

   private final static String PROJECT = CodeOutlineXmlTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/XmlCodeOutline.xml";
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.TEXT_XML, filePath);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   /**
    * IDE-174:XML Code Outline
    * @throws Exception
    */
   @Test
   public void testXmlCodeOutline() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);

      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      IDE.OUTLINE.waitOutlineTreeVisible();

      checkTreeCorrectlyCreated();
   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      //check web-app and sub nodes
      assertTrue(IDE.OUTLINE.isItemPresentById("web-app:TAG:2"));
      assertTrue(IDE.OUTLINE.isItemPresentById("display-name:TAG:3"));

      //check context-param and node and sub nodes
      assertTrue(IDE.OUTLINE.isItemPresentById("context-param:TAG:7"));
      assertTrue(IDE.OUTLINE.isItemPresentById("param-name:TAG:8"));
      assertTrue(IDE.OUTLINE.isItemPresentById("param-value:TAG:12"));

      //check context-param and node and sub nodes
      assertTrue(IDE.OUTLINE.isItemPresentById("context-param:TAG:19"));
      assertTrue(IDE.OUTLINE.isItemPresentById("param-name:TAG:20"));
      assertTrue(IDE.OUTLINE.isItemPresentById("param-value:TAG:21"));

      //check cdata tag
      assertTrue(IDE.OUTLINE.isItemPresentById("CDATA:CDATA:24"));

      //check filter tag and sub nodes
      assertTrue(IDE.OUTLINE.isItemPresentById("filter:TAG:27"));
      assertTrue(IDE.OUTLINE.isItemPresentById("filter-name:TAG:28"));
      assertTrue(IDE.OUTLINE.isItemPresentById("filter-class:TAG:29"));
   }
}
