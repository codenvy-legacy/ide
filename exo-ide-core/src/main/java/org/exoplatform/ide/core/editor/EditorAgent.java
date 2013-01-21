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

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.api.ui.workspace.WorkspaceAgent;
import org.exoplatform.ide.core.event.ActivePartChangedEvent;
import org.exoplatform.ide.core.event.ActivePartChangedHandler;
import org.exoplatform.ide.editor.EditorInitException;
import org.exoplatform.ide.editor.EditorInput;
import org.exoplatform.ide.editor.EditorPartPresenter;
import org.exoplatform.ide.editor.EditorPartPresenter.EditorPartCloseHandler;
import org.exoplatform.ide.editor.EditorProvider;
import org.exoplatform.ide.json.JsonArray;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.perspective.PerspectivePresenter.PartStackType;
import org.exoplatform.ide.resources.FileEvent;
import org.exoplatform.ide.resources.FileEvent.FileOperation;
import org.exoplatform.ide.resources.FileEventHandler;
import org.exoplatform.ide.resources.FileType;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.util.loging.Log;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
@Singleton
public class EditorAgent
{

   private final JsonStringMap<EditorPartPresenter> openedEditors;

   /**
    * Used to notify {@link EditorAgent} that editor has closed
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
   public EditorAgent(EventBus eventBus, EditorRegistry editorRegistry, ResourceProvider provider,
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
    *Get all opened editors
    * @return map with all opened editors
    */
   public JsonStringMap<EditorPartPresenter> getOpenedEditors()
   {
      return openedEditors;
   }

   /**
    * Current active editor
    * @return the current active editor
    */
   public EditorPartPresenter getActiveEditor()
   {
      return activeEditor;
   }
}
