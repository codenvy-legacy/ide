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
package org.exoplatform.ide.operation.file.autocompletion.java;

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
 * @version $Id: JavaCodeAssistant Apr 15, 2011 10:32:15 AM evgen $
 *
 */
public class JavaCodeAssistantTest extends BaseTest
{
   private static final String FOLDER_NAME = JavaCodeAssistantTest.class.getSimpleName();

   private static final String FILE_NAME = "JavaTestClass.java";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME + "/");
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/codeassistantj.txt",
            MimeType.APPLICATION_JAVA, WS_URL + FOLDER_NAME + "/" + FILE_NAME);
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
   public void testJavaCodeAssistant() throws Exception
   {
      waitForRootElement();
      IDE.navigator().assertItemPresent(WS_URL + FOLDER_NAME + "/");

      IDE.navigator().selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);

      openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);
      goToLine(32);
      typeTextIntoEditor(0, "a");
      IDE.codeAssistant().openForm();
      IDE.codeAssistant().clearInput();
      IDE.codeAssistant().checkElementNotPresent("in");
      IDE.codeAssistant().checkElementNotPresent("as");
      IDE.codeAssistant().checkElementNotPresent("def");

      IDE.codeAssistant().closeForm();

      IDE.editor().closeUnsavedFileAndDoNotSave(0);

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
