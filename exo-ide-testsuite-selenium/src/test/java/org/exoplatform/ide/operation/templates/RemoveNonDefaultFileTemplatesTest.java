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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
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
   
   @After
   public void tearDown()
   {
      cleanRegistry();
   }
   
   
   //IDE-163:Remove non-default file templates
   @Test
   public void testRemoveNonDefaultFileTemplates() throws Exception
   {
      putFileTemplateToRegistry();
      Thread.sleep(TestConstants.SLEEP);
      
      //------ 1 ----------
      //Click on "File->New->From Template..." topmenu item.
      runCommandFromMenuNewOnToolbar(MenuCommands.New.FILE_FROM_TEMPLATE);
      Thread.sleep(TestConstants.SLEEP);
      
      // check "Create file" dialog window
      TemplateUtils.checkCreateFileFromTemplateWindow(selenium);
      
      //------ 2 ----------
      // In "Create file"  window select "test template", then click "Delete" button.
      TemplateUtils.selectItemInTemplateList(selenium, FILE_TEMPLATE_NAME_1);
      
      //click Delete button
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/");
      // check warning dialog appeared
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      
      //------ 3 ----------
      //Click on button "Yes".
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      
      // must shown dialog that informs that template was deleted.
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/"));
      assertTrue(selenium.isTextPresent("Template test template deleted."));
      
      //------ 4 ----------
      // Click on button "Ok".
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      
      //"Create file" window should contain only default("red") templates.
      assertFalse(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[text()='" + FILE_TEMPLATE_NAME_1 + "']"));
      
      //------ 5 ----------
      // Close "Create file" window, and all opened tabs in content panel.
      selenium
         .click("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/member[Class=Canvas||index=0||length=3||classIndex=0||classLength=1]/");
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/");
      closeTab("0");
      Thread.sleep(TestConstants.SLEEP);
   }
   
   @Test
   public void testDeleteFileTemplateWhichUsedInProjectTemplate() throws Exception
   {
      putFileTemplateWithProjectTemplateToRegistry();
      Thread.sleep(TestConstants.SLEEP);
      
      //------ 1 --------
      //Click on "File->New->From Template..." topmenu item.
      runCommandFromMenuNewOnToolbar(MenuCommands.New.FILE_FROM_TEMPLATE);
      Thread.sleep(TestConstants.SLEEP);
      
      // check "Create file" dialog window
      TemplateUtils.checkCreateFileFromTemplateWindow(selenium);
      
      //------ 2 --------
      // In "Create file"  window select "test template", then click "Delete" button.
      TemplateUtils.selectItemInTemplateList(selenium, FILE_TEMPLATE_NAME_2);
      
      //click Delete button
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      // check warning dialog appeared
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      
      //------ 3 --------
      //Click on button "Yes".
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //------ 4 --------
      //check warn dialog, that this template is used in project template
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      assertTrue(selenium.isTextPresent("File template " + FILE_TEMPLATE_NAME_2 + " is used"));
      
      //Click on button "Yes".
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      // must shown dialog that informs that template was deleted.
      Thread.sleep(TestConstants.SLEEP);
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/"));
      assertTrue(selenium.isTextPresent("Template " + FILE_TEMPLATE_NAME_2 + " deleted"));
      
      //------ 5 --------
      // Click on button "Ok".
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //"Create file" window should contain only default("red") templates.
      assertFalse(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[text()='" + FILE_TEMPLATE_NAME_2 + "']"));
      
      //------ 6 --------
      // Close "Create file" window, and all opened tabs in content panel.
      selenium.click("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/");
      Thread.sleep(TestConstants.SLEEP);
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
      catch (ModuleException e)
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
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }
}