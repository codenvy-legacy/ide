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

import static org.exoplatform.ide.operation.templates.TemplateUtils.*;

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
   private static final String PROJECT_NAME = "My Project";
   
   private static final String PROJECT_TEMPLATE_NAME = "Test Project Template";
   
   private static final String PROJECT_TEMPLATE_NAME_2 = "Project Template 2";
   
   private static final String PROJECT_FROM_DEFAULT_TEMPLATE = "Greeting Project";
   
   private static final String DEFAULT_PROJECT_TEMPLATE_NAME = "Sample project";
   
   private static final String FOLDER_ORG = "org";
   
   private static final String FOLDER_EXOPLATFORM = "exoplatform";
   
   private static final String FILE_GROOVY = "Main.groovy";
   
   private static final String FILE_HTML = "Index.html";
   
   private static final String URL = BASE_URL + "rest/private/registry/repository/exo:applications/IDE/templates/";
   
   private final static String PROJECT_FOLDER_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + PROJECT_NAME;
   
   private static String templateUrl;
   
   private static final String PROJECT_TEMPLATE_XML = "<template><name>Test%20Project%20Template</name>"
      + "<description>Project%20template%20for%20test%20purposes</description>"
      + "<template-type>project</template-type><items><folder><name>org</name><items><folder><name>exoplatform</name>"
      + "<items><file><template-file-name>Groovy%20REST%20Service</template-file-name>"
      + "<file-name>Main.groovy</file-name></file><file><template-file-name>Empty%20HTML</template-file-name>"
      + "<file-name>Index.html</file-name></file></items></folder></items></folder></items></template>";
   
   private static final String PROJECT_TEMPLATE_XML_2 = "<template><name>Project%20Template%202</name>"
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
      cleanRegistry();
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
      
      try
      {
         VirtualFileSystemUtils.delete(PROJECT_FROM_DEFAULT_TEMPLATE);
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
   
   @Test
   public void testCreateProjectFromTemplate() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      //----- 1 ----------------
      //open create project from template form
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      
      checkCreateProjectFromTemplateForm(selenium);
      
      //----- 2 ----------------
      //select project template from list, type project name, click Create button
      selectProjectTemplate(selenium, PROJECT_TEMPLATE_NAME);
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
   
   /**
    * Issue IDE-352
    * @throws Exception
    */
   @Test
   public void createDefaultSampleProject() throws Exception
   {
      refresh();
      //----- 1 ----------------
      //open create project from template form
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      
      checkCreateProjectFromTemplateForm(selenium);
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 2 ----------------
      //select project template from list, type project name, click Create button
      selectProjectTemplate(selenium, DEFAULT_PROJECT_TEMPLATE_NAME);
      typeProjectName(PROJECT_FROM_DEFAULT_TEMPLATE);
      
      selenium.click(CREATE_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);
      
      //----- 3 ----------------
      //check sample project created
      assertElementPresentInWorkspaceTree(PROJECT_FROM_DEFAULT_TEMPLATE);
      
      clickOpenIconOfFolder(PROJECT_FROM_DEFAULT_TEMPLATE);
      assertElementPresentInWorkspaceTree("data");
      assertElementPresentInWorkspaceTree("business logic");
      assertElementPresentInWorkspaceTree("UI");
      
      clickOpenIconOfFolder("business logic");
      assertElementPresentInWorkspaceTree("Greeting REST Service.groovy");
      
      clickOpenIconOfFolder("UI");
      assertElementPresentInWorkspaceTree("Greeting Google Gadget.xml");
   }
   
   @Test
   public void testEnablingDisablingElements() throws Exception
   {
      putProjectTemplateToRegistry();
      refresh();
      
      //----- 1 ----------------
      //open create project from template form
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      
      checkNameFieldEnabled(selenium, false);
      checkDeleteButtonEnabled(selenium, false);
      checkCreateButtonEnabled(selenium, false);
      
      //----- 2 ----------------
      //select one template
      selectProjectTemplate(selenium, PROJECT_TEMPLATE_NAME);
      
      checkNameFieldEnabled(selenium, true);
      checkDeleteButtonEnabled(selenium, true);
      checkCreateButtonEnabled(selenium, true);
      
      //----- 3 ----------------
      //deselect one template
      selenium.controlKeyDown();
      selectProjectTemplate(selenium, PROJECT_TEMPLATE_NAME);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkNameFieldEnabled(selenium, false);
      checkDeleteButtonEnabled(selenium, false);
      checkCreateButtonEnabled(selenium, false);
      
      //----- 4 ----------------
      //select several templates (one is default)
      selectProjectTemplate(selenium, DEFAULT_PROJECT_TEMPLATE_NAME);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.controlKeyDown();
      selectProjectTemplate(selenium, PROJECT_TEMPLATE_NAME_2);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.controlKeyUp();
      
      checkNameFieldEnabled(selenium, false);
      checkDeleteButtonEnabled(selenium, false);
      checkCreateButtonEnabled(selenium, false);
      
      //----- 5 ----------------
      //select one template
      Thread.sleep(TestConstants.SLEEP);
      selectProjectTemplate(selenium, PROJECT_TEMPLATE_NAME);
      
      checkNameFieldEnabled(selenium, true);
      checkDeleteButtonEnabled(selenium, true);
      checkCreateButtonEnabled(selenium, true);
      
      //----- 6 ----------------
      //remove text from name field
      selenium.type(NAME_FIELD_LOCATOR, "");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkCreateButtonEnabled(selenium, false);
      
      //----- 7 ----------------
      //type some text to name field
      selenium.type(NAME_FIELD_LOCATOR, "a");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      checkCreateButtonEnabled(selenium, true);
      
      closeCreateFromTemplateForm(selenium);
   }
   
   private void putProjectTemplateToRegistry()
   {
      templateUrl = URL + "template-" + System.currentTimeMillis();
      try
      {
         VirtualFileSystemUtils.put(PROJECT_TEMPLATE_XML_2.getBytes(), templateUrl + "/?createIfNotExist=true");
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
   
}