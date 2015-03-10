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
package org.eclipse.che.ide.jseditor.client.editorconfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.eclipse.che.ide.api.texteditor.outline.OutlineModel;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.jseditor.client.annotation.AnnotationModel;
import org.eclipse.che.ide.jseditor.client.changeintercept.ChangeInterceptorProvider;
import org.eclipse.che.ide.jseditor.client.codeassist.CodeAssistProcessor;
import org.eclipse.che.ide.jseditor.client.partition.DocumentPartitioner;
import org.eclipse.che.ide.jseditor.client.quickfix.QuickAssistProcessor;
import org.eclipse.che.ide.jseditor.client.reconciler.Reconciler;
import org.eclipse.che.ide.jseditor.client.formatter.ContentFormatter;
import org.eclipse.che.ide.jseditor.client.partition.DocumentPositionMap;

/**
 * Configure extended functions of the editor.
 */
public interface TextEditorConfiguration {

    /**
     * Returns the visual width of the tab character. This implementation always returns 3.
     *
     * @return the tab width
     */
    public int getTabWidth();

    /**
     * Returns the outline model. This implementation always returns <code>null</code>.
     *
     * @return a model that used to build outline tree.
     */
    @Nullable
    public OutlineModel getOutline();

    /**
     * Returns the content formatter.
     *
     * @return the content formatter
     */
    @Nullable
    public ContentFormatter getContentFormatter();

    /**
     * Returns the content assistant (completion) processors.
     *
     * @return the code assist processors
     */
    @Nullable
    public StringMap<CodeAssistProcessor> getContentAssistantProcessors();

    /**
     * Returns the reconciler.
     *
     * @return the reconciler
     */
    @Nullable
    public Reconciler getReconciler();

    /**
     * Returns the document partitioner.
     *
     * @return the document partitioner
     */
    @Nonnull
    public DocumentPartitioner getPartitioner();

    /**
     * Return the document position model.
     *
     * @return the position model
     */
    @Nullable
    public DocumentPositionMap getDocumentPositionMap();

    /**
     * Return the annotation model.
     *
     * @return the annotation model
     */
    @Nullable
    public AnnotationModel getAnnotationModel();

    /**
     * Return the Quickassist assistant processor.
     *
     * @return the quickassist assistant processor
     */
    @Nullable
    public QuickAssistProcessor getQuickAssistProcessor();

    /**
     * Return the {@link org.eclipse.che.ide.jseditor.client.changeintercept.ChangeInterceptorProvider}.<br>
     * @return the change interceptors
     */
    @Nullable
    ChangeInterceptorProvider getChangeInterceptorProvider();
}
