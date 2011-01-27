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

import java.io.IOException;

import org.exoplatform.common.http.client.ModuleException;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.ToolbarCommands;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.Autocomplete;
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
      selectItemInWorkspaceTree(FOLDER_NAME);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);

      /*
       * 1. Open netvibes file.
       */
      openFileFromNavigationTreeWithCodeEditor(NETVIBES_NAME, false);

      /*
       * 2. Go inside <code><script></code> tag.
       */
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      Autocomplete.openForm();

      Autocomplete.checkElementPresent("widget");
      Autocomplete.checkElementPresent("UWA");

      Autocomplete.typeToInput("w");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      typeTextIntoEditor(0, ".");
      Autocomplete.openForm();

      Autocomplete.checkElementPresent("addBody(content)");
      Autocomplete.checkElementPresent("createElement(tagName,options) : Element");

      Autocomplete.typeToInput("getE");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertTrue(getTextFromCodeEditor(0).contains("widget.getElement(selector)"));
      
      Autocomplete.moveCursorDown(1);
      
      typeTextIntoEditor(0, "UWA.");
      Autocomplete.openForm();
      
      Autocomplete.checkElementPresent("Array");
      Autocomplete.checkElementPresent("Data");
      Autocomplete.checkElementPresent("Element");
      Autocomplete.checkElementPresent("Json");
      Autocomplete.checkElementPresent("String");
      Autocomplete.checkElementPresent("Utils");
      Autocomplete.checkElementPresent("Widget");
      
      Autocomplete.typeToInput("Data");
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertTrue(getTextFromCodeEditor(0).contains("UWA.Data"));
      
      typeTextIntoEditor(0, ".");
      
      Autocomplete.openForm();
      
      Autocomplete.checkElementPresent("domainMatch(url, options) : Boolean");
      Autocomplete.checkElementPresent("getFeed(url, callback)");
      Autocomplete.checkElementPresent("getOfflineCache()");
      Autocomplete.checkElementPresent("getText(url, callback)");
      Autocomplete.checkElementPresent("getXml(url, callback)");
      Autocomplete.checkElementPresent("proxifyUrl(url, options)");
      Autocomplete.checkElementPresent("request(url, options)");
      Autocomplete.checkElementPresent("storeInCache(url, callbackArguments)");
      
      Autocomplete.typeToInput("getF");
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertTrue(getTextFromCodeEditor(0).contains("UWA.Data.getFeed(url, callback)"));
      
      IDE.editor().closeFileTabIgnoreChanges(0);

   }
   
   @Test
   public void testNetvibesLocalVar() throws Exception
   {
      selenium.refresh();
      selenium.waitForPageToLoad("30000");
      waitForRootElement();
      selectItemInWorkspaceTree(FOLDER_NAME);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);
      
      waitForRootElement();
      selectItemInWorkspaceTree(FOLDER_NAME);
      IDE.toolbar().runCommand(ToolbarCommands.File.REFRESH);

      /*
       * 1. Open netvibes file.
       */
      openFileFromNavigationTreeWithCodeEditor(NETVIBES_NAME, false);

      /*
       * 2. Go inside <code><script></code> tag.
       */
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_DOWN);
      Thread.sleep(TestConstants.REDRAW_PERIOD);

      typeTextIntoEditor(0, "var a = new Array(); \n var b = new UWA.Element(); \n a.");
      Autocomplete.openForm();
      Autocomplete.checkElementPresent("concat(array2, array3, ...): Array");
      Autocomplete.checkElementPresent("detect(iterator): Boolean");
      Autocomplete.checkElementPresent("equals(compare): Boolean");
      Autocomplete.checkElementPresent("every(fn, bind)");
      Autocomplete.checkElementPresent("filter(fn, bind)");
      Autocomplete.checkElementPresent("forEach(fn, bind)");
      Autocomplete.checkElementPresent("length:Number");
      Autocomplete.typeToInput("som");
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertTrue(getTextFromCodeEditor(0).contains("a.some(fn, bind)"));
      
      Autocomplete.moveCursorDown(1);
      
      typeTextIntoEditor(0, "b.");
      
      Autocomplete.openForm();
      
      Autocomplete.checkElementPresent("addClassName(className) : Object");
      Autocomplete.checkElementPresent("getPosition() : Object");
      Autocomplete.checkElementPresent("hide() : Object");
      Autocomplete.checkElementPresent("show() : Object");
      Autocomplete.checkElementPresent("setHTML(html) : Object");
      Autocomplete.checkElementPresent("setContent(content) : Object");
      Autocomplete.checkElementPresent("inject(el, where) : Object");
      
      Autocomplete.typeToInput("setOpa");
      
      selenium.keyPressNative("" + java.awt.event.KeyEvent.VK_ENTER);
      Thread.sleep(TestConstants.REDRAW_PERIOD);
      
      assertTrue(getTextFromCodeEditor(0).contains("b.setOpacity(value)"));
   }
}
