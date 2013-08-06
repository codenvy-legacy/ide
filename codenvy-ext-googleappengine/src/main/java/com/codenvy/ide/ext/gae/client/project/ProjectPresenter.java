package com.codenvy.ide.ext.gae.client.project;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.gae.client.GAEExtension;
import com.codenvy.ide.ext.gae.client.actions.LoginAction;
import com.codenvy.ide.ext.gae.client.project.backend.BackendTabPanePresenter;
import com.codenvy.ide.ext.gae.client.project.cron.CronTabPanePresenter;
import com.codenvy.ide.ext.gae.client.project.general.GeneralTabPanePresenter;
import com.codenvy.ide.ext.gae.client.project.limit.LimitTabPanePresenter;
import com.codenvy.ide.resources.model.Project;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 05.08.13 vlad $
 */
@Singleton
public class ProjectPresenter implements ProjectView.ActionDelegate {
    private ProjectView             view;
    private LoginAction             loginAction;
    private ResourceProvider        resourceProvider;
    private GeneralTabPanePresenter generalTabPanePresenter;
    private LimitTabPanePresenter   limitTabPanePresenter;
    private CronTabPanePresenter    cronTabPanePresenter;
    private BackendTabPanePresenter backendTabPanePresenter;

    @Inject
    public ProjectPresenter(ProjectView view, LoginAction loginAction, ResourceProvider resourceProvider,
                            GeneralTabPanePresenter generalTabPanePresenter,
                            LimitTabPanePresenter limitTabPanePresenter, CronTabPanePresenter cronTabPanePresenter,
                            BackendTabPanePresenter backendTabPanePresenter) {
        this.view = view;
        this.loginAction = loginAction;
        this.resourceProvider = resourceProvider;
        this.generalTabPanePresenter = generalTabPanePresenter;
        this.limitTabPanePresenter = limitTabPanePresenter;
        this.cronTabPanePresenter = cronTabPanePresenter;
        this.backendTabPanePresenter = backendTabPanePresenter;

        this.view.setDelegate(this);

        AcceptsOneWidget generalTab = view.addTab("General");
        generalTabPanePresenter.go(generalTab);

        AcceptsOneWidget limitTab = view.addTab("Resource Limits");
        limitTabPanePresenter.go(limitTab);

        AcceptsOneWidget cronTab = view.addTab("Crons");
        cronTabPanePresenter.go(cronTab);

        AcceptsOneWidget backendTab = view.addTab("Backends");
        backendTabPanePresenter.go(backendTab);
    }

    public void showDialog() {
        final AsyncCallback<Boolean> onLoggedIn = new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                //ignore
            }

            @Override
            public void onSuccess(Boolean result) {
                if (result) {
                    showDialog();
                } else {
                    Window.alert(
                            "You aren't allowed to manage application on Google App Engine without authorization.");
                }
            }
        };

        AsyncCallback<Boolean> onIfUserLoggedIn = new AsyncCallback<Boolean>() {
            @Override
            public void onFailure(Throwable caught) {
                //ignore
            }

            @Override
            public void onSuccess(Boolean userLoggedIn) {
                if (userLoggedIn) {
                    init();
                } else {
                    loginAction.doLogin(onLoggedIn);
                }
            }
        };

        loginAction.isUserLoggedIn(onIfUserLoggedIn);
    }

    private void init() {
        Project project = resourceProvider.getActiveProject();

        if (!GAEExtension.isAppEngineProject(project)) {
            Window.alert("You should open Google App Engine application to manage it.");
            return;
        }

        generalTabPanePresenter.init(project);
        limitTabPanePresenter.init(project);
        cronTabPanePresenter.init(project);
        backendTabPanePresenter.init(project);

        if (!view.isShown()) {
            view.showDialog();
            view.focusFirstTab();
        }
    }

    @Override
    public void onCloseButtonClicked() {
        view.close();
    }
}
