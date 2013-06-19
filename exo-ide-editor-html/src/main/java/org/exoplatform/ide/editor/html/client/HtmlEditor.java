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
        CssAutocompleter cssAutocompleter = CssAutocompleter.create();
        HtmlAutocompleter htmlAutocompleter = HtmlAutocompleter.create(cssAutocompleter, new JavaScriptAutocompleter());
        editorBundle.getAutocompleter().addLanguageSpecificAutocompleter(htmlAutocompleter);
        editorBundle.getAutocompleter().addContentAssitProcessor(
                IDocument.DEFAULT_CONTENT_TYPE,
                new HtmlContentAssistProcessor(new CssContentAssistProcessor(cssAutocompleter),
                                               new JavaScriptContentAssistProcessor()));
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
