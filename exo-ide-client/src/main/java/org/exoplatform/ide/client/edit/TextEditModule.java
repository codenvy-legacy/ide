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

import org.exoplatform.ide.client.edit.control.DeleteCurrentLineControl;
import org.exoplatform.ide.client.edit.control.FindTextCommand;
import org.exoplatform.ide.client.edit.control.FormatSourceCommand;
import org.exoplatform.ide.client.edit.control.GoToLineControl;
import org.exoplatform.ide.client.edit.control.LockUnlockFileControl;
import org.exoplatform.ide.client.edit.control.RedoTypingCommand;
import org.exoplatform.ide.client.edit.control.ShowLineNumbersCommand;
import org.exoplatform.ide.client.edit.control.UndoTypingCommand;
import org.exoplatform.ide.client.edit.event.FindTextEvent;
import org.exoplatform.ide.client.edit.event.FindTextHandler;
import org.exoplatform.ide.client.edit.event.GoToLineEvent;
import org.exoplatform.ide.client.edit.event.GoToLineHandler;
import org.exoplatform.ide.client.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ide.client.edit.event.ShowLineNumbersHandler;
import org.exoplatform.ide.client.edit.ui.GoToLineForm;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent.DockTarget;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.model.settings.SettingsService;
import org.exoplatform.ide.client.search.text.FindTextForm;
import org.exoplatform.ide.client.statusbar.EditorCursorPositionControl;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class TextEditModule implements FindTextHandler, GoToLineHandler,
   ShowLineNumbersHandler, ApplicationSettingsReceivedHandler, EditorActiveFileChangedHandler
{
   private HandlerManager eventBus;

   private ApplicationSettings applicationSettings;

   private File activeFile;

   public TextEditModule(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.fireEvent(new RegisterControlEvent(new UndoTypingCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new RedoTypingCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new FormatSourceCommand(), DockTarget.TOOLBAR));

      eventBus.fireEvent(new RegisterControlEvent(new FindTextCommand(), DockTarget.TOOLBAR));
      eventBus.fireEvent(new RegisterControlEvent(new ShowLineNumbersCommand()));
      eventBus.fireEvent(new RegisterControlEvent(new DeleteCurrentLineControl()));
      eventBus.fireEvent(new RegisterControlEvent(new GoToLineControl()));

      eventBus.fireEvent(new RegisterControlEvent(new EditorCursorPositionControl(), DockTarget.STATUSBAR, true));
      
      eventBus.fireEvent(new RegisterControlEvent(new LockUnlockFileControl(), DockTarget.TOOLBAR));

      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      
      eventBus.addHandler(ShowLineNumbersEvent.TYPE, this);
      eventBus.addHandler(FindTextEvent.TYPE, this);
      eventBus.addHandler(GoToLineEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      
      new LockUnlockFileHandler(eventBus);
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
      eventBus.fireEvent(new ApplicationSettingsSavedEvent(applicationSettings, SaveType.COOKIES));
   }

   /**
    * @see org.exoplatform.ide.client.event.edit.FindTextHandler#onFindText(org.exoplatform.ide.client.event.edit.FindTextEvent)
    */
   public void onFindText(FindTextEvent event)
   {
      new FindTextForm(eventBus, activeFile);
   }

   public void onGoToLine(GoToLineEvent event)
   {
      if (activeFile == null)
      {
         return;
      }

      new GoToLineForm(eventBus, activeFile);
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();
   }

}
