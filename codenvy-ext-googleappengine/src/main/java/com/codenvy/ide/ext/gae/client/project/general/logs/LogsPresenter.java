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

package com.codenvy.ide.ext.gae.client.project.general.logs;

import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.base.BasePresenter;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.PartPresenter;
import com.codenvy.ide.api.ui.workspace.PartStackType;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.gae.client.GAEAsyncRequestCallback;
import com.codenvy.ide.ext.gae.client.GAEClientService;
import com.codenvy.ide.ext.gae.client.GAELocalization;
import com.codenvy.ide.ext.gae.client.GAEResources;
import com.codenvy.ide.ext.gae.client.actions.LoginAction;
import com.codenvy.ide.ext.gae.client.marshaller.StringUnmarshaller;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter to allow user to show application startup logs on Google App Engine.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 06.08.13 vlad $
 */
@Singleton
public class LogsPresenter extends BasePresenter implements LogsView.ActionDelegate {
    private LogsView            view;
    private GAEClientService    service;
    private EventBus            eventBus;
    private LoginAction         loginAction;
    private GAEResources        resources;
    private ResourceProvider    resourceProvider;
    private WorkspaceAgent      workspaceAgent;
    private GAELocalization     constant;
    private NotificationManager notificationManager;
    private Project             project;

    private JsonStringMap<String> severityFormatted = JsonCollections.createStringMap();

    {
        severityFormatted.put("All", "");
        severityFormatted.put("Error", "ERROR");
        severityFormatted.put("Info", "INFO");
        severityFormatted.put("Warning", "WARNING");
        severityFormatted.put("Debug", "DEBUG");
        severityFormatted.put("Critical", "CRITICAL");
    }

    /** Constructor for application logs presenter. */
    @Inject
    public LogsPresenter(LogsView view, GAEClientService service, EventBus eventBus, LoginAction loginAction, GAEResources resources,
                         ResourceProvider resourceProvider, WorkspaceAgent workspaceAgent, GAELocalization constant,
                         NotificationManager notificationManager) {
        this.view = view;
        this.service = service;
        this.eventBus = eventBus;
        this.loginAction = loginAction;
        this.resources = resources;
        this.resourceProvider = resourceProvider;
        this.workspaceAgent = workspaceAgent;
        this.constant = constant;
        this.notificationManager = notificationManager;

        this.view.setDelegate(this);
        this.view.setTitle("Logs");
    }

    /** Show current based dialog, placed in information panel of IDE. */
    public void showDialog(Project project) {
        this.project = project;

        workspaceAgent.openPart(this, PartStackType.INFORMATION);
        PartPresenter activePart = partStack.getActivePart();
        if (activePart != null && !activePart.equals(this)) {
            partStack.setActivePart(this);
        }

        getLogs();
    }

    /** {@inheritDoc} */
    @Override
    public void onGetLogsButtonClicked() {
        getLogs();
    }

    /** Request logs from Google App Engine. */
    public void getLogs() {
        String severity = severityFormatted.get(view.getLogsSeverity());
        int numDays = view.getLogsDaysCount();
        final String vfsId = resourceProvider.getVfsInfo().getId();
        StringUnmarshaller unmarshaller = new StringUnmarshaller();

        try {
            service.requestLogs(vfsId, project.getId(), numDays, severity,
                                new GAEAsyncRequestCallback<String>(unmarshaller, eventBus, constant, loginAction, notificationManager) {
                                    @Override
                                    protected void onSuccess(String result) {
                                        view.setLogsContent(result);
                                    }
                                });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    /** {@inheritDoc} */
    @Override
    public String getTitle() {
        return "Logs";
    }

    /** {@inheritDoc} */
    @Override
    public ImageResource getTitleImage() {
        return resources.logs();
    }

    /** {@inheritDoc} */
    @Override
    public String getTitleToolTip() {
        return "Display logs content.";
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
