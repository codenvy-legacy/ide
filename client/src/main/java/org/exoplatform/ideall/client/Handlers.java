/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ideall.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.GwtEvent.Type;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class Handlers
{

   private HandlerManager eventBus;

   private List<HandlerRegistration> handlers = new ArrayList<HandlerRegistration>();

   public Handlers(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
   }

   @SuppressWarnings("unchecked")
   public HandlerRegistration addHandler(Type type, EventHandler handler)
   {
      HandlerRegistration h = eventBus.addHandler(type, handler);
      handlers.add(h);
      return h;
   }

   public void removeHandlers()
   {
      for (HandlerRegistration handler : handlers)
      {
         handler.removeHandler();
      }
      handlers.clear();
   }

}
