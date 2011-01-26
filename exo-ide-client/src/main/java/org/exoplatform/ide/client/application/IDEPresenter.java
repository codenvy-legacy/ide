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
package org.exoplatform.ide.client.application;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.application.phases.LoadRegistryConfigurationPhase;
import org.exoplatform.ide.client.application.phases.Phase;
import org.exoplatform.ide.client.browser.BrowserPanel;
import org.exoplatform.ide.client.framework.ui.event.ActivateViewEvent;
import org.exoplatform.ide.client.framework.ui.event.ClearFocusEvent;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class IDEPresenter implements ExceptionThrownHandler
{

   public interface Display
   {

      void showDefaultPerspective();

   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display display;

   private ControlsRegistration controlsRegistration;

   public IDEPresenter(HandlerManager eventBus, ControlsRegistration controlsRegistration)
   {
      this.eventBus = eventBus;
      this.controlsRegistration = controlsRegistration;

      handlers = new Handlers(eventBus);
      //handlers.addHandler(ExceptionThrownEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;
      display.showDefaultPerspective();

      new Timer()
      {
         @Override
         public void run()
         {
            //activate default view
            eventBus.fireEvent(new ActivateViewEvent(BrowserPanel.ID));
            new LoadRegistryConfigurationPhase(eventBus, controlsRegistration);

            //            IDEConfiguration ideConfiguration = new IDEConfiguration();
            //
            //            CommandQueue.getInstance().addCommand(new LoadRegistryConfigurationCommand(eventBus, ideConfiguration),
            //               ConfigurationReceivedSuccessfullyEvent.TYPE);
            //            
            //            CommandQueue.getInstance().addCommand(new LoadUserInfoCommand(eventBus, controlsRegistration),
            //               UserInfoReceivedEvent.TYPE);
            //            
            //            CommandQueue.getInstance().addCommand(
            //               new LoadApplicationSettingsCommand(eventBus, controlsRegistration, ideConfiguration),
            //               ApplicationSettingsReceivedEvent.TYPE);
            //            
            //            CommandQueue.getInstance().addCommand(new InitializeServicesCommand(eventBus, controlsRegistration),
            //               InitializeServicesEvent.TYPE);
            //            
            //            CommandQueue.getInstance().addCommand(new LoadDefaultEntryPointCommand(eventBus, ideConfiguration),
            //               DefaultEntryPointReceivedEvent.TYPE);
            //
            //            CommandQueue.getInstance().run();

         }
      }.schedule(Phase.DELAY_BETWEEN_PHASES);

   }

   public void onError(ExceptionThrownEvent event)
   {
      eventBus.fireEvent(new ClearFocusEvent());
   }

}
