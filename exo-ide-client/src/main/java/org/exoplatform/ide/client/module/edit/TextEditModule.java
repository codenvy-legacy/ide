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
package org.exoplatform.ide.client.module.edit;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ide.client.framework.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDEModule;
import org.exoplatform.ide.client.model.settings.ApplicationSettings;
import org.exoplatform.ide.client.model.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.model.settings.event.SaveApplicationSettingsEvent;
import org.exoplatform.ide.client.model.settings.event.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.module.edit.action.GoToLineForm;
import org.exoplatform.ide.client.module.edit.control.FindTextCommand;
import org.exoplatform.ide.client.module.edit.control.FormatSourceCommand;
import org.exoplatform.ide.client.module.edit.control.GoToLineControl;
import org.exoplatform.ide.client.module.edit.control.RedoTypingCommand;
import org.exoplatform.ide.client.module.edit.control.ShowLineNumbersCommand;
import org.exoplatform.ide.client.module.edit.control.UndoTypingCommand;
import org.exoplatform.ide.client.module.edit.event.FindTextEvent;
import org.exoplatform.ide.client.module.edit.event.FindTextHandler;
import org.exoplatform.ide.client.module.edit.event.GoToLineEvent;
import org.exoplatform.ide.client.module.edit.event.GoToLineHandler;
import org.exoplatform.ide.client.module.edit.event.ShowLineNumbersEvent;
import org.exoplatform.ide.client.module.edit.event.ShowLineNumbersHandler;
import org.exoplatform.ide.client.module.navigation.control.DeleteLineControl;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.search.text.FindTextForm;
import org.exoplatform.ide.client.statusbar.EditorCursorPositionControl;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class TextEditModule implements IDEModule, RegisterEventHandlersHandler, FindTextHandler, GoToLineHandler,
   ShowLineNumbersHandler, ApplicationSettingsReceivedHandler, EditorActiveFileChangedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private ApplicationSettings applicationSettings;

   private File activeFile;

   public TextEditModule(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.fireEvent(new RegisterControlEvent(new UndoTypingCommand(eventBus), true));
      eventBus.fireEvent(new RegisterControlEvent(new RedoTypingCommand(eventBus), true));
      eventBus.fireEvent(new RegisterControlEvent(new FormatSourceCommand(eventBus), true));

      eventBus.fireEvent(new RegisterControlEvent(new FindTextCommand(eventBus), true));
      eventBus.fireEvent(new RegisterControlEvent(new ShowLineNumbersCommand(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new DeleteLineControl(eventBus)));
      eventBus.fireEvent(new RegisterControlEvent(new GoToLineControl(eventBus)));
      
      eventBus.fireEvent(new RegisterControlEvent(new EditorCursorPositionControl(eventBus)));

      handlers = new Handlers(eventBus);
      handlers.addHandler(RegisterEventHandlersEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
   }

   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      handlers.removeHandler(RegisterEventHandlersEvent.TYPE);
      handlers.addHandler(ShowLineNumbersEvent.TYPE, this);
      handlers.addHandler(FindTextEvent.TYPE, this);
      handlers.addHandler(GoToLineEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.event.edit.ShowLineNumbersHandler#onShowLineNumbers(org.exoplatform.ide.client.event.edit.ShowLineNumbersEvent)
    */
   public void onShowLineNumbers(ShowLineNumbersEvent event)
   {
      applicationSettings.setValue("line-numbers", new Boolean(event.isShowLineNumber()), Store.COOKIES);
      //      applicationSettings.setStoredIn("line-numbers", Store.COOKIES);
      //applicationSettings.setShowLineNumbers(event.isShowLineNumber());
      //CookieManager.setShowLineNumbers(event.isShowLineNumber());
      eventBus.fireEvent(new SaveApplicationSettingsEvent(applicationSettings, SaveType.COOKIES));
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
