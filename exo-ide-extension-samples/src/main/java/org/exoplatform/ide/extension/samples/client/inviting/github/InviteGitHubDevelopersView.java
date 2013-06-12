/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.samples.client.inviting.github;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;
import org.exoplatform.ide.git.shared.GitHubUser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class InviteGitHubDevelopersView extends ViewImpl implements InviteGitHubDevelopersPresenter.Display {

    private static final String                       ID       = "ide.inviteGitHubDevelopersView";

    private static final String                       TITLE    = "Invite GitHub Developers";

    private static final int                          WIDTH    = 800;

    private static final int                          HEIGHT   = 500;

    private static InviteGitHubDevelopersViewUiBinder uiBinder = GWT.create(InviteGitHubDevelopersViewUiBinder.class);

    interface InviteGitHubDevelopersViewUiBinder extends UiBinder<Widget, InviteGitHubDevelopersView> {
    }

    private class UserListWidget extends Widget {
        public UserListWidget(Element e) {
            setElement(e);
        }

        /**
         * Adds a new child widget
         * 
         * @param w the widget to be added
         */
        public void add(Widget w) {
            add(w, getElement());
        }

        /**
         * Adds a new child widget to the panel, attaching its Element to the specified container Element.
         * 
         * @param child the child widget to be added
         * @param container the element within which the child will be contained
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

    UserListWidget  userListWidget;

    @UiField
    DivElement      userListElement;

    @UiField
    CheckBox        checkAll;

    @UiField
    ImageButton     inviteButton, addMessageButton, cancelButton;

    @UiField
    DivElement      inviteUserListDiv;

    @UiField
    DivElement      inviteMessageDiv;

    @UiField
    TextAreaElement inviteMessage;

    public InviteGitHubDevelopersView() {
        super(ID, "modal", TITLE, new Image(SamplesClientBundle.INSTANCE.invite()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
        setCloseOnEscape(true);
    }

    private Map<GitHubUser, GitHubUserTile> cards = new HashMap<GitHubUser, GitHubUserTile>();

    @Override
    public void setDevelopers(List<GitHubUser> userList, GitHubUserSelectionChangedHandler selectionChangedHandler) {
        if (userListWidget != null) {
            userListWidget.removeFromParent();
        }
        userListWidget = new UserListWidget((Element)userListElement.cast());

        cards.clear();
        for (GitHubUser user : userList) {
            GitHubUserTile card = new GitHubUserTile(user);
            card.setSelectionChangedHandler(selectionChangedHandler);
            userListWidget.add(card);
            cards.put(user, card);
        }
    }

    @Override
    public boolean isSelected(GitHubUser user) {
        return cards.get(user).isSelected();
    }

    @Override
    public void setSelected(GitHubUser user, boolean selected) {
        cards.get(user).setSelected(selected);
    }

    @Override
    public HasValue<Boolean> getSelectAllCheckBox() {
        return checkAll;
    }

    @Override
    public HasClickHandlers getInviteButton() {
        return inviteButton;
    }

    @Override
    public HasClickHandlers getAddMessageButton() {
        return addMessageButton;
    }

    @Override
    public HasClickHandlers getCloseButton() {
        return cancelButton;
    }

    @Override
    public String getInviteMessage() {
        return inviteMessage.getValue();
    }

    @Override
    public void setInviteButtonEnabled(boolean enabled) {
        inviteButton.setEnabled(enabled);
    }

    @Override
    public void setAddMessageButtonEnabled(boolean enabled) {
        addMessageButton.setEnabled(enabled);
    }

    @Override
    public void setMessageFiledVisibility(boolean visible) {
        if (visible) {
            inviteMessageDiv.getStyle().setDisplay(Display.INLINE);
            inviteUserListDiv.getStyle().setBottom(130, Unit.PX);
        } else {
            inviteMessageDiv.getStyle().setDisplay(Display.NONE);
            inviteUserListDiv.getStyle().setBottom(70, Unit.PX);
            inviteMessage.setValue("");
        }
    }

    @Override
    public void setAddMessageButtonText(String text) {
        addMessageButton.setText(text);
    }

    @Override
    public String getAddMessageButtonText() {
        return addMessageButton.getText();
    }
}
