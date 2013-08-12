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
 * Presenter that allow user to create application on Google App Engine.
 *
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

    /**
     * Constructor for Create Application Presenter.
     */
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

    /**
     * Shows current dialog window.
     *
     * @param project
     *         opened project in current moment.
     */
    public void showDialog(Project project) {
        this.project = project;

        view.enableDeployButton(false);
        view.enableCreateButton(true);
        view.setUserInstruction(constant.createApplicationInstruction());

        if (!view.isShown()) {
            view.showDialog();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateApplicationButtonClicked() {
        loginAction.isUserLoggedIn(onIfUserLoggedIn);
    }

    /**
     * Handler to programmaticaly click on create application button when user logged in on Google Services.
     */
    private final AsyncCallback<Boolean> onLoggedIn = new AsyncCallback<Boolean>() {
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

    /**
     * Handler to run creating application when user logged in. When user isn't authorize on Google Services, login
     * window opened to authorize user.
     */
    private final AsyncCallback<Boolean> onIfUserLoggedIn = new AsyncCallback<Boolean>() {
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

    /**
     * Fetch current opened project and open new tab in browser to allow user create application on Google App Engine.
     * <p/>
     * TODO: need to check if appengine-web.xml exist in java-based project for normal deploy.
     */
    private void doCreateApplication() {
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

    /** {@inheritDoc} */
    @Override
    public void onDeployApplicationButtonClicked() {
        if (GAEExtension.isAppEngineProject(project)) {
            deploy();
        } else {
            Window.alert(constant.createApplicationCannotDeploy());
        }

        view.close();
    }

    /**
     * Open new tab in browser with specified URL.
     *
     * @param url
     *         url address.
     */
    private static native void openNativeWindow(String url) /*-{
        $wnd.open(url, "_blank");
    }-*/;

    /** {@inheritDoc} */
    @Override
    public void onCancelButtonClicked() {
        view.close();
    }

    /**
     * Start deploying specified project to Google App Engine.
     *
     * @param project
     *         project to deploy.
     */
    public void deploy(Project project) {
        this.project = project;
        deploy();
    }

    /**
     * Start deploying opened project in current moment.
     */
    public void deploy() {
        String projectType = (String)project.getPropertyValue("vfs:projectType");
        if (projectType.equals(JavaExtension.JAVA_WEB_APPLICATION_PROJECT_TYPE)) {
            startBuildingApplication();
        } else {
            uploadApplication();
        }
    }

    /**
     * Starts building project.
     */
    private void startBuildingApplication() {
        projectBuildHandler = eventBus.addHandler(ProjectBuiltEvent.TYPE, this);
        eventBus.fireEvent(new BuildProjectEvent(project));
    }

    /**
     * Starts upload application to Google App Engine.
     */
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

    /** {@inheritDoc} */
    @Override
    public void onProjectBuilt(ProjectBuiltEvent event) {
        projectBuildHandler.removeHandler();
        if (event.getBuildStatus().getDownloadUrl() != null) {
            warUrl = event.getBuildStatus().getDownloadUrl();
            uploadApplication();
        }
    }
}
