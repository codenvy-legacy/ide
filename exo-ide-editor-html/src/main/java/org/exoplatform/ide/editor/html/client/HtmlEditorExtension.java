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
package org.exoplatform.ide.editor.html.client;

import com.google.gwt.core.client.GWT;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.editor.AddCommentsModifierEvent;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.ckeditor.CKEditor;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.html.client.codemirror.HtmlOutlineItemCreator;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class HtmlEditorExtension extends Extension {

    public static final HtmlMessages MESSAGES = GWT.create(HtmlMessages.class);

    public static final HtmlClientBundle RESOURCES = GWT.create(HtmlClientBundle.class);

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        RESOURCES.css().ensureInjected();

        IDE.getInstance().addControl(
                new NewItemControl("File/New/New HTML", MESSAGES.controlNewHtmlTitle(), MESSAGES.controlNewHtmlPrompt(),
                                   Images.HTML, MimeType.TEXT_HTML).setGroupName(GroupNames.NEW_FILE));

        IDE.getInstance().getFileTypeRegistry()
           .addFileType(new FileType(MimeType.TEXT_HTML, "html", Images.INSTANCE.html()), new EditorCreator() {
                            @Override
                            public Editor createEditor() {
//               return new CodeMirror(
//                  MimeType.TEXT_HTML,
//                  new CodeMirrorConfiguration()
//                     .setGenericParsers(
//                        "['parsexml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']")
//                     .setGenericStyles(
//                        "['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH
//                           + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']")
//                     .setParser(new HtmlParser()).setCanBeOutlined(true)
//                     .setAutocompleteHelper(new HtmlAutocompleteHelper()).setCodeAssistant(new HtmlCodeAssistant())
//                     .setCanHaveSeveralMimeTypes(true));
                                return new HtmlEditor(MimeType.TEXT_HTML);
                            }
                        }, new EditorCreator() {
                            @Override
                            public Editor createEditor() {
                                return new CKEditor(MimeType.TEXT_HTML);
                            }
                        }
                       );

        IDE.getInstance().addOutlineItemCreator(MimeType.TEXT_HTML, new HtmlOutlineItemCreator());
        IDE.fireEvent(new AddCommentsModifierEvent(MimeType.TEXT_HTML, new HtmlCommentsModifier()));
    }

}
