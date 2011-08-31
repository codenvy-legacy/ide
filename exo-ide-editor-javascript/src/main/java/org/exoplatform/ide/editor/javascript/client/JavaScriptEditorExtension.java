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
package org.exoplatform.ide.editor.javascript.client;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.codemirror.CodeMirrorProducer;
import org.exoplatform.ide.editor.javascript.client.codeassistant.JavaScriptCodeAssistant;
import org.exoplatform.ide.editor.javascript.client.codemirror.JavaScriptAutocompleteHelper;
import org.exoplatform.ide.editor.javascript.client.codemirror.JavaScriptOutlineItemCreator;
import org.exoplatform.ide.editor.javascript.client.codemirror.JavaScriptParser;

import com.google.gwt.core.client.GWT;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class JavaScriptEditorExtension extends Extension
{

   
   public static final JavaScriptMessages MESSAGES = GWT.create(JavaScriptMessages.class);
   
   public static final JavaScriptClientBundle RESOURCES = GWT.create(JavaScriptClientBundle.class);
   
   /**
    * @see org.exoplatform.ide.client.framework.module.Extension#initialize()
    */
   @Override
   public void initialize()
   {
      RESOURCES.css().ensureInjected();
      
      IDE.getInstance().addControl(new NewItemControl(
            "File/New/New Java Script",
            MESSAGES.controlNewJavascriptTitle(),
            MESSAGES.controlNewJavascriptPrompt(),
            Images.JAVA_SCRIPT,
            MimeType.APPLICATION_JAVASCRIPT).setGroup(1));

      JavaScriptCodeAssistant javaScriptCodeAssistant = new JavaScriptCodeAssistant();
      IDE.getInstance().addEditor(new CodeMirrorProducer(MimeType.APPLICATION_JAVASCRIPT,
         MESSAGES.javaScriptEditor(), "js", RESOURCES.javaScript(), true,
         new CodeMirrorConfiguration().
            setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']").
            setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']").
            setParser(new JavaScriptParser()).
            setCanBeOutlined(true).
            setAutocompleteHelper(new JavaScriptAutocompleteHelper()).
            setCodeAssistant(javaScriptCodeAssistant)            
      ));

      IDE.getInstance().addEditor(new CodeMirrorProducer(MimeType.TEXT_JAVASCRIPT, MESSAGES.javaScriptEditor(),
         "js",RESOURCES.javaScript(), true, 
         new CodeMirrorConfiguration().
            setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']").
            setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']").
            setParser(new JavaScriptParser()).
            setCanBeOutlined(true).
            setAutocompleteHelper(new JavaScriptAutocompleteHelper()).
            setCodeAssistant(javaScriptCodeAssistant)            
      ));

      IDE.getInstance().addEditor(new CodeMirrorProducer(MimeType.APPLICATION_X_JAVASCRIPT,
         MESSAGES.javaScriptEditor(), "js", RESOURCES.javaScript(), true,
         new CodeMirrorConfiguration().
            setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']").
            setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']").
            setParser(new JavaScriptParser()).
            setCanBeOutlined(true).
            setAutocompleteHelper(new JavaScriptAutocompleteHelper()).
            setCodeAssistant(javaScriptCodeAssistant)            
      ));
      
      JavaScriptOutlineItemCreator javaScriptOutlineItemCreator = new JavaScriptOutlineItemCreator();
      IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_JAVASCRIPT, javaScriptOutlineItemCreator);
      IDE.getInstance().addOutlineItemCreator(MimeType.TEXT_JAVASCRIPT, javaScriptOutlineItemCreator);
      IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_X_JAVASCRIPT, javaScriptOutlineItemCreator);
   }

}
