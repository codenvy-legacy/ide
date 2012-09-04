package org.exoplatform.ide.operation.java;

import org.exoplatform.ide.BaseTest;

public class ServicesJavaTextFuctionTest extends BaseTest
{

   public void waitEditorIsReady(String PROJECT) throws Exception
   {
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SimpleSum.java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

   public void waitFormatTestIsReady(String PROJECT) throws Exception
   {
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SumController.java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }

   
   public void waitJavaCommentTestIsReady(String PROJECT) throws Exception
   {
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaCommentsTest.java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }
   
   
   public void waitJavaRemoveCommentTestIsReady(String PROJECT) throws Exception
   {
      IDE.EDITOR.waitActiveFile(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaRemoveCommentsTest.java");
      IDE.PROGRESS_BAR.waitProgressBarControlClose();
   }
   
   
   public void openSpringJavaTetsFile(String PROJECT) throws Exception
   {
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SumController.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SumController.java");
      waitFormatTestIsReady(PROJECT);
   }

   public void openJavaClassForFormat(String PROJECT) throws Exception
   {
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SimpleSum.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "sumcontroller" + "/"
         + "SimpleSum.java");
      waitEditorIsReady(PROJECT);
   }

   public void openJavaCommenTest(String PROJECT) throws Exception
   {
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaCommentsTest.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaCommentsTest.java");
      waitJavaCommentTestIsReady(PROJECT);
   }

   public void openJavaRemoveCommenTest (String PROJECT) throws Exception
   {
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest");
      IDE.PROJECT.EXPLORER.expandItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest");
      IDE.PROJECT.EXPLORER.waitForItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaRemoveCommentsTest.java");
      IDE.PROJECT.EXPLORER.openItem(PROJECT + "/" + "src" + "/" + "main" + "/" + "java/" + "commenttest" + "/"
         + "JavaRemoveCommentsTest.java");
      waitJavaRemoveCommentTestIsReady(PROJECT);   
   }
   
   
   
}
