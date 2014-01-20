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

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.ssh.client.SshKeyService;
import com.codenvy.ide.ext.ssh.dto.KeyItem;
import com.codenvy.ide.ext.ssh.dto.PublicKey;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.ui.loader.Loader;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * The presenter for showing ssh key.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 */
@Singleton
public class SshKeyPresenter implements SshKeyView.ActionDelegate {
    private SshKeyView view;
    private DtoFactory dtoFactory;
    private SshKeyService       service;
    private EventBus            eventBus;
    private NotificationManager notificationManager;
    private Loader              loader;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param eventBus
     * @param notificationManager
     */
    @Inject
    public SshKeyPresenter(SshKeyView view,
                           SshKeyService service,
                           EventBus eventBus,
                           Loader loader,
                           NotificationManager notificationManager,
                           DtoFactory dtoFactory) {
        this.view = view;
        this.dtoFactory = dtoFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.eventBus = eventBus;
        this.loader = loader;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog(@NotNull KeyItem keyItem) {
        view.addHostToTitle(keyItem.getHost());

        try {
            service.getPublicKey(keyItem, new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                public void onSuccess(String result) {
                    loader.hide();
                    PublicKey key = dtoFactory.createDtoFromJson(result, PublicKey.class);
//                    JSONObject jso = new JSONObject(result);
//                    String key = jso.get("key").isString().stringValue();
                    view.setKey(key.getKey());
                    view.showDialog();
                }

                @Override
                public void onFailure(Throwable exception) {
                    loader.hide();
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            e.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }
}