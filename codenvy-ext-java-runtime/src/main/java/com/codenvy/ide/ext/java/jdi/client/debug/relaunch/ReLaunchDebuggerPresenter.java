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
package com.codenvy.ide.ext.java.jdi.client.debug.relaunch;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.java.jdi.client.debug.DebuggerClientService;
import com.codenvy.ide.ext.java.jdi.shared.ApplicationInstance;
import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Provides relaunch debugger process.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 */
@Singleton
public class ReLaunchDebuggerPresenter implements ReLaunchDebuggerView.ActionDelegate {
    private ReLaunchDebuggerView view;
    private DtoFactory dtoFactory;
    private DebuggerClientService       service;
    private ApplicationInstance         instance;
    private NotificationManager         notificationManager;
    private AsyncCallback<DebuggerInfo> callback;
    /** A timer for checking events. */
    private Timer tryConnectDebugger = new Timer() {
        @Override
        public void run() {
            connectDebugger();
        }
    };

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param notificationManager
     */
    @Inject
    protected ReLaunchDebuggerPresenter(ReLaunchDebuggerView view,
                                        DebuggerClientService service,
                                        NotificationManager notificationManager,
                                        DtoFactory dtoFactory) {
        this.view = view;
        this.dtoFactory = dtoFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        tryConnectDebugger.cancel();
        view.close();
        callback.onSuccess(null);
    }

    /** Shows dialog. */
    public void showDialog(@NotNull ApplicationInstance instance, @NotNull AsyncCallback<DebuggerInfo> callback) {
        this.instance = instance;
        this.callback = callback;
        this.view.showDialog();
    }

    /** Connect to debugger. */
    protected void connectDebugger() {

        try {
            service.connect(instance.getDebugHost(), instance.getDebugPort(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                @Override
                public void onSuccess(String result) {
                    tryConnectDebugger.cancel();
                    view.close();
                    callback.onSuccess(dtoFactory.createDtoFromJson(result, DebuggerInfo.class));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Notification notification = new Notification(exception.getMessage(), ERROR);
                    notificationManager.showNotification(notification);
                    callback.onFailure(exception);

                }
            });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            callback.onFailure(e);
        }
    }
}