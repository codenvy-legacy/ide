package com.codenvy.ide.ext.gae.client.project.general.logs;

import com.codenvy.ide.api.parts.ConsolePart;
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
import com.codenvy.ide.part.base.BasePresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 06.08.13 vlad $
 */
@Singleton
public class LogsPresenter extends BasePresenter implements LogsView.ActionDelegate {
    private LogsView         view;
    private GAEClientService service;
    private EventBus         eventBus;
    private ConsolePart      console;
    private LoginAction      loginAction;
    private GAEResources     resources;
    private ResourceProvider resourceProvider;
    private WorkspaceAgent   workspaceAgent;
    private GAELocalization  constant;
    private Project          project;

    @Inject
    public LogsPresenter(LogsView view, GAEClientService service, EventBus eventBus,
                         ConsolePart console, LoginAction loginAction, GAEResources resources,
                         ResourceProvider resourceProvider, WorkspaceAgent workspaceAgent, GAELocalization constant) {
        this.view = view;
        this.service = service;
        this.eventBus = eventBus;
        this.console = console;
        this.loginAction = loginAction;
        this.resources = resources;
        this.resourceProvider = resourceProvider;
        this.workspaceAgent = workspaceAgent;
        this.constant = constant;

        this.view.setDelegate(this);
        this.view.setTitle("Logs");
    }

    public void showDialog(Project project) {
        this.project = project;

        workspaceAgent.openPart(this, PartStackType.INFORMATION);
        PartPresenter activePart = partStack.getActivePart();
        if (activePart != null && !activePart.equals(this)) {
            partStack.setActivePart(this);
        }

        getLogs();
    }

    @Override
    public void onGetLogsButtonClicked() {
        getLogs();
    }

    public void getLogs() {
        String severity = view.getLogsSeverity();
        int numDays = view.getLogsDaysCount();

        //Need to transform num days into enum values

        final String vfsId = resourceProvider.getVfsId();

        StringUnmarshaller unmarshaller = new StringUnmarshaller(new StringBuilder());

        try {
            service.requestLogs(vfsId, project.getId(), numDays, severity,
                                new GAEAsyncRequestCallback<StringBuilder>(unmarshaller, console, eventBus,
                                                                           constant, loginAction) {
                                    @Override
                                    protected void onSuccess(StringBuilder result) {
                                        view.setLogsContent(result.toString());
                                    }
                                });
        } catch (RequestException e) {
            eventBus.fireEvent(new ExceptionThrownEvent(e));
        }
    }

    @Override
    public String getTitle() {
        return "Logs";
    }

    @Override
    public ImageResource getTitleImage() {
        return resources.logs();
    }

    @Override
    public String getTitleToolTip() {
        return "Display logs content.";
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
    }
}
