/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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
