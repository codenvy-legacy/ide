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
import org.exoplatform.ide.MenuCommands;
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
   
   //temp URL after change URL IDE
   private static final String URL_WITH_IDE = BASE_URL + REST_CONTEXT_IDE + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;
   
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
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/");
      
      IDE.UPLOAD.open(MenuCommands.File.UPLOAD_FILE, FILE_PATH, MimeType.GOOGLE_GADGET);
      
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      
      String url = getSelectedItemUrl();
      
      assertEquals(URL_WITH_IDE + "/" + FILE_NAME, url);
      
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);
      
      IDE.MENU.runCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);
      IDE.PROPERTIES.waitForPropertiesViewOpened();
      assertEquals("exo:googleGadget", IDE.PROPERTIES.getContentNodeType());
      assertEquals(MimeType.GOOGLE_GADGET, IDE.PROPERTIES.getContentType());
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
