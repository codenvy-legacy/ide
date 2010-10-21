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
package org.exoplatform.ide.operation.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

import java.io.File;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class OpenLocalFileTest extends BaseTest
{
   private static final String MIME_TYPE_LOCATOR = "scLocator=//Window[ID=\"ideUploadForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideUploadFormMimeTypeField]/element";
   
   private static final String FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/upload/test";
   
   @Test
   public void testOpenFileWithoutExtention() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(WS_NAME);
      Thread.sleep(TestConstants.ANIMATION_PERIOD);

      //call Open Local File form
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.OPEN_LOCAL_FILE);
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideUploadForm\"]"));
      checkUploadButtonEnabled(false);
      
      //select file from local driver without file extention
      try
      {
         File file = new File(FILE_PATH);
         selenium.type("//input[@type='file']", file.getCanonicalPath());
      }
      catch (Exception e)
      {
      }
      Thread.sleep(TestConstants.SLEEP);

      assertEquals(FILE_PATH.substring(FILE_PATH.lastIndexOf("/") + 1, FILE_PATH.length()), selenium
         .getValue("scLocator=//DynamicForm[ID=\"ideUploadFormDynamicForm\"]/item[name=ideUploadFormFilenameField]/element"));
      assertEquals("", selenium.getText(MIME_TYPE_LOCATOR));
      checkUploadButtonEnabled(false);

      //select mime type
      selenium.type(MIME_TYPE_LOCATOR, "text/html");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkUploadButtonEnabled(true);
      
      //close form
      selenium.click("scLocator=//IButton[ID=\"ideUploadFormCloseButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideUploadForm\"]/"));

   }
   
   private void checkUploadButtonEnabled(boolean enabled)
   {
      if (enabled)
      {
         assertFalse(selenium.isElementPresent(
            "//div[@eventproxy='ideUploadFormUploadButton']//td[@class='buttonTitleDisabled' and text()='Open']"));
         assertTrue(selenium.isElementPresent(
         "//div[@eventproxy='ideUploadFormUploadButton']//td[@class='buttonTitle' and text()='Open']"));
      }
      else
      {
         assertTrue(selenium.isElementPresent(
            "//div[@eventproxy='ideUploadFormUploadButton']//td[@class='buttonTitleDisabled' and text()='Open']"));
         assertFalse(selenium.isElementPresent(
            "//div[@eventproxy='ideUploadFormUploadButton']//td[@class='buttonTitle' and text()='Open']"));
      }
   }
}
