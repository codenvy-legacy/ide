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
package com.codenvy.ide.api.editor;

import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.codenvy.ide.texteditor.api.TextEditorPartView;

import javax.validation.constraints.NotNull;

/**
 * CodenvyTextEditor is an embedded default fully featured Text Editor
 *
 * @author <a href="mailto:evidolob@codenvy.com">Evgen Vidolob</a>
 */
public interface CodenvyTextEditor extends TextEditorPartPresenter {
    /**
     * Initializes this editor with the configuration and document provider/
     *
     * @param configuration
     *         the configuration of this editor.
     * @param documentProvider
     *         the document provider which used in this editor
     * @param notificationManager
     *         the manager that provides showing notifications
     */
    void initialize(@NotNull TextEditorConfiguration configuration, @NotNull DocumentProvider documentProvider,
                    @NotNull NotificationManager notificationManager);

    /**
     * @return the text editor view implementation
     * //todo need to introduce more simple way to use TextEditorPartView interface
     */
    TextEditorPartView getView();

    /**
     * @return the text editor configuration
     */
    TextEditorConfiguration getConfiguration();
}
