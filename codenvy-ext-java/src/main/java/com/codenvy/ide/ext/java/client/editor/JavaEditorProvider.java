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

import com.codenvy.ide.Resources;
import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.java.client.JavaClientBundle;
import com.codenvy.ide.ext.java.client.JavaPartitions;
import com.codenvy.ide.text.DocumentFactory;
import com.codenvy.ide.util.executor.UserActivityManager;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

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
    private       ResourceProvider            resourceProvider;
    private JavaParserWorker worker;

    /**
     * @param resources
     * @param activityManager
     */
    @Inject
    public JavaEditorProvider(Resources resources, UserActivityManager activityManager,
                              Provider<CodenvyTextEditor> editorProvider, DocumentFactory documentFactory,
                              NotificationManager notificationManager, ResourceProvider resourceProvider,
                              JavaParserWorker worker) {
        super();
        this.activityManager = activityManager;
        this.editorProvider = editorProvider;
        this.resourceProvider = resourceProvider;
        this.worker = worker;
        this.documentProvider =
                new CompilationUnitDocumentProvider(resources.workspaceEditorCss(), JavaClientBundle.INSTANCE.css(), documentFactory);
        this.notificationManager = notificationManager;
    }

    /** @see com.codenvy.ide.api.editor.EditorProvider#getEditor() */
    @Override
    public EditorPartPresenter getEditor() {

        CodenvyTextEditor textEditor = editorProvider.get();
        JavaEditorConfiguration configuration =
                new JavaEditorConfiguration(activityManager, JavaClientBundle.INSTANCE, textEditor, JavaPartitions.JAVA_PARTITIONING,
                                            resourceProvider, worker);

        textEditor.initialize(configuration, documentProvider, notificationManager);
        return textEditor;
    }

}
