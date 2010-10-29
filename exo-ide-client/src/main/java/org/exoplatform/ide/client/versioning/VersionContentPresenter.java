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

import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.ImageUtil;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.panel.event.ChangePanelTitleEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionContentHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class VersionContentPresenter implements ShowVersionContentHandler
{

   public interface Display
   {
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
      handlers.addHandler(ShowVersionContentEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   /**
    * @see org.exoplatform.ide.client.versioning.event.ShowVersionContentHandler#onShowVersionContent(org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent)
    */
   public void onShowVersionContent(ShowVersionContentEvent event)
   {
      version = event.getVersion();
      eventBus.fireEvent(new ChangePanelTitleEvent(VersionContentForm.ID, getTitle()));
      display.setVersionContent(event.getVersion().getContent());
   }

   private String getTitle()
   {
      Image image = new Image(IDEImageBundle.INSTANCE.viewVersions());
      String imageHTML = ImageUtil.getHTML(image);
      String hint = "title=\"" + version.getHref() + "\"";
      String title = "<span " + hint + ">" + imageHTML + "&nbsp;" + "Version " + version.getDisplayName();
      return title;
   }
}
