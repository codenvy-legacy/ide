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

import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
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

   private static final String PROJECT = PhpCodeAssistantTest.class.getSimpleName();

   private static final String FILE_NAME = "PHPTest.php";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         Link link = project.get(Link.REL_CREATE_FILE);
         VirtualFileSystemUtils.createFileFromLocal(link, FILE_NAME, MimeType.APPLICATION_PHP,
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/php/php.php");
      }
      catch (Exception e)
      {
         fail("Can't create test project");
      }
   }

   @Test
   public void testPhpLocalVarAndParameters() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.LOADER.waitClosed();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.LOADER.waitClosed();

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile();

      IDE.GOTOLINE.goToLine(19);
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString());
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.waitForElementInCodeAssistant("$a");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("$go:String");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("$_COOKIE");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("$HTTP_SERVER_VARS");

      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpObjectThis() throws Exception
   {
      IDE.GOTOLINE.goToLine(21);
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("$sp2:ArrayList");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("AA");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("sf2()");
      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpClassSelf() throws Exception
   {
      IDE.GOTOLINE.goToLine(20);
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("$p2:Handler");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("f2($a)");
      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpVariableClass() throws Exception
   {
      IDE.GOTOLINE.goToLine(28);
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("f2($a)");
      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpVarStaticClass() throws Exception
   {
      IDE.GOTOLINE.goToLine(29);
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString());
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.waitForElementInCodeAssistant("$sp2:ArrayList");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("AA");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("sf2()");

      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpStaticClass() throws Exception
   {
      IDE.GOTOLINE.goToLine(30);
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("$sp2:ArrayList");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("AA");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("sf2()");
      IDE.CODEASSISTANT.closeForm();
   }

   @Test
   public void testPhpRootConstAndVars() throws Exception
   {
      IDE.GOTOLINE.goToLine(31);
      IDE.EDITOR.typeTextIntoEditor(Keys.END.toString());
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("$t:SimpleClass1");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("t");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("SimpleClass1");

      IDE.CODEASSISTANT.closeForm();
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

}
