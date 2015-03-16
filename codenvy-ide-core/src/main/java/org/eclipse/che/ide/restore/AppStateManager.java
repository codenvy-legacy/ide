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
import org.eclipse.che.ide.restore.components.LastProjectStateComponent;
import org.eclipse.che.ide.restore.components.OpenedFilesStateComponent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Manages (save/restore) the states of the Codenvy application.
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

    private final PreferencesManager   preferencesManager;
    private final DtoFactory           dtoFactory;
    private final AsyncRequestLoader   loader;
    private final List<StateComponent> stateComponents;

    @Inject
    public AppStateManager(PreferencesManager preferencesManager,
                           DtoFactory dtoFactory,
                           CoreLocalizationConstant localizationConstant,
                           AsyncRequestLoader loader,
                           LastProjectStateComponent lastProjectStateComponent,
                           OpenedFilesStateComponent openedFilesStateComponent) {
        this.preferencesManager = preferencesManager;
        this.dtoFactory = dtoFactory;
        this.loader = loader;

        waitSavingMessage = localizationConstant.waitSavingMessage();
        waitRestoringMessage = localizationConstant.waitRestoringMessage();

        stateComponents = new ArrayList<>();
        stateComponents.add(lastProjectStateComponent);
        stateComponents.add(openedFilesStateComponent);
    }

    public void saveState() {
        final Iterator<StateComponent> iterator = stateComponents.iterator();
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

    public void restoreState() {
        final Iterator<StateComponent> iterator = stateComponents.iterator();
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

    private void performSave(StateComponent component, AppState state, Callback callback) {
        component.save(state, callback);
    }

    private void performRestore(StateComponent component, AppState state, Callback callback) {
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
