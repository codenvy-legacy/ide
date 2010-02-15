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

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ideall.client.application.event.InitializeApplicationEvent;
import org.exoplatform.ideall.client.application.event.InitializeApplicationHandler;
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

public class SimpleCommand extends Command implements RegisterEventHandlersHandler, InitializeApplicationHandler
{

   protected HandlerManager eventBus;

   protected ApplicationContext context;

   protected Handlers handlers;

   private HandlerRegistration registerEventHandlersHandler;

   private HandlerRegistration initializeApplicationHandler;

   protected SimpleCommand(String id, String title, String icon, GwtEvent<?> event)
   {
      super(id, title, icon, event);
   }

   public final void initialize(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);

      registerEventHandlersHandler = eventBus.addHandler(RegisterEventHandlersEvent.TYPE, this);
      initializeApplicationHandler = eventBus.addHandler(InitializeApplicationEvent.TYPE, this);
      onInitializeCommand();
   }

   /**
    * Override this method to complete handling of initialization
    */
   protected void onInitializeCommand()
   {
   }

   /**
    * Adding event handler to handler list.
    * 
    * @param type
    * @param handler
    */
   protected final <H extends EventHandler> void addHandler(Type<H> type, H handler)
   {
      handlers.addHandler(type, handler);
   }

   /**
    * Uses for initializing event handlers in the command.
    * 
    * @see org.exoplatform.ideall.client.application.event.RegisterEventHandlersHandler#onRegisterEventHandlers(org.exoplatform.ideall.client.application.event.RegisterEventHandlersEvent)
    */
   public final void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      registerEventHandlersHandler.removeHandler();
      onRegisterHandlers();
   }

   /**
    * Override this method to complete registration of the handlers
    */
   protected void onRegisterHandlers()
   {
   }

   public final void onInitializeApplication(InitializeApplicationEvent event)
   {
      initializeApplicationHandler.removeHandler();
      onInitializeApplication();
   }

   /**
    * Override this handler to complete handling of initialization of application
    */
   protected void onInitializeApplication()
   {
   }

}
