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
package com.codenvy.ide.ext.openshift.client.domain;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.openshift.client.OpenShiftAsyncRequestCallback;
import com.codenvy.ide.ext.openshift.client.OpenShiftClientServiceImpl;
import com.codenvy.ide.ext.openshift.client.OpenShiftLocalizationConstant;
import com.codenvy.ide.ext.openshift.client.login.LoggedInHandler;
import com.codenvy.ide.ext.openshift.client.login.LoginPresenter;
import com.codenvy.ide.ext.openshift.client.marshaller.UserInfoUnmarshaller;
import com.codenvy.ide.ext.openshift.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.openshift.shared.RHUserInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Create or update domain name.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class CreateDomainPresenter implements CreateDomainView.ActionDelegate {
    private CreateDomainView              view;
    private EventBus                      eventBus;
    private ConsolePart                   console;
    private OpenShiftClientServiceImpl    service;
    private OpenShiftLocalizationConstant constant;
    private LoginPresenter                loginPresenter;
    private ResourceProvider              resourceProvider;
    private AsyncCallback<Boolean>        callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param service
     * @param constant
     * @param loginPresenter
     * @param resourceProvider
     */
    @Inject
    protected CreateDomainPresenter(CreateDomainView view, EventBus eventBus, ConsolePart console, OpenShiftClientServiceImpl service,
                                    OpenShiftLocalizationConstant constant, LoginPresenter loginPresenter,
                                    ResourceProvider resourceProvider) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.resourceProvider = resourceProvider;

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

        try {
            DtoClientImpls.RHUserInfoImpl userInfo = DtoClientImpls.RHUserInfoImpl.make();
            UserInfoUnmarshaller unmarshaller = new UserInfoUnmarshaller(userInfo);

            service.getUserInfo(true,
                                new OpenShiftAsyncRequestCallback<RHUserInfo>(unmarshaller, loggedInHandler, null, eventBus, console,
                                                                              constant, loginPresenter) {

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
            console.print(e.getMessage());
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
                                           new OpenShiftAsyncRequestCallback<Void>(null, loggedInHandler, null, eventBus, console, constant,
                                                                                   loginPresenter) {
                                               @Override
                                               protected void onSuccess(Void result) {
                                                   createDomain();
                                               }
                                           });
        } catch (RequestException e) {
            console.print(e.getMessage());
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
                                         console.print(msg);
                                         view.close();

                                         if (callback != null) {
                                             callback.onSuccess(true);
                                         }
                                     }

                                     @Override
                                     protected void onFailure(Throwable exception) {
                                         String msg = constant.changeDomainViewFailedChanged();
                                         console.print(msg);
                                         view.close();

                                         if (callback != null) {
                                             callback.onSuccess(false);
                                         }
                                     }
                                 });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
