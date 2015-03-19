/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.api.action;

/** @author Evgen Vidolob */
public interface IdeActions {
    String GROUP_MAIN_MENU      = "mainMenu";
    String GROUP_FILE           = "fileGroup";
    String GROUP_FILE_NEW       = "newGroup";
    String GROUP_CODE           = "codeGroup";
    String GROUP_IMPORT_PROJECT = "importProjectGroup";
    String GROUP_BUILD          = "buildGroup";
    String GROUP_RUN            = "runGroupMainMenu";
    String GROUP_WINDOW         = "windowGroup";
    String GROUP_HELP           = "helpGroup";

    String GROUP_MAIN_TOOLBAR  = "mainToolBar";
    String GROUP_RIGHT_TOOLBAR = "rightToolBar";
    String GROUP_BUILD_TOOLBAR = "buildGroupToolbar";
    String GROUP_RUN_TOOLBAR   = "runGroupToolbar";

    String GROUP_MAIN_CONTEXT_MENU  = "mainContextMenu";
    String GROUP_BUILD_CONTEXT_MENU = "buildGroupContextMenu";
    String GROUP_RUN_CONTEXT_MENU   = "runGroupContextMenu";

    String GROUP_EDITOR_POPUP    = "editorPopupMenu";
    String GROUP_EDITOR          = "editorActions";
    String GROUP_OTHER_MENU      = "otherMenu";
    String GROUP_LEFT_MAIN_MENU  = "leftMainMenu";
    String GROUP_RIGHT_MAIN_MENU = "rightMainMenu";

    String GROUP_CENTER_STATUS_PANEL = "centerStatusPanelGroup";
    String GROUP_LEFT_STATUS_PANEL   = "leftStatusPanelGroup";
    String GROUP_RIGHT_STATUS_PANEL  = "rightStatusPanelGroup";
}
