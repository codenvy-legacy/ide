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
package com.codenvy.ide.jseditor.client.preference.keymaps;

import java.util.Map.Entry;

import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.keymap.Keymap;
import com.codenvy.ide.jseditor.client.keymap.KeymapChangeEvent;
import com.codenvy.ide.jseditor.client.keymap.KeymapValuesHolder;
import com.codenvy.ide.jseditor.client.preference.EditorPreferenceSection;
import com.codenvy.ide.jseditor.client.prefmodel.KeymapPrefReader;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

/** Presenter for the keymap preference selection section. */
public class KeyMapsPreferencePresenter implements EditorPreferenceSection, KeymapsPreferenceView.ActionDelegate {

    private final KeymapsPreferenceView view;

    private final KeymapPrefReader       keymapPrefReader;
    private final EventBus eventBus;

    private final KeymapValuesHolder keymapValuesHolder;
    private final KeymapValuesHolder prefKeymaps;

    /** Has any of the keymap preferences been changed ? */
    private boolean keymapsDirty    = false;

    /** The preference page presenter. */
    private ParentPresenter parentPresenter;

    @Inject
    public KeyMapsPreferencePresenter(final KeymapsPreferenceView view,
                                      final KeymapPrefReader keymapPrefReader,
                                      final EventBus eventBus) {
        this.view = view;
        this.eventBus = eventBus;
        this.keymapPrefReader = keymapPrefReader;
        this.view.setDelegate(this);

        this.keymapValuesHolder = new KeymapValuesHolder();
        this.view.setKeymapValuesHolder(keymapValuesHolder);

        this.prefKeymaps = new KeymapValuesHolder();

    }

    @Override
    public void doApply() {
        if (this.keymapsDirty) {
            Log.debug(KeyMapsPreferencePresenter.class, "Applying changes - keymaps ");
            this.keymapPrefReader.storePrefs(this.keymapValuesHolder);
            for (final Entry<EditorType, Keymap> entry : this.keymapValuesHolder) {
                this.eventBus.fireEvent(new KeymapChangeEvent(entry.getKey().getEditorTypeKey(),
                                                              entry.getValue().getKey()));
            }
            this.keymapsDirty = false;
        }
        // let the higher level (page) presenter do the flush
    }

    @Override
    public boolean isDirty() {
        return this.keymapsDirty;
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        container.setWidget(null);
        initKeymapValues();
        container.setWidget(view);
    }

    private void initKeymapValues() {
        this.keymapPrefReader.readPref(prefKeymaps);
        for (final Entry<EditorType, Keymap> entry : this.prefKeymaps) {
            Log.debug(KeyMapsPreferencePresenter.class,
                      "Found one keymap pref: editorType=" + entry.getKey() + " keymap=" + entry.getValue());
            this.keymapValuesHolder.setKeymap(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void setParent(final ParentPresenter parent) {
        this.parentPresenter = parent;
    }

    @Override
    public void editorKeymapChanged(final EditorType editorType, final Keymap keymap) {
        if (editorType == null) {
            Log.error(KeyMapsPreferencePresenter.class, "editorKeymapChanged: editor type is null (keymap=" + keymap + ").");
            return;
        }
        if (keymap == null) {
            Log.error(KeyMapsPreferencePresenter.class, "editorKeymapChanged: keymap is null.");
            return;
        }

        Log.debug(KeyMapsPreferencePresenter.class, "editorKeymapChanged: editor=" + editorType + " keymap=" + keymap);

        boolean dirty = false;
        for (final Entry<EditorType, Keymap> entry : this.keymapValuesHolder) {
            final Keymap prefKeymap = prefKeymaps.getKeymap(entry.getKey());
            Log.debug(KeyMapsPreferencePresenter.class, "\t editor=" + editorType + " compare (new) " + keymap + " and (old)"
                                                           + prefKeymap);
            if (entry.getValue() == null) {
                dirty = (prefKeymap != null);
            } else {
                dirty = !(entry.getValue().equals(prefKeymap));
            }
            Log.debug(KeyMapsPreferencePresenter.class, "\t keymap dirty=" + dirty);
            if (dirty) {
                break;
            }
        }
        this.keymapsDirty = dirty;
        this.parentPresenter.signalDirtyState();
    }

}
