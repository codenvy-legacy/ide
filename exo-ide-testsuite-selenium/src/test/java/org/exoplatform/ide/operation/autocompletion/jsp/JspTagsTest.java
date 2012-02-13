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

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JspTagsTest May 6, 2011 12:01:55 PM evgen $
 *
 */
public class JspTagsTest extends BaseTest
{

   private static final String PROJECT = JspTagsTest.class.getSimpleName();

   private static final String FOLDER_NAME = JspTagsTest.class.getSimpleName();

   private static final String FILE_NAME = "JspTagsTest.jsp";

   private String docMessage =
      "A jsp:useBean action associates an instance of a Java programming language object defined within a given scope and available with a given id with a newly declared scripting variable of the same id. When a <jsp:useBean> action is used in an scriptless page, or in an scriptless context (as in the body of an action so indicated), there are no Java scripting variables created but instead an EL variable is created.";

   @BeforeClass
   public static void setUp()
   {
      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.createDefaultProject(PROJECT);

         VirtualFileSystemUtils.mkcol(WS_URL + PROJECT + "/" + FOLDER_NAME + "/");
         VirtualFileSystemUtils.put(
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/jsp/testJspTag.jsp",
            MimeType.APPLICATION_JSP, WS_URL + PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      }
      catch (Exception e)
      {
         fail("Can't create test folder");
      }
   }

   @Test
   public void testJspTag() throws Exception
   {
     
      
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME);

      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + FOLDER_NAME + "/" + FILE_NAME);

      IDE.GOTOLINE.goToLine(10);

     IDE.CODEASSISTANT.openForm();
      IDE.EDITOR.typeTextIntoEditor(0, "<jsp:");
      IDE.CODEASSISTANT.openForm();
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:attribute"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:body"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:element"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:fallback"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:forward"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:getProperty"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:include"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:invoke"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:output"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:plugin"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:text"));
      assertTrue(IDE.CODEASSISTANT.isElementPresent("jsp:useBean"));

      
      
      IDE.CODEASSISTANT.typeToInput("use");
      
    
     //TODO waitForElementPresent(CodeAssistant.Locators.JAVADOC_DIV));
      IDE.CODEASSISTANT.checkDocFormPresent();
      //TODO assertEquals(docMessage, selenium().getText(CodeAssistant.Locators.JAVADOC_DIV)));
      IDE.CODEASSISTANT.insertSelectedItem();
      
      IDE.EDITOR.getTextFromCodeEditor(0).contains("<jsp:useBean id=\"\"></jsp:useBean>");
      
     //IDE.EDITOR.closeUnsavedFileAndDoNotSave(0));
    // IDE.EDITOR.closeTabIgnoringChanges(0);
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
