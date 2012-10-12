/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.core.editor;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.ide.editor.DocumentProvider.DocumentCallback;
import org.exoplatform.ide.editor.EditorInput;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.resources.model.Project;
import org.exoplatform.ide.text.Document;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class ResourceDocumentProviderTest
{

   @Mock
   private DocumentCallback callback;

   @Mock
   private EditorInput input;

   @Mock
   private File file;

   @Mock
   private Project project;

   @Before
   public void setUp()
   {
      when(input.getFile()).thenReturn(file);
      when(file.getProject()).thenReturn(project);
      when(file.getContent()).thenReturn("test");
   }

   @Test
   public void shuldCallProjectGetContent()
   {
      ResourceDocumentProvider provider = new ResourceDocumentProvider();
      provider.getDocument(input, callback);
      verify(project).getContent(eq(file), Mockito.<AsyncCallback<File>> any());
   }

   @SuppressWarnings("unchecked")
   @Test
   public void shuldCallCallback()
   {
      ResourceDocumentProvider provider = new ResourceDocumentProvider();
      doAnswer(createServerResponse()).when(project).getContent((File)any(), (AsyncCallback<File>)any());
      provider.getDocument(input, callback);
      verify(callback).onDocument((Document)any());
   }

   /**
    * @return
    */
   @SuppressWarnings("unchecked")
   private Answer<?> createServerResponse()
   {
      Answer<?> responseEmulator = new Answer<Object>()
      {

         @Override
         public Object answer(InvocationOnMock invocation) throws Throwable
         {
            AsyncCallback<File> callback =  (AsyncCallback<File>)invocation.getArguments()[1];
            callback.onSuccess(file);
            return null;
         }
      };
      return responseEmulator;
   }
}
