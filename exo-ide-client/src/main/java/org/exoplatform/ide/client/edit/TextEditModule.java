/*
 * Copyright (C) 2010 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
