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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

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

   private final static String TEST_FOLDER = CodeOutlineXmlTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/XmlCodeOutline.xml";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_XML, URL + TEST_FOLDER + "/" + FILE_NAME);
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
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
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
      //---- 1 -----------------
      //open file with text
      IDE.WORKSPACE.waitForItem(URL + TEST_FOLDER + "/");
      IDE.WORKSPACE.doubleClickOnFolder(URL + TEST_FOLDER + "/");
      waitForElementNotPresent(IDE.NAVIGATION.getItemId(URL + TEST_FOLDER + "/" + FILE_NAME));
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME, false);

      //---- 2 -----------------
      IDE.TOOLBAR.runCommand("Show Outline");
      waitForElementPresent("ideOutlineTreeGrid");
      Thread.sleep(TestConstants.SLEEP);

      //---- 3 -----------------
      checkTreeCorrectlyCreated();

   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      //check web-app and sub nodes
      IDE.OUTLINE.assertElmentPresentById("web-app:TAG:2");
      IDE.OUTLINE.assertElmentPresentById("display-name:TAG:3");

      //check context-param and node and sub nodes
      IDE.OUTLINE.assertElmentPresentById("context-param:TAG:7");
      IDE.OUTLINE.assertElmentPresentById("param-name:TAG:8");
      IDE.OUTLINE.assertElmentPresentById("param-value:TAG:12");

      //check context-param and node and sub nodes
      IDE.OUTLINE.assertElmentPresentById("context-param:TAG:19");
      IDE.OUTLINE.assertElmentPresentById("param-name:TAG:20");
      IDE.OUTLINE.assertElmentPresentById("param-value:TAG:21");

      //check cdata tag
      IDE.OUTLINE.assertElmentPresentById("CDATA:CDATA:24");

      //check filter tag and sub nodes
      IDE.OUTLINE.assertElmentPresentById("filter:TAG:27");
      IDE.OUTLINE.assertElmentPresentById("filter-name:TAG:28");
      IDE.OUTLINE.assertElmentPresentById("filter-class:TAG:29");
   }
}
