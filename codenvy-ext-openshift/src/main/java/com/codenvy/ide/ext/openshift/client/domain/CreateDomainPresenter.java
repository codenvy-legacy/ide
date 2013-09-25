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
package com.codenvy.ide.ext.openshift.client.domain;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.client.marshaller.UserInfoUnmarshaller;
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Create or update domain name.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateDomainPresenter implements CreateDomainView.ActionDelegate {
    private CreateDomainView              view;
    private EventBus                      eventBus;
    private OpenShiftClientServiceImpl    service;
    private OpenShiftLocalizationConstant constant;
    private LoginPresenter                loginPresenter;
    private ResourceProvider              resourceProvider;
    private NotificationManager           notificationManager;
    private AsyncCallback<Boolean>        callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param service
     * @param constant
     * @param loginPresenter
     * @param resourceProvider
     * @param notificationManager
     */
    @Inject
    protected CreateDomainPresenter(CreateDomainView view, EventBus eventBus, OpenShiftClientServiceImpl service,
                                    OpenShiftLocalizationConstant constant, LoginPresenter loginPresenter,
                                    ResourceProvider resourceProvider, NotificationManager notificationManager) {
        this.view = view;
        this.eventBus = eventBus;
        this.service = service;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
    }

    /**
     * Shows dialog.
     *
     * @param callback
     *         callback that will be called after domain changed
     */
    public void showDialog(AsyncCallback<Boolean> callback) {
        this.callback = callback;

        if (!view.isShown()) {
            view.setEnableChangeDomainButton(false);
            view.focusDomainField();
            view.setDomain("");

            view.showDialog();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onDomainChangeClicked() {
        getUserInfo();
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        updateComponent();
    }

    /** Updates component on the view. */
    private void updateComponent() {
        view.setEnableChangeDomainButton(isFilledCorrected());
    }

    /**
     * Check whether necessary fields are correctly filled.
     *
     * @return if <code>true</code> all necessary fields are filled correctly
     */
    private boolean isFilledCorrected() {
        if (!view.getDomain().matches("[a-zA-Z-_]+")) {
            view.setError(constant.changeDomainViewIncorrectDomainName());
            return false;
        }

        view.setError("");
        return true;
    }

    /**
     * Get current user info for stored credentials, check if user has namespace and existed apps and asked for delete them, other wise
     * called another methods to start creating domain.
     */
    protected void getUserInfo() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getUserInfo();
            }
        };
        UserInfoUnmarshaller unmarshaller = new UserInfoUnmarshaller();

        try {
            service.getUserInfo(true,
                                new OpenShiftAsyncRequestCallback<RHUserInfo>(unmarshaller, loggedInHandler, null, eventBus, loginPresenter,
                                                                              notificationManager) {
                                    @Override
                                    protected void onSuccess(RHUserInfo result) {
                                        if (result.getNamespace() != null && !result.getNamespace().isEmpty()) {
                                            if (result.getApps() != null && result.getApps().size() > 0) {
                                                askForRemoveApps();
                                            } else {
                                                removeAllAppsAndDomain();
                                            }
                                        } else {
                                            createDomain();
                                        }
                                    }
                                });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Ask user to remove his applications. */
    private void askForRemoveApps() {
        boolean delete = Window.confirm(constant.changeDomainViewDeleteAppsMessage());

        if (delete) {
            removeAllAppsAndDomain();
        }
    }

    /** Remove user's application and called method to create new domain. */
    private void removeAllAppsAndDomain() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                removeAllAppsAndDomain();
            }
        };

        final String projectId = resourceProvider.getActiveProject() != null ? resourceProvider.getActiveProject().getId() : null;

        try {
            service.destroyAllApplications(true, resourceProvider.getVfsId(), projectId,
                                           new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, loginPresenter,
                                                                                   notificationManager) {
                                               @Override
                                               protected void onSuccess(Void result) {
                                                   createDomain();
                                               }
                                           });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Create new domain. */
    protected void createDomain() {
        final String domainName = view.getDomain();

        try {
            service.createDomain(domainName, false,
                                 new AsyncRequestCallback<String>() {
                                     @Override
                                     protected void onSuccess(String result) {
                                         String msg = constant.changeDomainViewSuccessfullyChanged();
                                         Notification notification = new Notification(msg, INFO);
                                         notificationManager.showNotification(notification);

                                         view.close();

                                         if (callback != null) {
                                             callback.onSuccess(true);
                                         }
                                     }

                                     @Override
                                     protected void onFailure(Throwable exception) {
                                         String msg = constant.changeDomainViewFailedChanged();
                                         Notification notification = new Notification(msg, ERROR);
                                         notificationManager.showNotification(notification);
                                         view.close();

                                         if (callback != null) {
                                             callback.onSuccess(false);
                                         }
                                     }
                                 });
        } catch (RequestException e) {
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
