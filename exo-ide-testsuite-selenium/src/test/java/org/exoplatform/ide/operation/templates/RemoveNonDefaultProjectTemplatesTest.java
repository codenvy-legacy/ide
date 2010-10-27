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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.exoplatform.ide.operation.templates.TemplateUtils.*;

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
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class RemoveNonDefaultProjectTemplatesTest extends BaseTest
{
   private static final String PROJECT_TEMPLATE_NAME_1 = "Test Project Template";
   
   private static final String PROJECT_TEMPLATE_NAME_2 = "Second Project Template";
   
   private static final String TEMPLATE_URL = BASE_URL + "rest/private/registry/repository/exo:applications/IDE/templates/";
   
   private static String templateUrl1;
   
   private static String templateUrl2;
   
   private static final String PROJECT_TEMPLATE_XML_1 = "<template><name>Test%20Project%20Template</name>"
      + "<description>Project%20template%20for%20test%20purposes</description>"
      + "<template-type>project</template-type><items><folder><name>org</name><items><folder><name>exoplatform</name>"
      + "<items><file><template-file-name>Groovy%20REST%20Service</template-file-name>"
      + "<file-name>Main.groovy</file-name></file><file><template-file-name>Sample%20Template</template-file-name>"
      + "<file-name>Index.html</file-name></file></items></folder></items></folder></items></template>";
   
   private static final String PROJECT_TEMPLATE_XML_2 = "<template><name>Second%20Project%20Template</name>"
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
   
   
   @Test
   public void testDeleteProjectTemplate() throws Exception
   {
      putProjectTemplateToRegistry();
      Thread.sleep(TestConstants.SLEEP);
      //----- 1 ----------------
      //open create project from template form
      runCommandFromMenuNewOnToolbar(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      
      checkCreateProjectFromTemplateForm(selenium);
      
      //----- 2 ----------------
      //select project template from list, type project name, click Create button
      selectProjectTemplate(selenium, PROJECT_TEMPLATE_NAME_1);
      
      //----- 3 ----------------
      //delete project template
      selenium.click(DELETE_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //check delete confirmation dialog
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertEquals("Do you want to delete template " + PROJECT_TEMPLATE_NAME_1 + "?", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/blurb/"));
      //click OK button
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //----- 4 ----------------
      //check template deleted
      checkElementPresentInListGrid(PROJECT_TEMPLATE_NAME_1, false);
      
      //close
      selenium.click(CANCEL_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   @Test
   public void testDeleteSeveralProjectTemplates() throws Exception
   {
      putSeveralProjectTemplatesToRegistry();
      selenium.refresh();
      selenium.waitForPageToLoad("" + TestConstants.IDE_LOAD_PERIOD);
      Thread.sleep(TestConstants.SLEEP);
      
      //------ 1 --------
      //Click on "New->Project From Template..."  toolbar button.
      runCommandFromMenuNewOnToolbar(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      Thread.sleep(TestConstants.SLEEP);
      
      // check "Create project" dialog window
      checkCreateProjectFromTemplateForm(selenium);
      
      //------ 2 --------
      // In "Create project"  window select two project templates, then click "Delete" button.
      TemplateUtils.selectItemInTemplateList(selenium, PROJECT_TEMPLATE_NAME_1);
      selenium.controlKeyDown();
      TemplateUtils.selectItemInTemplateList(selenium, PROJECT_TEMPLATE_NAME_2);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.controlKeyUp();
      
      //click Delete button
      selenium.click(DELETE_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      // check warning dialog appeared
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/noButton/"));
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/"));
      
      //------ 3 --------
      //Click on button "Yes".
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //"Create file" window should contain only default("red") templates.
      assertFalse(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[text()='" 
         + PROJECT_TEMPLATE_NAME_1 + "']"));
      assertFalse(selenium.isElementPresent("//div[@class='windowBody']//table[@class='listTable']//nobr/span[text()='" 
         + PROJECT_TEMPLATE_NAME_2 + "']"));
      
      //------ 4 --------
      // Close "Create project" window.
      selenium.click(CANCEL_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);
   }
   
   private void putProjectTemplateToRegistry()
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

   private void checkElementPresentInListGrid(String projectTemplateName, boolean isPresent) throws Exception
   {
      if (isPresent)
      {
         assertTrue(selenium.isElementPresent(
            "//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[text()='"
               + projectTemplateName + "']"));
      }
      else
      {
         assertFalse(selenium.isElementPresent(
            "//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[text()='" 
            + projectTemplateName + "']"));
      }
   }
   
   private void putSeveralProjectTemplatesToRegistry()
   {
      templateUrl1 = TEMPLATE_URL + "template-" + System.currentTimeMillis();
      templateUrl2 = TEMPLATE_URL + "template-" + System.currentTimeMillis() + 5;
      try
      {
         VirtualFileSystemUtils.put(PROJECT_TEMPLATE_XML_1.getBytes(), templateUrl1 + "/?createIfNotExist=true");
         VirtualFileSystemUtils.put(PROJECT_TEMPLATE_XML_2.getBytes(), templateUrl2 + "/?createIfNotExist=true");
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