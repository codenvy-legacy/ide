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
package com.codenvy.ide.java.client.editor;

import com.codenvy.ide.api.editor.TextEditorPartPresenter;
import com.codenvy.ide.api.outline.OutlineModel;
import com.codenvy.ide.java.client.JavaClientBundle;
import com.codenvy.ide.java.client.JavaPartitions;
import com.codenvy.ide.java.client.editor.outline.JavaNodeRenderer;
import com.codenvy.ide.java.client.internal.ui.text.BracketInserter;
import com.codenvy.ide.java.client.internal.ui.text.JavaAutoEditStrategy;
import com.codenvy.ide.java.client.internal.ui.text.JavaDocAutoIndentStrategy;
import com.codenvy.ide.java.client.internal.ui.text.JavaStringAutoIndentStrategy;
import com.codenvy.ide.java.client.internal.ui.text.SmartSemicolonAutoEditStrategy;
import com.codenvy.ide.java.client.projectmodel.JavaProject;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.TextEditorViewImpl;
import com.codenvy.ide.texteditor.api.AutoEditStrategy;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;
import com.codenvy.ide.texteditor.api.codeassistant.CodeAssistProcessor;
import com.codenvy.ide.texteditor.api.parser.BasicTokenFactory;
import com.codenvy.ide.texteditor.api.parser.CmParser;
import com.codenvy.ide.texteditor.api.parser.Parser;
import com.codenvy.ide.texteditor.api.quickassist.QuickAssistProcessor;
import com.codenvy.ide.texteditor.api.reconciler.Reconciler;
import com.codenvy.ide.texteditor.api.reconciler.ReconcilerImpl;
import com.codenvy.ide.util.executor.BasicIncrementalScheduler;
import com.codenvy.ide.util.executor.UserActivityManager;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JavaEditorConfiguration extends TextEditorConfiguration {

    private UserActivityManager     manager;
    private TextEditorPartPresenter javaEditor;
    private JavaCodeAssistProcessor codeAssistProcessor;
    private JavaReconcilerStrategy  reconcilerStrategy;
    private OutlineModel            outlineModel;
    private String                  documentPartitioning;
    private JavaProject             project;


    public JavaEditorConfiguration(UserActivityManager manager, JavaClientBundle resources, TextEditorPartPresenter javaEditor,
                                   String documentPartitioning) {
        super();
        this.manager = manager;
        this.javaEditor = javaEditor;
        this.documentPartitioning = documentPartitioning;
        outlineModel = new OutlineModel(new JavaNodeRenderer(resources));
        reconcilerStrategy = new JavaReconcilerStrategy(javaEditor);
    }

    private static native CmParser getParserForMime(String mime) /*-{
        conf = $wnd.CodeMirror.defaults;
        return $wnd.CodeMirror.getMode(conf, mime);
    }-*/;

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
            codeAssistProcessor = new JavaCodeAssistProcessor(
                    //TODO configure doc context
                    "rest/ide/code-assistant/java/class-doc?fqn=", reconcilerStrategy);
        }
        return codeAssistProcessor;
    }

    /** {@inheritDoc} */
    @Override
    public JsonStringMap<CodeAssistProcessor> getContentAssistantProcessors(TextEditorPartView view) {

        JsonStringMap<CodeAssistProcessor> map = JsonCollections.createStringMap();
        map.put(Document.DEFAULT_CONTENT_TYPE, getOrCreateCodeAssistProcessor());
        return map;
    }

    /** {@inheritDoc} */
    @Override
    public QuickAssistProcessor getQuickAssistAssistant(TextEditorPartView view) {
        JavaCorrectionAssistant assistant = new JavaCorrectionAssistant(javaEditor, reconcilerStrategy);
        assistant.install(view);
        ((TextEditorViewImpl)view).setQuickAssistAssistant(assistant);
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public OutlineModel getOutline(TextEditorPartView view) {
//        new OutlineModelUpdater(outlineModel, reconcilerStrategy);
//        return outlineModel;
        return super.getOutline(view);
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
