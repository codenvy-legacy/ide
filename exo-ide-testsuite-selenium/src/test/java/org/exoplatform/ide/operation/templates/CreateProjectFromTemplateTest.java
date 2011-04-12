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
import org.exoplatform.ide.project.classpath.ClasspathUtils;
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
   
   private static final String EMPTY_PROJECT = "New Project";
   
   private static final String FOLDER_ORG = "org";
   
   private static final String FOLDER_EXOPLATFORM = "exoplatform";
   
   private static final String FILE_GROOVY = "Main.groovy";
   
   private static final String FILE_HTML = "Index.html";
   
   private static final String URL = BASE_URL + "rest/private/registry/repository/exo:applications/IDE/templates/";
   
   private final static String PROJECT_FOLDER_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";
   
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
         VirtualFileSystemUtils.delete(PROJECT_FOLDER_URL + PROJECT_NAME);
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
         VirtualFileSystemUtils.delete(PROJECT_FOLDER_URL + PROJECT_FROM_DEFAULT_TEMPLATE);
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
      /*
       * 1. Open Create Project From Template form
       */
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      
      checkCreateProjectFromTemplateForm(selenium);
      
      /*
       * 2. Select project template from list, type project name, click Create button
       */
      selectProjectTemplate(selenium, PROJECT_TEMPLATE_NAME);
      typeProjectName(selenium, PROJECT_NAME);
      
      selenium.click(CREATE_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);
      
      /*
       * Configure classpath window dialog appeared. Close it
       */
      ClasspathUtils.checkConfigureClasspathDialog();
      ClasspathUtils.clickCancel();
      
      /*
       * 3. Check new project created
       */
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_NAME + "/");
      
      IDE.navigator().clickOpenIconOfFolder(PROJECT_NAME);      
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_NAME + "/" + FOLDER_ORG + "/");
      
      
      IDE.navigator().clickOpenIconOfFolder(FOLDER_ORG);
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_NAME + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/");
      
      IDE.navigator().clickOpenIconOfFolder(FOLDER_EXOPLATFORM);
      
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_NAME + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/" + FILE_GROOVY);
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_NAME + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/" + FILE_HTML);
   }
   
   /**
    * Issue IDE-352
    * @throws Exception
    */
   @Test
   public void createDefaultSampleProject() throws Exception
   {
      refresh();
      /*
       * 1. Create project from defaulte template
       */
      TemplateUtils.createProjectFromTemplate(selenium, TemplateUtils.DEFAULT_PROJECT_TEMPLATE_NAME, PROJECT_FROM_DEFAULT_TEMPLATE);
      
      /*
       * 2. Configure classpath window dialog appeared. Close it
       */
      ClasspathUtils.checkConfigureClasspathDialog();
      ClasspathUtils.clickCancel();
      
      /*
       * 3. Check sample project created
       */
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_FROM_DEFAULT_TEMPLATE + "/");
      
      IDE.navigator().clickOpenIconOfFolder(PROJECT_FOLDER_URL + PROJECT_FROM_DEFAULT_TEMPLATE + "/");
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_FROM_DEFAULT_TEMPLATE + "/" + "data/");
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_FROM_DEFAULT_TEMPLATE + "/" + "logic/");
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_FROM_DEFAULT_TEMPLATE + "/" + "UI/");
      
      IDE.navigator().clickOpenIconOfFolder("logic");
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_FROM_DEFAULT_TEMPLATE + "/" + "logic/" + "GreetingRESTService.grs");
      
      IDE.navigator().clickOpenIconOfFolder("UI");
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_FROM_DEFAULT_TEMPLATE + "/" + "UI/" + "GreetingGoogleGadget.xml");
      
      IDE.navigator().clickOpenIconOfFolder("data");
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_FROM_DEFAULT_TEMPLATE + "/" + "data/" +"DataObject.groovy"); 
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + PROJECT_FROM_DEFAULT_TEMPLATE + "/" + "data/" +"Pojo.groovy");
   }
   
   /**
    * IDE-668 Add template for empty project in IDE
    * @throws Exception
    */
   @Test
   public void createEmptyProject() throws Exception
   {
      refresh();
      /*
       * 1. Create new empty project
       */
      TemplateUtils.createProjectFromTemplate(selenium, TemplateUtils.EMPTY_PROJECT_TEMPLATE_NAME, EMPTY_PROJECT);
      
      /*
       * 2. Configure classpath window dialog appeared. Close it
       */
      ClasspathUtils.checkConfigureClasspathDialog();
      ClasspathUtils.clickCancel();
      
      /*
       * 3. Check new project created
       */
      IDE.navigator().assertItemNotPresent(PROJECT_FOLDER_URL + EMPTY_PROJECT + "/");
   }
   
   /**
    * Check, that buttons and fields are correctly change their state,
    * when template selected/deselected.
    * 
    * @throws Exception
    */
   @Test
   public void testEnablingDisablingElements() throws Exception
   {
      putProjectTemplateToRegistry();
      refresh();
      
      /*
       * 1. Open create project from template form
       */
      IDE.toolbar().runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_FROM_TEMPLATE);
      
      /*
       * Name field, Delete and Create buttons are disabled
       */
      checkNameFieldEnabled(selenium, false);
      checkDeleteButtonEnabled(selenium, false);
      checkCreateButtonEnabled(selenium, false);
      
      /*
       * 2. Select one template. 
       */
      selectProjectTemplate(selenium, PROJECT_TEMPLATE_NAME);
      
      /*
       * Name field, Delete and Create buttons are enabled
       */
      checkNameFieldEnabled(selenium, true);
      checkDeleteButtonEnabled(selenium, true);
      checkCreateButtonEnabled(selenium, true);
      
      /*
       * 3. Deselect one template
       */
      selenium.controlKeyDown();
      selectProjectTemplate(selenium, PROJECT_TEMPLATE_NAME);
      selenium.controlKeyUp();
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      /*
       * Name field, Delete and Create buttons are disabled
       */
      checkNameFieldEnabled(selenium, false);
      checkDeleteButtonEnabled(selenium, false);
      checkCreateButtonEnabled(selenium, false);
      
      /*
       * 4. Select several templates (one is default)
       */
      selectProjectTemplate(selenium, TemplateUtils.DEFAULT_PROJECT_TEMPLATE_NAME);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.controlKeyDown();
      selectProjectTemplate(selenium, PROJECT_TEMPLATE_NAME_2);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      selenium.controlKeyUp();
      
      /*
       * Name field, Delete and Create buttons are disabled
       */
      checkNameFieldEnabled(selenium, false);
      checkDeleteButtonEnabled(selenium, false);
      checkCreateButtonEnabled(selenium, false);
      
      /*
       * 5. Select one template
       */
      Thread.sleep(TestConstants.SLEEP);
      selectProjectTemplate(selenium, PROJECT_TEMPLATE_NAME);
      
      /*
       * Name field, Delete and Create buttons are enabled
       */
      checkNameFieldEnabled(selenium, true);
      checkDeleteButtonEnabled(selenium, true);
      checkCreateButtonEnabled(selenium, true);
      
      /*
       * 6. Remove text from name field
       */
      selenium.type(NAME_FIELD_LOCATOR, "");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      /*
       * Create button is disabled
       */
      checkCreateButtonEnabled(selenium, false);
      
      /*
       * 7. Type some text to name field
       */
      selenium.type(NAME_FIELD_LOCATOR, "a");
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      /*
       * 8. Create button is enabled
       */
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
   
}