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
import org.junit.After;
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
   public static void createProject() throws Exception
   {
      createProject(AutoCompletionXMLTest.class.getSimpleName());
   }

   @After
   public void forceClosedTabs() throws Exception
   {
      IDE.EDITOR.forcedClosureFile(1);
   }

   @Test
   public void testXMLAutocompletion() throws Throwable
   {
      openProject();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.XML_FILE);
      IDE.EDITOR.waitActiveFile();

      String text = IDE.EDITOR.getTextFromCodeEditor();
      assertTrue(text.startsWith("<?xml version='1.0' encoding='UTF-8'?>"));

      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString() + Keys.ENTER.toString());
      IDE.EDITOR.typeTextIntoEditor("<root>\n\n</root>");
      IDE.EDITOR.moveCursorUp(1);

      IDE.EDITOR.typeTextIntoEditor("<rot>\n\n</rot>");
      IDE.EDITOR.moveCursorUp(1);

      IDE.EDITOR.typeTextIntoEditor("<rt>\n\n</rt>");
      IDE.EDITOR.moveCursorUp(1);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.typeToInput("ro");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("rot");
      IDE.CODEASSISTANT.insertSelectedItem();

      String textAfter = IDE.EDITOR.getTextFromCodeEditor();
      assertTrue(textAfter.contains("<root></root>"));
   }

}
