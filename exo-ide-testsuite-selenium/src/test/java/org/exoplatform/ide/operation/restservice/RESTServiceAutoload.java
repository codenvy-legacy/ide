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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceAutoload extends BaseTest
{

   private static String FILE_NAME = "test.groovy";
   
   @Test
   public void testAutoload() throws Exception
   {
      Thread.sleep(1000);
      openNewFileFromToolbar("REST Service");
      Thread.sleep(1000);
      
      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(1000);      
      
      closeTab("0");
            
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME);
      
      clickOnToolbarButton(MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      
      String mess = selenium.getText("//font[@color='#880000']");
      
      assertTrue(mess.contains("[ERROR] http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/test.groovy undeploy failed. Error (400: Bad Request)"));
      assertTrue(mess.contains("Can't unbind script test.groovy, not bound or has wrong mapping to the resource class"));
      
      clickOnToolbarButton(MenuCommands.Run.SET_AUTOLOAD);
      
      checkToolbarButtonState(MenuCommands.Run.UNSET_AUTOLOAD, true);
      
      clickOnToolbarButton(MenuCommands.View.SHOW_PROPERTIES);
      
      assertEquals("true", selenium.getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextAutoload||title=%3Cb%3EAutoload%3C%24fs%24b%3E||value=false||index=0||Class=StaticTextItem]/textbox"));
      
      typeText("/// test comment 1\n");
      
      saveCurrentFile();
      Thread.sleep(1000);
      
      closeTab("0");
      
      selectRootOfWorkspaceTree();
      
      clickOnToolbarButton(MenuCommands.File.REFRESH);
      Thread.sleep(1000);
      
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME);
      
      clickOnToolbarButton(MenuCommands.Run.UNDEPLOY_REST_SERVICE);
     
      assertEquals("[INFO] http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/test.groovy undeployed successfully.", selenium.getText("//font[@color='#007700']"));
      
      closeTab("0");
      
      selectItemInWorkspaceTree(FILE_NAME);
      
      deleteSelectedItem();
   }
   
}
