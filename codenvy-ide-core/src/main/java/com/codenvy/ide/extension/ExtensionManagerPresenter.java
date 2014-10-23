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
package com.codenvy.ide.extension;

import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/** @author Evgen Vidolob */
@Singleton
public class ExtensionManagerPresenter extends AbstractPreferencesPagePresenter implements ExtensionManagerView.ActionDelegate {

    private ExtensionManagerView       view;
    private ExtensionRegistry          extensionRegistry;
    private PreferencesManager         preferencesManager;
    private DialogFactory              dialogFactory;
    private boolean                    isDirty;
    private List<ExtensionDescription> extensions;

    @Inject
    public ExtensionManagerPresenter(Resources resources, CoreLocalizationConstant constant, ExtensionManagerView view,
                                     ExtensionRegistry extensionRegistry, PreferencesManager preferencesManager,
                                     DialogFactory dialogFactory) {
        super(constant.extensionTitle(), constant.extensionCategory(), resources.extension());
        this.view = view;
        this.extensionRegistry = extensionRegistry;
        this.preferencesManager = preferencesManager;
        this.dialogFactory = dialogFactory;
        view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void doApply() {
        Jso jso = Jso.create();
        for (ExtensionDescription ed : extensions) {
            jso.addField(ed.getId(), ed.isEnabled());
        }
        preferencesManager.setPreference("ExtensionsPreferences", jso.serialize());
        preferencesManager.flushPreferences(new AsyncCallback<ProfileDescriptor>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error(ExtensionManagerPresenter.class, caught);
            }

            @Override
            public void onSuccess(ProfileDescriptor result) {
                dialogFactory.createConfirmDialog("Restart", "Restart Codenvy to activate changes in Extensions?",
                                                  new ConfirmCallback() {
                                                      @Override
                                                      public void accepted() {
                                                          Window.Location.reload();
                                                      }
                                                  }, null).show();
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public boolean isDirty() {
        return isDirty;
    }

    /** {@inheritDoc} */
    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        extensions = new ArrayList<>();
        for (ExtensionDescription ed : extensionRegistry.getExtensionDescriptions().getValues().asIterable()) {
            extensions.add(ed);
        }
        view.setExtensions(extensions);
    }

    @Override
    public void setDirty() {
        isDirty = true;
        delegate.onDirtyChanged();
    }
}
