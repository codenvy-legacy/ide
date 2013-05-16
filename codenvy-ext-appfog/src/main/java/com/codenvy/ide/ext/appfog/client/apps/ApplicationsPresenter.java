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
package com.codenvy.ide.ext.appfog.client.apps;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.*;
import com.codenvy.ide.ext.appfog.client.delete.DeleteApplicationPresenter;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.ApplicationListUnmarshaller;
import com.codenvy.ide.ext.appfog.client.marshaller.TargetsUnmarshaller;
import com.codenvy.ide.ext.appfog.client.start.StartApplicationPresenter;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The applications presenter manager AppFog application.
 * The presenter can start, stop, update, delete application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class ApplicationsPresenter implements ApplicationsView.ActionDelegate {
    private ApplicationsView           view;
    private EventBus                   eventBus;
    private ConsolePart                console;
    private AppfogLocalizationConstant constant;
    private AppfogAutoBeanFactory      autoBeanFactory;
    private LoginPresenter             loginPresenter;
    private StartApplicationPresenter  startApplicationPresenter;
    private DeleteApplicationPresenter deleteApplicationPresenter;
    private AppfogClientService        service;
    private JsonArray<String>          servers;
    private String                     currentServer;

    /** The callback what execute when some application's information was changed. */
    private AsyncCallback<String> appInfoChangedCallback = new AsyncCallback<String>() {
        @Override
        public void onSuccess(String result) {
            getApplicationList();
        }

        @Override
        public void onFailure(Throwable caught) {
            Log.error(ApplicationsPresenter.class, "Can not change information", caught);
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
     * @param startApplicationPresenter
     * @param deleteApplicationPresenter
     */
    @Inject
    protected ApplicationsPresenter(ApplicationsView view, EventBus eventBus, ConsolePart console,
                                    AppfogLocalizationConstant constant, AppfogAutoBeanFactory autoBeanFactory,
                                    LoginPresenter loginPresenter, AppfogClientService service,
                                    StartApplicationPresenter startApplicationPresenter,
                                    DeleteApplicationPresenter deleteApplicationPresenter) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.startApplicationPresenter = startApplicationPresenter;
        this.deleteApplicationPresenter = deleteApplicationPresenter;
    }

    /** Show dialog. */
    public void showDialog() {
        checkLogginedToServer();
    }

    private void checkLogginedToServer() {
        try {
            TargetsUnmarshaller unmarshaller = new TargetsUnmarshaller(JsonCollections.<String>createArray());
            service.getTargets(new AsyncRequestCallback<JsonArray<String>>(unmarshaller) {
                @Override
                protected void onSuccess(JsonArray<String> result) {
                    if (result.isEmpty()) {
                        servers = JsonCollections.createArray(AppFogExtension.DEFAULT_SERVER);
                    } else {
                        servers = result;
                    }
                    // open view
                    openView();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    console.print(exception.getMessage());
                    eventBus.fireEvent(new ExceptionThrownEvent(exception));
                }
            });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Opens view. */
    private void openView() {
        view.setTarget(AppFogExtension.DEFAULT_SERVER);
        // fill the list of applications
        currentServer = servers.get(0);
        getApplicationList();

        if (!view.isShown()) {
            view.showDialog();
        }
    }

    /** Gets list of available application for current user. */
    private void getApplicationList() {
        try {
            ApplicationListUnmarshaller unmarshaller =
                    new ApplicationListUnmarshaller(JsonCollections.<AppfogApplication>createArray(), autoBeanFactory);
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    getApplicationList();
                }
            };

            service.getApplicationList(view.getTarget(),
                                       new AppfogAsyncRequestCallback<JsonArray<AppfogApplication>>(unmarshaller, loggedInHandler, null,
                                                                                                    view.getTarget(), eventBus, constant,
                                                                                                    console, loginPresenter) {
                                           @Override
                                           protected void onSuccess(JsonArray<AppfogApplication> result) {
                                               view.setApplications(result);
                                               view.setTarget(AppFogExtension.DEFAULT_SERVER);
                                           }
                                       });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onShowClicked() {
        checkLogginedToServer();
    }

    /** {@inheritDoc} */
    @Override
    public void onStartClicked(AppfogApplication app) {
        startApplicationPresenter.startApp(app.getName(), appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onStopClicked(AppfogApplication app) {
        startApplicationPresenter.stopApp(app.getName(), appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onRestartClicked(AppfogApplication app) {
        startApplicationPresenter.restartApp(app.getName(), appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked(AppfogApplication app) {
        deleteApplicationPresenter.deleteApp(app.getName(), currentServer, appInfoChangedCallback);
    }
}