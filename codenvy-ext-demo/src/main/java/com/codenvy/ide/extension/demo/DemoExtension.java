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
package com.codenvy.ide.extension.demo;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Extension used to demonstrate the IDE 2.0 SDK fetures
 *
 * @author <a href="mailto:nzamosenchuk@exoplatform.com">Nikolay Zamosenchuk</a>
 */
@Singleton
@Extension(title = "Demo extension", version = "3.0.0")
public class DemoExtension {

    @Inject
    public DemoExtension(final WorkspaceAgent workspace,
                         ActionManager actionManager,
                         EditorAgent editorAgent) {
        // CREATE DYNAMIC MENU CONTENT
//        menu.addMenuItem("File/Create Demo Content", createDemoCommand);
//        menu.addMenuItem("Project/Some Project Operation", new ExtendedCommand() {
//            @Override
//            public Expression inContext() {
//                return projectOpenedExpression;
//            }
//
//            @Override
//            public ImageResource getIcon() {
//                return null;
//            }
//
//            @Override
//            public void execute() {
//                Window
//                        .alert("This is test item. The item changes enable/disable state when something happend(project was opened).");
//            }
//
//            @Override
//            public Expression canExecute() {
//                return null;
//            }
//
//            @Override
//            public String getToolTip() {
//                return null;
//            }
//        });
    }
}