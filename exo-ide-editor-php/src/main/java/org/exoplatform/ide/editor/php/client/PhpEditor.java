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
package org.exoplatform.ide.editor.php.client;

import com.google.collide.client.CollabEditor;

import org.exoplatform.ide.editor.client.api.EditorCapability;
import org.exoplatform.ide.editor.client.api.contentassist.ContentAssistProcessor;
import org.exoplatform.ide.editor.css.client.contentassist.CssAutocompleter;
import org.exoplatform.ide.editor.css.client.contentassist.CssContentAssistProcessor;
import org.exoplatform.ide.editor.html.client.contentassist.HtmlAutocompleter;
import org.exoplatform.ide.editor.html.client.contentassist.HtmlContentAssistProcessor;
import org.exoplatform.ide.editor.javascript.client.codemirror.JavaScriptAutocompleter;
import org.exoplatform.ide.editor.javascript.client.contentassist.JavaScriptContentAssistProcessor;
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
        editorBundle.getAutocompleter().addLanguageSpecificAutocompleter(htmlAutocompleter);

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
