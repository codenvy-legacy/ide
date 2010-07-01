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
package org.exoplatform.ideall.client.outline;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.vfs.api.File;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CodeHelperPresenter implements EditorActiveFileChangedHandler
{
   interface Display
   {
      void show();

      void hide();

   }

   private HandlerManager eventBus;

   private ApplicationContext context;

   private Handlers handlers;

   private Display display;

   public CodeHelperPresenter(HandlerManager bus, ApplicationContext applicationContext)
   {
      eventBus = bus;
      context = applicationContext;

      handlers = new Handlers(eventBus);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      File file = event.getFile();
      if (file == null || file.getContentType() == null)
      {
         display.hide();
         return;
      }

      if (file.getContentType().equals(MimeType.APPLICATION_JAVASCRIPT)
         || file.getContentType().equals(MimeType.GOOGLE_GADGET)
         || file.getContentType().equals(MimeType.TEXT_JAVASCRIPT))
      {
         if (context.isShowOutline())
         {
            display.show();
         }
      }
      else
      {
         display.hide();
      }
   }

}