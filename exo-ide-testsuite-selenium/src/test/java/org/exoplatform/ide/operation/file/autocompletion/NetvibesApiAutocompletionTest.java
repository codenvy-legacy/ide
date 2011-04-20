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
package org.exoplatform.ide.operation.file.autocompletion;

import static org.junit.Assert.assertTrue;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: NetvibesApiAutocompletionsTest Jan 27, 2011 2:03:27 PM evgen $
 *
 */
public class NetvibesApiAutocompletionTest extends BaseTest
{

   private static final String FOLDER_NAME = NetvibesApiAutocompletionTest.class.getSimpleName();

   private static final String NETVIBES_NAME = "Netvibes.html";

   private static final String NETVIBES_CONTENT = "<script type=\"text/javascript\">\n\n\n\n</script>";

   @Before
   public void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WORKSPACE_URL + FOLDER_NAME);
         VirtualFileSystemUtils.put(NETVIBES_CONTENT.getBytes(), MimeType.UWA_WIDGET, WORKSPACE_URL + FOLDER_NAME + "/"
            + NETVIBES_NAME);
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

   @After
   public void tearDown() throws Exception
   {
      IDE.editor().closeFileTabIgnoreChanges(0);

      try
      {
         VirtualFileSystemUtils.delete(WORKSPACE_URL + FOLDER_NAME);
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
   public void testNetvibesApiAutocompletions() throws Exception
   {
      waitForRootElement();
      IDE.navigator().selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);

      /*
       * 1. Open netvibes file.
       */
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(WORKSPACE_URL + FOLDER_NAME + "/"
         + NETVIBES_NAME, false);

      /*
       * 2. Go inside <code><script></code> tag.
       */
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.codeAssistant().openForm();

      IDE.codeAssistant().checkElementPresent("widget");
      IDE.codeAssistant().checkElementPresent("UWA");

      IDE.codeAssistant().typeToInput("w");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.editor().typeTextIntoEditor(0, ".");
      IDE.codeAssistant().openForm();

      IDE.codeAssistant().checkElementPresent("addBody(content)");
      IDE.codeAssistant().checkElementPresent("createElement(tagName,options) : Element");

      IDE.codeAssistant().typeToInput("getE");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertTrue(IDE.editor().getTextFromCodeEditor(0).contains("widget.getElement(selector)"));
      
      IDE.codeAssistant().moveCursorDown(1);
      
      IDE.editor().typeTextIntoEditor(0, "UWA.");
      IDE.codeAssistant().openForm();
      
      IDE.codeAssistant().checkElementPresent("Array");
      IDE.codeAssistant().checkElementPresent("Data");
      IDE.codeAssistant().checkElementPresent("Element");
      IDE.codeAssistant().checkElementPresent("Json");
      IDE.codeAssistant().checkElementPresent("String");
      IDE.codeAssistant().checkElementPresent("Utils");
      IDE.codeAssistant().checkElementPresent("Widget");
      
      IDE.codeAssistant().typeToInput("Data");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertTrue(IDE.editor().getTextFromCodeEditor(0).contains("UWA.Data"));
      
      IDE.editor().typeTextIntoEditor(0, ".");
      
      IDE.codeAssistant().openForm();
      
      IDE.codeAssistant().checkElementPresent("domainMatch(url, options) : Boolean");
      IDE.codeAssistant().checkElementPresent("getFeed(url, callback)");
      IDE.codeAssistant().checkElementPresent("getOfflineCache()");
      IDE.codeAssistant().checkElementPresent("getText(url, callback)");
      IDE.codeAssistant().checkElementPresent("getXml(url, callback)");
      IDE.codeAssistant().checkElementPresent("proxifyUrl(url, options)");
      IDE.codeAssistant().checkElementPresent("request(url, options)");
      IDE.codeAssistant().checkElementPresent("storeInCache(url, callbackArguments)");
      
      IDE.codeAssistant().typeToInput("getF");
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertTrue(IDE.editor().getTextFromCodeEditor(0).contains("UWA.Data.getFeed(url, callback)"));
   }
   
   @Test
   public void testNetvibesLocalVar() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      waitForRootElement();
      IDE.navigator().selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      waitForRootElement();
      IDE.navigator().selectItem(WS_URL + FOLDER_NAME + "/");
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);

      /*
       * 1. Open netvibes file.
       */
      IDE.navigator().openFileFromNavigationTreeWithCodeEditor(WORKSPACE_URL + FOLDER_NAME + "/"
         + NETVIBES_NAME, false);

      /*
       * 2. Go inside <code><script></code> tag.
       */
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.editor().typeTextIntoEditor(0, "var a = new Array(); \n var b = new UWA.Element(); \n a.");
      IDE.codeAssistant().openForm();
      IDE.codeAssistant().checkElementPresent("concat(array2, array3, ...): Array");
      IDE.codeAssistant().checkElementPresent("detect(iterator): Boolean");
      IDE.codeAssistant().checkElementPresent("equals(compare): Boolean");
      IDE.codeAssistant().checkElementPresent("every(fn, bind)");
      IDE.codeAssistant().checkElementPresent("filter(fn, bind)");
      IDE.codeAssistant().checkElementPresent("forEach(fn, bind)");
      IDE.codeAssistant().checkElementPresent("length:Number");
      IDE.codeAssistant().typeToInput("som");
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertTrue(IDE.editor().getTextFromCodeEditor(0).contains("a.some(fn, bind)"));
      
      IDE.codeAssistant().moveCursorDown(1);
      
      IDE.editor().typeTextIntoEditor(0, "b.");
      
      IDE.codeAssistant().openForm();
      
      IDE.codeAssistant().checkElementPresent("addClassName(className) : Object");
      IDE.codeAssistant().checkElementPresent("getPosition() : Object");
      IDE.codeAssistant().checkElementPresent("hide() : Object");
      IDE.codeAssistant().checkElementPresent("show() : Object");
      IDE.codeAssistant().checkElementPresent("setHTML(html) : Object");
      IDE.codeAssistant().checkElementPresent("setContent(content) : Object");
      IDE.codeAssistant().checkElementPresent("inject(el, where) : Object");
      
      IDE.codeAssistant().typeToInput("setOpa");
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertTrue(IDE.editor().getTextFromCodeEditor(0).contains("b.setOpacity(value)"));
   }
}
