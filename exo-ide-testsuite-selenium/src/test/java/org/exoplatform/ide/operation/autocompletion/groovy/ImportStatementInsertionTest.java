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
package org.exoplatform.ide.operation.autocompletion.groovy;

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public class ImportStatementInsertionTest extends CodeAssistantBaseTest
{

   private final static String SERVICE_FILE_NAME = "import-statement-insertion.groovy";

   @BeforeClass
   public static void setUp()
   {
      String serviceFilePath =
         "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/" + SERVICE_FILE_NAME;

      try
      {
         createProject(ImportStatementInsertionTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), SERVICE_FILE_NAME,
            MimeType.GROOVY_SERVICE, serviceFilePath);
      }
      catch (Exception e)
      {
      }
   }

   @Before
   public void openFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + SERVICE_FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + SERVICE_FILE_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/" + SERVICE_FILE_NAME);
   }
   
   //   GWTX-64: "Don't insert "import <FQN>;" statement if this is class from default package or there is existed import in the header."
   @Test
   public void testServiceFile() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP * 2);

      // goto line 14, type "B" symbol and then click on Ctrl+Space. Then select "Base64" class item from non-default package and press "Enter" key.
      IDE.EDITOR.moveCursorDown(0,14);
      IDE.EDITOR.typeTextIntoEditor(0, "B");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.typeToInput("ase64");
      IDE.CODEASSISTANT.insertSelectedItem();

      // test import statement
//      IDE.EDITOR.clickOnEditor(0);
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(
         "// simple groovy script\n" + "import javax.ws.rs.Path\n" + "import javax.ws.rs.GET\n"
            + "import javax.ws.rs.PathParam\n" + "import java.util.prefs.Base64\n" + "\n" + "@Path("));

      // Empty line 14, type "B" symbol and then click on Ctrl+Space. Then select "BitSet" class item from default package and press "Enter" key.
      goToLine(14);
      IDE.EDITOR.deleteLinesInEditor(0, 1);
      IDE.EDITOR.typeTextIntoEditor(0, "B");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.typeToInput("itSet");
      IDE.CODEASSISTANT.insertSelectedItem();

      // test import statement
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(
         "// simple groovy script\n" + "import javax.ws.rs.Path\n" + "import javax.ws.rs.GET\n"
            + "import javax.ws.rs.PathParam\n" + "import java.util.prefs.Base64\n" + "\n" + "@Path("));

      // Empty line 16 and then click on Ctrl+Space. Then select "HelloWorld" class item with current class name and press "Enter" key.
      goToLine(16);
      IDE.EDITOR.deleteLinesInEditor(0, 1);
      IDE.EDITOR.typeTextIntoEditor(0, " ");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.typeToInput("HelloWorld");
      IDE.CODEASSISTANT.insertSelectedItem();

      // test import statement
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(
         "// simple groovy script\n" + "import javax.ws.rs.Path\n" + "import javax.ws.rs.GET\n"
            + "import javax.ws.rs.PathParam\n" + "import java.util.prefs.Base64\n" + "\n" + "@Path("));


   }
}