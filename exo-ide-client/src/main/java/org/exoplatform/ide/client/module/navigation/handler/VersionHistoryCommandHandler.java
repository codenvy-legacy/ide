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

import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerRegistration;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileCallback;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.framework.vfs.VersionsCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.FileContentSavedEvent;
import org.exoplatform.ide.client.framework.vfs.event.FileContentSavedHandler;
import org.exoplatform.ide.client.versioning.VersionContentForm;
import org.exoplatform.ide.client.versioning.event.OpenVersionEvent;
import org.exoplatform.ide.client.versioning.event.OpenVersionHandler;
import org.exoplatform.ide.client.versioning.event.ShowNextVersionEvent;
import org.exoplatform.ide.client.versioning.event.ShowNextVersionHandler;
import org.exoplatform.ide.client.versioning.event.ShowPreviousVersionEvent;
import org.exoplatform.ide.client.versioning.event.ShowPreviousVersionHandler;
import org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionListEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class VersionHistoryCommandHandler implements OpenVersionHandler, EditorActiveFileChangedHandler,
   ShowPreviousVersionHandler, ShowNextVersionHandler, ViewClosedHandler, ViewOpenedHandler, FileContentSavedHandler
{
   private HandlerManager eventBus;

   private Version version;

   private File activeFile;

   private List<Version> versionHistory = new ArrayList<Version>();

   private Version versionToOpenOnError;

   private int ignoreErrorCount = 0;

   private boolean isVersionPanelOpened = false;

   private VersionContentForm view;
   
   /**
    * Used to remove handler when it is no longer needed.
    */
   private HandlerRegistration fileContentHandlerRegistration;

   public VersionHistoryCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      eventBus.addHandler(OpenVersionEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ShowNextVersionEvent.TYPE, this);
      eventBus.addHandler(ShowPreviousVersionEvent.TYPE, this);
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.versioning.event.OpenVersionHandler#onOpenVersion(org.exoplatform.ide.client.versioning.event.OpenVersionEvent)
    */
   public void onOpenVersion(OpenVersionEvent event)
   {
      if (event.isShowVersionHistory())
      {
         getVersionHistory();
      }
      else
      {
         if (event.getVersion() == null)
         {
            IDE.getInstance().closeView((VersionContentForm.ID));
         }
         else
         {
            versionHistory = event.getVersionHistory();
            openVersion(event.getVersion());
         }
      }
   }

   private void getVersionHistory()
   {
      if (activeFile != null)
      {
         versionHistory.clear();
         VirtualFileSystem.getInstance().getVersions(activeFile, new VersionsCallback()
         {
            @Override
            protected void onSuccess(VersionsData result)
            {
               if (result.getVersions() != null && result.getVersions().size() > 0)
               {
                  versionHistory = result.getVersions();
                  int index = 0;
                  if (version != null)
                  {
                     index = getVersionIndexInList(version.getHref());
                     index = (index > 0) ? index : 0;
                  }
                  openVersion(result.getVersions().get(index));
               }
               else
               {
                  Dialogs.getInstance().showInfo("Item \"" + result.getItem().getName() + "\" has no versions.");
               }
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               String errorMessage = "Versions were not received.";
               eventBus.fireEvent(new ExceptionThrownEvent(errorMessage));

               if (versionToOpenOnError != null && ignoreErrorCount > 0)
               {
                  ignoreErrorCount--;
                  getVersionContent(versionToOpenOnError);
                  return;
               }

               eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));
            }
         });
      }
      else
      {
         Dialogs.getInstance().showInfo("Please, open file.");
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
      IDE.getInstance().closeView(VersionContentForm.ID);
      view = null;
   }

   /**
    * @see org.exoplatform.ide.client.versioning.event.ShowNextVersionHandler#onShowNextVersion(org.exoplatform.ide.client.versioning.event.ShowNextVersionEvent)
    */
   public void onShowNextVersion(ShowNextVersionEvent event)
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
    * @see org.exoplatform.ide.client.versioning.event.ShowPreviousVersionHandler#onShowPreviousVersion(org.exoplatform.ide.client.versioning.event.ShowPreviousVersionEvent)
    */
   public void onShowPreviousVersion(ShowPreviousVersionEvent event)
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
      VirtualFileSystem.getInstance().getContent(version, new FileCallback()
      {
         @Override
         protected void onFailure(Throwable exception)
         {
            super.onFailure(exception);

            if (versionToOpenOnError != null && ignoreErrorCount > 0)
            {
               ignoreErrorCount--;
               getVersionContent(versionToOpenOnError);
               return;
            }

            eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));
         }

         @Override
         protected void onSuccess(File result)
         {
            if (result instanceof Version)
            {
               showVersion((Version)result);
            }
         }
      });
   }

   private void showVersion(final Version versionToShow)
   {
      version = versionToShow;
      if (!isVersionPanelOpened)
      {
         if (view == null)
         {
            view = new VersionContentForm(eventBus, version);
            view.setIcon(new Image(IDEImageBundle.INSTANCE.viewVersions()));
            view.setTitle("Version");
            IDE.getInstance().openView(view);
         }
         else
         {
            view.setVersionContent(versionToShow.getContent());
         }

         Timer timer = new Timer()
         {
            @Override
            public void run()
            {
               eventBus.fireEvent(new ShowVersionContentEvent(versionToShow));
            }
         };
         timer.schedule(2000);
      }
      else
      {
         eventBus.fireEvent(new ShowVersionContentEvent(versionToShow));
      }

      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
   }

   /**
    * @see org.exoplatform.ide.client.versioning.event.ShowVersionListHandler#onShowVersionList(org.exoplatform.ide.client.versioning.event.ShowVersionListEvent)
    */
   public void onShowVersionList(ShowVersionListEvent event)
   {
      getVersionHistory();
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
    * @see org.exoplatform.ide.client.framework.vfs.event.event.FileContentSavedHandler#onFileContentSaved(org.exoplatform.ide.client.framework.vfs.event.event.FileContentSavedEvent)
    */
   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (version != null && event.getFile().getHref().equals(version.getItemHref()))
      {
         getVersionHistory();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedEvent)
    */
   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getView().getId()))
      {
         isVersionPanelOpened = true;
         fileContentHandlerRegistration = eventBus.addHandler(FileContentSavedEvent.TYPE, this);
      }

   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getView().getId()))
      {
         isVersionPanelOpened = false;
         version = null;
         if (fileContentHandlerRegistration != null)
         {
            fileContentHandlerRegistration.removeHandler();
         }
         view = null;
      }
   }
}
