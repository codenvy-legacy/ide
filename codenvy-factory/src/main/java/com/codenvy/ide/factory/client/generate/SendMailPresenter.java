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

import com.codenvy.ide.factory.client.FactoryClientService;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
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
    }

    /** Display. */
    private Display             display;

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
    }

    /**
     * @see com.codenvy.ide.factory.client.generate.SendMailHandler#onSendMail(com.codenvy.ide.factory.client.generate.SendMailEvent)
     */
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
}
