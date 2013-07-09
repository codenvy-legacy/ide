/*
 * Copyright (C) 2003-2012 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package com.codenvy.ide.extension.demo;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.ui.action.ActionManager;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Extension used to demonstrate the IDE 2.0 SDK features.
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