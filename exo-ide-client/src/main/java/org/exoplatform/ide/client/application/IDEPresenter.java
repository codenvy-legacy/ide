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
package org.exoplatform.ide.client.application;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.application.phases.LoadRegistryConfigurationPhase;
import org.exoplatform.ide.client.application.phases.Phase;
import org.exoplatform.ide.client.framework.control.event.ControlsUpdatedEvent;
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
            new ControlsFormatter(eventBus).format(controlsRegistration.getRegisteredControls());
            eventBus.fireEvent(new ControlsUpdatedEvent(controlsRegistration.getRegisteredControls()));
            new LoadRegistryConfigurationPhase(eventBus, controlsRegistration);
         }
      }.schedule(Phase.DELAY_BETWEEN_PHASES);

   }

   public void onError(ExceptionThrownEvent event)
   {
      eventBus.fireEvent(new ClearFocusEvent());
   }

}
