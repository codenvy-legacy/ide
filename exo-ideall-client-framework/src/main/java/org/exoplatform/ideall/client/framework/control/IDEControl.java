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
package org.exoplatform.ideall.client.framework.control;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.component.command.SimpleControl;
import org.exoplatform.ideall.client.framework.application.event.InitializeApplicationEvent;
import org.exoplatform.ideall.client.framework.application.event.InitializeApplicationHandler;
import org.exoplatform.ideall.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ideall.client.framework.application.event.RegisterEventHandlersHandler;

import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.GwtEvent.Type;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class IDEControl extends SimpleControl implements RegisterEventHandlersHandler, InitializeApplicationHandler
{

   protected HandlerManager eventBus;

   protected Handlers handlers;

   protected IDEControl(String id, HandlerManager eventBus)
   {
      super(id);

      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(RegisterEventHandlersEvent.TYPE, this);
      handlers.addHandler(InitializeApplicationEvent.TYPE, this);
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
      handlers.removeHandler(RegisterEventHandlersEvent.TYPE);
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
      handlers.removeHandler(InitializeApplicationEvent.TYPE);
      onInitializeApplication();
   }

   /**
    * Override this handler to complete handling of initialization of application
    */
   protected void onInitializeApplication()
   {
   }

}
