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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.java.client.editor.outline.OutlineUpdater;
import com.codenvy.ide.ext.java.jdt.core.IProblemRequestor;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.texteditor.api.outline.OutlineModel;
import com.codenvy.ide.texteditor.api.reconciler.DirtyRegion;
import com.codenvy.ide.texteditor.api.reconciler.ReconcilingStrategy;
import com.codenvy.ide.util.loging.Log;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaReconcilerStrategy implements ReconcilingStrategy, JavaParserWorker.WorkerCallback<IProblem> {

    private final TextEditorPartPresenter editor;
    private       Document                document;
    private       JavaParserWorker        worker;
    private OutlineModel outlineModel;
    private File file;

    public JavaReconcilerStrategy(TextEditorPartPresenter editor, JavaParserWorker worker, OutlineModel outlineModel) {
        this.editor = editor;
        this.worker = worker;
        this.outlineModel = outlineModel;
    }

    /** {@inheritDoc} */
    @Override
    public void setDocument(Document document) {
        this.document = document;
        file = editor.getEditorInput().getFile();
        new OutlineUpdater(file.getId(), outlineModel, worker);
    }

    /** {@inheritDoc} */
    @Override
    public void reconcile(DirtyRegion dirtyRegion, Region subRegion) {
        parse();
    }

    /**
     *
     */
    public void parse() {
        worker.parse(document.get(), file.getName(), file.getId(), file.getParent().getName(),file.getProject().getPath(), this);
    }

    /** {@inheritDoc} */
    @Override
    public void reconcile(Region partition) {
        parse();
    }

    /** @return the file */
    public File getFile() {
        return file;
    }

    @Override
    public void onResult(Array<IProblem> problems) {
        AnnotationModel annotationModel = editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
        if (annotationModel == null)
            return;
        IProblemRequestor problemRequestor;
        if (annotationModel instanceof IProblemRequestor) {
            problemRequestor = (IProblemRequestor)annotationModel;
            problemRequestor.beginReporting();
        } else return;
        try {
            for (IProblem problem : problems.asIterable()) {
                  problemRequestor.acceptProblem(problem);
            }
        } catch (Exception e) {
            Log.error(getClass(), e);
        } finally {
            problemRequestor.endReporting();
        }
    }
}
