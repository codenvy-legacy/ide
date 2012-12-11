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
package org.exoplatform.ide.client.operation.collaboration;

import com.google.collide.client.CollabEditor;
import com.google.collide.client.CollabEditorExtension;
import com.google.collide.client.document.DocumentManager;
import com.google.collide.client.util.PathUtil;
import com.google.collide.dto.FileContents;
import com.google.collide.shared.document.Document;
import com.google.gwt.event.shared.HandlerManager;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.editor.EditorView;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Item;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: 
 */
public class OpenFileCollaborationHandler implements OpenFileCollaborationEventHandler, ItemsSelectedHandler
{

   private File selectedFile;

   public OpenFileHandler(HandlerManager eventBus)
   {
      eventBus.addHandler(OpenFileCollaborationEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   @Override
   public void onOpenFileCollaboration(OpenFileCollaborationEvent event)
   {
      if(selectedFile == null)
      {
         //TODO
         return;
      }

      final CollabEditor editor = new CollabEditor(selectedFile.getMimeType());
      CollabEditorExtension.get().getManager().getDocument(PathUtil.EMPTY_PATH, new DocumentManager.GetDocumentCallback()
      {
         @Override
         public void onDocumentReceived(Document document)
         {
            EditorView view = new EditorView((FileModel)selectedFile,false, new Editor[]{editor},0);
            editor.setDocument(document);
            IDE.getInstance().openView(view);
         }

         @Override
         public void onUneditableFileContentsReceived(FileContents contents)
         {
            //TODO

         }

         @Override
         public void onFileNotFoundReceived()
         {
            //TODO

         }
      });


   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (!event.getSelectedItems().isEmpty())
      {
         Item item = event.getSelectedItems().get(0);
         if(item instanceof File)
         {
            selectedFile = (File)item;
            return;
         }
      }
      selectedFile = null;
   }
}
