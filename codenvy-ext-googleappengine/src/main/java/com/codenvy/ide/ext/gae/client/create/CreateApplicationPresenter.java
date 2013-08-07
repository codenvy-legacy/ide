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
package com.codenvy.ide.ext.gae.client.create;

import com.codenvy.ide.api.event.ResourceChangedEvent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.gae.client.GAEAsyncRequestCallback;
import com.codenvy.ide.ext.gae.client.GAEClientService;
import com.codenvy.ide.ext.gae.client.GAEExtension;
import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.client.actions.LoginAction;
import com.codenvy.ide.ext.gae.client.marshaller.ApplicationInfoUnmarshaller;
import com.codenvy.ide.ext.gae.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.gae.shared.ApplicationInfo;
import com.codenvy.ide.ext.java.client.JavaExtension;
import com.codenvy.ide.extension.maven.client.event.BuildProjectEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltEvent;
import com.codenvy.ide.extension.maven.client.event.ProjectBuiltHandler;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.util.Utils;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.UrlBuilder;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Singleton
public class CreateApplicationPresenter implements CreateApplicationView.ActionDelegate, ProjectBuiltHandler {
    private CreateApplicationView view;
    private EventBus              eventBus;
    private ConsolePart           console;
    private GAEClientService      service;
    private GAELocalization       constant;
    private ResourceProvider      resourceProvider;
    private String                restContext;
    private LoginAction           loginAction;
    private HandlerRegistration   projectBuildHandler;
    private Project               project;
    private String                warUrl;

    @Inject
    public CreateApplicationPresenter(CreateApplicationView view, EventBus eventBus, ConsolePart console,
                                      GAEClientService service, GAELocalization constant,
                                      ResourceProvider resourceProvider, @Named("restContext") String restContext,
                                      LoginAction loginAction) {
        this.view = view;
        this.eventBus = eventBus;
        this.console = console;
        this.service = service;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
        this.restContext = restContext;
        this.loginAction = loginAction;

        this.view.setDelegate(this);
    }

    public void showDialog(Project project) {
        this.project = project;

        view.enableDeployButton(false);
        view.enableCreateButton(true);
        view.setUserInstruction(constant.createApplicationInstruction());

        if (!view.isShown()) {
            view.showDialog();
        }
    }

    @Override
    public void onCreateApplicationButtonClicked() {
        final AsyncCallback<Boolean> onLoggedIn = new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                //ignore
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    onCreateApplicationButtonClicked();
                } else {
                    Window.alert(
                            "You aren't allowed to create application on Google App Engine without authorization.");
                }
            }
        };

        AsyncCallback<Boolean> onIfUserLoggedIn = new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                //ignore
            }

            @Override
            public void onSuccess(Boolean userLoggedIn) {
                if (userLoggedIn) {
                    doCreateApplication();
                } else {
                    loginAction.doLogin(onLoggedIn);
                }
            }
        };

        loginAction.isUserLoggedIn(onIfUserLoggedIn);
    }

    private void doCreateApplication() {
        //todo need to check if appengine-web.xml exist in java-based project for normal deploy

        view.enableDeployButton(true);
        view.enableCreateButton(false);
        view.setUserInstruction(constant.deployApplicationInstruction());

        final String projectId =
                resourceProvider.getActiveProject() != null ? resourceProvider.getActiveProject().getId()
                                                            : null;
        final String vfsId = resourceProvider.getVfsId();

        UrlBuilder builder = new UrlBuilder();
        String redirectUrl = builder.setProtocol(Window.Location.getProtocol())
                                    .setHost(Window.Location.getHost())
                                    .setPath(restContext + '/' + Utils.getWorkspaceName() +
                                             "/appengine/change-appid/" +
                                             vfsId + '/' +
                                             projectId)
                                    .buildString();

        String url = GAEExtension.CREATE_APP_URL + "?redirect_url=" + redirectUrl;

        openNativeWindow(url);
    }

    @Override
    public void onDeployApplicationButtonClicked() {
        if (GAEExtension.isAppEngineProject(project)) {
            deploy();
        } else {
            Window.alert(constant.createApplicationCannotDeploy());
        }

        view.close();
    }

    private static native void openNativeWindow(String url) /*-{
        $wnd.open(url, "_blank");
    }-*/;

    @Override
    public void onCancelButtonClicked() {
        view.close();
    }

    public void deploy(Project project) {
        this.project = project;
        deploy();
    }

    public void deploy() {
        String projectType = (String)project.getPropertyValue("vfs:projectType");
        if (projectType.equals(JavaExtension.JAVA_WEB_APPLICATION_PROJECT_TYPE)) {
            startBuildingApplication();
        } else {
            uploadApplication();
        }
    }

    private void startBuildingApplication() {
        projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
        eventBus.fireEvent(new BuildProjectEvent(project));
    }

    private void uploadApplication() {
        DtoClientImpls.ApplicationInfoImpl applicationInfo = DtoClientImpls.ApplicationInfoImpl.make();
        ApplicationInfoUnmarshaller unmarshaller = new ApplicationInfoUnmarshaller(applicationInfo);

        final String vfsId = resourceProvider.getVfsId();

        try {
            service.update(vfsId, project, warUrl,
                           new GAEAsyncRequestCallback<ApplicationInfo>(unmarshaller, console, eventBus, constant,
                                                                        loginAction) {
                               @Override
                               protected void onSuccess(ApplicationInfo result) {
                                   console.print(constant.deployApplicationSuccess(project.getName(),
                                                                                   "<a href='" + result.getWebURL() +
                                                                                   "' target='_blank'>" +
                                                                                   result.getWebURL() + "</a>"));

                                   eventBus.fireEvent(ResourceChangedEvent.createResourceTreeRefreshedEvent(project));
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   console.print(exception.getMessage());
                                   super.onFailure(exception);
                               }
                           });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        projectBuildHandler.removeHandler();
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            uploadApplication();
        }
    }
}
