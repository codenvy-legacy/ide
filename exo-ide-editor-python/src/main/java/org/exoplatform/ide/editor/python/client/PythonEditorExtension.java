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
package org.exoplatform.ide.editor.python.client;

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

/**
 * Provides a text editing area. Support syntax coloration for Python language.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: PythonEditorExtension.java May 29, 2012 3:07:18 PM azatsarynnyy $
 */
public class PythonEditorExtension extends Extension implements InitializeServicesHandler {
    /** @see org.exoplatform.ide.client.framework.module.Extension#initialize() */
    @Override
    public void initialize() {
        IDE.addHandler(InitializeServicesEvent.TYPE, this);

        IDE.getInstance().addControl(
                new NewItemControl("File/New/New Python File", "Python File", "Create Python File",
                                   PythonClientBundle.INSTANCE.python(), PythonClientBundle.INSTANCE.pythonDisabled(),
                                   MimeType.TEXT_X_PYTHON)
                        .setGroupName(GroupNames.NEW_SCRIPT));
    }

    /**
     * @see org.exoplatform.ide.client.framework.application.event.InitializeServicesHandler#onInitializeServices(org.exoplatform.ide
     *      .client.framework.application.event.InitializeServicesEvent)
     */
    @Override
    public void onInitializeServices(InitializeServicesEvent event) {
        IDE.getInstance().getFileTypeRegistry().addFileType(
                new FileType(MimeType.TEXT_X_PYTHON, "py", Images.INSTANCE.pythonImage()),
                new EditorCreator() {
                    @Override
                    public Editor createEditor() {
                        return new PythonEditor(MimeType.TEXT_X_PYTHON);
                    }
                });
    }
}
