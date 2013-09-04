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
package org.exoplatform.ide.editor.html.client;

import com.google.collide.client.CollabEditor;

import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.editor.css.client.contentassist.CssAutocompleter;
import org.exoplatform.ide.editor.css.client.contentassist.CssContentAssistProcessor;
import org.exoplatform.ide.editor.html.client.contentassist.HtmlAutocompleter;
import org.exoplatform.ide.editor.html.client.contentassist.HtmlContentAssistProcessor;
import org.exoplatform.ide.editor.javascript.client.contentassist.JavaScriptAutocompleter;
import org.exoplatform.ide.editor.javascript.client.contentassist.JavaScriptContentAssistProcessor;
import org.exoplatform.ide.editor.shared.text.IDocument;

/**
 * HTML editor based on {@link CollabEditor}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlEditor.java Feb 7, 2013 10:48:00 AM azatsarynnyy $
 */
public class HtmlEditor extends CollabEditor {

    /**
     * Constructs new editor for the given MIME-type.
     *
     * @param mimeType
     */
    public HtmlEditor(String mimeType) {
        super(mimeType);
        /*
        CssAutocompleter cssAutocompleter = CssAutocompleter.create();
        HtmlAutocompleter htmlAutocompleter = HtmlAutocompleter.create(cssAutocompleter, new JavaScriptAutocompleter());
        editorBundle.getAutocompleter().addLanguageSpecificAutocompleter(htmlAutocompleter);
        editorBundle.getAutocompleter().addContentAssitProcessor(
                IDocument.DEFAULT_CONTENT_TYPE,
                new HtmlContentAssistProcessor(new CssContentAssistProcessor(cssAutocompleter),
                                               new JavaScriptContentAssistProcessor()));
                                               */
    }

    /** @see com.google.collide.client.CollabEditor#isCapable(org.exoplatform.ide.editor.client.api.EditorCapability) */
    @Override
    public boolean isCapable(EditorCapability capability) {
        if (capability == EditorCapability.OUTLINE) {
            return false;
        }
        return super.isCapable(capability);
    }
}
