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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import org.codenvy.mail.MailSenderClient;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.git.client.GitExtension;

import javax.mail.MessagingException;

import java.io.IOException;

/**
 * Presenter to share Factory URL by e-mail.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: SendMailPresenter.java Jun 11, 2013 12:17:04 PM azatsarynnyy $
 */
public class SendMailPresenter implements SendMailHandler, ViewClosedHandler {

    public interface Display extends IsView {

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

        /** Give focus to the 'Message' field. */
        void focusMessageField();
    }

    /** Display. */
    private Display             display;

    /** Text message to send. */
    private String              message;

    private static final String EMAIL_SUBJECT = "Check out my Codenvy project";

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
        display.focusMessageField();
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
        String from = "";
        String to = "";
        String replyTo = "";
        String subject = EMAIL_SUBJECT;
        String mimeType = "text/html; charset=utf-8";
        String template = display.getMessageField().getValue();
//        try {
//            new MailSenderClient().sendMail(from, to, replyTo, subject, mimeType, template);
//        } catch (IOException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (MessagingException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
    }

    private void handleSendingError(Throwable e) {
        String errorMessage = (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES.commitFailed();
        IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
    }

}
