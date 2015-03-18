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
package org.eclipse.che.ide.restore;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.event.ProjectActionEvent;
import org.eclipse.che.ide.api.event.ProjectActionHandler;
import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.rest.AsyncRequestLoader;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Responsible for persisting and restoring the Codenvy application's state across sessions.
 * Uses user preferences as storage for storing serialized state.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class AppStateManager implements ProjectActionHandler {
    /** The name of the property for the mappings in user preferences. */
    private static final String PREFERENCE_PROPERTY_NAME = "CodenvyAppState";

    /** The message displayed while waiting for saving state. */
    private final String waitSavingMessage;
    /** The message displayed while waiting for restoring state. */
    private final String waitRestoringMessage;

    private final Set<AppStateComponent> appStateComponents;
    private final PreferencesManager     preferencesManager;
    private final DtoFactory             dtoFactory;
    private final AsyncRequestLoader     loader;
    private final EventBus eventBus;

    private AppState appState;

    @Inject
    public AppStateManager(Set<AppStateComponent> appStateComponents,
                           PreferencesManager preferencesManager,
                           DtoFactory dtoFactory,
                           AsyncRequestLoader loader,
                           EventBus eventBus,
                           CoreLocalizationConstant localizationConstant) {
        this.appStateComponents = appStateComponents;
        this.preferencesManager = preferencesManager;
        this.dtoFactory = dtoFactory;
        this.loader = loader;
        this.eventBus = eventBus;

        waitSavingMessage = localizationConstant.waitSavingMessage();
        waitRestoringMessage = localizationConstant.waitRestoringMessage();
    }

    public void start() {
        eventBus.addHandler(ProjectActionEvent.TYPE, this);
        readStateFromPreferences(preferencesManager);
    }

//    @Inject
    private void readStateFromPreferences(PreferencesManager preferencesManager) {
        final String json = preferencesManager.getValue(PREFERENCE_PROPERTY_NAME);
        if (json != null) {
            appState = dtoFactory.createDtoFromJson(json, AppState.class);
        } else {
//            appState = dtoFactory.createDto(AppState.class);
            // TODO: for testing only
            appState = createTestAppState();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectOpened(ProjectActionEvent event) {
        final String projectPath = event.getProject().getPath();

        final ProjectState stateToRestore = appState.getProjects().get(projectPath);
        if (stateToRestore != null) {
            restoreState(stateToRestore);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onProjectClosed(ProjectActionEvent event) {
        final String projectPath = event.getProject().getPath();

        final Map<String, ProjectState> projectStates = appState.getProjects();
        ProjectState stateToPersist = projectStates.get(projectPath);
        if (stateToPersist == null) {
            stateToPersist = dtoFactory.createDto(ProjectState.class);
            projectStates.put(projectPath, stateToPersist);
        }

        persistState(stateToPersist);
    }

    /** Restore Codenvy application's state by calling all registered {@link AppStateComponent}s. */
    private void restoreState(@Nonnull final ProjectState projectState) {
        final Iterator<AppStateComponent> iterator = appStateComponents.iterator();
        if (!iterator.hasNext()) {
            return;
        }

        final Callback callback = new Callback() {
            @Override
            public void onPerformed() {
                if (iterator.hasNext()) {
                    performRestore(iterator.next(), projectState, this);
                } else {
                    loader.hide(waitRestoringMessage);
                }
            }
        };

        loader.show(waitRestoringMessage);
        performRestore(iterator.next(), projectState, callback);
    }

    /** Save Codenvy application's state by calling all registered {@link AppStateComponent}s. */
    private void persistState(@Nonnull final ProjectState projectState) {
        final Iterator<AppStateComponent> iterator = appStateComponents.iterator();
        if (!iterator.hasNext()) {
            return;
        }

        final Callback callback = new Callback() {
            @Override
            public void onPerformed() {
                if (iterator.hasNext()) {
                    performSave(iterator.next(), projectState, this);
                } else {
                    writeStateToPreferences();
                    loader.hide(waitSavingMessage);
                }
            }
        };

        loader.show(waitSavingMessage);
        performSave(iterator.next(), projectState, callback);
    }

    private void performSave(AppStateComponent component, ProjectState state, Callback callback) {
        component.save(state, callback);
    }

    private void performRestore(AppStateComponent component, ProjectState state, Callback callback) {
        component.restore(state, callback);
    }

    @Nullable
    private AppState readStateFromPreferences() {
        final String json = preferencesManager.getValue(PREFERENCE_PROPERTY_NAME);
        final AppState appState;
        if (json != null) {
            appState = dtoFactory.createDtoFromJson(json, AppState.class);
        } else {
            // TODO: remove it. For testing only.
            appState = createTestAppState();
        }
//        return null;
        return appState;
    }

    private void writeStateToPreferences() {
        final String json = dtoFactory.toJson(appState);
//        preferencesManager.setValue(PREFERENCE_PROPERTY_NAME, json);
    }

    private AppState createTestAppState() {
        List<String> openedFiles1 = new ArrayList<>();
        openedFiles1.add("/spring1/pom.xml");
        ProjectState projectState1 = dtoFactory.createDto(ProjectState.class)
                                              .withOpenedFilesPaths(openedFiles1);

        List<String> openedFiles2 = new ArrayList<>();
        openedFiles2.add("/spring2/src/main/webapp/index.jsp");
        ProjectState projectState2 = dtoFactory.createDto(ProjectState.class)
                                               .withOpenedFilesPaths(openedFiles2);

        Map<String, ProjectState> projects = new HashMap<>();
        projects.put("/spring1", projectState1);
        projects.put("/spring2", projectState2);

        final AppState appState = dtoFactory.createDto(AppState.class)
                                            .withProjects(projects);
        return appState;
    }
}
