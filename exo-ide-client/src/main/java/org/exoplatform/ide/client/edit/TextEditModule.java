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
package org.exoplatform.ide.client.edit;

import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.edit.control.*;
import org.exoplatform.ide.client.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ide.client.edit.event.ShowLineNumbersHandler;
import org.exoplatform.ide.client.edit.switching.SwitchingEditorCommandHandler;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.model.SettingsService;
import org.exoplatform.ide.client.operation.closeeditor.CloseEditorController;
import org.exoplatform.ide.client.operation.findtext.FindTextPresenter;
import org.exoplatform.ide.client.operation.gotoline.GoToLinePresenter;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 */
public class TextEditModule implements ShowLineNumbersHandler, ApplicationSettingsReceivedHandler {

    private ApplicationSettings applicationSettings;

    public TextEditModule() {
        IDE.getInstance().addControl(new UndoTypingControl(), Docking.TOOLBAR);
        IDE.getInstance().addControl(new RedoTypingControl(), Docking.TOOLBAR);
        IDE.getInstance().addControl(new FormatSourceControl(), Docking.TOOLBAR);

        /*
         * IDE.getInstance().addControl(new CutTextControl()); IDE.getInstance().addControl(new CopyTextControl());
         * IDE.getInstance().addControl(new PasteTextControl());
         */
        IDE.getInstance().addControl(new DeleteTextControl());
        IDE.getInstance().addControl(new SelectAllTextControl());
        IDE.getInstance().addControl(new ToggleCommentControl());
        IDE.getInstance().addControl(new AddBlockCommentControl());
        IDE.getInstance().addControl(new RemoveBlockCommentControl());

        new MoveLineUpDownManager();

        new FindTextPresenter();

        IDE.getInstance().addControl(new ShowLineNumbersControl());
        IDE.getInstance().addControl(new DeleteCurrentLineControl());
//        IDE.getInstance().addControl(new CollapseFoldControl());
//        IDE.getInstance().addControl(new ExpandFoldControl());
        // IDE.getInstance().addControl(new CollapseAllFoldsControl());
        // IDE.getInstance().addControl(new ExpandAllFoldsControl());
        IDE.getInstance().addControl(new FoldSelectionControl());

        new GoToLinePresenter();

        // IDE.getInstance().addControl(new LockUnlockFileControl(), Docking.TOOLBAR);
        // new LockUnlockFileHandler();

        IDE.addHandler(ShowLineNumbersEvent.TYPE, this);
        IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);

        new CloseAllFilesEventHandler();
        new CodeFormatterManager(IDE.eventBus());
        new CodeCommentsManager();

        new SwitchingEditorCommandHandler();
        new CloseEditorController();
    }


    /** {@inheritDoc} */
    public void onShowLineNumbers(ShowLineNumbersEvent event) {
        applicationSettings.setValue("line-numbers", Boolean.valueOf(event.isShowLineNumber()), Store.COOKIES);
        SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
        /*
         * fire event for show-hide line numbers command be able to update state.
         */
        IDE.fireEvent(new ApplicationSettingsSavedEvent(applicationSettings, SaveType.COOKIES));
    }

    public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event) {
        applicationSettings = event.getApplicationSettings();
    }

}
