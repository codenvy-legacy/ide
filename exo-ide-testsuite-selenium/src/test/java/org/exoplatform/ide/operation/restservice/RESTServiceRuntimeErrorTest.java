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
package org.exoplatform.ide.operation.restservice;

import static org.junit.Assert.*;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.utils.AbstractTextUtil;
import org.junit.AfterClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceRuntimeErrorTest extends BaseTest
{

   private static final String FILE_NAME = "RESTServiceRuntimeErrorTest.groovy";

   @Test
   public void testDeployUndeploy() throws Exception
   {
      
      Thread.sleep(TestConstants.SLEEP);
      //*************change*********
      createFolder("RuntimeError");
      //***************************
      Thread.sleep(TestConstants.SLEEP);
      runCommandFromMenuNewOnToolbar("REST Service");
      Thread.sleep(TestConstants.SLEEP);

      for (int i = 0; i < 10; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      }
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_END);

      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, " / 0");

      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);

      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      runToolbarButton(MenuCommands.Run.LAUNCH_REST_SERVICE);
      Thread.sleep(TestConstants.SLEEP);

      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServicePath||title=ideGroovyServicePath]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("//nobr[contains(text(), '/helloworld/{name}')]");

      selenium
         .click("scLocator=//DynamicForm[ID=\"ideGroovyServiceForm\"]/item[name=ideGroovyServiceMethod]/[icon='picker']");
      Thread.sleep(TestConstants.SLEEP);

      selenium.click("//nobr[contains(text(), 'GET')]");

      selenium.click("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]");
      selenium.click("scLocator=//IButton[ID=\"ideGroovyServiceSend\"]");

      Thread.sleep(TestConstants.SLEEP);

      assertFalse(selenium.isElementPresent("scLocator=//Window[ID=\"ideGroovyServiceOutputPreviewForm\"]"));
      Thread.sleep(TestConstants.SLEEP);

      String mess = selenium.getText("//div[contains(@eventproxy,'Record_1')]");
      assertTrue(mess
         .startsWith("[ERROR]"));
      assertTrue(mess.contains("helloworld/{name} 500 Internal Server Error"));
   }
   
   @AfterClass
   public static void tearDown()
   {
      String url = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + FILE_NAME;
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, url);
         VirtualFileSystemUtils.delete(url);
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
