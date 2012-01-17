/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.preview;

import org.exoplatform.gwtframework.commons.rest.copy.MimeType;
import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.event.FileSavedEvent;
import org.exoplatform.ide.client.framework.event.FileSavedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */
@RolesAllowed({"administrators", "developers"})
public class PreviewHTMLControl extends SimpleControl implements IDEControl, EditorActiveFileChangedHandler,
   FileSavedHandler
{

   public static final String ID = "Run/Show Preview";

   public static final String TITLE = IDE.IDE_LOCALIZATION_CONSTANT.htmlPreview();

   private FileModel currentlyActiveFile;

   /**
    * 
    */
   public PreviewHTMLControl()
   {
      super(ID);
      setTitle(TITLE);
      setPrompt(TITLE);
      setImages(IDEImageBundle.INSTANCE.preview(), IDEImageBundle.INSTANCE.previewDisabled());
      setEvent(new PreviewHTMLEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize()
    */
   @Override
   public void initialize()
   {
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(FileSavedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      currentlyActiveFile = event.getFile();
      updateVisibility(currentlyActiveFile, currentlyActiveFile == null ? false : !currentlyActiveFile.isPersisted());
   }

   /**
    * @param file
    * @param isNew
    */
   private void updateVisibility(FileModel file, boolean isNew)
   {
      if (file == null)
      {
         setVisible(false);
         setEnabled(false);
         return;
      }

      if (MimeType.TEXT_HTML.equals(file.getMimeType()))
      {
         setVisible(true);
         if (isNew)
         {
            setEnabled(false);
         }
         else
         {
            setEnabled(true);
         }
      }
      else
      {
         setVisible(false);
         setEnabled(false);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event.FileSavedEvent)
    */
   @Override
   public void onFileSaved(FileSavedEvent event)
   {
      if (currentlyActiveFile != null && event.getFile().getId().equals(currentlyActiveFile.getId()))
      {
         updateVisibility(currentlyActiveFile, false);
      }
   }

}
