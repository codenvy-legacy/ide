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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 15, 2010 11:11:29 AM evgen $
 */
// http://jira.exoplatform.org/browse/IDE-478
public class GroovyLocalVariableTest extends CodeAssistantBaseTest
{

   private static String FILE_NAME = "GroovyLocalVariable.groovy";

   @Before
   public void beforeTest() throws Exception
   {
      try
      {
         createProject(GroovyLocalVariableTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.GROOVY_SERVICE,
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/groovyLocalVar.groovy");
      }
      catch (IOException e)
      {
         fail("Can't create project structure");
      }
      
      openProject();
   }

   @Test
   public void testLocalVariable() throws Exception
   {
      //open file
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/" + FILE_NAME);
      
      IDE.EDITOR.moveCursorDown(0, 15);

      IDE.CODEASSISTANT.openForm();

      assertTrue(IDE.CODEASSISTANT.isElementPresent("getInt(Double):Integer"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("hello(String):List<Item>"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("hello():Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("printClosureOuter():Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("HelloWorld"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("s:String"));
      IDE.CODEASSISTANT.closeForm();

      IDE.EDITOR.moveCursorDown(0, 5);
      IDE.CODEASSISTANT.openForm();
      
      assertTrue(IDE.CODEASSISTANT.isElementPresent("e:Exception"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("name:String"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("s:String"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("getInt(Double):Integer"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("hello(String):List<Item>"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("hello():Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("printClosureOuter():Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("HelloWorld"));

      assertFalse(IDE.CODEASSISTANT.isElementPresent("col:Object"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("stream:PrintStream"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("ii"));
      IDE.CODEASSISTANT.closeForm();

      IDE.EDITOR.moveCursorDown(0, 3);
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("col:Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("e:Exception"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("name:String"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("stream:PrintStream"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("s:String"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("getInt(Double):Integer"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("hello(String):List<Item>"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("hello():Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("printClosureOuter():Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("HelloWorld"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("d:Double"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("ii:Integer"));
      IDE.CODEASSISTANT.closeForm();

      IDE.EDITOR.moveCursorDown(0, 6);
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("s:String"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("getInt(Double):Integer"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("hello(String):List<Item>"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("hello():Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("printClosureOuter():Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("HelloWorld"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("col:Object"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("name:String"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("e:Exception"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("stream:PrintStream"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("d:Double"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("ii:Integer"));
      IDE.CODEASSISTANT.closeForm();

      IDE.EDITOR.moveCursorDown(0, 8);
      IDE.CODEASSISTANT.openForm();

      assertTrue(IDE.CODEASSISTANT.isElementPresent("getInt(Double):Integer"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("hello(String):List<Item>"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("hello():Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("printClosureOuter():Object"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("s:String"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("name:String"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("e:Exception"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("stream:PrintStream"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("d:Double"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("ii:Integer"));
      IDE.CODEASSISTANT.closeForm();
      IDE.EDITOR.moveCursorDown(0, 1);
      IDE.EDITOR.typeTextIntoEditor(0, "\n");

      IDE.CODEASSISTANT.openForm();
      assertFalse(IDE.CODEASSISTANT.isElementPresent("getInt(Double):Integer"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("hello(String):List<Item>"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("hello():Object"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("printClosureOuter():Object"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("s:String"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("col:Object"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("name:String"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("e:Exception"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("stream:PrintStream"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("d:Double"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("ii:Integer"));
      IDE.CODEASSISTANT.closeForm();
   }

}
