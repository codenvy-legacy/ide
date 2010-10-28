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
import static org.junit.Assert.assertTrue;

import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 28, 2010 $
 *
 */
public class UploadMimeTypeAutoCompletionTest extends BaseTest
{

   @Test
   public void testMimeTypeAutoCompletion() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      String filePath = "src/test/resources/org/exoplatform/ide/operation/file/upload/Example.html";

      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.UPLOAD);

      Thread.sleep(TestConstants.SLEEP);
      try
      {
         File file = new File(filePath);
         selenium.type("//input[@type='file']", file.getCanonicalPath());
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }

      selenium
         .focus("scLocator=//Window[ID=\"ideUploadForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideUploadFormMimeTypeField]/element");
      selenium
         .type(
            "scLocator=//Window[ID=\"ideUploadForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideUploadFormMimeTypeField]/element",
            "t");
      Robot bot = new Robot();

      bot.keyPress(KeyEvent.VK_E);
      bot.keyRelease(KeyEvent.VK_E);
      bot.keyPress(KeyEvent.VK_X);
      bot.keyRelease(KeyEvent.VK_X);
      bot.keyPress(KeyEvent.VK_T);
      bot.keyRelease(KeyEvent.VK_T);
      bot.keyPress(KeyEvent.VK_SLASH);
      bot.keyRelease(KeyEvent.VK_SLASH);
      Thread.sleep(TestConstants.SLEEP_SHORT);

      assertTrue(selenium
         .isElementPresent("scLocator=//Window[ID=\"ideUploadForm\"]/item[0][Class=\"DynamicForm\"]/item[value=text/all]/[icon='picker']"));
      assertTrue(selenium
         .isElementPresent("scLocator=//Window[ID=\"ideUploadForm\"]/item[0][Class=\"DynamicForm\"]/item[value=text/qhtml]/[icon='picker']"));

      selenium
         .click("scLocator=//Window[ID=\"ideUploadForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideUploadFormMimeTypeField]/pickList/body/row[5]/col[0]");
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      assertEquals(
         "text/richtext",
         selenium
            .getValue("scLocator=//Window[ID=\"ideUploadForm\"]/item[0][Class=\"DynamicForm\"]/item[name=ideUploadFormMimeTypeField]/element"));

   }

}
