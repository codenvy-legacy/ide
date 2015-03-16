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

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.dto.DtoFactory;
import org.eclipse.che.ide.rest.AsyncRequestLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Manages (save/restore) the states of the Codenvy application between sessions.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class AppStateManager {
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

    @Inject
    public AppStateManager(Set<AppStateComponent> appStateComponents,
                           PreferencesManager preferencesManager,
                           DtoFactory dtoFactory,
                           AsyncRequestLoader loader,
                           CoreLocalizationConstant localizationConstant) {
        this.appStateComponents = appStateComponents;
        this.preferencesManager = preferencesManager;
        this.dtoFactory = dtoFactory;
        this.loader = loader;

        waitSavingMessage = localizationConstant.waitSavingMessage();
        waitRestoringMessage = localizationConstant.waitRestoringMessage();
    }

    /** Save Codenvy application's state by calling all registered {@link AppStateComponent}s. */
    public void saveState() {
        final Iterator<AppStateComponent> iterator = appStateComponents.iterator();
        if (!iterator.hasNext()) {
            return;
        }

        final AppState stateToSave = dtoFactory.createDto(AppState.class);

        final Callback callback = new Callback() {
            @Override
            public void onPerformed() {
                if (iterator.hasNext()) {
                    performSave(iterator.next(), stateToSave, this);
                } else {
                    writeStateToPreferences(stateToSave);
                    loader.hide(waitSavingMessage);
                }
            }
        };

        loader.show(waitSavingMessage);
        performSave(iterator.next(), stateToSave, callback);
    }

    /** Restore Codenvy application's state by calling all registered {@link AppStateComponent}s. */
    public void restoreState() {
        final Iterator<AppStateComponent> iterator = appStateComponents.iterator();
        if (!iterator.hasNext()) {
            return;
        }

        final AppState stateToRestore = readStateFromPreferences();

        final Callback callback = new Callback() {
            @Override
            public void onPerformed() {
                if (iterator.hasNext()) {
                    performRestore(iterator.next(), stateToRestore, this);
                } else {
                    loader.hide(waitRestoringMessage);
                }
            }
        };

        loader.show(waitRestoringMessage);
        performRestore(iterator.next(), stateToRestore, callback);
    }

    private void performSave(AppStateComponent component, AppState state, Callback callback) {
        component.save(state, callback);
    }

    private void performRestore(AppStateComponent component, AppState state, Callback callback) {
        component.restore(state, callback);
    }

    private AppState readStateFromPreferences() {
        final String json = preferencesManager.getValue(PREFERENCE_PROPERTY_NAME);
        final AppState appState;
        if (json != null) {
            appState = dtoFactory.createDtoFromJson(json, AppState.class);
        } else {
            // TODO: remove it. For testing only.
            appState = dtoFactory.createDto(AppState.class);

            appState.setLastProjectPath("/spring");

            List<String> openedFiles = new ArrayList<>();
            openedFiles.add("/spring/pom.xml");
            appState.setOpenedFilesPaths(openedFiles);

            appState.setOutlineShown(true);
        }
        return appState;
    }

    private void writeStateToPreferences(AppState appState) {
        final String json = dtoFactory.toJson(appState);
//        preferencesManager.setValue(PREFERENCE_PROPERTY_NAME, json);
    }
}
