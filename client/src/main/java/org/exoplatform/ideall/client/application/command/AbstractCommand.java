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
package org.exoplatform.ideall.client.application.command;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.application.event.InitializeApplicationEvent;
import org.exoplatform.ideall.client.application.event.InitializeApplicationHandler;
import org.exoplatform.ideall.client.model.ApplicationContext;

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

public abstract class AbstractCommand implements InitializeApplicationHandler
{

   /**
    * File > New > XML File
    */
   private String id;

   /**
    * Create new XML file
    */
   private String title;

   /**
    * Some icon
    */
   private String icon;

   /**
    * Enabled / Disabled
    */
   private boolean enabled;

   /**
    * Visibility Show / Hidden
    */
   private boolean visible;

   /**
    * List of command state listeners.
    * Listeners are uses for enabling, disabling, showing or hiding item in menu or toolbar.
    */
   private List<CommandStateListener> stateListeners = new ArrayList<CommandStateListener>();

   /**
    * Event which fired by this command.
    */
   private GwtEvent<?> event;

   protected HandlerManager eventBus;

   protected ApplicationContext context;

   protected Handlers handlers;

   /**
    * This constructor uses for placing delimiter in top menu.
    * Delimiter = (title == null);
    * 
    * for example, use ICommand("File") for placing delimiter to File menu
    * use ICommand("File/New") for placing delimiter in sub menu File/New
    * 
    * @param id
    * 
    */
   protected AbstractCommand(String id)
   {
      this.id = id;
   }

   protected AbstractCommand(String id, String title, String icon, boolean enabled, boolean visible, GwtEvent<?> event)
   {
      this.id = id;
      this.title = title;
      this.icon = icon;
      this.enabled = enabled;
      this.visible = visible;
      this.event = event;
   }

   public void initialize(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);

      initializeApplicationHandler = eventBus.addHandler(InitializeApplicationEvent.TYPE, this);
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

   public String getId()
   {
      return id;
   }

   public String getTitle()
   {
      return title;
   }

   public String getIcon()
   {
      return icon;
   }

   public boolean isEnabled()
   {
      return enabled;
   }

   /**
    * Update command enabling.
    * Also this method updates all the CommandStateListeners.
    * 
    * @param enabled
    */
   public void setEnabled(boolean enabled)
   {
      if (this.enabled == enabled)
      {
         return;
      }

      this.enabled = enabled;
      for (CommandStateListener stateListener : stateListeners)
      {
         stateListener.updateCommandEnabling(enabled);
      }
   }

   public boolean isVisible()
   {
      return visible;
   }

   /**
    * Update command visibility.
    * Also this method updates all the CommandStateListeners.
    * 
    * @param visible
    */
   public void setVisible(boolean visible)
   {
      if (this.visible == visible)
      {
         return;
      }

      this.visible = visible;
      for (CommandStateListener stateListener : stateListeners)
      {
         stateListener.updateCommandVisibility(enabled);
      }
   }

   public List<CommandStateListener> getStateListeners()
   {
      return stateListeners;
   }

   public GwtEvent<?> getEvent()
   {
      return event;
   }

   private HandlerRegistration initializeApplicationHandler;

   /**
    * Uses for initializing event handlers in the command.
    * 
    * @see org.exoplatform.ideall.client.application.event.InitializeApplicationHandler#onInitializeApplication(org.exoplatform.ideall.client.application.event.InitializeApplicationEvent)
    */
   public void onInitializeApplication(InitializeApplicationEvent event)
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
