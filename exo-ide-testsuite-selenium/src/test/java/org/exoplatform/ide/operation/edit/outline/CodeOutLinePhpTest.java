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
package org.exoplatform.ide.operation.edit.outline;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: Oct 25, 2010 $
 * 
 */
public class CodeOutLinePhpTest extends CodeAssistantBaseTest
{
   private final static String FILE_NAME = "PhpCodeOutline.php";

   private final static String PROJECT = CodeOutLinePhpTest.class.getSimpleName();

   private final static String PATH = "src/test/resources/org/exoplatform/ide/operation/edit/outline/" + FILE_NAME;

   @BeforeClass
   public static void setUp()
   {
      try
      {

         VirtualFileSystemUtils.createDefaultProject(PROJECT);

         VirtualFileSystemUtils.put(PATH, MimeType.APPLICATION_PHP, WS_URL + PROJECT + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
      }
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

   @Test
   public void testCodeOutLinePhp() throws Exception
   {
      // step 1 open projecr and php file, run outline
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PERSPECTIVE.fullMaximizeBrowser();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile();
      //TODO After add progressor to PHP file delay should be remove
      Thread.sleep(4000);

      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      IDE.OUTLINE.waitOpened();
      IDE.OUTLINE.waitOutlineTreeVisible();
      IDE.OUTLINE.waitOutlineViewVisible();

      //check nodes in string #2
      IDE.GOTOLINE.goToLine(2);
      IDE.OUTLINE.waitElementIsSelect("A");
      IDE.OUTLINE.waitNodeWithSubNamePresent("my\\name");

      //check nodes in string #6
      IDE.GOTOLINE.goToLine(6);
      IDE.OUTLINE.waitElementIsSelect("stest_interface_static($var1, $var2)");
      IDE.OUTLINE.waitNodeWithSubNamePresent("test_interface_static");
      IDE.OUTLINE.waitNodeWithSubNamePresent("test_interface");
      IDE.OUTLINE.waitNodeWithSubNamePresent("$var2");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : String");

      //check nodes in string #23
      IDE.GOTOLINE.goToLine(23);
      IDE.OUTLINE.waitElementIsSelect("x()");
      IDE.OUTLINE.waitNodeWithSubNamePresent("html");
      IDE.OUTLINE.waitNodeWithSubNamePresent("body");
      IDE.OUTLINE.waitNodeWithSubNamePresent("script");
      IDE.OUTLINE.waitNodeWithSubNamePresent("regex");

      //check nodes in string #43
      IDE.GOTOLINE.goToLine(43);
      IDE.OUTLINE.waitElementIsSelect("$t0 : Boolean");
      IDE.OUTLINE.waitNodeWithSubNamePresent("CONSTANT_EX");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : SimpleXMLElement");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : Array");
      IDE.OUTLINE.waitNodeWithSubNamePresent(" : Array");
      IDE.OUTLINE.waitNodeWithSubNamePresent("$parent");

      //check nodes in string #62
      IDE.GOTOLINE.goToLine(62);
      IDE.OUTLINE.waitElementIsSelect("atest");

      //check nodes in string #64
      IDE.GOTOLINE.goToLine(64);
      IDE.OUTLINE.waitElementIsSelect("domainObjectBuilder($var2)");
      IDE.OUTLINE.waitNodeWithSubNamePresent("MYCONST");

      //check move cursor after select nodes in Outline tree
      IDE.OUTLINE.selectItem("makecoffee_error");
      IDE.STATUSBAR.waitCursorPositionAt("90 : 1");

      IDE.OUTLINE.selectItem("$t10");
      IDE.STATUSBAR.waitCursorPositionAt("53 : 1");
      IDE.OUTLINE.selectItem("CONSTANT_EX");
      IDE.STATUSBAR.waitCursorPositionAt("30 : 1");

      IDE.OUTLINE.selectItem("html");
      IDE.STATUSBAR.waitCursorPositionAt("18 : 1");
      IDE.OUTLINE.selectItem("regex");
      IDE.STATUSBAR.waitCursorPositionAt("21 : 1");

   }

}
