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
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.extension.builder.client.BuilderLocalizationConstant;
import com.codenvy.ide.extension.builder.client.build.BuildProjectPresenter;
import com.codenvy.ide.extension.maven.client.MavenLocalizationConstant;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.MessageBus;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Presenter for build project with builder.
 *
 * @author Artem Zatsarynnyy
 */
//TODO: need rework for using websocket wait for server side
@Singleton
public class MavenBuilderPresenter extends BuildProjectPresenter
        implements Notification.OpenNotificationHandler, MavenBuildView.ActionDelegate {
    private       MavenBuildView            view;
    private final MavenLocalizationConstant constant;

    /**
     * Create presenter.
     */
    @Inject
    protected MavenBuilderPresenter(MavenBuildView view,
                                    EventBus eventBus,
                                    ResourceProvider resourceProvider,
                                    ConsolePart console,
                                    BuilderServiceClient service,
                                    BuilderLocalizationConstant constant,
                                    MavenLocalizationConstant mavenConstant,
                                    WorkspaceAgent workspaceAgent,
                                    MessageBus messageBus,
                                    NotificationManager notificationManager,
                                    DtoFactory dtoFactory,
                                    DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(eventBus, resourceProvider, console, service, constant, workspaceAgent, messageBus, notificationManager,
              dtoFactory, dtoUnmarshallerFactory);
        this.view = view;
        this.view.setDelegate(this);
        this.constant = mavenConstant;
    }


    public void showDialog() {
        view.showDialog();
    }


    @Override
    public void onStartBuildClicked() {
        String buildCommand = view.getBuildCommand();
        if (buildCommand == null || buildCommand.isEmpty()) {
            buildActiveProject(null);
        } else {
            BuildOptions buildOptions = dtoFactory.createDto(BuildOptions.class);
            buildOptions.setSkipTest(view.isSkipTestSelected());

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
                        options.put(str, "");
                } else {
                    targets.add(str);
                }
            }
            buildOptions.setOptions(options);
            buildOptions.setTargets(targets);
            buildActiveProject(buildOptions);
        }
        view.close();
    }

    @Override
    public void onCancelClicked() {
        view.close();
    }

}