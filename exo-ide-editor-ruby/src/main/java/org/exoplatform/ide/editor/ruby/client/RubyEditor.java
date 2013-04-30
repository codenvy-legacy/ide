/*
 * Copyright (C) 2013 eXo Platform SAS.
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
