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
package org.exoplatform.ide.client.editor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.Images;
import org.exoplatform.ide.client.framework.editor.EditorNotFoundException;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.editor.ckeditor.CKEditorConfiguration;
import org.exoplatform.ide.editor.ckeditor.CKEditorProducer;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantFactory;
import org.exoplatform.ide.editor.codemirror.CodeMirrorClientBundle;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.codemirror.autocomplete.HtmlAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.JavaScriptAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.parser.CssParser;
import org.exoplatform.ide.editor.codemirror.parser.HtmlParser;
import org.exoplatform.ide.editor.codemirror.parser.JavaScriptParser;
import org.exoplatform.ide.editor.codemirror.parser.XmlParser;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorFactory Feb 22, 2011 11:06:05 AM evgen $
 *
 */
public class EditorFactory
{
   private static Map<String, List<EditorProducer>> editors = new HashMap<String, List<EditorProducer>>();

   static
   {

      addEditor(new CodeMirrorProducer(MimeType.TEXT_PLAIN, IDE.EDITOR_CONSTANT.codeMirrorTextEditor(), "txt",
         Images.FileTypes.TXT, true, new CodeMirrorConfiguration().setGenericParsers("['parsexml.js', 'parsecss.js']")
            .setGenericStyles( // generic code parsers
               "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']" // code styles
            )));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_XML, IDE.EDITOR_CONSTANT.codeMirrorXmlEditor(), "xml",
         Images.FileTypes.XML, true, new CodeMirrorConfiguration().setGenericParsers("['parsexml.js', 'tokenize.js']")
            .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']").setCanBeOutlined(true)
            .setParser(new XmlParser()).setCodeAssistant(CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_XML))));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_XML, IDE.EDITOR_CONSTANT.codeMirrorXmlEditor(), "xml",
         Images.FileTypes.XML, true, new CodeMirrorConfiguration().setGenericParsers("['parsexml.js', 'tokenize.js']")
         .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']").setCanBeOutlined(true)
         .setParser(new XmlParser()).setCodeAssistant(CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_XML))));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_JAVASCRIPT,
         IDE.EDITOR_CONSTANT.codeMirrorJavascriptEditor(), "js", Images.FileTypes.JAVASCRIPT, true,
         new CodeMirrorConfiguration().
         setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']").
         setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']").
         setParser(new JavaScriptParser()).
         setCanBeOutlined(true).
         setAutocompleteHelper(new JavaScriptAutocompleteHelper()).
         setCodeAssistant(CodeAssistantFactory.getCodeAssistant(MimeType.APPLICATION_JAVASCRIPT))));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_JAVASCRIPT, IDE.EDITOR_CONSTANT.codeMirrorJavascriptEditor(),
         "js", Images.FileTypes.JAVASCRIPT, true, new CodeMirrorConfiguration().
         setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']").
         setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']").
         setParser(new JavaScriptParser()).
         setCanBeOutlined(true).
         setAutocompleteHelper(new JavaScriptAutocompleteHelper()).
         setCodeAssistant(CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_JAVASCRIPT))));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_X_JAVASCRIPT,
         IDE.EDITOR_CONSTANT.codeMirrorJavascriptEditor(), "js", Images.FileTypes.JAVASCRIPT, true,
         new CodeMirrorConfiguration().
         setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']").
         setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']").
         setParser(new JavaScriptParser()).
         setCanBeOutlined(true).
         setAutocompleteHelper(new JavaScriptAutocompleteHelper()).
         setCodeAssistant(CodeAssistantFactory.getCodeAssistant(MimeType.APPLICATION_X_JAVASCRIPT))));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_CSS, IDE.EDITOR_CONSTANT.codeMirrorCssEditor(), "css",
         Images.FileTypes.CSS, true,  new CodeMirrorConfiguration().
         setGenericParsers("['parsecss.js']").
         setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/csscolors.css']").
         setParser(new CssParser()).
         setCodeAssistant(CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_CSS))));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_HTML, IDE.EDITOR_CONSTANT.codeMirrorHtmlEditor(), "html",
         Images.FileTypes.HTML, true,  new CodeMirrorConfiguration().
         setGenericParsers("['parsexml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']").
         setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH
            + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']").
         setParser(new HtmlParser()).
         setCanBeOutlined(true).
         setAutocompleteHelper(new HtmlAutocompleteHelper()).
         setCodeAssistant(CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_HTML)).
         setCanHaveSeveralMimeTypes(true)));

      addEditor(new CKEditorProducer(MimeType.TEXT_HTML, IDE.EDITOR_CONSTANT.ckEditorHtmlEditor(), "html",
         Images.FileTypes.HTML, false, new CKEditorConfiguration()));

      //To initialize client bundle 
      CodeAssistantClientBundle.INSTANCE.css().ensureInjected();
      CodeMirrorClientBundle.INSTANCE.css().ensureInjected();
   }

   public static void addEditor(EditorProducer producer)
   {
      if (editors.containsKey(producer.getMimeType()))
         editors.get(producer.getMimeType()).add(producer);
      else
      {
         ArrayList<EditorProducer> producers = new ArrayList<EditorProducer>();
         producers.add(producer);
         editors.put(producer.getMimeType(), producers);
      }
      IDEMimeTypes.addExtension(producer.getDefaultFileExtension(), producer.getMimeType());
      ImageUtil.putIcon(producer.getMimeType(), producer.getIcon());
   }

   public static EditorProducer getEditorProducer(String mimeType, String description) throws EditorNotFoundException
   {
      if (description == null)
      {
         return getDefaultEditor(mimeType);
      }
      if (editors.containsKey(mimeType))
      {
         for (EditorProducer p : editors.get(mimeType))
         {
            if (p.getDescription().equals(description))
               return p;
         }
      }
      throw new EditorNotFoundException();
   }

   public static EditorProducer getDefaultEditor(String mimeType) throws EditorNotFoundException
   {
      if (editors.containsKey(mimeType))
      {
         for (EditorProducer p : editors.get(mimeType))
         {
            if (p.isDefault())
               return p;
         }
      }
      throw new EditorNotFoundException();
   }

   /**
    * @param mimeType
    * @return
    */
   public static List<EditorProducer> getEditors(String mimeType) throws EditorNotFoundException
   {
      List<EditorProducer> editorProducers = editors.get(mimeType);
      if (editorProducers == null)
         throw new EditorNotFoundException();
      return editorProducers;
   }
}
