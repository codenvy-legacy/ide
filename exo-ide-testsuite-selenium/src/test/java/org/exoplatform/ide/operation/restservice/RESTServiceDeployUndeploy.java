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
public class RESTServiceDeployUndeploy extends BaseTest
{

 private static String FILE_NAME = "Example.groovy";
   
   @Test
   public void testDeployUndeploy() throws Exception
   {
      Thread.sleep(1000);
      openNewFileFromToolbar("REST Service");
      Thread.sleep(1000);
      
      saveAsUsingToolbarButton(FILE_NAME);
      Thread.sleep(1000);      
      
      closeTab("0");
            
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME);
      
      runToolbarButton(MenuCommands.Run.DEPLOY_REST_SERVICE);
      Thread.sleep(1500);
      
      assertTrue(selenium.isElementPresent("scLocator=//VLayout[ID=\"ideOutputForm\"]/"));
      
      assertEquals("[INFO] http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/"+FILE_NAME+" deployed successfully.", selenium.getText("//div[contains(@eventproxy,'Record_0')]"));
      
      runToolbarButton(MenuCommands.Run.UNDEPLOY_REST_SERVICE);
      Thread.sleep(1000);
      
      assertEquals("[INFO] http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/"+FILE_NAME+" undeployed successfully.", selenium.getText("//div[contains(@eventproxy,'Record_1')]"));

      runTopMenuCommand("Run", "Undeploy");
      Thread.sleep(1000);
      
      String mess = selenium.getText("//div[contains(@eventproxy,'Record_2')]");
      assertTrue(mess.contains("[ERROR] http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/"+FILE_NAME+" undeploy failed. Error (400: Bad Request)"));
      assertTrue(mess.contains("Can't unbind script Example.groovy, not bound or has wrong mapping to the resource class"));
      
      closeTab("0");
      
      selectItemInWorkspaceTree(FILE_NAME);
      
      deleteSelectedItem();
   }
   
}
