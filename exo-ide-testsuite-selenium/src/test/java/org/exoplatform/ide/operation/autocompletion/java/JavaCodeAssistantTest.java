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

import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.fail;

import org.exoplatform.ide.MenuCommands;
import org.exoplatform.ide.TestConstants;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.Keys;

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
      openJavaProject();
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
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.CODE_ASSISTANT_JAVA.waitForJavaToolingInitialized(FILE_NAME);
   }

   @Test
   public void testJavaCodeAssistant() throws Exception
   {

      // step 1 got line 24, open form and check data in coeassist
      // IDE.GOTOLINE.goToLine(24);
      // -------on this moment go to line does not work in java editor
      // after fix this block can be uncomment

      // Go to string with num 24
      for (int i = 0; i < 23; i++)
      {
         IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.ARROW_DOWN.toString());
      }
      IDE.CODE_ASSISTANT_JAVA.openForm();
      String[] proposalsText = IDE.CODE_ASSISTANT_JAVA.getFormProposalsText();
      assertThat(proposalsText).isNotNull().isNotEmpty();
      assertThat(proposalsText).contains(shuldContainsVars);
      IDE.CODE_ASSISTANT_JAVA.closeForm();

      // step 2 type iList and check data in codeassist for List
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("List");

      // delay for reparse codeassistant
      Thread.sleep(3000);

      IDE.CODE_ASSISTANT_JAVA.openForm();
      // IDE.CODE_ASSISTANT_JAVA.moveCursorDown(2);
      // delay for user keypress emulation
      Thread.sleep(500);
      IDE.CODE_ASSISTANT_JAVA.waitFromImportContent("java.util");
      IDE.CODE_ASSISTANT_JAVA.selectImportProposal("java.util");
      IDE.CODE_ASSISTANT_JAVA.insertSelectedItem();

      // delay for user keypress emulation
      Thread.sleep(500);
      // IDE.CODE_ASSISTANT_JAVA.selectProposal("List", "java.util");
      assertThat(IDE.JAVAEDITOR.getTextFromJavaEditor()).contains("List<E>").contains("import java.util.List;");
      IDE.JAVAEDITOR.moveCursorLeft(1);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.BACK_SPACE.toString());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("String");
      IDE.JAVAEDITOR.moveCursorRight(1);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(" ");

      // sleep to re-parse file
      // step 3 check work cursor in codeassist and insert first value
      Thread.sleep(TestConstants.SLEEP);
      IDE.CODE_ASSISTANT_JAVA.openForm();
      proposalsText = IDE.CODE_ASSISTANT_JAVA.getFormProposalsText();
      assertThat(proposalsText).contains("strings : List<java.lang.String>", "list : List<java.lang.String>");
      IDE.CODE_ASSISTANT_JAVA.moveCursorDown(1);
      IDE.CODE_ASSISTANT_JAVA.insertSelectedItem();
      assertThat(IDE.JAVAEDITOR.getTextFromJavaEditor()).contains("List<String> strings");

      IDE.JAVAEDITOR.typeTextIntoJavaEditor(" = new ArrayLis");

      // step 3 open form, select first element and insert
      IDE.CODE_ASSISTANT_JAVA.openForm();
      proposalsText = IDE.CODE_ASSISTANT_JAVA.getFormProposalsText();
      assertThat(proposalsText).contains("ArrayList(int arg0) - java.util.ArrayList",
         "ArrayList() - java.util.ArrayList", "ArrayList(Collection arg0) - java.util.ArrayList");
      IDE.CODE_ASSISTANT_JAVA.moveCursorDown(1);
      IDE.CODE_ASSISTANT_JAVA.insertSelectedItem();
      assertThat(IDE.JAVAEDITOR.getTextFromJavaEditor()).contains("List<String> strings = new ArrayList").contains(
         "import java.util.ArrayList;");
      IDE.JAVAEDITOR.moveCursorLeft(3);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.BACK_SPACE.toString());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("String");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.END.toString());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(";\n");

      // sleep to re-parse file
      // step 4 type next java-values, open form and check
      Thread.sleep(TestConstants.SLEEP);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("str");
      IDE.CODE_ASSISTANT_JAVA.openForm();
      proposalsText = IDE.CODE_ASSISTANT_JAVA.getFormProposalsText();
      assertThat(proposalsText).containsOnly("strings : List<java.lang.String>");
      IDE.CODE_ASSISTANT_JAVA.selectProposalPanel();
      IDE.CODE_ASSISTANT_JAVA.insertSelectedItem();
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(".clea");
      IDE.CODE_ASSISTANT_JAVA.openForm();
      proposalsText = IDE.CODE_ASSISTANT_JAVA.getFormProposalsText();
      IDE.CODE_ASSISTANT_JAVA.selectProposalPanel();
      IDE.CODE_ASSISTANT_JAVA.insertSelectedItem();
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(";\n");
      assertThat(IDE.JAVAEDITOR.getTextFromJavaEditor()).contains("strings.clear();");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("\"test\".");
      IDE.CODE_ASSISTANT_JAVA.openForm();
      proposalsText = IDE.CODE_ASSISTANT_JAVA.getFormProposalsText();
      assertThat(proposalsText).contains("trim() : String - String", "intern() : String - String",
         "endsWith(String arg0) : boolean - String");
      IDE.CODE_ASSISTANT_JAVA.closeForm();
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("toCha");
      IDE.CODE_ASSISTANT_JAVA.openForm();
      IDE.CODE_ASSISTANT_JAVA.selectProposalPanel();
      IDE.CODE_ASSISTANT_JAVA.insertSelectedItem();
      assertThat(IDE.JAVAEDITOR.getTextFromJavaEditor()).contains("\"test\".toCharArray()");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(";");

      IDE.MENU.runCommand(MenuCommands.File.FILE, MenuCommands.File.SAVE);
      IDE.LOADER.waitClosed();
      IDE.JAVAEDITOR.waitNoContentModificationMark(FILE_NAME);
      IDE.EDITOR.closeFile(FILE_NAME);
      IDE.PROJECT.EXPLORER.openItem(projectName + "/src/main/java/helloworld/" + FILE_NAME);
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.CODE_ASSISTANT_JAVA.waitForJavaToolingInitialized(FILE_NAME);

      IDE.GOTOLINE.goToLine(33);

      IDE.CODE_ASSISTANT_JAVA.openForm();
      IDE.CODE_ASSISTANT_JAVA.selectProposalPanel();
      IDE.CODE_ASSISTANT_JAVA.moveCursorDown(1);
      Thread.sleep(TestConstants.SLEEP);
      IDE.CODE_ASSISTANT_JAVA.moveCursorUp(1);

      proposalsText = IDE.CODE_ASSISTANT_JAVA.getFormProposalsText();
      assertThat(proposalsText).contains(shuldContainsOverride);
      Thread.sleep(TestConstants.SLEEP);
      IDE.CODE_ASSISTANT_JAVA.doudleClickSelectedItem("toString() : String");
      // for close form
      Thread.sleep(TestConstants.SLEEP / 3);
      assertThat(IDE.JAVAEDITOR.getTextFromJavaEditor()).contains("public String toString()").contains(
         "return super.toString();");

      // step 5 checking work cursor in codeassistant after select class
      // -------on this moment go to line does not work in java editor
      // after fix this block with GOTOLINE method can be uncomment
      // IDE.GOTOLINE.goToLine(22);
      // on this moment we go to line 22 with cursor
      IDE.JAVAEDITOR.moveCursorUp(16);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.ARROW_UP.toString());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(Keys.END.toString());
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("\n");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("System.");
      Thread.sleep(TestConstants.SLEEP / 3);
      Thread.sleep(TestConstants.SLEEP);
      IDE.CODE_ASSISTANT_JAVA.openForm();
      IDE.CODE_ASSISTANT_JAVA.moveCursorDown(1);
      IDE.CODE_ASSISTANT_JAVA.moveCursorUp(1);
      IDE.CODE_ASSISTANT_JAVA.insertSelectedItem();

      assertThat(IDE.JAVAEDITOR.getTextFromJavaEditor().contains("System.class"));
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(".");
      Thread.sleep(TestConstants.SLEEP);
      IDE.CODE_ASSISTANT_JAVA.openForm();
      IDE.CODE_ASSISTANT_JAVA.moveCursorDown(1);
      IDE.CODE_ASSISTANT_JAVA.insertSelectedItem();
      assertThat(IDE.JAVAEDITOR.getTextFromJavaEditor().contains("System.class.newInstance()"));
   }
}
