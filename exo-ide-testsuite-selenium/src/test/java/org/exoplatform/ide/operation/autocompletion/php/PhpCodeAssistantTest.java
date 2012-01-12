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
package org.exoplatform.ide.operation.autocompletion.php;

import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class PhpCodeAssistantTest extends BaseTest
{

   private static final String FOLDER_NAME = PhpCodeAssistantTest.class.getSimpleName();

   private static final String FILE_NAME = "PHPTest.php";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME + "/");
         VirtualFileSystemUtils.put("src/test/resources/org/exoplatform/ide/operation/file/autocomplete/php/php.php",
            MimeType.APPLICATION_PHP, WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
         fail("Can't create test folder");
      }
   }

   @Before
   public void beforeTest() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      IDE.WORKSPACE.waitForItem(WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);

   }

   @Test
   public void testPhpLocalVarAndParameters() throws Exception
   {
      goToLine(19);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("$a");
      IDE.CODEASSISTANT.checkElementPresent("$go:String");
      IDE.CODEASSISTANT.checkElementPresent("$_COOKIE");
      IDE.CODEASSISTANT.checkElementPresent("$HTTP_SERVER_VARS");
      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpObjectThis() throws Exception
   {
      goToLine(21);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("$sp2:ArrayList");
      IDE.CODEASSISTANT.checkElementPresent("AA");
      IDE.CODEASSISTANT.checkElementPresent("sf2()");
      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpClassSelf() throws Exception
   {
      goToLine(20);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("$p2:Handler");
      IDE.CODEASSISTANT.checkElementPresent("f2($a)");
      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpVariableClass() throws Exception
   {
      goToLine(28);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("f2($a)");
      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpVarStaticClass() throws Exception
   {
      goToLine(29);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("$sp2:ArrayList");
      IDE.CODEASSISTANT.checkElementPresent("AA");
      IDE.CODEASSISTANT.checkElementPresent("sf2()");
      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpStaticClass() throws Exception
   {
      goToLine(30);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("$sp2:ArrayList");
      IDE.CODEASSISTANT.checkElementPresent("AA");
      IDE.CODEASSISTANT.checkElementPresent("sf2()");
      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpRootConstAndVars() throws Exception
   {
      goToLine(31);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("$t:SimpleClass1");
      IDE.CODEASSISTANT.checkElementPresent("t");
      IDE.CODEASSISTANT.checkElementPresent("SimpleClass1");
      IDE.CODEASSISTANT.closeForm();
   }

   @After
   public void afterTest() throws Exception
   {
      IDE.CODEASSISTANT.closeForm();
      IDE.EDITOR.closeFile(0);

   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WORKSPACE_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
      }
   }

}
