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

import com.google.collide.client.util.Elements;

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierEvent;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.api.Editor;
import org.exoplatform.ide.editor.javascript.client.codemirror.JavaScriptOutlineItemCreator;

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

      IDE.getInstance().addControl(
         new NewItemControl("File/New/New Java Script", MESSAGES.controlNewJavascriptTitle(), MESSAGES
            .controlNewJavascriptPrompt(), Images.JAVA_SCRIPT, MimeType.APPLICATION_JAVASCRIPT).setGroupName(GroupNames.NEW_FILE));

//      final JavaScriptCodeAssistant javaScriptCodeAssistant = new JavaScriptCodeAssistant();

      IDE.getInstance().getFileTypeRegistry().addFileType(
         new FileType(MimeType.APPLICATION_JAVASCRIPT, "js", RESOURCES.javaScript()),
         new EditorCreator()
         {
            @Override
            public Editor createEditor()
            {
//               return new CodeMirror(MimeType.APPLICATION_JAVASCRIPT, new CodeMirrorConfiguration()
//               .setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']")
//               .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']")
//               .setParser(new JavaScriptParser())
//               .setCanBeOutlined(true)
//               .setAutocompleteHelper(new JavaScriptAutocompleteHelper())
//               .setCodeAssistant(javaScriptCodeAssistant));
               return new JavaScriptEditor(MimeType.APPLICATION_JAVASCRIPT);
            }
         });

//      IDE.getInstance().addEditor(new CodeMirror(MimeType.APPLICATION_JAVASCRIPT, MESSAGES.javaScriptEditor(), "js",
//         new CodeMirrorConfiguration()
//            .setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']")
//            .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']")
//            .setParser(new JavaScriptParser())
//            .setCanBeOutlined(true)
//            .setAutocompleteHelper(new JavaScriptAutocompleteHelper())
//            .setCodeAssistant(javaScriptCodeAssistant)));

      IDE.getInstance().getFileTypeRegistry().addFileType(
         new FileType(MimeType.TEXT_JAVASCRIPT, "js", RESOURCES.javaScript()),
         new EditorCreator()
         {
            @Override
            public Editor createEditor()
            {
//               return new CodeMirror(MimeType.TEXT_JAVASCRIPT, new CodeMirrorConfiguration()
//               .setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']")
//               .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']")
//               .setParser(new JavaScriptParser())
//               .setCanBeOutlined(true)
//               .setAutocompleteHelper(new JavaScriptAutocompleteHelper())
//               .setCodeAssistant(javaScriptCodeAssistant));
               return new JavaScriptEditor(MimeType.TEXT_JAVASCRIPT);
            }
         });

//      IDE.getInstance().addEditor(new CodeMirror(MimeType.TEXT_JAVASCRIPT, MESSAGES.javaScriptEditor(), "js",
//         new CodeMirrorConfiguration()
//            .setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']")
//            .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']")
//            .setParser(new JavaScriptParser())
//            .setCanBeOutlined(true)
//            .setAutocompleteHelper(new JavaScriptAutocompleteHelper())
//            .setCodeAssistant(javaScriptCodeAssistant)));

      IDE.getInstance().getFileTypeRegistry().addFileType(
         new FileType(MimeType.APPLICATION_X_JAVASCRIPT, "js", RESOURCES.javaScript()),
         new EditorCreator()
         {
            @Override
            public Editor createEditor()
            {
//               return new CodeMirror(MimeType.APPLICATION_X_JAVASCRIPT, new CodeMirrorConfiguration()
//               .setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']")
//               .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']")
//               .setParser(new JavaScriptParser())
//               .setCanBeOutlined(true)
//               .setAutocompleteHelper(new JavaScriptAutocompleteHelper())
//               .setCodeAssistant(javaScriptCodeAssistant));
               return new JavaScriptEditor(MimeType.APPLICATION_X_JAVASCRIPT);
            }
         });

//      IDE.getInstance().addEditor(new CodeMirror(MimeType.APPLICATION_X_JAVASCRIPT, MESSAGES.javaScriptEditor(), "js",
//         new CodeMirrorConfiguration()
//            .setGenericParsers("['tokenizejavascript.js', 'parsejavascript.js']")
//            .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/jscolors.css']")
//            .setParser(new JavaScriptParser())
//            .setCanBeOutlined(true)
//            .setAutocompleteHelper(new JavaScriptAutocompleteHelper())
//            .setCodeAssistant(javaScriptCodeAssistant)));

      JavaScriptOutlineItemCreator javaScriptOutlineItemCreator = new JavaScriptOutlineItemCreator();
      IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_JAVASCRIPT, javaScriptOutlineItemCreator);
      IDE.getInstance().addOutlineItemCreator(MimeType.TEXT_JAVASCRIPT, javaScriptOutlineItemCreator);
      IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_X_JAVASCRIPT, javaScriptOutlineItemCreator);

      JavaScriptCommentsModifier commentsModifier = new JavaScriptCommentsModifier();
      IDE.fireEvent(new AddCommentsModifierEvent(MimeType.APPLICATION_JAVASCRIPT, commentsModifier));
      IDE.fireEvent(new AddCommentsModifierEvent(MimeType.TEXT_JAVASCRIPT, commentsModifier));
      IDE.fireEvent(new AddCommentsModifierEvent(MimeType.APPLICATION_X_JAVASCRIPT, commentsModifier));
      Elements.injectJs(RESOURCES.esprima().getText() + RESOURCES.esprimaJsContentAssist().getText());
   }
}
