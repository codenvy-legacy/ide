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

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.ViewEx;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.preview.event.PreviewHTMLEvent;
import org.exoplatform.ide.client.preview.event.PreviewHTMLHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class PreviewHTMLPresenter implements PreviewHTMLHandler, ViewClosedHandler, EditorActiveFileChangedHandler
{

   public interface Display
   {

      /**
       * ID of Preview View
       */
      String ID = "idePreviewHTMLView";

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

   private HandlerManager eventBus;

   /**
    * Instance of attached Display
    */
   private Display display;

   private File activeFile;

   public PreviewHTMLPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(PreviewHTMLEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * Do preview HTML file
    * 
    * @see org.exoplatform.ide.client.preview.event.PreviewHTMLHandler#onPreviewHTMLFile(org.exoplatform.ide.client.preview.event.PreviewHTMLEvent)
    */
   @Override
   public void onPreviewHTMLFile(PreviewHTMLEvent event)
   {
      if (event.isShowPreview() && display == null)
      {
         display = GWT.create(Display.class);
         IDE.getInstance().openView((ViewEx)display);
         previewActiveFile();

         return;
      }

      if (!event.isShowPreview() && display != null)
      {
         IDE.getInstance().closeView(Display.ID);
      }

   }

   private void previewActiveFile()
   {
      if (activeFile == null)
      {
         display.setPreviewAvailable(false);
         display.setMessage("Preview is not available.");
         return;
      }

      if (MimeType.TEXT_HTML.equals(activeFile.getContentType()))
      {

         if (activeFile.isNewFile())
         {
            display.setPreviewAvailable(false);
            display.setMessage("Preview is not available.<br>You should save the file.");
         }
         else
         {
            display.setPreviewAvailable(true);
            display.showPreview(activeFile.getHref());
         }
      }
      else
      {
         display.setPreviewAvailable(false);
         display.setMessage("Preview is not available.");
      }
   }

   /**
    * Handler of ViewClosed event.
    * Clear display instance if closed view is Preview.
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
