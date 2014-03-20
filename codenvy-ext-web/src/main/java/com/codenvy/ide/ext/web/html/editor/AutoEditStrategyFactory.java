/*
 * CODENVY CONFIDENTIAL
 *  __________________
 *
 *   [2014] Codenvy, S.A.
 *   All Rights Reserved.
 *
 *  NOTICE:  All information contained herein is, and remains
 *  the property of Codenvy S.A. and its suppliers,
 *  if any.  The intellectual and technical concepts contained
 *  herein are proprietary to Codenvy S.A.
 *  and its suppliers and may be covered by U.S. and Foreign Patents,
 *  patents in process, and are protected by trade secret or copyright law.
 *  Dissemination of this information or reproduction of this material
 *  is strictly forbidden unless prior written permission is obtained
 *  from Codenvy S.A..
 */

package com.codenvy.ide.ext.web.html.editor;

import com.codenvy.ide.texteditor.api.AutoEditStrategy;
import com.codenvy.ide.texteditor.api.TextEditorPartView;

/**
 * Allows to define a new AutoEditStrategy based on text editor and content type.
 * @author Florent Benoit
 */
public interface AutoEditStrategyFactory {

    /**
     * Build a new instance
     * @param textEditorPartView editor view
     * @param contentType content type
     * @return a new strategy
     */
    AutoEditStrategy build(TextEditorPartView textEditorPartView, String contentType);
}
