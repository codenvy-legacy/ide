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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
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
 */
public class UseOfClasspathEntriesTest extends BaseTest
{
   private static final String PROJECT = UseOfClasspathEntriesTest.class.getSimpleName();
   
   private static final String PROJECT_2 = UseOfClasspathEntriesTest.class.getSimpleName() + "-2";
   
   private static final String FILE_NAME = "Sample.grs";
      
   @BeforeClass
   public static void setUp()
   {
      try
      {
         //create exo-app project with .groovyclasspath file
         String projectPath = "src/test/resources/org/exoplatform/ide/project/classpath/";
         VirtualFileSystemUtils.importZipProject(PROJECT, projectPath + "classpath-project.zip");
         
         //create default project with no .groovyclasspath file
         VirtualFileSystemUtils.importZipProject(PROJECT_2, projectPath + "classpath-project-2.zip");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
         VirtualFileSystemUtils.delete(WS_URL + PROJECT_2);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testUsingResourcesFromClasspath() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      
      //Open REST Service.
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FILE_NAME);
      
      IDE.EDITOR.waitTabPresent(1);
      
      //Validate REST Service and check, that is was successful.
      final String validateMsg = IDE.REST_SERVICE.validate(1);
      assertEquals("[INFO] " + FILE_NAME + " validated successfully.", validateMsg);

      //Deploy REST Service.
      final String deployMsg = IDE.REST_SERVICE.deploy(PROJECT + "/" + FILE_NAME, 2);
      assertTrue(deployMsg.contains("deployed successfully."));

      //Launch REST Service and try to send request.
      IDE.REST_SERVICE.launchRestService();
      
      //Click Send button.
      IDE.REST_SERVICE.sendRequest();
      IDE.REST_SERVICE.waitClosed();
      IDE.OUTPUT.waitForMessageShow(3);

      //Check output message.
      final String msg = IDE.OUTPUT.getOutputMessage(3);
      assertTrue(msg.endsWith("Hello {name} Ivanov"));
   }

}
