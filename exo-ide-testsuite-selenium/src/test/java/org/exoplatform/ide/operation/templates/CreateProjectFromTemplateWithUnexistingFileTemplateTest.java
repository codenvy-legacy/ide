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

import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.common.http.client.ProtocolNotSuppException;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.Utils;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Test for creating project from template.
 *	
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:   ${date} ${time}
 *
 */
public class CreateProjectFromTemplateWithUnexistingFileTemplateTest extends BaseTest
{
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
      IDE.WORKSPACE.waitForItem(WS_URL);
      IDE.TOOLBAR.waitForButtonEnabled(ToolbarCommands.File.REFRESH, true, TestConstants.WAIT_PERIOD * 10);
      //----- 1 ----------------
      //open create project from template form
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.PROJECT_FROM_TEMPLATE);

      IDE.TEMPLATES.checkProjectCreateForm();

      //----- 2 ----------------
      //select project template from list, type project name, click Create button
      IDE.TEMPLATES.selectProjectTemplate(PROJECT_TEMPLATE_NAME);
      IDE.TEMPLATES.typeNameToInputField(PROJECT_NAME);
      IDE.TEMPLATES.clickCreateButton();

      IDE.CLASSPATH_PROJECT.waitForClasspathDialogOpen();
      IDE.CLASSPATH_PROJECT.checkConfigureClasspathDialog();
      IDE.CLASSPATH_PROJECT.clickCancelButton();

      //----- 3 ----------------
      //check new project created
      IDE.WORKSPACE.waitForItem(PROJECT_FOLDER_URL + "/");
      IDE.NAVIGATION.assertItemVisible(PROJECT_FOLDER_URL + "/"); 

      IDE.WORKSPACE.selectItem(PROJECT_FOLDER_URL + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/");
      IDE.NAVIGATION.assertItemVisible(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/");

      IDE.WORKSPACE.selectItem(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/");
      IDE.NAVIGATION.assertItemVisible(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/");

      IDE.WORKSPACE.selectItem(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/" + FILE_GROOVY);
      IDE.WORKSPACE.waitForItem(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/" + FILE_HTML);
      IDE.NAVIGATION.assertItemVisible(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/" + FILE_GROOVY);
      IDE.NAVIGATION.assertItemVisible(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/" + FILE_HTML);
      IDE.NAVIGATION.assertItemNotVisible(PROJECT_FOLDER_URL + "/" + FOLDER_ORG + "/" + FOLDER_EXOPLATFORM + "/" + UNEXISTING_FILE_HTML);
   }

}