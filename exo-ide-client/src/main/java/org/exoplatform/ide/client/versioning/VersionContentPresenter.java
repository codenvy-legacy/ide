/**
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
 *
 */

package org.exoplatform.ide.client.versioning;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.editor.event.EditorInitializedEvent;
import org.exoplatform.gwtframework.editor.event.EditorInitializedHandler;
import org.exoplatform.ide.client.module.vfs.api.Version;
import org.exoplatform.ide.client.panel.event.ClosePanelEvent;
import org.exoplatform.ide.client.panel.event.ClosePanelHandler;
import org.exoplatform.ide.client.versioning.event.ShowVersionEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class VersionContentPresenter implements ShowVersionHandler, EditorInitializedHandler, ClosePanelHandler
{

   public interface Display
   {
      void showVersion(Version version);

      String getEditorId();
      
      void setVersionContent(String content);
      
      void closeForm();
   }

   private HandlerManager eventBus;

   private Display display;
   

   private Handlers handlers;

   private Version version;

   public VersionContentPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;
      handlers.addHandler(ShowVersionEvent.TYPE, this);
      handlers.addHandler(ClosePanelEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   /**
    * @see org.exoplatform.ide.client.versioning.event.ShowVersionHandler#onShowVersion(org.exoplatform.ide.client.versioning.event.ShowVersionEvent)
    */
   public void onShowVersion(ShowVersionEvent event)
   {
      System.out.println("VersionContentPresenter.onShowVersion()");
      version = event.getVersion();
      handlers.addHandler(EditorInitializedEvent.TYPE, this);
      display.showVersion(version);
   }

   /**
    * @see org.exoplatform.gwtframework.editor.event.EditorInitializedHandler#onEditorInitialized(org.exoplatform.gwtframework.editor.event.EditorInitializedEvent)
    */
   public void onEditorInitialized(EditorInitializedEvent event)
   {
      if (display.getEditorId().equals(event.getEditorId()))
      {
         handlers.removeHandler(EditorInitializedEvent.TYPE);
         display.setVersionContent(version.getContent());
      }
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.ClosePanelHandler#onClosePanel(org.exoplatform.ide.client.panel.event.ClosePanelEvent)
    */
   public void onClosePanel(ClosePanelEvent event)
   {
      if (VersionContentForm.ID.equals(event.getPanelId())){
         display.closeForm();
      }
   }

}
