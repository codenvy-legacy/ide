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
package org.eclipse.che.ide.jseditor.client.preference.editorselection;

import static org.eclipse.che.ide.jseditor.client.JsEditorExtension.DEFAULT_EDITOR_TYPE_INSTANCE;

import javax.inject.Named;

import org.eclipse.che.ide.jseditor.client.editortype.EditorType;
import org.eclipse.che.ide.jseditor.client.preference.EditorPreferenceSection;
import org.eclipse.che.ide.jseditor.client.prefmodel.DefaultEditorTypePrefReader;
import org.eclipse.che.ide.jseditor.client.prefmodel.EditorPreferenceReader;
import org.eclipse.che.ide.jseditor.client.prefmodel.EditorPreferences;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

/** Editor selection section of the editor preference page. */
public class EditorSelectionPreferencePresenter implements EditorPreferenceSection, EditorSelectionPreferenceView.ActionDelegate {

    private final EditorSelectionPreferenceView view;
    private final DefaultEditorTypePrefReader   defaultEditorPrefReader;
    private final EditorPreferenceReader        editorPreferenceReader;

    private final EditorType defaultEditorType;
    private       EditorType configuredDefaultEditorType;
    private       EditorType savedDefaultEditorType;


    /** have the default editor pref been changed ? */
    private boolean dirty = false;

    private ParentPresenter parentPresenter;

    @Inject
    public EditorSelectionPreferencePresenter(final EditorSelectionPreferenceView view,
                                              final EditorPreferenceReader editorPreferenceReader,
                                              final DefaultEditorTypePrefReader defaultEditorPrefReader,
                                              final @Named(DEFAULT_EDITOR_TYPE_INSTANCE) EditorType defaultEditorType) {
        this.view = view;
        this.defaultEditorPrefReader = defaultEditorPrefReader;
        this.editorPreferenceReader = editorPreferenceReader;
        this.defaultEditorType = defaultEditorType;
        this.view.setDelegate(this);
    }

    public void storeChanges() {
        EditorPreferences editorPreferences = editorPreferenceReader.getPreferences();
        defaultEditorPrefReader.storePref(editorPreferences, configuredDefaultEditorType);
        savedDefaultEditorType = configuredDefaultEditorType;
        dirty = false;
    }

    public void refresh() {
        final EditorPreferences editorPreferences = editorPreferenceReader.getPreferences();
        savedDefaultEditorType = defaultEditorPrefReader.readPref(editorPreferences);
        configuredDefaultEditorType = savedDefaultEditorType;
        view.refresh();
        dirty = false;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(null);
        final EditorPreferences editorPreferences = editorPreferenceReader.getPreferences();
        savedDefaultEditorType = defaultEditorPrefReader.readPref(editorPreferences);
        configuredDefaultEditorType = savedDefaultEditorType;
        container.setWidget(view);
    }

    @Override
    public void defaultEditorChanged(final EditorType newEditorType) {
        configuredDefaultEditorType = newEditorType;
        dirty = (savedDefaultEditorType != configuredDefaultEditorType);
        if (dirty) {
            parentPresenter.signalDirtyState();
        }
    }

    @Override
    public void setParent(final ParentPresenter parent) {
        parentPresenter = parent;
    }

    @Override
    public EditorType getConfiguredDefaultEditor() {
        if (this.configuredDefaultEditorType == null) {
            return this.defaultEditorType;
        } else {
            return this.configuredDefaultEditorType;
        }
    }
}
