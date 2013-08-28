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
package org.exoplatform.ide.extension.heroku.client.info;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.Property;
import org.exoplatform.ide.git.client.GitPresenter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Presenter for getting and displaying application's information.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 1, 2011 11:32:37 AM anya $
 */
public class ApplicationInfoPresenter extends GitPresenter implements ShowApplicationInfoHandler, ViewClosedHandler,
                                                                      LoggedInHandler {
    /** Properties order to be displayed. */
    private static List<String> order;

    private String applicationName;

    /**
     * Set the properties order.
     */
    static {
        order = new ArrayList<String>();

        order.add("Name");
        order.add("WebUrl");
        order.add("GitUrl");
        order.add("Owner");
        order.add("DomainName");
        order.add("Dynos");
        order.add("RepoSize");
        order.add("DatabaseSize");
        order.add("SlugSize");
        order.add("Stack");
        order.add("Workers");
    }

    /** Properties order comparator. */
    private class PropertiesComparator implements Comparator<Property> {
        /** @see java.util.Comparator#compare(java.lang.Object, java.lang.Object) */
        @Override
        public int compare(Property p1, Property p2) {
            Integer index1 = order.indexOf(p1.getName());
            Integer index2 = order.indexOf(p2.getName());

            if (index1 == -1 || index2 == -1)
                return 0;

            return index1.compareTo(index2);
        }

    }

    interface Display extends IsView {
        HasClickHandlers getOkButton();

        ListGridItem<Property> getApplicationInfoGrid();
    }

    private Display display;

    /**
     * @param eventBus
     *         events handler
     */
    public ApplicationInfoPresenter() {
        IDE.addHandler(ShowApplicationInfoEvent.TYPE, this);
        IDE.addHandler(ViewClosedEvent.TYPE, this);
    }

    /** Bind presenter with display. */
    public void bindDisplay() {
        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(display.asView().getId());
            }
        });
    }

    /** @see org.exoplatform.ide.extension.heroku.client.info.ShowApplicationInfoHandler#onShowApplicationInfo(org.exoplatform.ide
     * .extension.heroku.client.info.ShowApplicationInfoEvent) */
    @Override
    public void onShowApplicationInfo(ShowApplicationInfoEvent event) {
        applicationName = event.getApplicationName();
        if (event.getApplicationName() != null && !event.getApplicationName().isEmpty()) {
            getApplicationInfo();
            return;
        }

        if (makeSelectionCheck()) {
            getApplicationInfo();
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

    /** Get application's information. */
    public void getApplicationInfo() {
        //final String projectId = (applicationName == null) ? ((ItemContext)selectedItems.get(0)).getProject().getId() : null;
        final String projectId = (applicationName == null) ? getSelectedProject().getId() : null;
        try {
            HerokuClientService.getInstance().getApplicationInfo(applicationName, vfs.getId(), projectId, false,
                                                                 new HerokuAsyncRequestCallback(this) {

                                                                     @Override
                                                                     protected void onSuccess(List<Property> properties) {
                                                                         if (display == null) {
                                                                             display = GWT.create(Display.class);
                                                                             bindDisplay();
                                                                             IDE.getInstance().openView(display.asView());
                                                                         }
                                                                         // Make first letter of property name to be in upper case.
                                                                         for (Property property : properties) {
                                                                             String name =
                                                                                     (property.getName().length() > 1)
                                                                                     ? (property.getName().substring(0, 1).toUpperCase() +
                                                                                        property.getName().substring(1))
                                                                                     : property.getName().toUpperCase();
                                                                             property.setName(name);

                                                                             if ("WebUrl".equals(property.getName())) {
                                                                                 property.setValue("<a href =\"" + property.getValue() +
                                                                                                   "\" target=\"_blank\">"
                                                                                                   + property.getValue() + "</a>");
                                                                             }
                                                                         }
                                                                         Collections.sort(properties, new PropertiesComparator());
                                                                         display.getApplicationInfoGrid().setValue(properties);
                                                                     }
                                                                 });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent) */
    @Override
    public void onLoggedIn(LoggedInEvent event) {
        IDE.removeHandler(LoggedInEvent.TYPE, this);
        if (!event.isFailed()) {
            getApplicationInfo();
        }
    }
}
