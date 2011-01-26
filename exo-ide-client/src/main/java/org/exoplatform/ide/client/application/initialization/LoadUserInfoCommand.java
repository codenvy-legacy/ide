/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.client.application.initialization;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.application.ControlsRegistration;
import org.exoplatform.ide.client.framework.userinfo.event.GetUserInfoEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class LoadUserInfoCommand implements Command, UserInfoReceivedHandler
{

   private HandlerManager eventBus;

   private Handlers handlers;

   private ControlsRegistration controls;

   public LoadUserInfoCommand(HandlerManager eventBus, ControlsRegistration controls)
   {
      this.eventBus = eventBus;
      this.controls = controls;

      handlers = new Handlers(eventBus);
      handlers.addHandler(UserInfoReceivedEvent.TYPE, this);
   }

   @Override
   public void execute()
   {
      eventBus.fireEvent(new GetUserInfoEvent());
   }

   /**
    * Called when user information ( name, ect ) is received
    * 
    * @see org.exoplatform.ide.client.model.conversation.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ide.client.model.conversation.event.UserInfoReceivedEvent)
    */
   public void onUserInfoReceived(final UserInfoReceivedEvent event)
   {
      if (event.getUserInfo().getRoles() != null && event.getUserInfo().getRoles().size() > 0)
      {
         controls.initControls(event.getUserInfo().getRoles());
         //new LoadApplicationSettingsPhase(eventBus, applicationConfiguration, event.getUserInfo(), controls);
      }
      else
      {
         Dialogs.getInstance().showError("User has no roles defined.");
      }
   }

}
