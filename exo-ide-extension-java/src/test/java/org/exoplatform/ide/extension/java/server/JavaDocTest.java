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
package org.exoplatform.ide.extension.java.server;

import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 30, 2011 5:17:52 PM evgen $
 *
 */
public class JavaDocTest extends JavaDocBase
{

   @Test
   public void classJavaDocTest() throws CodeAssistantException, VirtualFileSystemException
   {
      String javaDoc = javaCa.getClassJavaDocFromProject("org.exoplatform.ide.client.IDE", project.getId(), VFS_ID);
      Assert.assertTrue(javaDoc.contains("Created by The eXo Platform SAS"));
      Assert.assertTrue(javaDoc.contains("<a href=\"mailto:dmitry.ndp@exoplatform.com.ua\">Dmytro Nochevnov</a>"));
   }

   @Test
   @Ignore("not ready yet with new API")
   public void methodJavaDocTest() throws CodeAssistantException, VirtualFileSystemException
   {
      String javaDoc =
         javaCa.getMemberJavaDocFromProject(
            "org.exoplatform.ide.client.autocompletion.AutoCompletionManager.onAutocompleteTokenSelected(Token)",
            project.getId(), VFS_ID);
      Assert.assertTrue(javaDoc
         .contains("org.exoplatform.gwtframework.ui.client.component.autocomlete.AutocompleteTokenSelectedHandler"));
   }

   @Test(expected = CodeAssistantException.class)
   public void methodWithNoJavaDocTest() throws CodeAssistantException, VirtualFileSystemException
   {
      String javaDoc =
         javaCa
            .getMemberJavaDocFromProject(
               "org.exoplatform.ide.client.autocompletion.AutoCompletionManager.onEditorAutoCompleteCalled(EditorAutoCompleteCalledEvent)",
               project.getId(), VFS_ID);
      Assert.assertFalse(javaDoc.contains("null"));
   }
}
