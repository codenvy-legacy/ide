/**
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ide.client.history;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeApplicationEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeApplicationHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.model.ApplicationContext;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedHandler;

import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class HistoryManager implements EditorActiveFileChangedHandler,
   ValueChangeHandler<String>, ItemPropertiesReceivedHandler, InitializeApplicationHandler,
   ExceptionThrownHandler
{

   private HandlerManager eventBus;

   private String currentHistoryToken;

   private String pathToLoad;

   private Handlers handlers;

   public HistoryManager(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);

      this.eventBus.addHandler(InitializeApplicationEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {     
      String path = event.getFile() != null ? event.getFile().getHref() : "";
      if (path.equals(currentHistoryToken))
      {
         return;
      }

      currentHistoryToken = path;
      History.newItem(path);
   }

   public void onValueChange(ValueChangeEvent<String> value)
   {
      // TODO recheck this method
      
//      String path = value.getValue();
//      if ("".equals(path))
//      {
//         return;
//      }
//
//      File file = context.getOpenedFiles().get(path);
//      if (file == null)
//      {
//         loadFileAndSwitch(path);
//      }
//      else
//      {
//         if (file == context.getActiveFile())
//         {
//            return;
//         }
//
//         context.setActiveFile(file);
//         eventBus.fireEvent(new EditorChangeActiveFileEvent(file));
//      }
   }

//   private void loadFileAndSwitch(String path)
//   {
//      pathToLoad = path;
//
//      ExceptionThrownEventHandlerInitializer.clear();
//      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
//      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
//
//      File file = new File(path);
//      VirtualFileSystem.getInstance().getProperties(file);
//   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      stopHandling();
      VirtualFileSystem.getInstance().getContent((File)event.getItem());
   }

   public void onInitializeApplication(InitializeApplicationEvent event)
   {
      // init history state here!
      currentHistoryToken = History.getToken();
      History.addValueChangeHandler(this);
      History.fireCurrentHistoryState();
   }

   public void onError(ExceptionThrownEvent event)
   {
      stopHandling();
      Dialogs.getInstance().showError("Can't open file <b>" + pathToLoad + "</b>!");
   }

   private void stopHandling()
   {
      handlers.removeHandlers();
      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
   }

}
