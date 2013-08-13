/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.appfog.client.url;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshaller;
import com.codenvy.ide.ext.appfog.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected UnmapUrlPresenter(UnmapUrlView view, ResourceProvider resourceProvider, EventBus eventBus, ConsolePart console,
                                AppfogLocalizationConstant constant, LoginPresenter loginPresenter, AppfogClientService service) {
        this.view = view;
        this.view.setDelegate(this);
        this.resourceProvider = resourceProvider;
        this.eventBus = eventBus;
        this.console = console;
        this.constant = constant;
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
        DtoClientImpls.AppfogApplicationImpl appfogApplication = DtoClientImpls.AppfogApplicationImpl.make();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller(appfogApplication);

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, null, null, eventBus, constant,
                                                                                         console, loginPresenter) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               isBindingChanged = false;
                                               registeredUrls = result.getUris();

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