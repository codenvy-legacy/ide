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
package org.eclipse.che.ide.extension;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;

import org.eclipse.che.ide.api.preferences.AbstractPreferencePagePresenter;
import org.eclipse.che.ide.api.preferences.PreferencesManager;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.ArrayList;
import java.util.List;

/** @author Evgen Vidolob */
@Singleton
public class ExtensionManagerPresenter extends AbstractPreferencePagePresenter implements ExtensionManagerView.ActionDelegate {

    public final static String PREFS_EXTENSIONS = "extensions";

    private ExtensionManagerView       view;
    private ExtensionRegistry          extensionRegistry;
    private PreferencesManager         preferencesManager;
    private DialogFactory              dialogFactory;
    private boolean                    dirty;

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
    public boolean isDirty() {
        return dirty;
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
        dirty = true;
        delegate.onDirtyChanged();
    }

    @Override
    public void storeChanges() {
//        Jso jso = Jso.create();
//        for (ExtensionDescription ed : extensions) {
//            jso.addField(ed.getId(), ed.isEnabled());
//        }
//        preferencesManager.setValue(PREFS_EXTENSIONS, jso.serialize());
        dirty = false;
    }

    @Override
    public void revertChanges() {
    }

}
