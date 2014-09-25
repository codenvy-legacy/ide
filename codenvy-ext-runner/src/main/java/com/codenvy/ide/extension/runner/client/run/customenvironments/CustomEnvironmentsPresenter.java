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
package com.codenvy.ide.extension.runner.client.run.customenvironments;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.codenvy.ide.ui.dialogs.askValue.AskValueCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueDialog;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Drives the process of editing custom environments.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class CustomEnvironmentsPresenter implements CustomEnvironmentsView.ActionDelegate {
    private final String                     envFolderPath;
    private       NotificationManager        notificationManager;
    private       RunnerLocalizationConstant constants;
    private       ProjectServiceClient       projectServiceClient;
    private       DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private       EventBus                   eventBus;
    private       AppContext                 appContext;
    private       EnvironmentActionsManager  environmentActionsManager;
    private       CustomEnvironmentsView     view;
    private       CustomEnvironment          selectedEnvironment;

    /** Create presenter. */
    @Inject
    protected CustomEnvironmentsPresenter(@Named("envFolderPath") String envFolderPath, CustomEnvironmentsView view,
                                          EventBus eventBus,
                                          AppContext appContext, EnvironmentActionsManager environmentActionsManager,
                                          ProjectServiceClient projectServiceClient,
                                          DtoUnmarshallerFactory dtoUnmarshallerFactory, NotificationManager notificationManager,
                                          RunnerLocalizationConstant constants) {
        this.envFolderPath = envFolderPath;
        this.view = view;
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.environmentActionsManager = environmentActionsManager;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.constants = constants;
        this.view.setDelegate(this);

        updateView();
    }

    /** {@inheritDoc} */
    @Override
    public void onAddClicked() {
        new AskValueDialog(constants.customEnvironmentsViewAddNewEnvTitle(),
                           constants.customEnvironmentsViewAddNewEnvMessage(),
                           new AskValueCallback() {
                               @Override
                               public void onOk(final String value) {
                                   createEnvironment(value);
                               }
                           }).show();
    }

    private void createEnvironment(String name) {
        final Unmarshallable<ItemReference> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ItemReference.class);
        final String path = appContext.getCurrentProject().getProjectDescription().getPath() + '/' + envFolderPath + '/' + name;
        projectServiceClient.createFolder(path, new AsyncRequestCallback<ItemReference>(unmarshaller) {
            @Override
            protected void onSuccess(ItemReference result) {
                final CustomEnvironment env = new CustomEnvironment(result.getName());
                createScriptFilesForEnvironment(env);
                environmentActionsManager.addActionForEnvironment(env);
                refreshEnvironmentsList();
                updateView();
            }

            @Override
            protected void onFailure(Throwable exception) {
                notificationManager.showNotification(new Notification(exception.getMessage(), ERROR));
            }
        });
    }

    private void createScriptFilesForEnvironment(CustomEnvironment env) {
        final String path = appContext.getCurrentProject().getProjectDescription().getPath() + '/' + envFolderPath + '/';
        for (String scriptName : env.getScriptNames(true)) {
            projectServiceClient.createFile(path, scriptName, "", null, new AsyncRequestCallback<ItemReference>() {
                @Override
                protected void onSuccess(ItemReference result) {
                }

                @Override
                protected void onFailure(Throwable ignore) {
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoveClicked() {
        new Ask(constants.customEnvironmentsViewRemoveEnvTitle(),
                constants.customEnvironmentsViewRemoveEnvMessage(selectedEnvironment.getName()),
                new AskHandler() {
                    @Override
                    public void onOk() {
                        removeSelectedEnvironment();
                    }
                }).show();
    }

    private void removeSelectedEnvironment() {
        final String path = appContext.getCurrentProject().getProjectDescription().getPath() + '/' + envFolderPath + '/' +
                            selectedEnvironment.getName();
        projectServiceClient.delete(path, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                environmentActionsManager.removeActionForEnvironment(selectedEnvironment);
                selectedEnvironment = null;
                updateView();
                refreshEnvironmentsList();
            }

            @Override
            protected void onFailure(Throwable exception) {
                notificationManager.showNotification(new Notification(exception.getMessage(), ERROR));
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onEditClicked() {
        view.closeDialog();

        final String path = appContext.getCurrentProject().getProjectDescription().getPath() + '/' + envFolderPath + '/' +
                            selectedEnvironment.getName();
        final Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.getChildren(path, new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ItemReference> result) {
                for (ItemReference item : result.asIterable()) {
                    eventBus.fireEvent(new FileEvent(new FileNode(null, item, eventBus, projectServiceClient, dtoUnmarshallerFactory),
                                                     FileEvent.FileOperation.OPEN));
                }
            }

            @Override
            protected void onFailure(Throwable ignore) {
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.closeDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onEnvironmentSelected(CustomEnvironment customEnvironment) {
        selectedEnvironment = customEnvironment;
        updateView();
    }

    /** Show dialog. */
    public void showDialog() {
        selectedEnvironment = null;
        updateView();
        view.showDialog();
        refreshEnvironmentsList();
    }

    private void updateView() {
        view.setEditButtonEnabled(selectedEnvironment != null);
        view.setRemoveButtonEnabled(selectedEnvironment != null);
    }

    private void refreshEnvironmentsList() {
        view.setEnvironments(Collections.<CustomEnvironment>createArray());

        environmentActionsManager.requestCustomEnvironmentsForProject(
                appContext.getCurrentProject().getProjectDescription(),
                new AsyncCallback<Array<CustomEnvironment>>() {
                    @Override
                    public void onSuccess(Array<CustomEnvironment> result) {
                        view.setEnvironments(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        if (caught.getMessage().contains("doesn't exist")) {
                            createEnvironmentsFolder();
                        } else {
                            Notification notification = new Notification(constants.retrievingImagesFailed(caught.getMessage()), ERROR);
                            notificationManager.showNotification(notification);
                        }
                    }
                });
    }

    private void createEnvironmentsFolder() {
        projectServiceClient.createFolder(
                appContext.getCurrentProject().getProjectDescription().getPath() + '/' + envFolderPath,
                new AsyncRequestCallback<ItemReference>() {
                    @Override
                    protected void onSuccess(ItemReference result) {
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(CustomEnvironmentsPresenter.class, exception.getMessage());
                    }
                });
    }
}
