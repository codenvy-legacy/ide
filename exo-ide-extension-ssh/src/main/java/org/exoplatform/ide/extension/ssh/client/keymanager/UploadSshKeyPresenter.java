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
package org.exoplatform.ide.extension.ssh.client.keymanager;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedEvent;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedHandler;
import org.exoplatform.ide.client.framework.ui.upload.HasFileSelectedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.ssh.client.SshKeyExtension;
import org.exoplatform.ide.extension.ssh.client.keymanager.ui.UploadSshKeyView;

/**
 * This class is presenter for {@link UploadSshKeyView}. Main appointment of this class is upload private SSH key to the server.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class UploadSshKeyPresenter implements ViewClosedHandler, FileSelectedHandler {
    public interface Display extends IsView {

        /**
         * Get host filed
         *
         * @return instance of {@link HasValue} interface
         */
        HasValue<String> getHostField();

        /** @return {@link HasClickHandlers} instance for Cancel button */
        HasClickHandlers getCancelButon();

        /** @return {@link HasClickHandlers} instance for Upload button */
        HasClickHandlers getUploadButton();

        /**
         * Get file name filed
         *
         * @return instance of {@link HasValue} interface
         */
        HasValue<String> getFileNameField();

        /**
         * Form that do upload
         *
         * @return {@link FormPanel} instance
         */
        FormPanel getFormPanel();

        /**
         * Set error message
         *
         * @param message
         *         the message
         */
        void setMessage(String message);

        HasFileSelectedHandler getFileUploadInput();

        /** Enable Upload button */
        void setUploadButtonEnabled();
    }

    /** Instance of display */
    private Display display;

    /** Registration of {@link ViewClosedEvent} handler */
    private HandlerRegistration viewClosedHandler;

    /** IDE REST Context URL */
    private String restContext = Utils.getRestContext();

    /**
     * @param restContext
     *         part of URL to IDE REST Context
     */
    public UploadSshKeyPresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        display = GWT.create(Display.class);

        bind();

        IDE.getInstance().openView(display.asView());
        viewClosedHandler = IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Add all handlers to controls. */
    private void bind() {
        display.getCancelButon().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getUploadButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                upload();
            }
        });

        display.getFormPanel().addSubmitCompleteHandler(new SubmitCompleteHandler() {
            @Override
            public void onSubmitComplete(SubmitCompleteEvent event) {
                String result = event.getResults();
                if (result.isEmpty()) {
                    IDE.getInstance().closeView(display.asView().getId());
                } else {
                    if (result.startsWith("<pre>") && result.endsWith("</pre>")) {
                        result.substring(5, (result.length() - 6));
                    }
                    IDE.fireEvent(new ExceptionThrownEvent(result));
                }
            }
        });

        display.getFileUploadInput().addFileSelectedHandler(this);
    }

    /** Validate <b>host</b> parameter and do submit action. If <b>host</b> parameter is null or empty string, show error message. */
    private void upload() {
        String host = display.getHostField().getValue();
        if (host == null || host.isEmpty()) {
            display.setMessage(SshKeyExtension.CONSTANTS.hostValidationError());
            return;
        }

        display.getFormPanel().setEncoding(FormPanel.ENCODING_MULTIPART);
        display.getFormPanel().setAction(restContext + Utils.getWorkspaceName() + "/ssh-keys/add?host=" + host);
        display.getFormPanel().submit();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
            viewClosedHandler.removeHandler();
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.upload.FileSelectedHandler#onFileSelected(org.exoplatform.ide.client.framework.ui
     * .upload.FileSelectedEvent) */
    @Override
    public void onFileSelected(FileSelectedEvent event) {
        String file = event.getFileName();
        file = file.replace('\\', '/');

        if (file.indexOf('/') >= 0) {
            file = file.substring(file.lastIndexOf("/") + 1);
        }

        display.getFileNameField().setValue(file);
        display.setUploadButtonEnabled();
    }

}
