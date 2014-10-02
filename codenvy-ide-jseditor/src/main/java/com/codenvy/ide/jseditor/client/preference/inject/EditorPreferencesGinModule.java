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
package com.codenvy.ide.jseditor.client.preference.inject;


import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.api.preferences.PreferencesPagePresenter;
import com.codenvy.ide.jseditor.client.preference.EditorPreferencePresenter;
import com.codenvy.ide.jseditor.client.preference.EditorPreferenceView;
import com.codenvy.ide.jseditor.client.preference.EditorPreferenceViewImpl;
import com.codenvy.ide.jseditor.client.preference.editorselection.EditorSelectionPreferencePresenter;
import com.codenvy.ide.jseditor.client.preference.editorselection.EditorSelectionPreferenceView;
import com.codenvy.ide.jseditor.client.preference.editorselection.EditorSelectionPreferenceViewImpl;
import com.codenvy.ide.jseditor.client.preference.keymaps.KeyMapsPreferencePresenter;
import com.codenvy.ide.jseditor.client.preference.keymaps.KeymapsPreferenceView;
import com.codenvy.ide.jseditor.client.preference.keymaps.KeymapsPreferenceViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;

/** Gin module for the editor preferences. */
@ExtensionGinModule
public class EditorPreferencesGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        // Bind the editor preference panel
        final GinMultibinder<PreferencesPagePresenter> prefBinder = GinMultibinder.newSetBinder(binder(), PreferencesPagePresenter.class);
        prefBinder.addBinding().to(EditorPreferencePresenter.class);

        bind(EditorPreferenceView.class).to(EditorPreferenceViewImpl.class);
        bind(EditorSelectionPreferenceView.class).to(EditorSelectionPreferenceViewImpl.class);
        bind(KeymapsPreferenceView.class).to(KeymapsPreferenceViewImpl.class);
        bind(EditorSelectionPreferencePresenter.class);
        bind(KeyMapsPreferencePresenter.class);

    }
}
