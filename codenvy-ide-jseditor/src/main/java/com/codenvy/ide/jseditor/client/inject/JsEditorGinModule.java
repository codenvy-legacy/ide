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

import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.extension.ExtensionGinModule;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.jseditor.client.JsEditorConstants;
import com.codenvy.ide.jseditor.client.JsEditorExtension;
import com.codenvy.ide.jseditor.client.defaulteditor.DefaultEditorProvider;
import com.codenvy.ide.jseditor.client.document.DocumentStorage;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeMapping;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistryImpl;
import com.codenvy.ide.jseditor.client.filetype.FileTypeIdentifier;
import com.codenvy.ide.jseditor.client.filetype.MultipleMethodFileIdentifier;
import com.codenvy.ide.jseditor.client.infopanel.InfoPanelFactory;
import com.codenvy.ide.jseditor.client.prefmodel.DefaultEditorTypePrefReader;
import com.codenvy.ide.jseditor.client.prefmodel.EditorPreferenceReader;
import com.codenvy.ide.jseditor.client.prefmodel.KeymapPrefReader;
import com.codenvy.ide.jseditor.client.requirejs.ModuleHolder;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPartView;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPartViewImpl;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorPresenterFactory;
import com.codenvy.ide.jseditor.client.texteditor.EmbeddedTextEditorViewFactory;
import com.google.gwt.inject.client.AbstractGinModule;
import com.google.gwt.inject.client.assistedinject.GinFactoryModuleBuilder;
import com.google.inject.Provides;
import com.google.inject.name.Names;

import org.vectomatic.dom.svg.ui.SVGResource;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

@ExtensionGinModule
public class JsEditorGinModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind(ModuleHolder.class).in(Singleton.class);

        // Bind the embedded text editor presenter factory
        install(new GinFactoryModuleBuilder().build(EmbeddedTextEditorPresenterFactory.class));
        // the view factory
        install(new GinFactoryModuleBuilder()
                .implement(EmbeddedTextEditorPartView.class, EmbeddedTextEditorPartViewImpl.class)
                .build(EmbeddedTextEditorViewFactory.class));

        // Bind the file type identifier
        bind(FileTypeIdentifier.class).to(MultipleMethodFileIdentifier.class);

        // editor registration
        bind(EditorTypeRegistry.class).to(EditorTypeRegistryImpl.class).in(Singleton.class);

        // bind the components that read/write editor preferences
        bind(EditorPreferenceReader.class);
        bind(DefaultEditorTypePrefReader.class);
        bind(KeymapPrefReader.class);

        // bind the document storage
        bind(DocumentStorage.class);

        // bind the default editor
        bind(EditorProvider.class).annotatedWith(Names.named("defaultEditor")).to(DefaultEditorProvider.class);

        // bind the info panel factory
        install(new GinFactoryModuleBuilder().build(InfoPanelFactory.class));
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
    @PlainTextFileType
    protected FileType textPlainFileType(final JsEditorConstants constants) {
        return new FileType(constants.defaultEditorDescription(),
                            (SVGResource)null,
                            EditorTypeMapping.CONTENT_TYPE_TEXT_PLAIN,
                            (String)null);
    }
}
