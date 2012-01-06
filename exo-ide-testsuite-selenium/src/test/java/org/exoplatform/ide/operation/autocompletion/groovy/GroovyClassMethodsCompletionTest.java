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

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * Test to check, that autocomplete form
 * works correctly inside method in Groovy class.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 8, 2010 2:36:49 PM evgen $
 *
 */
public class GroovyClassMethodsCompletionTest extends CodeAssistantBaseTest
{

   @BeforeClass
   public static void createProject()
   {
      createProject(GroovyClassMethodsCompletionTest.class.getSimpleName());
   }

   @Test
   public void testGroovyClassMethodCompletion() throws Exception
   {
      /*
       * 1. Open REST Service file.
       */
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(projectName + "/" + "Untitled file.grs");
      /*
       * 2. Go inside hello() method.
       */
      IDE.EDITOR.moveCursorDown(0, 9);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + "\nCollections.");

      /*
       * 4. Call autocomplete form.
       */
      IDE.CODEASSISTANT.openForm();

      /*
       * 5. Type to the input field text "so".
       */
      IDE.CODEASSISTANT.typeToInput("so");

      /*
       * Check, that to elements are found:
       * sort(List):void
       * sort(List, Comparator):void
       */
      assertTrue(IDE.CODEASSISTANT.isElementPresent("sort(List):void"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("sort(List, Comparator):void"));

      /*
       * 6. Select sort(List, Comparator):void element
       */
      IDE.CODEASSISTANT.moveCursorDown(1);

      /*
       * 7. Press Enter to instert element info editor
       */
      IDE.CODEASSISTANT.insertSelectedItem();

      /*
       * Check, that autocomplete form dissapeared, and new text in editor appeared.
       */
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("Collections.sort(List, Comparator)"));
   }

}
