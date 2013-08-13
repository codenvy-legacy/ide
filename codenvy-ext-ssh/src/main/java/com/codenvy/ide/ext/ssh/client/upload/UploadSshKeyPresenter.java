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
package com.codenvy.ide.ext.ssh.client.upload;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.ssh.client.SshLocalizationConstant;
import com.codenvy.ide.util.Utils;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Main appointment of this class is upload private SSH key to the server.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
@Singleton
public class UploadSshKeyPresenter implements UploadSshKeyView.ActionDelegate {
    private UploadSshKeyView        view;
    private SshLocalizationConstant constant;
    private String                  restContext;
    private EventBus                eventBus;
    private ConsolePart             console;

    /**
     * Create presenter.
     *
     * @param view
     * @param constant
     * @param restContext
     * @param eventBus
     * @param console
     */
    @Inject
    public UploadSshKeyPresenter(UploadSshKeyView view, SshLocalizationConstant constant, @Named("restContext") String restContext,
                                 EventBus eventBus, ConsolePart console) {
        this.view = view;
        this.view.setDelegate(this);
        this.constant = constant;
        this.restContext = restContext;
        this.console = console;
        this.eventBus = eventBus;
    }

    /** Show dialog. */
    public void showDialog() {
        view.setMessage("");
        view.setHost("");
        view.setEnabledUploadButton(false);
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onUploadClicked() {
        String host = view.getHost();
        if (host.isEmpty()) {
            view.setMessage(constant.hostValidationError());
            return;
        }
        view.setEncoding(FormPanel.ENCODING_MULTIPART);
        view.setAction(restContext + '/' + Utils.getWorkspaceName() + "/ssh-keys/add?host=" + host);
        view.submit();
    }

    /** {@inheritDoc} */
    @Override
    public void onSubmitComplete(@NotNull String result) {
        if (result.isEmpty()) {
            UploadSshKeyPresenter.this.view.close();
        } else {
            if (result.startsWith("<pre>") && result.endsWith("</pre>")) {
                result.substring(5, (result.length() - 6));
            }
            eventBus.fireEvent(new ExceptionThrownEvent(result));
            console.print(result);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onFileNameChanged() {
        String fileName = view.getFileName();
        view.setEnabledUploadButton(!fileName.isEmpty());
    }
}