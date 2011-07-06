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
package org.exoplatform.ide.operation.autocompletion;

import static org.junit.Assert.assertTrue;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

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
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @After
   public void tearDown() throws Exception
   {
      //IDE.EDITOR.closeFileTabIgnoreChanges(0);
      IDE.EDITOR.closeTabIgnoringChanges(0);

      try
      {
         VirtualFileSystemUtils.delete(WORKSPACE_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   @Test
   public void testNetvibesApiAutocompletions() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      /*
       * 1. Open netvibes file.
       */
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WORKSPACE_URL + FOLDER_NAME + "/" + NETVIBES_NAME, false);

      /*
       * 2. Go inside <code><script></code> tag.
       */
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("widget");
      IDE.CODEASSISTANT.checkElementPresent("UWA");

      IDE.CODEASSISTANT.typeToInput("w");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.EDITOR.typeTextIntoEditor(0, ".");
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("addBody(content)");
      IDE.CODEASSISTANT.checkElementPresent("createElement(tagName,options) : Element");

      IDE.CODEASSISTANT.typeToInput("getE");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("widget.getElement(selector)"));

      IDE.CODEASSISTANT.moveCursorDown(1);

      IDE.EDITOR.typeTextIntoEditor(0, "UWA.");
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("Array");
      IDE.CODEASSISTANT.checkElementPresent("Data");
      IDE.CODEASSISTANT.checkElementPresent("Element");
      IDE.CODEASSISTANT.checkElementPresent("Json");
      IDE.CODEASSISTANT.checkElementPresent("String");
      IDE.CODEASSISTANT.checkElementPresent("Utils");
      IDE.CODEASSISTANT.checkElementPresent("Widget");

      IDE.CODEASSISTANT.typeToInput("Data");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("UWA.Data"));

      IDE.EDITOR.typeTextIntoEditor(0, ".");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("domainMatch(url, options) : Boolean");
      IDE.CODEASSISTANT.checkElementPresent("getFeed(url, callback)");
      IDE.CODEASSISTANT.checkElementPresent("getOfflineCache()");
      IDE.CODEASSISTANT.checkElementPresent("getText(url, callback)");
      IDE.CODEASSISTANT.checkElementPresent("getXml(url, callback)");
      IDE.CODEASSISTANT.checkElementPresent("proxifyUrl(url, options)");
      IDE.CODEASSISTANT.checkElementPresent("request(url, options)");
      IDE.CODEASSISTANT.checkElementPresent("storeInCache(url, callbackArguments)");

      IDE.CODEASSISTANT.typeToInput("getF");

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("UWA.Data.getFeed(url, callback)"));
   }

   @Test
   public void testNetvibesLocalVar() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      /*
       * 1. Open netvibes file.
       */
      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WORKSPACE_URL + FOLDER_NAME + "/" + NETVIBES_NAME, false);

      /*
       * 2. Go inside <code><script></code> tag.
       */
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      IDE.EDITOR.typeTextIntoEditor(0, "var a = new Array(); \n var b = new UWA.Element(); \n a.");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("concat(array2, array3, ...): Array");
      IDE.CODEASSISTANT.checkElementPresent("detect(iterator): Boolean");
      IDE.CODEASSISTANT.checkElementPresent("equals(compare): Boolean");
      IDE.CODEASSISTANT.checkElementPresent("every(fn, bind)");
      IDE.CODEASSISTANT.checkElementPresent("filter(fn, bind)");
      IDE.CODEASSISTANT.checkElementPresent("forEach(fn, bind)");
      IDE.CODEASSISTANT.checkElementPresent("length:Number");
      IDE.CODEASSISTANT.typeToInput("som");

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("a.some(fn, bind)"));

      IDE.CODEASSISTANT.moveCursorDown(1);

      IDE.EDITOR.typeTextIntoEditor(0, "b.");

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("addClassName(className) : Object");
      IDE.CODEASSISTANT.checkElementPresent("getPosition() : Object");
      IDE.CODEASSISTANT.checkElementPresent("hide() : Object");
      IDE.CODEASSISTANT.checkElementPresent("show() : Object");
      IDE.CODEASSISTANT.checkElementPresent("setHTML(html) : Object");
      IDE.CODEASSISTANT.checkElementPresent("setContent(content) : Object");
      IDE.CODEASSISTANT.checkElementPresent("inject(el, where) : Object");

      IDE.CODEASSISTANT.typeToInput("setOpa");

      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("b.setOpacity(value)"));
   }
}
