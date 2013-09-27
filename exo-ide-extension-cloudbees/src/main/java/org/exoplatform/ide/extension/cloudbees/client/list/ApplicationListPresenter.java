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
package org.exoplatform.ide.extension.cloudbees.client.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesAsyncRequestCallback;
import org.exoplatform.ide.extension.cloudbees.client.CloudBeesClientService;
import org.exoplatform.ide.extension.cloudbees.client.delete.DeleteApplicationEvent;
import org.exoplatform.ide.extension.cloudbees.client.info.ApplicationInfoEvent;
import org.exoplatform.ide.extension.cloudbees.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.cloudbees.client.marshaller.ApplicationListUnmarshaller;
import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Sep 21, 2011 evgen $
 */
public class ApplicationListPresenter implements ViewClosedHandler, ShowApplicationListHandler, OutputHandler {
    public interface Display extends IsView {
        String ID = "ideCloudBeesAppListView";

        HasClickHandlers getOkButton();

        HasApplicationListActions getAppListGrid();
    }

    private Display display;

    private HandlerRegistration outputHandler;

    /**
     *
     */
    public ApplicationListPresenter() {
        super();
        IDE.addHandler(ViewClosedEvent.TYPE, this);
        IDE.addHandler(ShowApplicationListEvent.TYPE, this);
    }

    /** @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api
     * .event.ViewClosedEvent) */
    @Override
    public void onViewClosed(ViewClosedEvent event) {
        if (event.getView() instanceof Display) {
            display = null;
        }
    }

    private void bind() {
        display.getOkButton().addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                IDE.getInstance().closeView(Display.ID);
            }
        });
        display.getAppListGrid().addInfoHandler(new SelectionHandler<ApplicationInfo>() {

            @Override
            public void onSelection(SelectionEvent<ApplicationInfo> event) {
                IDE.fireEvent(new ApplicationInfoEvent(event.getSelectedItem()));
            }
        });

        display.getAppListGrid().addDeleteHandler(new SelectionHandler<ApplicationInfo>() {

            @Override
            public void onSelection(SelectionEvent<ApplicationInfo> event) {
                outputHandler = IDE.addHandler(OutputEvent.TYPE, ApplicationListPresenter.this);
                IDE.fireEvent(new DeleteApplicationEvent(event.getSelectedItem().getId(), event.getSelectedItem()
                                                                                               .getTitle()));
            }
        });

        getOrUpdateAppList();
    }

    /**
     *
     */
    private void getOrUpdateAppList() {
        try {
            CloudBeesClientService.getInstance().applicationList(
                    new CloudBeesAsyncRequestCallback<List<ApplicationInfo>>(new ApplicationListUnmarshaller(
                            new ArrayList<ApplicationInfo>()), new LoggedInHandler() {

                        @Override
                        public void onLoggedIn() {
                            getOrUpdateAppList();
                        }
                    }, null) {

                        @Override
                        protected void onSuccess(List<ApplicationInfo> result) {
                            display.getAppListGrid().setValue(result);
                        }
                    });
        } catch (RequestException e) {
            IDE.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** @see org.exoplatform.ide.extension.cloudbees.client.list.ShowApplicationListHandler#onShowApplicationList(org.exoplatform.ide
     * .extension.cloudbees.client.list.ShowApplicationListEvent) */
    @Override
    public void onShowApplicationList(ShowApplicationListEvent event) {
        if (display == null) {
            display = GWT.create(Display.class);
            IDE.getInstance().openView(display.asView());
            bind();
        } else {
            display.asView().activate();
        }
    }

    /** @see org.exoplatform.ide.client.framework.output.event.OutputHandler#onOutput(org.exoplatform.ide.client.framework.output.event.OutputEvent) */
    @Override
    public void onOutput(OutputEvent event) {
        outputHandler.removeHandler();
        getOrUpdateAppList();
    }
}
