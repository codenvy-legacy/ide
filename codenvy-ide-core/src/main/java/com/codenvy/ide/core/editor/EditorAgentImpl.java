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

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.core.event.ActivePartChangedEvent;
import com.codenvy.ide.core.event.ActivePartChangedHandler;
import com.codenvy.ide.editor.EditorInitException;
import com.codenvy.ide.editor.EditorInput;
import com.codenvy.ide.editor.EditorPartPresenter;
import com.codenvy.ide.editor.EditorProvider;
import com.codenvy.ide.editor.EditorPartPresenter.EditorPartCloseHandler;
import com.codenvy.ide.perspective.PerspectivePresenter.PartStackType;
import com.codenvy.ide.resources.FileEvent;
import com.codenvy.ide.resources.FileEventHandler;
import com.codenvy.ide.resources.FileType;
import com.codenvy.ide.resources.FileEvent.FileOperation;
import com.codenvy.ide.resources.model.File;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.util.loging.Log;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
@Singleton
public class EditorAgentImpl implements EditorAgent
{

   private final JsonStringMap<EditorPartPresenter> openedEditors;

   /**
    * Used to notify {@link EditorAgentImpl} that editor has closed
    */
   private final EditorPartCloseHandler editorClosed = new EditorPartCloseHandler()
   {
      @Override
      public void onClose(EditorPartPresenter editor)
      {
         editorClosed(editor);
      }
   };

   private final ActivePartChangedHandler activePartChangedHandler = new ActivePartChangedHandler()
   {
      @Override
      public void onActivePartChanged(ActivePartChangedEvent event)
      {
         if (event.getActivePart() instanceof EditorPartPresenter)
         {
            activeEditor = (EditorPartPresenter)event.getActivePart();
         }
      }
   };

   private final FileEventHandler fileEventHandler = new FileEventHandler()
   {
      @Override
      public void onFileOperation(final FileEvent event)
      {
         if (event.getOperationType() == FileOperation.OPEN)
         {
            openEditor(event.getFile());
         }
         else if (event.getOperationType() == FileOperation.CLOSE)
         {
            // close associated editor. OR it can be closed itself TODO
         }
      }
   };

   /**
    *
    */
   final class EditorInputImpl implements EditorInput
   {
      /**
       *
       */
      private final File file;

      /**
       * @param file
       */
      private EditorInputImpl(File file)
      {
         this.file = file;
      }

      @Override
      public String getToolTipText()
      {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public String getName()
      {
         return file.getName();
      }

      @Override
      public ImageResource getImageResource()
      {
         // TODO Auto-generated method stub
         return null;
      }

      @Override
      public File getFile()
      {
         return file;
      }
   }

   private EditorRegistry editorRegistry;

   private ResourceProvider provider;

   private final WorkspaceAgent workspace;

   private EditorPartPresenter activeEditor;

   private final EventBus eventBus;

   @Inject
   public EditorAgentImpl(EventBus eventBus, EditorRegistry editorRegistry, ResourceProvider provider,
      final WorkspaceAgent workspace)
   {
      super();
      this.eventBus = eventBus;
      this.editorRegistry = editorRegistry;
      this.provider = provider;
      this.workspace = workspace;
      openedEditors = JsonCollections.createStringMap();

      bind();
   }

   protected void bind()
   {
      eventBus.addHandler(ActivePartChangedEvent.TYPE, activePartChangedHandler);
      eventBus.addHandler(FileEvent.TYPE, fileEventHandler);
   }

    /**
    * {@inheritDoc}
    */
   @Override
   public void openEditor(final File file)
   {
      if (openedEditors.containsKey(file.getId()))
      {
         workspace.setActivePart(openedEditors.get(file.getId()));
      }
      else
      {
         FileType fileType = provider.getFileType(file);
         EditorProvider editorProvider = editorRegistry.getDefaultEditor(fileType);
         EditorPartPresenter editor = editorProvider.getEditor();
         try
         {
            editor.init(new EditorInputImpl(file));
            editor.addCloseHandler(editorClosed);
         }
         catch (EditorInitException e)
         {
            Log.error(getClass(), e);
         }
         workspace.showPart(editor, PartStackType.EDITING);
         openedEditors.put(file.getId(), editor);
      }
   }

   /**
    * @param editor
    */
   protected void editorClosed(EditorPartPresenter editor)
   {
      if (activeEditor == editor)
      {
         activeEditor = null;
      }
      JsonArray<String> keys = openedEditors.getKeys();
      for (int i = 0; i < keys.size(); i++)
      {
         String fileId = keys.get(i);
         // same instance
         if (openedEditors.get(fileId) == editor)
         {
            openedEditors.remove(fileId);
            return;
         }
      }

   }

    /**
    * {@inheritDoc}
    */
   @Override
   public JsonStringMap<EditorPartPresenter> getOpenedEditors()
   {
      return openedEditors;
   }

    /**
    * {@inheritDoc}
    */
   @Override
   public EditorPartPresenter getActiveEditor()
   {
      return activeEditor;
   }
}
