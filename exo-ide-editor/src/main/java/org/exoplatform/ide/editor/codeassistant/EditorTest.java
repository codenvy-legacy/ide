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
import org.exoplatform.ide.editor.api.EditorProducer;
import org.exoplatform.ide.editor.codeassistant.css.CssCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.html.HtmlCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.java.JavaCodeAssistantErrorHandler;
import org.exoplatform.ide.editor.codeassistant.java.JavaTokenWidgetFactory;
import org.exoplatform.ide.editor.codeassistant.java.service.CodeAssistantServiceImpl;
import org.exoplatform.ide.editor.codeassistant.javascript.JavaScriptCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.netvibes.NetvibesCodeAssistant;
import org.exoplatform.ide.editor.codeassistant.xml.XmlCodeAssistant;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorParams;
import org.exoplatform.ide.editor.codemirror.autocomplete.GroovyAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.GroovyTemplateAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.HtmlAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.autocomplete.JavaScriptAutocompleteHelper;
import org.exoplatform.ide.editor.codemirror.parser.CssParser;
import org.exoplatform.ide.editor.codemirror.parser.GoogleGadgetParser;
import org.exoplatform.ide.editor.codemirror.parser.GroovyParser;
import org.exoplatform.ide.editor.codemirror.parser.GroovyTemplateParser;
import org.exoplatform.ide.editor.codemirror.parser.HtmlParser;
import org.exoplatform.ide.editor.codemirror.parser.JavaScriptParser;
import org.exoplatform.ide.editor.codemirror.parser.XmlParser;
import org.exoplatform.ide.editor.codemirror.producers.CodeMirrorProducer;
import org.exoplatform.ide.editor.codevalidator.GroovyCodeValidator;
import org.exoplatform.ide.editor.codevalidator.GroovyTemplateCodeValidator;

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

   private static Map<String, EditorProducer> editors = new HashMap<String, EditorProducer>();

   static
   {
      
      JavaCodeAssistant javaCodeAssistant = new JavaCodeAssistant(new JavaTokenWidgetFactory("http://127.0.0.1:8888/rest/private"), new JavaCodeAssistantErrorHandler()
      {
         
         @Override
         public void handleError(Throwable exception)
         {
            if(exception instanceof ServerException)
            {
               ServerException s = (ServerException)exception;
               System.out.println(s.getMessage());
            }
            else exception.printStackTrace();
         }
      });
      javaCodeAssistant.setactiveFileHref("http://127.0.0.1:8888/rest/private/jcr/repository/dev-monit/1.txt");

      addEditor(new CodeMirrorProducer(MimeType.TEXT_PLAIN, "CodeMirror text editor", "txt", true,
         new CodeMirrorConfiguration("['parsexml.js', 'parsecss.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']" // code styles
         )));

      addEditor(new CodeMirrorProducer(MimeType.TEXT_XML, "CodeMirror XML editor", "xml", true,
         new CodeMirrorConfiguration("['parsexml.js', 'tokenize.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new XmlParser(), // exoplatform code parser
            CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_XML)
         )));

      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_XML, "CodeMirror XML editor", "xml", true,
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

//      Set<String> comTypes = new HashSet<String>();
//      comTypes.add(MimeType.TEXT_HTML);

      addEditor(new CodeMirrorProducer(MimeType.TEXT_HTML, "CodeMirror HTML editor", "html", true,
         new CodeMirrorConfiguration(
            "['parsexml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']", // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH
               + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new HtmlParser(), // exoplatform code parser
            new HtmlAutocompleteHelper(), // autocomplete helper
            CodeAssistantFactory.getCodeAssistant(MimeType.TEXT_HTML), true)));
      
      addEditor(new CodeMirrorProducer(MimeType.GOOGLE_GADGET, "CodeMirror Google Gadget editor", "xml", true,
         new CodeMirrorConfiguration(
               "['parsegadgetxml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']",  // generic code parsers
               "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']", // code styles
               true, // can be outlined
               true, // can be autocompleted
               new GoogleGadgetParser(), // exoplatform code parser 
               new HtmlAutocompleteHelper(), // autocomplete helper
               new HtmlCodeAssistant(), true)));
      
      
      addEditor(new CodeMirrorProducer(MimeType.UWA_WIDGET, "CodeMirror Netvibes editor", "xml", true,
         new CodeMirrorConfiguration(
            "['parsegadgetxml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']",  // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']", // code styles
            true, // can be outlined
            true, // can be autocompleted
            new HtmlParser(), // exoplatform code parser 
            new HtmlAutocompleteHelper(), // autocomplete helper
            new NetvibesCodeAssistant(), true)));
      
      addEditor(new CodeMirrorProducer(MimeType.APPLICATION_GROOVY, "CodeMirror POJO file editor", "groovy", true,
         new CodeMirrorConfiguration(
            "['parsegroovy.js', 'tokenizegroovy.js']",  // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/groovycolors.css']", // code styles
               true, // can be outlined
               true, // can be autocompleted
               new GroovyParser(), // exoplatform code parser 
               new GroovyAutocompleteHelper(), // autocomplete helper
               true, // can be validated
               new GroovyCodeValidator(),
               javaCodeAssistant
              )));

      addEditor(new CodeMirrorProducer(MimeType.GROOVY_SERVICE, "CodeMirror REST Service editor", "grs", true,
         new CodeMirrorConfiguration(
            "['parsegroovy.js', 'tokenizegroovy.js']",  // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/groovycolors.css']", // code styles
               true, // can be outlined
               true, // can be autocompleted
               new GroovyParser(), // exoplatform code parser 
               new GroovyAutocompleteHelper(), // autocomplete helper
               true, // can be validated
               new GroovyCodeValidator(),
               javaCodeAssistant)));      

      addEditor(new CodeMirrorProducer(MimeType.CHROMATTIC_DATA_OBJECT, "CodeMirror Data Object editor", "groovy", true,
         new CodeMirrorConfiguration(
            "['parsegroovy.js', 'tokenizegroovy.js']",  // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/groovycolors.css']", // code styles
               true, // can be outlined
               true, // can be autocompleted
               new GroovyParser(), // exoplatform code parser 
               new GroovyAutocompleteHelper(), // autocomplete helper
               true, // can be validated
               new GroovyCodeValidator(),
               javaCodeAssistant)));
      
      addEditor(new CodeMirrorProducer(MimeType.GROOVY_TEMPLATE, "CodeMirror Groovy Template editor", "gtmpl", true,
         new CodeMirrorConfiguration(
            "['parsegtmpl.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'tokenizegroovy.js', 'parsegroovy.js', 'parsegtmplmixed.js']",  // generic code parsers
            "['" + CodeMirrorConfiguration.PATH + "css/gtmplcolors.css', '" + CodeMirrorConfiguration.PATH + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css', '" + CodeMirrorConfiguration.PATH + "css/groovycolors.css']", // code styles
               true, // can be outlined
               true, // can be autocompleted
               new GroovyTemplateParser(), // exoplatform code parser 
               new GroovyTemplateAutocompleteHelper(), // autocomplete helper
               true, // can be validated
               new GroovyTemplateCodeValidator(),
               javaCodeAssistant, true)));     
      
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
      new CodeAssistantServiceImpl(eventBus, "http://127.0.0.1:8888/rest/private", new EmptyLoader());
      FlowPanel toolbar = new FlowPanel();
      toolbar.setWidth("100%");
      toolbar.setHeight("25px");

      final SimplePanel panel = new SimplePanel();
      panel.setStyleName("");
      panel.setWidth("100%");
      panel.setHeight("100%");

      final HashMap<String, Object> params = new HashMap<String, Object>();

      params.put(CodeMirrorParams.IS_READ_ONLY, false);
      params.put(CodeMirrorParams.IS_SHOW_LINE_NUMER, true);
      params.put(CodeMirrorParams.HOT_KEY_LIST, new ArrayList<String>());

      CodeAssistantClientBundle.INSTANCE.css().ensureInjected();

      Button cssButton =
         new Button();
      cssButton.setTitle("Create CodeMorror Editor for Css");
      cssButton.setText("CSS");
      cssButton.addClickHandler(new ClickHandler()
      {
         
         @Override
         public void onClick(ClickEvent event)
         {
            params.put(CodeMirrorParams.MIME_TYPE, MimeType.TEXT_CSS);
            
            Editor editor = editors.get(MimeType.TEXT_CSS).createEditor(".test-class{\n\n}", eventBus, params);
            panel.clear();
            panel.add(editor);
            
         }
      });
      
      Button htmlButton =
         new Button();
      htmlButton.setTitle("Create HTML CodeMirror Editor");
      htmlButton.setText("HTML");
      htmlButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(CodeMirrorParams.MIME_TYPE, MimeType.TEXT_HTML);

            Editor editor =
               editors.get(MimeType.TEXT_HTML).createEditor(ExamplesBundle.INSTANCE.htmlExample().getText(), eventBus,
                  params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button jsButton =
         new Button();
      jsButton.setTitle("Create JavaScript CodeMirror Editor");
      jsButton.setText("JS");
      jsButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(CodeMirrorParams.MIME_TYPE, MimeType.APPLICATION_JAVASCRIPT);

            Editor editor =
               editors.get(MimeType.APPLICATION_JAVASCRIPT).createEditor(
                  ExamplesBundle.INSTANCE.jsExample().getText(), eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button xmlButton =
         new Button();
      xmlButton.setTitle("Create XML CodeMirror Editor");
      xmlButton.setText("XML");
      xmlButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(CodeMirrorParams.MIME_TYPE, MimeType.TEXT_XML);

            Editor editor =
               editors.get(MimeType.TEXT_XML).createEditor(ExamplesBundle.INSTANCE.xmlExample().getText(), eventBus,
                  params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button googleGadgetButton =
         new Button();
      googleGadgetButton.setTitle("Create Google Gadget CodeMirror Editor");
      googleGadgetButton.setText("Gadget");
      googleGadgetButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(CodeMirrorParams.MIME_TYPE, MimeType.GOOGLE_GADGET);

            Editor editor =
               editors.get(MimeType.GOOGLE_GADGET).createEditor(ExamplesBundle.INSTANCE.googleGadgetExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });
      
      Button netvibesButton =
         new Button();
      netvibesButton.setTitle("Create Netvibes Widget CodeMirror Editor");
      netvibesButton.setText("Netvibes");
      netvibesButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(CodeMirrorParams.MIME_TYPE, MimeType.UWA_WIDGET);

            Editor editor =
               editors.get(MimeType.UWA_WIDGET).createEditor(ExamplesBundle.INSTANCE.netvibesExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });

      Button groovyButton =
         new Button();
      groovyButton.setTitle("Create POJO file CodeMirror Editor");
      groovyButton.setText("POJO");
      groovyButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(CodeMirrorParams.MIME_TYPE, MimeType.APPLICATION_GROOVY);

            Editor editor =
               editors.get(MimeType.APPLICATION_GROOVY).createEditor(ExamplesBundle.INSTANCE.groovyExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });
      
      Button groovyServiceButton =
         new Button();
      groovyServiceButton.setTitle("Create Groovy Service CodeMirror Editor");
      groovyServiceButton.setText("GroovyService");
      groovyServiceButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(CodeMirrorParams.MIME_TYPE, MimeType.GROOVY_SERVICE);

            Editor editor =
               editors.get(MimeType.GROOVY_SERVICE).createEditor(ExamplesBundle.INSTANCE.groovyServiceExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });
      
      Button dataObjectButton =
         new Button();
      dataObjectButton.setTitle("Create Data Object CodeMirror Editor");
      dataObjectButton.setText("DataObject");
      dataObjectButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(CodeMirrorParams.MIME_TYPE, MimeType.CHROMATTIC_DATA_OBJECT);

            Editor editor =
               editors.get(MimeType.CHROMATTIC_DATA_OBJECT).createEditor(ExamplesBundle.INSTANCE.dataObjectExample().getText(),
                  eventBus, params);
            panel.clear();
            panel.add(editor);
         }
      });
      
      Button groovyTemplateButton =
         new Button();
      groovyTemplateButton.setTitle("Create Groovy Template CodeMirror Editor");
      groovyTemplateButton.setText("Template");
      groovyTemplateButton.addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            params.put(CodeMirrorParams.MIME_TYPE, MimeType.GROOVY_TEMPLATE);

            Editor editor =
               editors.get(MimeType.GROOVY_TEMPLATE).createEditor(ExamplesBundle.INSTANCE.groovyTemplateExample().getText(),
                  eventBus, params);
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
