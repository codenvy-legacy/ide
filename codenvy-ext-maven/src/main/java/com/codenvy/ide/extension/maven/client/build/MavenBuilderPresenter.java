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
package com.codenvy.ide.extension.maven.client.build;

import com.codenvy.api.builder.dto.BuildOptions;
import com.codenvy.api.builder.gwt.client.BuilderServiceClient;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.builder.client.build.BuildProjectPresenter;
import com.codenvy.ide.extension.builder.client.console.BuilderConsolePresenter;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.MessageBus;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for customizing building Maven project.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class MavenBuilderPresenter extends BuildProjectPresenter implements MavenBuildView.ActionDelegate {
    private MavenBuildView view;

    /** Create presenter. */
    @Inject
    protected MavenBuilderPresenter(EventBus eventBus,
                                    MavenBuildView view,
                                    ResourceProvider resourceProvider,
                                    BuilderConsolePresenter console,
                                    BuilderServiceClient service,
                                    BuilderLocalizationConstant constant,
                                    WorkspaceAgent workspaceAgent,
                                    MessageBus messageBus,
                                    NotificationManager notificationManager,
                                    DtoFactory dtoFactory,
                                    DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(eventBus, workspaceAgent, resourceProvider, console, service, constant, notificationManager, dtoFactory,
              dtoUnmarshallerFactory, messageBus);
        this.view = view;
        this.view.setDelegate(this);
        this.view.setBuildCommand("clean install");
    }

    public void showDialog() {
        view.showDialog();
    }

    @Override
    public void onStartBuildClicked() {
        buildActiveProject(getBuildOptions());
        view.close();
    }

    private BuildOptions getBuildOptions() {//TODO : need create smarter parser for command line
        BuildOptions buildOptions = dtoFactory.createDto(BuildOptions.class);
        buildOptions.setSkipTest(view.isSkipTestSelected());
        String buildCommand = view.getBuildCommand();
        if (buildCommand != null && !buildCommand.isEmpty()) {
            Map<String, String> options = new HashMap<>();
            List<String> targets = new ArrayList<>();

            String[] splited = buildCommand.split("\\s+");
            for (int i = 0; i < splited.length; i++) {
                String str = splited[i];
                if (str.startsWith("-")) {
                    if (str.contains("=")) {
                        String[] split = str.split("=");
                        options.put(split[0], split[1]);
                    } else
                        options.put(str, null);
                } else {
                    targets.add(str);
                }
            }
            buildOptions.setOptions(options);
            buildOptions.setTargets(targets);
        }
        return buildOptions;
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

    @Override
    public void onSkipTestValueChange(ValueChangeEvent<Boolean> event) {
        if (event.getValue()) {
            String command = view.getBuildCommand().concat(" -Dmaven.test.skip");
            view.setBuildCommand(command.replaceAll("\\s+", " "));
        } else {
            String buildCommand = view.getBuildCommand();
            view.setBuildCommand(buildCommand.replaceAll("-Dmaven.test.skip", "").replaceAll("\\s+", " ")); //TODO: need improve it
        }
    }

    @Override
    public void onUpdateSnapshotValueChange(ValueChangeEvent<Boolean> event) {
        if (event.getValue()) {
            String command = view.getBuildCommand().concat(" -U");
            view.setBuildCommand(command.replaceAll("\\s+", " "));
        } else {
            String buildCommand = view.getBuildCommand();
            view.setBuildCommand(
                    buildCommand.replaceAll("-U", "").replaceAll("--update-snapshots", "").replaceAll("\\s+", " ")); //TODO: need improve it
        }
    }

    @Override
    public void onOfflineValueChange(ValueChangeEvent<Boolean> event) {
        if (event.getValue()) {
            String command = view.getBuildCommand().concat(" -o");
            view.setBuildCommand(command.replaceAll("\\s+", " "));
        } else {
            String buildCommand = view.getBuildCommand();
            view.setBuildCommand(
                    buildCommand.replaceAll("-o", "").replaceAll("--offline", "").replaceAll("\\s+", " ")); //TODO: need improve it
        }
    }
}