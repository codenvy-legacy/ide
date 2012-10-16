/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.editor.javascript.client;

import com.google.collide.client.CollabEditor;

import org.exoplatform.ide.editor.javascript.client.codemirror.JavaScriptAutocompleter;
import org.exoplatform.ide.editor.javascript.client.contentassist.JavaScriptContentAssistProcessor;
import org.exoplatform.ide.editor.text.IDocument;

/**
 * JavaScript editor based on {@link CollabEditor}.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: JavaScriptEditor.java Aug 27, 2012 4:21:04 PM azatsarynnyy $
 *
 */
public class JavaScriptEditor extends CollabEditor
{
   /**
    * Constructs new instance of {@link JavaScriptEditor} for a specific MIME-type.
    * 
    * @param mimeType MIME-type
    */
   public JavaScriptEditor(String mimeType)
   {
      super(mimeType);
      editorBundle.getAutocompleter().addLanguageSpecificAutocompleter(new JavaScriptAutocompleter());
      editorBundle.getAutocompleter().addContentAssitProcessor(IDocument.DEFAULT_CONTENT_TYPE,
         new JavaScriptContentAssistProcessor());
   }
}
