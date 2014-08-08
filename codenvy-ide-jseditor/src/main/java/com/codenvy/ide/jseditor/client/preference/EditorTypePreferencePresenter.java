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
package com.codenvy.ide.jseditor.client.preference;

import java.util.Collections;
import java.util.Map.Entry;

import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.Notification.Type;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeMapping;
import com.codenvy.ide.jseditor.client.inject.PlainTextFileType;
import com.codenvy.ide.jseditor.client.keymap.Keymap;
import com.codenvy.ide.jseditor.client.keymap.KeymapChangeEvent;
import com.codenvy.ide.jseditor.client.keymap.KeymapPrefReader;
import com.codenvy.ide.jseditor.client.keymap.KeymapValuesHolder;
import com.codenvy.ide.jseditor.client.preference.dataprovider.FileTypeKeyProvider;
import com.codenvy.ide.jseditor.client.preference.dataprovider.RefreshableDataProvider;
import com.codenvy.ide.jseditor.client.preference.dataprovider.RefreshableListDataProvider;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.gwt.view.client.AbstractDataProvider;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Preference page presenter for the editors.
 * 
 * @author "Mickaël Leduque"
 */
@Singleton
public class EditorTypePreferencePresenter extends AbstractPreferencesPagePresenter implements EditorTypePreferenceView.ActionDelegate {

    private final EditorTypePreferenceView       view;
    private final EditorTypeMapping              editorTypeMapping;
    private final PreferencesManager             preferencesManager;

    private final KeymapValuesHolder             keymapValuesHolder;
    private final KeymapValuesHolder             prefKeymaps;

    private final EventBus                       eventBus;

    private final AbstractDataProvider<FileType> fileTypeDataProvider;
    private final RefreshableDataProvider        refreshableDataProvider;
    private final FileTypeEditorMapping          fileTypeEditorMapping;
    private final EditorPrefLocalizationConstant constant;
    private final NotificationManager            notificationManager;


    // dirty states
    private boolean                              editorTypeDirty = false;
    private boolean                              keymapsDirty    = false;

    @Inject
    public EditorTypePreferencePresenter(final EditorTypePreferenceView view,
                                         final EditorPrefLocalizationConstant constant,
                                         final EditorTypeMapping editorTypeMapping,
                                         final FileTypeRegistry fileTypeRegistry,
                                         final PreferencesManager preferencesManager,
                                         final NotificationManager notificationManager,
                                         final EventBus eventBus,
                                         final EditorPreferenceResource resource,
                                         final @PlainTextFileType FileType plainTextFileType) {
        super(constant.editorTypeTitle(), resource.editorPrefIconTemporary());// TODO use svg icon when the PreferencesPagePresenter allow
                                                                              // it
        this.view = view;
        this.eventBus = eventBus;
        this.editorTypeMapping = editorTypeMapping;
        this.preferencesManager = preferencesManager;
        this.notificationManager = notificationManager;
        this.view.setDelegate(this);
        this.constant = constant;

        this.keymapValuesHolder = new KeymapValuesHolder();
        this.view.setKeymapValuesHolder(keymapValuesHolder);

        this.prefKeymaps = new KeymapValuesHolder();

        // disable the use of the filetype registry
        // this.fileTypeDataProvider = new FileTypeDataProvider(fileTypeRegistry);

        this.fileTypeDataProvider = new RefreshableListDataProvider<FileType>(Collections.singletonList(plainTextFileType),
                                                                              new FileTypeKeyProvider());
        this.refreshableDataProvider = (RefreshableDataProvider)this.fileTypeDataProvider;

        this.view.setFileTypeDataProvider(this.fileTypeDataProvider);

        this.fileTypeEditorMapping = new FileTypeEditorMapping();
        this.view.setFileTypeEditorMapping(fileTypeEditorMapping);
    }

    @Override
    public void doApply() {
        if (this.editorTypeDirty) {
            Log.debug(EditorTypePreferencePresenter.class, "Applying changes - editor types");
            for (final Entry<FileType, EditorType> entry : this.fileTypeEditorMapping) {
                this.editorTypeMapping.setEditorType(entry.getKey(), entry.getValue());
                Log.debug(EditorTypePreferencePresenter.class, "Editor type for file type "
                                                               + entry.getKey().getContentDescription()
                                                               + " set to " + entry.getValue());
            }
            this.editorTypeMapping.storeInPreferences();
            this.editorTypeDirty = false;
        }

        if (this.keymapsDirty) {
            Log.debug(EditorTypePreferencePresenter.class, "Applying changes - keymaps ");
            KeymapPrefReader.storePrefs(this.preferencesManager, this.keymapValuesHolder);
            for (final Entry<EditorType, Keymap> entry : this.keymapValuesHolder) {
                this.eventBus.fireEvent(new KeymapChangeEvent(entry.getKey().getEditorTypeKey(), entry.getValue().getKey()));
            }
            this.keymapsDirty = false;
        }
        this.preferencesManager.flushPreferences(new AsyncCallback<ProfileDescriptor>() {

            @Override
            public void onSuccess(final ProfileDescriptor result) {
                Notification notification = new Notification(constant.flushSuccess(), Type.INFO);
                notificationManager.showNotification(notification);

            }

            @Override
            public void onFailure(final Throwable caught) {
                Notification notification = new Notification(constant.flushError(), Type.ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    @Override
    public boolean isDirty() {
        return this.editorTypeDirty || this.keymapsDirty;
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(null);
        KeymapPrefReader.readPref(preferencesManager, prefKeymaps);
        initKeymapValues();
        view.buildEditorTypesList();
        initEditorTypeMappings();
        this.refreshableDataProvider.refresh();
        container.setWidget(view);
    }

    private void initKeymapValues() {
        for (final Entry<EditorType, Keymap> entry : this.prefKeymaps) {
            Log.debug(EditorTypePreferencePresenter.class,
                      "Found one keymap pref: editorType=" + entry.getKey() + " keymap=" + entry.getValue());
            this.keymapValuesHolder.setKeymap(entry.getKey(), entry.getValue());
        }
    }

    private void initEditorTypeMappings() {
        for (final Entry<FileType, EditorType> entry : this.editorTypeMapping) {
            this.fileTypeEditorMapping.setEditor(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void editorKeymapChanged(final EditorType editorType, final Keymap keymap) {
        if (editorType == null) {
            Log.error(EditorTypePreferencePresenter.class, "editorKeymapChanged: editor type is null (keymap=" + keymap + ").");
            return;
        }
        if (keymap == null) {
            Log.error(EditorTypePreferencePresenter.class, "editorKeymapChanged: keymap is null.");
            return;
        }

        Log.debug(EditorTypePreferencePresenter.class, "editorKeymapChanged: editor=" + editorType + " keymap=" + keymap);

        boolean dirty = false;
        for (final Entry<EditorType, Keymap> entry : this.keymapValuesHolder) {
            final Keymap prefKeymap = prefKeymaps.getKeymap(entry.getKey());
            Log.debug(EditorTypePreferencePresenter.class, "\t editor=" + editorType + " compare (new) " + keymap + " and (old)"
                                                           + prefKeymap);
            if (entry.getValue() == null) {
                dirty = (prefKeymap != null);
            } else {
                dirty = !(entry.getValue().equals(prefKeymap));
            }
            Log.debug(EditorTypePreferencePresenter.class, "\t keymap dirty=" + dirty);
            if (dirty) {
                break;
            }
        }
        this.keymapsDirty = dirty;
        delegate.onDirtyChanged();
    }

    @Override
    public void filetypeEditorChanged(final FileType fileType, final EditorType editorType) {
        if (fileType == null) {
            Log.error(EditorTypePreferencePresenter.class, "filetypeEditorChanged: file type is null (editorType=" + editorType + ").");
            return;
        }
        if (editorType == null) {
            Log.error(EditorTypePreferencePresenter.class, "filetypeEditorChanged: editorType is null.");
            return;
        }

        Log.debug(EditorTypePreferencePresenter.class, "filetypeEditorChanged: fileType=" + fileType + " editorType=" + editorType);

        boolean dirty = false;
        for (final Entry<FileType, EditorType> entry : this.fileTypeEditorMapping) {
            final EditorType prefEditor = this.editorTypeMapping.getEditorType(entry.getKey());
            Log.debug(EditorTypePreferencePresenter.class, "\t fileType=" + fileType + " compare (new) " + editorType + " and (old)"
                                                           + prefEditor);
            if (entry.getValue() == null) {
                dirty = (prefEditor != null);
            } else {
                dirty = !(entry.getValue().equals(prefEditor));
            }
            Log.debug(EditorTypePreferencePresenter.class, "\t editor dirty=" + dirty);
            if (dirty) {
                break;
            }
        }
        this.editorTypeDirty = dirty;
        delegate.onDirtyChanged();
    }
}
