/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.editor.javascript.client;

import com.codenvy.ide.client.util.Elements;
import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierEvent;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.javascript.client.codemirror.JavaScriptOutlineItemCreator;
import org.exoplatform.ide.editor.javascript.client.syntaxvalidator.JavaScriptCodeController;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class JavaScriptEditorExtension extends Extension {

    public static final JavaScriptMessages MESSAGES = GWT.create(JavaScriptMessages.class);

    public static final JavaScriptClientBundle RESOURCES = GWT.create(JavaScriptClientBundle.class);

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        RESOURCES.css().ensureInjected();

        IDE.getInstance().addControl(
                new NewItemControl("File/New/New Java Script", MESSAGES.controlNewJavascriptTitle(), MESSAGES
                        .controlNewJavascriptPrompt(), Images.JAVA_SCRIPT, MimeType.APPLICATION_JAVASCRIPT)
                        .setGroupName(GroupNames.NEW_FILE));

//      final JavaScriptCodeAssistant javaScriptCodeAssistant = new JavaScriptCodeAssistant();

        IDE.getInstance().getFileTypeRegistry().addFileType(
                new FileType(MimeType.APPLICATION_JAVASCRIPT, "js", RESOURCES.javaScript()),
                new EditorCreator() {
                    @Override
                    public Editor createEditor() {
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
                new EditorCreator() {
                    @Override
                    public Editor createEditor() {
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
                new EditorCreator() {
                    @Override
                    public Editor createEditor() {
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
        new JavaScriptCodeController();
    }
}
