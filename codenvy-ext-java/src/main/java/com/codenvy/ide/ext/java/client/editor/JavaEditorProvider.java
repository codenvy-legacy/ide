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

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.jdt.JavaPartitions;
import com.codenvy.ide.text.DocumentFactory;
import com.codenvy.ide.texteditor.api.ContentFormatter;
import com.codenvy.ide.util.executor.UserActivityManager;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
@Singleton
public class JavaEditorProvider implements EditorProvider {
    private final DocumentProvider            documentProvider;
    private final UserActivityManager         activityManager;
    private final NotificationManager         notificationManager;
    private       Provider<CodenvyTextEditor> editorProvider;
    private       JavaParserWorker            worker;
    private       FileSaveWatcher             watcher;
    private       AnalyticsEventLogger        eventLogger;
    private       ContentFormatter            contentFormatter;

    /**
     * @param resources
     * @param activityManager
     */
    @Inject
    public JavaEditorProvider(Resources resources,
                              UserActivityManager activityManager,
                              Provider<CodenvyTextEditor> editorProvider,
                              DocumentFactory documentFactory,
                              NotificationManager notificationManager,
                              JavaParserWorker worker,
                              EventBus eventBus,
                              FileSaveWatcher watcher,
                              ContentFormatter contentFormatter,
                              AnalyticsEventLogger eventLogger) {
        super();
        this.activityManager = activityManager;
        this.editorProvider = editorProvider;
        this.worker = worker;
        this.watcher = watcher;
        this.eventLogger = eventLogger;
        this.documentProvider =
                new CompilationUnitDocumentProvider(resources.workspaceEditorCss(), JavaResources.INSTANCE.css(), documentFactory,
                                                    eventBus);
        this.notificationManager = notificationManager;
        this.contentFormatter = contentFormatter;
    }

    @Override
    public String getId() {
        return "codenvyJavaEditor";
    }

    @Override
    public String getDescription() {
        return "Codenvy Java Editor";
    }

    /** {@inheritDoc} */
    @Override
    public EditorPartPresenter getEditor() {

        CodenvyTextEditor textEditor = editorProvider.get();
        JavaEditorConfiguration configuration =
                new JavaEditorConfiguration(activityManager, JavaResources.INSTANCE, textEditor, JavaPartitions.JAVA_PARTITIONING,
                                            worker, contentFormatter, eventLogger, notificationManager);

        textEditor.initialize(configuration, documentProvider, notificationManager);
        watcher.editorOpened(textEditor);
        return textEditor;
    }

}
