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
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.client.editor.outline.JavaNodeRenderer;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ext.java.jdt.JavaPartitions;
import com.codenvy.ide.ext.java.jdt.internal.ui.text.BracketInserter;
import com.codenvy.ide.ext.java.jdt.internal.ui.text.JavaAutoEditStrategy;
import com.codenvy.ide.ext.java.jdt.internal.ui.text.JavaDocAutoIndentStrategy;
import com.codenvy.ide.ext.java.jdt.internal.ui.text.JavaStringAutoIndentStrategy;
import com.codenvy.ide.ext.java.jdt.internal.ui.text.SmartSemicolonAutoEditStrategy;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.api.AutoEditStrategy;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.outline.OutlineModel;
import com.codenvy.ide.texteditor.api.parser.BasicTokenFactory;
import com.codenvy.ide.texteditor.api.parser.CmParser;
import com.codenvy.ide.texteditor.api.parser.Parser;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistProcessor;
import com.codenvy.ide.texteditor.api.reconciler.Reconciler;
import com.codenvy.ide.texteditor.api.reconciler.ReconcilerImpl;
import com.codenvy.ide.util.executor.BasicIncrementalScheduler;
import com.codenvy.ide.util.executor.UserActivityManager;


/**
 * Java specific configuration of the editor
 * @author Evgen Vidolob
 */
public class JavaEditorConfiguration extends TextEditorConfiguration {

    private UserActivityManager     manager;
    private TextEditorPartPresenter javaEditor;
    private JavaCodeAssistProcessor codeAssistProcessor;
    private JavaReconcilerStrategy  reconcilerStrategy;
    private OutlineModel            outlineModel;
    private String                  documentPartitioning;
    private JavaParserWorker        worker;
    private JavaResources javaResources;
    private JavaProject project;


    public JavaEditorConfiguration(UserActivityManager manager, JavaResources resources, TextEditorPartPresenter javaEditor,
                                   String documentPartitioning, JavaParserWorker worker) {
        super();
        this.manager = manager;
        this.javaEditor = javaEditor;
        this.documentPartitioning = documentPartitioning;
        this.worker = worker;
        this.javaResources = resources;
        outlineModel = new OutlineModel(new JavaNodeRenderer(resources));
        reconcilerStrategy = new JavaReconcilerStrategy(javaEditor, worker, outlineModel);

    }

    /** {@inheritDoc} */
    @Override
    public Parser getParser(TextEditorPartView view) {
        CmParser parser = getParserForMime("text/x-java");
        parser.setNameAndFactory("clike", new BasicTokenFactory());
        return parser;
    }

    /** {@inheritDoc} */
    @Override
    public Reconciler getReconciler(TextEditorPartView view) {
        BasicIncrementalScheduler scheduler = new BasicIncrementalScheduler(manager, 50, 100);
        ReconcilerImpl reconciler = new ReconcilerImpl(Document.DEFAULT_PARTITIONING, scheduler);
        reconciler.addReconcilingStrategy(Document.DEFAULT_CONTENT_TYPE, reconcilerStrategy);
        return reconciler;
    }

    private JavaCodeAssistProcessor getOrCreateCodeAssistProcessor() {
        if (codeAssistProcessor == null) {
            codeAssistProcessor = new JavaCodeAssistProcessor(javaEditor, worker, javaResources);
        }
        return codeAssistProcessor;
    }

    /** {@inheritDoc} */
    @Override
    public StringMap<CodeAssistProcessor> getContentAssistantProcessors(TextEditorPartView view) {

        StringMap<CodeAssistProcessor> map = Collections.createStringMap();
        map.put(Document.DEFAULT_CONTENT_TYPE, getOrCreateCodeAssistProcessor());
        return map;
    }

    /** {@inheritDoc} */
    @Override
    public QuickAssistProcessor getQuickAssistAssistant(TextEditorPartView view) {
        JavaCorrectionAssistant assistant = new JavaCorrectionAssistant(javaEditor, worker);
        assistant.install(view);
        ((TextEditorViewImpl)view).setQuickAssistAssistant(assistant);
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public OutlineModel getOutline(TextEditorPartView view) {
        return outlineModel;
    }

    @Override
    public String[] getConfiguredContentTypes(TextEditorPartView view) {
        return new String[]{
                Document.DEFAULT_CONTENT_TYPE,
                JavaPartitions.JAVA_DOC,
                JavaPartitions.JAVA_MULTI_LINE_COMMENT,
                JavaPartitions.JAVA_SINGLE_LINE_COMMENT,
                JavaPartitions.JAVA_STRING,
                JavaPartitions.JAVA_CHARACTER
        };
    }

    @Override
    public AutoEditStrategy[] getAutoEditStrategies(TextEditorPartView view, String contentType) {
        String partitioning = getConfiguredDocumentPartitioning(view);
        if (JavaPartitions.JAVA_DOC.equals(contentType) || JavaPartitions.JAVA_MULTI_LINE_COMMENT.equals(contentType))
            return new AutoEditStrategy[]{new JavaDocAutoIndentStrategy(partitioning)};
        else if (JavaPartitions.JAVA_STRING.equals(contentType))
            return new AutoEditStrategy[]{new SmartSemicolonAutoEditStrategy(partitioning), new JavaStringAutoIndentStrategy(partitioning)};
        else if (JavaPartitions.JAVA_CHARACTER.equals(contentType) || Document.DEFAULT_CONTENT_TYPE.equals(contentType))
            return new AutoEditStrategy[]{new SmartSemicolonAutoEditStrategy(partitioning),
                                          new JavaAutoEditStrategy(partitioning, getProject()),
                                          new BracketInserter(view)};
        else
            return new AutoEditStrategy[]{new JavaAutoEditStrategy(partitioning, getProject())};
    }

    @Override
    public String getConfiguredDocumentPartitioning(TextEditorPartView view) {
        if (documentPartitioning != null)
            return documentPartitioning;
        return super.getConfiguredDocumentPartitioning(view);
    }

    public JavaProject getProject() {
        return project;
    }
}
