// $codepro.audit.disable logExceptions
/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.module.navigation.handler;

import java.util.HashMap;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.ide.client.editor.EditorUtil;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.OpenFileHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.FileContentReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.FileContentReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileCommandHandler implements OpenFileHandler, FileContentReceivedHandler, ExceptionThrownHandler,
   ItemPropertiesReceivedHandler, ItemLockResultReceivedHandler, EditorFileOpenedHandler, EditorFileClosedHandler,
   ApplicationSettingsReceivedHandler, UserInfoReceivedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private String selectedEditor;

   private ApplicationSettings applicationSettings;

   private UserInfo userInfo;
   
   private int ignoreErrorsCount = 0;
   
   private File fileToOpenOnError;

   private Map<String, File> openedFiles = new HashMap<String, File>();

   public OpenFileCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      handlers = new Handlers(eventBus);

      eventBus.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      eventBus.addHandler(OpenFileEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
      eventBus.addHandler(UserInfoReceivedEvent.TYPE, this);
   }

   public void onOpenFile(OpenFileEvent event)
   {
      ignoreErrorsCount = event.getIgnoreErrorsCount();
      selectedEditor = event.getEditor();

      File file = event.getFile();
      if (file != null)
      {
         if (file.isNewFile())
         {
            openFile(file);
            return;
         }

         //TODO Check opened file!!!
         if (openedFiles.containsKey(file.getHref()))
         {
            System.out.println("OpenFileCommandHandler.onOpenFile()");
            openFile(file);
            return;
         }
      }
      else
      {
         file = new File(event.getHref());
      }

      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      handlers.addHandler(FileContentReceivedEvent.TYPE, this);
      if (event.isLockFile())
      {
         handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
         handlers.addHandler(ItemLockResultReceivedEvent.TYPE, this);

         VirtualFileSystem.getInstance().getProperties(file, null);
      }
      else
      {
         getFileContent(file);
      }
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      File file = (File)event.getItem();
      for (Property p : file.getProperties())
      {
         if (ItemProperty.Namespace.JCR.equals(p.getName().getNamespaceURI())
            && ItemProperty.JCR_LOCKOWNER.getLocalName().equalsIgnoreCase(p.getName().getLocalName()))
         {
            getFileContent((File)event.getItem());
            return;
         }
      }

      VirtualFileSystem.getInstance().lock(event.getItem(), 600, userInfo.getName());
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedHandler#onItemLockResultReceived(org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedEvent)
    */
   public void onItemLockResultReceived(final ItemLockResultReceivedEvent event)
   {
      if (event.getException() == null)
      {
         File file = (File)event.getItem();
         if (file.getContent() != null)
         {
            openFile(file);
            return;
         }
         getFileContent((File)event.getItem());
      }
      else
      {
         Dialogs.getInstance().ask("Lock fail", "Can't lock file <b>"+event.getItem().getName()+"</b><br />Do you wont open file without locking?", new BooleanValueReceivedCallback()
         {
            
            public void execute(Boolean value)
            {
                     if(value)
                     {
                        getFileContent((File)event.getItem());
                     }
                     else
                     {
                        handlers.removeHandlers();
                     }
            }
         });
         
      }
   }
   
   
   private void getFileContent(File file){
      fileToOpenOnError = file;
      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));
      VirtualFileSystem.getInstance().getContent(file);
   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      handlers.removeHandlers();
      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
      openFile(event.getFile());
   }

   private void openFile(File file)
   {
      handlers.removeHandlers();

      try
      {
         if (selectedEditor == null)
         {
            Map<String, String> defaultEditors = applicationSettings.getValueAsMap("default-editors");
            if (defaultEditors != null)
            {
               selectedEditor = defaultEditors.get(file.getContentType());
            }
         }

         Editor editor = EditorUtil.getEditor(file.getContentType(), selectedEditor);
         eventBus.fireEvent(new EditorOpenFileEvent(file, editor));
      }
      catch (EditorNotFoundException e)
      {
         Dialogs.getInstance().showError("Can't find editor for type <b>" + file.getContentType() + "</b>");
      }
   }

   public void onError(ExceptionThrownEvent event)
   {
      if (fileToOpenOnError != null && ignoreErrorsCount > 0)
      {
         ignoreErrorsCount--;
         getFileContent(fileToOpenOnError);
         return;
      }
      
      handlers.removeHandlers();
      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();
   }

   /**
    * @see org.exoplatform.ide.client.model.conversation.event.UserInfoReceivedHandler#onUserInfoReceived(org.exoplatform.ide.client.model.conversation.event.UserInfoReceivedEvent)
    */
   public void onUserInfoReceived(UserInfoReceivedEvent event)
   {
      userInfo = event.getUserInfo();
   }

}
