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
package com.codenvy.ide.ext.openshift.client.cartridge;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.client.marshaller.ListUnmarshaller;
import com.codenvy.ide.ext.openshift.shared.AppInfo;
import com.codenvy.ide.ext.openshift.shared.OpenShiftEmbeddableCartridge;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Presenter to control creating cartridges for the application on OpenShift.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateCartridgePresenter implements CreateCartridgeView.ActionDelegate {
    private CreateCartridgeView           view;
    private EventBus                      eventBus;
    private OpenShiftClientServiceImpl    service;
    private OpenShiftLocalizationConstant constant;
    private LoginPresenter                loginPresenter;
    private NotificationManager           notificationManager;
    private AppInfo                       application;
    private AsyncCallback<Boolean>        callback;

    @Inject
    protected CreateCartridgePresenter(CreateCartridgeView view, EventBus eventBus, OpenShiftClientServiceImpl service,
                                       OpenShiftLocalizationConstant constant, LoginPresenter loginPresenter,
                                       NotificationManager notificationManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.constant = constant;
        this.service = service;
        this.loginPresenter = loginPresenter;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
    }

    /**
     * Show dialog for change domain name.
     *
     * @param application
     *         instance of application that contain name for which cartridge will be created.
     * @param callback
     *         callback
     */
    public void showDialog(AppInfo application, AsyncCallback<Boolean> callback) {
        this.application = application;
        this.callback = callback;

        if (!view.isShown()) {
            setCartridges();
        }
    }

    /** Get cartridges list from server and setting them into client's list box. */
    private void setCartridges() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                setCartridges();
            }
        };
        ListUnmarshaller unmarshaller = new ListUnmarshaller();

        try {
            service.getCartridges(
                    new OpenShiftAsyncRequestCallback<JsonArray<String>>(unmarshaller, loggedInHandler, null, eventBus, loginPresenter,
                                                                         notificationManager) {
                        @Override
                        protected void onSuccess(JsonArray<String> result) {
                            JsonArray<OpenShiftEmbeddableCartridge> cartridges = application.getEmbeddedCartridges();
                            for (int i = 0; i < cartridges.size(); i++) {
                                if (result.contains(cartridges.get(i).getName())) {
                                    result.remove(cartridges.get(i).getName());
                                }
                            }

                            view.setCartridgesList(result);
                            view.showDialog();
                        }
                    });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Handler for creating cartridge click. */
    @Override
    public void onCreateCartridgeClicked() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                onCreateCartridgeClicked();
            }
        };

        final String appName = application.getName();
        final String cartridgeName = view.getCartridgeName();
        try {
            service.addCartridge(appName, cartridgeName,
                                 new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, loginPresenter,
                                                                         notificationManager) {
                                     @Override
                                     protected void onSuccess(Void result) {
                                         if (callback != null) {
                                             callback.onSuccess(true);
                                         }
                                         view.close();
                                         String msg = constant.createCartridgeViewSuccessfullyAdded(cartridgeName, appName);
                                         Notification notification = new Notification(msg, INFO);
                                         notificationManager.showNotification(notification);
                                     }

                                     @Override
                                     protected void onFailure(Throwable exception) {
                                         super.onFailure(exception);
                                         if (callback != null) {
                                             callback.onSuccess(false);
                                         }
                                         view.close();
                                     }
                                 });
        } catch (RequestException e) {
            view.close();

            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Handler for cancel click. Perform close window. */
    @Override
    public void onCancelClicked() {
        view.close();
    }
}
