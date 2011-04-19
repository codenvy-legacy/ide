/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.project.classpath;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.restservice.RestServiceUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Check, that elements (classes, packages) added to
 * classpath configure file can be added in project.
 * 
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Jan 14, 2011 $
 *
 */
public class UseOfClasspathEntriesTest extends BaseTest
{
   private static final String FOLDER_NAME = UseOfClasspathEntriesTest.class.getSimpleName() + "-test";
   
   private static final String PROJECT_NAME = UseOfClasspathEntriesTest.class.getSimpleName() + "-project";
   
   private static final String EMPLOYEE_FILE_NAME = "Employee.groovy";
   
   private static final String REST_SERVICE_FILE_NAME = "Sample.grs";
   
   private static final String CLASSPATH_FILE_CONTENT = "{\"entries\":[{\"kind\":\"dir\", \"path\":\"" 
      + ClasspathUtils.CLASSPATH_RESOURCE_PREFIX + FOLDER_NAME + "/\"}]}";
   
   private static final String CLASSPATH_FILE_NAME = ".groovyclasspath";
   
   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/project/classpath/";
      try
      {
         VirtualFileSystemUtils.mkcol(WORKSPACE_URL + FOLDER_NAME);
         //create structure of folder for package org/exoplatform/sample, 
         //where will be placed Employee.groovy file
         VirtualFileSystemUtils.mkcol(WORKSPACE_URL + FOLDER_NAME + "/org");
         VirtualFileSystemUtils.mkcol(WORKSPACE_URL + FOLDER_NAME + "/org/exoplatform");
         VirtualFileSystemUtils.mkcol(WORKSPACE_URL + FOLDER_NAME + "/org/exoplatform/sample");
         //put Employee.groovy file
         VirtualFileSystemUtils.put(filePath + "employee.groovy", MimeType.APPLICATION_GROOVY, WORKSPACE_URL + FOLDER_NAME 
            + "/org/exoplatform/sample/" + EMPLOYEE_FILE_NAME);
         
         VirtualFileSystemUtils.mkcol(WORKSPACE_URL + PROJECT_NAME);
         //put rest service file
         VirtualFileSystemUtils.put(filePath + "rest-service.grs", MimeType.GROOVY_SERVICE, WORKSPACE_URL + PROJECT_NAME 
            + "/" + REST_SERVICE_FILE_NAME);
         //put classpath file
         VirtualFileSystemUtils.put(CLASSPATH_FILE_CONTENT.getBytes(), MimeType.APPLICATION_JSON, 
            WORKSPACE_URL + PROJECT_NAME + "/" + CLASSPATH_FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
         fail("Can't create project structure");
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
         fail("Can't create project structure");
      }
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WORKSPACE_URL + FOLDER_NAME);
         VirtualFileSystemUtils.delete(WORKSPACE_URL + PROJECT_NAME);
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
   public void testUsingResourcesFromClasspath() throws Exception
   {
      waitForRootElement();
      
      /*
       * 1. Check, that project folder and folder with resources are present.
       * Open REST Service. 
       */
      IDE.navigator().assertItemPresent(WS_URL + PROJECT_NAME + "/");
      IDE.navigator().assertItemPresent(WS_URL + FOLDER_NAME + "/");
      
      IDE.navigator().selectItem(WS_URL + PROJECT_NAME + "/");
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(REST_SERVICE_FILE_NAME, false);
      
      /*
       * 2. Validate REST Service and check, that is was successful.
       */
      RestServiceUtils.validate(REST_SERVICE_FILE_NAME, 0);
      
      /*
       * 3. Deploy REST Service.
       */
      RestServiceUtils.deploy(PROJECT_NAME + "/" + REST_SERVICE_FILE_NAME, 1);
      
      /*
       * 4. Launch REST Service and try to send request.
       */
      launchRestService();
      /*
       * Click Send button.
       */
      selenium.click(RestServiceUtils.Locators_Rest.SC_LAUNCH_SEND_BTN);
      Thread.sleep(TestConstants.SLEEP);
      
      /*
       * Check output message.
       */
      final String msg = RestServiceUtils.getOutputMsgText(2);
      assertTrue(msg.endsWith("Hello {name} Ivanov"));
   }

}
