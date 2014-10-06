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
package com.codenvy.ide.jseditor.client.editorconfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.codenvy.ide.api.texteditor.ContentFormatter;
import com.codenvy.ide.api.texteditor.outline.OutlineModel;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.jseditor.client.annotation.AnnotationModel;
import com.codenvy.ide.jseditor.client.codeassist.CodeAssistProcessor;
import com.codenvy.ide.jseditor.client.partition.DocumentPartitioner;
import com.codenvy.ide.jseditor.client.partition.DocumentPositionMap;
import com.codenvy.ide.jseditor.client.reconciler.Reconciler;

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
}
