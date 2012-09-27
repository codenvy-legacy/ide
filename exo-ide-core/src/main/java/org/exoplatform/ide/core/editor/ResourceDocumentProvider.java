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

import com.google.gwt.user.client.rpc.AsyncCallback;

import org.exoplatform.ide.editor.DocumentProvider;
import org.exoplatform.ide.editor.EditorInput;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.text.Document;
import org.exoplatform.ide.text.DocumentImpl;
import org.exoplatform.ide.util.loging.Log;

/**
 * Document provider implementation on Resource API
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class ResourceDocumentProvider implements DocumentProvider
{

   /**
    * @see org.exoplatform.ide.editor.DocumentProvider#getDocument(org.exoplatform.ide.editor.EditorInput)
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
      callback.onDocument(new DocumentImpl(content));
   }

   /**
    * @see org.exoplatform.ide.editor.DocumentProvider#saveDocument(org.exoplatform.ide.editor.EditorInput, org.exoplatform.ide.text.Document, boolean)
    */
   @Override
   public void saveDocument(EditorInput input, Document document, boolean overwrite)
   {
      File file = input.getFile();
      file.getProject().updateContent(file, new AsyncCallback<File>()
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

   /**
    * @see org.exoplatform.ide.editor.DocumentProvider#saveDocumentAs(org.exoplatform.ide.editor.EditorInput, org.exoplatform.ide.text.Document, boolean)
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
