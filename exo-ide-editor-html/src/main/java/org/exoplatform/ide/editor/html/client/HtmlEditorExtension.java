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
