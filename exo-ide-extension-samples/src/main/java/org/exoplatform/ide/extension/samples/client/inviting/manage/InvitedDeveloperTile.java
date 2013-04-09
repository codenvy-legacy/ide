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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class InvitedDeveloperTile extends Composite {
    private static InvitedDeveloperTileUiBinder uiBinder = GWT.create(InvitedDeveloperTileUiBinder.class);

    interface InvitedDeveloperTileUiBinder extends UiBinder<Widget, InvitedDeveloperTile> {
    }

    interface Style extends CssResource {
        String userFieldBody();

        String userFieldBodySelected();
    }

    @UiField
    Style style;

    @UiField
    Label recipient;

    @UiField
    Label status;

    public InvitedDeveloperTile(final UserInvitations invite) {
        initWidget(uiBinder.createAndBindUi(this));

        recipient.setText(invite.getRecipient());

        if ("OWNER".equals(invite.getStatus())) {
            status.getElement().setInnerHTML("<span style=\"font-weight:bold\">Owner</span>");
        } else if ("INACTIVE".equals(invite.getStatus())) {
            status.getElement().setInnerHTML("<span style=\"color:green\">Accepted</span>");
        } else {
            status.getElement().setInnerHTML("<span style=\"color:red;\">Pending</span>");
        }
    }
}
