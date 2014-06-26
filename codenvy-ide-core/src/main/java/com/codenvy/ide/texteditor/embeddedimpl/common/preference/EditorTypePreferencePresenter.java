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
package com.codenvy.ide.texteditor.embeddedimpl.common.preference;

import com.codenvy.api.user.shared.dto.Profile;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.ui.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.core.editor.EditorType;
import com.codenvy.ide.core.editor.EditorTypeSelection;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Preference page presenter for the editor type implementation.
 * 
 * @author "Mickaël Leduque"
 */
@Singleton
public class EditorTypePreferencePresenter extends AbstractPreferencesPagePresenter implements EditorTypePreferenceView.ActionDelegate {


    private final EditorTypePreferenceView view;
    private final EditorTypeSelection      editorTypeSelection;
    private final PreferencesManager       preferencesManager;
    private final NotificationManager      notificationManager;

    private boolean                        dirty = false;
    private EditorType                     selectedEditorType;

    @Inject
    public EditorTypePreferencePresenter(final EditorTypePreferenceView view,
                                         final CoreLocalizationConstant constant,
                                         final EditorTypeSelection editorTypeSelection,
                                         final PreferencesManager preferencesManager,
                                         final NotificationManager notificationManager) {
        super(constant.editorTypeTitle(), null);
        this.view = view;
        this.editorTypeSelection = editorTypeSelection;
        this.preferencesManager = preferencesManager;
        this.notificationManager = notificationManager;
        view.setDelegate(this);
    }

    @Override
    public void doApply() {
        if (isDirty()) {
            preferencesManager.setPreference(EditorTypeSelection.PREFERENCE_PROPERTY_NAME, selectedEditorType.name());
            preferencesManager.flushPreferences(new AsyncCallback<Profile>() {
                @Override
                public void onFailure(final Throwable caught) {
                    // TODO notification ?
                }

                @Override
                public void onSuccess(final Profile result) {
                    // TODO notification ?
                }
            });
            this.editorTypeSelection.setEditorType(selectedEditorType);
        }
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        view.setEditorType(this.editorTypeSelection.getEditorType());
    }

    @Override
    public void editorTypeSelected(final EditorType selection) {
        this.selectedEditorType = selection;
        dirty = !(this.editorTypeSelection.getEditorType().equals(selection));
        delegate.onDirtyChanged();
    }
}
