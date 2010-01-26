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
package org.exoplatform.ideall.client.history;

import org.exoplatform.ideall.client.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ideall.client.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ideall.client.editor.event.EditorActiveFileChangedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class HistoryManager implements HistoryListener, RegisterEventHandlersHandler, EditorActiveFileChangedHandler
{

   private HandlerManager eventBus;

   public HistoryManager(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      History.addHistoryListener(this);
   }

   public void onHistoryChanged(String historyToken)
   {
      System.out.println("on history changed > " + historyToken);
   }

   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      System.out.println("changing active file > " + event.getFile());
      String path = event.getFile() != null ? event.getFile().getPath() : "";
      History.newItem(path);
   }

}
