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
package org.exoplatform.ide.operation.autocompletion.java;

import static org.junit.Assert.fail;
import static org.fest.assertions.Assertions.*;

import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.List;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: JavaCodeAssistant Apr 15, 2011 10:32:15 AM evgen $
 * 
 */
public class JavaCodeAssistantTest extends CodeAssistantBaseTest
{
   private static final String FILE_NAME = "GreetingController.java";

   private static Object[] shuldContainsVars = new String[]{"request : HttpServletRequest", //
      "response : HttpServletResponse", //
      "userName : String",//
      "result : String",//
      "handleRequest(HttpServletRequest request, HttpServletResponse response) : ModelAndView - GreetingController",//
      "equals(Object arg0) : boolean - Object",//
      "GreetingController - helloworld"};

   private static Object[] shuldContainsMethods = new String[]{"toArray(T[] arg0) : T[] - List",//
      "subList(int arg0, int arg1) : List<String> - List",//
      "removeAll(Collection<?> arg0) : boolean - List",//
      "listIterator() : ListIterator<String> - List",//
      "addAll(Collection<? extends String> arg0) : boolean - List",//
      "getClass() : Class<?> - Object"};
   
   private static Object[] shuldContainsOverride = new String[]{//
      "toString() : String - Override method in 'Object'",//
      "hashCode() : int - Override method in 'Object'",
      "equals(Object arg0) : boolean - Override method in 'Object'", //
      "clone() : Object - Override method in 'Object'"//
   };

   @Before
   public void beforeTest() throws Exception
   {
      try
      {
         createProject(JavaCodeAssistantTest.class.getSimpleName(),
            "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/JavaTestProject.zip");
      }
      catch (Exception e)
      {
         fail("Can't create test folder");
      }
      openProject();
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/" + "pom.xml");
      IDE.PROJECT.EXPLORER.openItem(projectName + "/src");
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/src/main");
      IDE.PROJECT.EXPLORER.openItem(projectName + "/src/main");
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/src/main/java");
      IDE.PROJECT.EXPLORER.openItem(projectName + "/src/main/java");
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/src/main/java/helloworld");
      IDE.PROJECT.EXPLORER.openItem(projectName + "/src/main/java/helloworld");
      IDE.PROJECT.EXPLORER.waitForItem(projectName + "/src/main/java/helloworld/" + FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/src/main/java/helloworld/" + FILE_NAME);
      IDE.EDITOR.waitActiveFile(projectName + "/src/main/java/helloworld/" + FILE_NAME);
      IDE.CODEASSISTANT.waitForJavaToolingInitialized(FILE_NAME);
   }

   @Test
   public void testJavaCodeAssistant() throws Exception
   {
      IDE.GOTOLINE.goToLine(24);
      // IDE.EDITOR.typeTextIntoEditor(0, "a");
      IDE.CODEASSISTANT.openForm();
      String[] proposalsText = IDE.CODEASSISTANT.getFormProposalsText();
      assertThat(proposalsText).isNotNull().isNotEmpty();
      assertThat(proposalsText).contains(shuldContainsVars);
      IDE.CODEASSISTANT.closeForm();
      IDE.EDITOR.typeTextIntoEditor(0, "List");
      IDE.CODEASSISTANT.openForm();
      IDE.CODEASSISTANT.selectProposal("List", "java.util");
      assertThat(IDE.EDITOR.getTextFromCodeEditor(0)).contains("List<E>").contains("import java.util.List;");

      IDE.EDITOR.moveCursorLeft(0, 1);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.BACK_SPACE.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "String");
      IDE.EDITOR.moveCursorRight(0, 1);
      IDE.EDITOR.typeTextIntoEditor(0, " ");
      // sleep to re-parse file
      Thread.sleep(TestConstants.SLEEP);

      IDE.CODEASSISTANT.openForm();
      proposalsText = IDE.CODEASSISTANT.getFormProposalsText();
      assertThat(proposalsText).contains("strings : List<java.lang.String>", "list : List<java.lang.String>");
      IDE.CODEASSISTANT.moveCursorDown(1);
      IDE.CODEASSISTANT.insertSelectedItem();

      assertThat(IDE.EDITOR.getTextFromCodeEditor(0)).contains("List<String> strings");

      IDE.EDITOR.typeTextIntoEditor(0, " = new ArrayLis");

      IDE.CODEASSISTANT.openForm();
      proposalsText = IDE.CODEASSISTANT.getFormProposalsText();
      assertThat(proposalsText).containsOnly("ArrayList(int arg0) - java.util.ArrayList",
         "ArrayList() - java.util.ArrayList", "ArrayList(Collection arg0) - java.util.ArrayList");
      IDE.CODEASSISTANT.moveCursorDown(1);
      IDE.CODEASSISTANT.insertSelectedItem();

      assertThat(IDE.EDITOR.getTextFromCodeEditor(0)).contains("List<String> strings = new ArrayList").contains(
         "import java.util.ArrayList;");
      IDE.EDITOR.moveCursorLeft(0, 3);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.BACK_SPACE.toString());
      IDE.EDITOR.typeTextIntoEditor(0, "String");
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString());
      IDE.EDITOR.typeTextIntoEditor(0, ";\n");

      // sleep to re-parse file
      Thread.sleep(TestConstants.SLEEP);
      IDE.EDITOR.typeTextIntoEditor(0, "str");
      IDE.CODEASSISTANT.openForm();
      proposalsText = IDE.CODEASSISTANT.getFormProposalsText();
      assertThat(proposalsText).containsOnly("strings : List<java.lang.String>");
      IDE.CODEASSISTANT.insertSelectedItem();
      IDE.EDITOR.typeTextIntoEditor(0, ".");
      IDE.CODEASSISTANT.openForm();
      proposalsText = IDE.CODEASSISTANT.getFormProposalsText();
      assertThat(proposalsText).contains(shuldContainsMethods);
      IDE.CODEASSISTANT.typeToInput("clea");
      IDE.CODEASSISTANT.insertSelectedItem();
      IDE.EDITOR.typeTextIntoEditor(0, ";\n");
      assertThat(IDE.EDITOR.getTextFromCodeEditor(0)).contains("strings.clear();");
      IDE.EDITOR.typeTextIntoEditor(0, "\"test\".");

      IDE.CODEASSISTANT.openForm();
      proposalsText = IDE.CODEASSISTANT.getFormProposalsText();
      assertThat(proposalsText).contains("trim() : String - String", "intern() : String - String",
         "endsWith(String arg0) : boolean - String");
      IDE.CODEASSISTANT.typeToInput("toCha");
      IDE.CODEASSISTANT.insertSelectedItem();
      assertThat(IDE.EDITOR.getTextFromCodeEditor(0)).contains("\"test\".toCharArray()");
      IDE.EDITOR.typeTextIntoEditor(0, ";");
      
      IDE.GOTOLINE.goToLine(32);
      IDE.EDITOR.typeTextIntoEditor(0, Keys.END.toString() +"\n");
      // sleep to re-parse file
      Thread.sleep(TestConstants.SLEEP);
      IDE.CODEASSISTANT.openForm();
      proposalsText = IDE.CODEASSISTANT.getFormProposalsText();
      assertThat(proposalsText).contains(shuldContainsOverride);
      IDE.CODEASSISTANT.insertSelectedItem();
      assertThat(IDE.EDITOR.getTextFromCodeEditor(0)).contains("public String toString() {").contains("return super.toString();");
      
      
   }
}
