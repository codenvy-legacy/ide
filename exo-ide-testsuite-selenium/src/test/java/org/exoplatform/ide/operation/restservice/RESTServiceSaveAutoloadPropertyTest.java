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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

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
public class RESTServiceSaveAutoloadPropertyTest extends BaseTest
{

   private static final String FILE_NAME = System.currentTimeMillis() + ".groovy";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + FILE_NAME;
   
   @Test
   public void testAutoload() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      createFileFromToolbar("REST Service");
      Thread.sleep(TestConstants.SLEEP);
      
      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(TestConstants.SLEEP);      
      
      closeTab("0");
            
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      
      runTopMenuCommand("Run", MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      
      String mess = selenium.getText("//font[@color='#880000']");
      
      assertTrue(mess.contains("[ERROR]"));
      assertTrue(mess.contains(FILE_NAME + " undeploy failed. Error (400: Bad Request)"));
      assertTrue(mess.contains("Can't unbind script " + FILE_NAME + ", not bound or has wrong mapping to the resource class"));
      
      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.SET_AUTOLOAD);
      Thread.sleep(TestConstants.SLEEP);
      
      checkToolbarButtonState("Unset REST Service Autoload", true);
      
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);
      
      assertEquals("true", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextAutoload||title=%3Cb%3EAutoload%3C%24fs%24b%3E||value=false||index=0||Class=StaticTextItem]/textbox"));
      
      AbstractTextUtil.getInstance().typeTextToEditor(TestConstants.CODEMIRROR_EDITOR_LOCATOR, "/// test comment 1\n");
      
      saveCurrentFile();
      Thread.sleep(TestConstants.SLEEP);
      
      closeTab("0");
      
      selectRootOfWorkspaceTree();
      
      runTopMenuCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      
      runTopMenuCommand(MenuCommands.Run.RUN, MenuCommands.Run.UNDEPLOY_REST_SERVICE);
     
      mess = selenium.getText("//font[@color='#007700']");
      
      assertTrue(mess.contains("[INFO]"));
      assertTrue(mess.contains(FILE_NAME + " undeployed successfully."));
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         Utils.undeployService(BASE_URL, REST_CONTEXT, URL);
         VirtualFileSystemUtils.delete(URL);
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
