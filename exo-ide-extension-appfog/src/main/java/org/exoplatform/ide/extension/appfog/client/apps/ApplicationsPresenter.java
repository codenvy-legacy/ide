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
package org.exoplatform.ide.extension.appfog.client.apps;

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
import org.exoplatform.ide.extension.appfog.client.AppfogAsyncRequestCallback;
import org.exoplatform.ide.extension.appfog.client.AppfogClientService;
import org.exoplatform.ide.extension.appfog.client.AppfogExtension;
import org.exoplatform.ide.extension.appfog.client.delete.ApplicationDeletedEvent;
import org.exoplatform.ide.extension.appfog.client.delete.ApplicationDeletedHandler;
import org.exoplatform.ide.extension.appfog.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.appfog.client.marshaller.ApplicationListUnmarshaller;
import org.exoplatform.ide.extension.appfog.client.marshaller.TargetsUnmarshaller;
import org.exoplatform.ide.extension.appfog.client.project.ApplicationInfoChangedEvent;
import org.exoplatform.ide.extension.appfog.client.project.ApplicationInfoChangedHandler;
import org.exoplatform.ide.extension.appfog.client.start.RestartApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.start.StartApplicationEvent;
import org.exoplatform.ide.extension.appfog.client.start.StopApplicationEvent;
import org.exoplatform.ide.extension.appfog.shared.AppfogApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class ApplicationsPresenter implements ViewClosedHandler, ShowApplicationsHandler, ApplicationDeletedHandler,
                                              ApplicationInfoChangedHandler {
    public interface Display extends IsView {
        String ID = "ideAppfogApplicationsView";

        HasClickHandlers getCloseButton();

        HasClickHandlers getShowButton();

        ListGridItem<AppfogApplication> getAppsGrid();

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
         * @param server
         */
        void setServerValue(String server);
    }

    private Display display;

    private List<String> servers = new ArrayList<String>();

    private String currentServer;

    /**
     *
     */
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
                getApplicationList();
            }
        });

        display.getActions().addStopApplicationHandler(new SelectionHandler<AppfogApplication>() {

            @Override
            public void onSelection(SelectionEvent<AppfogApplication> event) {
                IDE.fireEvent(new StopApplicationEvent(event.getSelectedItem().getName(), display.getServerSelectField().getValue()));
            }
        });

        display.getActions().addStartApplicationHandler(new SelectionHandler<AppfogApplication>() {

            @Override
            public void onSelection(SelectionEvent<AppfogApplication> event) {
                IDE.fireEvent(new StartApplicationEvent(event.getSelectedItem().getName(), display.getServerSelectField().getValue()));
            }
        });

        display.getActions().addRestartApplicationHandler(new SelectionHandler<AppfogApplication>() {

            @Override
            public void onSelection(SelectionEvent<AppfogApplication> event) {
                IDE.fireEvent(new RestartApplicationEvent(event.getSelectedItem().getName(), display.getServerSelectField().getValue()));
            }
        });

        display.getActions().addDeleteApplicationHandler(new SelectionHandler<AppfogApplication>() {

            @Override
            public void onSelection(SelectionEvent<AppfogApplication> event) {
                IDE.fireEvent(new DeleteApplicationEvent(event.getSelectedItem().getName(), display.getServerSelectField().getValue()));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.apps.ShowApplicationsHandler#onShowApplications(org.exoplatform.ide
     * .extension.cloudfoundry.client.apps.ShowApplicationsEvent) */
    @Override
    public void onShowApplications(ShowApplicationsEvent event) {

        checkLogginedToServer();
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
            AppfogClientService.getInstance().getTargets(
                    new AsyncRequestCallback<List<String>>(new TargetsUnmarshaller(new ArrayList<String>())) {
                        @Override
                        protected void onSuccess(List<String> result) {
                            if (result.isEmpty()) {
                                servers = new ArrayList<String>();
                                servers.add(AppfogExtension.DEFAULT_SERVER);
                            } else {
                                servers = result;
                            }
                            // open view
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
        display.setServerValue(AppfogExtension.DEFAULT_SERVER);
        getApplicationList();
    }

    private void getApplicationList() {
        try {
            AppfogClientService.getInstance().getApplicationList(
                    display.getServerSelectField().getValue(),
                    new AppfogAsyncRequestCallback<List<AppfogApplication>>(new ApplicationListUnmarshaller(
                            new ArrayList<AppfogApplication>()), new LoggedInHandler()//
                    {
                        @Override
                        public void onLoggedIn() {
                            getApplicationList();
                        }
                    }, null, display.getServerSelectField().getValue()) {

                        @Override
                        protected void onSuccess(List<AppfogApplication> result) {
                            display.getAppsGrid().setValue(result);
                            display.getServerSelectField().setValue(display.getServerSelectField().getValue());
                            display.setServerValue(AppfogExtension.DEFAULT_SERVER);
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
