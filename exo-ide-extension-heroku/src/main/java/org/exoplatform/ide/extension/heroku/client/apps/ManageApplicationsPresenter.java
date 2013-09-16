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
package org.exoplatform.ide.extension.heroku.client.apps;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.ApplicationListAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.delete.ApplicationDeletedEvent;
import org.exoplatform.ide.extension.heroku.client.delete.ApplicationDeletedHandler;
import org.exoplatform.ide.extension.heroku.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.heroku.client.imports.ImportApplicationEvent;
import org.exoplatform.ide.extension.heroku.client.info.ShowApplicationInfoEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.rename.ApplicationRenamedEvent;
import org.exoplatform.ide.extension.heroku.client.rename.ApplicationRenamedHandler;
import org.exoplatform.ide.extension.heroku.client.rename.RenameApplicationEvent;
import org.exoplatform.ide.extension.heroku.client.stack.ChangeApplicationStackEvent;

import java.util.Collections;
import java.util.List;

/**
 * Presenter for managing the list Heroku applications.
 *
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Mar 14, 2012 5:26:59 PM anya $
 */
public class ManageApplicationsPresenter implements ManageApplicationsHandler, ViewClosedHandler,
                                                    ApplicationDeletedHandler, ApplicationRenamedHandler {
    interface Display extends IsView {
        HasClickHandlers getCloseButton();

        HasApplicationsActions getActions();

        ListGridItem<String> getAppsGrid();
    }

    /** Display. */
    private Display display;

    public ManageApplicationsPresenter() {
        IDE.getInstance().addControl(new ManageApplicationsControl());

        IDE.addHandler(ManageApplicationsEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ApplicationDeletedEvent.TYPE, this);
        IDE.addHandler(ApplicationRenamedEvent.TYPE, this);
    }

    /** Bind display with presenter. */
    public void bindDisplay() {
        display.getCloseButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });

        display.getActions().addApplicationInfoHandler(new SelectionHandler<String>() {

            @Override
            public void onSelection(SelectionEvent<String> event) {
                IDE.fireEvent(new ShowApplicationInfoEvent(event.getSelectedItem()));
            }
        });

        display.getActions().addChangeEnvironmentHandler(new SelectionHandler<String>() {

            @Override
            public void onSelection(SelectionEvent<String> event) {
                IDE.fireEvent(new ChangeApplicationStackEvent(event.getSelectedItem()));
            }
        });

        display.getActions().addRenameApplicationHandler(new SelectionHandler<String>() {

            @Override
            public void onSelection(SelectionEvent<String> event) {
                IDE.fireEvent(new RenameApplicationEvent(event.getSelectedItem()));
            }
        });

        display.getActions().addDeleteApplicationHandler(new SelectionHandler<String>() {

            @Override
            public void onSelection(SelectionEvent<String> event) {
                IDE.fireEvent(new DeleteApplicationEvent(event.getSelectedItem()));
            }
        });

        display.getActions().addImportApplicationHandler(new SelectionHandler<String>() {

            @Override
            public void onSelection(SelectionEvent<String> event) {
                IDE.getInstance().closeView(display.asView().getId());
                IDE.fireEvent(new ImportApplicationEvent(event.getSelectedItem()));
            }
        });
    }

    /** @see org.exoplatform.ide.extension.heroku.client.apps.ManageApplicationsHandler#onManageApplications(org.exoplatform.ide
     * .extension.heroku.client.apps.ManageApplicationsEvent) */
    @Override
    public void onManageApplications(ManageApplicationsEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            bindDisplay();
            getApplications();
        }
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    /** Get the list of Heroku applications. */
    public void getApplications() {
        LoggedInHandler loggedInHandler = new LoggedInHandler() {
            @Override
            public void onLoggedIn(LoggedInEvent event) {
                if (!event.isFailed()) {
                    IDE.getInstance().openView(display.asView());
                    getApplications();
                }
            }
        };

        try {
            HerokuClientService.getInstance().listApplications(new ApplicationListAsyncRequestCallback(loggedInHandler) {

                @Override
                protected void onSuccess(List<String> result) {
                    if (!display.asView().isActive()) {
                        IDE.getInstance().openView(display.asView());
                    }
                    Collections.sort(result);
                    display.getAppsGrid().setValue(result);
                }
            });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.heroku.client.rename.ApplicationRenamedHandler#onApplicationRenamed(org.exoplatform.ide
     * .extension.heroku.client.rename.ApplicationRenamedEvent) */
    @Override
    public void onApplicationRenamed(ApplicationRenamedEvent event) {
        if (display != null) {
            getApplications();
        }
    }

    /** @see org.exoplatform.ide.extension.heroku.client.delete.ApplicationDeletedHandler#onApplicationDeleted(org.exoplatform.ide
     * .extension.heroku.client.delete.ApplicationDeletedEvent) */
    @Override
    public void onApplicationDeleted(ApplicationDeletedEvent event) {
        if (display != null) {
            getApplications();
        }
    }

}
