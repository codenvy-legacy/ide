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

import com.codenvy.ide.api.texteditor.ContentFormatter;
import com.codenvy.ide.api.texteditor.outline.OutlineModel;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.jseditor.client.annotation.AnnotationModel;
import com.codenvy.ide.jseditor.client.codeassist.CodeAssistProcessor;
import com.codenvy.ide.jseditor.client.partition.ConstantPartitioner;
import com.codenvy.ide.jseditor.client.partition.DocumentPartitioner;
import com.codenvy.ide.jseditor.client.partition.DocumentPositionMap;
import com.codenvy.ide.jseditor.client.reconciler.Reconciler;

/**
 * Default implementation of the {@link TextEditorConfiguration}.
 */
public class DefaultTextEditorConfiguration implements TextEditorConfiguration {

    @Override
    public int getTabWidth() {
        return 3;
    }

    @Override
    public OutlineModel getOutline() {
        return null;
    }

    @Override
    public ContentFormatter getContentFormatter() {
        return null;
    }

    @Override
    public StringMap<CodeAssistProcessor> getContentAssistantProcessors() {
        return null;
    }

    @Override
    public Reconciler getReconciler() {
        return null;
    }

    @Override
    public DocumentPartitioner getPartitioner() {
        return new ConstantPartitioner();
    }

    @Override
    public AnnotationModel getAnnotationModel() {
        return null;
    }

    @Override
    public DocumentPositionMap getDocumentPositionMap() {
        return null;
    }
}
