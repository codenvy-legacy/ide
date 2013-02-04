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
package org.exoplatform.ide.operation.autocompletion.jsp;

import static org.junit.Assert.fail;

import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JspImplicitObjectsTest May 5, 2011 3:03:29 PM evgen $
 * 
 */
public class JspImplicitObjectsTest extends BaseTest
{

   private static final String PROJECT = JspImplicitObjectsTest.class.getSimpleName();

   private static final String FOLDER_NAME = JspImplicitObjectsTest.class.getSimpleName();

   private static final String FILE_NAME = "JspImplicitObjectsTest.jsp";

   private String docMessage =
      "The servlet context obtained from the servlet conﬁguration object (as in the call getServletConfig().getContext())";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);
         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_NAME + "/");
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/jsp/testImplicitObject.jsp",
            MimeType.APPLICATION_JSP, WS_URL + PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
         fail("Can't create test folder");
      }
   }

   @Test
   public void testJspImplicitObjects() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile();

      IDE.GOTOLINE.goToLine(10);

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.openForm();

      IDE.CODEASSISTANT.waitForElementInCodeAssistant("application:javax.servlet.ServletContext");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("config:javax.servlet.ServletConfig");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("exception:java.lang.Throwable");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("out:javax.servlet.jsp.JspWriter");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("page:java.lang.Object");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("pageContext:javax.servlet.jsp.PageContext");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("request:javax.servlet.http.HttpServletRequest");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("response:javax.servlet.http.HttpServletResponse");
      IDE.CODEASSISTANT.waitForElementInCodeAssistant("session:javax.servlet.http.HttpSession");

      IDE.CODEASSISTANT.waitForDocPanelOpened();
      IDE.CODEASSISTANT.checkDocFormPresent();
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WORKSPACE_URL + FOLDER_NAME);
      }
      catch (Exception e)
      {
      }
   }

}
