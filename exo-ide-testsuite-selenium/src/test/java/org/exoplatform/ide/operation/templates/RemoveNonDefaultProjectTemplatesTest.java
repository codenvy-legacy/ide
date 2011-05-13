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
package org.exoplatform.ide.operation.templates;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for deleting non-default project templates.
 *	
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RemoveNonDefaultProjectTemplatesTest extends BaseTest
{
   private static final String PROJECT_TEMPLATE_NAME_1 = "Test Project Template";
   
   private static final String TEMPLATE_URL = BASE_URL + "rest/private/registry/repository/exo:applications/IDE/templates/";
   
   private static String templateUrl1;
   
   private static final String PROJECT_TEMPLATE_XML_1 = "<template><name>Test%20Project%20Template</name>"
      + "<description>Project%20template%20for%20test%20purposes</description>"
      + "<template-type>project</template-type><items><folder><name>org</name><items><folder><name>exoplatform</name>"
      + "<items><file><template-file-name>Groovy%20REST%20Service</template-file-name>"
      + "<file-name>Main.groovy</file-name></file><file><template-file-name>Sample%20Template</template-file-name>"
      + "<file-name>Index.html</file-name></file></items></folder></items></folder></items></template>";
   
   @BeforeClass
   public static void setUp()
   {
      templateUrl1 = TEMPLATE_URL + "template-" + System.currentTimeMillis();
      try
      {
         VirtualFileSystemUtils.put(PROJECT_TEMPLATE_XML_1.getBytes(), templateUrl1 + "/?createIfNotExist=true");
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
      cleanRegistry();
   }
   
   
   @Test
   public void testDeleteProjectTemplate() throws Exception
   {
      IDE.NAVIGATION.waitForItem(WS_URL);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true, TestConstants.WAIT_PERIOD * 10);
      //----- 1 ----------------
      //open create project from template form
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      IDE.TEMPLATES.waitForProjectCreateForm();
      IDE.TEMPLATES.checkProjectCreateForm();
      
      //----- 2 ----------------
      //select project template from list, type project name, click Create button
      IDE.TEMPLATES.selectProjectTemplate(PROJECT_TEMPLATE_NAME_1);
      
      //----- 3 ----------------
      //delete project template
      IDE.TEMPLATES.clickDeleteButton();
      
      //check delete confirmation dialog
      IDE.ASK_DIALOG.waitForDialog("Do you want to delete template " + PROJECT_TEMPLATE_NAME_1 + "?");
      //click OK button
      IDE.ASK_DIALOG.clickYes();
      waitForLoaderDissapeared();
      IDE.ASK_DIALOG.waitForDialogNotPresent();
      IDE.TEMPLATES.waitForTemplateDeleted(PROJECT_TEMPLATE_NAME_1);
      
      //----- 4 ----------------
      //check template deleted
      IDE.TEMPLATES.checkTemplatePresent(PROJECT_TEMPLATE_NAME_1, false);
      
      //close
      IDE.TEMPLATES.clickCancelButton();
   }
}