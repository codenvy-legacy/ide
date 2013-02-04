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

import java.io.IOException;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 10, 2010 2:34:17 PM evgen $
 * 
 */
public class GroovyClassNameCompletionTest extends CodeAssistantBaseTest
{
   @Before
   public void createProject() throws Exception
   {
      createExoPrj(GroovyClassNameCompletionTest.class.getSimpleName());
      openProject();
   }

   @AfterClass
   public static void tearDown() throws Exception
   {

      try
      {
         VirtualFileSystemUtils.delete(WS_URL + GroovyClassNameCompletionTest.class.getSimpleName());
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

   }

   @Test
   public void testGroovyClassNameCompletion() throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile();

      IDE.GOTOLINE.goToLine(9);
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString() + "\nCollection");

      // open autocomplete form
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.waitForDocPanelOpened();
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("Collection");

      // can does not work on standalone because different orders in list
      IDE.CODEASSISTANT.closeForm();
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString() + "\nCollectionCertStore");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.waitForDocPanelOpened();
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("CollectionCertStoreParameters");

      IDE.CODEASSISTANT.setFocusTInput(); // insure that focus set to proposal
      // from
      IDE.CODEASSISTANT.moveCursorDown(1);

      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor().contains("CollectionCertStoreParameters"));
      IDE.EDITOR.closeTabIgnoringChanges(1);
   }

}
