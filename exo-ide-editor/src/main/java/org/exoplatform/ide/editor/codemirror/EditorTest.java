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
package org.exoplatform.ide.editor.codemirror;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.client.button.IconButton;
import org.exoplatform.gwtframework.ui.client.toolbar.Toolbar;
import org.exoplatform.gwtframework.ui.client.util.ImageHelper;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.editor.codemirror.autocomplete.HtmlAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.JavaScriptAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.codeassistant.css.CssCodeAssistant;
import org.exoplatform.ide.editor.codemirror.codeassistant.html.HtmlCodeAssistant;
import org.exoplatform.ide.editor.codemirror.codeassistant.javascript.JavaScriptCodeAssistant;
import org.exoplatform.ide.editor.codemirror.parser.CssParser;
import org.exoplatform.ide.editor.codemirror.parser.HtmlParser;
import org.exoplatform.ide.editor.codemirror.parser.JavaScriptParser;
import org.exoplatform.ide.editor.codemirror.parser.XmlParser;
import org.exoplatform.ide.editor.codemirror.producers.CodeMirrorProducer;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorTest Feb 24, 2011 10:03:57 AM evgen $
 *
 */
public class EditorTest implements EntryPoint
{

   private static final HandlerManager eventBus = new HandlerManager(null);

   private static Map<String, EditorProducer> editors = new HashMap<String, EditorProducer>();

   static
   {

      addEditor(new CodeMirrorProducer(MimeType.TEXT_PLAIN, "CodeMirror text editor", "txt", true,
         new CodeMirrorConfiguration("['parsexml.js', 'parsecss.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']" // code styles
         )));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_XML, "CodeMirror XML editor", "xml", true,
         new CodeMirrorConfiguration("['parsexml.js', 'tokenize.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new XmlParser() // exoplatform code parser 
         )));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_XML, "CodeMirror XML editor", "xml", true,
         new CodeMirrorConfiguration("['parsexml.js', 'tokenize.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new XmlParser() // exoplatform code parser 
         )));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_JAVASCRIPT, "CodeMirror JavaScript editor", "js", true,
         new CodeMirrorConfiguration("['tokenizejavascript.js', 'parsejavascript.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new JavaScriptParser(), // exoplatform code parser
            new JavaScriptAutocompleteHelper() ,// autocomplete helper
            new JavaScriptCodeAssistant(eventBus)
         )));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_JAVASCRIPT, "CodeMirror JavaScript editor", "js", true,
         new CodeMirrorConfiguration("['tokenizejavascript.js', 'parsejavascript.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new JavaScriptParser(), // exoplatform code parser
            new JavaScriptAutocompleteHelper(), // autocomplete helper
            new JavaScriptCodeAssistant(eventBus)
         )));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_X_JAVASCRIPT, "CodeMirror JavaScript editor", "js", true,
         new CodeMirrorConfiguration("['tokenizejavascript.js', 'parsejavascript.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new JavaScriptParser(), // exoplatform code parser
            new JavaScriptAutocompleteHelper(), // autocomplete helper
            new JavaScriptCodeAssistant(eventBus)
         )));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_CSS, "CodeMirror Css editor", "css", true,
         new CodeMirrorConfiguration("['parsecss.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/csscolors.css']", // code styles
            false, // can be outlined
            true, // can be autocompleted
            new CssParser() // exoplatform code parser 
            , new CssCodeAssistant(eventBus))));

      Set<String> comTypes = new HashSet<String>();
      comTypes.add(MimeType.TEXT_HTML);

      addEditor(new CodeMirrorProducer(MimeType.TEXT_HTML, "CodeMirror HTML editor", "html", true,
         new CodeMirrorConfiguration(
            "['parsexml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH
               + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new HtmlParser(), // exoplatform code parser
            new HtmlAutocompleteHelper(), // autocomplete helper
            new HtmlCodeAssistant(eventBus), comTypes)));
      //To initialize client bundle 
      CodeAssistantClientBundle.INSTANCE.css().ensureInjected();
   }

   /**
    * @param codeMirrorProducer
    */
   private static void addEditor(CodeMirrorProducer codeMirrorProducer)
   {
      editors.put(codeMirrorProducer.getMimeType(), codeMirrorProducer);
   }

   /**
    * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
    */
   @Override
   public void onModuleLoad()
   {
      Toolbar toolbar = new Toolbar();
      
      final SimplePanel panel  = new  SimplePanel();
      panel.setWidth("100%");
      panel.setHeight("100%");
      
      final HashMap<String, Object> params = new HashMap<String, Object>();

      params.put(CodeMirrorParams.IS_READ_ONLY, false);
      params.put(CodeMirrorParams.IS_SHOW_LINE_NUMER, true);
      params.put(CodeMirrorParams.HOT_KEY_LIST, new ArrayList<String>());
      
      CodeAssistantClientBundle.INSTANCE.css().ensureInjected();
      
      IconButton cssButton =
         new IconButton(ImageHelper.getImageHTML(CodeAssistantClientBundle.INSTANCE.attribute()),
            ImageHelper.getImageHTML(CodeAssistantClientBundle.INSTANCE.attribute()));
      cssButton.setTitle("Create CodeMorror Editor for Css");
      cssButton.setCommand(new Command()
      {

         @Override
         public void execute()
         {

            params.put(CodeMirrorParams.MIME_TYPE, MimeType.TEXT_CSS);

            Editor editor = editors.get(MimeType.TEXT_CSS).createEditor(".test-class{\n\n}", eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      IconButton htmlButton = new IconButton(ImageHelper.getImageHTML(CodeAssistantClientBundle.INSTANCE.tag()),
            ImageHelper.getImageHTML(CodeAssistantClientBundle.INSTANCE.tag()));
      htmlButton.setTitle("Create HTML CodeMirror Editor");
      htmlButton.setCommand(new Command()
      {
         
         @Override
         public void execute()
         {
            params.put(CodeMirrorParams.MIME_TYPE, MimeType.TEXT_HTML);

            Editor editor = editors.get(MimeType.TEXT_HTML).createEditor(ExamplesBuandle.INSTANCE.htmlExample().getText(), eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });
      
      IconButton jsButton = new IconButton(ImageHelper.getImageHTML(CodeAssistantClientBundle.INSTANCE.functionItem()),
         ImageHelper.getImageHTML(CodeAssistantClientBundle.INSTANCE.tag()));
      jsButton.setTitle("Create JavaScript CodeMirror Editor");
      jsButton.setCommand(new Command()
   {
      
      @Override
      public void execute()
      {
         params.put(CodeMirrorParams.MIME_TYPE, MimeType.APPLICATION_JAVASCRIPT);

         Editor editor = editors.get(MimeType.APPLICATION_JAVASCRIPT).createEditor("", eventBus, params);
         panel.clear();
         panel.add(editor);
      }
   });    
      
      toolbar.addItem(cssButton);
      toolbar.addDelimiter();
      toolbar.addItem(htmlButton);
      toolbar.addDelimiter();
      toolbar.addItem(jsButton);
      
      RootPanel.get().add(toolbar);
      RootPanel.get().add(panel);
   }

}
