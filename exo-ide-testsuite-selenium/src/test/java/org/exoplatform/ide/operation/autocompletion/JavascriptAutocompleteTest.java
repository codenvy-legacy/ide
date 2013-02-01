package org.exoplatform.ide.operation.autocompletion;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Map;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.java.FormatJavaCodeWithShortKeysTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JavascriptAutocompleteTest extends BaseTest
{
   private static final String PROJECT = FormatJavaCodeWithShortKeysTest.class.getSimpleName();

   private static Object[] stringJavascriptMethods = new String[]{"charCodeAt(index) : Number",
      "lastIndexOf(searchString) : Number", "length() : Number", "match(regexp) : Boolean",
      "replace(searchValue, replaceValue) : String", "slice(start, end) : String", "substring(start, end) : String",
      "toLocaleString() : String", "toLocaleUpperCase() : String", "toLowerCase() : String", "toString() : String",
      "toUpperCase() : String", "trim() : String"};

   private static Object[] stringDateMethods = new String[]{"getDay() : Number", "getFullYear() : Number",
      "getHours() : Number", "getMinutes() : Number", "hasOwnProperty(property) : boolean",
      "isPrototypeOf(object) : boolean", "propertyIsEnumerable(property) : boolean", "prototype : Object",
      "setDay(dayOfWeek) : Number", "setFullYear(year) : Number", "setHours(hour) : Number",
      "setMinutes(minute) : Number", "setTime(millis) : Number", "toLocaleString() : String", "toString() : String",
      "valueOf() : Object"};

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/project/JavaScriptAutoComplete.zip";

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
   public void mainStringMethods() throws Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "js");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "js");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "js" + "/" + "script.js");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "js" + "/" + "script.js");
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.GOTOLINE.goToLine(10);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("a.");
      IDE.JAVAEDITOR.waitJavaDocContainer();
      assertThat(IDE.JAVAEDITOR.getTextFromJavaDocContainer().split("\n")).contains(stringJavascriptMethods);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("\n");
      // IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, Keys.CONTROL.toString() +
      // "d");
      // for reparse updated code
      Thread.sleep(500);
   }

   @Test
   public void mainDateMethods() throws Exception
   {
      IDE.GOTOLINE.goToLine(21);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("date.");
      IDE.JAVAEDITOR.waitJavaDocContainer();
      assertThat(IDE.JAVAEDITOR.getTextFromJavaDocContainer().split("\n")).contains(stringDateMethods);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("\n");
   }

   @Test
   public void mainNumberMethods() throws Exception
   {
      IDE.GOTOLINE.goToLine(31);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("var numb =10000;\n");
      // for reparse updated code
      Thread.sleep(500);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("numb.");
      IDE.JAVAEDITOR.waitJavaDocContainer();

      assertThat(IDE.JAVAEDITOR.getTextFromJavaDocContainer()).contains("toFixed(digits) : Number")
         .contains("toPrecision(digits) : Number").contains("toExponential(digits) : Number")
         .contains("valueOf() : Object");
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("\n");
   }

   @Test
   public void jsonMethods() throws Exception
   {
      IDE.GOTOLINE.goToLine(36);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("JSON.");
      IDE.JAVAEDITOR.waitJavaDocContainer();
      assertThat(IDE.JAVAEDITOR.getTextFromJavaDocContainer()).contains("parse(str) : Object")
         .contains("valueOf() : Object").contains("stringify(obj) : String");
      IDE.EDITOR.closeTabIgnoringChanges("script.js");
   }

}
