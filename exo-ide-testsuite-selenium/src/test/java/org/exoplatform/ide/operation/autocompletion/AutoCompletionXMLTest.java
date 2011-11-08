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
package org.exoplatform.ide.operation.autocompletion;

import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.MenuCommands;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompletionXMLTest extends CodeAssistantBaseTest
{

   @BeforeClass
   public static void createProject()
   {
      createProject(AutoCompletionXMLTest.class.getSimpleName());
   }

   @Test
   public void openForm() throws Throwable
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitActiveFile(projectName + "/Untitled file.xml");

      String text = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(text.startsWith("<?xml version='1.0' encoding='UTF-8'?>"));

      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "<root>\n\n</root>");
      IDE.EDITOR.moveCursorUp(0, 1);

      IDE.EDITOR.typeTextIntoEditor(0, "<rot>\n\n</rot>");
      IDE.EDITOR.moveCursorUp(0, 1);

      IDE.EDITOR.typeTextIntoEditor(0, "<rt>\n\n</rt>");
      IDE.EDITOR.moveCursorUp(0, 1);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.typeToInput("ro");
      IDE.CODEASSISTANT.checkElementPresent("rot");
      IDE.CODEASSISTANT.insertSelectedItem();

      String textAfter = IDE.EDITOR.getTextFromCodeEditor(0);
      assertTrue(textAfter.contains("<root></root>"));
   }

}
