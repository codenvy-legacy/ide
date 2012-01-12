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
import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 8, 2010 12:40:45 PM evgen $
 *
 */
public class GroovyObjectCompletionTest extends CodeAssistantBaseTest
{

   private static final String FILE_NAME = "groovyObjectCompetitionTest.grs";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         createProject(GroovyObjectCompletionTest.class.getSimpleName());
         VirtualFileSystemUtils
            .createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME, MimeType.GROOVY_SERVICE,
               "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/groovy/groovyObjectCompetitionTest.grs");
      }
      catch (Exception e)
      {
         fail("Can't create test folder");
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
   public void testGroovyObjectCompletion() throws Exception
   {
      Thread.sleep(TestConstants.SLEEP);
      IDE.EDITOR.moveCursorDown(0, 10);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + ".");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.typeToInput("con");
      Thread.sleep(TestConstants.SLEEP_SHORT);

      IDE.CODEASSISTANT.checkElementPresent("concat(String):String");
      IDE.CODEASSISTANT.checkElementPresent("contains(CharSequence):boolean");
      IDE.CODEASSISTANT.checkElementPresent("contentEquals(StringBuffer):boolean");
      IDE.CODEASSISTANT.checkElementPresent("contentEquals(CharSequence):boolean");

      IDE.CODEASSISTANT.moveCursorDown(2);

      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains(".contentEquals(StringBuffer)"));

   }

}
