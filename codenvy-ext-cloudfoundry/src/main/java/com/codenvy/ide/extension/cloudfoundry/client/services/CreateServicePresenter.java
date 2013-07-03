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
package com.codenvy.ide.extension.cloudfoundry.client.services;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryServicesUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.ProvisionedServiceUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.dto.client.DtoClientImpls;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryServices;
import com.codenvy.ide.extension.cloudfoundry.shared.ProvisionedService;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for creating new service.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 16, 2012 12:31:33 PM anya $
 */
@Singleton
public class CreateServicePresenter implements CreateServiceView.ActionDelegate {
    private CreateServiceView                   view;
    private EventBus                            eventBus;
    private ConsolePart                         console;
    private CloudFoundryLocalizationConstant    constant;
    private AsyncCallback<ProvisionedService>   createServiceCallback;
    private LoginPresenter                      loginPresenter;
    private CloudFoundryClientService           service;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider;
    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler createServiceLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            doCreate();
        }
    };

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param constant
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected CreateServicePresenter(CreateServiceView view, EventBus eventBus, ConsolePart console,
                                     CloudFoundryLocalizationConstant constant, LoginPresenter loginPresenter,
                                     CloudFoundryClientService service) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateClicked() {
        doCreate();
    }

    /** Create new provisioned service. */
    private void doCreate() {
        String name = view.getName();
        String type = view.getSystemServices();
        DtoClientImpls.ProvisionedServiceImpl provisionedService = DtoClientImpls.ProvisionedServiceImpl.make();
        ProvisionedServiceUnmarshaller unmarshaller = new ProvisionedServiceUnmarshaller(provisionedService);

        try {
            service.createService(null, type, name, null, null, null,
                                  new CloudFoundryAsyncRequestCallback<ProvisionedService>(unmarshaller, createServiceLoggedInHandler, null,
                                                                                           eventBus, console, constant, loginPresenter,
                                                                                           paasProvider) {
                                      @Override
                                      protected void onSuccess(ProvisionedService result) {
                                          createServiceCallback.onSuccess(result);
                                          view.close();
                                      }
                                  });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** Shows dialog. */
    public void showDialog(CloudFoundryExtension.PAAS_PROVIDER paasProvider, AsyncCallback<ProvisionedService> callback) {
        this.createServiceCallback = callback;
        this.paasProvider = paasProvider;

        getServices();

        view.setName("");

        view.showDialog();
    }

    /** Get the list of CloudFoundry services (provisioned and system). */
    private void getServices() {
        CloudFoundryServicesUnmarshaller unmarshaller = new CloudFoundryServicesUnmarshaller();

        try {
            service.services(null, paasProvider, new AsyncRequestCallback<CloudFoundryServices>(unmarshaller) {
                @Override
                protected void onSuccess(CloudFoundryServices result) {
                    view.setServices(result.getSystem());
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