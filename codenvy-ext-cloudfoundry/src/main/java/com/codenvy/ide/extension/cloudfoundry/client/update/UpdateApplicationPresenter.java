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
package com.codenvy.ide.extension.cloudfoundry.client.update;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryApplicationUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.extension.builder.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.builder.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.builder.client.event.ProjectBuiltHandler;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.Resource;
import com.google.gwt.http.client.RequestException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * Presenter for update application operation.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: OperationsApplicationPresenter.java Jul 14, 2011 11:51:13 AM vereshchaka $
 */
@Singleton
public class UpdateApplicationPresenter implements ProjectBuiltHandler {
    /** Location of war file (Java only). */
    private String                              warUrl;
    private EventBus                            eventBus;
    private ResourceProvider                    resourceProvider;
    private ConsolePart                         console;
    private CloudFoundryLocalizationConstant    constant;
    private HandlerRegistration                 projectBuildHandler;
    private LoginPresenter                      loginPresenter;
    private CloudFoundryClientService           service;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider;

    /**
     * Create presenter.
     *
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param constant
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected UpdateApplicationPresenter(EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                         CloudFoundryLocalizationConstant constant, LoginPresenter loginPresenter,
                                         CloudFoundryClientService service) {
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler loggedInHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            updateApplication();
        }
    };

    /** Updates CloudFoundry application. */
    public void updateApp(CloudFoundryExtension.PAAS_PROVIDER paasProvider) {
        this.paasProvider = paasProvider;

        validateData();
    }

    /** Updates application. */
    private void updateApplication() {
        final String projectId = resourceProvider.getActiveProject().getId();

        try {
            service.updateApplication(resourceProvider.getVfsId(), projectId, null, null, warUrl,
                                      new CloudFoundryAsyncRequestCallback<String>(null, loggedInHandler, null, eventBus, console, constant,
                                                                                   loginPresenter, paasProvider) {
                                          @Override
                                          protected void onSuccess(String result) {
                                              CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller();
                                              try {
                                                  service.getApplicationInfo(resourceProvider.getVfsId(), projectId, null, null,
                                                                             new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(
                                                                                     unmarshaller, null, null, eventBus, console, constant,
                                                                                     loginPresenter, paasProvider) {
                                                                                 @Override
                                                                                 protected void onSuccess(CloudFoundryApplication result) {
                                                                                     console.print(constant.updateApplicationSuccess(
                                                                                             result.getName()));
                                                                                 }
                                                                             });
                                              } catch (RequestException e) {
                                                  eventBus.fireEvent(new ExceptionThrownEvent(e));
                                                  console.print(e.getMessage());
                                              }
                                          }
                                      });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        projectBuildHandler.removeHandler();
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            updateApplication();
        }
    }

    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler validateHandler = new LoggedInHandler() {
        @Override
        public void onLoggedIn() {
            validateData();
        }
    };

    /** Validate action before building project. */
    private void validateData() {
        final String projectId = resourceProvider.getActiveProject().getId();

        try {
            service.validateAction("update", null, null, null, null, resourceProvider.getVfsId(), projectId, paasProvider, 0, 0, false,
                                   new CloudFoundryAsyncRequestCallback<String>(null, validateHandler, null, eventBus, console, constant,
                                                                                loginPresenter, paasProvider) {
                                       @Override
                                       protected void onSuccess(String result) {
                                           isBuildApplication();
                                       }
                                   });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** Check, is work directory contains <code>pom.xml</code> file. */
    private void isBuildApplication() {
        final Project project = resourceProvider.getActiveProject();

        JsonArray<Resource> children = project.getChildren();

        for (int i = 0; i < children.size(); i++) {
            Resource child = children.get(i);
            if (child.isFile() && "pom.xml".equals(child.getName())) {
                buildApplication();
                return;
            }
        }
        warUrl = null;
        updateApplication();
    }

    /** Builds application. */
    private void buildApplication() {
        // TODO IDEX-57
        // Replace EventBus Events with direct method calls and DI
        projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
        eventBus.fireEvent(new BuildProjectEvent());
    }
}