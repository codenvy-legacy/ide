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
package org.exoplatform.ide.client.module.navigation.handler;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorCloseFileEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewItemVersionsEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewItemVersionsHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewNextVersionEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewNextVersionHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewPreviousVersionEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewPreviousVersionHandler;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Version;
import org.exoplatform.ide.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedHandler;
import org.exoplatform.ide.client.module.vfs.api.event.ItemVersionsReceivedEvent;
import org.exoplatform.ide.client.module.vfs.api.event.ItemVersionsReceivedHandler;
import org.exoplatform.ide.client.versioning.ViewVersionsForm;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class ViewItemVersionsControlHandler implements ViewItemVersionsHandler, ExceptionThrownHandler,
   ItemVersionsReceivedHandler, EditorActiveFileChangedHandler, ViewPreviousVersionHandler, ViewNextVersionHandler,
   FileContentReceivedHandler, ItemDeletedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private File activeFile;

   private Map<String, List<Version>> versionsMap = new HashMap<String, List<Version>>();

   private Version versionToOpenOnError;

   private int ignoreErrorCount = 0;

   public ViewItemVersionsControlHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(ViewItemVersionsEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(ViewNextVersionEvent.TYPE, this);
      handlers.addHandler(ViewPreviousVersionEvent.TYPE, this);
      handlers.addHandler(ItemDeletedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.ViewItemVersionsHandler#onViewItemVersions(org.exoplatform.ide.client.module.navigation.event.versioning.ViewItemVersionsEvent)
    */
   public void onViewItemVersions(ViewItemVersionsEvent event)
   {
      System.out.println("ViewItemVersionsControlHandler.onViewItemVersions()" + versionsMap.size());

      if (activeFile != null && !(activeFile instanceof Version))
      {
         handlers.addHandler(ExceptionThrownEvent.TYPE, this);
         handlers.addHandler(ItemVersionsReceivedEvent.TYPE, this);
         VirtualFileSystem.getInstance().getVersions(activeFile);
      }
      else
      {
         Dialogs.getInstance().showInfo("Please, open file.");
      }
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandler(ExceptionThrownEvent.TYPE);
      handlers.removeHandler(FileContentReceivedEvent.TYPE);

      if (versionToOpenOnError != null && ignoreErrorCount > 0)
      {
         ignoreErrorCount--;
         getVersionContent(versionToOpenOnError);
         return;
      }

      handlers.removeHandler(ItemVersionsReceivedEvent.TYPE);
      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemVersionsReceivedHandler#onItemVersionsReceived(org.exoplatform.ide.client.module.vfs.api.event.ItemVersionsReceivedEvent)
    */
   public void onItemVersionsReceived(ItemVersionsReceivedEvent event)
   {
      handlers.removeHandler(ExceptionThrownEvent.TYPE);
      handlers.removeHandler(ItemVersionsReceivedEvent.TYPE);
      if (event.getVersions() != null && event.getVersions().size() > 0)
      {
         versionsMap.put(event.getItem().getHref(), event.getVersions());
         new ViewVersionsForm(eventBus, event.getItem(), event.getVersions());
      }
      else
      {
         Dialogs.getInstance().showInfo("Item \"" + event.getItem().getName() + "\" has no versions.");
      }

   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.ViewNextVersionHandler#onViewNextrVersion(org.exoplatform.ide.client.module.navigation.event.versioning.ViewNextVersionEvent)
    */
   public void onViewNextrVersion(ViewNextVersionEvent event)
   {
      if (activeFile == null || !(activeFile instanceof Version))
      {
         return;
      }

      Version version = (Version)activeFile;
      List<Version> versions = versionsMap.get(version.getItemHref());
      if (versions == null || versions.size() <= 0)
      {
         return;
      }

      int currentIndex = versions.indexOf(activeFile);
      if ((currentIndex >= 1))
      {
         int nextIndex = currentIndex - 1;
         getVersionContent(versions.get(nextIndex));
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.ViewPreviousVersionHandler#onViewPreviousVersion(org.exoplatform.ide.client.module.navigation.event.versioning.ViewPreviousVersionEvent)
    */
   public void onViewPreviousVersion(ViewPreviousVersionEvent event)
   {
      if (activeFile == null || !(activeFile instanceof Version))
      {
         return;
      }
      Version version = (Version)activeFile;
      List<Version> versions = versionsMap.get(version.getItemHref());
      if (versions == null || versions.size() <= 0)
      {
         return;
      }
      int currentIndex = versions.indexOf(activeFile);
      if ((currentIndex >= 0 && currentIndex < versions.size() - 1))
      {
         getVersionContent(versions.get(currentIndex + 1));
      }
   }

   private void getVersionContent(Version version)
   {
      ignoreErrorCount = 3;
      versionToOpenOnError = version;
      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      handlers.addHandler(FileContentReceivedEvent.TYPE, this);
      VirtualFileSystem.getInstance().getContent(version);
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedHandler#onFileContentReceived(org.exoplatform.ide.client.module.vfs.api.event.FileContentReceivedEvent)
    */
   public void onFileContentReceived(FileContentReceivedEvent event)
   {
      handlers.removeHandler(FileContentReceivedEvent.TYPE);
      handlers.removeHandler(ExceptionThrownEvent.TYPE);
      eventBus.fireEvent(new EditorReplaceFileEvent(activeFile, event.getFile()));
      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.client.module.vfs.api.event.ItemDeletedEvent)
    */
   public void onItemDeleted(ItemDeletedEvent event)
   {
      List<Version> versions = versionsMap.get(event.getItem().getHref());
      if (versions != null)
      {
         for (Version version : versions)
         {
            eventBus.fireEvent(new EditorCloseFileEvent(version, true));
         }
         versionsMap.remove(event.getItem().getHref());
      }

   }
}
