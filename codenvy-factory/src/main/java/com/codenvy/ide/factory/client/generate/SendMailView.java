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

    private static final String              ID                 = LOCALIZATION_CONSTANTS.sendMailViewId();

    private static final String              TITLE              = LOCALIZATION_CONSTANTS.sendMailViewTitle();

    private static final int                 HEIGHT             = 250;

    private static final int                 WIDTH              = 450;

    private static final String              RECIPIENT_FIELD_ID = LOCALIZATION_CONSTANTS.sendMailFieldMessageId();

    private static final String              MESSAGE_FIELD_ID   = LOCALIZATION_CONSTANTS.sendMailFieldMessageId();

    private static final String              SEND_BUTTON_ID     = LOCALIZATION_CONSTANTS.sendMailButtonSend();

    private static final String              CANCEL_BUTTON_ID   = LOCALIZATION_CONSTANTS.sendMailButtonCancel();

    private static CommitChangesViewUiBinder uiBinder           = GWT.create(CommitChangesViewUiBinder.class);

    interface CommitChangesViewUiBinder extends UiBinder<Widget, SendMailView> {
    }

    @UiField
    TextInput     recipientField;

    @UiField
    TextAreaInput messageField;

    @UiField
    ImageButton   sendButton;

    @UiField
    ImageButton   cancelButton;

    public SendMailView() {
        super(ID, ViewType.MODAL, TITLE, null, WIDTH, HEIGHT, false);
        setCloseOnEscape(true);
        add(uiBinder.createAndBindUi(this));

        recipientField.getElement().setId(RECIPIENT_FIELD_ID);
        messageField.getElement().setId(MESSAGE_FIELD_ID);
        sendButton.setId(SEND_BUTTON_ID);
        cancelButton.setId(CANCEL_BUTTON_ID);
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#getRecipientField()
     */
    @Override
    public HasValue<String> getRecipientField() {
        return recipientField;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#getMessageField()
     */
    @Override
    public HasValue<String> getMessageField() {
        return messageField;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#getSendButton()
     */
    @Override
    public HasClickHandlers getSendButton() {
        return sendButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#getCancelButton()
     */
    @Override
    public HasClickHandlers getCancelButton() {
        return cancelButton;
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.SendMailPresenter.Display#focusRecipientField()
     */
    @Override
    public void focusRecipientField() {
        recipientField.focus();
    }

}
