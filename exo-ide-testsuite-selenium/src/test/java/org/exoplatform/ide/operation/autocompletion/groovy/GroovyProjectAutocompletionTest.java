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
package org.exoplatform.ide.operation.autocompletion.groovy;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GroovyClassNameProjectTest Jan 20, 2011 2:13:30 PM evgen $
 *
 */
public class GroovyProjectAutocompletionTest extends CodeAssistantBaseTest
{

   private static final String FOLDER_NAME = "src";

   private static final String REST_SERVICE_FILE_NAME = "rest-service.grs";

   private static final String CLASSPATH_FILE_CONTENT = "{\"entries\":[{\"kind\":\"dir\", \"path\":\""
      + BaseTest.WS_NAME + "#/" + GroovyProjectAutocompletionTest.class.getSimpleName() + "/" + FOLDER_NAME + "/\"}]}";;

   private static final String CLASSPATH_FILE_NAME = ".groovyclasspath";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         createProject(GroovyProjectAutocompletionTest.class.getSimpleName(),
            "src/test/resources/org/exoplatform/ide/project/classpath.zip");

         VirtualFileSystemUtils.createFile(project.get(Link.REL_CREATE_FILE), CLASSPATH_FILE_NAME,
            MimeType.APPLICATION_JSP, CLASSPATH_FILE_CONTENT);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail("Can't create project structure");
      }
   }

   @Before
   public void openFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + REST_SERVICE_FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + REST_SERVICE_FILE_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/" + REST_SERVICE_FILE_NAME);
   }

   @Test
   public void testGroovyClassNameProject() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);

      IDE.EDITOR.moveCursorDown(0, 12);

      IDE.EDITOR.typeTextIntoEditor(0, "Po");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("Pojo");

      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("import org.exoplatform.sample.Pojo"));

      IDE.EDITOR.typeTextIntoEditor(0, " p\n");

      IDE.EDITOR.typeTextIntoEditor(0, "p.");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("getName():String");
      IDE.CODEASSISTANT.checkElementPresent("printText(String):void");
      IDE.CODEASSISTANT.checkElementPresent("setName(String):void");

      IDE.CODEASSISTANT.typeToInput("pr");

      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("p.printText(String)"));

   }

}
