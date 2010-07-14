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
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.initializer.RegistryConstants;
import org.exoplatform.gwtframework.ui.client.component.command.Control;
import org.exoplatform.gwtframework.ui.client.component.command.PopupMenuControl;
import org.exoplatform.gwtframework.ui.client.component.menu.event.UpdateMainMenuEvent;
import org.exoplatform.gwtframework.ui.client.component.statusbar.event.UpdateStatusBarEvent;
import org.exoplatform.gwtframework.ui.client.component.toolbar.event.UpdateToolbarEvent;
import org.exoplatform.ideall.client.ExceptionThrownEventHandlerInitializer;
import org.exoplatform.ideall.client.IDELoader;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.framework.control.IDEControl;
import org.exoplatform.ideall.client.framework.control.NewItemControl;
import org.exoplatform.ideall.client.framework.plugin.IDEModule;
import org.exoplatform.ideall.client.framework.ui.event.ClearFocusEvent;
import org.exoplatform.ideall.client.hotkeys.event.RefreshHotKeysEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.configuration.Configuration;
import org.exoplatform.ideall.client.model.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ideall.client.model.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ideall.client.model.configuration.InvalidConfigurationRecievedEvent;
import org.exoplatform.ideall.client.model.configuration.InvalidConfigurationRecievedHandler;
import org.exoplatform.ideall.client.model.conversation.ConversationService;
import org.exoplatform.ideall.client.model.conversation.ConversationServiceImpl;
import org.exoplatform.ideall.client.model.conversation.event.UserInfoReceivedEvent;
import org.exoplatform.ideall.client.model.conversation.event.UserInfoReceivedHandler;
import org.exoplatform.ideall.client.model.discovery.DiscoveryServiceImpl;
import org.exoplatform.ideall.client.model.settings.SettingsService;
import org.exoplatform.ideall.client.model.settings.SettingsServiceImpl;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextReceivedEvent;
import org.exoplatform.ideall.client.model.settings.event.ApplicationContextReceivedHandler;
import org.exoplatform.ideall.client.model.template.TemplateServiceImpl;
import org.exoplatform.ideall.client.model.util.ImageUtil;
import org.exoplatform.ideall.client.module.navigation.control.newitem.NewFileCommand;
import org.exoplatform.ideall.client.module.navigation.control.newitem.NewFilePopupMenuControl;
import org.exoplatform.ideall.client.module.preferences.event.SelectWorkspaceEvent;
import org.exoplatform.ideall.gadget.GadgetServiceImpl;
import org.exoplatform.ideall.groovy.GroovyServiceImpl;
import org.exoplatform.ideall.vfs.webdav.WebDavVirtualFileSystem;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class IDEallPresenter implements InvalidConfigurationRecievedHandler, ConfigurationReceivedSuccessfullyHandler,
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

   public IDEallPresenter(HandlerManager eventBus, ApplicationContext context)
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

      for (IDEModule module : context.getModules())
      {
         module.initializePlugin(eventBus, context);
      }

      createNewItemControlsGroup();

      /*
       * Updating top menu
       */
      eventBus.fireEvent(new UpdateMainMenuEvent(context.getCommands()));
      eventBus.fireEvent(new UpdateStatusBarEvent(context.getStatusBarItems(), context.getCommands()));

      initializeControls();

      /*
       * Copy state of toolbar to defaultToolBarItems list.
       * Then will be used for restoring the default state of toolbar.
       */
      context.getToolBarDefaultItems().clear();
      context.getToolBarDefaultItems().addAll(context.getToolBarItems());
   }

   private void createNewItemControlsGroup()
   {
      while (true)
      {
         NewItemControl control = getNewItemControl();
         if (control == null)
         {
            break;
         }

         int position = context.getCommands().indexOf(control);
         NewFileCommand command =
            new NewFileCommand(control.getId(), control.getTitle(), control.getPrompt(), control.getIcon(),
               control.getEvent());
         
         System.out.println("event > " + control.getEvent());
         
         context.getCommands().set(position, command);
      }
   }

   private NewItemControl getNewItemControl()
   {
      for (Control control : context.getCommands())
      {
         if (control instanceof NewItemControl)
         {
            return (NewItemControl)control;
         }
      }

      return null;
   }

   private void fillNewItemPopupControl() {
      PopupMenuControl popup = null;
      for (Control control : context.getCommands()) {
         if (NewFilePopupMenuControl.ID.equals(control.getId())) {
            popup = (PopupMenuControl)control;
         }
      }
      
      if (popup == null) {
         return;
      }
      
//      for (Control contro1l : context.getCommands()) {
//         if () {
//         }
//      }
//      
//      popup.getCommands().
   }

   private void initializeControls()
   {
      /*
       * Initializing handlers of menu items
       */
      for (Control command : context.getCommands())
      {
         if (command instanceof IDEControl)
         {
            System.out.println("initialization " + command.getId());
            ((IDEControl)command).initialize(eventBus, context);
         }
      }
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
      new SettingsServiceImpl(eventBus, IDELoader.getInstance(), context.getApplicationConfiguration().getRegistryURL());

      new ConversationServiceImpl(eventBus, IDELoader.getInstance(), context.getApplicationConfiguration().getContext());

      new WebDavVirtualFileSystem(eventBus, IDELoader.getInstance(), ImageUtil.getIcons(), context
         .getApplicationConfiguration().getContext());

      new TemplateServiceImpl(eventBus, IDELoader.getInstance(), context.getApplicationConfiguration().getRegistryURL()
         + "/" + RegistryConstants.EXO_APPLICATIONS + "/" + Configuration.APPLICATION_NAME);

      new GroovyServiceImpl(eventBus, context.getApplicationConfiguration().getContext(), IDELoader.getInstance());

      new GadgetServiceImpl(eventBus, IDELoader.getInstance(), context.getApplicationConfiguration().getContext(),
         context.getApplicationConfiguration().getGadgetServer(), context.getApplicationConfiguration()
            .getPublicContext());
      ConversationService.getInstance().getUserInfo();

      new DiscoveryServiceImpl(eventBus, IDELoader.getInstance(), context.getApplicationConfiguration().getContext());
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

      if (context.getEntryPoint() == null)
      {
         context.setEntryPoint(context.getApplicationConfiguration().getDefaultEntryPoint());
      }

      //      context.getToolBarItems().clear();
      //      context.getToolBarItems().addAll(context.getToolBarDefaultItems());

      eventBus.fireEvent(new UpdateToolbarEvent(context.getToolBarItems(), context.getCommands()));

      eventBus.fireEvent(new RefreshHotKeysEvent());

      if (context.getEntryPoint() != null)
      {
         new WorkspaceChecker(eventBus, context);
      }
      else
      {
         Dialogs
            .getInstance()
            .ask(
               "Working workspace",
               "Workspace is not set. Goto <strong>Window->Select workspace</strong>  in main menu for set working workspace?",
               new BooleanValueReceivedCallback()
               {

                  public void execute(Boolean value)
                  {
                     if (value)
                     {
                        eventBus.fireEvent(new SelectWorkspaceEvent());
                     }
                     else
                     {
                        ExceptionThrownEventHandlerInitializer.initialize(eventBus);
                     }
                  }
               });
         new ApplicationInitializer(eventBus, context);
      }
   }

   public void onError(ExceptionThrownEvent event)
   {
      eventBus.fireEvent(new ClearFocusEvent());
   }

}
