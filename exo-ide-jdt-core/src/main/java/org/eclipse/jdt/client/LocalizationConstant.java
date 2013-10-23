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
package org.eclipse.jdt.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: LocalizationConstant.java Oct 26, 2012 vetal $
 */
public interface LocalizationConstant extends Messages {
    /*
     * Buttons
     */
    @Key("button.refactoring.rename")
    String refactoringRenameButton();

    @Key("button.cancel")
    String cancelButton();

    /*
     * Controls
     */
    @Key("control.updateDependency.id")
    String updateDependencyControlId();

    @Key("control.updateDependency.title")
    String updateDependencyControlTitle();

    @Key("control.updateDependency.prompt")
    String updateDependencyControlPrompt();

    @Key("control.refactoringRename.id")
    String refactoringRenameControlId();

    @Key("control.refactoringRename.title")
    String refactoringRenameControlTitle();

    @Key("control.refactoringRename.prompt")
    String refactoringRenameControlPrompt();

    @Key("control.disableSyntaxErrorHighlighting.id")
    String disableSyntaxErrorHighlightingId();

    @Key("control.disableSyntaxErrorHighlighting.title")
    String disableSyntaxErrorHighlightingControlTitle();

    @Key("control.enableSyntaxErrorHighlighting.title")
    String enableSyntaxErrorHighlightingControlTitle();

    @Key("control.disableSyntaxErrorHighlighting.prompt")
    String disableSyntaxErrorHighlightingControlPrompt();

    /*
     * Messages
     */
    @Key("messages.updateDependency.started")
    String updateDependencyStarted(String project);

    @Key("messages.updateDependency.finished")
    String updateDependencyFinished(String project);

    @Key("messages.updateDependency.suggest")
    String updateDependencySuggest();

    @Key("messages.refactoringRename.saveFiles")
    String refactoringRenameSaveFiles();

    @Key("messages.refactoringRename.wait")
    String refactoringRenameWait();

    @Key("messages.refactoringRename.unavailable")
    String refactoringRenameUnavailable();

    @Key("messages.refactoringRename.badCursorPosition")
    String refactoringRenameBadCursorPosition();

    /*
     * RefactoringRenameView
     */
    @Key("refactoringRename.view.title")
    String refactoringRenameViewTitle();

    @Key("refactoringRename.field.newName")
    String refactoringRenameFieldNewName();
}
