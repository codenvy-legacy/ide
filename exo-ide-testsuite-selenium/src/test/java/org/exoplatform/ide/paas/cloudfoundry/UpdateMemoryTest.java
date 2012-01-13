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

package org.exoplatform.ide.paas.cloudfoundry;

import junit.framework.Assert;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UpdateMemoryTest extends CloudFoundryTest
{

   private static final String TEST_FOLDER = "cloudfoundry-test-update-memory-" + System.currentTimeMillis();

   private static final String PROJECT_NAME = "java-spring-project";

   @Before
   public void setUp() throws Exception
   {
      try
      {
         resetMockService();
      }
      catch (Exception e)
      {
      }
   }

   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(REST_WORKSPACE_URL + TEST_FOLDER);
      }
      catch (Exception e)
      {
      }
   }

  // @Test
   public void testUpdateMemory() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();

      IDE.NAVIGATION.createFolder(TEST_FOLDER);
      uploadResource("src/test/resources/org/exoplatform/ide/paas/cloudfoundry/java-spring-project.zip", REPO_NAME
         + "/" + WS_NAME + "/" + TEST_FOLDER + "/java-spring-project.zip");

      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      Thread.sleep(TestConstants.FOLDER_REFRESH_PERIOD);

      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/" + PROJECT_NAME + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      if (IDE.OUTPUT.isOpened())
      {
         IDE.OUTPUT.clickClearButton();
      }

      IDE.MENU.runCommand(MenuCommands.PaaS.PAAS, MenuCommands.PaaS.CloudFoundry.CLOUDFOUNDRY,
         MenuCommands.PaaS.CloudFoundry.UPDATE_MEMORY);

      /*
      Uncomment AskForValue dialog using for version 1.2.0-M4 
      */
      /*
      IDE.ASK_FOR_VALUE_DIALOG.waitForPresent();
      IDE.ASK_FOR_VALUE_DIALOG.setValue("789");
      IDE.ASK_FOR_VALUE_DIALOG.clickOkButton();
      IDE.ASK_FOR_VALUE_DIALOG.waitForAskDialogNotPresent();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      */

      waitForElementPresent("exoAskForValueDialog");
      IDE.INPUT.typeToElement(
         driver.findElement(By.xpath("//div[@id='exoAskForValueDialog']//input[@name='valueField']")), "789", true);
      selenium().click("OkButton");
      waitForElementNotPresent("exoAskForValueDialog");
      Thread.sleep(1000);

      String outputMessage = IDE.OUTPUT.getOutputMessage(1);
      Assert.assertEquals("[INFO] Amount of memory set to 789 megabytes.", outputMessage);
   }

}
