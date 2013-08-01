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