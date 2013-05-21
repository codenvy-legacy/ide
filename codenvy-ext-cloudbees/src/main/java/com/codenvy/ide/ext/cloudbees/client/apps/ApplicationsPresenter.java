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
package com.codenvy.ide.ext.cloudbees.client.apps;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAsyncRequestCallback;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesAutoBeanFactory;
import com.codenvy.ide.ext.cloudbees.client.CloudBeesClientService;
import com.codenvy.ide.ext.cloudbees.client.delete.DeleteApplicationPresenter;
import com.codenvy.ide.ext.cloudbees.client.info.ApplicationInfoPresenter;
import com.codenvy.ide.ext.cloudbees.client.login.LoggedInHandler;
import com.codenvy.ide.ext.cloudbees.client.login.LoginPresenter;
import com.codenvy.ide.ext.cloudbees.client.marshaller.ApplicationListUnmarshaller;
import com.codenvy.ide.ext.cloudbees.shared.ApplicationInfo;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * The applications presenter manager CloudBees application.
 * The presenter can delete application and show information about it.
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2011 evgen $
 */
@Singleton
public class ApplicationsPresenter implements ApplicationsView.ActionDelegate {
    private ApplicationsView           view;
    private EventBus                   eventBus;
    private ConsolePart                console;
    private CloudBeesAutoBeanFactory   autoBeanFactory;
    private LoginPresenter             loginPresenter;
    private CloudBeesClientService     service;
    private ApplicationInfoPresenter   applicationInfoPresenter;
    private DeleteApplicationPresenter deleteApplicationPresenter;

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param console
     * @param autoBeanFactory
     * @param loginPresenter
     * @param service
     * @param applicationInfoPresenter
     * @param deleteApplicationPresenter
     */
    @Inject
    protected ApplicationsPresenter(ApplicationsView view, EventBus eventBus, ConsolePart console, CloudBeesAutoBeanFactory autoBeanFactory,
                                    LoginPresenter loginPresenter, CloudBeesClientService service,
                                    ApplicationInfoPresenter applicationInfoPresenter,
                                    DeleteApplicationPresenter deleteApplicationPresenter) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.console = console;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.applicationInfoPresenter = applicationInfoPresenter;
        this.deleteApplicationPresenter = deleteApplicationPresenter;
    }

    /** Show dialog. */
    public void showDialog() {
        getOrUpdateAppList();
    }

    /** Gets list of available application for current user. */
    private void getOrUpdateAppList() {
        try {
            ApplicationListUnmarshaller unmarshaller =
                    new ApplicationListUnmarshaller(autoBeanFactory, JsonCollections.<ApplicationInfo>createArray());
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    getOrUpdateAppList();
                }
            };

            service.applicationList(
                    new CloudBeesAsyncRequestCallback<JsonArray<ApplicationInfo>>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                  console, loginPresenter) {
                        @Override
                        protected void onSuccess(JsonArray<ApplicationInfo> result) {
                            view.setApplications(result);

                            if (!view.isShown()) {
                                view.showDialog();
                            }
                        }
                    });
        } catch (RequestException e) {
            console.print(e.getMessage());
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onOkClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onInfoClicked(ApplicationInfo app) {
        applicationInfoPresenter.showDialog(app);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked(ApplicationInfo app) {
        deleteApplicationPresenter.deleteApp(app.getId(), app.getTitle(), new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                getOrUpdateAppList();
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ApplicationsPresenter.class, "Can not delete application", caught);
            }
        });
    }
}