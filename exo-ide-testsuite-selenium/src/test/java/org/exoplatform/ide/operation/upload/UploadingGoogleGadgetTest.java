/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.operation.upload;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.Test;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class UploadingGoogleGadgetTest extends BaseTest
{
   
   private static final String FOLDER_NAME = "uploads";
   
   private static final String FILE_NAME = "gadget.xml";
   
   private final static String URL = BASE_URL + REST_CONTEXT + "/jcr/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER_NAME;
   
   @Test
   public void testUploadGoogleGadget() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      createFolder(FOLDER_NAME);
      uploadFile(MenuCommands.File.UPLOAD,
         "src/test/resources/org/exoplatform/ide/operation/file/upload/gadget.xml", MimeType.GOOGLE_GADGET);
      Thread.sleep(TestConstants.SLEEP);
      selectItemInWorkspaceTree(FILE_NAME);
      String url = getItemUrl();
      
      assertEquals(BASE_URL+"rest/private/jcr/repository/dev-monit/uploads/gadget.xml",url);
      
      openFileFromNavigationTreeWithCodeEditor(FILE_NAME, false);
      
      runTopMenuCommand(MenuCommands.View.VIEW, MenuCommands.View.SHOW_PROPERTIES);
      
      assertEquals(
         "exo:googleGadget",
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentNodeType||title=%3Cb%3EContent%20Node%20Type%3C%24fs%24b%3E||index=2||Class=StaticTextItem]/textbox"));
      assertEquals(
         MimeType.GOOGLE_GADGET,
         selenium
            .getText("scLocator=//DynamicForm[ID=\"ideDynamicPropertiesForm\"]/item[name=idePropertiesTextContentType||title=%3Cb%3EContent%20Type%3C%24fs%24b%3E||index=3||Class=StaticTextItem]/textbox"));
      
      selectItemInWorkspaceTree(FOLDER_NAME);
      deleteSelectedItems();
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
