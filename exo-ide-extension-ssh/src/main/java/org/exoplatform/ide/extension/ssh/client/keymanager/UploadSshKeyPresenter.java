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
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedEvent;
import org.exoplatform.ide.client.framework.ui.upload.FileSelectedHandler;
import org.exoplatform.ide.client.framework.ui.upload.HasFileSelectedHandler;
import org.exoplatform.ide.client.framework.util.Utils;
import org.exoplatform.ide.extension.ssh.client.SshKeyExtension;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.RefreshKeysEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowUploadFormEvent;
import org.exoplatform.ide.extension.ssh.client.keymanager.event.ShowUploadFormHandler;
import org.exoplatform.ide.extension.ssh.client.keymanager.ui.UploadSshKeyView;

/**
 * This class is presenter for {@link UploadSshKeyView}. Main appointment of this class is upload private SSH key to the server.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class UploadSshKeyPresenter implements ViewClosedHandler, FileSelectedHandler, ShowUploadFormHandler {
    public interface Display extends IsView {

        /**
         * Get host filed
         *
         * @return instance of {@link HasValue} interface
         */
        HasValue<String> getHostField();

        /** @return {@link HasClickHandlers} instance for Cancel button */
        HasClickHandlers getCancelButton();

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

    public UploadSshKeyPresenter() {
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ShowUploadFormEvent.TYPE, this);
    }

    @Override
    public void onShowSshKeyUploadForm(ShowUploadFormEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
        }

        bindDisplay();
    }

    /** Add all handlers to controls. */
    private void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {
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
                    IDE.fireEvent(new RefreshKeysEvent());
                    IDE.getInstance().closeView(display.asView().getId());
                } else {
                    Dialogs.getInstance().showInfo(result);
                }
            }
        });

        display.getFileUploadInput().addFileSelectedHandler(this);

        IDE.getInstance().openView(display.asView());
    }

    /** Validate <b>host</b> parameter and do submit action. If <b>host</b> parameter is null or empty string, show error message. */
    private void upload() {
        String host = display.getHostField().getValue();
        if (host == null || host.isEmpty()) {
            display.setMessage(SshKeyExtension.CONSTANTS.hostValidationError());
            return;
        }

        display.getFormPanel().setEncoding(FormPanel.ENCODING_MULTIPART);
        display.getFormPanel().setAction(Utils.getRestContext() + Utils.getWorkspaceName() + "/ssh-keys/add?host=" + host);
        display.getFormPanel().submit();
    }

    /** {@inheritDoc} */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** {@inheritDoc} */
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
