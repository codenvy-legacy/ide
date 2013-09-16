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
package org.exoplatform.ide.extension.cloudfoundry.client.services;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;
import com.google.web.bindery.autobean.shared.AutoBean;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.AutoBeanUnmarshaller;
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
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemService;
import org.exoplatform.ide.git.client.GitPresenter;

import java.util.LinkedHashMap;

/**
 * Presenter for creating new service.
 * 
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jul 16, 2012 12:31:33 PM anya $
 */
public class CreateServicePresenter extends GitPresenter implements CreateServiceHandler, ViewClosedHandler {
    interface Display extends IsView {
        HasValue<String> getSystemServicesField();

        HasValue<String> getNameField();

        HasClickHandlers getCreateButton();

        HasClickHandlers getCancelButton();

        void setServices(LinkedHashMap<String, String> values);
    }

    /** Display. */
    private Display                          display;

    /** Handler for successful service creation. */
    private ProvisionedServiceCreatedHandler serviceCreatedHandler;

    private PAAS_PROVIDER                    paasProvider;

    private LoggedInHandler                  createServiceLoggedInHandler = new LoggedInHandler() {

                                                                              @Override
                                                                              public void onLoggedIn(String server) {
                                                                                  doCreate();
                                                                              }
                                                                          };

    public CreateServicePresenter() {
        IDE.addHandler(CreateServiceEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    public void bindDisplay() {
        display.getCancelButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getCreateButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                doCreate();
            }
        });
    }

    /** Get the list of CloudFoundry services (provisioned and system). */
    private void getServices() {
        try {
            CloudFoundryClientService.getInstance()
                                     .services(null, paasProvider,
                                               new AsyncRequestCallback<CloudfoundryServices>(
                                                                                              new CloudFoundryServicesUnmarshaller()) {

                                                   @Override
                                                   protected void onSuccess(CloudfoundryServices result) {
                                                       LinkedHashMap<String, String> values =
                                                                                              new LinkedHashMap<String, String>();
                                                       for (SystemService service : result.getSystem()) {
                                                           values.put(service.getVendor(), service.getDescription());
                                                       }
                                                       display.setServices(values);
                                                   }

                                                   @Override
                                                   protected void onFailure(Throwable exception) {
                                                       Dialogs.getInstance()
                                                              .showError(
                                                                         CloudFoundryExtension.LOCALIZATION_CONSTANT
                                                                                                                    .retrieveServicesFailed());
                                                   }
                                               });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /**
     * @see org.exoplatform.ide.extension.cloudfoundry.client.services.CreateServiceHandler#onCreateService(org.exoplatform.ide
     *      .extension.cloudfoundry.client.services.CreateServiceEvent)
     */
    @Override
    public void onCreateService(CreateServiceEvent event) {
        this.serviceCreatedHandler = event.getProvisionedServiceCreatedHandler();
        this.paasProvider = event.getPaasProvider();
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bindDisplay();
        }
        getServices();
    }

    /**
     * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     *      .event.ViewClosedEvent)
     */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** Create new provisioned service. */
    private void doCreate() {
        String name = display.getNameField().getValue();
        String type = display.getSystemServicesField().getValue();
        try {
            AutoBean<ProvisionedService> provisionedService = CloudFoundryExtension.AUTO_BEAN_FACTORY.provisionedService();
            AutoBeanUnmarshaller<ProvisionedService> unmarshaller =
                                                                    new AutoBeanUnmarshaller<ProvisionedService>(provisionedService);

            CloudFoundryClientService.getInstance()
                                     .createService(null, type, name, null, vfs.getId(), getSelectedProject().getId(),
                                                    new CloudFoundryAsyncRequestCallback<ProvisionedService>(unmarshaller,
                                                                                                             createServiceLoggedInHandler,
                                                                                                             null, paasProvider) {
                                                        @Override
                                                        protected void onSuccess(ProvisionedService result) {
                                                            IDE.getInstance().closeView(display.asView().getId());
                                                            serviceCreatedHandler.onProvisionedServiceCreated(result);
                                                        }
                                                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }
}
