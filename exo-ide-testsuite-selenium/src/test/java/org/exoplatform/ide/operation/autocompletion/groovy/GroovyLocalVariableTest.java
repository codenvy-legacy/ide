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

import junit.framework.Assert;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

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
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testLocalVariable() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + TEST_FOLDER + "/");

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + TEST_FOLDER + "/" + FILE_NAME, false);
      moveCursorDown(15);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementNotPresent("s:String");
      IDE.CODEASSISTANT.closeForm();

      moveCursorDown(5);
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementPresent("s:String");
      IDE.CODEASSISTANT.checkElementPresent("name:String");
      IDE.CODEASSISTANT.checkElementPresent("e:Exception");
      IDE.CODEASSISTANT.checkElementNotPresent("stream:PrintStream");
      IDE.CODEASSISTANT.checkElementNotPresent("d");
      IDE.CODEASSISTANT.checkElementNotPresent("ii");
      IDE.CODEASSISTANT.closeForm();

      moveCursorDown(2);
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementPresent("s:String");
      IDE.CODEASSISTANT.checkElementPresent("name:String");
      IDE.CODEASSISTANT.checkElementPresent("e:Exception");
      IDE.CODEASSISTANT.checkElementPresent("stream:PrintStream");
      IDE.CODEASSISTANT.checkElementNotPresent("d:Double");
      IDE.CODEASSISTANT.checkElementNotPresent("ii:Integer");
      IDE.CODEASSISTANT.closeForm();

      moveCursorDown(7);
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementPresent("s:String");
      IDE.CODEASSISTANT.checkElementNotPresent("name:String");
      IDE.CODEASSISTANT.checkElementNotPresent("e:Exception");
      IDE.CODEASSISTANT.checkElementNotPresent("stream:PrintStream");
      IDE.CODEASSISTANT.checkElementPresent("d:Double");
      IDE.CODEASSISTANT.checkElementPresent("ii:Integer");
      IDE.CODEASSISTANT.closeForm();

      moveCursorDown(2);
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementNotPresent("s:String");
      IDE.CODEASSISTANT.checkElementNotPresent("name:String");
      IDE.CODEASSISTANT.checkElementNotPresent("e:Exception");
      IDE.CODEASSISTANT.checkElementNotPresent("stream:PrintStream");
      IDE.CODEASSISTANT.checkElementNotPresent("d:Double");
      IDE.CODEASSISTANT.checkElementNotPresent("ii:Integer");
      IDE.CODEASSISTANT.closeForm();
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);

      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementNotPresent("hello(String):List<Item>");
      IDE.CODEASSISTANT.checkElementNotPresent("getInt(Double):Integer");
      IDE.CODEASSISTANT.checkElementNotPresent("s:String");
      IDE.CODEASSISTANT.checkElementNotPresent("name:String");
      IDE.CODEASSISTANT.checkElementNotPresent("e:Exception");
      IDE.CODEASSISTANT.checkElementNotPresent("stream:PrintStream");
      IDE.CODEASSISTANT.checkElementNotPresent("d:Double");
      IDE.CODEASSISTANT.checkElementNotPresent("ii:Integer");
      IDE.CODEASSISTANT.closeForm();
      
     //TODO this block should be remove after fix problem in issue IDE-804. File does not should be modified  
     if (IDE.EDITOR.isFileContentChanged(0)){
      
      IDE.EDITOR.closeTabIgnoringChanges(0);
     }
     else
       IDE.EDITOR.closeFile(0);

   }

   /**
    * @throws InterruptedException
    */
   private void moveCursorDown(int row) throws InterruptedException
   {
      Thread.sleep(TestConstants.SLEEP_SHORT);
      for (int i = 0; i < row; i++)
      {
         selenium().keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
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
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

}
