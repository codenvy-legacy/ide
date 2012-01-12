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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 15, 2010 11:11:29 AM evgen $
 *
 */
// http://jira.exoplatform.org/browse/IDE-478
public class GroovyLocalVariableTest extends CodeAssistantBaseTest
{

   private static String FILE_NAME = "GroovyLocalVariable.groovy";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         createProject(GroovyLocalVariableTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.GROOVY_SERVICE,
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/groovyLocalVar.groovy");
      }
      catch (Exception e)
      {
      }
   }

   @Before
   public void openFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/" + FILE_NAME);
   }

   @Test
   public void testLocalVariable() throws Exception
   {
      IDE.EDITOR.moveCursorDown(0, 15);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementPresent("hello():Object");
      IDE.CODEASSISTANT.checkElementPresent("printClosureOuter():Object");
      IDE.CODEASSISTANT.checkElementPresent("HelloWorld");
      IDE.CODEASSISTANT.checkElementNotPresent("s:String");
      IDE.CODEASSISTANT.closeForm();

      IDE.EDITOR.moveCursorDown(0, 5);
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("e:Exception");
      IDE.CODEASSISTANT.checkElementPresent("name:String");
      IDE.CODEASSISTANT.checkElementPresent("s:String");
      IDE.CODEASSISTANT.checkElementPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementPresent("hello():Object");
      IDE.CODEASSISTANT.checkElementPresent("printClosureOuter():Object");
      IDE.CODEASSISTANT.checkElementPresent("HelloWorld");

      IDE.CODEASSISTANT.checkElementNotPresent("col:Object");
      IDE.CODEASSISTANT.checkElementNotPresent("stream:PrintStream");
      IDE.CODEASSISTANT.checkElementNotPresent("d");
      IDE.CODEASSISTANT.checkElementNotPresent("ii");
      IDE.CODEASSISTANT.closeForm();

      IDE.EDITOR.moveCursorDown(0, 3);
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("col:Object");
      IDE.CODEASSISTANT.checkElementPresent("e:Exception");
      IDE.CODEASSISTANT.checkElementPresent("name:String");
      IDE.CODEASSISTANT.checkElementPresent("stream:PrintStream");
      IDE.CODEASSISTANT.checkElementPresent("s:String");
      IDE.CODEASSISTANT.checkElementPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementPresent("hello():Object");
      IDE.CODEASSISTANT.checkElementPresent("printClosureOuter():Object");
      IDE.CODEASSISTANT.checkElementPresent("HelloWorld");
      IDE.CODEASSISTANT.checkElementNotPresent("d:Double");
      IDE.CODEASSISTANT.checkElementNotPresent("ii:Integer");
      IDE.CODEASSISTANT.closeForm();

      IDE.EDITOR.moveCursorDown(0, 6);
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("s:String");
      IDE.CODEASSISTANT.checkElementPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementPresent("hello():Object");
      IDE.CODEASSISTANT.checkElementPresent("printClosureOuter():Object");
      IDE.CODEASSISTANT.checkElementPresent("HelloWorld");
      IDE.CODEASSISTANT.checkElementNotPresent("col:Object");
      IDE.CODEASSISTANT.checkElementNotPresent("name:String");
      IDE.CODEASSISTANT.checkElementNotPresent("e:Exception");
      IDE.CODEASSISTANT.checkElementNotPresent("stream:PrintStream");
      IDE.CODEASSISTANT.checkElementPresent("d:Double");
      IDE.CODEASSISTANT.checkElementPresent("ii:Integer");
      IDE.CODEASSISTANT.closeForm();

      IDE.EDITOR.moveCursorDown(0, 8);
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementPresent("hello():Object");
      IDE.CODEASSISTANT.checkElementPresent("printClosureOuter():Object");
      IDE.CODEASSISTANT.checkElementNotPresent("s:String");
      IDE.CODEASSISTANT.checkElementNotPresent("name:String");
      IDE.CODEASSISTANT.checkElementNotPresent("e:Exception");
      IDE.CODEASSISTANT.checkElementNotPresent("stream:PrintStream");
      IDE.CODEASSISTANT.checkElementNotPresent("d:Double");
      IDE.CODEASSISTANT.checkElementNotPresent("ii:Integer");
      IDE.CODEASSISTANT.closeForm();
      IDE.EDITOR.moveCursorDown(0, 1);
      IDE.EDITOR.typeTextIntoEditor(0, "\n");

      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementNotPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementNotPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementNotPresent("hello():Object");
      IDE.CODEASSISTANT.checkElementNotPresent("printClosureOuter():Object");
      IDE.CODEASSISTANT.checkElementNotPresent("s:String");
      IDE.CODEASSISTANT.checkElementNotPresent("col:Object");
      IDE.CODEASSISTANT.checkElementNotPresent("name:String");
      IDE.CODEASSISTANT.checkElementNotPresent("e:Exception");
      IDE.CODEASSISTANT.checkElementNotPresent("stream:PrintStream");
      IDE.CODEASSISTANT.checkElementNotPresent("d:Double");
      IDE.CODEASSISTANT.checkElementNotPresent("ii:Integer");
      IDE.CODEASSISTANT.closeForm();
   }

}
