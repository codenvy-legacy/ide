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
package com.codenvy.ide.ext.ssh.client.key;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.ssh.client.JsonpAsyncCallback;
import com.codenvy.ide.ext.ssh.client.SshKeyService;
import com.codenvy.ide.ext.ssh.shared.KeyItem;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The presenter for showing ssh key.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
@Singleton
public class SshKeyPresenter implements SshKeyView.ActionDelegate {
    private SshKeyView    view;
    private SshKeyService service;
    private EventBus      eventBus;
    private ConsolePart   consolePart;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param eventBus
     * @param consolePart
     */
    @Inject
    public SshKeyPresenter(SshKeyView view, SshKeyService service, EventBus eventBus, ConsolePart consolePart) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.eventBus = eventBus;
        this.consolePart = consolePart;
    }

    /** Show dialog. */
    public void showDialog(@NotNull KeyItem keyItem) {
        view.addHostToTitle(keyItem.getHost());

        service.getPublicKey(keyItem, new JsonpAsyncCallback<JavaScriptObject>() {
            @Override
            public void onSuccess(JavaScriptObject result) {
                getLoader().hide();
                JSONObject jso = new JSONObject(result);
                String key = jso.get("key").isString().stringValue();
                view.setKey(key);
                view.showDialog();
            }

            @Override
            public void onFailure(Throwable exception) {
                getLoader().hide();
                consolePart.print(exception.getMessage());
                eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }
}