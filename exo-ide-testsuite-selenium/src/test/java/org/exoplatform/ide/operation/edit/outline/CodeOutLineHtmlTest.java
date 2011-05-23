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
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test for code outline for html files.
 * 
 * @author <a href="mailto:njusha.exo@gmail.com">Nadia Zavalko</a>
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CodeOutLineHtmlTest extends BaseTest
{

   private final static String FILE_NAME = "HtmlCodeOutline.html";

   private final static String FOLDER_NAME = CodeOutLineHtmlTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/HtmlCodeOutline.html";
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

   // IDE-175:Html Code Outline
   @Test
   public void testCodeOutLineHtml() throws Exception
   {
      //---- 1 -----------------
      //open file with text
      IDE.WORKSPACE.waitForItem(URL + FOLDER_NAME + "/");
      IDE.WORKSPACE.doubleClickOnFolder(URL + FOLDER_NAME + "/");
      waitForElementNotPresent(IDE.NAVIGATION.getItemId(URL + FOLDER_NAME + "/" + FILE_NAME));
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);

      //---- 2 ----
      //show Outline
      IDE.TOOLBAR.runCommand("Show Outline");
      waitForElementPresent("ideOutlineTreeGrid");

      //-----3------
      checkTreeCorrectlyCreated();

   }

   private void checkTreeCorrectlyCreated() throws Exception
   {

      //check html node
      IDE.OUTLINE.assertElmentPresentById("html:TAG:1");

      //check head tag and subnodes head
      IDE.OUTLINE.assertElmentPresentById("head:TAG:2");
      IDE.OUTLINE.assertElmentPresentById("meta:TAG:3");
      IDE.OUTLINE.assertElmentPresentById("link:TAG:4");
      IDE.OUTLINE.assertElmentPresentById("title:TAG:5");
      IDE.OUTLINE.assertElmentPresentById("script:TAG:6");
      IDE.OUTLINE.assertElmentPresentById("style:TAG:7");

      //check body tag and subnodes body
      IDE.OUTLINE.assertElmentPresentById("body:TAG:13");

      //check table tag and subnodes table
      IDE.OUTLINE.assertElmentPresentById("table:TAG:14");
      IDE.OUTLINE.assertElmentPresentById("thead:TAG:15");
      IDE.OUTLINE.assertElmentPresentById("tr:TAG:16");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:16");

      //check tbody tag and subnodes tbody
      IDE.OUTLINE.assertElmentPresentById("tbody:TAG:18");
      IDE.OUTLINE.assertElmentPresentById("tr:TAG:19");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:20");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:21");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:22");
      IDE.OUTLINE.assertElmentPresentById("br:TAG:25");
      IDE.OUTLINE.assertElmentPresentById("br:TAG:26");

      //check script tag and subnodes script
      IDE.OUTLINE.assertElmentPresentById("script:TAG:27");
      IDE.OUTLINE.assertElmentPresentById("prefs:VARIABLE:28");
      IDE.OUTLINE.assertElmentPresentById("displayGreeting:FUNCTION:30");
      IDE.OUTLINE.assertElmentPresentById("today:VARIABLE:31");
      IDE.OUTLINE.assertElmentPresentById("html:VARIABLE:33");

      //check style tag
      IDE.OUTLINE.assertElmentPresentById("style:TAG:36");

      //check tr and subnodes tag
      IDE.OUTLINE.assertElmentPresentById("tr:TAG:45");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:46");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:47");

      //check tr and subnodes tag
      IDE.OUTLINE.assertElmentPresentById("tr:TAG:49");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:50");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:51");

   }

}
