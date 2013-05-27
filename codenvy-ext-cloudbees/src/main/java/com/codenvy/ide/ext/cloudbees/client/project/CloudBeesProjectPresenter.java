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
package com.codenvy.ide.ext.cloudbees.client.project;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAsyncRequestCallback;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAutoBeanFactory;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.delete.DeleteApplicationPresenter;
import com.codenvy.ide.ext.cloudbees.client.info.ApplicationInfoPresenter;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.ext.cloudbees.client.update.UpdateApplicationPresenter;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for managing project, deployed on CloudBeess.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 5, 2011 9:42:32 AM anya $
 */
@Singleton
public class CloudBeesProjectPresenter implements CloudBeesProjectView.ActionDelegate {
    private CloudBeesProjectView       view;
    private ApplicationInfoPresenter   applicationInfoPresenter;
    private UpdateApplicationPresenter updateApplicationPresenter;
    private EventBus                   eventBus;
    private ResourceProvider           resourceProvider;
    private ConsolePart                console;
    private CloudBeesAutoBeanFactory   autoBeanFactory;
    private DeleteApplicationPresenter deleteAppPresenter;
    private LoginPresenter             loginPresenter;
    private CloudBeesClientService     service;

    @Inject
    protected CloudBeesProjectPresenter(CloudBeesProjectView view, ApplicationInfoPresenter applicationInfoPresenter, EventBus eventBus,
                                        ResourceProvider resourceProvider, ConsolePart console, CloudBeesAutoBeanFactory autoBeanFactory,
                                        DeleteApplicationPresenter deleteAppPresenter, LoginPresenter loginPresenter,
                                        CloudBeesClientService service, UpdateApplicationPresenter updateApplicationPresenter) {
        this.view = view;
        this.view.setDelegate(this);
        this.applicationInfoPresenter = applicationInfoPresenter;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.autoBeanFactory = autoBeanFactory;
        this.deleteAppPresenter = deleteAppPresenter;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.updateApplicationPresenter = updateApplicationPresenter;
    }

    /** Shows dialog. */
    public void showDialog() {
        getApplicationInfo(resourceProvider.getActiveProject());
    }

    /**
     * Get application's properties.
     *
     * @param project
     *         project deployed to CloudBees
     */
    private void getApplicationInfo(final Project project) {
        try {
            AutoBean<ApplicationInfo> autoBean = autoBeanFactory.applicationInfo();
            AutoBeanUnmarshaller<ApplicationInfo> unmarshaller = new AutoBeanUnmarshaller<ApplicationInfo>(autoBean);
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    getApplicationInfo(project);
                }
            };

            service.getApplicationInfo(null, resourceProvider.getVfsId(), project.getId(),
                                       new CloudBeesAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                          console, loginPresenter) {
                                           @Override
                                           protected void onSuccess(ApplicationInfo appInfo) {
                                               showAppInfo(appInfo);

                                               view.showDialog();
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Show application's properties.
     *
     * @param appInfo
     */
    private void showAppInfo(ApplicationInfo appInfo) {
        view.setApplicationName(appInfo.getTitle());
        view.setApplicationStatus(appInfo.getStatus());
        view.setApplicationInstances(appInfo.getClusterSize());
        view.setApplicationUrl(appInfo.getUrl());
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateClicked() {
        updateApplicationPresenter.updateApp(null, null);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        deleteAppPresenter.deleteApp(null, null, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                view.close();
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(CloudBeesProjectPresenter.class, "Can not delete application", caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onInfoClicked() {
        applicationInfoPresenter.showDialog();
    }
}