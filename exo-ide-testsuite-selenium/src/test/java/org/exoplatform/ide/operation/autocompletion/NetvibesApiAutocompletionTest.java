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
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: NetvibesApiAutocompletionsTest Jan 27, 2011 2:03:27 PM evgen $
 *
 */
public class NetvibesApiAutocompletionTest extends CodeAssistantBaseTest
{

   private static final String NETVIBES_NAME = "Netvibes.html";

   private static final String NETVIBES_CONTENT = "<script type=\"text/javascript\">\n\n\n\n</script>";

   @BeforeClass
   public static void setUp() throws IOException
   {
      createProject(NetvibesApiAutocompletionTest.class.getSimpleName());

      VirtualFileSystemUtils.createFile(project.get(Link.REL_CREATE_FILE), NETVIBES_NAME, MimeType.UWA_WIDGET,
         NETVIBES_CONTENT);
   }

   @Before
   public void openFile() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + NETVIBES_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + NETVIBES_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/" + NETVIBES_NAME);
   }

   @Test
   public void testNetvibesApiAutocompletions() throws Exception
   {
      /*
       * 2. Go inside <code><script></code> tag.
       */
      IDE.EDITOR.moveCursorDown(0, 1);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("widget");
      IDE.CODEASSISTANT.checkElementPresent("UWA");

      IDE.CODEASSISTANT.typeToInput("w");
      IDE.CODEASSISTANT.insertSelectedItem();

      IDE.EDITOR.typeTextIntoEditor(0, ".");
      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.checkElementPresent("addBody(content)");
      IDE.CODEASSISTANT.checkElementPresent("createElement(tagName,options) : Element");

      IDE.CODEASSISTANT.typeToInput("getE");
      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("widget.getElement(selector)"));

      IDE.EDITOR.moveCursorDown(0, 1);

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
      IDE.CODEASSISTANT.insertSelectedItem();

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
      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("UWA.Data.getFeed(url, callback)"));
   }

   @Test
   public void testNetvibesLocalVar() throws Exception
   {
      /*
       * 2. Go inside <code><script></code> tag.
       */
      IDE.EDITOR.moveCursorDown(0, 1);

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

      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("a.some(fn, bind)"));

      IDE.EDITOR.moveCursorDown(0, 1);

      IDE.EDITOR.typeTextIntoEditor(0, "b.");
      
      //sleep form to give editor time to parse file content
      Thread.sleep(TestConstants.SLEEP_SHORT);
      
      IDE.CODEASSISTANT.openForm();
      
      IDE.CODEASSISTANT.checkElementPresent("addClassName(className) : Object");
      IDE.CODEASSISTANT.checkElementPresent("getPosition() : Object");
      IDE.CODEASSISTANT.checkElementPresent("hide() : Object");
      IDE.CODEASSISTANT.checkElementPresent("show() : Object");
      IDE.CODEASSISTANT.checkElementPresent("setHTML(html) : Object");
      IDE.CODEASSISTANT.checkElementPresent("setContent(content) : Object");
      IDE.CODEASSISTANT.checkElementPresent("inject(el, where) : Object");

      IDE.CODEASSISTANT.typeToInput("setOpa");

      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("b.setOpacity(value)"));
   }
}
