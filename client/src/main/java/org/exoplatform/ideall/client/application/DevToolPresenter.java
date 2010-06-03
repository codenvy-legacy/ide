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
package org.exoplatform.ideall.client.application;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.ui.client.component.command.Command;
import org.exoplatform.gwtframework.ui.client.component.menu.event.UpdateMainMenuEvent;
import org.exoplatform.gwtframework.ui.client.component.statusbar.event.UpdateStatusBarEvent;
import org.exoplatform.gwtframework.ui.client.component.toolbar.event.UpdateToolbarEvent;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ideall.client.ExceptionThrownEventHandlerInitializer;
import org.exoplatform.ideall.client.application.component.AbstractApplicationComponent;
import org.exoplatform.ideall.client.application.component.IDECommand;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.event.ClearFocusEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ideall.client.model.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ideall.client.model.configuration.InvalidConfigurationRecievedEvent;
import org.exoplatform.ideall.client.model.configuration.InvalidConfigurationRecievedHandler;
import org.exoplatform.ideall.client.model.conversation.ConversationService;
import org.exoplatform.ideall.client.model.conversation.event.UserInfoReceivedEvent;
import org.exoplatform.ideall.client.model.conversation.event.UserInfoReceivedHandler;
import org.exoplatform.ideall.client.model.settings.SettingsService;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextReceivedEvent;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class DevToolPresenter implements InvalidConfigurationRecievedHandler, ConfigurationReceivedSuccessfullyHandler,
   ApplicationContextReceivedHandler, UserInfoReceivedHandler, ExceptionThrownHandler
{

   public interface Display
   {

      void showDefaultPerspective();

   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display display;

   private ApplicationContext context;

   public DevToolPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.showDefaultPerspective();

      handlers.addHandler(InvalidConfigurationRecievedEvent.TYPE, this);
      handlers.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      handlers.addHandler(UserInfoReceivedEvent.TYPE, this);
      handlers.addHandler(ApplicationContextReceivedEvent.TYPE, this);

      /*
       * Intializing application components
       */
      for (AbstractApplicationComponent plugin : context.getComponents())
      {
         plugin.initialize(eventBus, context);
      }

      /*
       * Updating top menu
       */
      eventBus.fireEvent(new UpdateMainMenuEvent(context.getCommands()));
      eventBus.fireEvent(new UpdateStatusBarEvent(context.getStatusBarItems(), context.getCommands()));
      
      /*
       * Initializing handlers of menu items
       */
      for (Command command : context.getCommands())
      {
         if (command instanceof IDECommand)
         {
            ((IDECommand)command).initialize(eventBus, context);
         }
      }

      /*
       * Copy state of toolbar to defaultToolBarItems list.
       * Then will be used for restoring the default state of toolbar.
       */
      context.getToolBarDefaultItems().clear();
      context.getToolBarDefaultItems().addAll(context.getToolBarItems());
   }

   /**
    * Invalid application configuration received handler
    * 
    * @see org.exoplatform.ideall.client.model.configuration.InvalidConfigurationRecievedHandler#onInvalidConfigurationReceived(org.exoplatform.ideall.client.model.configuration.InvalidConfigurationRecievedEvent)
    */
   public void onInvalidConfigurationReceived(InvalidConfigurationRecievedEvent event)
   {
      Dialogs.getInstance().showError("Invalid configuration", event.getMessage());
   }

   /**
    * Called in case the valid configuration of the application is received
    * 
    * @see org.exoplatform.ideall.client.model.configuration.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ideall.client.model.configuration.ConfigurationReceivedSuccessfullyEvent)
    */
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      ConversationService.getInstance().getUserInfo();
   }

   /**
    * Called when user information ( name, ect ) is received
    * 
    * @see org.exoplatform.ideall.client.model.conversation.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ideall.client.model.conversation.event.UserInfoReceivedEvent)
    */
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      context.setUserInfo(event.getUserInfo());
      SettingsService.getInstance().getSettings(context);
   }

   /**
    * Called when application state is received
    * 
    * @see org.exoplatform.ideall.client.model.settings.event.ApplicationContextReceivedHandler#onApplicationContextReceived(org.exoplatform.ideall.client.model.settings.event.ApplicationContextReceivedEvent)
    */
   public void onApplicationContextReceived(ApplicationContextReceivedEvent event)
   {
      CookieManager.getInstance().getApplicationState(context);

      if (context.getEntryPoint() == null) {
         context.setEntryPoint(Configuration.getInstance().getDefaultEntryPoint());
      }
      
//      context.getToolBarItems().clear();
//      context.getToolBarItems().addAll(context.getToolBarDefaultItems());

      eventBus.fireEvent(new UpdateToolbarEvent(context.getToolBarItems(), context.getCommands()));
      
      if (context.getEntryPoint() != null) {
         new WorkspaceChecker(eventBus, context);
      } else {
         Dialogs.getInstance().showError("Entry point not set!");
         ExceptionThrownEventHandlerInitializer.initialize(eventBus);         
         new ApplicationInitializer(eventBus, context);         
      }
   }

   public void onError(ExceptionThrownEvent event)
   {
      eventBus.fireEvent(new ClearFocusEvent());
   }

}
