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
package com.codenvy.ide.ext.openshift.client.key;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.client.marshaller.UserInfoUnmarshaller;
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Handler which execute update public ssh key for OpenShift account.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class UpdateKeyPresenter {
    private EventBus                   eventBus;
    private LoginPresenter             loginPresenter;
    private OpenShiftClientServiceImpl service;
    private NotificationManager        notificationManager;
    private AsyncCallback<Boolean>     publicKeyUpdateCallback;

    /**
     * Create handler.
     *
     * @param eventBus
     * @param loginPresenter
     * @param service
     * @param notificationManager
     */
    @Inject
    protected UpdateKeyPresenter(EventBus eventBus, LoginPresenter loginPresenter, OpenShiftClientServiceImpl service,
                                 NotificationManager notificationManager) {
        this.eventBus = eventBus;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.notificationManager = notificationManager;
    }

    /** If user is not logged in to OpenShift, this handler will be called, after user logged in. */
    private LoggedInHandler updatePublicKeyLoginHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            updatePublicKey(publicKeyUpdateCallback);
        }
    };

    /**
     * Perform to update ssh public key for current loggined account.
     *
     * @param callback
     *         callback which will be executed if update is successful or fails
     */
    public void updatePublicKey(AsyncCallback<Boolean> callback) {
        this.publicKeyUpdateCallback = callback;
        UserInfoUnmarshaller unmarshaller = new UserInfoUnmarshaller();

        try {
            service.getUserInfo(false,
                                new OpenShiftAsyncRequestCallback<RHUserInfo>(unmarshaller, updatePublicKeyLoginHandler, null, eventBus,
                                                                              loginPresenter, notificationManager) {
                                    @Override
                                    protected void onSuccess(RHUserInfo result) {
                                        if (result.getNamespace() != null && !result.getNamespace().isEmpty()) {
                                            updateDomainWithNewKey(result.getNamespace());
                                        } else {
                                            if (publicKeyUpdateCallback != null) {
                                                publicKeyUpdateCallback.onSuccess(false);
                                            }
                                            Window.alert("You must create domain first to update public key for it.");
                                        }
                                    }
                                });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Perform update in domain public ssh key.
     *
     * @param nameSpace
     *         domain name in which key should be updated
     */
    private void updateDomainWithNewKey(String nameSpace) {
        try {
            service.createDomain(nameSpace, true,
                                 new OpenShiftAsyncRequestCallback<String>(null, updatePublicKeyLoginHandler, null, eventBus,
                                                                           loginPresenter, notificationManager) {
                                     @Override
                                     protected void onSuccess(String result) {
                                         String msg = "Public key successfully updated.";
                                         Notification notification = new Notification(msg, INFO);
                                         notificationManager.showNotification(notification);
                                         if (publicKeyUpdateCallback != null) {
                                             publicKeyUpdateCallback.onSuccess(true);
                                         }
                                     }

                                     @Override
                                     protected void onFailure(Throwable exception) {
                                         super.onFailure(exception);
                                         if (publicKeyUpdateCallback != null) {
                                             publicKeyUpdateCallback.onSuccess(false);
                                         }
                                     }
                                 });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            if (publicKeyUpdateCallback != null) {
                publicKeyUpdateCallback.onFailure(e);
            }
        }
    }
}
