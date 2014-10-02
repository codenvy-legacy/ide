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
package com.codenvy.ide.importproject;

import com.codenvy.api.core.rest.shared.dto.ServiceError;
import com.codenvy.api.project.gwt.client.ProjectImportersServiceClient;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ImportSourceDescriptor;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.api.project.shared.dto.ProjectImporterDescriptor;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentConfigurationDescriptor;
import com.codenvy.api.runner.dto.ResourcesDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.event.OpenProjectEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttype.wizard.ProjectWizard;
import com.codenvy.ide.api.wizard.WizardContext;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.commons.exception.UnauthorizedException;
import com.codenvy.ide.commons.exception.UnmarshallerException;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.ui.dialogs.info.InfoHandler;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.SubscriptionHandler;
import com.codenvy.ide.wizard.project.NewProjectWizardPresenter;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.regexp.shared.RegExp;
import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Provides importing project.
 *
 * @author Roman Nikitenko
 */
public class ImportProjectPresenter implements ImportProjectView.ActionDelegate {

    private static final RegExp HTTPS_URL_Pattern = RegExp.compile("((https|http)://)((([^\\\\\\\\@:;, (//)])+/){2,})[^\\\\\\\\@:; ,]+");
    private static final RegExp SSH_URL_Pattern   = RegExp.compile("((((git|ssh)://)(([^\\\\/@:]+@)??)[^\\\\/@:]+)(:|/)|" +
                                                                   "([^\\\\/@:]+@[^\\\\/@:]+):)[^\\\\@:]+");
    private ProjectServiceClient                   projectServiceClient;
    private RunnerServiceClient                    runnerServiceClient;
    private NotificationManager                    notificationManager;
    private CoreLocalizationConstant               locale;
    private DtoFactory                             dtoFactory;
    private ImportProjectView                      view;
    private ProjectImportersServiceClient          projectImportersService;
    private DtoUnmarshallerFactory                 dtoUnmarshallerFactory;
    private NewProjectWizardPresenter              wizardPresenter;
    private EventBus                               eventBus;
    private Map<String, ProjectImporterDescriptor> importers;
    private String                                 workspaceId;
    private MessageBus                             messageBus;

    @Inject
    public ImportProjectPresenter(ProjectServiceClient projectServiceClient,
                                  RunnerServiceClient runnerServiceClient,
                                  NotificationManager notificationManager,
                                  CoreLocalizationConstant locale,
                                  DtoFactory dtoFactory,
                                  ImportProjectView view,
                                  ProjectImportersServiceClient projectImportersService,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                  NewProjectWizardPresenter wizardPresenter,
                                  EventBus eventBus,
                                  @Named("workspaceId") String workspaceId,
                                  MessageBus messageBus) {
        this.projectServiceClient = projectServiceClient;
        this.runnerServiceClient = runnerServiceClient;
        this.notificationManager = notificationManager;
        this.locale = locale;
        this.dtoFactory = dtoFactory;
        this.view = view;
        this.projectImportersService = projectImportersService;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.wizardPresenter = wizardPresenter;
        this.eventBus = eventBus;
        this.workspaceId = workspaceId;
        this.messageBus = messageBus;

        this.view.setDelegate(this);
    }

    /** Show dialog. */
    public void showDialog() {
        importers = new HashMap<>();
        view.setUri("");
        view.setProjectName("");
        final List<String> importersList = new ArrayList<>();
        //TODO: need add test on this
        projectImportersService.getProjectImporters(new AsyncRequestCallback<Array<ProjectImporterDescriptor>>(
                dtoUnmarshallerFactory.newArrayUnmarshaller(ProjectImporterDescriptor.class)) {
            @Override
            protected void onSuccess(Array<ProjectImporterDescriptor> result) {
                for (int i = 0; i < result.size(); i++) {
                    if (!result.get(i).isInternal()) {
                        importers.put(result.get(i).getId(), result.get(i));
                        importersList.add(result.get(i).getId());
                    }
                }
                view.setImporters(importersList);
                view.setEnabledImportButton(false);
                onImporterSelected();
                view.showDialog();
            }

            @Override
            protected void onFailure(Throwable exception) {
                Log.error(ImportProjectPresenter.class, "can not get project importers");
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onImportClicked() {
        final String url = view.getUri();
        final String projectName = view.getProjectName();

        if (!(SSH_URL_Pattern.test(url) || HTTPS_URL_Pattern.test(url))) {
            view.showWarning(locale.importProjectEnteredWrongUri());
            return;
        }

        projectServiceClient.getProject(projectName, new AsyncRequestCallback<ProjectDescriptor>() {
            @Override
            protected void onSuccess(ProjectDescriptor result) {
                //Project with the same name already exists
                view.showWarning(locale.createProjectFromTemplateProjectExists(projectName));
            }

            @Override
            protected void onFailure(Throwable exception) {
                //Project with the same name does not exist
                view.close();
                importProject(url, projectName);
            }
        });
    }

    private void importProject(String url, final String projectName) {

        final String wsChannel = "importProject:output:" + workspaceId + ":" + projectName;
        final Notification notification = new Notification(locale.importingProject(), Notification.Status.PROGRESS);

        final SubscriptionHandler<String> importProjectOutputWShandler = new SubscriptionHandler<String>(new LineUnmarshaller()) {
            @Override
            protected void onMessageReceived(String result) {
                notification.setMessage(locale.importingProject() + " " + result);
            }

            @Override
            protected void onErrorReceived(Throwable throwable) {
                try {
                    messageBus.unsubscribe(wsChannel, this);
                    notification.setType(Notification.Type.ERROR);
                    notification.setImportant(true);
                    notification.setMessage(locale.importProjectMessageFailure() + " " + throwable.getMessage());
                    Log.error(getClass(), throwable);
                } catch (WebSocketException e) {
                    Log.error(getClass(), e);
                }
            }
        };

        try {
            messageBus.subscribe(wsChannel, importProjectOutputWShandler);
        } catch (WebSocketException e1) {
            Log.error(ImportProjectPresenter.class, e1);
        }


        String importer = view.getImporter();
        ImportSourceDescriptor importSourceDescriptor =
                dtoFactory.createDto(ImportSourceDescriptor.class).withType(importer).withLocation(url);
        Unmarshallable<ProjectDescriptor> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ProjectDescriptor.class);

        notificationManager.showNotification(notification);

        projectServiceClient
                .importProject(projectName, false, importSourceDescriptor, new AsyncRequestCallback<ProjectDescriptor>(unmarshaller) {
                    @Override
                    protected void onSuccess(ProjectDescriptor result) {
                        checkRam(result, importProjectOutputWShandler);
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        String errorMessage;
                        if (exception instanceof UnauthorizedException) {
                            ServiceError serverError =
                                    dtoFactory.createDtoFromJson(((UnauthorizedException)exception).getResponse().getText(),
                                                                 ServiceError.class);
                            errorMessage = serverError.getMessage();
                        } else {
                            Log.error(ImportProjectPresenter.class, "can not import project: " + exception);
                            errorMessage = exception.getMessage();
                        }

                        try {
                            messageBus.unsubscribe(wsChannel, importProjectOutputWShandler);
                        } catch (WebSocketException e) {
                            Log.error(getClass(), e);
                        }
                        notification.setStatus(Notification.Status.FINISHED);
                        notification.setType(Notification.Type.ERROR);
                        notification.setImportant(true);
                        notification.setMessage(locale.importProjectMessageFailure() + " " + exception.getMessage());

                        view.showWarning(errorMessage);
                        deleteFolder(projectName);
                    }
                });
    }

    private void checkRam(final ProjectDescriptor projectDescriptor, final SubscriptionHandler<String> importProjectOutputWShandler) {
        int requiredMemorySize = 0;
        Map<String, RunnerEnvironmentConfigurationDescriptor> runEnvConfigurations = projectDescriptor.getRunnerEnvironmentConfigurations();
        String defaultRunnerEnvironment = projectDescriptor.getDefaultRunnerEnvironment();

        if (runEnvConfigurations != null && defaultRunnerEnvironment != null &&
            runEnvConfigurations.containsKey(defaultRunnerEnvironment)) {
            RunnerEnvironmentConfigurationDescriptor runEnvConfDescriptor = runEnvConfigurations.get(defaultRunnerEnvironment);
            requiredMemorySize = runEnvConfDescriptor.getRequiredMemorySize();
        }

        if (requiredMemorySize > 0) {
            final int finalRequiredMemorySize = requiredMemorySize;
            runnerServiceClient.getResources(
                    new AsyncRequestCallback<ResourcesDescriptor>(dtoUnmarshallerFactory.newUnmarshaller(ResourcesDescriptor.class)) {
                        @Override
                        protected void onSuccess(ResourcesDescriptor result) {
                            int workspaceMemory = Integer.valueOf(result.getTotalMemory());
                            if (workspaceMemory < finalRequiredMemorySize) {
                                final Info warningWindow = new Info(locale.createProjectWarningTitle(),
                                                                    locale.messagesWorkspaceRamLessRequiredRam(finalRequiredMemorySize,
                                                                                                               workspaceMemory),
                                                                    new InfoHandler() {
                                                                        @Override
                                                                        public void onOk() {
                                                                            importSuccessful(projectDescriptor,
                                                                                             importProjectOutputWShandler);
                                                                        }
                                                                    }
                                );
                                warningWindow.show();
                            }
                        }

                        @Override
                        protected void onFailure(Throwable exception) {
                            importSuccessful(projectDescriptor, importProjectOutputWShandler);

                            Info infoWindow = new Info(locale.createProjectWarningTitle(), locale.messagesGetResourcesFailed());
                            infoWindow.show();
                            Log.error(getClass(), exception.getMessage());
                        }
                    });
            return;
        }
        importSuccessful(projectDescriptor, importProjectOutputWShandler);
    }

    private void importSuccessful(ProjectDescriptor projectDescriptor, SubscriptionHandler<String> importProjectOutputWShandler) {
        final String wsChannel = "importProject:output:" + workspaceId + ":" + projectDescriptor.getName();
        try {
            messageBus.unsubscribe(wsChannel, importProjectOutputWShandler);
        } catch (WebSocketException e) {
            Log.error(getClass(), e);
        }

        final Notification notification = new Notification(locale.importProjectMessageSuccess(), Notification.Status.FINISHED);
        notificationManager.showNotification(notification);

        eventBus.fireEvent(new OpenProjectEvent(projectDescriptor.getName()));

        if (projectDescriptor.getProjectTypeId() == null ||
            com.codenvy.api.project.shared.Constants.BLANK_ID.equals(projectDescriptor.getProjectTypeId())) {

            WizardContext context = new WizardContext();
            context.putData(ProjectWizard.PROJECT_FOR_UPDATE, projectDescriptor);
            wizardPresenter.show(context);
        }
    }

    static class LineUnmarshaller implements com.codenvy.ide.websocket.rest.Unmarshallable<String> {
        private String line;

        @Override
        public void unmarshal(Message response) throws UnmarshallerException {
            JSONObject jsonObject = JSONParser.parseStrict(response.getBody()).isObject();
            if (jsonObject == null) {
                return;
            }
            if (jsonObject.containsKey("line")) {
                line = jsonObject.get("line").isString().stringValue();
            }
        }

        @Override
        public String getPayload() {
            return line;
        }
    }

    private void deleteFolder(String name) {
        projectServiceClient.delete(name, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
            }

            @Override
            protected void onFailure(Throwable exception) {
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onUriChanged() {
        String projectName = view.getProjectName();
        String uri = view.getUri();

        if (projectName.isEmpty() && !uri.isEmpty()) {
            projectName = parseUri(uri);
            view.setProjectName(projectName);
        }
        onProjectNameChanged();
    }

    @Override
    public void onProjectNameChanged() {
        String projectName = view.getProjectName();
        String uri = view.getUri();
        boolean enable = !uri.isEmpty() && !projectName.isEmpty();

        view.setEnabledImportButton(enable);
    }

    @Override
    public void onImporterSelected() {
        String importer = view.getImporter();
        view.setDescription(importers.get(importer).getDescription());

    }

    /** Gets project name from uri. */
    private String parseUri(String uri) {
        String result;
        int indexStartProjectName = uri.lastIndexOf("/") + 1;
        int indexFinishProjectName = uri.indexOf(".", indexStartProjectName);
        if (indexStartProjectName != 0 && indexFinishProjectName != (-1)) {
            result = uri.substring(indexStartProjectName, indexFinishProjectName);
        } else if (indexStartProjectName != 0) {
            result = uri.substring(indexStartProjectName);
        } else {
            result = "";
        }
        return result;
    }
}
