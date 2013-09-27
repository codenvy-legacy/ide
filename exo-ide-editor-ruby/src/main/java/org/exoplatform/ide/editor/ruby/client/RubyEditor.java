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
package org.exoplatform.ide.editor.ruby.client;

import com.google.collide.client.CollabEditor;

import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.editor.ruby.client.contentassist.RubyAutocompliter;
import org.exoplatform.ide.editor.ruby.client.contentassist.RubyContentAssistProcessor;
import org.exoplatform.ide.editor.shared.text.IDocument;

/**
 * Ruby editor based on {@link CollabEditor}.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RubyEditor.java Apr 29, 2013 12:38:51 PM azatsarynnyy $
 */
public class RubyEditor extends CollabEditor {

    /**
     * Constructs new editor for the given MIME-type.
     * 
     * @param mimeType MIME-type
     */
    public RubyEditor(String mimeType) {
        super(mimeType);
        editorBundle.getAutocompleter().addLanguageSpecificAutocompleter(new RubyAutocompliter());
        editorBundle.getAutocompleter().addContentAssitProcessor(IDocument.DEFAULT_CONTENT_TYPE, new RubyContentAssistProcessor());
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
