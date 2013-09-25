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
package com.codenvy.ide.ext.appfog.client.services;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.appfog.client.AppFogExtension;
import com.codenvy.ide.ext.appfog.client.AppfogAsyncRequestCallback;
import com.codenvy.ide.ext.appfog.client.AppfogClientService;
import com.codenvy.ide.ext.appfog.client.AppfogLocalizationConstant;
import com.codenvy.ide.ext.appfog.client.login.LoggedInHandler;
import com.codenvy.ide.ext.appfog.client.login.LoginPresenter;
import com.codenvy.ide.ext.appfog.client.marshaller.AppFogApplicationUnmarshaller;
import com.codenvy.ide.ext.appfog.client.marshaller.AppfogServicesUnmarshaller;
import com.codenvy.ide.ext.appfog.shared.AppfogApplication;
import com.codenvy.ide.ext.appfog.shared.AppfogProvisionedService;
import com.codenvy.ide.ext.appfog.shared.AppfogServices;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for managing Appfog services.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
@Singleton
public class ManageServicesPresenter implements ManageServicesView.ActionDelegate {
    private ManageServicesView         view;
    /** Application, for which need to bind service. */
    private AppfogApplication          application;
    /** Selected provisioned service. */
    private AppfogProvisionedService   selectedService;
    /** Selected provisioned service. */
    private String                     selectedBoundedService;
    private CreateServicePresenter     createServicePresenter;
    private EventBus                   eventBus;
    private AppfogLocalizationConstant constant;
    private LoginPresenter             loginPresenter;
    private AppfogClientService        service;
    private NotificationManager        notificationManager;
    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler deleteServiceLoggedInHandler      = new LoggedInHandler() {

        @Override
        public void onLoggedIn() {
            deleteService(selectedService);
        }
    };
    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler bindServiceLoggedInHandler        = new LoggedInHandler() {

        @Override
        public void onLoggedIn() {
            bindService(selectedService);
        }
    };
    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler unBindServiceLoggedInHandler      = new LoggedInHandler() {

        @Override
        public void onLoggedIn() {
            unbindService(selectedBoundedService);
        }
    };
    /** If user is not logged in to CloudFoundry, this handler will be called, after user logged in. */
    private LoggedInHandler getApplicationInfoLoggedInHandler = new LoggedInHandler() {

        @Override
        public void onLoggedIn() {
            getApplicationInfo();
        }
    };

    /**
     * Create presenter.
     *
     * @param view
     * @param eventBus
     * @param constant
     * @param loginPresenter
     * @param service
     * @param createServicePresenter
     * @param notificationManager
     */
    @Inject
    protected ManageServicesPresenter(ManageServicesView view, EventBus eventBus, AppfogLocalizationConstant constant,
                                      LoginPresenter loginPresenter, AppfogClientService service,
                                      CreateServicePresenter createServicePresenter, NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
        this.createServicePresenter = createServicePresenter;
        this.notificationManager = notificationManager;
    }

    /**
     * Shows dialog.
     *
     * @param application
     *         application where will manage services
     */
    public void showDialog(AppfogApplication application) {
        this.application = application;

        view.setEnableDeleteButton(false);
        getApplicationInfo();

        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onAddClicked() {
        createServicePresenter.showDialog(new AsyncCallback<AppfogProvisionedService>() {
            @Override
            public void onSuccess(AppfogProvisionedService result) {
                getServices();
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ManageServicesPresenter.class, "Can not create service", caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        askBeforeDelete(selectedService);
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onUnbindServiceClicked(String service) {
        unbindService(service);
    }

    /** {@inheritDoc} */
    @Override
    public void onBindServiceClicked(AppfogProvisionedService service) {
        bindService(service);
    }

    /** {@inheritDoc} */
    @Override
    public void onSelectedService(AppfogProvisionedService service) {
        selectedService = service;

        updateControls();
    }

    /** Updates graphic components on the view. */
    private void updateControls() {
        view.setEnableDeleteButton(selectedService != null);
    }

    /**
     * Delete provisioned service.
     *
     * @param service
     *         service to delete
     */
    private void deleteService(final AppfogProvisionedService service) {
        try {
            this.service.deleteService(AppFogExtension.DEFAULT_SERVER, service.getName(),
                                       new AppfogAsyncRequestCallback<Object>(null, deleteServiceLoggedInHandler, null, eventBus, constant,
                                                                              loginPresenter, notificationManager) {
                                           @Override
                                           protected void onSuccess(Object result) {
                                               getServices();
                                               if (application.getServices().contains(service.getName())) {
                                                   getApplicationInfo();
                                               }
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Bind service to application.
     *
     * @param service
     *         service to bind
     */
    private void bindService(final AppfogProvisionedService service) {
        try {
            this.service.bindService(AppFogExtension.DEFAULT_SERVER, service.getName(), application.getName(), null, null,
                                     new AppfogAsyncRequestCallback<Object>(null, bindServiceLoggedInHandler, null, eventBus,
                                                                            constant, loginPresenter, notificationManager) {
                                         @Override
                                         protected void onSuccess(Object result) {
                                             getApplicationInfo();
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             //Maybe appear 502 unexpected gateway response from appfog while bind
                                             // mysql service
                                             //that's why we showing user error dialog that his service can't bind
                                             Window.alert("Can't bind " + service.getName() + " service.");
                                             getApplicationInfo();
                                         }
                                     });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Unbind service from application.
     *
     * @param service
     */
    private void unbindService(String service) {
        try {
            selectedBoundedService = service;
            this.service.unbindService(AppFogExtension.DEFAULT_SERVER, service, application.getName(), null, null,
                                       new AppfogAsyncRequestCallback<Object>(null, unBindServiceLoggedInHandler, null, eventBus, constant,
                                                                              loginPresenter, notificationManager) {
                                           @Override
                                           protected void onSuccess(Object result) {
                                               getApplicationInfo();
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /**
     * Ask user before deleting service.
     *
     * @param service
     */
    private void askBeforeDelete(final AppfogProvisionedService service) {
        if (Window.confirm(constant.deleteServiceQuestion(service.getName()))) {
            deleteService(service);
        }
    }

    /** Gets the list of services and put them to field. */
    private void getApplicationInfo() {
        AppFogApplicationUnmarshaller unmarshaller = new AppFogApplicationUnmarshaller();

        try {
            service.getApplicationInfo(null, null, application.getName(), AppFogExtension.DEFAULT_SERVER,
                                       new AppfogAsyncRequestCallback<AppfogApplication>(unmarshaller, getApplicationInfoLoggedInHandler,
                                                                                         null, eventBus, constant, loginPresenter,
                                                                                         notificationManager) {
                                           @Override
                                           protected void onSuccess(AppfogApplication result) {
                                               application = result;
                                               getServices();
                                               view.setBoundedServices(result.getServices());
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }

    /** Get the list of Appfog services (system and provisioned). */
    private void getServices() {
        AppfogServicesUnmarshaller unmarshaller = new AppfogServicesUnmarshaller();

        try {
            service.services(AppFogExtension.DEFAULT_SERVER, new AsyncRequestCallback<AppfogServices>(unmarshaller) {
                @Override
                protected void onSuccess(AppfogServices result) {
                    JsonArray<AppfogProvisionedService> filteredServices = JsonCollections.createArray();
                    JsonArray<AppfogProvisionedService> appfogProvisionedService = result.getAppfogProvisionedService();
                    for (int i = 0; i < appfogProvisionedService.size(); i++) {
                        AppfogProvisionedService service = appfogProvisionedService.get(i);
                        if (service.getInfra().getName().equals(application.getInfra().getName())) {
                            filteredServices.add(service);
                        }
                    }

                    view.setProvisionedServices(filteredServices);
                    view.setEnableDeleteButton(false);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Window.alert(constant.retrieveServicesFailed());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            Notification notification = new Notification(e.getMessage(), ERROR);
            notificationManager.showNotification(notification);
        }
    }
}