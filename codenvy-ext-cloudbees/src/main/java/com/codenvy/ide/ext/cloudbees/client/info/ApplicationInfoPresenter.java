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
package com.codenvy.ide.ext.cloudbees.client.info;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAsyncRequestCallback;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAutoBeanFactory;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesLocalizationConstant;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for showing application info.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: ApplicationInfoPresenter.java Jun 30, 2011 5:02:31 PM vereshchaka $
 */
@Singleton
public class ApplicationInfoPresenter implements ApplicationInfoView.ActionDelegate {
    private ApplicationInfoView           view;
    private EventBus                      eventBus;
    private ResourceProvider              resourceProvider;
    private ConsolePart                   console;
    private CloudBeesLocalizationConstant constant;
    private CloudBeesAutoBeanFactory      autoBeanFactory;
    private LoginPresenter                loginPresenter;
    private CloudBeesClientService        service;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param constant
     * @param autoBeanFactory
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected ApplicationInfoPresenter(ApplicationInfoView view, EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                       CloudBeesLocalizationConstant constant, CloudBeesAutoBeanFactory autoBeanFactory,
                                       LoginPresenter loginPresenter, CloudBeesClientService service) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public void onOKClicked() {
        view.close();
    }

    /** Show dialog. */
    public void showDialog() {
        showApplicationInfo(resourceProvider.getActiveProject().getId());
    }

    /** Show dialog. */
    public void showDialog(ApplicationInfo appInfo) {
        showAppInfo(appInfo);
    }

    /**
     * Gets application info from current project.
     *
     * @param projectId
     */
    private void showApplicationInfo(final String projectId) {
        try {
            AutoBean<ApplicationInfo> autoBean = autoBeanFactory.applicationInfo();
            AutoBeanUnmarshaller<ApplicationInfo> unmarshaller = new AutoBeanUnmarshaller<ApplicationInfo>(autoBean);
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    showApplicationInfo(projectId);
                }
            };

            service.getApplicationInfo(null, resourceProvider.getVfsId(), projectId,
                                       new CloudBeesAsyncRequestCallback<ApplicationInfo>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                          console, loginPresenter) {
                                           @Override
                                           protected void onSuccess(ApplicationInfo result) {
                                               showAppInfo(result);
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Shows application info.
     *
     * @param appInfo
     */
    private void showAppInfo(ApplicationInfo appInfo) {
        view.setAppId(appInfo.getId());
        view.setAppTitle(appInfo.getTitle());
        view.setServerPool(appInfo.getServerPool());
        view.setAppStatus(appInfo.getStatus());
        view.setAppContainer(appInfo.getContainer());
        view.setIdleTimeout(appInfo.getIdleTimeout());
        view.setMaxMemory(appInfo.getMaxMemory());
        view.setSecurityMode(appInfo.getSecurityMode());
        view.setClusterSize(appInfo.getClusterSize());
        view.setUrl(appInfo.getUrl());

        view.showDialog();
    }
}