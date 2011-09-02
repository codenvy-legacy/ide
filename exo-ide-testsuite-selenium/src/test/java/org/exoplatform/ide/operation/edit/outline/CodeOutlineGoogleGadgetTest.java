/*
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

import org.everrest.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeOutlineGoogleGadgetTest extends BaseTest
{
   private final static String FILE_NAME = "GoogleGadgetCodeOutline.xml";

   private final static String TEST_FOLDER = CodeOutlineGoogleGadgetTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @BeforeClass
   public static void setUp()
   {

      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/GoogleGadgetCodeOutline.xml";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(filePath, MimeType.GOOGLE_GADGET, URL + TEST_FOLDER + "/" + FILE_NAME);
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

   //IDE-173 : Google Gadget Code Outline
   @Test
   public void testCodeOutlineGoogleGadget() throws Exception
   {
      //---- 1 -----------------
      //open file with text
      IDE.WORKSPACE.waitForItem(URL);
      
      IDE.WORKSPACE.doubleClickOnFolder(URL + TEST_FOLDER + "/");
      waitForElementNotPresent(IDE.NAVIGATION.getItemId(URL + TEST_FOLDER + "/" + FILE_NAME));
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME, false);

      //---- 2 -----------------
      IDE.TOOLBAR.runCommand("Show Outline");
      waitForElementPresent("ideOutlineTreeGrid");

      //---- 3 -----------------
      checkTreeCorrectlyCreated();

   }

   private void checkTreeCorrectlyCreated() throws Exception
   {
      //check module tag and subnode
      IDE.OUTLINE.assertElmentPresentById("Module:TAG:2");
      IDE.OUTLINE.assertElmentPresentById("ModulePrefs:TAG:3");

      IDE.OUTLINE.assertElmentPresentById("Content:TAG:4");
      IDE.OUTLINE.assertElmentPresentById("CDATA:CDATA:5");
      IDE.OUTLINE.assertElmentPresentById("html:TAG:6");

      //check head tag and subnodes
      IDE.OUTLINE.assertElmentPresentById("head:TAG:7");
      IDE.OUTLINE.assertElmentPresentById("meta:TAG:8");
      IDE.OUTLINE.assertElmentPresentById("link:TAG:9");
      IDE.OUTLINE.assertElmentPresentById("title:TAG:10");

      //check script tag and subnodes
      IDE.OUTLINE.assertElmentPresentById("script:TAG:11");
      IDE.OUTLINE.assertElmentPresentById("a:VARIABLE:12");

      IDE.OUTLINE.assertElmentPresentById("style:TAG:14");

      //check body tag and subnodes
      IDE.OUTLINE.assertElmentPresentById("body:TAG:20");
      IDE.OUTLINE.assertElmentPresentById("table:TAG:21");
      IDE.OUTLINE.assertElmentPresentById("thead:TAG:22");
      IDE.OUTLINE.assertElmentPresentById("tr:TAG:23");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:23");

      //check tbody tag and subnodes
      IDE.OUTLINE.assertElmentPresentById("tbody:TAG:25");
      IDE.OUTLINE.assertElmentPresentById("tr:TAG:26");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:27");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:28");
      IDE.OUTLINE.assertElmentPresentById("td:TAG:29");
      IDE.OUTLINE.assertElmentPresentById("br:TAG:32");
      IDE.OUTLINE.assertElmentPresentById("br:TAG:33");
      IDE.OUTLINE.assertElmentPresentById("style:TAG:34");
      IDE.OUTLINE.assertElmentPresentById("script:TAG:40");
      IDE.OUTLINE.assertElmentPresentById("b:VARIABLE:41");

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
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
}