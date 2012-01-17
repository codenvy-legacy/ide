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
package org.exoplatform.ide.client.preview;

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.rest.copy.MimeType;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.shared.Link;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PreviewHTMLPresenter implements PreviewHTMLHandler, ViewClosedHandler, EditorActiveFileChangedHandler
{

   public interface Display extends IsView
   {

      /**
       * Shows preview
       * 
       * @param url
       */
      void showPreview(String url);

      /**
       * Sets is preview available
       * 
       * @param available
       */
      void setPreviewAvailable(boolean available);

      void setMessage(String message);

   }

   private static final String PREVIEW_NOT_AVAILABLE_SAVE_FILE = org.exoplatform.ide.client.IDE.OPERATION_CONSTANT
      .previewNotAvailableSaveFile();

   /**
    * Instance of attached Display
    */
   private Display display;

   private FileModel activeFile;

   public PreviewHTMLPresenter()
   {
      IDE.addHandler(PreviewHTMLEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);

      IDE.getInstance().addControl(new PreviewHTMLControl(), Docking.TOOLBAR_RIGHT);
   }

   /**
    * Do preview HTML file
    * 
    * @see org.exoplatform.ide.client.preview.event.PreviewHTMLHandler#onPreviewHTMLFile(org.exoplatform.ide.client.preview.event.PreviewHTMLEvent)
    */
   @Override
   public void onPreviewHTMLFile(PreviewHTMLEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView((View)display);
      }
      display.asView().setViewVisible();
      previewActiveFile();
   }

   private void previewActiveFile()
   {
      if (activeFile == null)
      {
         IDE.getInstance().closeView(display.asView().getId());
         return;
      }

      if (MimeType.TEXT_HTML.equals(activeFile.getMimeType()))
      {
         if (!activeFile.isPersisted())
         {
            display.setPreviewAvailable(false);
            display.setMessage(PREVIEW_NOT_AVAILABLE_SAVE_FILE);
         }
         else
         {
            display.setPreviewAvailable(true);
            display.showPreview(activeFile.getLinkByRelation(Link.REL_CONTENT_BY_PATH).getHref());
         }
      }
      else
      {
         IDE.getInstance().closeView(display.asView().getId());
      }
   }

   /**
    * Handler of ViewClosed event. Clear display instance if closed view is Preview.
    * 
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();

      if (display != null)
      {
         previewActiveFile();
      }
   }

}
