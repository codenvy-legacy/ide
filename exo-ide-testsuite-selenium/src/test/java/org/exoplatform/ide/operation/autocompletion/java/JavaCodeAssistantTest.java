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
package org.exoplatform.ide.operation.autocompletion.java;

import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaCodeAssistant Apr 15, 2011 10:32:15 AM evgen $
 *
 */
public class JavaCodeAssistantTest extends CodeAssistantBaseTest
{
   private static final String FILE_NAME = "JavaTestClass.java";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         createProject(JavaCodeAssistantTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.APPLICATION_JAVA,
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/codeassistantj.txt");
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
   public void testJavaCodeAssistant() throws Exception
   {
      goToLine(32);
      IDE.EDITOR.typeTextIntoEditor(0, "a");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.clearInput();
      IDE.CODEASSISTANT.checkElementNotPresent("in");
      IDE.CODEASSISTANT.checkElementNotPresent("as");
      IDE.CODEASSISTANT.checkElementNotPresent("def");

      IDE.CODEASSISTANT.closeForm();

   }

}
