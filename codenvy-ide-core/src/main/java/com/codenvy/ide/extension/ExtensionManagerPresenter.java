/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.extension;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.ui.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.collections.js.Jso;
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
        preferencesManager.flushPreferences(new AsyncCallback<Void>() {
            @Override
            public void onFailure(Throwable caught) {
                Log.error(ExtensionManagerPresenter.class, caught);
            }

            @Override
            public void onSuccess(Void result) {
                if (Window.confirm("Restart Codenvy to activate changes in Extensions?")) {
                    Window.Location.reload();
                }
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
