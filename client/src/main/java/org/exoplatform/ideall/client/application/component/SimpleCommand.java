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
package org.exoplatform.ideall.client.application.component;

import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ideall.client.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.solution.command.Command;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.GwtEvent.Type;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class SimpleCommand extends Command implements RegisterEventHandlersHandler
{

   protected HandlerManager eventBus;

   protected ApplicationContext context;

   protected Handlers handlers;
   
   protected SimpleCommand(String id, String title, String icon, GwtEvent<?> event)
   {
      super(id, title, icon, event);
   }

   public void initialize(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);

      initializeApplicationHandler = eventBus.addHandler(RegisterEventHandlersEvent.TYPE, this);
   }

   /**
    * Adding event handler to handler list.
    * 
    * @param type
    * @param handler
    */
   protected void addHandler(Type type, EventHandler handler)
   {
      handlers.addHandler(type, handler);
   }

   private HandlerRegistration initializeApplicationHandler;

   /**
    * Uses for initializing event handlers in the command.
    * 
    * @see org.exoplatform.ideall.client.application.event.RegisterEventHandlersHandler#onRegisterEventHandlers(org.exoplatform.ideall.client.application.event.RegisterEventHandlersEvent)
    */
   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      initializeApplicationHandler.removeHandler();
      initialize();
   }

   /**
    * Override this method to complete registration of the handlers
    */
   protected void initialize()
   {
   }

}
