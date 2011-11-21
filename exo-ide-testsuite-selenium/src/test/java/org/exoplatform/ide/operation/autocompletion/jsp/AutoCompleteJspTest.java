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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: AutoCompleteJspTest Apr 26, 2011 11:07:34 AM evgen $
 *
 */
public class AutoCompleteJspTest extends CodeAssistantBaseTest
{

   private static final String FILE_NAME = "JSPtest.jsp";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         createProject(AutoCompleteJspTest.class.getSimpleName());
         VirtualFileSystemUtils.createFileFromLocal(project.get(Link.REL_CREATE_FILE), FILE_NAME,
            MimeType.APPLICATION_JSP,
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/jsp/testJsp.jsp");
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail("Can't create test folder");
      }
   }

   @Before
   public void openFile() throws Exception
   {
      openFile(FILE_NAME);
   }

   @Test
   public void testAutocompleteJsp() throws Exception
   {

      IDE.GOTOLINE.goToLine(6);
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("background-attachment");
      IDE.CODEASSISTANT.checkElementPresent("counter-increment");
      IDE.CODEASSISTANT.insertSelectedItem();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("!important"));

      IDE.GOTOLINE.goToLine(11);
      IDE.EDITOR.typeTextIntoEditor(0, "Collection");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("Collection");
      IDE.CODEASSISTANT.checkElementPresent("Collections");
      IDE.CODEASSISTANT.insertSelectedItem();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("Collection"));

      IDE.GOTOLINE.goToLine(18);

      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("a");
      IDE.CODEASSISTANT.checkElementPresent("Window");
      IDE.CODEASSISTANT.closeForm();

      IDE.GOTOLINE.goToLine(24);

      IDE.EDITOR.typeTextIntoEditor(0, "<t");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("table");
      IDE.CODEASSISTANT.checkElementPresent("textarea");
      IDE.CODEASSISTANT.closeForm();
   }

}
