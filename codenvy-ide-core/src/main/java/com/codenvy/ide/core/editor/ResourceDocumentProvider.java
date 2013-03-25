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
package com.codenvy.ide.core.editor;

import com.codenvy.ide.editor.DocumentProvider;
import com.codenvy.ide.editor.EditorInput;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentFactory;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;


/**
 * Document provider implementation on Resource API
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class ResourceDocumentProvider implements DocumentProvider
{

   private DocumentFactory documentFactory;

   @Inject
   public ResourceDocumentProvider(DocumentFactory documentFactory)
   {
      this.documentFactory = documentFactory;
   }

   /**
    * {@inheritDoc}
    * This implementation return null
    */
   @Override
   public AnnotationModel getAnnotationModel(EditorInput input)
   {
      return null;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void getDocument(EditorInput input, final DocumentCallback callback)
   {
      File file = input.getFile();

      file.getProject().getContent(file, new AsyncCallback<File>()
      {

         @Override
         public void onSuccess(File result)
         {
            contentReceived(result.getContent(), callback);
         }

         @Override
         public void onFailure(Throwable caught)
         {
            Log.error(ResourceDocumentProvider.class, caught);
         }
      });

   }

   /**
    * @param content
    * @param callback
    */
   private void contentReceived(String content, DocumentCallback callback)
   {
      callback.onDocument(documentFactory.get(content));
   }


   /**
    * {@inheritDoc}
    */
   @Override
   public void saveDocument(final EditorInput input, Document document, boolean overwrite, final AsyncCallback<EditorInput> callback)
   {
      File file = input.getFile();
      file.setContent(document.get());
      file.getProject().updateContent(file, new AsyncCallback<File>()
      {

         @Override
         public void onSuccess(File result)
         {
            callback.onSuccess(input);
         }

         @Override
         public void onFailure(Throwable caught)
         {
            callback.onFailure(caught);
         }
      });
   }

   /**
    * @see com.codenvy.ide.editor.DocumentProvider#saveDocumentAs(com.codenvy.ide.editor.EditorInput, com.codenvy.ide.text.Document, boolean)
    */
   @Override
   public void saveDocumentAs(EditorInput input, Document document, boolean overwrite)
   {
      File file = input.getFile();
      file.getProject().createFile(file.getParent(), file.getName(), file.getContent(), file.getMimeType(),
         new AsyncCallback<File>()
         {

            @Override
            public void onSuccess(File result)
            {
               //TODO
            }

            @Override
            public void onFailure(Throwable caught)
            {
               Log.error(ResourceDocumentProvider.class, caught);
            }
         });
   }
}
