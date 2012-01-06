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

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 16, 2010 10:46:04 AM evgen $
 */
public class GroovyAnnotationAutocompleteTest extends CodeAssistantBaseTest
{

   @BeforeClass
   public static void createProject()
   {
      createProject(GroovyAnnotationAutocompleteTest.class.getSimpleName());
   }

   @Test
   public void testGroovyAnnotation() throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(projectName + "/" + "Untitled file.grs");
      IDE.EDITOR.moveCursorDown(0, 8);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + "\n@");

      IDE.CODEASSISTANT.openForm();
      assertFalse(IDE.CODEASSISTANT.isElementPresent("hello(String):String"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Deprecated"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Documented"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Inherited"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Override"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Retention"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("SuppressWarnings"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Target"));
      
      IDE.CODEASSISTANT.typeToInput("Over");
      IDE.CODEASSISTANT.insertSelectedItem();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("@Override"));
   }

}
