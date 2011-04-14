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

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Dec 15, 2010 11:11:29 AM evgen $
 *
 */
// http://jira.exoplatform.org/browse/IDE-478
public class GroovyLocalVariableTest extends BaseTest
{

   private static String FILE_NAME = "GroovyLocalVariable.groovy";

   private final static String TEST_FOLDER = GroovyLocalVariableTest.class.getSimpleName();

   private final static String URL = BASE_URL + REST_CONTEXT + "/" + WEBDAV_CONTEXT + "/" + REPO_NAME + "/" + WS_NAME
      + "/";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(URL + TEST_FOLDER);
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/groovyLocalVar.groovy",
            MimeType.GROOVY_SERVICE, TestConstants.NodeTypes.EXO_GROOVY_RESOURCE_CONTAINER, URL + TEST_FOLDER + "/"
               + FILE_NAME);
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
   public void testLocalVariable() throws Exception
   {
      waitForRootElement();
      IDE.navigator().selectItem(WS_URL);
      Thread.sleep(TestConstants.SLEEP);
      IDE.navigator().selectItem(WS_URL + TEST_FOLDER + "/");
      IDE.menu().runCommand(MenuCommands.File.FILE, MenuCommands.File.REFRESH);
      Thread.sleep(TestConstants.SLEEP);
      openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME, false);
      Thread.sleep(TestConstants.SLEEP);
      moveCursorDown(15);

      IDE.codeAssistant().openForm();

      IDE.codeAssistant().checkElementPresent("hello(String):String");
      IDE.codeAssistant().checkElementPresent("getInt(Double):Integer");
      IDE.codeAssistant().checkElementNotPresent("s:String");
      IDE.codeAssistant().closeForm();
      
      moveCursorDown(5);
      IDE.codeAssistant().openForm();
      IDE.codeAssistant().checkElementPresent("hello(String):String");
      IDE.codeAssistant().checkElementPresent("getInt(Double):Integer");
      IDE.codeAssistant().checkElementPresent("s:String");
      IDE.codeAssistant().checkElementPresent("name:String");
      IDE.codeAssistant().checkElementPresent("e:Exception");
      IDE.codeAssistant().checkElementNotPresent("stream:PrintStream");
      IDE.codeAssistant().checkElementNotPresent("d");
      IDE.codeAssistant().checkElementNotPresent("ii");
      IDE.codeAssistant().closeForm();

      moveCursorDown(2);
      IDE.codeAssistant().openForm();
      IDE.codeAssistant().checkElementPresent("hello(String):String");
      IDE.codeAssistant().checkElementPresent("getInt(Double):Integer");
      IDE.codeAssistant().checkElementPresent("s:String");
      IDE.codeAssistant().checkElementPresent("name:String");
      IDE.codeAssistant().checkElementPresent("e:Exception");
      IDE.codeAssistant().checkElementPresent("stream:PrintStream");
      IDE.codeAssistant().checkElementNotPresent("d:Double");
      IDE.codeAssistant().checkElementNotPresent("ii:Integer");
      IDE.codeAssistant().closeForm();

      moveCursorDown(7);
      IDE.codeAssistant().openForm();
      IDE.codeAssistant().checkElementPresent("hello(String):String");
      IDE.codeAssistant().checkElementPresent("getInt(Double):Integer");
      IDE.codeAssistant().checkElementPresent("s:String");
      IDE.codeAssistant().checkElementNotPresent("name:String");
      IDE.codeAssistant().checkElementNotPresent("e:Exception");
      IDE.codeAssistant().checkElementNotPresent("stream:PrintStream");
      IDE.codeAssistant().checkElementPresent("d:Double");
      IDE.codeAssistant().checkElementPresent("ii:Integer");
      IDE.codeAssistant().closeForm();

      moveCursorDown(2);
      IDE.codeAssistant().openForm();

      IDE.codeAssistant().checkElementPresent("hello(String):String");
      IDE.codeAssistant().checkElementPresent("getInt(Double):Integer");
      IDE.codeAssistant().checkElementNotPresent("s:String");
      IDE.codeAssistant().checkElementNotPresent("name:String");
      IDE.codeAssistant().checkElementNotPresent("e:Exception");
      IDE.codeAssistant().checkElementNotPresent("stream:PrintStream");
      IDE.codeAssistant().checkElementNotPresent("d:Double");
      IDE.codeAssistant().checkElementNotPresent("ii:Integer");
      IDE.codeAssistant().closeForm();
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);

      IDE.codeAssistant().openForm();
      IDE.codeAssistant().checkElementNotPresent("hello(String):String");
      IDE.codeAssistant().checkElementNotPresent("getInt(Double):Integer");
      IDE.codeAssistant().checkElementNotPresent("s:String");
      IDE.codeAssistant().checkElementNotPresent("name:String");
      IDE.codeAssistant().checkElementNotPresent("e:Exception");
      IDE.codeAssistant().checkElementNotPresent("stream:PrintStream");
      IDE.codeAssistant().checkElementNotPresent("d:Double");
      IDE.codeAssistant().checkElementNotPresent("ii:Integer");
      IDE.codeAssistant().closeForm();
      IDE.editor().closeTab(0);
   }

   /**
    * @throws InterruptedException
    */
   private void moveCursorDown(int row) throws InterruptedException
   {
      Thread.sleep(TestConstants.SLEEP_SHORT);
      for (int i = 0; i < row; i++)
      {
         selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
         Thread.sleep(TestConstants.SLEEP_SHORT);
      }
   }

   @AfterClass
   public static void tearDown()
   {
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

}
