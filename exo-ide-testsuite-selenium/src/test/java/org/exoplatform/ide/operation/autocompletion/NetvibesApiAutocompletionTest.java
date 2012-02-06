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
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: NetvibesApiAutocompletionsTest Jan 27, 2011 2:03:27 PM evgen $
 * 
 */
public class NetvibesApiAutocompletionTest extends CodeAssistantBaseTest
{

   private static final String NETVIBES_NAME = "Netvibes.html";

   private static final String NETVIBES_CONTENT = "<script type=\"text/javascript\">\n\n\n\n</script>";

   @Before
   public void beforeTest() throws Exception
   {
      createProject(NetvibesApiAutocompletionTest.class.getSimpleName());

      VirtualFileSystemUtils.createFile(project.get(Link.REL_CREATE_FILE), NETVIBES_NAME, MimeType.UWA_WIDGET,
         NETVIBES_CONTENT);
      openProject();
      openFiles();
   }

   public void openFiles() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + NETVIBES_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/" + NETVIBES_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/" + NETVIBES_NAME);
      IDE.WELCOME_PAGE.close();
   }

   @Test
   public void testNetvibesApiAutocompletions() throws Exception
   {
      /*
       * 2. Go inside <code><script></code> tag.
       */
      IDE.EDITOR.moveCursorDown(0, 1);

      IDE.CODEASSISTANT.openForm();

      assertTrue(IDE.CODEASSISTANT.isElementPresent("widget"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("UWA"));

      IDE.CODEASSISTANT.typeToInput("w");
      IDE.CODEASSISTANT.insertSelectedItem();

      IDE.EDITOR.typeTextIntoEditor(0, ".");
      IDE.CODEASSISTANT.openForm();

      assertTrue(IDE.CODEASSISTANT.isElementPresent("addBody(content)"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("createElement(tagName,options) : Element"));

      IDE.CODEASSISTANT.typeToInput("getE");
      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("widget.getElement(selector)"));

      IDE.EDITOR.moveCursorDown(0, 1);

      IDE.EDITOR.typeTextIntoEditor(0, "UWA.");
      IDE.CODEASSISTANT.openForm();

      assertTrue(IDE.CODEASSISTANT.isElementPresent("Array"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Data"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Element"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Json"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("String"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Utils"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("Widget"));

      IDE.CODEASSISTANT.typeToInput("Data");
      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("UWA.Data"));

      IDE.EDITOR.typeTextIntoEditor(0, ".");

      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("domainMatch(url, options) : Boolean"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("getFeed(url, callback)"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("getOfflineCache()"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("getText(url, callback)"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("getXml(url, callback)"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("proxifyUrl(url, options)"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("request(url, options)"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("storeInCache(url, callbackArguments)"));

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
      assertTrue(IDE.CODEASSISTANT.isElementPresent("concat(array2, array3, ...): Array"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("detect(iterator): Boolean"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("equals(compare): Boolean"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("every(fn, bind)"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("filter(fn, bind)"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("forEach(fn, bind)"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("length:Number"));
      IDE.CODEASSISTANT.typeToInput("som");

      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("a.some(fn, bind)"));

      IDE.EDITOR.moveCursorDown(0, 1);

      IDE.EDITOR.typeTextIntoEditor(0, "b.");

      // sleep form to give editor time to parse file content
      Thread.sleep(TestConstants.SLEEP_SHORT);

      IDE.CODEASSISTANT.openForm();

      assertTrue(IDE.CODEASSISTANT.isElementPresent("addClassName(className) : Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("getPosition() : Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("hide() : Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("show() : Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("setHTML(html) : Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("setContent(content) : Object"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("inject(el, where) : Object"));

      IDE.CODEASSISTANT.typeToInput("setOpa");

      IDE.CODEASSISTANT.insertSelectedItem();

      assertTrue(IDE.EDITOR.getTextFromCodeEditor(0).contains("b.setOpacity(value)"));
   }
}
