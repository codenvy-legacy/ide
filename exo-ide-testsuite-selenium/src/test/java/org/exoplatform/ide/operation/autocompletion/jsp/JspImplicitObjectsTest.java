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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.CodeAssistant;
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

   private static final String FOLDER_NAME = JspImplicitObjectsTest.class.getSimpleName();

   private static final String FILE_NAME = "JspImplicitObjectsTest.jsp";

   private String docMessage =
      "The servlet context obtained from the servlet conﬁguration object (as in the call getServletConfig().getContext())";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         VirtualFileSystemUtils.mkcol(WS_URL + FOLDER_NAME + "/");
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/jsp/testImplicitObject.jsp",
            MimeType.APPLICATION_JSP, WS_URL + FOLDER_NAME + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail("Can't create test folder");
      }
   }

   @Test
   public void testJspImplicitObjects() throws Exception
   {
      IDE.WORKSPACE.waitForRootItem();
      IDE.WORKSPACE.doubleClickOnFolder(WS_URL + FOLDER_NAME + "/");

      IDE.NAVIGATION.openFileFromNavigationTreeWithCodeEditor(WS_URL + FOLDER_NAME + "/" + FILE_NAME, false);

      goToLine(10);
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.checkElementPresent("application:javax.servlet.ServletContext");
      IDE.CODEASSISTANT.checkElementPresent("config:javax.servlet.ServletConfig");
      IDE.CODEASSISTANT.checkElementPresent("exception:java.lang.Throwable");
      IDE.CODEASSISTANT.checkElementPresent("out:javax.servlet.jsp.JspWriter");
      IDE.CODEASSISTANT.checkElementPresent("page:java.lang.Object");
      IDE.CODEASSISTANT.checkElementPresent("pageContext:javax.servlet.jsp.PageContext");
      IDE.CODEASSISTANT.checkElementPresent("request:javax.servlet.http.HttpServletRequest");
      IDE.CODEASSISTANT.checkElementPresent("response:javax.servlet.http.HttpServletResponse");
      IDE.CODEASSISTANT.checkElementPresent("session:javax.servlet.http.HttpSession");
      
   //TODO   waitForElementPresent(CodeAssistant.Locators.JAVADOC_DIV);
      IDE.CODEASSISTANT.checkDocFormPresent();
 //TODO     assertEquals(docMessage, selenium().getText(CodeAssistant.Locators.JAVADOC_DIV));
      
      IDE.CODEASSISTANT.closeForm();
      
      IDE.EDITOR.closeFile(0);

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
         e.printStackTrace();
      }
   }

}
