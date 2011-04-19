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
package org.exoplatform.ide.operation.upload;

import static org.junit.Assert.assertEquals;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.Locators;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class UploadingGoogleGadgetTest extends BaseTest
{
   
   private static final String FOLDER_NAME = UploadingGoogleGadgetTest.class.getSimpleName();
   
   private static final String FILE_NAME = "gadget.xml";
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;
   
   private static final String FILE_PATH = "src/test/resources/org/exoplatform/ide/operation/file/upload/gadget.xml";
   
   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
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
   public void testUploadGoogleGadget() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      
      uploadFile(MenuCommands.File.UPLOAD_FILE, FILE_PATH, MimeType.GOOGLE_GADGET);
      Thread.sleep(TestConstants.SLEEP);
      
      IDE.navigator().assertItemPresent(URL + "/" + FILE_NAME);
      String url = getSelectedItemUrl();
      
      assertEquals(URL + "/" + FILE_NAME, url);
      
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      
      IDE.menu().runCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);
      
      assertEquals("exo:googleGadget", selenium.getText(Locators.PropertiesPanel.SC_CONTENT_NODE_TYPE_TEXTBOX));
      assertEquals(MimeType.GOOGLE_GADGET, selenium.getText(Locators.PropertiesPanel.SC_CONTENT_TYPE_TEXTBOX));
      
    }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(URL);
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
