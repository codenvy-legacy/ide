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
package org.eclipse.che.ide.jseditor.client.preference.inject;


import org.eclipse.che.ide.api.extension.ExtensionGinModule;
import org.eclipse.che.ide.api.preferences.PreferencePagePresenter;
import org.eclipse.che.ide.jseditor.client.preference.EditorPreferencePresenter;
import org.eclipse.che.ide.jseditor.client.preference.EditorPreferenceView;
import org.eclipse.che.ide.jseditor.client.preference.EditorPreferenceViewImpl;
import org.eclipse.che.ide.jseditor.client.preference.editorselection.EditorSelectionPreferenceView;
import org.eclipse.che.ide.jseditor.client.preference.editorselection.EditorSelectionPreferenceViewImpl;
import org.eclipse.che.ide.jseditor.client.preference.keymaps.KeymapsPreferenceView;
import org.eclipse.che.ide.jseditor.client.preference.editorselection.EditorSelectionPreferencePresenter;
import org.eclipse.che.ide.jseditor.client.preference.keymaps.KeyMapsPreferencePresenter;
import org.eclipse.che.ide.jseditor.client.preference.keymaps.KeymapsPreferenceViewImpl;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.multibindings.GinMultibinder;

/** Gin module for the editor preferences. */
@ExtensionGinModule
public class EditorPreferencesGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        // Bind the editor preference panel
        final GinMultibinder<PreferencePagePresenter> prefBinder = GinMultibinder.newSetBinder(binder(), PreferencePagePresenter.class);
        prefBinder.addBinding().to(EditorPreferencePresenter.class);

        bind(EditorPreferenceView.class).to(EditorPreferenceViewImpl.class);
        bind(EditorSelectionPreferenceView.class).to(EditorSelectionPreferenceViewImpl.class);
        bind(KeymapsPreferenceView.class).to(KeymapsPreferenceViewImpl.class);
        bind(EditorSelectionPreferencePresenter.class);
        bind(KeyMapsPreferencePresenter.class);

    }
}
