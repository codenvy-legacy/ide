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
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantClientBundle;
import org.exoplatform.ide.editor.codeassistant.CodeAssistantFactory;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.codemirror.autocomplete.JavaScriptAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.parser.CssParser;
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

      addEditor(new CodeMirrorProducer(MimeType.TEXT_PLAIN, "CodeMirror text editor", "txt", true,
         new CodeMirrorConfiguration("['parsexml.js', 'parsecss.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']" // code styles
         )));
      
      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_XML, "CodeMirror XML editor", "xml", true,
         new CodeMirrorConfiguration("['parsexml.js', 'tokenize.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new XmlParser(), // exoplatform code parser
            CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_XML)
         )));
      
      addEditor(new CodeMirrorProducer(MimeType.TEXT_XML, "CodeMirror XML editor", "xml", true,
         new CodeMirrorConfiguration("['parsexml.js', 'tokenize.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new XmlParser(), // exoplatform code parser
            CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_XML)
         )));
      
      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_JAVASCRIPT, "CodeMirror JavaScript editor", "js", true,
         new CodeMirrorConfiguration("['tokenizejavascript.js', 'parsejavascript.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new JavaScriptParser(), // exoplatform code parser
            new JavaScriptAutocompleteHelper(),// autocomplete helper
            CodeAssistantFactory.getCodeAssistant(MimeType.APPLICATION_JAVASCRIPT))));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_JAVASCRIPT, "CodeMirror JavaScript editor", "js", true,
         new CodeMirrorConfiguration("['tokenizejavascript.js', 'parsejavascript.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new JavaScriptParser(), // exoplatform code parser
            new JavaScriptAutocompleteHelper(), // autocomplete helper
            CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_JAVASCRIPT))));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_X_JAVASCRIPT, "CodeMirror JavaScript editor", "js", true,
         new CodeMirrorConfiguration("['tokenizejavascript.js', 'parsejavascript.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new JavaScriptParser(), // exoplatform code parser
            new JavaScriptAutocompleteHelper(), // autocomplete helper
            CodeAssistantFactory.getCodeAssistant(MimeType.APPLICATION_X_JAVASCRIPT))));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_CSS, "CodeMirror Css editor", "css", true,
         new CodeMirrorConfiguration("['parsecss.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/csscolors.css']", // code styles
            false, // can be outlined
            true, // can be autocompleted
            new CssParser() // exoplatform code parser 
            , CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_CSS))));
      
            //To initialize client bundle 
            CodeAssistantClientBundle.INSTANCE.css().ensureInjected();
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
   }

   public static EditorProducer getEditorProducer(String mimeType, String description) throws EditorNotFoundException
   {
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
}
