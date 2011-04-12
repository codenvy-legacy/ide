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
import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.common.http.client.ProtocolNotSuppException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.project.classpath.ClasspathUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by The eXo Platform SAS.
 *	
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CreateProjectFromTemplateWithUnexistingFileTemplateTest extends BaseTest
{
   private static final String CREATE_BUTTON_LOCATOR =
      "scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCreateButton\"]/";

   private static final String DELETE_BUTTON_LOCATOR =
      "scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormDeleteButton\"]/";

   private static final String PROJECT_NAME = "Sample Project";

   private static final String PROJECT_TEMPLATE_NAME = "Test Project Template";

   private static final String FOLDER_ORG = "org";

   private static final String FOLDER_EXOPLATFORM = "exoplatform";

   private static final String FILE_GROOVY = "Main.groovy";

   private static final String FILE_HTML = "Index.html";

   private static final String UNEXISTING_FILE_HTML = "Index-test.html";

   private static final String URL = BASE_URL + "rest/private/registry/repository/exo:applications/IDE/templates/";

   private final static String PROJECT_FOLDER_URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME
      + "/" + WS_NAME + "/" + PROJECT_NAME;

   private static String templateUrl;

   private static final String PROJECT_TEMPLATE_XML = "<template><name>Test%20Project%20Template</name>"
      + "<description>Project%20template%20for%20test%20purposes</description>"
      + "<template-type>project</template-type><items><folder><name>org</name><items><folder><name>exoplatform</name>"
      + "<items><file><template-file-name>Groovy%20REST%20Service</template-file-name>"
      + "<file-name>Main.groovy</file-name></file><file><template-file-name>Empty%20HTML</template-file-name>"
      + "<file-name>Index.html</file-name></file>"
      + "<file><template-file-name>Not%20Empty%20HTML</template-file-name>"
      + "<file-name>Index-test.html</file-name></file>" + "</items></folder></items></folder></items></template>";

   @BeforeClass
   public static void setUp()
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

   @AfterClass
   public static void tearDown()
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

      HTTPConnection connection;
      URL url;
      try
      {
         url = new URL(BASE_URL);
         connection = Utils.getConnection(url);
         connection.Delete(templateUrl);
      }
      catch (MalformedURLException e)
      {
         e.printStackTrace();
      }
      catch (ProtocolNotSuppException e)
      {
         e.printStackTrace();
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

      checkCreateProjectFromTemplateForm();

      //----- 2 ----------------
      //select project template from list, type project name, click Create button
      selectProjectTemplate(PROJECT_TEMPLATE_NAME);
      typeProjectName(PROJECT_NAME);

      selenium.click(CREATE_BUTTON_LOCATOR);
      Thread.sleep(TestConstants.SLEEP);

      ClasspathUtils.checkConfigureClasspathDialog();
      ClasspathUtils.clickCancel();

      //----- 3 ----------------
      //check new project created
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + "/"); 

      clickOpenIconOfFolder(PROJECT_NAME);
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/");

      clickOpenIconOfFolder(FOLDER_ORG);
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/");

      clickOpenIconOfFolder(FOLDER_EXOPLATFORM);
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/" + FILE_GROOVY);
      IDE.navigator().assertItemPresent(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/" + FILE_HTML);
      IDE.navigator().assertItemNotPresent(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/" + UNEXISTING_FILE_HTML);
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
      assertEquals("Create project",
         selenium.getText("scLocator=//Window[ID=\"ideCreateFileFromTemplateForm\"]/header/"));
      assertTrue(selenium.isElementPresent(DELETE_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent(CREATE_BUTTON_LOCATOR));
      assertTrue(selenium.isElementPresent("scLocator=//IButton[ID=\"ideCreateFileFromTemplateFormCancelButton\"]/"));
      assertTrue(selenium
         .isElementPresent("scLocator=//DynamicForm[ID=\"ideCreateFileFromTemplateFormDynamicForm\"]/item[name=ideCreateFileFromTemplateFormFileNameField]/element"));
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