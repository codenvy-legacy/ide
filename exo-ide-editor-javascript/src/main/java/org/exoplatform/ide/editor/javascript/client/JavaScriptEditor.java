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
package org.exoplatform.ide.editor.javascript.client;

import com.google.collide.client.CollabEditor;

import org.exoplatform.ide.editor.javascript.client.contentassist.JavaScriptAutocompleter;
import org.exoplatform.ide.editor.javascript.client.contentassist.JavaScriptContentAssistProcessor;
import org.exoplatform.ide.editor.shared.text.IDocument;

/**
 * JavaScript editor based on {@link CollabEditor}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: JavaScriptEditor.java Aug 27, 2012 4:21:04 PM azatsarynnyy $
 */
public class JavaScriptEditor extends CollabEditor {
    /**
     * Constructs new instance of {@link JavaScriptEditor} for a specific MIME-type.
     *
     * @param mimeType
     *         MIME-type
     */
    public JavaScriptEditor(String mimeType) {
        super(mimeType);
        editorBundle.getAutocompleter().addLanguageSpecificAutocompleter(new JavaScriptAutocompleter());
        editorBundle.getAutocompleter().addContentAssitProcessor(IDocument.DEFAULT_CONTENT_TYPE,
                                                                 new JavaScriptContentAssistProcessor());
        extParser = new JavaScriptExternalParser();
    }
}
