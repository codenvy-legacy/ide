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
package org.exoplatform.ide.operation.file.autocompletion.groovy;

import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: Dec 6, 2010 $
 *
 */
public class ImportStatementInsertionTest extends BaseTest
{

   private final static String SERVICE_FILE_NAME = "import-statement-insertion.groovy";
   
   private final static String TEMPLATE_FILE_NAME = "import-statement-insertion.gtmpl";

   private final static String TEST_FOLDER = ImportStatementInsertionTest.class.getSimpleName();
   
   private static final String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME + "/";

   @BeforeClass
   public static void setUp()
   {

      String serviceFilePath = "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/" + SERVICE_FILE_NAME;
      String templateFilePath = "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/" + TEMPLATE_FILE_NAME;
      
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(serviceFilePath, MimeType.GROOVY_SERVICE, URL + TEST_FOLDER + "/" + SERVICE_FILE_NAME);
         VirtualFileSystemUtils.put(templateFilePath, MimeType.GROOVY_TEMPLATE, URL + TEST_FOLDER + "/" + TEMPLATE_FILE_NAME);         
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

      
//   GWTX-64: "Don't insert "import <FQN>;" statement if this is class from default package or there is existed import in the header."


   @Test
   public void testServiceFile() throws Exception
   {
      // Open groovy file with test content
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + SERVICE_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP * 2);   
      
      // goto line 14, type "B" symbol and then click on Ctrl+Space. Then select "Base64" class item from non-default package and press "Enter" key.
      goToLine(14);
     IDE.EDITOR.typeTextIntoEditor(0, "B"); 
      IDE.CODEASSISTANT.openForm();
      selenium.clickAt(getErrorCorrectionListItemLocator("Base64"), "");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP);
      
      // test import statement
     IDE.EDITOR.clickOnEditor();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(
         "// simple groovy script\n" 
         + "import javax.ws.rs.Path\n"
         + "import javax.ws.rs.GET\n"
         + "import javax.ws.rs.PathParam\n"
         + "import java.util.prefs.Base64\n"
         + "\n"
         + "@Path("
      ));
      
      // Empty line 14, type "B" symbol and then click on Ctrl+Space. Then select "BitSet" class item from default package and press "Enter" key.
      goToLine(14);
     IDE.EDITOR.deleteLinesInEditor(1);
     IDE.EDITOR.typeTextIntoEditor(0, "B"); 
      IDE.CODEASSISTANT.openForm();
      selenium.clickAt(getErrorCorrectionListItemLocator("BitSet"), "");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP);
      
      // test import statement
     IDE.EDITOR.clickOnEditor();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(
         "// simple groovy script\n" 
         + "import javax.ws.rs.Path\n"
         + "import javax.ws.rs.GET\n"
         + "import javax.ws.rs.PathParam\n"
         + "import java.util.prefs.Base64\n"
         + "\n"
         + "@Path("
      ));
      
      // Empty line 14 and then click on Ctrl+Space. Then select "HelloWorld" class item with current class name and press "Enter" key.
      goToLine(14);
     IDE.EDITOR.deleteLinesInEditor(1);
     IDE.EDITOR.typeTextIntoEditor(0, " "); 
      IDE.CODEASSISTANT.openForm();
      selenium.clickAt(getErrorCorrectionListItemLocator("HelloWorld"), "");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP);
      
      // test import statement
     IDE.EDITOR.clickOnEditor();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).startsWith(
         "// simple groovy script\n" 
         + "import javax.ws.rs.Path\n"
         + "import javax.ws.rs.GET\n"
         + "import javax.ws.rs.PathParam\n"
         + "import java.util.prefs.Base64\n"
         + "\n"
         + "@Path("
      ));
   }

   // Could be turned on after the will be realized an autocomplete within the ECM Template file.
   // @Test
   public void testTemplateFile() throws Exception
   {
      // Open ECM template file with test content
      Thread.sleep(TestConstants.SLEEP);
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.WORKSPACE.selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.TOOLBAR.runCommand(ToolbarCommands.File.REFRESH);
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + TEMPLATE_FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP * 2);   
      
      // goto line 15, type "B" symbol and then click on Ctrl+Space. Then select "Base64" class item from non-default package and press "Enter" key.
      goToLine(15);
     IDE.EDITOR.typeTextIntoEditor(1, "B"); 
      IDE.CODEASSISTANT.openForm();
      selenium.clickAt(getErrorCorrectionListItemLocator("Base64"), "");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP);
      
      // test import statement
     IDE.EDITOR.clickOnEditor();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(1).startsWith(
         "<html>"
         + "   <head>"
         + "     <%"
         + "       import javax.ws.rs.Path"
         + "       import javax.ws.rs.GET"
         + "       import javax.ws.rs.PathParam"
         + "       import java.util.prefs.Base64"
         + "     %>"
         + "   </head>"
      ));     
      
      // Empty line 15, type "B" symbol and then click on Ctrl+Space. Then select "BitSet" class item from default package and press "Enter" key.
      goToLine(15);
     IDE.EDITOR.deleteLinesInEditor(1);
     IDE.EDITOR.typeTextIntoEditor(1, "B"); 
      IDE.CODEASSISTANT.openForm();
      selenium.clickAt(getErrorCorrectionListItemLocator("BitSet"), "");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.SLEEP);
      
      // test import statement
     IDE.EDITOR.clickOnEditor();
      assertTrue(IDE.EDITOR.getTextFromCodeEditor(1).startsWith(
         "<html>"
         + "   <head>"
         + "     <%"
         + "       import javax.ws.rs.Path"
         + "       import javax.ws.rs.GET"
         + "       import javax.ws.rs.PathParam"
         + "       import java.util.prefs.Base64"
         + "     %>"
         + "   </head>"
      ));
   }   
   
   @AfterClass
   public static void tearDown() throws Exception
   {
//     IDE.EDITOR.closeFileTabIgnoreChanges(1);
     IDE.EDITOR.closeFileTabIgnoreChanges(0);
      
      try
      {
         VirtualFileSystemUtils.delete(URL + TEST_FOLDER);
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

   private String getErrorCorrectionListItemLocator(String packageName)
   {
      return "//div[@class='gwt-Label' and contains(text(),'" + packageName + "')]";
   }
}