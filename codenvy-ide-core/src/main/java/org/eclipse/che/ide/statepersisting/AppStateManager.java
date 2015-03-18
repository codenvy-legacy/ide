/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.statepersisting;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.api.user.shared.dto.ProfileDescriptor;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.ActionManager;
import org.eclipse.che.ide.api.action.Presentation;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.controlflow.Task;
import org.eclipse.che.ide.api.controlflow.TaskFlow;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.event.CloseCurrentProjectEvent;
import org.eclipse.che.ide.api.event.CloseCurrentProjectHandler;
import org.eclipse.che.ide.api.event.ProjectActionEvent;
import org.eclipse.che.ide.api.event.ProjectActionHandler;
import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.core.Component;
import org.eclipse.che.ide.core.ComponentException;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.rest.AsyncRequestLoader;
import org.eclipse.che.ide.restore.AppStateComponent;
import org.eclipse.che.ide.toolbar.PresentationFactory;
import org.eclipse.che.ide.util.loging.Log;

import javax.annotation.Nonnull;
import javax.inject.Provider;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * //
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class AppStateManager implements Component, ProjectActionHandler, CloseCurrentProjectHandler {
    /** The name of the property for the mappings in user preferences. */
    private static final String PREFERENCE_PROPERTY_NAME = "CodenvyAppState";

    /** The message displayed while waiting for saving state. */
    private final String waitSavingMessage;
    /** The message displayed while waiting for restoring state. */
    private final String waitRestoringMessage;

    private final AsyncRequestLoader    loader;
    private final PreferencesManager    preferencesManager;
    private final DtoFactory            dtoFactory;
    private final EventBus              eventBus;
    private final ActionManager         actionManager;
    private final PresentationFactory   presentationFactory;
    private final Provider<EditorAgent> editorAgentProvider;
    private final AppContext            appContext;

    private AppState myAppState;

    @Inject
    public AppStateManager(AsyncRequestLoader loader,
                           PreferencesManager preferencesManager,
                           DtoFactory dtoFactory,
                           EventBus eventBus,
                           ActionManager actionManager,
                           PresentationFactory presentationFactory,
                           Provider<EditorAgent> editorAgentProvider,
                           AppContext appContext,
                           CoreLocalizationConstant localizationConstant) {
        this.loader = loader;
        this.preferencesManager = preferencesManager;
        this.dtoFactory = dtoFactory;
        this.eventBus = eventBus;
        this.actionManager = actionManager;
        this.presentationFactory = presentationFactory;
        this.editorAgentProvider = editorAgentProvider;
        this.appContext = appContext;

        waitSavingMessage = localizationConstant.waitSavingMessage();
        waitRestoringMessage = localizationConstant.waitRestoringMessage();
    }

    @Override
    public void start(Callback<Component, ComponentException> callback) {
        eventBus.addHandler(ProjectActionEvent.TYPE, this);
//        callback.onSuccess(this);

        readStateFromPreferences();
    }

    @Override
    public void onProjectOpened(ProjectActionEvent event) {
        List<Task> tasks = new ArrayList<>();

        Action action1 = actionManager.getAction("showAbout");
        final Presentation presentation1 = presentationFactory.getPresentation(action1);
        ActionEvent e1 = new ActionEvent("", presentation1, actionManager, 0, null);

        Action action2 = actionManager.getAction("openProject");
        final Presentation presentation2 = presentationFactory.getPresentation(action2);
        ActionEvent e2 = new ActionEvent("", presentation2, actionManager, 0, null);

        tasks.add(new PerformActionTask(action1, e1));
        tasks.add(new PerformActionTask(action2, e2));

        TaskFlow taskFlow = new TaskFlow(tasks);

        taskFlow.runInSeries(new TaskFlow.FlowCallback() {
            @Override
            public void onDone() {
                // taskFlow has done
                Log.info(TaskFlow.class, "done");
            }

            @Override
            public void onError(Throwable exception) {
                // taskFlow has error
                Log.info(TaskFlow.class, "error");
            }
        });



        final String projectPath = event.getProject().getPath();

        // restore the state of the project's workspace

        final ProjectState stateToRestore = myAppState.getProjects().get(projectPath);
        if (stateToRestore != null) {
            restoreState(stateToRestore);
        }
    }

    /** Restore Codenvy application's state by calling all registered {@link AppStateComponent}s. */
    private void restoreState(@Nonnull final ProjectState projectState) {
//        final Map<String, Map<String, String>> actions = projectState.getActions();
//
//        final Iterator<AppStateComponent> iterator = appStateComponents.iterator();
//        if (!iterator.hasNext()) {
//            return;
//        }
//
//        final Callback callback = new Callback() {
//            @Override
//            public void onPerformed() {
//                if (iterator.hasNext()) {
//                    performRestore(iterator.next(), projectState, this);
//                } else {
//                    loader.hide(waitRestoringMessage);
//                }
//            }
//        };
//
//        loader.show(waitRestoringMessage);
//        performRestore(iterator.next(), projectState, callback);
    }

    @Override
    public void onProjectClosed(ProjectActionEvent event) {
        persist();
    }

    public void persist() {
        saveOpenedFiles();
        writeStateToPreferences();
    }

    private void saveOpenedFiles() {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        final String projectPath = currentProject.getRootProject().getPath();

        ProjectState projectState = dtoFactory.createDto(ProjectState.class);
        myAppState.getProjects().put(projectPath, projectState);

        Map<String, Map<String, String>> actions = projectState.getActions();

        EditorAgent editorAgent = editorAgentProvider.get();

        final StringMap<EditorPartPresenter> openedEditors = editorAgent.getOpenedEditors();
        for (String filePath : openedEditors.getKeys().asIterable()) {
            Map<String, String> parameters = new HashMap<>();
            final String relFilePath = filePath.replaceFirst(projectPath, "");
            parameters.put("file", relFilePath);

            actions.put("openFile", parameters);
        }
    }

    private void writeStateToPreferences() {
        final String json = dtoFactory.toJson(myAppState);
        preferencesManager.setValue(PREFERENCE_PROPERTY_NAME, json);
        preferencesManager.flushPreferences(new AsyncCallback<ProfileDescriptor>() {
            @Override
            public void onSuccess(ProfileDescriptor result) {
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }

//    public void restore() {
//        final String projectPath = appContext.getCurrentProject().getRootProject().getPath();
//
//        final AppState appState = readStateFromPreferences();
//        ProjectState projectState = appState.getProjects().get(projectPath);
//
//        Map<String, Map<String, String>> actions = projectState.getActions();
//        for (Map.Entry<String, Map<String, String>> entry : actions.entrySet()) {
//            final String actionId = entry.getKey();
//            final Map<String, String> parameters = entry.getValue();
//
//            // TODO: need to perform actions in async way if need
//            performAction(actionId, parameters);
//        }
//    }

    private void readStateFromPreferences() {
        final String json = preferencesManager.getValue(PREFERENCE_PROPERTY_NAME);
        if (json != null) {
            myAppState = dtoFactory.createDtoFromJson(json, AppState.class);
        } else {
            // TODO: remove it. For testing only.
            myAppState = createTestAppState();
        }
    }

    private void performAction(String actionId, Map<String, String> parameters) {
        final Action action = actionManager.getAction(actionId);

        if (action == null) {
            return;
        }

        final Presentation presentation = presentationFactory.getPresentation(action);

        final ActionEvent event = new ActionEvent("", presentation, actionManager, 0, parameters);
        action.update(event);

        if (presentation.isEnabled() && presentation.isVisible()) {
            action.actionPerformed(event);
        }
    }

    private AppState createTestAppState() {
        Map<String, String> openFileActionParameters = new HashMap<>();
        openFileActionParameters.put("file", "pom.xml");

        Map<String, Map<String, String>> actions = new HashMap<>();
        actions.put("openFile", openFileActionParameters);

        ProjectState projectState = dtoFactory.createDto(ProjectState.class);
        projectState.setActions(actions);

        Map<String, ProjectState> projectStates = new HashMap<>();
        projectStates.put("/spring", projectState);

        return dtoFactory.createDto(AppState.class).withProjects(projectStates);
    }

    @Override
    public void onCloseCurrentProject(CloseCurrentProjectEvent event) {

    }
}
