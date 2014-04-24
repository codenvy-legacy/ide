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
package com.codenvy.ide.api.ui.action;

/** @author Evgen Vidolob */
public interface IdeActions {
    String GROUP_MAIN_MENU = "MainMenu";
    String GROUP_FILE      = "FileGroup";
    String GROUP_PROJECT   = "BuildProject";
    String GROUP_BUILD     = "BuildGroup";
    String GROUP_RUN       = "RunGroupMainMenu";
    String GROUP_WINDOW    = "WindowGroup";
    String GROUP_HELP      = "HelpGroup";

    String GROUP_MAIN_TOOLBAR  = "MainToolBar";
    String GROUP_BUILD_TOOLBAR = "BuildGroupToolbar";
    String GROUP_RUN_TOOLBAR   = "RunGroupToolbar";

    String GROUP_MAIN_CONTEXT_MENU  = "MainContextMenu";
    String GROUP_BUILD_CONTEXT_MENU = "BuildGroupContextMenu";
    String GROUP_RUN_CONTEXT_MENU   = "RunGroupContextMenu";

    String GROUP_EDITOR_POPUP = "EditorPopupMenu";
    String GROUP_EDITOR       = "EditorActions";
    String GROUP_OTHER_MENU   = "OtherMenu";
}
