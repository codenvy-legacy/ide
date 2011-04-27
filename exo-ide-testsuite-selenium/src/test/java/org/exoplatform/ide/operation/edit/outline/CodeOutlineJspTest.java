/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeOutlineJspTest Apr 27, 2011 10:47:14 AM evgen $
 *
 */
public class CodeOutlineJspTest extends BaseTest
{
   
private final static String FILE_NAME = "JspCodeOutline.jsp";
   
   private final static String FOLDER_NAME = CodeOutlineJspTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
   @BeforeClass
   public static void setUp()
   {
      String filePath ="src/test/resources/org/exoplatform/ide/operation/edit/outline/test-jsp.jsp";
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(filePath, MimeType.APPLICATION_JSP, URL + FOLDER_NAME + "/" + FILE_NAME);
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
   
   @Test
   public void testCodeOutlineJSP() throws Exception
   {
      waitForRootElement();
      IDE.NAVIGATION.clickOpenIconOfFolder(WS_URL + FOLDER_NAME + "/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" +FILE_NAME, false);
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      waitForElementPresent(Locators.CodeHelperPanel.OUTLINE_TAB_LOCATOR);
      
      IDE.OUTLINE.assertOutlineTreePresent();
      
      IDE.OUTLINE.assertElmentPresentById("html:TAG:1");
      goToLine(9);
      
      waitForElementPresent("a:VARIABLE:9");
      IDE.OUTLINE.assertElmentPresentById("a:VARIABLE:9");
      
      goToLine(23);
      
      waitForElementPresent("a:PROPERTY:23");
      IDE.OUTLINE.assertElmentPresentById("a:PROPERTY:23");
      IDE.OUTLINE.assertElmentPresentById("head:TAG:2");
      IDE.OUTLINE.assertElmentPresentById("script:TAG:8");
      IDE.OUTLINE.assertElmentPresentById("body:TAG:12");
      IDE.OUTLINE.assertElmentPresentById("java code:JSP_TAG:13");
      IDE.OUTLINE.assertElmentPresentById("curentState:PROPERTY:14");
      IDE.OUTLINE.assertElmentPresentById("identity:PROPERTY:17");
      IDE.OUTLINE.assertElmentPresentById("i:PROPERTY:18");      
      
      IDE.EDITOR.closeTab(0);
   }

}
