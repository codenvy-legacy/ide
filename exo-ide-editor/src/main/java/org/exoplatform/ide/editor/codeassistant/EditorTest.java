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
package org.exoplatform.ide.editor.codeassistant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.exception.ServerException;
import org.exoplatform.gwtframework.commons.loader.EmptyLoader;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.api.EditorParameters;
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.editor.api.codeassitant.ui.TokenWidgetFactory;
import org.exoplatform.ide.editor.api.event.EditorContentChangedEvent;
import org.exoplatform.ide.editor.api.event.EditorContentChangedHandler;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityEvent;
import org.exoplatform.ide.editor.api.event.EditorCursorActivityHandler;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedEvent;
import org.exoplatform.ide.editor.api.event.EditorFocusReceivedHandler;
import org.exoplatform.ide.editor.api.event.EditorHotKeyCalledEvent;
import org.exoplatform.ide.editor.api.event.EditorHotKeyCalledHandler;
import org.exoplatform.ide.editor.api.event.EditorInitializedEvent;
import org.exoplatform.ide.editor.api.event.EditorInitializedHandler;
import org.exoplatform.ide.editor.ckeditor.CKEditorConfiguration;
import org.exoplatform.ide.editor.ckeditor.CKEditorProducer;
import org.exoplatform.ide.editor.codeassistant.groovy.GroovyCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.groovy.service.GroovyCodeAssistantService;
import org.exoplatform.ide.editor.codeassistant.groovytemplate.GroovyTemplateCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.html.HtmlCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.codeassistant.java.JavaTokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.java.service.CodeAssistantService;
import org.exoplatform.ide.editor.codeassistant.java.service.JavaCodeAssistantService;
import org.exoplatform.ide.editor.codeassistant.jsp.JspCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.netvibes.NetvibesCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.ruby.RubyCodeAssistant;
import org.exoplatform.ide.editor.codemirror.CodeMirrorClientBundle;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.codemirror.autocomplete.DefaultAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.GroovyAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.GroovyTemplateAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.HtmlAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.JavaAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.JavaScriptAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.JspAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.RubyAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.parser.CssParser;
import org.exoplatform.ide.editor.codemirror.parser.GoogleGadgetParser;
import org.exoplatform.ide.editor.codemirror.parser.GroovyParser;
import org.exoplatform.ide.editor.codemirror.parser.GroovyTemplateParser;
import org.exoplatform.ide.editor.codemirror.parser.HtmlParser;
import org.exoplatform.ide.editor.codemirror.parser.JavaParser;
import org.exoplatform.ide.editor.codemirror.parser.JavaScriptParser;
import org.exoplatform.ide.editor.codemirror.parser.JspParser;
import org.exoplatform.ide.editor.codemirror.parser.RubyParser;
import org.exoplatform.ide.editor.codemirror.parser.XmlParser;
import org.exoplatform.ide.editor.codevalidator.GroovyCodeValidator;
import org.exoplatform.ide.editor.codevalidator.GroovyTemplateCodeValidator;
import org.exoplatform.ide.editor.codevalidator.JavaCodeValidator;
import org.exoplatform.ide.editor.codevalidator.JspCodeValidator;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.SimplePanel;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: EditorTest Feb 24, 2011 10:03:57 AM evgen $
 *
 */
public class EditorTest implements EntryPoint, JavaCodeAssistantErrorHandler
{

   private static final HandlerManager eventBus = new HandlerManager(null);

   private static Map<String, EditorProducer> codeEditors = new HashMap<String, EditorProducer>();

   private static Map<String, EditorProducer> WYSIWYGEditors = new HashMap<String, EditorProducer>();

   Editor editor;

   static
   {

      // verify events
      eventBus.addHandler(EditorHotKeyCalledEvent.TYPE, new EditorHotKeyCalledHandler()
      {
         public void onEditorHotKeyCalled(EditorHotKeyCalledEvent event)
         {
            System.out.println(">>>>>>>>>>> onEditorHotKeyCalled = " + event.getHotKey());
         }
      });

      eventBus.addHandler(EditorCursorActivityEvent.TYPE, new EditorCursorActivityHandler()
      {
         public void onEditorCursorActivity(EditorCursorActivityEvent event)
         {
            System.out.println(">>>>>>>>>>> onEditorCursorActivity = " + event.getEditorId());
         }
      });

      eventBus.addHandler(EditorInitializedEvent.TYPE, new EditorInitializedHandler()
      {
         public void onEditorInitialized(EditorInitializedEvent event)
         {
            System.out.println(">>>>>>>>>>> onEditorInitialized = " + event.getEditorId());
         }
      });

      eventBus.addHandler(EditorFocusReceivedEvent.TYPE, new EditorFocusReceivedHandler()
      {
         public void onEditorFocusReceived(EditorFocusReceivedEvent event)
         {
            System.out.println(">>>>>>>>>>> onEditorFocusReceived = " + event.getEditorId());
         }
      });

      eventBus.addHandler(EditorContentChangedEvent.TYPE, new EditorContentChangedHandler()
      {
         public void onEditorContentChanged(EditorContentChangedEvent event)
         {
            System.out.println(">>>>>>>>>>> onEditorContentChanged = " + event.getEditorId());
         }
      });

      GroovyCodeAssistant groovyCodeAssistant =
         new GroovyCodeAssistant(new GroovyCodeAssistantService(eventBus, "", new EmptyLoader()),
            new JavaTokenWidgetFactory("http://127.0.0.1:8888/rest/private" + "/ide/code-assistant/class-doc?fqn="),
            new JavaCodeAssistantErrorHandler()
            {

               @Override
               public void handleError(Throwable exception)
               {
                  if (exception instanceof ServerException)
                  {
                     ServerException s = (ServerException)exception;
                     System.out.println(s.getMessage());
                  }
                  else
                     exception.printStackTrace();
               }
            });
      groovyCodeAssistant.setactiveFileHref("http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/1.txt");

      JavaCodeAssistant javaCodeAssistant =
         new JavaCodeAssistant(new JavaCodeAssistantService(eventBus, "", new EmptyLoader()),
            new JavaTokenWidgetFactory(""), new JavaCodeAssistantErrorHandler()
            {

               @Override
               public void handleError(Throwable exception)
               {

               }
            });
      javaCodeAssistant.setactiveFileHref("http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/1.txt");

      GroovyTemplateCodeAssistant templateCodeAssistant =
         new GroovyTemplateCodeAssistant(new GroovyCodeAssistantService(eventBus, "", new EmptyLoader()),
            new JavaTokenWidgetFactory("http://127.0.0.1:8888/rest/private"), new JavaCodeAssistantErrorHandler()
            {

               @Override
               public void handleError(Throwable exception)
               {
                  if (exception instanceof ServerException)
                  {
                     ServerException s = (ServerException)exception;
                     System.out.println(s.getMessage());
                  }
                  else
                     exception.printStackTrace();
               }
            });
      templateCodeAssistant.setactiveFileHref("http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/1.txt");

      JspCodeAssistant jspCodeAssistant =
         new JspCodeAssistant(new JavaCodeAssistantService(eventBus, "", new EmptyLoader()),
            new JavaTokenWidgetFactory("http://127.0.0.1:8888/rest/private"), new JavaCodeAssistantErrorHandler()
            {

               @Override
               public void handleError(Throwable exception)
               {
                  if (exception instanceof ServerException)
                  {
                     ServerException s = (ServerException)exception;
                     System.out.println(s.getMessage());
                  }
                  else
                     exception.printStackTrace();
               }
            });
      jspCodeAssistant.setactiveFileHref("http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/1.txt");

      addEditor(new CodeMirrorProducer(MimeType.TEXT_PLAIN, "CodeMirror text editor", "txt", "", true,
         new CodeMirrorConfiguration("['parsexml.js', 'parsecss.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']" // code styles
         )));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_XML, "CodeMirror XML editor", "xml", "", true,
         new CodeMirrorConfiguration("['parsexml.js', 'tokenize.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new XmlParser(), // exoplatform code parser
            CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_XML))));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_XML, "CodeMirror XML editor", "xml", "", true,
         new CodeMirrorConfiguration("['parsexml.js', 'tokenize.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new XmlParser(), // exoplatform code parser
            CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_XML))));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_JAVASCRIPT, "CodeMirror JavaScript editor", "js", "", true,
         new CodeMirrorConfiguration("['tokenizejavascript.js', 'parsejavascript.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new JavaScriptParser(), // exoplatform code parser
            new JavaScriptAutocompleteHelper(),// autocomplete helper
            CodeAssistantFactory.getCodeAssistant(MimeType.APPLICATION_JAVASCRIPT))));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_JAVASCRIPT, "CodeMirror JavaScript editor", "js", "", true,
         new CodeMirrorConfiguration("['tokenizejavascript.js', 'parsejavascript.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new JavaScriptParser(), // exoplatform code parser
            new JavaScriptAutocompleteHelper(), // autocomplete helper
            CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_JAVASCRIPT))));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_X_JAVASCRIPT, "CodeMirror JavaScript editor", "js", "",
         true, new CodeMirrorConfiguration("['tokenizejavascript.js', 'parsejavascript.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new JavaScriptParser(), // exoplatform code parser
            new JavaScriptAutocompleteHelper(), // autocomplete helper
            CodeAssistantFactory.getCodeAssistant(MimeType.APPLICATION_X_JAVASCRIPT))));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_CSS, "CodeMirror Css editor", "css", "", true,
         new CodeMirrorConfiguration("['parsecss.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/csscolors.css']", // code styles
            false, // can be outlined
            true, // can be autocompleted
            new CssParser() // exoplatform code parser 
            , CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_CSS))));

      //      Set<String> comTypes = new HashSet<String>();
      //      comTypes.add(MimeType.TEXT_HTML);

      addEditor(new CodeMirrorProducer(MimeType.TEXT_HTML, "CodeMirror HTML editor", "html", "", true,
         new CodeMirrorConfiguration(
            "['parsexml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH
               + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new HtmlParser(), // exoplatform code parser
            new HtmlAutocompleteHelper(), // autocomplete helper
            CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_HTML), true)));

      addEditor(new CodeMirrorProducer(MimeType.GOOGLE_GADGET, "CodeMirror Google Gadget editor", "xml", "", true,
         new CodeMirrorConfiguration(
            "['parsegadgetxml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH
               + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new GoogleGadgetParser(), // exoplatform code parser 
            new HtmlAutocompleteHelper(), // autocomplete helper
            new HtmlCodeAssistant(), true)));

      addEditor(new CodeMirrorProducer(MimeType.UWA_WIDGET, "CodeMirror Netvibes editor", "xml", "", true,
         new CodeMirrorConfiguration(
            "['parsegadgetxml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH
               + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new HtmlParser(), // exoplatform code parser 
            new HtmlAutocompleteHelper(), // autocomplete helper
            new NetvibesCodeAssistant(), true)));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_GROOVY, "CodeMirror POJO file editor", "groovy", "", true,
         new CodeMirrorConfiguration("['parsegroovy.js', 'tokenizegroovy.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/groovycolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new GroovyParser(), // exoplatform code parser 
            new GroovyAutocompleteHelper(), // autocomplete helper
            true, // can be validated
            new GroovyCodeValidator(), groovyCodeAssistant)));

      addEditor(new CodeMirrorProducer(MimeType.GROOVY_SERVICE, "CodeMirror REST Service editor", "grs", "", true,
         new CodeMirrorConfiguration("['parsegroovy.js', 'tokenizegroovy.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/groovycolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new GroovyParser(), // exoplatform code parser 
            new GroovyAutocompleteHelper(), // autocomplete helper
            true, // can be validated
            new GroovyCodeValidator(), groovyCodeAssistant)));

      addEditor(new CodeMirrorProducer(MimeType.CHROMATTIC_DATA_OBJECT, "CodeMirror Data Object editor", "groovy", "",
         true, new CodeMirrorConfiguration("['parsegroovy.js', 'tokenizegroovy.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/groovycolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new GroovyParser(), // exoplatform code parser 
            new GroovyAutocompleteHelper(), // autocomplete helper
            true, // can be validated
            new GroovyCodeValidator(), groovyCodeAssistant)));

      addEditor(new CodeMirrorProducer(
         MimeType.GROOVY_TEMPLATE,
         "CodeMirror Groovy Template editor",
         "gtmpl",
         "",
         true,
         new CodeMirrorConfiguration(
            "['parsegtmpl.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'tokenizegroovy.js', 'parsegroovy.js', 'parsegtmplmixed.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/gtmplcolors.css', '" + CodeMirrorConfiguration.PATH
               + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css', '"
               + CodeMirrorConfiguration.PATH + "css/groovycolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new GroovyTemplateParser(), // exoplatform code parser 
            new GroovyTemplateAutocompleteHelper(), // autocomplete helper
            true, // can be validated
            new GroovyTemplateCodeValidator(), templateCodeAssistant, true)));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_JAVA, "CodeMirror Java file editor", "java", "", true,
         new CodeMirrorConfiguration("['parsejava.js', 'tokenizejava.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/javacolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new JavaParser(), // exoplatform code parser 
            new JavaAutocompleteHelper(), // autocomplete helper
            true, // can be validated
            new JavaCodeValidator(), javaCodeAssistant)));

      addEditor(new CodeMirrorProducer(
         MimeType.APPLICATION_JSP,
         "CodeMirror JSP file editor",
         "jsp",
         "",
         true,
         new CodeMirrorConfiguration(
            "['parsejsp.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'tokenizejava.js', 'parsejava.js', 'parsejspmixed.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/jspcolors.css', '" + CodeMirrorConfiguration.PATH
               + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css', '"
               + CodeMirrorConfiguration.PATH + "css/javacolors.css']", // code styles               
            true, // can be outlined
            true, // can be autocompleted
            new JspParser(), // exoplatform code parser 
            new JspAutocompleteHelper(), // autocomplete helper
            true, // can be validated
            new JspCodeValidator(), jspCodeAssistant, true // can have several mimetypes
         )));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_RUBY, "CodeMirror Ruby file editor", "rb", "", true,
         new CodeMirrorConfiguration("['parseruby.js', 'tokenizeruby.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/rubycolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new RubyParser(), // exoplatform code parser
            new RubyAutocompleteHelper(), // autocomplete helper
            new RubyCodeAssistant())));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_PHP, "CodeMirror php-script editor", "php", "", true,
         new CodeMirrorConfiguration("['parsexml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'tokenizephp.js', 'parsephp.js', 'parsephphtmlmixed.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH +  "css/csscolors.css', '" + CodeMirrorConfiguration.PATH +  "css/phpcolors.css']" // code styles
         )));   

      addEditor(new CodeMirrorProducer(MimeType.DIFF, "CodeMirror diff editor", "diff", "", true,
         new CodeMirrorConfiguration("['parsediff.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/diffcolors.css']" // code styles
         )));
      
      
      // ckeditor
      addEditor(new CKEditorProducer(MimeType.TEXT_HTML, "CKEditor HTML editor", "html", "", false,
         new CKEditorConfiguration()));

      addEditor(new CKEditorProducer(MimeType.GOOGLE_GADGET, "CKEditor Google Gadget editor", "xml", "", true,
         new CKEditorConfiguration()));

      //To initialize client bundles 
      CodeAssistantClientBundle.INSTANCE.css().ensureInjected();
      CodeMirrorClientBundle.INSTANCE.css().ensureInjected();
   }

   /**
    * @param codeMirrorProducer
    */
   private static void addEditor(CodeMirrorProducer codeMirrorProducer)
   {
      codeEditors.put(codeMirrorProducer.getMimeType(), codeMirrorProducer);
   }

   /**
    * @param CKEditorProducer
    */
   private static void addEditor(CKEditorProducer CKEditorProducer)
   {
      WYSIWYGEditors.put(CKEditorProducer.getMimeType(), CKEditorProducer);
   }

   /**
    * @see com.google.gwt.core.client.EntryPoint#onModuleLoad()
    */
   @Override
   public void onModuleLoad()
   {
      new JavaCodeAssistantService(eventBus, "http://127.0.0.1:8888/rest/private", new EmptyLoader());
      FlowPanel toolbar = new FlowPanel();
      toolbar.setWidth("100%");
      toolbar.setHeight("25px");

      final SimplePanel panel = new SimplePanel();
      panel.setStyleName("");
      panel.setWidth("100%");
      panel.setHeight("100%");

      final HashMap<String, Object> params = new HashMap<String, Object>();

      params.put(EditorParameters.IS_READ_ONLY, false);
      params.put(EditorParameters.IS_SHOW_LINE_NUMER, true);
      params.put(EditorParameters.HOT_KEY_LIST, new ArrayList<String>());

      CodeAssistantClientBundle.INSTANCE.css().ensureInjected();

      Button cssButton = new Button();
      cssButton.setTitle("Create CodeMorror Editor for Css");
      cssButton.setText("CSS");
      cssButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.TEXT_CSS);

            editor = codeEditors.get(MimeType.TEXT_CSS).createEditor(".test-class{\n\n}", eventBus, params);
            panel.clear();
            panel.add(editor);

         }
      });

      Button htmlButton = new Button();
      htmlButton.setTitle("Create HTML CodeMirror Editor");
      htmlButton.setText("HTML");
      htmlButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.TEXT_HTML);

            editor =
               codeEditors.get(MimeType.TEXT_HTML).createEditor(ExamplesBundle.INSTANCE.htmlExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button jsButton = new Button();
      jsButton.setTitle("Create JavaScript CodeMirror Editor");
      jsButton.setText("JS");
      jsButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.APPLICATION_JAVASCRIPT);

            editor =
               codeEditors.get(MimeType.APPLICATION_JAVASCRIPT).createEditor(
                  ExamplesBundle.INSTANCE.jsExample().getText(), eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button xmlButton = new Button();
      xmlButton.setTitle("Create XML CodeMirror Editor");
      xmlButton.setText("XML");
      xmlButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.TEXT_XML);

            editor =
               codeEditors.get(MimeType.TEXT_XML).createEditor(ExamplesBundle.INSTANCE.xmlExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button googleGadgetButton = new Button();
      googleGadgetButton.setTitle("Create Google Gadget CodeMirror Editor");
      googleGadgetButton.setText("Gadget");
      googleGadgetButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.GOOGLE_GADGET);

            editor =
               codeEditors.get(MimeType.GOOGLE_GADGET).createEditor(
                  ExamplesBundle.INSTANCE.googleGadgetExample().getText(), eventBus, params);

            editor.setHotKeyList(new ArrayList<String>()
            {
               {
                  add("Ctrl+70"); // Ctrl+F
                  add("Ctrl+68"); // Ctrl+D
                  add("Ctrl+83"); // Ctrl+S
                  add("Alt+70"); // Alt+F    
                  add("Ctrl+78"); // Ctrl+N               
               }
            });

            panel.clear();
            panel.add(editor);
         }
      });

      Button netvibesButton = new Button();
      netvibesButton.setTitle("Create Netvibes Widget CodeMirror Editor");
      netvibesButton.setText("Netvibes");
      netvibesButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.UWA_WIDGET);

            editor =
               codeEditors.get(MimeType.UWA_WIDGET).createEditor(ExamplesBundle.INSTANCE.netvibesExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button groovyButton = new Button();
      groovyButton.setTitle("Create POJO file CodeMirror Editor");
      groovyButton.setText("POJO");
      groovyButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.APPLICATION_GROOVY);

            editor =
               codeEditors.get(MimeType.APPLICATION_GROOVY).createEditor(
                  ExamplesBundle.INSTANCE.groovyExample().getText(), eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button groovyServiceButton = new Button();
      groovyServiceButton.setTitle("Create Groovy Service CodeMirror Editor");
      groovyServiceButton.setText("GroovyService");
      groovyServiceButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.GROOVY_SERVICE);

            editor =
               codeEditors.get(MimeType.GROOVY_SERVICE).createEditor(
                  ExamplesBundle.INSTANCE.groovyServiceExample().getText(), eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button dataObjectButton = new Button();
      dataObjectButton.setTitle("Create Data Object CodeMirror Editor");
      dataObjectButton.setText("DataObject");
      dataObjectButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.CHROMATTIC_DATA_OBJECT);

            editor =
               codeEditors.get(MimeType.CHROMATTIC_DATA_OBJECT).createEditor(
                  ExamplesBundle.INSTANCE.dataObjectExample().getText(), eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button groovyTemplateButton = new Button();
      groovyTemplateButton.setTitle("Create Groovy Template CodeMirror Editor");
      groovyTemplateButton.setText("Template");
      groovyTemplateButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.GROOVY_TEMPLATE);

            editor =
               codeEditors.get(MimeType.GROOVY_TEMPLATE).createEditor(
                  ExamplesBundle.INSTANCE.groovyTemplateExample().getText(), eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button javaButton = new Button();
      javaButton.setTitle("Create JavaCodeMirror Editor");
      javaButton.setText("Java");
      javaButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.APPLICATION_JAVA);

            editor =
               codeEditors.get(MimeType.APPLICATION_JAVA).createEditor(ExamplesBundle.INSTANCE.javaExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button htmlCKEditorButton = new Button();
      htmlCKEditorButton.setTitle("Create HTML CKEditor");
      htmlCKEditorButton.setText("CKEditor HTML");
      htmlCKEditorButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.TEXT_HTML);

            editor =
               WYSIWYGEditors.get(MimeType.TEXT_HTML).createEditor(ExamplesBundle.INSTANCE.htmlExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button googleGadgetCKEditorButton = new Button();
      googleGadgetCKEditorButton.setTitle("Create Google Gadget CKEditor");
      googleGadgetCKEditorButton.setText("CKEditor Gadget");
      googleGadgetCKEditorButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.GOOGLE_GADGET);

            editor =
               WYSIWYGEditors.get(MimeType.GOOGLE_GADGET).createEditor(
                  ExamplesBundle.INSTANCE.googleGadgetExample().getText(), eventBus, params);

            editor.setHotKeyList(new ArrayList<String>()
            {
               {
                  //               add("Ctrl+78");  // Ctrl+N 
                  //               add("Ctrl+83"); // Ctrl+S               
                  //               add("Alt+78");    // Alt+N               
               }
            });

            panel.clear();
            panel.add(editor);
         }
      });

      Button jspButton = new Button();
      jspButton.setTitle("Create JSPCodeMirror Editor");
      jspButton.setText("JSP");
      jspButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.APPLICATION_JSP);

            editor =
               codeEditors.get(MimeType.APPLICATION_JSP).createEditor(ExamplesBundle.INSTANCE.jspExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button phpButton =
         new Button();
      phpButton.setTitle("Create CodeMirror Editor for PHP");
      phpButton.setText("PHP");
      phpButton.addClickHandler(new ClickHandler()
      {
         
         @Override
         public void onClick(ClickEvent event)
         {            
            editor = codeEditors.get(MimeType.APPLICATION_PHP).createEditor(ExamplesBundle.INSTANCE.phpExample().getText(), eventBus, params);
            panel.clear();
            panel.add(editor);
            
         }
      });
      
      Button showLineNumbersButton = new Button();
      showLineNumbersButton.setTitle("Show LineNumbers");
      showLineNumbersButton.setText("Show LineNumbers");
      showLineNumbersButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            editor.showLineNumbers(true);
         }
      });

      Button hideLineNumbersButton = new Button();
      hideLineNumbersButton.setTitle("Hide LineNumbers");
      hideLineNumbersButton.setText("Hide LineNumbers");
      hideLineNumbersButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            editor.showLineNumbers(false);
         }
      });

      Button rubyButton = new Button();
      rubyButton.setTitle("Create CodeMirror Editor for Ruby file");
      rubyButton.setText("Ruby");
      rubyButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(EditorParameters.MIME_TYPE, MimeType.APPLICATION_RUBY);

            editor =
               codeEditors.get(MimeType.APPLICATION_RUBY).createEditor(ExamplesBundle.INSTANCE.rubyExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);

         }
      });

      Button diffButton = new Button();
      diffButton.setTitle("Create CodeMirror Editor for diff");
      diffButton.setText("Diff");
      diffButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            editor = codeEditors.get(MimeType.DIFF).createEditor(ExamplesBundle.INSTANCE.diffExample().getText(), eventBus, params);
            panel.clear();
            panel.add(editor);

         }
      });
      
      
      
      toolbar.add(cssButton);
      toolbar.add(htmlButton);
      toolbar.add(jsButton);
      toolbar.add(xmlButton);
      toolbar.add(googleGadgetButton);
      toolbar.add(netvibesButton);
      toolbar.add(groovyButton);
      toolbar.add(groovyServiceButton);
      toolbar.add(dataObjectButton);
      toolbar.add(groovyTemplateButton);
      toolbar.add(javaButton);
      toolbar.add(jspButton);
      toolbar.add(rubyButton);
      toolbar.add(phpButton);
      toolbar.add(diffButton);      
      

      toolbar.add(htmlCKEditorButton);
      toolbar.add(googleGadgetCKEditorButton);
      toolbar.add(showLineNumbersButton);
      toolbar.add(hideLineNumbersButton);

      RootPanel.get().add(toolbar);
      RootPanel.get().add(panel);
   }

   /**
    * @see org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistantErrorHandler#handleError(java.lang.Throwable)
    */
   @Override
   public void handleError(Throwable exception)
   {
      // TODO Auto-generated method stub

   }

}
