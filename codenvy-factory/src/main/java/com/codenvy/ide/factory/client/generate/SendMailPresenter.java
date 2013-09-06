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

import com.codenvy.ide.factory.client.FactoryClientService;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

/**
 * Presenter to share Factory URL by e-mail.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: SendMailPresenter.java Jun 11, 2013 12:17:04 PM azatsarynnyy $
 */
public class SendMailPresenter implements SendMailHandler, ViewClosedHandler {

    public interface Display extends IsView {

        /**
         * Returns 'To' field.
         *
         * @return 'To' field
         */
        HasValue<String> getRecipientField();

        /**
         * Returns 'Message' field.
         *
         * @return 'Message' field
         */
        HasValue<String> getMessageField();

        /**
         * Returns the 'Send' button.
         *
         * @return 'Send' button
         */
        HasClickHandlers getSendButton();

        /**
         * Returns the 'Cancel' button.
         *
         * @return 'Cancel' button
         */
        HasClickHandlers getCancelButton();

        /** Give focus to the 'To' field. */
        void focusRecipientField();

        /**
         * Enable send button.
         *
         * @param enable
         *         true if enable, otherwise false.
         */
        void enableSendButton(boolean enable);
    }

    /** Display. */
    private Display display;

    public SendMailPresenter() {
        IDE.addHandler(SendMailEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getSendButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doSend();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getRecipientField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                display.enableSendButton(isCorrectFilled());
            }
        });

        display.getMessageField().addValueChangeHandler(new ValueChangeHandler<String>() {
            @Override
            public void onValueChange(ValueChangeEvent<String> stringValueChangeEvent) {
                display.enableSendButton(isCorrectFilled());
            }
        });
    }

    /** @see com.codenvy.ide.factory.client.generate.SendMailHandler#onSendMail(com.codenvy.ide.factory.client.generate.SendMailEvent) */
    @Override
    public void onSendMail(SendMailEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }

        display.getMessageField().setValue(event.getMessage());
        display.focusRecipientField();
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    private void doSend() {
        String recipient = display.getRecipientField().getValue();
        String message = display.getMessageField().getValue();
        try {
            FactoryClientService.getInstance().share(recipient, message, new AsyncRequestCallback<Object>() {

                @Override
                protected void onSuccess(Object result) {
                    IDE.getInstance().closeView(display.asView().getId());
                }

                @Override
                protected void onFailure(Throwable exception) {
                    IDE.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Checks if recipient and message fields are filled correctly.
     *
     * @return true if email is valid and message field isn't empty otherwise false.
     */
    private boolean isCorrectFilled() {
        return !display.getRecipientField().getValue().isEmpty() && !display.getMessageField().getValue().isEmpty() &&
               display.getRecipientField().getValue().matches("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                                                              + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$");
    }
}
