/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.action;

/** @author Evgen Vidolob */
public interface IdeActions {
    String GROUP_MAIN_MENU      = "MainMenu";
    String GROUP_FILE           = "FileGroup";
    String GROUP_FILE_NEW       = "NewGroup";
    String GROUP_CODE           = "CodeGroup";
    String GROUP_IMPORT_PROJECT = "ImportProjectGroup";
    String GROUP_BUILD          = "BuildGroup";
    String GROUP_RUN            = "RunGroupMainMenu";
    String GROUP_WINDOW         = "WindowGroup";
    String GROUP_HELP           = "HelpGroup";

    String GROUP_MAIN_TOOLBAR  = "MainToolBar";
    String GROUP_BUILD_TOOLBAR = "BuildGroupToolbar";
    String GROUP_RUN_TOOLBAR   = "RunGroupToolbar";

    String GROUP_MAIN_CONTEXT_MENU  = "MainContextMenu";
    String GROUP_BUILD_CONTEXT_MENU = "BuildGroupContextMenu";
    String GROUP_RUN_CONTEXT_MENU   = "RunGroupContextMenu";

    String GROUP_EDITOR_POPUP    = "EditorPopupMenu";
    String GROUP_EDITOR          = "EditorActions";
    String GROUP_OTHER_MENU      = "OtherMenu";
    String GROUP_RIGHT_MAIN_MENU = "RightMainMenu";
}
