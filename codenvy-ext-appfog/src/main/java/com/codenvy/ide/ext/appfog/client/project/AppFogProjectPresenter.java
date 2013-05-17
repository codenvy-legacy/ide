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
package com.codenvy.ide.ext.appfog.client.project;

import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogAutoBeanFactory;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.delete.DeleteApplicationPresenter;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.StringUnmarshaller;
import com.codenvy.ide.ext.appfog.client.start.StartApplicationPresenter;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.AutoBeanUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for managing project, deployed on Appfog.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class AppFogProjectPresenter implements AppFogProjectView.ActionDelegate {
    private AppFogProjectView          view;
    //    private ApplicationInfoPresenter         applicationInfoPresenter;
//    private UnmapUrlPresenter                unmapUrlPresenter;
//    private UpdatePropertiesPresenter        updateProperyPresenter;
//    private ManageServicesPresenter          manageServicesPresenter;
//    private UpdateApplicationPresenter       updateApplicationPresenter;
    private EventBus                   eventBus;
    private ResourceProvider           resourceProvider;
    private ConsolePart                console;
    private AppfogApplication          application;
    private AppfogLocalizationConstant constant;
    private AppfogAutoBeanFactory      autoBeanFactory;
    private StartApplicationPresenter  startAppPresenter;
    private DeleteApplicationPresenter deleteAppPresenter;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;

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

    @Inject
    protected AppFogProjectPresenter(AppFogProjectView view, EventBus eventBus, ResourceProvider resourceProvider, ConsolePart console,
                                     AppfogLocalizationConstant constant, AppfogAutoBeanFactory autoBeanFactory,
                                     StartApplicationPresenter startAppPresenter, DeleteApplicationPresenter deleteAppPresenter,
                                     LoginPresenter loginPresenter, AppfogClientService service) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.autoBeanFactory = autoBeanFactory;
        this.startAppPresenter = startAppPresenter;
        this.deleteAppPresenter = deleteAppPresenter;
        this.loginPresenter = loginPresenter;
        this.service = service;
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /** {@inheritDoc} */
    @Override
    public void onLogsClicked() {
        getLogs();
    }

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
        //To change body of implemented methods use File | Settings | File Templates.
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
        //To change body of implemented methods use File | Settings | File Templates.
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
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /** {@inheritDoc} */
    @Override
    public void onEditUrlClicked() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /** {@inheritDoc} */
    @Override
    public void onEditInstancesClicked() {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    /**
     * Get application properties.
     *
     * @param project
     */
    protected void getApplicationInfo(final Project project) {
        try {
            AutoBean<AppfogApplication> appfogApplication = autoBeanFactory.appfogApplication();
            AutoBeanUnmarshaller<AppfogApplication> unmarshaller = new AutoBeanUnmarshaller<AppfogApplication>(appfogApplication);
            LoggedInHandler loggedInHandler = new LoggedInHandler() {
                @Override
                public void onLoggedIn() {
                    getApplicationInfo(project);
                }
            };

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