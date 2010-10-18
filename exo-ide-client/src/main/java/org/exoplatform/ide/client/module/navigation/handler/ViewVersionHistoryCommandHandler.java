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

import com.google.gwt.user.client.Timer;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.OpenVersionEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.OpenVersionHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewNextVersionEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewNextVersionHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewPreviousVersionEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewPreviousVersionHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewVersionHistoryEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewVersionHistoryHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ViewVersionListEvent;
import org.exoplatform.ide.client.framework.module.vfs.api.File;
import org.exoplatform.ide.client.framework.module.vfs.api.Version;
import org.exoplatform.ide.client.framework.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ide.client.framework.module.vfs.api.event.FileContentReceivedEvent;
import org.exoplatform.ide.client.framework.module.vfs.api.event.FileContentReceivedHandler;
import org.exoplatform.ide.client.framework.module.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ide.client.framework.module.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemVersionsReceivedEvent;
import org.exoplatform.ide.client.framework.module.vfs.api.event.ItemVersionsReceivedHandler;
import org.exoplatform.ide.client.panel.event.ClosePanelEvent;
import org.exoplatform.ide.client.panel.event.OpenPanelEvent;
import org.exoplatform.ide.client.panel.event.PanelClosedEvent;
import org.exoplatform.ide.client.panel.event.PanelClosedHandler;
import org.exoplatform.ide.client.panel.event.PanelOpenedEvent;
import org.exoplatform.ide.client.panel.event.PanelOpenedHandler;
import org.exoplatform.ide.client.versioning.VersionContentForm;
import org.exoplatform.ide.client.versioning.event.ShowVersionEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class ViewVersionHistoryCommandHandler implements ViewVersionHistoryHandler, ExceptionThrownHandler,
   ItemVersionsReceivedHandler, EditorActiveFileChangedHandler, ViewPreviousVersionHandler, ViewNextVersionHandler,
   FileContentReceivedHandler, OpenVersionHandler, PanelClosedHandler, PanelOpenedHandler, FileContentSavedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private Version version;

   private File activeFile;

   private List<Version> versionHistory = new ArrayList<Version>();

   private Version versionToOpenOnError;

   private int ignoreErrorCount = 0;

   private boolean isVersionPanelOpened = false;

   public ViewVersionHistoryCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(ViewVersionHistoryEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(ViewNextVersionEvent.TYPE, this);
      handlers.addHandler(ViewPreviousVersionEvent.TYPE, this);
      handlers.addHandler(OpenVersionEvent.TYPE, this);
      handlers.addHandler(PanelOpenedEvent.TYPE, this);
      handlers.addHandler(PanelClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.ViewVersionHistoryHandler#onViewVersionHistory(org.exoplatform.ide.client.module.navigation.event.versioning.ViewVersionHistoryEvent)
    */
   public void onViewVersionHistory(ViewVersionHistoryEvent event)
   {
      if (event.isShowVersionHistory())
      {
         getVersionHistory();
      }
      else
      {
         eventBus.fireEvent(new ClosePanelEvent(VersionContentForm.ID));
      }
   }

   private void getVersionHistory()
   {
      if (activeFile != null)
      {
         versionHistory.clear();
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
   public void onItemVersionsReceived(final ItemVersionsReceivedEvent event)
   {
      handlers.removeHandler(ExceptionThrownEvent.TYPE);
      handlers.removeHandler(ItemVersionsReceivedEvent.TYPE);
      if (event.getVersions() != null && event.getVersions().size() > 0)
      {
         versionHistory = event.getVersions();
         int index = 0;
         if (version != null)
         {
            index = getVersionIndexInList(version.getHref());
            index = (index > 0) ? index : 0;
         }
         openVersion(event.getVersions().get(index));
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
      eventBus.fireEvent(new ClosePanelEvent(VersionContentForm.ID));
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.ViewNextVersionHandler#onViewNextrVersion(org.exoplatform.ide.client.module.navigation.event.versioning.ViewNextVersionEvent)
    */
   public void onViewNextrVersion(ViewNextVersionEvent event)
   {
      if (versionHistory == null || versionHistory.size() <= 0)
      {
         return;
      }
      int currentIndex = getVersionIndexInList(version.getHref());
      if ((currentIndex >= 1))
      {
         int nextIndex = currentIndex - 1;
         getVersionContent(versionHistory.get(nextIndex));
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.ViewPreviousVersionHandler#onViewPreviousVersion(org.exoplatform.ide.client.module.navigation.event.versioning.ViewPreviousVersionEvent)
    */
   public void onViewPreviousVersion(ViewPreviousVersionEvent event)
   {
      if (versionHistory == null || versionHistory.size() <= 0)
      {
         return;
      }

      int currentIndex = getVersionIndexInList(version.getHref());
      if ((currentIndex >= 0 && currentIndex < versionHistory.size() - 1))
      {
         getVersionContent(versionHistory.get(currentIndex + 1));
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
   public void onFileContentReceived(final FileContentReceivedEvent event)
   {
      if (event.getFile() instanceof Version)
      {
         handlers.removeHandler(FileContentReceivedEvent.TYPE);
         handlers.removeHandler(ExceptionThrownEvent.TYPE);
         version = (Version)event.getFile();
         if (!isVersionPanelOpened)
         {
            eventBus.fireEvent(new OpenPanelEvent(new VersionContentForm(eventBus, version)));
            Timer timer = new Timer()
            {
               @Override
               public void run()
               {
                  eventBus.fireEvent(new ShowVersionEvent((Version)event.getFile()));
               }
            };
            timer.schedule(500);
         } else {
            eventBus.fireEvent(new ShowVersionEvent((Version)event.getFile()));
         }
        
         eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.ViewVersionListHandler#onViewVersionList(org.exoplatform.ide.client.module.navigation.event.versioning.ViewVersionListEvent)
    */
   public void onViewVersionList(ViewVersionListEvent event)
   {
      getVersionHistory();
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.OpenVersionHandler#onOpenVersion(org.exoplatform.ide.client.module.navigation.event.versioning.OpenVersionEvent)
    */
   public void onOpenVersion(OpenVersionEvent event)
   {
      versionHistory = event.getVersionHistory();
      openVersion(event.getVersion());
   }

   private void openVersion(Version version)
   {
      getVersionContent(version);
   }

   private int getVersionIndexInList(String href)
   {
      if (versionHistory.size() <= 0)
         return -1;
      for (int i = 0; i < versionHistory.size(); i++)
      {
         if (versionHistory.get(i).getHref().equals(href))
         {
            return i;
         }
      }
      return -1;
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.PanelClosedHandler#onPanelClosed(org.exoplatform.ide.client.panel.event.PanelClosedEvent)
    */
   public void onPanelClosed(PanelClosedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getPanelId()))
      {
         isVersionPanelOpened = false;
         version = null;
         handlers.removeHandler(FileContentSavedEvent.TYPE);
      }
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.PanelOpenedHandler#onPanelOpened(org.exoplatform.ide.client.panel.event.PanelOpenedEvent)
    */
   public void onPanelOpened(PanelOpenedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getPanelId()))
      {
         isVersionPanelOpened = true;
         handlers.addHandler(FileContentSavedEvent.TYPE, this);
      }
   }

   /**
    * @see org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedHandler#onFileContentSaved(org.exoplatform.ide.client.module.vfs.api.event.FileContentSavedEvent)
    */
   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (version != null && event.getFile().getHref().equals(version.getItemHref()))
      {
         getVersionHistory();
      }
   }
}
