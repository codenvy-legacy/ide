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
package org.exoplatform.ide.editor.xml.client;

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
import org.exoplatform.ide.editor.codemirror.CodeMirror;
import org.exoplatform.ide.editor.codemirror.CodeMirrorConfiguration;
import org.exoplatform.ide.editor.xml.client.codeassistant.XmlCodeAssistant;
import org.exoplatform.ide.editor.xml.client.codemirror.XmlOutlineItemCreator;
import org.exoplatform.ide.editor.xml.client.codemirror.XmlParser;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class XmlEditorExtension extends Extension {

    public final static XmlConstant CONSTANT = GWT.create(XmlConstant.class);

    public static final XmlClientBundle RESOURCES = GWT.create(XmlClientBundle.class);

    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        RESOURCES.css().ensureInjected();

        IDE.getInstance().addControl(
                new NewItemControl("File/New/New XML", CONSTANT.controlNewXmlTitle(), CONSTANT.controlNewXmlPrompt(),
                                   Images.XML, MimeType.TEXT_XML).setGroupName(GroupNames.NEW_FILE));

        IDE.getInstance().getFileTypeRegistry().addFileType(
                new FileType(MimeType.APPLICATION_XML, "xml", RESOURCES.xml()),
                new EditorCreator() {
                    @Override
                    public Editor createEditor() {
                        return new CodeMirror(MimeType.APPLICATION_XML, new CodeMirrorConfiguration()
                                .setGenericParsers("['parsexml.js', 'tokenize.js']")
                                .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']")
                                .setParser(new XmlParser())
                                .setCanBeOutlined(true)
                                .setCodeAssistant(new XmlCodeAssistant()));
                    }
                });

        IDE.getInstance().getFileTypeRegistry().addFileType(
                new FileType(MimeType.TEXT_XML, "xml", RESOURCES.xml()),
                new EditorCreator() {
                    @Override
                    public Editor createEditor() {
                        return new CodeMirror(MimeType.TEXT_XML, new CodeMirrorConfiguration()
                                .setGenericParsers("['parsexml.js', 'tokenize.js']")
                                .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']")
                                .setParser(new XmlParser())
                                .setCanBeOutlined(true)
                                .setCodeAssistant(new XmlCodeAssistant()));
                    }
                });


//      CodeAssistant xmlAssistant = new XmlCodeAssistant();
//
//      IDE.getInstance().addEditor(new CodeMirror(MimeType.APPLICATION_XML, CONSTANT.xmlEditor(), "xml", 
//         new CodeMirrorConfiguration()
//            .setGenericParsers("['parsexml.js', 'tokenize.js']")
//            .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']")
//            .setParser(new XmlParser())
//            .setCanBeOutlined(true)
//            .setCodeAssistant(xmlAssistant)));

//      IDE.getInstance().addEditor(new CodeMirror(MimeType.TEXT_XML, CONSTANT.xmlEditor(), "xml", 
//         new CodeMirrorConfiguration()
//            .setGenericParsers("['parsexml.js', 'tokenize.js']")
//            .setGenericStyles("['" + CodeMirrorConfiguration.PATH + "css/xmlcolors.css']")
//            .setParser(new XmlParser())
//            .setCanBeOutlined(true)
//            .setCodeAssistant(xmlAssistant)));

        IDE.getInstance().addOutlineItemCreator(MimeType.APPLICATION_XML, new XmlOutlineItemCreator());
        IDE.getInstance().addOutlineItemCreator(MimeType.TEXT_XML, new XmlOutlineItemCreator());

        IDE.fireEvent(new AddCommentsModifierEvent(MimeType.APPLICATION_XML, new XmlCommentsModifier()));
        IDE.fireEvent(new AddCommentsModifierEvent(MimeType.TEXT_XML, new XmlCommentsModifier()));
    }

}
