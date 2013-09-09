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
package org.exoplatform.ide.editor.yaml.client;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesEvent;
import org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler;
import org.exoplatform.ide.client.framework.control.GroupNames;
import org.exoplatform.ide.client.framework.control.NewItemControl;
import org.exoplatform.ide.client.framework.module.EditorCreator;
import org.exoplatform.ide.client.framework.module.Extension;
import org.exoplatform.ide.client.framework.module.FileType;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.editor.client.api.Editor;
import org.exoplatform.ide.editor.codemirror.CodeMirror;

/**
 * Provides a text editing area.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: YamlEditorExtension.java May 29, 2012 3:07:18 PM azatsarynnyy $
 */
public class YamlEditorExtension extends Extension implements InitializeServicesHandler {
    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        IDE.getInstance().addControl(
                new NewItemControl("File/New/New YAML File", "YAML File", "Create YAML File",
                                   YamlClientBundle.INSTANCE.yaml(), YamlClientBundle.INSTANCE.yamlDisabled(), MimeType.TEXT_YAML)
                        .setGroupName(GroupNames.NEW_SCRIPT));
    }

    /** @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     * .client.framework.application.event.InitializeServicesEvent) */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        IDE.getInstance().getFileTypeRegistry().addFileType(
                new FileType(MimeType.TEXT_YAML, "yaml", Images.INSTANCE.yamlImage()),
                new EditorCreator() {
                    @Override
                    public Editor createEditor() {
                        return new CodeMirror(MimeType.TEXT_YAML);
                    }
                });

//      IDE.getInstance().addEditor(new CodeMirror(MimeType.TEXT_YAML, "CodeMirror YAML editor", "yml",
//         new CodeMirrorConfiguration()
//      ));

//      IDE.getInstance().addEditor(
//         new CodeMirrorProducer(MimeType.TEXT_YAML, "CodeMirror YAML editor", "yml", Images.INSTANCE.yamlImage(), true,
//            new CodeMirrorConfiguration()));
    }

}
