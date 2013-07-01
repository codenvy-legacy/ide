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
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ManageInvitesView extends ViewImpl implements ManageInvitePresenter.Display {
    private static final String ID = "ideManageInvitesView";

    private static final String TITLE = "Manage access";

    private static final int WIDTH = 800;

    private static final int HEIGHT = 400;

    private static final String CLOSE_BUTTON_ID = "ideManageInvitesViewCloseButton";

    private static final String USER_LIST_ELEMENT = "ideManageInvitesViewUserListElement";

    private static ManageAccessViewUiBinder uiBinder = GWT.create(ManageAccessViewUiBinder.class);

    interface ManageAccessViewUiBinder extends UiBinder<Widget, ManageInvitesView> {
    }

    private class UserListWidget extends Widget {
        public UserListWidget(Element e) {
            setElement(e);
        }

        /**
         * Adds a new child widget
         *
         * @param w
         *         the widget to be added
         */
        public void add(Widget w) {
            add(w, getElement());
        }

        public void clear() {
            for (int i = 0; i < getChildren().size(); i++) {
                getChildren().remove(i);
            }
            getElement().setInnerHTML("");
        }

        /**
         * Adds a new child widget to the panel, attaching its Element to the
         * specified container Element.
         *
         * @param child
         *         the child widget to be added
         * @param container
         *         the element within which the child will be contained
         */
        protected void add(Widget child, Element container) {
            // Detach new child.
            child.removeFromParent();

            // Logical attach.
            getChildren().add(child);

            // Physical attach.
            DOM.appendChild(container, child.getElement());

            // Adopt.
            adopt(child);
        }

    }

    public ManageInvitesView() {
        super(ID, "modal", TITLE, new Image(SamplesClientBundle.INSTANCE.manageInvite()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
        setCloseOnEscape(true);

        closeButton.setId(CLOSE_BUTTON_ID);
        userListElement.setId(USER_LIST_ELEMENT);
    }

    interface Style extends CssResource {
        String inviteTopbarTextInput();

        String inviteTopbarTextInputWithHint();
    }

    @UiField
    Style style;

    @UiField
    ImageButton closeButton;

    UserListWidget userListWidget;

    @UiField
    DivElement userListElement;

    private Map<String, InvitedDeveloperTile> cards = new HashMap<String, InvitedDeveloperTile>();

    @Override
    public void setInvitedDevelopers(List<UserInvitations> invites) {
        if (userListWidget != null) {
            userListWidget.removeFromParent();
        }

        userListWidget = new UserListWidget((Element)userListElement.cast());

        //add self to invites for indicating that we are owner of this workspace
        invites.add(new UserInvitations("owner", IDE.user.getName(), "OWNER"));

        Collections.sort(invites, new InvitesComparator());

        cards.clear();
        for (UserInvitations invite : invites) {
            InvitedDeveloperTile card = new InvitedDeveloperTile(invite);
            userListWidget.add(card);
            cards.put(invite.getId(), card);
        }
    }

    @Override
    public HasClickHandlers getCloseButton() {
        return closeButton;
    }

    @Override
    public void clearInvitedDevelopers() {
        if (userListWidget != null) {
            userListWidget.clear();
        }
    }
}
