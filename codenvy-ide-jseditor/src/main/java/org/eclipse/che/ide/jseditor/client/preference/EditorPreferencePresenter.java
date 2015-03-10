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
package org.eclipse.che.ide.jseditor.client.preference;

import org.eclipse.che.ide.api.preferences.AbstractPreferencePagePresenter;
import org.eclipse.che.ide.jseditor.client.preference.editorselection.EditorSelectionPreferencePresenter;
import org.eclipse.che.ide.jseditor.client.preference.keymaps.KeyMapsPreferencePresenter;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** Preference page presenter for the editors. */
@Singleton
public class EditorPreferencePresenter extends AbstractPreferencePagePresenter implements EditorPreferenceSection.ParentPresenter {

    /** The editor preferences page view. */
    private final EditorPreferenceView view;

    /** I18n messages for the editor preferences. */
    private final EditorPrefLocalizationConstant constant;

    private final EditorSelectionPreferencePresenter editorTypeSection;
    private final KeyMapsPreferencePresenter keymapsSection;

    @Inject
    public EditorPreferencePresenter(final EditorPreferenceView view,
                                     final EditorPrefLocalizationConstant constant,
                                     final EditorPreferenceResource resource,
                                     final EditorSelectionPreferencePresenter editorTypeSection,
                                     final KeyMapsPreferencePresenter keymapsSection) {

        super(constant.editorTypeTitle(),
              constant.editorTypeCategory(),
              resource.editorPrefIconTemporary());// TODO use svg icon when the PreferencesPagePresenter allow it

        this.view = view;
        this.constant = constant;
        this.editorTypeSection = editorTypeSection;
        this.keymapsSection = keymapsSection;

        this.editorTypeSection.setParent(this);
        this.keymapsSection.setParent(this);
    }

    @Override
    public boolean isDirty() {
        return editorTypeSection.isDirty() || keymapsSection.isDirty();
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        editorTypeSection.go(view.getEditorTypeContainer());
        keymapsSection.go(view.getKeymapsContainer());
        container.setWidget(view);
    }

    @Override
    public void signalDirtyState() {
        delegate.onDirtyChanged();
    }

    @Override
    public void storeChanges() {
        if (editorTypeSection.isDirty()) {
            editorTypeSection.storeChanges();
        }

        if (keymapsSection.isDirty()) {
            keymapsSection.storeChanges();
        }
    }

    @Override
    public void revertChanges() {
        editorTypeSection.refresh();
        keymapsSection.refresh();
        signalDirtyState();
    }

}
