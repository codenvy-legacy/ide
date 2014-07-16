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
package com.codenvy.ide.jseditor.client.inject;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import org.vectomatic.dom.svg.ui.SVGResource;

import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.jseditor.client.JsEditorConstants;
import com.codenvy.ide.jseditor.client.JsEditorExtension;
import com.codenvy.ide.jseditor.client.document.DocumentStorage;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeMapping;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeMappingImpl;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistryImpl;
import com.codenvy.ide.jseditor.client.filetype.FileTypeIdentifier;
import com.codenvy.ide.jseditor.client.filetype.MultipleMethodFileIdentifier;
import com.codenvy.ide.jseditor.client.preference.EditorTypePreferencePresenter;
import com.codenvy.ide.jseditor.client.preference.EditorTypePreferenceView;
import com.codenvy.ide.jseditor.client.preference.EditorTypePreferenceViewImpl;
import com.codenvy.ide.jseditor.client.requirejs.ModuleHolder;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenterFactory;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.gwt.inject.client.multibindings.GinMultibinder;
import com.google.inject.Provides;
import com.google.inject.name.Names;

@ExtensionGinModule
public class JsEditorGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(ModuleHolder.class).in(Singleton.class);

        // Bind the editor preference panel
        GinMultibinder<PreferencesPagePresenter> prefBinder = GinMultibinder.newSetBinder(binder(), PreferencesPagePresenter.class);
        prefBinder.addBinding().to(EditorTypePreferencePresenter.class);
        bind(EditorTypePreferenceView.class).to(EditorTypePreferenceViewImpl.class);

        // Bind the embedded text editor presenter factory
        install(new GinFactoryModuleBuilder().build(EmbeddedTextEditorPresenterFactory.class));

        // Bind the file type identifier
        bind(FileTypeIdentifier.class).to(MultipleMethodFileIdentifier.class);

        // bind the default editor type key
        bindConstant().annotatedWith(Names.named(JsEditorExtension.CLASSIC_EDITOR_TYPE_INJECT_NAME))
                      .to(JsEditorExtension.CLASSIC_EDITOR_KEY);

        // editor registration and selection
        bind(EditorTypeMapping.class).to(EditorTypeMappingImpl.class).in(Singleton.class);
        bind(EditorTypeRegistry.class).to(EditorTypeRegistryImpl.class).in(Singleton.class);

        // bind the document storage
        bind(DocumentStorage.class);
    }

    // no real need to make it a singleton, it's a simple instantiation
    @Provides
    @Named(JsEditorExtension.DEFAULT_EDITOR_TYPE_INSTANCE)
    @Inject
    protected EditorType defaultEditorType(final @Named(JsEditorExtension.DEFAULT_EDITOR_TYPE_INJECT_NAME) String defaultEditorKey) {
        return EditorType.fromKey(defaultEditorKey);
    }

    @Provides
    @Singleton
    @Named(JsEditorExtension.PLAIN_TEXT_FILETYPE_INJECT_NAME)
    protected FileType textPlainFileType(final JsEditorConstants constants) {
        return new FileType(constants.defaultEditorDescription(),
                            (SVGResource)null,
                            EditorTypeMapping.CONTENT_TYPE_TEXT_PLAIN,
                            (String)null);
    }
}
