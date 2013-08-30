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
package com.codenvy.ide.ext.appfog.client.info;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshaller;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
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
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected ApplicationInfoPresenter(ApplicationInfoView view, EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                       AppfogLocalizationConstant constant, LoginPresenter loginPresenter, AppfogClientService service) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
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
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                showApplicationInfo(projectId);
            }
        };
        try {
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