/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.java.client.editor;

import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.ext.java.client.JavaAutoBeanFactory;
import com.codenvy.ide.ext.java.client.NameEnvironment;
import com.codenvy.ide.ext.java.client.TypeInfoStorage;
import com.codenvy.ide.ext.java.client.core.IProblemRequestor;
import com.codenvy.ide.ext.java.client.core.compiler.IProblem;
import com.codenvy.ide.ext.java.client.core.dom.AST;
import com.codenvy.ide.ext.java.client.core.dom.ASTNode;
import com.codenvy.ide.ext.java.client.core.dom.ASTParser;
import com.codenvy.ide.ext.java.client.core.dom.CompilationUnit;
import com.codenvy.ide.ext.java.client.internal.compiler.env.INameEnvironment;
import com.codenvy.ide.json.JsonCollections;
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
import com.google.gwt.core.shared.GWT;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaReconcilerStrategy implements ReconcilingStrategy, AstProvider {

    private Document document;

    private INameEnvironment nameEnvironment;

    private final TextEditorPartPresenter editor;

    private File file;

    private ListenerManager<AstListener> astListeners;

    private static JavaReconcilerStrategy instance;

    public static JavaReconcilerStrategy get() {
        return instance;
    }

    /** @param editor */
    public JavaReconcilerStrategy(TextEditorPartPresenter editor) {
        this.editor = editor;
        instance = this;
        astListeners = ListenerManager.create();
    }

    /** {@inheritDoc} */
    @Override
    public void setDocument(Document document) {
        this.document = document;
        file = editor.getEditorInput().getFile();
        nameEnvironment =
                new NameEnvironment(file.getProject().getId(), GWT.<JavaAutoBeanFactory>create(JavaAutoBeanFactory.class),
                                    "/ide/rest");
        TypeInfoStorage.get().setPackages(file.getProject().getId(), JsonCollections.createStringSet());
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
        AnnotationModel annotationModel = editor.getDocumentProvider().getAnnotationModel(editor.getEditorInput());
        if (annotationModel == null)
            return;
        IProblemRequestor problemRequestor = null;
        if (annotationModel instanceof IProblemRequestor) {
            problemRequestor = (IProblemRequestor)annotationModel;
            problemRequestor.beginReporting();
        }
        try {
            ASTParser parser = ASTParser.newParser(AST.JLS3);
            parser.setSource(document.get());
            parser.setKind(ASTParser.K_COMPILATION_UNIT);
            parser.setUnitName(file.getName().substring(0, file.getName().lastIndexOf('.')));
            parser.setResolveBindings(true);
            parser.setNameEnvironment(nameEnvironment);
            ASTNode ast = parser.createAST();
            CompilationUnit unit = (CompilationUnit)ast;
            sheduleAstChanged(unit);
            IProblem[] problems = unit.getProblems();
            for (IProblem p : problems) {
                problemRequestor.acceptProblem(p);
            }
            IProblem[] tasks = (IProblem[])unit.getProperty("tasks");
            if (tasks != null) {
                for (IProblem p : tasks) {
                    problemRequestor.acceptProblem(p);
                }
            }
        } catch (Exception e) {
            Log.error(getClass(), e);
        } finally {
            if (problemRequestor != null)
                problemRequestor.endReporting();
        }
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

}
