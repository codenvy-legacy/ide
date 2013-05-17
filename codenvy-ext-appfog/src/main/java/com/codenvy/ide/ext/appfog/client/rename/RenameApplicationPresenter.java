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
package com.codenvy.ide.ext.appfog.client.rename;

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
 * Presenter for rename operation with application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class RenameApplicationPresenter implements RenameApplicationView.ActionDelegate {
    private RenameApplicationView      view;
    private EventBus                   eventBus;
    private ResourceProvider           resourceProvider;
    private ConsolePart                console;
    /** The name of application. */
    private String                     applicationName;
    private AppfogLocalizationConstant constant;
    private AppfogAutoBeanFactory      autoBeanFactory;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param applicationName
     * @param constant
     * @param autoBeanFactory
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected RenameApplicationPresenter(RenameApplicationView view, EventBus eventBus, ResourceProvider resourceProvider,
                                         ConsolePart console, String applicationName, AppfogLocalizationConstant constant,
                                         AppfogAutoBeanFactory autoBeanFactory, LoginPresenter loginPresenter,
                                         AppfogClientService service) {
        this.view = view;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.applicationName = applicationName;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public void onNameChanged() {
        String newName = view.getName();
        boolean enable = !applicationName.equals(newName) && newName != null && !newName.isEmpty();
        view.setEnableRenameButton(enable);
    }

    /** {@inheritDoc} */
    @Override
    public void onRenameClicked() {
        renameApplication();
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler renameAppLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            renameApplication();
        }
    };

    /** Renames application. */
    private void renameApplication() {
        final String newName = view.getName();
        String projectId = resourceProvider.getActiveProject().getId();

        try {
            service.renameApplication(resourceProvider.getVfsId(), projectId, applicationName, null, newName,
                                      new AppfogAsyncRequestCallback<String>(null, renameAppLoggedInHandler, null, eventBus, constant,
                                                                             console, loginPresenter) {
                                          @Override
                                          protected void onSuccess(String result) {
                                              view.close();

                                              console.print(constant.renameApplicationSuccess(applicationName, newName));
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
    public void showDialog() {
        view.setName(applicationName);
        view.selectValueInRenameField();
        view.setEnableRenameButton(false);

        getApplicationInfo();
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler appInfoLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            getApplicationInfo();
        }
    };

    /** Get the application's information. */
    private void getApplicationInfo() {
        String projectId = resourceProvider.getActiveProject().getId();

        try {
            AutoBean<AppfogApplication> appfogApplication = autoBeanFactory.appfogApplication();
            AutoBeanUnmarshaller<AppfogApplication> unmarshaller = new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

            service.getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, appInfoLoggedInHandler, null,
                                                                                         eventBus, constant, console, loginPresenter) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               applicationName = result.getName();
                                               view.showDialog();
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}