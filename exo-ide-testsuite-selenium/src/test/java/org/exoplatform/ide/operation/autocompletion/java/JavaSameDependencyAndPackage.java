package org.exoplatform.ide.operation.autocompletion.java;

import static org.junit.Assert.*;
import static org.junit.Assert.assertTrue;

import org.exoplatform.ide.BaseTest;
import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.core.AbstractTestModule;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.fest.assertions.AssertExtension;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Map;

import javax.validation.constraints.AssertTrue;

public class JavaSameDependencyAndPackage extends CodeAssistantBaseTest
{
   private static final String PROJECT =  JavaSameDependencyAndPackage.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      final String filePath = "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/SamePackageAndDependency.zip";

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
      IDE.GOTOLINE.goToLine(7);
      IDE.JAVAEDITOR.typeTextIntoJavaEditor(0, "import ju");
      IDE.CODE_ASSISTANT_JAVA.openForm();
      IDE.CODE_ASSISTANT_JAVA.waitFromImportContent("junit.*;");
      IDE.CODE_ASSISTANT_JAVA.insertSelectedItem();
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor(0).contains("import junit.*;"));
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
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "junit");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "junit");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "junit" + "/"
         + "GreetingController.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "junit" + "/"
         + "GreetingController.java");

      IDE.EDITOR.waitActiveFile(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "junit" + "/"
         + "GreetingController.java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }
   
   
}
