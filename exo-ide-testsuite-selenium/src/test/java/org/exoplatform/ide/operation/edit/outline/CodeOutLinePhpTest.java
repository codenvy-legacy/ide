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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Outline.TokenType;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a> 
 * @version $Id: Oct 25, 2010 $
 *
 */
public class CodeOutLinePhpTest extends BaseTest
{
   private final static String FILE_NAME = "PhpCodeOutline.php";

   private final static String FOLDER = CodeOutLinePhpTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/" + FOLDER + "/";
   
   private OulineTreeHelper outlineTreeHelper;
   
   public CodeOutLinePhpTest()
   {
      this.outlineTreeHelper = new OulineTreeHelper();
   }
   
   @BeforeClass
   public static void setUp()
   {
      String filePath = "src/test/resources/org/exoplatform/ide/operation/edit/outline/" + FILE_NAME;
      try
      {
         VirtualFileSystemUtils.mkcol(URL);
         VirtualFileSystemUtils.put(filePath, MimeType.APPLICATION_PHP, "nt:resource", URL + FILE_NAME);
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ModuleException e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testCodeOutLinePhp() throws Exception
   {
      // Open groovy file with content
      Thread.sleep(TestConstants.IDE_LOAD_PERIOD);

      IDE.WORKSPACE.selectItem(WS_URL);
      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      IDE.WORKSPACE.clickOpenIconOfFolder(URL);
      Thread.sleep(TestConstants.SLEEP);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(URL + FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      
      // open outline panel
      IDE.TOOLBAR.runCommand(ToolbarCommands.View.SHOW_OUTLINE);
      Thread.sleep(TestConstants.SLEEP);

      // check for presence and visibility of outline tab
      IDE.OUTLINE.assertOutlineTreePresent();
      IDE.OUTLINE.checkOutlinePanelVisibility(true);
            
      // create initial outline tree map
      OulineTreeHelper.init();
      outlineTreeHelper.addOutlineItem("php code", 1, TokenType.PHP_TAG, "php code");
      outlineTreeHelper.addOutlineItem("php code", 2, TokenType.PHP_TAG);
      outlineTreeHelper.addOutlineItem("html", 18, TokenType.TAG, "html");
      
      // expand outline tree
      outlineTreeHelper.expandOutlineTree();
      
      // create opened outline tree map
      outlineTreeHelper.clearOutlineTreeInfo();      
      OulineTreeHelper.init();
      outlineTreeHelper.addOutlineItem("php code", 1, false, TokenType.PHP_TAG, "php code");  // false, because outline node is not highlighted from test, but highlighted when goto this line manually
      outlineTreeHelper.addOutlineItem("A", 2, TokenType.NAMESPACE, "A");
      outlineTreeHelper.addOutlineItem("my\\name", 3, TokenType.NAMESPACE, "my\\name");         
      outlineTreeHelper.addOutlineItem("test", 5, TokenType.INTERFACE, "test");
      outlineTreeHelper.addOutlineItem("stest_interface_static($var1, $var2)", 6, TokenType.METHOD, "test_interface_static");
      outlineTreeHelper.addOutlineItem("test_interface($var1)", 7, TokenType.METHOD, "test_interface");
      outlineTreeHelper.addOutlineItem("b : String", 8, TokenType.CLASS_CONSTANT, "b");
      outlineTreeHelper.addOutlineItem("php code", 15, TokenType.PHP_TAG, "php code");
      outlineTreeHelper.addOutlineItem("html", 18, TokenType.TAG, "html");
      outlineTreeHelper.addOutlineItem("head", 19, TokenType.TAG, "head");
      outlineTreeHelper.addOutlineItem("script", 20, TokenType.TAG, "script");      
      outlineTreeHelper.addOutlineItem("regex : String", 21, TokenType.VARIABLE, "regex");      
      outlineTreeHelper.addOutlineItem("x()", 23, TokenType.FUNCTION, "x");
      outlineTreeHelper.addOutlineItem("y : Number", 24, TokenType.VARIABLE, "y");
      outlineTreeHelper.addOutlineItem("body", 28, TokenType.TAG, "body");
      outlineTreeHelper.addOutlineItem("php code", 29, TokenType.PHP_TAG, "php code");      
      outlineTreeHelper.addOutlineItem("CONSTANT_EX : String", 30, TokenType.CONSTANT, "CONSTANT_EX");
      outlineTreeHelper.addOutlineItem("$a", 42, TokenType.VARIABLE, "$a");
      outlineTreeHelper.addOutlineItem("$t0 : Boolean", 44, TokenType.VARIABLE, "$t0");
      outlineTreeHelper.addOutlineItem("$t1 : Boolean", 45, TokenType.VARIABLE, "$t1");
      outlineTreeHelper.addOutlineItem("$t2 : Integer", 47, TokenType.VARIABLE, "$t2");
      outlineTreeHelper.addOutlineItem("$t3 : Integer", 48, TokenType.VARIABLE, "$t3");
      outlineTreeHelper.addOutlineItem("$t4 : Integer", 49, TokenType.VARIABLE, "$t4");
      outlineTreeHelper.addOutlineItem("$t5 : Float", 51, TokenType.VARIABLE, "$t5");
      outlineTreeHelper.addOutlineItem("$t6 : Float", 52, TokenType.VARIABLE, "$t6");
      outlineTreeHelper.addOutlineItem("$t7 : String", 54, TokenType.VARIABLE, "$t7");
      outlineTreeHelper.addOutlineItem("$t8 : String", 55, TokenType.VARIABLE, "$t8");
      outlineTreeHelper.addOutlineItem("$t9 : Null", 57, TokenType.VARIABLE, "$t9");
      outlineTreeHelper.addOutlineItem("$t10 : SimpleXMLElement", 59, TokenType.VARIABLE, "$t10");
      outlineTreeHelper.addOutlineItem("$t11 : \\my\\name\\MyClass", 60, TokenType.VARIABLE, "$t11");
      outlineTreeHelper.addOutlineItem("$t12 : \\Exception", 61, TokenType.VARIABLE, "$t12");
      outlineTreeHelper.addOutlineItem("$t13", 65, TokenType.VARIABLE, "$t13");
      outlineTreeHelper.addOutlineItem("$t14", 66, TokenType.VARIABLE, "$t14");
      outlineTreeHelper.addOutlineItem("$t15 : Array", 68, TokenType.VARIABLE, "$t15");
      outlineTreeHelper.addOutlineItem("$parent", 71, TokenType.VARIABLE, "$parent");
      outlineTreeHelper.addOutlineItem("atest", 73, TokenType.CLASS, "test");
      outlineTreeHelper.addOutlineItem("domainObjectBuilder($var2)", 75, TokenType.METHOD, "domainObjectBuilder");
      outlineTreeHelper.addOutlineItem("$test : Integer", 76, TokenType.VARIABLE, "$test");
      outlineTreeHelper.addOutlineItem("MYCONST : String", 83, TokenType.CLASS_CONSTANT, "MYCONST");
      outlineTreeHelper.addOutlineItem("$a", 90, TokenType.PROPERTY, "$a");
      outlineTreeHelper.addOutlineItem("s$b : String", 92, TokenType.PROPERTY, "$b");      
      outlineTreeHelper.addOutlineItem("s$s", 93, TokenType.PROPERTY, "$s");
      outlineTreeHelper.addOutlineItem("floadPageXML($filename : UnderflowException, $merge : array, $x : ArrayObject, $y)", 98, TokenType.METHOD, "loadPageXML");
      outlineTreeHelper.addOutlineItem("$state : Integer", 102, TokenType.VARIABLE, "$state");
      outlineTreeHelper.addOutlineItem("$sql : String", 103, TokenType.VARIABLE, "$sql");
      outlineTreeHelper.addOutlineItem("$bitpattern : Integer", 113, TokenType.VARIABLE, "$bitpattern");
      outlineTreeHelper.addOutlineItem("$composite_string : String", 120, TokenType.VARIABLE, "$composite_string");
      outlineTreeHelper.addOutlineItem("makecoffee_error($types, $coffeeMaker)", 129, TokenType.METHOD, "makecoffee_error");
      outlineTreeHelper.addOutlineItem("$placeholders", 131, TokenType.VARIABLE, "$placeholders");
      outlineTreeHelper.addOutlineItem("$i : Integer", 142, TokenType.VARIABLE, "$i");
      outlineTreeHelper.addOutlineItem("$test : String", 159, TokenType.VARIABLE, "$test");
      outlineTreeHelper.addOutlineItem("$q1 : Integer", 160, TokenType.VARIABLE, "$q1");
      outlineTreeHelper.addOutlineItem("$k : Integer", 165, TokenType.VARIABLE, "$k");
      outlineTreeHelper.addOutlineItem("r3:cphp", 174, TokenType.TAG, "r3:cphp");
      outlineTreeHelper.addOutlineItem("r4:cphp", 178, TokenType.TAG, "r4:cphp");
      outlineTreeHelper.addOutlineItem("php code", 186, TokenType.PHP_TAG, "php code");
      outlineTreeHelper.addOutlineItem("php code", 191, TokenType.PHP_TAG, "php code");
      outlineTreeHelper.addOutlineItem("TEST : String", 192, TokenType.CONSTANT, "TEST");
      outlineTreeHelper.addOutlineItem("test($a, $b)", 195, TokenType.FUNCTION, "test");
      outlineTreeHelper.addOutlineItem("Foo", 202, TokenType.CLASS, "Foo");
      outlineTreeHelper.addOutlineItem("E_USER1_ERROR : Integer", 203, TokenType.CLASS_CONSTANT, "E_USER1_ERROR");
      outlineTreeHelper.addOutlineItem("$test", 204, TokenType.PROPERTY, "$test");
      outlineTreeHelper.addOutlineItem("s$my_static : String", 206, TokenType.PROPERTY, "$my_static");
      outlineTreeHelper.addOutlineItem("test($test)", 207, TokenType.METHOD, "test");
      outlineTreeHelper.addOutlineItem("sfoo()", 210, TokenType.METHOD, "foo");
      outlineTreeHelper.addOutlineItem("foo($a)", 223, TokenType.FUNCTION, "foo");
      outlineTreeHelper.addOutlineItem("$args : Exception", 228, TokenType.VARIABLE, "$args");
      
      // check is tree created correctly      
      outlineTreeHelper.checkOutlineTree();
   }

   @Ignore      //TODO Issue IDE - 466
   @AfterClass
   public static void tearDown() throws Exception
   {
     IDE.EDITOR.closeFile(0);
      cleanDefaultWorkspace();
   }
}
