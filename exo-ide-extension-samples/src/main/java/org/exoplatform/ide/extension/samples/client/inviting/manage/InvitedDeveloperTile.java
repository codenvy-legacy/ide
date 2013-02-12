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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;
import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class InvitedDeveloperTile extends Composite
{
   private static final String REVOKE_ACCESS_BUTTON = "ideDeveloperAccessTileRevokeAccessButton";

   private static InvitedDeveloperTileUiBinder uiBinder = GWT.create(InvitedDeveloperTileUiBinder.class);

   interface InvitedDeveloperTileUiBinder extends UiBinder<Widget, InvitedDeveloperTile>
   {
   }

   interface Style extends CssResource
   {
      String userFieldBody();

      String userFieldBodySelected();
   }

   private RevokeInviteHandler revokeInviteHandler;

   @UiField
   Style style;

   @UiField
   Label invitedId;

   @UiField
   Label inviteRole;

   @UiField
   ImageButton revokeImage;

   public InvitedDeveloperTile(final Invite invite)
   {
      initWidget(uiBinder.createAndBindUi(this));

      revokeImage.setId(REVOKE_ACCESS_BUTTON);

      invitedId.setText(invite.getEmail());

      if (invite.isActivated() == null)
      {
         revokeImage.setVisible(false);
         revokeImage.setEnabled(false);
         inviteRole.getElement().setInnerHTML("<span style=\"font-weight:bold\">Owner</span>");
      }
      else if (invite.isActivated())
      {
         revokeImage.setVisible(false);
         revokeImage.setEnabled(false);
         inviteRole.getElement().setInnerHTML("<span style=\"color:green\">Accepted</span>");
      }
      else
      {
         inviteRole.getElement().setInnerHTML("<span style=\"color:red; margin-right:10px\">Pending</span>");
      }

      revokeImage.setImage(new Image(SamplesClientBundle.INSTANCE.cancel()));
      revokeImage.setDisabledImage(new Image(SamplesClientBundle.INSTANCE.cancelDisabled()));
      revokeImage.setPixelSize(25, 16);

      revokeImage.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            revokeInviteHandler.onRevokeInvite(invite);
         }
      });
   }

   public void setRevokeInviteHandler(RevokeInviteHandler handler)
   {
      this.revokeInviteHandler = handler;
   }
}
