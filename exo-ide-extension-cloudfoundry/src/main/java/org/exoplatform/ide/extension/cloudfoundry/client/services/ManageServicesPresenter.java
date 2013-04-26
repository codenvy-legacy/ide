/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;

import java.util.Arrays;

/**
 * Presenter for managing CloudFondry services.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 13, 2012 10:53:33 AM anya $
 */
public class ManageServicesPresenter implements ManageServicesHandler, ViewClosedHandler,
                                                ProvisionedServiceCreatedHandler {
    interface Display extends IsView {
        HasClickHandlers getAddButton();

        HasClickHandlers getDeleteButton();

        HasClickHandlers getCancelButton();

        HasUnbindServiceHandler getUnbindServiceHandler();

        HasBindServiceHandler getBindServiceHandler();

        ListGridItem<ProvisionedService> getProvisionedServicesGrid();

        ListGridItem<String> getBoundedServicesGrid();

        void enableDeleteButton(boolean enabled);

    }

    private Display display;

    /** Application, for which need to bind service. */
    private CloudFoundryApplication application;

    private PAAS_PROVIDER paasProvider;

    /** Selected provisioned service. */
    private ProvisionedService selectedService;

    /** Selected provisioned service. */
    private String selectedBoundedService;

    private LoggedInHandler deleteServiceLoggedInHandler = new LoggedInHandler() {

        @Override
        public void onLoggedIn() {
            deleteService(selectedService);
        }
    };

    private LoggedInHandler bindServiceLoggedInHandler = new LoggedInHandler() {

        @Override
        public void onLoggedIn() {
            bindService(selectedService);
        }
    };

    private LoggedInHandler unBindServiceLoggedInHandler = new LoggedInHandler() {

        @Override
        public void onLoggedIn() {
            unbindService(selectedBoundedService);
        }
    };

    private LoggedInHandler getApplicationInfoLoggedInHandler = new LoggedInHandler() {

        @Override
        public void onLoggedIn() {
            getApplicationInfo();
        }
    };

    public ManageServicesPresenter() {
        IDE.addHandler(ManageServicesEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getProvisionedServicesGrid().addSelectionHandler(new SelectionHandler<ProvisionedService>() {
            @Override
            public void onSelection(SelectionEvent<ProvisionedService> event) {
                selectedService = event.getSelectedItem();
                boolean enable = (selectedService != null);
                display.enableDeleteButton(enable);
            }
        });

        display.getBoundedServicesGrid().addSelectionHandler(new SelectionHandler<String>() {

            @Override
            public void onSelection(SelectionEvent<String> event) {
                selectedBoundedService = event.getSelectedItem();
            }
        });

        display.getCancelButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getDeleteButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                askBeforeDelete(selectedService);
            }
        });

        display.getAddButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                doAdd();
            }
        });

        display.getBindServiceHandler().addBindServiceHandler(new SelectionHandler<ProvisionedService>() {

            @Override
            public void onSelection(SelectionEvent<ProvisionedService> event) {
                bindService(event.getSelectedItem());
            }
        });

        display.getUnbindServiceHandler().addUnbindServiceHandler(new SelectionHandler<String>() {
            public void onSelection(SelectionEvent<String> event) {
                unbindService(event.getSelectedItem());
            }

            ;
        });
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.ManageServicesHandler#onManageServices(org.exoplatform.ide
     * .extension.cloudfoundry.client.services.ManageServicesEvent) */
    @Override
    public void onManageServices(ManageServicesEvent event) {
        this.application = event.getApplication();
        this.paasProvider = event.getPaasProvider();
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
        display.enableDeleteButton(false);
        getApplicationInfo();
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** Get the list of CloudFoundry services (system and provisioned). */
    private void getServices() {
        try {
            CloudFoundryClientService.getInstance().services(null, paasProvider,
                                                             new AsyncRequestCallback<CloudfoundryServices>(
                                                                     new CloudFoundryServicesUnmarshaller()) {

                                                                 @Override
                                                                 protected void onSuccess(CloudfoundryServices result) {
                                                                     display.getProvisionedServicesGrid()
                                                                            .setValue(Arrays.asList(result.getProvisioned()));
                                                                     display.enableDeleteButton(false);
                                                                 }

                                                                 @Override
                                                                 protected void onFailure(Throwable exception) {
                                                                     Dialogs.getInstance().showError(
                                                                             CloudFoundryExtension.LOCALIZATION_CONSTANT
                                                                                                  .retrieveServicesFailed());
                                                                 }
                                                             });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** Perform adding new provisioned service. */
    private void doAdd() {
        IDE.fireEvent(new CreateServiceEvent(this, paasProvider));
    }

    /**
     * Delete provisioned service.
     *
     * @param service
     *         service to delete
     */
    private void deleteService(final ProvisionedService service) {
        try {
            CloudFoundryClientService.getInstance().deleteService(null, service.getName(), paasProvider,
                                                                  new CloudFoundryAsyncRequestCallback<Object>(null,
                                                                                                               deleteServiceLoggedInHandler,
                                                                                                               null, paasProvider) {
                                                                      @Override
                                                                      protected void onSuccess(Object result) {
                                                                          getServices();
                                                                          if (application.getServices().contains(service.getName())) {
                                                                              getApplicationInfo();
                                                                          }
                                                                      }
                                                                  });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
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
            CloudFoundryClientService.getInstance().bindService(null, service.getName(), application.getName(), null,
                                                                null, new CloudFoundryAsyncRequestCallback<Object>(null,
                                                                                                                   bindServiceLoggedInHandler,
                                                                                                                   null, paasProvider) {

                @Override
                protected void onSuccess(Object result) {
                    getApplicationInfo();
                }

                /**
                 * @see org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback#onFailure(java.lang.Throwable)
                 */
                @Override
                protected void onFailure(Throwable exception) {
                    super.onFailure(exception);
                    getApplicationInfo();
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Unbind service from application.
     *
     * @param service
     */
    private void unbindService(String service) {
        try {
            CloudFoundryClientService.getInstance().unbindService(null, service, application.getName(), null, null, new CloudFoundryAsyncRequestCallback<Object>(null,
                                                                                                               unBindServiceLoggedInHandler,
                                                                                                               null, paasProvider) {

                                                                      @Override
                                                                      protected void onSuccess(Object result) {
                                                                          getApplicationInfo();
                                                                      }
                                                                  });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * Ask user before deleting service.
     *
     * @param service
     */
    private void askBeforeDelete(final ProvisionedService service) {
        Dialogs.getInstance().ask(CloudFoundryExtension.LOCALIZATION_CONSTANT.deleteServiceTitle(),
                                  CloudFoundryExtension.LOCALIZATION_CONSTANT.deleteServiceQuestion(service.getName()),
                                  new BooleanValueReceivedHandler() {

                                      @Override
                                      public void booleanValueReceived(Boolean value) {
                                          if (value != null && value) {
                                              deleteService(service);
                                          }
                                      }
                                  });
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.services.ProvisionedServiceCreatedHandler#onProvisionedServiceCreated(org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService) */
    @Override
    public void onProvisionedServiceCreated(ProvisionedService service) {
        getServices();
    }

    private void getApplicationInfo() {
        AutoBean<CloudFoundryApplication> cloudFoundryApplication =
                CloudFoundryExtension.AUTO_BEAN_FACTORY.cloudFoundryApplication();

        AutoBeanUnmarshaller<CloudFoundryApplication> unmarshaller =
                new AutoBeanUnmarshaller<CloudFoundryApplication>(cloudFoundryApplication);
        try {
            CloudFoundryClientService.getInstance().getApplicationInfo(
                    null,
                    null,
                    application.getName(),
                    null,
                    new CloudFoundryAsyncRequestCallback<CloudFoundryApplication>(unmarshaller,
                                                                                  getApplicationInfoLoggedInHandler, null, paasProvider) {

                        @Override
                        protected void onSuccess(CloudFoundryApplication result) {
                            application = result;
                            getServices();
                            display.getBoundedServicesGrid().setValue(result.getServices());
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
