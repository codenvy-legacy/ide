/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.ext.java.client.newresource;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.api.ui.action.ActionEvent;
import com.codenvy.ide.ext.java.client.JavaLocalizationConstant;
import com.codenvy.ide.ext.java.client.JavaResources;
import com.codenvy.ide.ext.java.client.projectmodel.JavaProject;
import com.codenvy.ide.ui.dialogs.askValue.AskValueCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueDialog;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Action to create new Java interface.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NewInterfaceAction extends NewJavaResourceAction {
    @Inject
    public NewInterfaceAction(JavaResources javaResources,
                              JavaLocalizationConstant localizationConstant,
                              ResourceProvider resourceProvider,
                              SelectionAgent selectionAgent,
                              EditorAgent editorAgent) {
        super(localizationConstant.actionNewInterfaceTitle(),
              localizationConstant.actionNewInterfaceDescription(),
              javaResources.interfaceItem(),
              null,
              resourceProvider,
              selectionAgent,
              editorAgent);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        new AskValueDialog("New " + title, "Name:", new AskValueCallback() {
            @Override
            public void onOk(String value) {
                JavaProject activeProject = (JavaProject)resourceProvider.getActiveProject();

                StringBuilder content = new StringBuilder(getPackage(getParent(), (activeProject).getSourceFolders()));
                content.append("public interface ").append(value).append(DEFAULT_CONTENT);

                createFile(value, getParent(), activeProject, content.toString());
            }
        }).show();
    }
}
