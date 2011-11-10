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
import org.exoplatform.ide.client.edit.control.DeleteCurrentLineControl;
import org.exoplatform.ide.client.edit.control.FindTextControl;
import org.exoplatform.ide.client.edit.control.FormatSourceControl;
import org.exoplatform.ide.client.edit.control.GoToLineControl;
import org.exoplatform.ide.client.edit.control.LockUnlockFileControl;
import org.exoplatform.ide.client.edit.control.RedoTypingControl;
import org.exoplatform.ide.client.edit.control.ShowLineNumbersControl;
import org.exoplatform.ide.client.edit.control.UndoTypingControl;
import org.exoplatform.ide.client.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ide.client.edit.event.ShowLineNumbersHandler;
import org.exoplatform.ide.client.framework.control.Docking;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.client.statusbar.EditorCursorPositionControl;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class TextEditModule implements ShowLineNumbersHandler, ApplicationSettingsReceivedHandler
{

   private ApplicationSettings applicationSettings;

   public TextEditModule()
   {
      IDE.getInstance().addControl(new UndoTypingControl(), Docking.TOOLBAR);
      IDE.getInstance().addControl(new RedoTypingControl(), Docking.TOOLBAR);
      IDE.getInstance().addControl(new FormatSourceControl(), Docking.TOOLBAR);

      IDE.getInstance().addControl(new FindTextControl(), Docking.TOOLBAR);
      new FindTextPresenter();
      
      IDE.getInstance().addControl(new ShowLineNumbersControl());
      
      IDE.getInstance().addControl(new DeleteCurrentLineControl());

      IDE.getInstance().addControl(new GoToLineControl());
      new GoToLinePresenter();

      IDE.getInstance().addControl(new EditorCursorPositionControl(), Docking.STATUSBAR_RIGHT);
      
      IDE.getInstance().addControl(new LockUnlockFileControl(), Docking.TOOLBAR);

      
      new LockUnlockFileHandler();
      
      
      IDE.addHandler(ShowLineNumbersEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      
      new CloseAllFilesEventHandler();
   }

   /**
    * @see org.exoplatform.ide.client.event.edit.ShowLineNumbersHandler#onShowLineNumbers(org.exoplatform.ide.client.event.edit.ShowLineNumbersEvent)
    */
   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {
      applicationSettings.setValue("line-numbers", new Boolean(event.isShowLineNumber()), Store.COOKIES);
      SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
      /*
       * fire event for show-hide line numbers command be able to update state.
       */
      IDE.fireEvent(new ApplicationSettingsSavedEvent(applicationSettings, SaveType.COOKIES));
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

}
