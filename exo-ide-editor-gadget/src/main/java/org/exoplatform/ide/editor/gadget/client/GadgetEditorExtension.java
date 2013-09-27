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
package org.exoplatform.ide.editor.gadget.client;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.ckeditor.CKEditor;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.gadget.client.codemirror.GoogleGadgetParser;
import org.exoplatform.ide.editor.html.client.codeassistant.HtmlCodeAssistant;
import org.exoplatform.ide.editor.html.client.codemirror.HtmlAutocompleteHelper;
import org.exoplatform.ide.editor.html.client.codemirror.HtmlOutlineItemCreator;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: GadgetEditor Mar 10, 2011 11:10:28 AM evgen $
 */
public class GadgetEditorExtension extends Extension {

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.getInstance().getFileTypeRegistry().addFileType(new FileType(MimeType.GOOGLE_GADGET, "gadget", Images.INSTANCE.gadgetImage()),
                                                            new EditorCreator() {
                                                                @Override
                                                                public Editor createEditor() {
                                                                    return new CodeMirror(MimeType.GOOGLE_GADGET,
                                                                                          new CodeMirrorConfiguration()
                                                                                                  .setGenericParsers(
                                                                                                          "['parsegadgetxml.js', " +
                                                                                                          "'parsecss.js', " +
                                                                                                          "'tokenizejavascript.js', " +
                                                                                                          "'parsejavascript.js', " +
                                                                                                          "'parsehtmlmixed.js']")
                                                                                                  .setGenericStyles("['" +















                                                                                                                    CodeMirrorConfiguration.PATH +
                                                                                                                    "css/xmlcolors.css', '" +
                                                                                                                    CodeMirrorConfiguration.PATH +
                                                                                                                    "css/jscolors.css', '" +
                                                                                                                    CodeMirrorConfiguration.PATH +
                                                                                                                    "css/csscolors.css']")
                                                                                                  .setParser(new GoogleGadgetParser())
                                                                                                  .setCanBeOutlined(true)
                                                                                                  .setAutocompleteHelper(
                                                                                                          new HtmlAutocompleteHelper())
                                                                                                  .setCodeAssistant(new HtmlCodeAssistant())
                                                                                                  .setCanHaveSeveralMimeTypes(true)
                                                                    );
                                                                }
                                                            },
                                                            new EditorCreator() {
                                                                @Override
                                                                public Editor createEditor() {
                                                                    return new CKEditor(MimeType.GOOGLE_GADGET);
                                                                }
                                                            }
                                                           );

//      IDE.getInstance().addEditor(new CodeMirror(MimeType.GOOGLE_GADGET, "CodeMirror OpenSocial Gadget editor", "gadget",
//         new CodeMirrorConfiguration()
//            .setGenericParsers("['parsegadgetxml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']")
//            .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']")
//            .setParser(new GoogleGadgetParser())
//            .setCanBeOutlined(true)
//            .setAutocompleteHelper(new HtmlAutocompleteHelper())
//            .setCodeAssistant(new HtmlCodeAssistant())
//            .setCanHaveSeveralMimeTypes(true)
//               ));

//      CodeMirrorProducer producer = new CodeMirrorProducer(
//         MimeType.GOOGLE_GADGET,
//         "CodeMirror OpenSocial Gadget editor",
//         "gadget",
//         Images.INSTANCE.gadgetImage(),
//         true,
//         
//         new CodeMirrorConfiguration().
//            setGenericParsers("['parsegadgetxml.js', 'parsecss.js', 'tokenizejavascript.js', 'parsejavascript.js', 'parsehtmlmixed.js']").
//            setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css', '" + CodeMirrorConfiguration.PATH + "css/jscolors.css', '" + CodeMirrorConfiguration.PATH + "css/csscolors.css']").
//            setParser(new GoogleGadgetParser()).
//            setCanBeOutlined(true).
//            setAutocompleteHelper(new HtmlAutocompleteHelper()).
//            setCodeAssistant(new HtmlCodeAssistant()).
//            setCanHaveSeveralMimeTypes(true)
//      );      
//      IDE.getInstance().addEditor(producer);

        IDE.getInstance().addControl(new NewItemControl("File/New/New OpenSocial Gadget", "OpenSocial Gadget",
                                                        "Create New OpenSocial Gadget", Images.GOOGLE_GADGET, MimeType.GOOGLE_GADGET)
                                             .setGroupName(GroupNames.NEW_FILE));

        IDE.getInstance().addOutlineItemCreator(MimeType.GOOGLE_GADGET, new HtmlOutlineItemCreator());
    }

}
