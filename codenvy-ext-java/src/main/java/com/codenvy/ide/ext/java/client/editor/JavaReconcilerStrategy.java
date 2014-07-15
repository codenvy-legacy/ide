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

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorWithErrors;
import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.java.client.JavaLocalizationConstant;
import com.codenvy.ide.ext.java.client.editor.outline.OutlineUpdater;
import com.codenvy.ide.ext.java.jdt.core.IProblemRequestor;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.texteditor.api.outline.OutlineModel;
import com.codenvy.ide.texteditor.api.reconciler.DirtyRegion;
import com.codenvy.ide.texteditor.api.reconciler.ReconcilingStrategy;
import com.codenvy.ide.util.loging.Log;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaReconcilerStrategy implements ReconcilingStrategy, JavaParserWorker.WorkerCallback<IProblem> {

    private final TextEditorPartPresenter  editor;
    private       Document                 document;
    private       JavaParserWorker         worker;
    private       OutlineModel             outlineModel;
    private       NotificationManager      notificationManager;
    private       JavaCodeAssistProcessor  codeAssistProcessor;
    private       JavaLocalizationConstant localizationConstant;
    private       File                     file;
    private       EditorWithErrors         editorWithErrors;
    private boolean first = true;
    private Notification notification;

    public JavaReconcilerStrategy(TextEditorPartPresenter editor,
                                  JavaParserWorker worker,
                                  OutlineModel outlineModel,
                                  NotificationManager notificationManager,
                                  JavaCodeAssistProcessor codeAssistProcessor,
                                  JavaLocalizationConstant localizationConstant) {
        this.editor = editor;
        this.worker = worker;
        this.outlineModel = outlineModel;
        this.notificationManager = notificationManager;
        this.codeAssistProcessor = codeAssistProcessor;
        this.localizationConstant = localizationConstant;
        if (editor instanceof EditorWithErrors) {
            editorWithErrors = ((EditorWithErrors)editor);
        }
        editor.addCloseHandler(new EditorPartPresenter.EditorPartCloseHandler() {
            @Override
            public void onClose(EditorPartPresenter editor) {
                if (notification != null && !notification.isFinished()) {
                    notification.setStatus(FINISHED);
                    notification.setType(Notification.Type.WARNING);
                    notification.setMessage("Parsing file canceled");
                    notification = null;
                }
            }
        });
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
        if (first) {
            notification = new Notification("Parsing file...", Notification.Status.PROGRESS);
            codeAssistProcessor.disableCodeAssistant();
            notificationManager.showNotification(notification);
            first = false;
        }
        worker.parse(document.get(), file.getName(), file.getId(), file.getParent().getName(), file.getProject().getPath(), this);
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
        if (!first) {
            if (notification != null) {
                notification.setStatus(FINISHED);
                notification.setMessage(localizationConstant.fileFuccessfullyParsed());
                notification = null;
            }
            codeAssistProcessor.enableCodeAssistant();
        }
        AnnotationModel annotationModel = editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
        if (annotationModel == null)
            return;
        IProblemRequestor problemRequestor;
        if (annotationModel instanceof IProblemRequestor) {
            problemRequestor = (IProblemRequestor)annotationModel;
            problemRequestor.beginReporting();
        } else {
            if (editorWithErrors != null) {
                editorWithErrors.setErrorState(EditorWithErrors.EditorState.NONE);
            }
            return;
        }
        try {
            boolean error = false;
            boolean warning = false;
            for (IProblem problem : problems.asIterable()) {
                if (!error) {
                    error = problem.isError();
                }
                if (!warning) {
                    warning = problem.isWarning();
                }
                problemRequestor.acceptProblem(problem);
            }
            if (editorWithErrors != null) {
                if (error) {
                    editorWithErrors.setErrorState(EditorWithErrors.EditorState.ERROR);
                } else if (warning) {
                    editorWithErrors.setErrorState(EditorWithErrors.EditorState.WARNING);
                } else {
                    editorWithErrors.setErrorState(EditorWithErrors.EditorState.NONE);
                }
            }
        } catch (Exception e) {
            Log.error(getClass(), e);
        } finally {
            problemRequestor.endReporting();
        }
    }
}
