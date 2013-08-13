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
package com.codenvy.ide.ext.appfog.client.project;

import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.delete.DeleteApplicationPresenter;
import com.codenvy.ide.ext.appfog.client.info.ApplicationInfoPresenter;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshaller;
import com.codenvy.ide.ext.appfog.client.marshaller.StringUnmarshaller;
import com.codenvy.ide.ext.appfog.client.services.ManageServicesPresenter;
import com.codenvy.ide.ext.appfog.client.start.StartApplicationPresenter;
import com.codenvy.ide.ext.appfog.client.update.UpdateApplicationPresenter;
import com.codenvy.ide.ext.appfog.client.update.UpdatePropertiesPresenter;
import com.codenvy.ide.ext.appfog.client.url.UnmapUrlPresenter;
import com.codenvy.ide.ext.appfog.dto.client.DtoClientImpls;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for managing project, deployed on Appfog.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class AppFogProjectPresenter implements AppFogProjectView.ActionDelegate {
    private AppFogProjectView          view;
    private UnmapUrlPresenter          unmapUrlPresenter;
    private UpdatePropertiesPresenter  updateProperyPresenter;
    private ManageServicesPresenter    manageServicesPresenter;
    private UpdateApplicationPresenter updateApplicationPresenter;
    private EventBus                   eventBus;
    private ResourceProvider           resourceProvider;
    private ConsolePart                console;
    private AppfogApplication          application;
    private AppfogLocalizationConstant constant;
    private StartApplicationPresenter  startAppPresenter;
    private DeleteApplicationPresenter deleteAppPresenter;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;
    private ApplicationInfoPresenter   applicationInfoPresenter;
    /** The callback what execute when some application's information was changed. */
    private AsyncCallback<String> appInfoChangedCallback = new AsyncCallback<String>() {
        @Override
        public void onSuccess(String result) {
            Project openedProject = resourceProvider.getActiveProject();
            if (result != null && openedProject != null && openedProject.getId().equals(result)) {
                getApplicationInfo(openedProject);
            }
        }

        @Override
        public void onFailure(Throwable caught) {
            Log.error(AppFogProjectPresenter.class, "Can not change  information", caught);
        }
    };

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param constant
     * @param startAppPresenter
     * @param deleteAppPresenter
     * @param loginPresenter
     * @param service
     * @param applicationInfoPresenter
     * @param manageServicesPresenter
     * @param updateProperyPresenter
     * @param updateApplicationPresenter
     * @param unmapUrlPresenter
     */
    @Inject
    protected AppFogProjectPresenter(AppFogProjectView view, EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                     AppfogLocalizationConstant constant, StartApplicationPresenter startAppPresenter,
                                     DeleteApplicationPresenter deleteAppPresenter, LoginPresenter loginPresenter,
                                     AppfogClientService service, ApplicationInfoPresenter applicationInfoPresenter,
                                     ManageServicesPresenter manageServicesPresenter, UpdatePropertiesPresenter updateProperyPresenter,
                                     UpdateApplicationPresenter updateApplicationPresenter, UnmapUrlPresenter unmapUrlPresenter) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.startAppPresenter = startAppPresenter;
        this.deleteAppPresenter = deleteAppPresenter;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.applicationInfoPresenter = applicationInfoPresenter;
        this.manageServicesPresenter = manageServicesPresenter;
        this.updateProperyPresenter = updateProperyPresenter;
        this.updateApplicationPresenter = updateApplicationPresenter;
        this.unmapUrlPresenter = unmapUrlPresenter;
    }

    /** Shows dialog. */
    public void showDialog() {
        getApplicationInfo(resourceProvider.getActiveProject());
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onUpdateClicked() {
        updateApplicationPresenter.updateApp();
    }

    /** {@inheritDoc} */
    @Override
    public void onLogsClicked() {
        getLogs();
    }

    /** Getting logs for AppFog Application. */
    protected void getLogs() {
        try {
            StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());
            service.getLogs(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(),
                            new AsyncRequestCallback<StringBuilder>(unmarshaller) {
                                @Override
                                protected void onSuccess(StringBuilder result) {
                                    console.print("<pre>" + result.toString() + "</pre>");
                                }

                                @Override
                                protected void onFailure(Throwable exception) {
                                    eventBus.fireEvent(new ExceptionThrownEvent(exception.getMessage()));
                                    console.print(exception.getMessage());
                                }
                            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e.getMessage()));
            console.print(e.getMessage());
            e.printStackTrace();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onServicesClicked() {
        manageServicesPresenter.showDialog(application);
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        deleteAppPresenter.deleteApp(null, null, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Project openedProject = resourceProvider.getActiveProject();
                if (result != null && openedProject != null
                    && result.equals(openedProject.getPropertyValue("appfog-application"))) {
                    eventBus.fireEvent(new RefreshBrowserEvent(openedProject));
                }
                if (view.isShown()) {
                    view.close();
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(AppFogProjectPresenter.class, "Can not delete application", caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onInfoClicked() {
        applicationInfoPresenter.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onStartClicked() {
        startAppPresenter.startApp(null, appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onStopClicked() {
        startAppPresenter.stopApp(null, appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onRestartClicked() {
        startAppPresenter.restartApp(null, appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onEditMemoryClicked() {
        updateProperyPresenter.showUpdateMemoryDialog(appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onEditUrlClicked() {
        unmapUrlPresenter.showDialog(appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onEditInstancesClicked() {
        updateProperyPresenter.showUpdateInstancesDialog(appInfoChangedCallback);
    }

    /**
     * Get application properties.
     *
     * @param project
     */
    protected void getApplicationInfo(final Project project) {
        DtoClientImpls.AppfogApplicationImpl appfogApplication = DtoClientImpls.AppfogApplicationImpl.make();
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller(appfogApplication);
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getApplicationInfo(project);
            }
        };

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), project.getId(), null, null,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, loggedInHandler, null, eventBus,
                                                                                         constant, console, loginPresenter) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               if (!view.isShown()) {
                                                   view.showDialog();
                                               }

                                               application = result;
                                               displayApplicationProperties(result);
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Displays application's properties on the view.
     *
     * @param application
     *         current application
     */
    protected void displayApplicationProperties(AppfogApplication application) {
        view.setApplicationName(application.getName());
        view.setApplicationInstances(String.valueOf(application.getInstances()));
        view.setApplicationMemory(String.valueOf(application.getResources().getMemory()) + "MB");
        view.setApplicationInfra(String.valueOf(application.getInfra().getProvider()));
        view.setApplicationModel(String.valueOf(application.getStaging().getModel()));
        view.setApplicationStack(String.valueOf(application.getStaging().getStack()));
        view.setApplicationStatus(String.valueOf(application.getState()));

        if (application.getUris() != null && application.getUris().size() > 0) {
            view.setApplicationUrl(application.getUris().get(0));
        } else {
            //Set empty field if we specialy unmap all urls and closed url controller window, if whe don't do this, in
            //info window will be appear old url, that is not good
            view.setApplicationUrl(null);
        }
        boolean isStarted = ("STARTED".equals(application.getState()));
        view.setEnabledStartButton(!isStarted);
        view.setEnabledStopButton(isStarted);
    }
}