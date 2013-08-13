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
package com.codenvy.ide.extension.cloudfoundry.client.services;

import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import com.codenvy.ide.extension.cloudfoundry.client.CloudFoundryLocalizationConstant;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import com.codenvy.ide.extension.cloudfoundry.client.login.LoginPresenter;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryApplicationUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.client.marshaller.CloudFoundryServicesUnmarshaller;
import com.codenvy.ide.extension.cloudfoundry.dto.client.DtoClientImpls;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import com.codenvy.ide.extension.cloudfoundry.shared.CloudFoundryServices;
import com.codenvy.ide.extension.cloudfoundry.shared.ProvisionedService;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for managing CloudFondry services.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 13, 2012 10:53:33 AM anya $
 */
@Singleton
public class ManageServicesPresenter implements ManageServicesView.ActionDelegate {
    private ManageServicesView                  view;
    /** Application, for which need to bind service. */
    private CloudFoundryApplication             application;
    /** Selected provisioned service. */
    private ProvisionedService                  selectedService;
    /** Selected provisioned service. */
    private String                              selectedBoundedService;
    private CreateServicePresenter              createServicePresenter;
    private EventBus                            eventBus;
    private ConsolePart                         console;
    private CloudFoundryLocalizationConstant    constant;
    private LoginPresenter                      loginPresenter;
    private CloudFoundryClientService           service;
    private CloudFoundryExtension.PAAS_PROVIDER paasProvider;
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
     * @param console
     * @param createServicePresenter
     * @param constant
     * @param loginPresenter
     * @param service
     */
    @Inject
    protected ManageServicesPresenter(ManageServicesView view, EventBus eventBus, ConsolePart console,
                                      CreateServicePresenter createServicePresenter, CloudFoundryLocalizationConstant constant,
                                      LoginPresenter loginPresenter, CloudFoundryClientService service) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.console = console;
        this.createServicePresenter = createServicePresenter;
        this.constant = constant;
        this.loginPresenter = loginPresenter;
        this.service = service;
    }

    /** {@inheritDoc} */
    @Override
    public void onAddClicked() {
        createServicePresenter.showDialog(paasProvider, new AsyncCallback<ProvisionedService>() {
            @Override
            public void onSuccess(ProvisionedService result) {
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
    public void onBindServiceClicked(ProvisionedService service) {
        bindService(service);
    }

    /**
     * Delete provisioned service.
     *
     * @param service
     *         service to delete
     */
    private void deleteService(final ProvisionedService service) {
        try {
            this.service.deleteService(null, service.getName(), paasProvider,
                                       new CloudFoundryAsyncRequestCallback<Object>(null, deleteServiceLoggedInHandler, null, eventBus,
                                                                                    console, constant, loginPresenter, paasProvider) {
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
            console.print(e.getMessage());
        }
    }

    /**
     * Bind service to application.
     *
     * @param service
     *         service to bind
     */
    private void bindService(final ProvisionedService service) {
        try {
            this.service.bindService(null, service.getName(), application.getName(), null, null,
                                     new CloudFoundryAsyncRequestCallback<Object>(null, bindServiceLoggedInHandler, null, eventBus, console,
                                                                                  constant, loginPresenter, paasProvider) {
                                         @Override
                                         protected void onSuccess(Object result) {
                                             getApplicationInfo();
                                         }

                                         @Override
                                         protected void onFailure(Throwable exception) {
                                             super.onFailure(exception);
                                             getApplicationInfo();
                                         }
                                     });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
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
            this.service.unbindService(null, service, application.getName(), null, null,
                                       new CloudFoundryAsyncRequestCallback<Object>(null, unBindServiceLoggedInHandler, null, eventBus,
                                                                                    console, constant, loginPresenter, paasProvider) {
                                           @Override
                                           protected void onSuccess(Object result) {
                                               getApplicationInfo();
                                           }
                                       });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Ask user before deleting service.
     *
     * @param service
     */
    private void askBeforeDelete(final ProvisionedService service) {
        if (Window.confirm(constant.deleteServiceQuestion(service.getName()))) {
            deleteService(service);
        }
    }

    /** Gets the list of services and put them to field. */
    private void getApplicationInfo() {
        DtoClientImpls.CloudFoundryApplicationImpl cloudFoundryApplication = DtoClientImpls.CloudFoundryApplicationImpl.make();
        CloudFoundryApplicationUnmarshaller unmarshaller = new CloudFoundryApplicationUnmarshaller(cloudFoundryApplication);

        try {
            this.service.getApplicationInfo(null, null, application.getName(), null,
                                            new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
                                                                                                          getApplicationInfoLoggedInHandler,
                                                                                                          null, eventBus, console, constant,
                                                                                                          loginPresenter, paasProvider) {
                                                @Override
                                                protected void onSuccess(CloudFoundryApplication result) {
                                                    application = result;
                                                    getServices();
                                                    view.setBoundedServices(result.getServices());
                                                }
                                            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /** Get the list of CloudFoundry services (system and provisioned). */
    private void getServices() {
        CloudFoundryServicesUnmarshaller unmarshaller = new CloudFoundryServicesUnmarshaller();

        try {
            this.service.services(null, paasProvider, new AsyncRequestCallback<CloudFoundryServices>(unmarshaller) {
                @Override
                protected void onSuccess(CloudFoundryServices result) {
                    view.setProvisionedServices(result.getProvisioned());
                    view.setEnableDeleteButton(false);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Window.alert(constant.retrieveServicesFailed());
                }
            });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
            console.print(e.getMessage());
        }
    }

    /**
     * Shows dialog.
     *
     * @param application
     *         application where will manage services
     */
    public void showDialog(CloudFoundryApplication application, CloudFoundryExtension.PAAS_PROVIDER paasProvider) {
        this.application = application;
        this.paasProvider = paasProvider;

        view.setEnableDeleteButton(false);
        getApplicationInfo();

        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onSelectedService(ProvisionedService service) {
        selectedService = service;

        updateControls();
    }

    /** Updates graphic components on the view. */
    private void updateControls() {
        view.setEnableDeleteButton(selectedService != null);
    }
}