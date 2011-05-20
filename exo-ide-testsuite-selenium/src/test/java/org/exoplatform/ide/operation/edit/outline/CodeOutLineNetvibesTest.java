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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for code outline for netvibes files.
 * 
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CodeOutLineNetvibesTest extends BaseTest
{

   private final static String FILE_NAME = "NetvibesCodeOutline.html";

   private final static String FOLDER_NAME = CodeOutLineNetvibesTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/NetvibesCodeOutline.html";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(filePath, MimeType.TEXT_HTML, URL + FOLDER_NAME + "/" + FILE_NAME);
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
      deleteCookies();
      try
      {
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
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

   // IDE-473 Issue
   @Test
   public void testCodeOutLineNetvibes() throws Exception
   {
      //------ 1 ------------
      //open file with text
      IDE.WORKSPACE.waitForItem(URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.doubleClickOnFolder(URL + FOLDER_NAME + "/");
      waitForElementNotPresent(IDE.NAVIGATION.getItemId(URL + FOLDER_NAME + "/" + FILE_NAME));
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FOLDER_NAME + "/" + FILE_NAME, false);

      //------ 2 ------------
      //show Outline
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      waitForElementPresent("ideOutlineTreeGrid");

      //------3--------
      checkTreeCorrectlyCreated();

   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      //check html node
      IDE.OUTLINE.assertElmentPresentById("html:TAG:4");

      //check head tag and subnodes head
      IDE.OUTLINE.assertElmentPresentById("head:TAG:6");
      IDE.OUTLINE.assertElmentPresentById("meta:TAG:7");
      IDE.OUTLINE.assertElmentPresentById("meta:TAG:8");
      IDE.OUTLINE.assertElmentPresentById("meta:TAG:9");
      IDE.OUTLINE.assertElmentPresentById("meta:TAG:10");
      IDE.OUTLINE.assertElmentPresentById("meta:TAG:11");
      IDE.OUTLINE.assertElmentPresentById("link:TAG:12");
      IDE.OUTLINE.assertElmentPresentById("script:TAG:14");
      IDE.OUTLINE.assertElmentPresentById("title:TAG:16");
      IDE.OUTLINE.assertElmentPresentById("link:TAG:17");
      IDE.OUTLINE.assertElmentPresentById("widget:preferences:TAG:20");
      IDE.OUTLINE.assertElmentPresentById("style:TAG:22");

      //check script tag and subnodes script
      IDE.OUTLINE.assertElmentPresentById("script:TAG:26");
      IDE.OUTLINE.assertElmentPresentById("YourWidgetName:VARIABLE:31");
      IDE.OUTLINE.assertElmentPresentById("function():FUNCTION:37");
      IDE.OUTLINE.assertElmentPresentById("function():FUNCTION:44");

      //check body tag and subnodes body
      IDE.OUTLINE.assertElmentPresentById("body:TAG:50");
      IDE.OUTLINE.assertElmentPresentById("p:TAG:51");

   }

}
