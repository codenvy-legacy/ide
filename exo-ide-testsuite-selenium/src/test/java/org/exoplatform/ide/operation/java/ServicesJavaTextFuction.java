package org.exoplatform.ide.operation.java;

import org.exoplatform.ide.BaseTest;

public class ServicesJavaTextFuction extends BaseTest
{

   public void waitEditorIsReady() throws Exception
   {
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

   public void waitFormatTestIsReady(String PROJECT) throws Exception
   {
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

   public void waitJavaCommentTestIsReady(String PROJECT) throws Exception
   {
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

   public void waitJavaRemoveCommentTestIsReady(String PROJECT) throws Exception
   {
      IDE.JAVAEDITOR.waitJavaEditorIsActive();
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

   public void openSpringJavaTetsFile(String PROJECT) throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SumController.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SumController.java");
      waitFormatTestIsReady(PROJECT);
   }

   public void openJavaClassForFormat(String PROJECT) throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SimpleSum.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SimpleSum.java");
      waitEditorIsReady();
   }

   
   public void openJavaClassInPackageExplorerFormat(String PROJECT) throws Exception
   {
      IDE.PROJECT.PACKAGE_EXPLORER.waitForItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SimpleSum.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SimpleSum.java");
      waitEditorIsReady();
   }
   
   
   public void openJavaClassForFormatInAlreadyOpenedProgect(String PROJECT) throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SimpleSum.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SimpleSum.java");
      waitEditorIsReady();
   }

   public void openJavaCommenTest(String PROJECT) throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaCommentsTest.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaCommentsTest.java");
      waitJavaCommentTestIsReady(PROJECT);
   }

   public void openJavaRemoveCommenTest(String PROJECT) throws Exception
   {
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaRemoveCommentsTest.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaRemoveCommentsTest.java");
      waitJavaRemoveCommentTestIsReady(PROJECT);
   }
}
