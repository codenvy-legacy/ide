///*
// * CODENVY CONFIDENTIAL
// * __________________
// *
// * [2012] - [2013] Codenvy, S.A.
// * All Rights Reserved.
// *
// * NOTICE:  All information contained herein is, and remains
// * the property of Codenvy S.A. and its suppliers,
// * if any.  The intellectual and technical concepts contained
// * herein are proprietary to Codenvy S.A.
// * and its suppliers and may be covered by U.S. and Foreign Patents,
// * patents in process, and are protected by trade secret or copyright law.
// * Dissemination of this information or reproduction of this material
// * is strictly forbidden unless prior written permission is obtained
// * from Codenvy S.A..
// */
//package org.exoplatform.ide.extension.java.server;
//
//import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
//import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
//import org.junit.Assert;
//import org.junit.Ignore;
//import org.junit.Test;
//
///**
// * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
// * @version ${Id}: Nov 30, 2011 5:17:52 PM evgen $
// * 
// */
//public class JavaDocTest extends JavaDocBase
//{
//
//   @Test
//   public void classJavaDocTest() throws CodeAssistantException, VirtualFileSystemException
//   {
//      String javaDoc = javaCa.getClassJavaDocFromProject("org.exoplatform.ide.client.IDE", project.getId(), VFS_ID);
//      Assert.assertTrue(javaDoc.contains("Created by The eXo Platform SAS"));
//      Assert.assertTrue(javaDoc.contains("<a href=\"mailto:dmitry.ndp@exoplatform.com.ua\">Dmytro Nochevnov</a>"));
//   }
//
//   @Test
//   @Ignore("not ready yet with new API")
//   public void methodJavaDocTest() throws CodeAssistantException, VirtualFileSystemException
//   {
//      String javaDoc =
//         javaCa.getMemberJavaDocFromProject(
//            "org.exoplatform.ide.client.autocompletion.AutoCompletionManager.onAutocompleteTokenSelected(Token)",
//            project.getId(), VFS_ID);
//      Assert.assertTrue(javaDoc
//         .contains("org.exoplatform.gwtframework.ui.client.component.autocomlete.AutocompleteTokenSelectedHandler"));
//   }
//
//   @Test(expected = CodeAssistantException.class)
//   @Ignore
//   public void methodWithNoJavaDocTest() throws CodeAssistantException, VirtualFileSystemException
//   {
//      String javaDoc =
//         javaCa
//            .getMemberJavaDocFromProject(
//               "org.exoplatform.ide.client.autocompletion.AutoCompletionManager.onEditorAutoCompleteCalled
// (EditorAutoCompleteCalledEvent)",
//               project.getId(), VFS_ID);
//      Assert.assertFalse(javaDoc.contains("null"));
//   }
//}
