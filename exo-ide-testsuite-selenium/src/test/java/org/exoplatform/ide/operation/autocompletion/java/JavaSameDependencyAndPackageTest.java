package org.exoplatform.ide.operation.autocompletion.java;

import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.exoplatform.ide.VirtualFileSystemUtils;
import org.exoplatform.ide.operation.autocompletion.CodeAssistantBaseTest;
import org.exoplatform.ide.vfs.shared.Link;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class JavaSameDependencyAndPackageTest extends CodeAssistantBaseTest
{
   private static final String PROJECT = JavaSameDependencyAndPackageTest.class.getSimpleName();

   @BeforeClass
   public static void setUp()
   {
      final String filePath =
         "src/test/resources/org/exoplatform/ide/operation/file/autocomplete/SamePackageAndDependency.zip";

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
      IDE.JAVAEDITOR.typeTextIntoJavaEditor("import ju");
      IDE.CODE_ASSISTANT_JAVA.openForm();
      IDE.CODE_ASSISTANT_JAVA.waitFromImportContent("junit.*;");
      IDE.CODE_ASSISTANT_JAVA.insertSelectedItem();
      assertTrue(IDE.JAVAEDITOR.getTextFromJavaEditor().contains("import junit.*;"));
   }

   private void openTestClass() throws InterruptedException, Exception
   {
      IDE.PROJECT.EXPLORER.waitOpened();
      IDE.PROJECT.OPEN.openProject(PROJECT);
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
      IDE.PROJECT.PACKAGE_EXPLORER.waitAndClosePackageExplorer();
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT);

      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "junit");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "junit");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "junit" + "/"
         + "GreetingController.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "junit" + "/"
         + "GreetingController.java");

      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

}
