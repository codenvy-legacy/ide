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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Test for deleting user file template.
 *	
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RemoveNonDefaultFileTemplatesTest extends BaseTest
{
   private static final String FILE_TEMPLATE_NAME_1 = "test template";
   
   private static final String FILE_TEMPLATE_NAME_2 = "Sample Template";
   
   private static final String TEMPLATE_URL = BASE_URL + "rest/private/registry/repository/exo:applications/IDE/templates/";
   
   private static String templateUrl;
   
   private static String fileTemplateUrl;
   
   private static final String FILE_TEMPLATE_XML_1 = "<template><name>test%20template</name>" 
      + "<description>test%20template</description><template-type>file</template-type>" 
      + "<mime-type>text%2Fxml</mime-type><content>%3C%3Fxml%20version%3D'1.0'%20encoding%3D'UTF-8'%3F%3E%0A</content>"
      + "</template>";
   
   private static final String FILE_TEMPLATE_XML_2 = "<template><name>Sample%20Template</name>" 
      + "<description>Sample%20template</description><template-type>file</template-type>" 
      + "<mime-type>text%2Fxml</mime-type><content>%3C%3Fxml%20version%3D'1.0'%20encoding%3D'UTF-8'%3F%3E%0A</content>"
      + "</template>";
   
   private static final String PROJECT_TEMPLATE_XML = "<template><name>Test%20Project%20Template</name>"
      + "<description>Project%20template%20for%20test%20purposes</description>"
      + "<template-type>project</template-type><items><folder><name>org</name><items><folder><name>exoplatform</name>"
      + "<items><file><template-file-name>Groovy%20REST%20Service</template-file-name>"
      + "<file-name>Main.groovy</file-name></file><file><template-file-name>Sample%20Template</template-file-name>"
      + "<file-name>Index.html</file-name></file></items></folder></items></folder></items></template>";
   
   //IDE-163:Remove non-default file templates
   /**
    * Test added to Igore, because we don't use registry service.
    * We must put file with template to 
    * http://localhost:8080/IDE/rest/private/jcr/repository/production/ide-home/users/templates/fileTemplates
    * or
    * http://localhost:8080/IDE/rest/private/jcr/repository/production/ide-home/users/templates/projectTemplates
    * file.
    * @throws Exception
    */
   @Ignore
   @Test
   public void testRemoveNonDefaultFileTemplates() throws Exception
   {
      putFileTemplateToRegistry();
      IDE.WORKSPACE.waitForItem(WS_URL);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true);
      
      //------ 1 ----------
      //Click on "File->New->From Template..." topmenu item.
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      IDE.TEMPLATES.waitForFileFromTemplateForm();
      
      // check "Create file" dialog window
      IDE.TEMPLATES.checkCreateFileFromTemplateWindow();
      
      //------ 2 ----------
      // In "Create file"  window select "test template", then click "Delete" button.
      IDE.TEMPLATES.selectFileTemplate(FILE_TEMPLATE_NAME_1);
      
      //click Delete button
      IDE.TEMPLATES.clickDeleteButton();
      // check warning dialog appeared
      IDE.ASK_DIALOG.waitForDialog();
      
      //------ 3 ----------
      //Click on button "Yes".
      IDE.ASK_DIALOG.clickYes();
      waitForLoaderDissapeared();
      IDE.ASK_DIALOG.waitForDialogNotPresent();
      IDE.TEMPLATES.waitForTemplateDeleted(FILE_TEMPLATE_NAME_1);
      
      //"Create file" window should contain only default("red") templates.
      IDE.TEMPLATES.checkTemplatePresent(FILE_TEMPLATE_NAME_1, false);
      
      //------ 4 ----------
      // Close "Create file" window, and all opened tabs in content panel.
      IDE.TEMPLATES.clickCancelButton();
   }
   
   /**
    * Test added to Igore, because we don't use registry service.
    * We must put file with template to 
    * http://localhost:8080/IDE/rest/private/jcr/repository/production/ide-home/users/templates/fileTemplates
    * or
    * http://localhost:8080/IDE/rest/private/jcr/repository/production/ide-home/users/templates/projectTemplates
    * file.
    * @throws Exception
    */
   @Test
   public void testDeleteFileTemplateWhichUsedInProjectTemplate() throws Exception
   {
      putFileTemplateWithProjectTemplateToRegistry();
      refresh();
      IDE.WORKSPACE.waitForItem(WS_URL);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true);
      
      //------ 1 --------
      //Click on "File->New->From Template..." topmenu item.
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.FILE_FROM_TEMPLATE);
      IDE.TEMPLATES.waitForFileFromTemplateForm();
      
      // check "Create file" dialog window
      IDE.TEMPLATES.checkCreateFileFromTemplateWindow();
      
      //------ 2 --------
      // In "Create file"  window select "test template", then click "Delete" button.
      IDE.TEMPLATES.selectFileTemplate(FILE_TEMPLATE_NAME_2);
      
      //click Delete button
      IDE.TEMPLATES.clickDeleteButton();
      // check warning dialog appeared
      IDE.ASK_DIALOG.waitForDialog();
      
      //------ 3 --------
      //Click on button "Yes".
      selenium().click("//div[@id='exoAskDialog']//div[@id='YesButton']");
      
      //------ 4 --------
      //check warn dialog, that this template is used in project template
      final String msg = "File template " + FILE_TEMPLATE_NAME_2 + " is used in Test Project Template project template(s)";
      IDE.ASK_DIALOG.waitForDialog(msg);
      
      //Click on button "Yes".
      IDE.ASK_DIALOG.clickYes();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      IDE.TEMPLATES.waitForTemplateDeleted(FILE_TEMPLATE_NAME_2);
      
      //"Create file" window should contain only default("red") templates.
      IDE.TEMPLATES.checkTemplatePresent(FILE_TEMPLATE_NAME_2, false);
      
      //------ 5 --------
      // Close "Create file" window, and all opened tabs in content panel.
      IDE.TEMPLATES.clickCancelButton();
   }
   
   private void putFileTemplateToRegistry()
   {
      fileTemplateUrl = TEMPLATE_URL + "template-" + System.currentTimeMillis();
      try
      {
         VirtualFileSystemUtils.put(FILE_TEMPLATE_XML_1.getBytes(), fileTemplateUrl + "/?createIfNotExist=true");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
   
   private void putFileTemplateWithProjectTemplateToRegistry()
   {
      templateUrl = TEMPLATE_URL + "template-" + System.currentTimeMillis();
      fileTemplateUrl = TEMPLATE_URL + "template-" + System.currentTimeMillis() + 5;
      try
      {
         VirtualFileSystemUtils.put(FILE_TEMPLATE_XML_2.getBytes(), fileTemplateUrl + "/?createIfNotExist=true");
         VirtualFileSystemUtils.put(PROJECT_TEMPLATE_XML.getBytes(), templateUrl + "/?createIfNotExist=true");
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }
}