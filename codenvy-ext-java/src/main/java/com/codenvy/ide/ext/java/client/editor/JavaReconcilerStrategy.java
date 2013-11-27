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
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.java.jdt.core.IProblemRequestor;
import com.codenvy.ide.ext.java.jdt.core.compiler.IProblem;
import com.codenvy.ide.ext.java.jdt.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.jdt.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.Region;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.texteditor.api.reconciler.DirtyRegion;
import com.codenvy.ide.texteditor.api.reconciler.ReconcilingStrategy;
import com.codenvy.ide.util.ListenerManager;
import com.codenvy.ide.util.ListenerManager.Dispatcher;
import com.codenvy.ide.util.ListenerRegistrar.Remover;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaReconcilerStrategy implements ReconcilingStrategy, AstProvider, JavaParserWorker.JavaParserCallback {

    private final  TextEditorPartPresenter editor;
    private        Document                document;
    private        INameEnvironment        nameEnvironment;
    private        ResourceProvider        resourceProvider;
    private JavaParserWorker worker;
    private File                         file;
    private ListenerManager<AstListener> astListeners;

    public JavaReconcilerStrategy(TextEditorPartPresenter editor, ResourceProvider resourceProvider, JavaParserWorker worker) {
        this.editor = editor;
        this.resourceProvider = resourceProvider;
        this.worker = worker;
        astListeners = ListenerManager.create();
    }

    /** {@inheritDoc} */
    @Override
    public void setDocument(Document document) {
        this.document = document;
        file = editor.getEditorInput().getFile();
//        nameEnvironment =
//                new NameEnvironment(file.getProject().getId(), "/ide/rest");
//        TypeInfoStorage.get().setPackages(file.getProject().getId(), JsonCollections.createStringSet());

    }

    /** {@inheritDoc} */
    @Override
    public void reconcile(DirtyRegion dirtyRegion, Region subRegion) {
        parse();
    }

    /**
     *
     */
    private void parse() {
        worker.parse(document.get(), file.getName(), file.getParent().getName(), this);
    }

    private void sheduleAstChanged(final CompilationUnit unit) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                astListeners.dispatch(new Dispatcher<AstProvider.AstListener>() {

                    @Override
                    public void dispatch(AstListener listener) {
                        listener.onCompilationUnitChanged(unit);
                    }
                });
            }
        });
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

    /** @return the nameEnvironment */
    public INameEnvironment getNameEnvironment() {
        return nameEnvironment;
    }

    public Remover addAstListener(AstListener listener) {
        return astListeners.add(listener);
    }

    @Override
    public void onProblems(JsonArray<IProblem> problems) {
        AnnotationModel annotationModel = editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
        if (annotationModel == null)
            return;
        IProblemRequestor problemRequestor;
        if (annotationModel instanceof IProblemRequestor) {
            problemRequestor = (IProblemRequestor)annotationModel;
            problemRequestor.beginReporting();
        } else return;
        try {
            for(IProblem problem : problems.asIterable()){
                problemRequestor.acceptProblem(problem);
            }
        } catch (Exception e) {
            Log.error(getClass(), e);
        } finally {
            problemRequestor.endReporting();
        }
    }
}
