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
package com.codenvy.ide.ext.appfog.client.url;

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
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for unmaping (unregistering) URLs from application.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class UnmapUrlPresenter implements UnmapUrlView.ActionDelegate {
    private UnmapUrlView               view;
    private JsonArray<String>          registeredUrls;
    private String                     unregisterUrl;
    private String                     urlToMap;
    private ResourceProvider           resourceProvider;
    private EventBus                   eventBus;
    private ConsolePart                console;
    private AppfogLocalizationConstant constant;
    private AppfogAutoBeanFactory      autoBeanFactory;
    private AsyncCallback<String>      unmapUrlCallback;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;
    private boolean isBindingChanged = false;

    /**
     * Create presenter.
     *
     * @param view
     * @param resourceProvider
     * @param eventBus
     * @param console
     * @param constant
     * @param autoBeanFactory
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected UnmapUrlPresenter(UnmapUrlView view, ResourceProvider resourceProvider, EventBus eventBus, ConsolePart console,
                                AppfogLocalizationConstant constant, AppfogAutoBeanFactory autoBeanFactory, LoginPresenter loginPresenter,
                                AppfogClientService service) {
        this.view = view;
        this.view.setDelegate(this);
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        if (isBindingChanged) {
            String projectId = resourceProvider.getActiveProject().getId();
            unmapUrlCallback.onSuccess(projectId);
        }

        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onMapUrlClicked() {
        urlToMap = view.getMapUrl();
        for (int i = 0; i < registeredUrls.size(); i++) {
            String url = registeredUrls.get(i);
            if (url.equals(urlToMap) || ("http://" + url).equals(urlToMap)) {
                Window.alert(constant.mapUrlAlredyRegistered());
                return;
            }
        }

        if (urlToMap.startsWith("http://")) {
            urlToMap = urlToMap.substring(7);
        }

        mapUrl(urlToMap);
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    private LoggedInHandler mapUrlLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            mapUrl(urlToMap);
        }
    };

    /**
     * Maps url.
     *
     * @param url
     */
    private void mapUrl(final String url) {
        Project project = resourceProvider.getActiveProject();

        final String server = project.getProperty("appfog-target").getValue().get(0);
        final String appName = project.getProperty("appfog-application").getValue().get(0);

        try {
            service.mapUrl(null, null, appName, server, url,
                           new AppfogAsyncRequestCallback<String>(null, mapUrlLoggedInHandler, null, eventBus, constant, console,
                                                                  loginPresenter) {
                               @Override
                               protected void onSuccess(String result) {
                                   isBindingChanged = true;
                                   String registeredUrl = url;
                                   if (!url.startsWith("http")) {
                                       registeredUrl = "http://" + url;
                                   }
                                   registeredUrl = "<a href=\"" + registeredUrl + "\" target=\"_blank\">" + registeredUrl + "</a>";
                                   String msg = constant.mapUrlRegisteredSuccess(registeredUrl);
                                   console.print(msg);
                                   registeredUrls.add(url);
                                   view.setRegisteredUrls(registeredUrls);
                                   view.setMapUrl("");
                               }
                           });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onUnMapUrlClicked(String url) {
        askForUnmapUrl(url);
    }

    /**
     * Checking really need to unregister url before unregistre.
     *
     * @param url
     */
    private void askForUnmapUrl(final String url) {
        if (Window.confirm(constant.unmapUrlConfirmationDialogMessage())) {
            unregisterUrl = url;
            unregisterUrl(unregisterUrl);
        }
    }

    /** If user is not logged in to AppFog, this handler will be called, after user logged in. */
    LoggedInHandler unregisterUrlLoggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            unregisterUrl(unregisterUrl);
        }
    };

    private void unregisterUrl(final String url) {
        Project project = resourceProvider.getActiveProject();

        final String server = project.getProperty("appfog-target").getValue().get(0);
        final String appName = project.getProperty("appfog-application").getValue().get(0);

        try {
            service.unmapUrl(null, null, appName, server, url,
                             new AppfogAsyncRequestCallback<Object>(null, unregisterUrlLoggedInHandler, null, eventBus, constant, console,
                                                                    loginPresenter) {
                                 @Override
                                 protected void onSuccess(Object result) {
                                     isBindingChanged = true;
                                     registeredUrls.remove(url);
                                     view.setRegisteredUrls(registeredUrls);
                                     String unmappedUrl = url;
                                     if (!unmappedUrl.startsWith("http")) {
                                         unmappedUrl = "http://" + unmappedUrl;
                                     }
                                     String msg = constant.unmapUrlSuccess(unmappedUrl);
                                     console.print(msg);
                                 }
                             });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onMapUrlChanged() {
        view.setEnableMapUrlButton(view.getMapUrl() != null && !view.getMapUrl().isEmpty());
    }

    /**
     * Shows dialog.
     *
     * @param callback
     */
    public void showDialog(AsyncCallback<String> callback) {
        this.unmapUrlCallback = callback;

        getAppRegisteredUrls();
    }

    /** Gets registered urls. */
    private void getAppRegisteredUrls() {
        String projectId = resourceProvider.getActiveProject().getId();

        try {
            AutoBean<AppfogApplication> appfogApplication = autoBeanFactory.appfogApplication();
            AutoBeanUnmarshaller<AppfogApplication> unmarshaller = new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);

            service.getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, null, null, eventBus, constant,
                                                                                         console, loginPresenter) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               isBindingChanged = false;

                                               registeredUrls = JsonCollections.createArray(result.getUris());

                                               view.setEnableMapUrlButton(false);
                                               view.setRegisteredUrls(registeredUrls);
                                               view.setMapUrl("");

                                               view.showDialog();
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }
}