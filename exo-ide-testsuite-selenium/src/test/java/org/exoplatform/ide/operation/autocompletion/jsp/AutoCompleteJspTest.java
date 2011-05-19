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
package org.exoplatform.ide.operation.autocompletion.jsp;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: AutoCompleteJspTest Apr 26, 2011 11:07:34 AM evgen $
 *
 */
public class AutoCompleteJspTest extends BaseTest
{

   private static final String FOLDER_NAME = AutoCompleteJspTest.class.getSimpleName();

   private static final String FILE_NAME = "JSPtest.jsp";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME + "/");
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/jsp/testJsp.jsp",
            MimeType.APPLICATION_JSP, WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
         fail("Can't create test folder");
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
         fail("Can't create test folder");
      }
   }
   
   @Test
   public void testAutocompleteJsp() throws Exception
   {
      waitForRootElement();
      IDE.NAVIGATION.assertItemVisible(WS_URL + FOLDER_NAME + "/");

      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);
      
      goToLine(6);
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("background-attachment");
      IDE.CODEASSISTANT.checkElementPresent("counter-increment");
      IDE.CODEASSISTANT.insertSelectedItem();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("!important"));
      
      goToLine(11);
      IDE.EDITOR.typeTextIntoEditor(0, "Coll");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("Collection");
      IDE.CODEASSISTANT.checkElementPresent("Collections");
      IDE.CODEASSISTANT.insertSelectedItem();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("Collection"));
     
      goToLine(18);
      
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("a");
      IDE.CODEASSISTANT.checkElementPresent("Window");
      IDE.CODEASSISTANT.closeForm();
      
      goToLine(24);
      
      IDE.EDITOR.typeTextIntoEditor(0, "<t");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("table");
      IDE.CODEASSISTANT.checkElementPresent("textarea");
      IDE.CODEASSISTANT.closeForm();
      
      //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0);
      IDE.EDITOR.closeTabIgnoringChanges(0);
      
   }
   
   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WORKSPACE_URL + FOLDER_NAME);
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
