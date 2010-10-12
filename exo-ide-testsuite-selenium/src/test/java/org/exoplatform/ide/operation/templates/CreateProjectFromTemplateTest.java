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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CreateProjectFromTemplateTest extends BaseTest
{
   private static final String CREATE_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/";
   
   private static final String DELETE_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/";
   
   private static final String CANCEL_BUTTON_LOCATOR = "scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/";
   
   private static final String PROJECT_NAME = "Sample Project";
   
   private static final String PROJECT_TEMPLATE_NAME = "Test Project Template";
   
   private static final String FOLDER_ORG = "org";
   
   private static final String FOLDER_EXOPLATFORM = "exoplatform";
   
   private static final String FILE_GROOVY = "Main.groovy";
   
   private static final String FILE_HTML = "Index.html";
   
   private static final String URL = BASE_URL + "rest/private/registry/repository/exo:applications/IDE/templates/";
   
   private final static String PROJECT_FOLDER_URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + PROJECT_NAME;
   
   private static String templateUrl;
   
   private static final String PROJECT_TEMPLATE_XML = "<template><name>Test%20Project%20Template</name>"
      + "<description>Project%20template%20for%20test%20purposes</description>"
      + "<template-type>project</template-type><items><folder><name>org</name><items><folder><name>exoplatform</name>"
      + "<items><file><template-file-name>Groovy%20REST%20Service</template-file-name>"
      + "<file-name>Main.groovy</file-name></file><file><template-file-name>Empty%20HTML</template-file-name>"
      + "<file-name>Index.html</file-name></file></items></folder></items></folder></items></template>";
   
   @Before
   public void setUp()
   {
      templateUrl = URL + "template-" + System.currentTimeMillis();
      try
      {
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
   
   @After
   public void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(PROJECT_FOLDER_URL);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
      
      cleanRegistry();
      
   }
   
   @Test
   public void testCreateProjectFromTemplate() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //----- 1 ----------------
      //open create project from template form
      runCommandFromMenuNewOnToolbar(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      
      checkCreateProjectFromTemplateForm();
      
      //----- 2 ----------------
      //select project template from list, type project name, click Create button
      selectProjectTemplate(PROJECT_TEMPLATE_NAME);
      typeProjectName(PROJECT_NAME);
      
      selenium.click(CREATE_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 3 ----------------
      //check new project created
      assertElementPresentInWorkspaceTree(PROJECT_NAME);
      
      clickOpenIconOfFolder(PROJECT_NAME);
      assertElementPresentInWorkspaceTree(FOLDER_ORG);
      
      clickOpenIconOfFolder(FOLDER_ORG);
      assertElementPresentInWorkspaceTree(FOLDER_EXOPLATFORM);
      
      clickOpenIconOfFolder(FOLDER_EXOPLATFORM);
      assertElementPresentInWorkspaceTree(FILE_GROOVY);
      assertElementPresentInWorkspaceTree(FILE_HTML);
   }
   
   @Test
   public void testDeleteProjectTemplate() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //----- 1 ----------------
      //open create project from template form
      runCommandFromMenuNewOnToolbar(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      
      checkCreateProjectFromTemplateForm();
      
      //----- 2 ----------------
      //select project template from list, type project name, click Create button
      selectProjectTemplate(PROJECT_TEMPLATE_NAME);
      
      //----- 3 ----------------
      //delete project template
      selenium.click(DELETE_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      //confirm deletion
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertEquals("Do you want to delete template " + PROJECT_TEMPLATE_NAME + "?", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/blurb/"));
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/yesButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      assertTrue(selenium.isElementPresent("scLocator=//Dialog[ID=\"isc_globalWarn\"]/"));
      assertEquals("Template " + PROJECT_TEMPLATE_NAME + " deleted.", selenium.getText("scLocator=//Dialog[ID=\"isc_globalWarn\"]/blurb/"));
      selenium.click("scLocator=//Dialog[ID=\"isc_globalWarn\"]/okButton/");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   
      
      //----- 4 ----------------
      //check template deleted
      checkElementPresentInListGrid(PROJECT_TEMPLATE_NAME, false);
      
      //close
      selenium.click(CANCEL_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   

   private void checkElementPresentInListGrid(String projectTemplateName, boolean isPresent) throws Exception
   {
      if (isPresent)
      {
         assertTrue(selenium
            .isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[text()='"
               + projectTemplateName + "']"));
      }
      else
      {
         assertFalse(selenium.isElementPresent("//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[text()='" 
            + projectTemplateName + "']"));
      }
   }
   
   private void clickOpenIconOfFolder(String folderName) throws Exception
   {
      selenium.click("scLocator=//TreeGrid[ID=\"ideNavigatorItemTreeGrid\"]/body/row[name=" + folderName
         + "]/col[0]/open");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
   }
   
   private void typeProjectName(String projectName) throws Exception
   {
      selenium.type("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[" 
         + "name=ideCreateFileFromTemplateFormFileNameField]/element", projectName);
      
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }
   
   private void checkCreateProjectFromTemplateForm()
   {
      assertTrue(selenium.isElementPresent("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/"));
      assertEquals("Create project", selenium.getText("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/"));
      assertTrue(selenium.isElementPresent(DELETE_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(CREATE_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(CANCEL_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element"));
   }
   
   private void selectProjectTemplate(String projectTemplateName) throws Exception
   {
      selenium.mouseDownAt("//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[text()='" 
         + projectTemplateName + "']", "");
      selenium.mouseUpAt("//div[@eventproxy='ideCreateFileFromTemplateFormTemplateListGrid_body']//span[text()='"
         + projectTemplateName + "']", "");
      
      Thread.sleep(TestConstants.ANIMATION_PERIOD);
   }

}