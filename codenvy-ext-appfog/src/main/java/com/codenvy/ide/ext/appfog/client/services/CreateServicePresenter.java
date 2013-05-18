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
package com.codenvy.ide.ext.appfog.client.services;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.*;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppfogServicesUnmarshaller;
import com.codenvy.ide.ext.appfog.shared.AppfogProvisionedService;
import com.codenvy.ide.ext.appfog.shared.AppfogServices;
import com.codenvy.ide.ext.appfog.shared.AppfogSystemService;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

import java.util.LinkedHashMap;

/**
 * Presenter for creating new service.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class CreateServicePresenter implements CreateServiceView.ActionDelegate {
    private CreateServiceView                       view;
    private EventBus                                eventBus;
    private ConsolePart                             console;
    private AppfogLocalizationConstant              constant;
    private AppfogAutoBeanFactory                   autoBeanFactory;
    private AsyncCallback<AppfogProvisionedService> createServiceCallback;
    private LoginPresenter                          loginPresenter;
    private AppfogClientService                     service;
    private ResourceProvider                        resourceProvider;
    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler createServiceLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            onCreateClicked();
        }
    };

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param constant
     * @param autoBeanFactory
     * @param loginPresenter
     * @param service
     * @param resourceProvider
     */
    @Inject
    protected CreateServicePresenter(CreateServiceView view, EventBus eventBus, ConsolePart console, AppfogLocalizationConstant constant,
                                     AppfogAutoBeanFactory autoBeanFactory, LoginPresenter loginPresenter, AppfogClientService service,
                                     ResourceProvider resourceProvider) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.resourceProvider = resourceProvider;
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateClicked() {
        String name = view.getName();
        String type = view.getSystemServices();

        final Project project = resourceProvider.getActiveProject();
        final String infraName = project.getProperty("appfog-infra").getValue().get(0);

        try {
            AutoBean<AppfogProvisionedService> provisionedService = autoBeanFactory.provisionedService();
            AutoBeanUnmarshaller<AppfogProvisionedService> unmarshaller =
                    new AutoBeanUnmarshaller<AppfogProvisionedService>(provisionedService);

            service.createService(AppFogExtension.DEFAULT_SERVER, type, name, null, null, null, infraName,
                                  new AppfogAsyncRequestCallback<AppfogProvisionedService>(unmarshaller, createServiceLoggedInHandler, null,
                                                                                           eventBus, constant, console, loginPresenter) {
                                      @Override
                                      protected void onSuccess(AppfogProvisionedService result) {
                                          view.close();
                                          createServiceCallback.onSuccess(result);
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** Shows dialog. */
    public void showDialog(AsyncCallback<AppfogProvisionedService> callback) {
        this.createServiceCallback = callback;

        getServices();

        view.setName("");

        view.showDialog();
    }

    /** Get the list of AppFog services (provisioned and system). */
    private void getServices() {
        try {
            AppfogServicesUnmarshaller unmarshaller = new AppfogServicesUnmarshaller(autoBeanFactory);
            service.services(AppFogExtension.DEFAULT_SERVER, new AsyncRequestCallback<AppfogServices>(unmarshaller) {
                @Override
                protected void onSuccess(AppfogServices result) {
                    LinkedHashMap<String, String> values = new LinkedHashMap<String, String>();
                    for (AppfogSystemService service : result.getAppfogSystemService()) {
                        values.put(service.getVendor(), service.getDescription());
                    }
                    view.setServices(values);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Window.alert(constant.retrieveServicesFailed());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}