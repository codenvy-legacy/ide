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
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
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
public class ManageInvitePresenter implements ManageInviteHandler, ViewClosedHandler {
    interface Display extends IsView {
        public void setInvitedDevelopers(List<UserInvitations> invites);

        public void clearInvitedDevelopers();

        public HasClickHandlers getCloseButton();
    }

    private Display               display;

    public ManageInvitePresenter() {
        IDE.addHandler(ManageInviteEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    @Override
    public void onManageInvite(ManageInviteEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            getInvites();
            IDE.getInstance().openView(display.asView());
        }
    }

    private void getInvites() {
        try {
            display.clearInvitedDevelopers();
            InvitedDeveloperUnmarshaller unmarshaller = new InvitedDeveloperUnmarshaller(new ArrayList<UserInvitations>());
            InviteClientService.getInstance().getInvitesList(new AsyncRequestCallback<List<UserInvitations>>(unmarshaller) {
                @Override
                protected void onSuccess(List<UserInvitations> invites) {
                    display.setInvitedDevelopers(invites);
                }

                @Override
                protected void onFailure(Throwable throwable) {
                    Dialogs.getInstance().showError(throwable.getMessage());
                }
            });
        } catch (RequestException e) {
            Dialogs.getInstance().showError("Can't get invite list.");
        }
    }

    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }
}
