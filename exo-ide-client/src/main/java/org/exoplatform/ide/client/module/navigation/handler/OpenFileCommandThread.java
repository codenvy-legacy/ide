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
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.commons.webdav.PropfindResponse.Property;
import org.exoplatform.gwtframework.editor.api.Editor;
import org.exoplatform.gwtframework.editor.api.EditorNotFoundException;
import org.exoplatform.ide.client.editor.EditorUtil;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorOpenFileEvent;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.OpenFileHandler;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedEvent;
import org.exoplatform.ide.client.framework.userinfo.event.UserInfoReceivedHandler;
import org.exoplatform.ide.client.model.settings.ApplicationSettings;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.model.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemLockedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemLockedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ide.client.module.vfs.property.ItemProperty;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
*/
public class OpenFileCommandThread implements OpenFileHandler, FileContentReceivedHandler, ExceptionThrownHandler,
   ItemPropertiesReceivedHandler, ItemLockedHandler, EditorFileOpenedHandler, EditorFileClosedHandler, ApplicationSettingsReceivedHandler, UserInfoReceivedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private String selectedEditor;

   private ApplicationSettings applicationSettings;

   private UserInfo userInfo;

   private Map<String, File> openedFiles = new HashMap<String, File>();

   public OpenFileCommandThread(HandlerManager eventBus)
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
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
      handlers.addHandler(ItemLockedEvent.TYPE, this);

      VirtualFileSystem.getInstance().getProperties(file);
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      File file = (File)event.getItem();
      for (Property p : file.getProperties())
      {
         if (ItemProperty.Namespace.JCR.equals(p.getName().getNamespaceURI())
            && ItemProperty.JCR_LOCKOWNER.getLocalName().equalsIgnoreCase(p.getName().getLocalName()))
         {
            VirtualFileSystem.getInstance().getContent((File)event.getItem());
            return;
         }
      }

      VirtualFileSystem.getInstance().lock(event.getItem(), 600, userInfo.getName());
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemLockedHandler#onItemLocked(org.exoplatform.ide.client.module.vfs.api.event.ItemLockedEvent)
    */
   public void onItemLocked(ItemLockedEvent event)
   {
      File file = (File)event.getItem();
      if (file.getContent() != null)
      {
         openFile(file);
         return;
      }
      VirtualFileSystem.getInstance().getContent((File)event.getItem());
   }

   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      handlers.removeHandlers();
      openFile(event.getFile());
   }

   @SuppressWarnings("unchecked")
   private void openFile(File file)
   {
      handlers.removeHandlers();

      try
      {
         if (selectedEditor == null)
         {
            Map<String, String> defaultEditors = (Map<String, String>)applicationSettings.getValue("default-editors");
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
      handlers.removeHandlers();
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
