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
package org.exoplatform.ide.editor.css.client;

import com.google.collide.client.CollabEditor;

import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.editor.css.client.contentassist.CssAutocompleter;
import org.exoplatform.ide.editor.css.client.contentassist.CssContentAssistProcessor;
import org.exoplatform.ide.editor.shared.text.IDocument;

/**
 * CSS editor based on {@link CollabEditor}.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: CssEditor.java Feb 4, 2013 1:09:50 PM azatsarynnyy $
 */
public class CssEditor extends CollabEditor {
    /**
     * Constructs new editor for the provided MIME-type.
     *
     * @param mimeType
     */
    public CssEditor(String mimeType) {
        super(mimeType);
        CssAutocompleter autocompleter = CssAutocompleter.create();
        editorBundle.getAutocompleter().addLanguageSpecificAutocompleter(autocompleter);
        editorBundle.getAutocompleter().addContentAssitProcessor(IDocument.DEFAULT_CONTENT_TYPE,
                                                                 new CssContentAssistProcessor(autocompleter));
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
