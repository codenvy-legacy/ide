/*
 * Copyright (C) 2011 eXo Platform SAS.
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

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.ckeditor.CKEditorConfiguration;
import org.exoplatform.ide.editor.ckeditor.CKEditorProducer;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.html.client.codeassistant.HtmlCodeAssistant;
import org.exoplatform.ide.editor.html.client.codemirror.HtmlAutocompleteHelper;
import org.exoplatform.ide.editor.html.client.codemirror.HtmlOutlineItemCreator;
import org.exoplatform.ide.editor.html.client.codemirror.HtmlParser;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class HtmlEditorExtension extends Extension
{

   public static final HtmlMessages MESSAGES = GWT.create(HtmlMessages.class);
   
   public static final HtmlClientBundle RESOURCES = GWT.create(HtmlClientBundle.class);
   
   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {      
      RESOURCES.css().ensureInjected();
      
      IDE.getInstance().addControl(new NewItemControl(
         "File/New/New HTML",
         MESSAGES.controlNewHtmlTitle(),
         MESSAGES.controlNewHtmlPrompt(),
         Images.HTML,
         MimeType.TEXT_HTML).setGroup(1));

      IDE.getInstance().addEditor(new CodeMirrorProducer(MimeType.TEXT_HTML, MESSAGES.codeMirrorHtmlEditor(), "html",
         Images.INSTANCE.html(), true, 
         new CodeMirrorConfiguration().
            setGenericParsers("['parsexml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']").
            setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH
               + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']").
            setParser(new HtmlParser()).
            setCanBeOutlined(true).
            setAutocompleteHelper(new HtmlAutocompleteHelper()).
            setCodeAssistant(new HtmlCodeAssistant()).
            setCanHaveSeveralMimeTypes(true)
      ));   

      IDE.getInstance().addEditor(new CKEditorProducer(MimeType.TEXT_HTML, MESSAGES.ckEditorHtmlEditor(), "html",
         Images.INSTANCE.html(), false, new CKEditorConfiguration()));

      
      IDE.getInstance().addOutlineItemCreator(MimeType.TEXT_HTML, new HtmlOutlineItemCreator());
   }

}
