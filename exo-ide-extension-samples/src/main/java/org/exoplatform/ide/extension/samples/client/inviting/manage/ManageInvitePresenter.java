/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.extension.samples.client.inviting.manage;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.samples.client.inviting.InviteClientService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ManageInvitePresenter implements ManageInviteHandler, ViewClosedHandler, RevokeInviteHandler
{
   interface Display extends IsView
   {
      public void setInvitedDevelopers(List<Invite> invites, RevokeInviteHandler revokeInviteHandler);

      public void clearInvitedDevelopers();

      public HasClickHandlers getCloseButton();
   }

   private Display display;
   private ManageInvitePresenter instance;

   public ManageInvitePresenter()
   {
      IDE.addHandler(ManageInviteEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);

      instance = this;
   }

   public void bindDisplay()
   {
      display.getCloseButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });
   }

   @Override
   public void onManageInvite(ManageInviteEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         getAccessList();
         IDE.getInstance().openView(display.asView());
      }
   }

   private void getAccessList()
   {
      try
      {
         display.clearInvitedDevelopers();
         InvitedDeveloperUnmarshaller unmarshaller = new InvitedDeveloperUnmarshaller(new ArrayList<Invite>());
         InviteClientService.getInstance().getInvitesList(new AsyncRequestCallback<List<Invite>>(unmarshaller)
         {
            @Override
            protected void onSuccess(List<Invite> invites)
            {
               display.setInvitedDevelopers(invites, instance);
            }

            @Override
            protected void onFailure(Throwable throwable)
            {
            }
         });
      }
      catch (RequestException e)
      {
         Dialogs.getInstance().showError("Can't get invite list.");
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onRevokeInvite(final Invite invite)
   {
      final String title = "Revoke invite";
      final String message = "Revoke invite for user: <b>" + invite.getEmail() + "</b>?";
      Dialogs.getInstance().ask(title, message, new BooleanValueReceivedHandler()
      {
         @Override
         public void booleanValueReceived(Boolean value)
         {
            if (value != null && value)
            {
               revokeInvite(invite);
            }
         }
      });
   }

   private void revokeInvite(final Invite invite)
   {
      try
      {
         InviteClientService.getInstance().invalidateInvite(invite.getEmail(), new AsyncRequestCallback<Void>()
         {
            @Override
            protected void onSuccess(Void result)
            {
               getAccessList();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showError("Can't revoke invite.");
            }
         });
      }
      catch (RequestException e)
      {
         Dialogs.getInstance().showError("Some error while trying to revoke invite.");
      }
   }
}
