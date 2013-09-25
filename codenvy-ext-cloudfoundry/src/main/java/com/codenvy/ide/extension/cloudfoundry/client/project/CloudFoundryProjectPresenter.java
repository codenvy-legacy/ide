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
package com.codenvy.ide.extension.cloudfoundry.client.project;

import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.delete.DeleteApplicationPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.info.ApplicationInfoPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryApplicationUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.StringUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.services.ManageServicesPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.start.StartApplicationPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.update.UpdateApplicationPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.update.UpdatePropertiesPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.url.UnmapUrlPresenter;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for managing project, deployed on CloudFoundry.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Dec 2, 2011 5:54:50 PM anya $
 */
@Singleton
public class CloudFoundryProjectPresenter implements CloudFoundryProjectView.ActionDelegate {
    private CloudFoundryProjectView             view;
    private ApplicationInfoPresenter            applicationInfoPresenter;
    private UnmapUrlPresenter                   unmapUrlPresenter;
    private UpdatePropertiesPresenter           updateProperyPresenter;
    private ManageServicesPresenter             manageServicesPresenter;
    private UpdateApplicationPresenter          updateApplicationPresenter;
    private EventBus                            eventBus;
    private ResourceProvider                    resourceProvider;
    private ConsolePart                         console;
    private CloudFoundryApplication             application;
    private CloudFoundryLocalizationConstant    constant;
    private StartApplicationPresenter           startAppPresenter;
    private DeleteApplicationPresenter          deleteAppPresenter;
    private LoginPresenter                      loginPresenter;
    private CloudFoundryClientService           service;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider;

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
            Log.error(CloudFoundryProjectPresenter.class, "Can not change  information", caught);
        }
    };

    /**
     * Create presenter.
     *
     * @param view
     * @param applicationInfoPresenter
     * @param unmapUrlPresenter
     * @param updateProperyPresenter
     * @param manageServicesPresenter
     * @param updateApplicationPresenter
     * @param eventBus
     * @param resourceProvider
     * @param console
     * @param constant
     * @param startAppPresenter
     * @param deleteAppPresenter
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected CloudFoundryProjectPresenter(CloudFoundryProjectView view, ApplicationInfoPresenter applicationInfoPresenter,
                                           UnmapUrlPresenter unmapUrlPresenter, UpdatePropertiesPresenter updateProperyPresenter,
                                           ManageServicesPresenter manageServicesPresenter,
                                           UpdateApplicationPresenter updateApplicationPresenter, EventBus eventBus,
                                           ResourceProvider resourceProvider, ConsolePart console,
                                           CloudFoundryLocalizationConstant constant, StartApplicationPresenter startAppPresenter,
                                           DeleteApplicationPresenter deleteAppPresenter, LoginPresenter loginPresenter,
                                           CloudFoundryClientService service) {
        this.view = view;
        this.view.setDelegate(this);
        this.applicationInfoPresenter = applicationInfoPresenter;
        this.unmapUrlPresenter = unmapUrlPresenter;
        this.updateProperyPresenter = updateProperyPresenter;
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.manageServicesPresenter = manageServicesPresenter;
        this.updateApplicationPresenter = updateApplicationPresenter;
        this.constant = constant;
        this.startAppPresenter = startAppPresenter;
        this.deleteAppPresenter = deleteAppPresenter;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** Shows dialog. */
    public void showDialog(CloudFoundryExtension.PAAS_PROVIDER paasProvider) {
        this.paasProvider = paasProvider;

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
        updateApplicationPresenter.updateApp(paasProvider);
    }

    /** {@inheritDoc} */
    @Override
    public void onLogsClicked() {
        getLogs();
    }

    /** Getting logs for CloudFoundry Application. */
    protected void getLogs() {
        try {
            StringUnmarshaller unmarshaller = new StringUnmarshaller();
            service.getLogs(resourceProvider.getVfsId(), resourceProvider.getActiveProject().getId(),
                            new AsyncRequestCallback<String>(unmarshaller) {
                                @Override
                                protected void onSuccess(String result) {
                                    console.print("<pre>" + result + "</pre>");
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
        manageServicesPresenter.showDialog(application, paasProvider);
    }

    /**
     * Gets application's properties.
     *
     * @param project
     */
    protected void getApplicationInfo(final Project project) {
        CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller();
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn() {
                getApplicationInfo(project);
            }
        };

        try {
            service.getApplicationInfo(resourceProvider.getVfsId(), project.getId(), null, null,
                                       new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller, loggedInHandler, null,
                                                                                                     eventBus, console, constant,
                                                                                                     loginPresenter, paasProvider) {
                                           @Override
                                           protected void onSuccess(CloudFoundryApplication result) {
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
    protected void displayApplicationProperties(CloudFoundryApplication application) {
        view.setApplicationName(application.getName());
        view.setApplicationInstances(String.valueOf(application.getInstances()));
        view.setApplicationMemory(String.valueOf(application.getResources().getMemory()) + "MB");
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

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        deleteAppPresenter.deleteApp(null, null, paasProvider, new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Project openedProject = resourceProvider.getActiveProject();
                if (result != null && openedProject != null
                    && result.equals(openedProject.getPropertyValue("cloudfoundry-application"))) {
                    eventBus.fireEvent(new RefreshBrowserEvent(openedProject));
                }
                if (view.isShown()) {
                    view.close();
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(CloudFoundryProjectPresenter.class, "Can not delete application", caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onInfoClicked() {
        applicationInfoPresenter.showDialog(paasProvider);
    }

    /** {@inheritDoc} */
    @Override
    public void onStartClicked() {
        startAppPresenter.startApp(null, null, paasProvider, appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onStopClicked() {
        startAppPresenter.stopApp(null, null, paasProvider, appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onRestartClicked() {
        startAppPresenter.restartApp(null, null, paasProvider, appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onEditMemoryClicked() {
        updateProperyPresenter.showUpdateMemoryDialog(paasProvider, appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onEditUrlClicked() {
        unmapUrlPresenter.showDialog(paasProvider, appInfoChangedCallback);
    }

    /** {@inheritDoc} */
    @Override
    public void onEditInstancesClicked() {
        updateProperyPresenter.showUpdateInstancesDialog(paasProvider, appInfoChangedCallback);
    }
}