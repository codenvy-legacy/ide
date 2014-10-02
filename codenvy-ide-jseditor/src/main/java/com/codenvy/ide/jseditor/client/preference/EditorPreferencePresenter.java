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

import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.Notification.Type;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.jseditor.client.preference.editorselection.EditorSelectionPreferencePresenter;
import com.codenvy.ide.jseditor.client.preference.keymaps.KeyMapsPreferencePresenter;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** Preference page presenter for the editors. */
@Singleton
public class EditorPreferencePresenter extends AbstractPreferencesPagePresenter implements EditorPreferenceSection.ParentPresenter {

    /** The editor preferences page view. */
    private final EditorPreferenceView view;

    /** Preference manager instance. */
    private final PreferencesManager preferencesManager;
    /** The notification manager. */
    private final NotificationManager notificationManager;

    /** I18n messages for the editor preferences. */
    private final EditorPrefLocalizationConstant constant;

    private final EditorSelectionPreferencePresenter editorTypeSection;
    private final KeyMapsPreferencePresenter keymapsSection;

    @Inject
    public EditorPreferencePresenter(final EditorPreferenceView view,
                                     final EditorPrefLocalizationConstant constant,
                                     final PreferencesManager preferencesManager,
                                     final NotificationManager notificationManager,
                                     final EditorPreferenceResource resource,
                                     final EditorSelectionPreferencePresenter editorTypeSection,
                                     final KeyMapsPreferencePresenter keymapsSection) {

        super(constant.editorTypeTitle(),
              constant.editorTypeCategory(),
              resource.editorPrefIconTemporary());// TODO use svg icon when the PreferencesPagePresenter allow it

        this.view = view;
        this.preferencesManager = preferencesManager;
        this.notificationManager = notificationManager;
        this.constant = constant;
        this.editorTypeSection = editorTypeSection;
        this.keymapsSection = keymapsSection;

        this.editorTypeSection.setParent(this);
        this.keymapsSection.setParent(this);
    }

    @Override
    public void doApply() {
        this.editorTypeSection.doApply();
        this.keymapsSection.doApply();

        this.preferencesManager.flushPreferences(new AsyncCallback<ProfileDescriptor>() {

            @Override
            public void onSuccess(final ProfileDescriptor result) {
                final Notification notification = new Notification(constant.flushSuccess(), Type.INFO);
                notificationManager.showNotification(notification);

            }

            @Override
            public void onFailure(final Throwable caught) {
                final Notification notification = new Notification(constant.flushError(), Type.ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    @Override
    public boolean isDirty() {
        return this.editorTypeSection.isDirty() || this.keymapsSection.isDirty();
    }

    @Override
    public void go(final AcceptsOneWidget container) {
        this.editorTypeSection.go(view.getEditorTypeContainer());
        this.keymapsSection.go(view.getKeymapsContainer());
        container.setWidget(view);
    }

    @Override
    public void signalDirtyState() {
        this.delegate.onDirtyChanged();
    }

}
