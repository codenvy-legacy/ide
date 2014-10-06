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
package com.codenvy.ide.jseditor.client.texteditor;

import java.util.List;
import java.util.logging.Logger;

import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.collections.StringMap.IterationCallback;
import com.codenvy.ide.jseditor.client.annotation.AnnotationModel;
import com.codenvy.ide.jseditor.client.annotation.AnnotationModelEvent;
import com.codenvy.ide.jseditor.client.annotation.ClearAnnotationModelEvent;
import com.codenvy.ide.jseditor.client.annotation.GutterAnnotationRenderer;
import com.codenvy.ide.jseditor.client.annotation.InlineAnnotationRenderer;
import com.codenvy.ide.jseditor.client.codeassist.CodeAssistCallback;
import com.codenvy.ide.jseditor.client.codeassist.CodeAssistProcessor;
import com.codenvy.ide.jseditor.client.codeassist.CodeAssistant;
import com.codenvy.ide.jseditor.client.codeassist.CodeAssistantFactory;
import com.codenvy.ide.jseditor.client.codeassist.CompletionProposal;
import com.codenvy.ide.jseditor.client.codeassist.CompletionReadyCallback;
import com.codenvy.ide.jseditor.client.codeassist.CompletionsSource;
import com.codenvy.ide.jseditor.client.document.DocumentHandle;
import com.codenvy.ide.jseditor.client.editorconfig.TextEditorConfiguration;
import com.codenvy.ide.jseditor.client.events.CompletionRequestEvent;
import com.codenvy.ide.jseditor.client.events.CompletionRequestHandler;
import com.codenvy.ide.jseditor.client.events.DocumentChangeEvent;
import com.codenvy.ide.jseditor.client.events.DocumentReadyEvent;
import com.codenvy.ide.jseditor.client.events.doc.DocReadyWrapper;
import com.codenvy.ide.jseditor.client.events.doc.DocReadyWrapper.DocReadyInit;
import com.codenvy.ide.jseditor.client.keymap.KeyBindingAction;
import com.codenvy.ide.jseditor.client.keymap.Keybinding;
import com.codenvy.ide.jseditor.client.partition.DocumentPartitioner;
import com.codenvy.ide.jseditor.client.reconciler.Reconciler;
import com.google.web.bindery.event.shared.EventBus;

import elemental.events.KeyboardEvent.KeyCode;

/**
 * Initialization controller for the text editor.
 * Sets-up (when available) the different components that depend on the document being ready.
 */
public class TextEditorInit {

    /** The logger. */
    private static final Logger LOG = Logger.getLogger(TextEditorInit.class.getName());

    private final TextEditorConfiguration configuration;
    private final EventBus generalEventBus;
    private final EditorHandle editorHandle;
    private final CodeAssistantFactory codeAssistantFactory;


    public TextEditorInit(final TextEditorConfiguration configuration,
                          final EventBus generalEventBus,
                          final EditorHandle editorHandle,
                          final CodeAssistantFactory codeAssistantFactory) {
        this.configuration = configuration;
        this.generalEventBus = generalEventBus;
        this.editorHandle = editorHandle;
        this.codeAssistantFactory = codeAssistantFactory;
    }

    /**
     * Initialize the text editor.
     * Sets itself as {@link DocumentReadyEvent} handler.
     */
    public void init() {

        final DocReadyInit<TextEditorInit> init = new DocReadyInit<TextEditorInit>() {

            @Override
            public void initialize(final DocumentHandle documentHandle, final TextEditorInit wrapped) {	
                configurePartitioner(documentHandle);
                configureReconciler(documentHandle);
                configureAnnotationModel(documentHandle);
                configureCodeAssist(documentHandle);
            }
        };
        new DocReadyWrapper<TextEditorInit>(generalEventBus, this.editorHandle, init, this);
    }

    /**
     * Configures the editor's DocumentPartitioner.
     * @param documentHandle the handle to the document
     */
    private void configurePartitioner(final DocumentHandle documentHandle) {
        final DocumentPartitioner partitioner = configuration.getPartitioner();
        if (partitioner != null) {
            partitioner.setDocumentHandle(documentHandle);
            documentHandle.getDocEventBus().addHandler(DocumentChangeEvent.TYPE, partitioner);
            partitioner.initialize();
        }
    }

    /**
     * Configures the editor's Reconciler.
     * @param documentHandle the handle to the document
     */
    private void configureReconciler(final DocumentHandle documentHandle) {
        final Reconciler reconciler = configuration.getReconciler();
        if (reconciler != null) {
            reconciler.setDocumentHandle(documentHandle);
            documentHandle.getDocEventBus().addHandler(DocumentChangeEvent.TYPE, reconciler);
            reconciler.install();
        }
    }

    /**
     * Configures the editor's annotation model.
     * @param documentHandle the handle on the editor
     */
    private void configureAnnotationModel(final DocumentHandle documentHandle) {
        final AnnotationModel annotationModel = configuration.getAnnotationModel();
        if (annotationModel == null) {
            return;
        }
        // add the renderers (event handler) before the model (event source)

        // gutter renderer
        final GutterAnnotationRenderer annotationRenderer = new GutterAnnotationRenderer();
        annotationRenderer.setDocumentHandle(documentHandle);
        annotationRenderer.setHasGutter(this.editorHandle.getEditor().getHasGutter());
        documentHandle.getDocEventBus().addHandler(AnnotationModelEvent.TYPE, annotationRenderer);
        documentHandle.getDocEventBus().addHandler(ClearAnnotationModelEvent.TYPE, annotationRenderer);

        // inline renderer
        final InlineAnnotationRenderer inlineAnnotationRenderer = new InlineAnnotationRenderer();
        inlineAnnotationRenderer.setDocumentHandle(documentHandle);
        inlineAnnotationRenderer.setHasTextMarkers(this.editorHandle.getEditor().getHasTextMarkers());
        documentHandle.getDocEventBus().addHandler(AnnotationModelEvent.TYPE, inlineAnnotationRenderer);

        annotationModel.setDocumentHandle(documentHandle);
        documentHandle.getDocEventBus().addHandler(DocumentChangeEvent.TYPE, annotationModel);
    }

    /**
     * Configure the editor's code assistant.
     * @param documentHandle the handle on the document
     */
    private void configureCodeAssist(final DocumentHandle documentHandle) {
        if (this.codeAssistantFactory == null) {
            return;
        }
        final StringMap<CodeAssistProcessor> processors = configuration.getContentAssistantProcessors();

        if (processors != null) {
            LOG.info("Creating code assistant.");
            this.editorHandle.getEditor().setCodeAssistEnabled(true);

            final CodeAssistant codeAssistant = this.codeAssistantFactory.create(this.editorHandle,
                                                                                 this.configuration.getPartitioner());
            processors.iterate(new IterationCallback<CodeAssistProcessor>() {
                @Override
                public void onIteration(final String key, final CodeAssistProcessor value) {
                    codeAssistant.setCodeAssistantProcessor(key, value);
                }
            });
            final KeyBindingAction action = new KeyBindingAction() {
                @Override
                public void action() {
                    showCompletion(codeAssistant);
                }
            };
            final HasKeybindings hasKeybindings = this.editorHandle.getEditor().getHasKeybindings();
            hasKeybindings.addKeybinding(new Keybinding(true, false, false, false, KeyCode.SPACE, action));

            // handle CompletionRequest events that come from text operations instead of simple key binding
            documentHandle.getDocEventBus().addHandler(CompletionRequestEvent.TYPE, new CompletionRequestHandler() {
                @Override
                public void onCompletionRequest(final CompletionRequestEvent event) {
                    showCompletion(codeAssistant);
                }
            });
        }
    }

    /**
     * Show the available completions.
     *
     * @param codeAssistant the code assistant
     */
    private void showCompletion(final CodeAssistant codeAssistant) {
        editorHandle.getEditor().showCompletionProposals(new CompletionsSource() {

            @Override
            public void computeCompletions(final CompletionReadyCallback callback) {
                codeAssistant.computeCompletionProposals(new CodeAssistCallback() {
                    @Override
                    public void proposalComputed(final List<CompletionProposal> proposals) {
                        callback.onCompletionReady(proposals);
                    }
                });
            }
        });
    }
}
