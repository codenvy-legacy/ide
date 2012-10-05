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
package org.exoplatform.ide.client.editor;

import com.google.gwt.resources.client.ImageResource;
import com.google.inject.Inject;

import org.exoplatform.ide.api.resources.ResourceProvider;
import org.exoplatform.ide.core.editor.EditorRegistry;
import org.exoplatform.ide.editor.EditorInitException;
import org.exoplatform.ide.editor.EditorInput;
import org.exoplatform.ide.editor.EditorPartPresenter;
import org.exoplatform.ide.editor.EditorProvider;
import org.exoplatform.ide.json.JsonCollections;
import org.exoplatform.ide.json.JsonStringMap;
import org.exoplatform.ide.part.PartAgent;
import org.exoplatform.ide.part.PartAgent.PartStackType;
import org.exoplatform.ide.resources.FileType;
import org.exoplatform.ide.resources.model.File;
import org.exoplatform.ide.util.loging.Log;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
public class EditorAgent
{

   private JsonStringMap<EditorPartPresenter> openedEditors;

   /**
    * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
    * @version $Id:
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

   private PartAgent partAgent;

   @Inject
   public EditorAgent(EditorRegistry editorRegistry, ResourceProvider provider, PartAgent partAgent)
   {
      super();
      this.editorRegistry = editorRegistry;
      this.provider = provider;
      this.partAgent = partAgent;
      openedEditors = JsonCollections.createStringMap();
   }

   public void openEditor(final File file)
   {
//      if (openedEditors.containsKey(file.getId()))
//      {
//         partAgent.setActivePart(openedEditors.get(file.getId()));
//      }
//      else
//      {
         FileType fileType = provider.getFileType(file);
         EditorProvider editorProvider = editorRegistry.getDefaultEditor(fileType);
         EditorPartPresenter editor = editorProvider.getEditor();
         try
         {
            editor.init(new EditorInputImpl(file));
         }
         catch (EditorInitException e)
         {
            Log.error(getClass(), e);
         }
         partAgent.addPart(editor, PartStackType.EDITING);
         openedEditors.put(file.getId(), editor);
      }
//   }

}
