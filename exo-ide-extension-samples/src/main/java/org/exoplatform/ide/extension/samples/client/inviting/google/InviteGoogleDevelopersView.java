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
package org.exoplatform.ide.extension.samples.client.inviting.google;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.dom.client.TextAreaElement;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.invite.GoogleContact;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.extension.samples.client.SamplesClientBundle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class InviteGoogleDevelopersView extends ViewImpl implements InviteGoogleDevelopersPresenter.Display {

    private static final String                       ID         = "ide.inviteGitHubDevelopersView";

    private static final String                       TITLE      = "Invite developers";

    private static final String                       emailsHint = "Type email addresses separated by commas";

    private static final int                          WIDTH      = 700;

    private static final int                          HEIGHT     = 500;

    private boolean                                   isEmailsHintShown;

    private static InviteGoogleDevelopersViewUiBinder uiBinder   = GWT.create(InviteGoogleDevelopersViewUiBinder.class);

    interface InviteGoogleDevelopersViewUiBinder extends UiBinder<Widget, InviteGoogleDevelopersView> {
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

    interface Style extends CssResource {
        String inviteTopbarTextInput();

        String inviteTopbarTextInputWithHint();
    }

    @UiField
    Style           style;

    UserListWidget  userListWidget;

    @UiField
    DivElement      userListElement;

    @UiField
    DivElement      inviteToolbarDiv;

    @UiField
    DivElement      inviteMessageDiv;

    @UiField
    DivElement      inviteUserListDiv;

    @UiField
    CheckBox        checkAll;

    @UiField
    ImageButton     inviteButton, loadGmailContactsButton, addMessageButton, cancelButton;

    @UiField
    TextAreaElement inviteMessage;

    @UiField
    TextInput       emailsTextField;

    public InviteGoogleDevelopersView() {
        super(ID, "modal", TITLE, new Image(SamplesClientBundle.INSTANCE.invite()), WIDTH, HEIGHT);
        add(uiBinder.createAndBindUi(this));
        setCloseOnEscape(true);
    }

    private Map<GoogleContact, GoogleContactTile> cards = new HashMap<GoogleContact, GoogleContactTile>();

    @Override
    public void setDevelopers(List<GoogleContact> contacts, GoogleContactSelectionChangedHandler selectionChangedHandler) {
        if (userListWidget != null) {
            userListWidget.removeFromParent();
        }

        userListWidget = new UserListWidget((Element)userListElement.cast());

        cards.clear();
        for (GoogleContact contact : contacts) {
            GoogleContactTile card = new GoogleContactTile(contact);
            card.setSelectionChangedHandler(selectionChangedHandler);
            userListWidget.add(card);
            cards.put(contact, card);
        }
    }

    @Override
    public boolean isSelected(GoogleContact user) {
        return cards.get(user).isSelected();
    }

    @Override
    public void setSelected(GoogleContact contact, boolean selected) {
        cards.get(contact).setSelected(selected);
    }

    @Override
    public HasValue<Boolean> getSelectAllCheckBox() {
        return checkAll;
    }

    @Override
    public HasClickHandlers getLoadGmailContactsButton() {
        return loadGmailContactsButton;
    }

    @Override
    public void setLoadGmailContactsButtonText(String text) {
        loadGmailContactsButton.setText(text);
    }

    @Override
    public String getLoadGmailContactsButtonText() {
        return loadGmailContactsButton.getText();
    }

    @Override
    public HasClickHandlers getInviteButton() {
        return inviteButton;
    }

    @Override
    public HasClickHandlers getCloseButton() {
        return cancelButton;
    }

    @Override
    public String getInviteMessge() {
        return inviteMessage.getValue();
    }

    @Override
    public void setInviteButtonEnabled(boolean enabled) {
        inviteButton.setEnabled(enabled);
    }

    @Override
    public void setInviteButtonTitle(String title) {
        inviteButton.setText(title);
    }

    @Override
    public HasValue<String> getEmailsTextField() {
        return emailsTextField;
    }

    @Override
    public FocusWidget getEmailsFocusWidget() {
        return emailsTextField;
    }

    /** @see org.exoplatform.ide.extension.samples.client.inviting.google.InviteGoogleDevelopersPresenter.Display#showEmailsHint() */
    @Override
    public void showEmailsHint() {
        emailsTextField.setStyleName(style.inviteTopbarTextInputWithHint());
        emailsTextField.setText(emailsHint);
        isEmailsHintShown = true;
    }

    /** @see org.exoplatform.ide.extension.samples.client.inviting.google.InviteGoogleDevelopersPresenter.Display#hideEmailsHint() */
    @Override
    public void hideEmailsHint() {
        if (!isEmailsHintShown) {
            return;
        }

        emailsTextField.setStyleName(style.inviteTopbarTextInput());
        emailsTextField.setText("");
        isEmailsHintShown = false;
    }

    private void trimHeight() {
        int height = 190;
        int space = 10;

        if (!inviteToolbarDiv.getStyle().getDisplay().equals(Display.NONE.getCssName())) {
            height += 18;
            height += space;
        }
        if (!inviteUserListDiv.getStyle().getDisplay().equals(Display.NONE.getCssName())) {
            height += 295;
            height += space;
        }
        if (!inviteMessageDiv.getStyle().getDisplay().equals(Display.NONE.getCssName())
            && !inviteMessageDiv.getStyle().getDisplay().equals("")) {
            height += 60;
            height += space;
        }
        setHeight(height, Unit.PX);
    }

    @Override
    public void setDevelopersListVisible(boolean visible) {
        if (visible) {
            inviteUserListDiv.getStyle().setDisplay(Display.NONE);
            inviteUserListDiv.getStyle().setDisplay(Display.BLOCK);
            inviteToolbarDiv.getStyle().setDisplay(Display.NONE);
            inviteToolbarDiv.getStyle().setDisplay(Display.BLOCK);
        } else {
            inviteUserListDiv.getStyle().setDisplay(Display.BLOCK);
            inviteUserListDiv.getStyle().setDisplay(Display.NONE);
            inviteToolbarDiv.getStyle().setDisplay(Display.BLOCK);
            inviteToolbarDiv.getStyle().setDisplay(Display.NONE);
        }
        trimHeight();
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
        trimHeight();
    }

    @Override
    public void setAddMessageButtonText(String text) {
        addMessageButton.setText(text);
    }

    @Override
    public String getAddMessageButtonText() {
        return addMessageButton.getText();
    }

    @Override
    public HasClickHandlers getAddMessageButton() {
        return addMessageButton;
    }
}
