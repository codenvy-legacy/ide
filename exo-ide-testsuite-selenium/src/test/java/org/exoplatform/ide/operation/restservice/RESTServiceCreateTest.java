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
package org.exoplatform.ide.operation.restservice;

import static org.junit.Assert.assertEquals;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class RESTServiceCreateTest extends BaseTest
{

   /**
    * Resource bundle for non-latin names.
    */
   private static final ResourceBundle rb = ResourceBundle.getBundle("FileMsg", Locale.getDefault());

   private static final String FOLDER_NAME = RESTServiceCreateTest.class.getSimpleName();

   private static final String FIRST_NAME = "test.grs";

   private static final String SECOND_NAME = rb.getString("new.file.name") + ".grs";

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + FOLDER_NAME);
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
         VirtualFileSystemUtils.delete(URL + FOLDER_NAME);
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
   public void testCreatingRESTService() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      
      IDE.NAVIGATION.selectItem(URL + FOLDER_NAME + "/");
      
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      assertEquals("Untitled file.grs *", IDE.EDITOR.getTabTitle(0));
      saveAsUsingToolbarButton(FIRST_NAME);

      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      IDE.PROPERTIES.openProperties();

      assertProperties(FIRST_NAME);

      IDE.PROPERTIES.closeProperties();

     IDE.PROPERTIES.openProperties();

      saveAsUsingToolbarButton(SECOND_NAME);
      Thread.sleep(TestConstants.SLEEP);

      assertProperties(SECOND_NAME);
      
      IDE.EDITOR.closeTab(0);
   }

   /**
    * Check file properties 
    */
   private void assertProperties(String name)
   {
      assertEquals("false", IDE.PROPERTIES.getAutoloadProperty());
      assertEquals(TestConstants.NodeTypes.EXO_GROOVY_RESOURCE_CONTAINER, IDE.PROPERTIES.getContentNodeType());
      assertEquals(MimeType.GROOVY_SERVICE, IDE.PROPERTIES.getContentType());
      assertEquals(name, IDE.PROPERTIES.getDisplayName());
      assertEquals(TestConstants.NodeTypes.NT_FILE, IDE.PROPERTIES.getFileNodeType());
   }

}
