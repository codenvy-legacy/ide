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

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class AutoCompletionJavaScriptDuplicationTest extends BaseTest
{

   @Test
   public void testDuplication() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.JAVASCRIPT_FILE);

      IDE.EDITOR.typeTextIntoEditor(0, "var a;\n \n function a() {\n}");

      IDE.EDITOR.moveCursorUp(0, 2);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("a");
      IDE.CODEASSISTANT.closeForm();
      IDE.EDITOR.moveCursorDown(0, 3);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("a()");
      IDE.CODEASSISTANT.closeForm();
   }

}
