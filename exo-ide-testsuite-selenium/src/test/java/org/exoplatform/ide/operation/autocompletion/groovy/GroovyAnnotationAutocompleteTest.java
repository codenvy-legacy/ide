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
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 16, 2010 10:46:04 AM evgen $
 */
public class GroovyAnnotationAutocompleteTest extends CodeAssistantBaseTest
{

   @Before
   public void createProject() throws Exception
   {
      createExoPrj(GroovyAnnotationAutocompleteTest.class.getSimpleName());
      openProject();
   }


   @AfterClass
   public static void tearDown() throws Exception
   {

      try
      {
         VirtualFileSystemUtils.delete(WS_URL + GroovyAnnotationAutocompleteTest.class.getSimpleName());
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }

   }
   
   
   
   @Test
   public void testGroovyAnnotation() throws Exception
   {
      IDE.TOOLBAR.runCommandFromNewPopupMenu(MenuCommands.New.REST_SERVICE_FILE);
      IDE.EDITOR.waitActiveFile(projectName + "/" + "Untitled file.grs");
      IDE.EDITOR.moveCursorDown(0, 8);

      //check Deprecated annotation
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + "\n@Depr");
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Deprecated"));
      assertFalse(IDE.CODEASSISTANT.isElementPresent("hello(String):String"));
      closeFormAndDeleteAnotatationString();

      //check Inherited annotation
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + "@Inh");
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Inherited"));
      closeFormAndDeleteAnotatationString();

      //check Retention annotation
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + "@Rete");
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Retention"));
      closeFormAndDeleteAnotatationString();

      //check SuppressWarnings annotation
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + "@Suppress");
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("SuppressWarnings"));
      closeFormAndDeleteAnotatationString();

      //check Override annotation
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() + "@Ove");
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Override"));
      closeFormAndDeleteAnotatationString();
     
   }

   private void closeFormAndDeleteAnotatationString() throws Exception
   {
      IDE.CODEASSISTANT.closeForm();
      IDE.EDITOR.typeTextIntoEditor(0, Keys.SHIFT.toString() + Keys.HOME.toString());
      IDE.EDITOR.typeTextIntoEditor(0, Keys.DELETE.toString());
   }

}
