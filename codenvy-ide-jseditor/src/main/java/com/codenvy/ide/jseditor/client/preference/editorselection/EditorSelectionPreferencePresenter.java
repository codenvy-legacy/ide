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
package com.codenvy.ide.jseditor.client.preference.editorselection;

import static com.codenvy.ide.jseditor.client.JsEditorExtension.DEFAULT_EDITOR_TYPE_INSTANCE;

import javax.inject.Named;

import com.codenvy.ide.api.editor.EditorRegistry;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.inject.PlainTextFileType;
import com.codenvy.ide.jseditor.client.preference.EditorPreferenceSection;
import com.codenvy.ide.jseditor.client.prefmodel.DefaultEditorTypePrefReader;
import com.codenvy.ide.jseditor.client.prefmodel.EditorPreferenceReader;
import com.codenvy.ide.jseditor.client.prefmodel.EditorPreferences;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;

/** Editor selection section of the editor preference page. */
public class EditorSelectionPreferencePresenter implements EditorPreferenceSection, EditorSelectionPreferenceView.ActionDelegate {

    private final EditorSelectionPreferenceView view;
    private final DefaultEditorTypePrefReader defaultEditorPrefReader;
    private final EditorPreferenceReader editorPreferenceReader;

    private final EditorType defaultEditorType;
    private EditorType configuredDefaultEditorType;
    private EditorType savedDefaultEditorType;


    /** have the default editor pref been changed ? */
    private boolean defaultEditorDirty = false;

    private ParentPresenter parentPresenter;

    @Inject
    public EditorSelectionPreferencePresenter(final EditorSelectionPreferenceView view,
                                              final EditorPreferenceReader editorPreferenceReader,
                                              final DefaultEditorTypePrefReader defaultEditorPrefReader,
                                              final @PlainTextFileType FileType plainTextFileType,
                                              final @Named(DEFAULT_EDITOR_TYPE_INSTANCE) EditorType defaultEditorType,
                                              final EditorRegistry editorRegistry,
                                              final FileTypeRegistry fileTypeRegistry) {
        this.view = view;
        this.defaultEditorPrefReader = defaultEditorPrefReader;
        this.editorPreferenceReader = editorPreferenceReader;
        this.defaultEditorType = defaultEditorType;
        this.view.setDelegate(this);
    }

    @Override
    public void doApply() {
        if (!isDirty()) {
            return;
        }
        final EditorPreferences editorPreferences = this.editorPreferenceReader.getPreferences();

        if (this.defaultEditorDirty) {
            Log.debug(EditorSelectionPreferencePresenter.class, "Applying changes - default editor");
            this.defaultEditorPrefReader.storePref(editorPreferences, configuredDefaultEditorType);
            this.savedDefaultEditorType = this.configuredDefaultEditorType;
            this.defaultEditorDirty = false;
        }
    }

    @Override
    public boolean isDirty() {
        return this.defaultEditorDirty;
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(null);

        final EditorPreferences editorPreferences = this.editorPreferenceReader.getPreferences();
        initDefaultEditor(editorPreferences);

        container.setWidget(view);
    }

    private void initDefaultEditor(final EditorPreferences editorPreferences) {
        // read configured and saved default editor from preferences
        this.savedDefaultEditorType = this.defaultEditorPrefReader.readPref(editorPreferences);
        this.configuredDefaultEditorType = this.savedDefaultEditorType;
    }

    @Override
    public void defaultEditorChanged(final EditorType newEditorType) {
        this.configuredDefaultEditorType = newEditorType;
        this.defaultEditorDirty = (this.savedDefaultEditorType != this.configuredDefaultEditorType);
        if (this.defaultEditorDirty) {
            this.parentPresenter.signalDirtyState();
        }
    }

    @Override
    public void setParent(final ParentPresenter parent) {
        this.parentPresenter = parent;
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
