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
package org.exoplatform.ide.editor.php.client;

import com.google.collide.client.CollabEditor;

import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.css.client.contentassist.CssAutocompleter;
import org.exoplatform.ide.editor.css.client.contentassist.CssContentAssistProcessor;
import org.exoplatform.ide.editor.html.client.contentassist.HtmlAutocompleter;
import org.exoplatform.ide.editor.html.client.contentassist.HtmlContentAssistProcessor;
import org.exoplatform.ide.editor.javascript.client.contentassist.JavaScriptAutocompleter;
import org.exoplatform.ide.editor.javascript.client.contentassist.JavaScriptContentAssistProcessor;
import org.exoplatform.ide.editor.php.client.contentassist.PhpAutocompleter;
import org.exoplatform.ide.editor.php.client.contentassist.PhpContentAssistProcessor;
import org.exoplatform.ide.editor.shared.text.IDocument;

/**
 * PHP editor based on {@link CollabEditor}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: PhpEditor.java Apr 12, 2013 5:02:00 PM azatsarynnyy $
 */
public class PhpEditor extends CollabEditor {

    /**
     * Constructs new editor for the given MIME-type.
     * 
     * @param mimeType
     */
    public PhpEditor(String mimeType) {
        super(mimeType);
        CssAutocompleter cssAutocompleter = CssAutocompleter.create();
        HtmlAutocompleter htmlAutocompleter = HtmlAutocompleter.create(cssAutocompleter, new JavaScriptAutocompleter());
        editorBundle.getAutocompleter().addLanguageSpecificAutocompleter(new PhpAutocompleter(htmlAutocompleter));

        ContentAssistProcessor htmlContentAssistProcessor = new HtmlContentAssistProcessor(new CssContentAssistProcessor(cssAutocompleter),
                                                                                           new JavaScriptContentAssistProcessor());
        ContentAssistProcessor phpContentAssistProcessor = new PhpContentAssistProcessor(htmlContentAssistProcessor);
        editorBundle.getAutocompleter().addContentAssitProcessor(IDocument.DEFAULT_CONTENT_TYPE, phpContentAssistProcessor);
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
