package org.exoplatform.ide.operation.autocompletion.java;

import static org.junit.Assert.*;
import static org.junit.Assert.fail;
import static org.fest.assertions.Assertions.*;
import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.openqa.selenium.Keys;

import java.util.Map;

public class JavaDocTests extends BaseTest
{

   private static final String PROJECT = JavaDocTests.class.getSimpleName();

   private static final String FILE_NAME = "GreetingController.java";

   private static Object[] modelAndViewJavaDoc =
      new String[]{
         "org.springframework.web.servlet.ModelAndView",
         "Holder for both Model and View in the web MVC framework. Note that these are entirely distinct. This class merely holds both to make it possible for a controller to return both model and view in a single return value.",
         "Represents a model and view returned by a handler, to be resolved by a DispatcherServlet. The view can take the form of a String view name which will need to be resolved by a ViewResolver object; alternatively a View object can be specified directly. The model is a Map, allowing the use of multiple objects keyed by name. @author Rod Johnson @author Juergen Hoeller @author Rob Harrop @see DispatcherServlet @see ViewResolver @see HandlerAdapter#handle @see org.springframework.web.servlet.mvc.Controller#handleRequest"};

   private static Object[] stringJavaDoc =
      new String[]{
         "java.lang.String",
         "The String class represents character strings. All string literals in Java programs, such as \"abc\", are implemented as instances of this class.",
         "Strings are constant; their values cannot be changed after they are created. String buffers support mutable strings. Because String objects are immutable they can be shared. For example:",
         "    String str = \"abc\";",
         "is equivalent to:",
         "    char data[] = {'a', 'b', 'c'};",
         "    String str = new String(data);",
         "Here are some more examples of how strings can be used:",
         "    System.out.println(\"abc\");",
         "    String cde = \"cde\";",
         "    String c = \"abc\".substring(2,3);",
         "    String d = cde.substring(1, 2);",

         "The class String includes methods for examining individual characters of the sequence, for comparing strings, for searching strings, for extracting substrings, and for creating a copy of a string with all characters translated to uppercase or to lowercase. Case mapping is based on the Unicode Standard version specified by the {@link java.lang.Character Character} class.",

         "The Java language provides special support for the string concatenation operator ( + ), and for conversion of other objects to strings. String concatenation is implemented through the StringBuilder(or StringBuffer) class and its append method. String conversions are implemented through the method toString, defined by Object and inherited by all classes in Java. For additional information on string concatenation and conversion, see Gosling, Joy, and Steele, The Java Language Specification.",
         "Unless otherwise noted, passing a null argument to a constructor or method in this class will cause a {@link NullPointerException} to be thrown.",
         "A String represents a string in the UTF-16 format in which supplementary characters are represented by surrogate pairs (see the section Unicode Character Representations in the Character class for more information). Index values refer to char code units, so a supplementary character uses two positions in a String.",
         "The String class provides methods for dealing with Unicode code points (i.e., characters), in addition to those for dealing with Unicode code units (i.e., char values). @author Lee Boynton @author Arthur van Hoff @version %I%, %G% @see java.lang.Object#toString() @see java.lang.StringBuffer @see java.lang.StringBuilder @see java.nio.charset.Charset @since JDK1.0"};

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/JavaTestProject.zip";

      try
      {
         Map<String, Link> project = VirtualFileSystemUtils.importZipProject(PROJECT, filePath);
      }
      catch (Exception e)
      {
      }
   }

   @AfterClass
   public static void tearDown()
   {
      try
      {
         VirtualFileSystemUtils.delete(WS_URL + PROJECT);
      }
      catch (Exception e)
      {
      }
   }

   @Test
   public void generateJavaDocTest() throws Exception
   {
      openTestClass();
      IDE.GOTOLINE.goToLine(11);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, "/**");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, "\n");
      //for reparse insertion in staging
      Thread.sleep(500);
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("/**\n"));
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains(" * \n"));
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains(" */"));
      IDE.EDITOR.forcedClosureFile(1);
   }

   @Test
   public void checkJavadocInfo() throws Exception
   {
      reOpenTestClass();
      IDE.GOTOLINE.goToLine(16);
      IDE.JAVAEDITOR.moveCursorRight(1, 12);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(1, Keys.CONTROL.toString() + "q");
      IDE.JAVAEDITOR.waitJavaDocContainer();
      assertThat(IDE.JAVAEDITOR.getTextFronJavaDocContainer().split("\n")).contains(modelAndViewJavaDoc);
      IDE.JAVAEDITOR.setCursorToJavaEditor(1);
      IDE.GOTOLINE.goToLine(18);
      IDE.JAVAEDITOR.moveCursorRight(1, 9);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(1, Keys.CONTROL.toString() + "q");
      IDE.JAVAEDITOR.waitJavaDocContainer();
      assertThat(IDE.JAVAEDITOR.getTextFronJavaDocContainer().split("\n")).contains(stringJavaDoc);
   }

   private void openTestClass() throws InterruptedException, Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld" + "/"
         + "GreetingController.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld" + "/"
         + "GreetingController.java");

      IDE.EDITOR.waitActiveFile(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld" + "/"
         + "GreetingController.java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

   private void reOpenTestClass() throws InterruptedException, Exception
   {
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld" + "/"
         + "GreetingController.java");

      IDE.EDITOR.waitActiveFile(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "helloworld" + "/"
         + "GreetingController.java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();

   }

}
