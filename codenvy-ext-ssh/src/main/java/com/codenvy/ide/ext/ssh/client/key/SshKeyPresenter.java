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