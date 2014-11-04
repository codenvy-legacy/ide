/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.core;

import elemental.client.Browser;

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunnerMetric;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.api.runner.gwt.client.utils.RunnerUtils;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.event.CloseCurrentProjectEvent;
import com.codenvy.ide.api.event.CloseCurrentProjectHandler;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.event.OpenProjectHandler;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectDescriptorChangedEvent;
import com.codenvy.ide.api.event.ProjectDescriptorChangedHandler;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.wizard.WizardContext;
import com.codenvy.ide.core.problemDialog.ProjectProblemDialog;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.util.Config;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.wizard.project.NewProjectWizardPresenter;
import com.google.gwt.core.client.Callback;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.dom.client.Document;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;

import static com.codenvy.api.runner.dto.RunnerMetric.ALWAYS_ON;
import static com.codenvy.api.runner.dto.RunnerMetric.TERMINATION_TIME;
import static com.codenvy.api.runner.internal.Constants.LINK_REL_STOP;
import static com.codenvy.ide.api.projecttype.wizard.ProjectWizard.PROJECT_FOR_UPDATE;

/**
 * Component that does some preliminary operations before opening/closing projects.
 * <p/>
 * E.g.:
 * <ul>
 * <li>closing already opened project before opening another one;</li>
 * <li>setting Browser tab's title;</li>
 * <li>rewriting URL in Browser's address bar;</li>
 * <li>setting {@link CurrentProject} to {@link AppContext};</li>
 * <li>etc.</li>
 * </ul>
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class ProjectStateHandler implements Component, OpenProjectHandler, CloseCurrentProjectHandler, ProjectDescriptorChangedHandler {
    private EventBus                  eventBus;
    private AppContext                appContext;
    private ProjectServiceClient      projectServiceClient;
    private RunnerServiceClient       runnerServiceClient;
    private NewProjectWizardPresenter newProjectWizard;
    private DtoUnmarshallerFactory    dtoUnmarshallerFactory;
    private CoreLocalizationConstant  constant;
    private DialogFactory             dialogFactory;

    @Inject
    public ProjectStateHandler(AppContext appContext, EventBus eventBus, ProjectServiceClient projectServiceClient,
                               RunnerServiceClient runnerServiceClient, NewProjectWizardPresenter newProjectWizard,
                               CoreLocalizationConstant constant, DtoUnmarshallerFactory dtoUnmarshallerFactory,
                               DialogFactory dialogFactory) {
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.projectServiceClient = projectServiceClient;
        this.runnerServiceClient = runnerServiceClient;
        this.newProjectWizard = newProjectWizard;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.constant = constant;
        this.dialogFactory = dialogFactory;
    }

    @Override
    public void start(Callback<Component, ComponentException> callback) {
        eventBus.addHandler(OpenProjectEvent.TYPE, this);
        eventBus.addHandler(CloseCurrentProjectEvent.TYPE, this);
        eventBus.addHandler(ProjectDescriptorChangedEvent.TYPE, this);
        callback.onSuccess(this);
    }

    @Override
    public void onOpenProject(final OpenProjectEvent event) {
        if (appContext.getCurrentProject() == null) {
            tryOpenProject(event.getProjectName());
        } else {
            // previously opened project should be closed correctly
            checkRunnerAndCloseCurrentProject(new CloseCallback() {
                @Override
                public void onClosed() {
                    Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {
                        @Override
                        public void execute() {
                            tryOpenProject(event.getProjectName());
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onCloseCurrentProject(CloseCurrentProjectEvent event) {
        checkRunnerAndCloseCurrentProject(null);
    }

    @Override
    public void onProjectDescriptorChanged(ProjectDescriptorChangedEvent event) {
        final String path = event.getProjectDescriptor().getPath();
        if (appContext.getCurrentProject().getProjectDescription().getPath().equals(path)) {
            appContext.getCurrentProject().setProjectDescription(event.getProjectDescriptor());
        }
    }

    private void checkRunnerAndCloseCurrentProject(CloseCallback closeCallback) {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        final ApplicationProcessDescriptor appProcess = appContext.getCurrentProject().getProcessDescriptor();
        boolean alwaysOn = false;
        if (appProcess != null) {
            for (RunnerMetric metric : appProcess.getRunStats()) {
                if (TERMINATION_TIME.equals(metric.getName()) && ALWAYS_ON.equals(metric.getValue())) {
                    alwaysOn = true;
                    break;
                }
            }
        }

        if (appProcess == null || alwaysOn) {
            closeCurrentProject();
            if (closeCallback != null) {
                closeCallback.onClosed();
            }
        } else {
            dialogFactory.createConfirmDialog(constant.closeProjectTitle(),
                                              constant.appWillBeStopped(currentProject.getProjectDescription().getName()),
                                              new ConfirmStoppingAppCallback(closeCallback), null).show();
        }
    }

    private void closeCurrentProject() {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            ProjectDescriptor closedProject = currentProject.getRootProject();
            appContext.setCurrentProject(null);

            Document.get().setTitle(constant.codenvyTabTitle());
            rewriteBrowserHistory(null);

            // notify all listeners about current project has been closed
            eventBus.fireEvent(ProjectActionEvent.createProjectClosedEvent(closedProject));
        }
    }

    private boolean hasProblems(ProjectDescriptor project) {
        return !project.getProblems().isEmpty();
    }

    private void tryOpenProject(String projectName) {
        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);
        projectServiceClient.getProject(projectName, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
            @Override
            protected void onSuccess(ProjectDescriptor project) {
                if (hasProblems(project)) {
                    openProblemProject(project);
                } else {
                    openProject(project);
                }
            }

            @Override
            protected void onFailure(Throwable throwable) {
                Log.error(ProjectStateHandler.class, throwable);
            }
        });
    }

    private void openProject(ProjectDescriptor project) {
        appContext.setCurrentProject(new CurrentProject(project));

        Document.get().setTitle(constant.codenvyTabTitle(project.getName()));
        rewriteBrowserHistory(project.getName());

        // notify all listeners about opening project
        eventBus.fireEvent(ProjectActionEvent.createProjectOpenedEvent(project));
    }

    private void openProblemProject(final ProjectDescriptor project) {
        ProjectProblemDialog dialog = new ProjectProblemDialog(
                constant.projectProblemTitle(),
                constant.projectProblemMessage(),
                new ProjectProblemDialog.AskHandler() {
                    @Override
                    public void onConfigure() {
                        openProject(project);
                        // open 'Project Configuration' wizard
                        final WizardContext context = new WizardContext();
                        context.putData(PROJECT_FOR_UPDATE, appContext.getCurrentProject().getProjectDescription());
                        newProjectWizard.show(context);
                    }

                    @Override
                    public void onDelete() {
                        deleteProject(project);
                    }
                });
        dialog.show();
    }

    private void rewriteBrowserHistory(@Nullable String projectName) {
        StringBuilder url = new StringBuilder();
        url.append(Config.getContext()).append('/').append(Config.getWorkspaceName());
        if (projectName != null) {
            url.append('/').append(projectName);
        }
        Browser.getWindow().getHistory().replaceState(null, Window.getTitle(), url.toString());
    }

    private void deleteProject(ProjectDescriptor project) {
        projectServiceClient.delete(project.getPath(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                eventBus.fireEvent(new RefreshProjectTreeEvent());
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(ProjectStateHandler.class, exception);
            }
        });
    }

    private interface CloseCallback {
        void onClosed();
    }

    private class ConfirmStoppingAppCallback implements ConfirmCallback {
        final ProjectStateHandler.CloseCallback closeCallback;

        ConfirmStoppingAppCallback(CloseCallback closeCallback) {
            this.closeCallback = closeCallback;
        }

        @Override
        public void accepted() {
            final Link stopLink = RunnerUtils.getLink(appContext.getCurrentProject().getProcessDescriptor(), LINK_REL_STOP);
            if (stopLink != null) {
                runnerServiceClient.stop(stopLink, new AsyncRequestCallback<ApplicationProcessDescriptor>() {
                    @Override
                    protected void onSuccess(ApplicationProcessDescriptor applicationProcessDescriptor) {
                        closeCurrentProject();
                        if (closeCallback != null) {
                            closeCallback.onClosed();
                        }
                    }

                    @Override
                    protected void onFailure(Throwable ignore) {
                    }
                });
            }
        }
    }
}
