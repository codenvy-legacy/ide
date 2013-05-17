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
package com.codenvy.ide.ext.appfog.client.info;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogAutoBeanFactory;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for showing application info.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class ApplicationInfoPresenter implements ApplicationInfoView.ActionDelegate {
    private ApplicationInfoView        view;
    private EventBus                   eventBus;
    private ResourceProvider           resourceProvider;
    private ConsolePart                console;
    private AppfogLocalizationConstant constant;
    private AppfogAutoBeanFactory      autoBeanFactory;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;

    /**
     * Create view.
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
                                       AppfogLocalizationConstant constant, AppfogAutoBeanFactory autoBeanFactory,
                                       LoginPresenter loginPresenter, AppfogClientService service) {
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

    /**
     * Shows application info.
     *
     * @param projectId
     */
    private void showApplicationInfo(final String projectId) {
        try {
            AutoBean<AppfogApplication> appfogApplication = autoBeanFactory.appfogApplication();
            AutoBeanUnmarshaller<AppfogApplication> unmarshaller = new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    showApplicationInfo(projectId);
                }
            };

            service.getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                         constant, console, loginPresenter) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               view.setName(result.getName());
                                               view.setState(result.getState());
                                               view.setInstances(String.valueOf(result.getInstances()));
                                               view.setVersion(result.getVersion());
                                               view.setDisk(String.valueOf(result.getResources().getDisk()));
                                               view.setMemory(String.valueOf(result.getResources().getMemory()) + "MB");
                                               view.setModel(String.valueOf(result.getStaging().getModel()));
                                               view.setStack(String.valueOf(result.getStaging().getStack()));
                                               view.setApplicationUris(result.getUris());
                                               view.setApplicationServices(result.getServices());
                                               view.setApplicationEnvironments(result.getEnv());

                                               view.showDialog();
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}