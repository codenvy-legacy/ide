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
package com.codenvy.ide.factory.client.generate;

import com.codenvy.ide.factory.client.generate.SendMailPresenter.Display;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.Widget;

import org.exoplatform.gwtframework.ui.client.component.ImageButton;
import org.exoplatform.gwtframework.ui.client.component.TextAreaInput;
import org.exoplatform.gwtframework.ui.client.component.TextInput;
import org.exoplatform.ide.client.framework.ui.impl.ViewImpl;
import org.exoplatform.ide.client.framework.ui.impl.ViewType;

import static com.codenvy.ide.factory.client.FactoryExtension.LOCALIZATION_CONSTANTS;

/**
 * View for {@link SendMailPresenter}.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: SendMailView.java Jun 11, 2013 12:19:01 PM azatsarynnyy $
 */
public class SendMailView extends ViewImpl implements Display {

    private static final String ID = LOCALIZATION_CONSTANTS.sendMailViewId();

    private static final String TITLE = LOCALIZATION_CONSTANTS.sendMailViewTitle();

    private static final int HEIGHT = 400;

    private static final int WIDTH = 700;

    private static final String RECIPIENT_FIELD_ID = LOCALIZATION_CONSTANTS.sendMailFieldMessageId();

    private static final String MESSAGE_FIELD_ID = LOCALIZATION_CONSTANTS.sendMailFieldMessageId();

    private static final String SEND_BUTTON_ID = LOCALIZATION_CONSTANTS.sendMailButtonSend();

    private static final String CANCEL_BUTTON_ID = LOCALIZATION_CONSTANTS.sendMailButtonCancel();

    private static CommitChangesViewUiBinder uiBinder = GWT.create(CommitChangesViewUiBinder.class);

    interface CommitChangesViewUiBinder extends UiBinder<Widget, SendMailView> {
    }

    @UiField
    TextInput recipientField;

    @UiField
    TextInput senderEmailField;

    @UiField
    TextInput senderNameField;

    @UiField
    TextAreaInput messageField;

    @UiField
    ImageButton sendButton;

    @UiField
    ImageButton cancelButton;

    public SendMailView() {
        super(ID, ViewType.MODAL, TITLE, null, WIDTH, HEIGHT, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        recipientField.getElement().setId(RECIPIENT_FIELD_ID);
        messageField.getElement().setId(MESSAGE_FIELD_ID);
        sendButton.setId(SEND_BUTTON_ID);
        cancelButton.setId(CANCEL_BUTTON_ID);

        sendButton.setEnabled(false);
    }

    /** @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#getRecipientField() */
    @Override
    public HasValue<String> getRecipientField() {
        return recipientField;
    }

    /** @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#getMessageField() */
    @Override
    public HasValue<String> getMessageField() {
        return messageField;
    }

    /** @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#getSendButton() */
    @Override
    public HasClickHandlers getSendButton() {
        return sendButton;
    }

    /** @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#getCancelButton() */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /** @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#focusRecipientField() */
    @Override
    public void focusRecipientField() {
        recipientField.focus();
    }

    /** @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#enableSendButton(boolean) */
    @Override
    public void enableSendButton(boolean enable) {
        sendButton.setEnabled(enable);
    }

    /** @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#getSenderName()  */
    @Override
    public HasValue<String> getSenderName() {
        return senderNameField;
    }

    /** @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#getSenderEmail()  */
    @Override
    public HasValue<String> getSenderEmail() {
        return senderEmailField;
    }
}
