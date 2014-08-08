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
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.collections.Jso;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class ExtensionManagerPresenter extends AbstractPreferencesPagePresenter implements ExtensionManagerView.ActionDelegate {


    private ExtensionManagerView       view;
    private ExtensionRegistry          extensionRegistry;
    private PreferencesManager         preferencesManager;
    private boolean                    isDirty;
    private List<ExtensionDescription> extensions;

    @Inject
    public ExtensionManagerPresenter(Resources resources, CoreLocalizationConstant constant, ExtensionManagerView view,
                                     ExtensionRegistry extensionRegistry, PreferencesManager preferencesManager) {
        super(constant.extensionTitle(), resources.extension());
        this.view = view;
        this.extensionRegistry = extensionRegistry;
        this.preferencesManager = preferencesManager;
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
                Ask ask = new Ask("Restart", "Restart Codenvy to activate changes in Extensions?", new AskHandler() {
                    @Override
                    public void onOk() {
                        Window.Location.reload();

                    }
                });
                ask.show();
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
        extensions = new ArrayList<ExtensionDescription>();
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
