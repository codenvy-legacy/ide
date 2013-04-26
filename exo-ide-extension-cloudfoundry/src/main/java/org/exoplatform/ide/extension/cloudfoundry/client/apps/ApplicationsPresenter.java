/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.cloudfoundry.client.apps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientService;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.ApplicationListUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.marshaller.TargetsUnmarshaller;
import org.exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedHandler;
import org.exoplatform.ide.extension.cloudfoundry.client.start.RestartApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.start.StartApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.client.start.StopApplicationEvent;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;

import java.util.ArrayList;
import java.util.List;

import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER.WEB_FABRIC;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 18, 2011 evgen $
 */
public class ApplicationsPresenter implements ViewClosedHandler, ShowApplicationsHandler, ApplicationDeletedHandler,
                                              ApplicationInfoChangedHandler {
    public interface Display extends IsView {
        String ID = "ideCloudFoundryApplicationsView";

        HasClickHandlers getCloseButton();

        HasClickHandlers getShowButton();

        ListGridItem<CloudFoundryApplication> getAppsGrid();

        HasApplicationsActions getActions();

        /**
         * Get server select field.
         *
         * @return
         */
        HasValue<String> getServerSelectField();

        /**
         * Set the list of servers to ServerSelectField.
         *
         * @param servers
         */
        void setServerValues(String[] servers);
    }

    private Display display;

    private List<String> servers = new ArrayList<String>();

    private String currentServer;

    private PAAS_PROVIDER paasProvider = null;

    public ApplicationsPresenter() {
        IDE.addHandler(ShowApplicationsEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind presenter with display. */
    public void bindDisplay() {
        display.getCloseButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getShowButton().addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                currentServer = display.getServerSelectField().getValue();
                getApplicationList();
            }
        });

        display.getActions().addStopApplicationHandler(new SelectionHandler<CloudFoundryApplication>() {

            @Override
            public void onSelection(SelectionEvent<CloudFoundryApplication> event) {
                IDE.fireEvent(new StopApplicationEvent(event.getSelectedItem().getName(), paasProvider));
            }
        });

        display.getActions().addStartApplicationHandler(new SelectionHandler<CloudFoundryApplication>() {

            @Override
            public void onSelection(SelectionEvent<CloudFoundryApplication> event) {
                IDE.fireEvent(new StartApplicationEvent(event.getSelectedItem().getName(), paasProvider));
            }
        });

        display.getActions().addRestartApplicationHandler(new SelectionHandler<CloudFoundryApplication>() {

            @Override
            public void onSelection(SelectionEvent<CloudFoundryApplication> event) {
                IDE.fireEvent(new RestartApplicationEvent(event.getSelectedItem().getName(), paasProvider));
            }
        });

        display.getActions().addDeleteApplicationHandler(new SelectionHandler<CloudFoundryApplication>() {

            @Override
            public void onSelection(SelectionEvent<CloudFoundryApplication> event) {
                IDE.fireEvent(new DeleteApplicationEvent(event.getSelectedItem().getName(), currentServer, paasProvider));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ShowApplicationsHandler#onShowApplications(org.exoplatform.ide
     * .extension.cloudfoundry.client.apps.ShowApplicationsEvent) */
    @Override
    public void onShowApplications(ShowApplicationsEvent event) {
        this.paasProvider = event.getPaasProvider();
        if (paasProvider == WEB_FABRIC) {
            openView();
        } else {
            checkLogginedToServer();
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
            IDE.removeHandler(ApplicationDeletedEvent.TYPE, this);
        }
    }

    private void checkLogginedToServer() {
        try {
            CloudFoundryClientService.getInstance().getTargets(
                    paasProvider, new AsyncRequestCallback<List<String>>(new TargetsUnmarshaller(new ArrayList<String>())) {
                        @Override
                        protected void onSuccess(List<String> result) {
                            if (!result.isEmpty()) {
                                servers = result;
                            } else {
                                servers = new ArrayList<String>();
                                servers.add(CloudFoundryExtension.DEFAULT_CF_SERVER);
                            }
                            openView();
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void openView() {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
            IDE.addHandler(ApplicationInfoChangedEvent.TYPE, this);
            IDE.addHandler(ApplicationDeletedEvent.TYPE, this);
        }
        display.setServerValues(servers.toArray(new String[servers.size()]));
        // fill the list of applications
        currentServer = servers.get(0);
        getApplicationList();
    }

    private void getApplicationList() {
        try {
            CloudFoundryClientService.getInstance().getApplicationList(
                    currentServer,
                    paasProvider, new CloudFoundryAsyncRequestCallback<List<CloudFoundryApplication>>(new ApplicationListUnmarshaller(
                            new ArrayList<CloudFoundryApplication>()), new LoggedInHandler()//
                    {
                        @Override
                        public void onLoggedIn() {
                            getApplicationList();
                        }
                    }, null, currentServer, paasProvider) {

                        @Override
                        protected void onSuccess(List<CloudFoundryApplication> result) {
                            display.getAppsGrid().setValue(result);
                            display.getServerSelectField().setValue(currentServer);

                            // update the list of servers, if was enter value, that doesn't present in list
                            if (!servers.contains(currentServer)) {
                                getServers();
                            }
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    private void getServers() {
        try {
            CloudFoundryClientService.getInstance().getTargets(
                    paasProvider, new AsyncRequestCallback<List<String>>(new TargetsUnmarshaller(new ArrayList<String>())) {
                        @Override
                        protected void onSuccess(List<String> result) {
                            servers = result;
                            display.setServerValues(result.toArray(new String[result.size()]));
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.delete.ApplicationDeletedHandler#onApplicationDeleted(org.exoplatform.ide
     * .extension.cloudfoundry.client.delete.ApplicationDeletedEvent) */
    @Override
    public void onApplicationDeleted(ApplicationDeletedEvent event) {
        if (display != null) {
            getApplicationList();
        }
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedHandler#onApplicationInfoChanged(org
     * .exoplatform.ide.extension.cloudfoundry.client.project.ApplicationInfoChangedEvent) */
    @Override
    public void onApplicationInfoChanged(ApplicationInfoChangedEvent event) {
        if (display != null) {
            getApplicationList();
        }
    }

}
