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
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Image;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.IDEImageBundle;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.View;
import org.exoplatform.ide.client.framework.ui.ViewType;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileCallback;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.framework.vfs.VersionsCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.FileContentSavedEvent;
import org.exoplatform.ide.client.framework.vfs.event.FileContentSavedHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.OpenVersionEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.OpenVersionHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ShowNextVersionEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.ShowNextVersionHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ShowPreviousVersionEvent;
import org.exoplatform.ide.client.module.navigation.event.versioning.ShowPreviousVersionHandler;
import org.exoplatform.ide.client.module.navigation.event.versioning.ShowVersionListEvent;
import org.exoplatform.ide.client.versioning.VersionContentForm;
import org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class VersionHistoryCommandHandler implements OpenVersionHandler, EditorActiveFileChangedHandler, ShowPreviousVersionHandler, 
ShowNextVersionHandler, ViewClosedHandler, ViewOpenedHandler, FileContentSavedHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   private Version version;

   private File activeFile;

   private List<Version> versionHistory = new ArrayList<Version>();

   private Version versionToOpenOnError;

   private int ignoreErrorCount = 0;

   private boolean isVersionPanelOpened = false;

   public VersionHistoryCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(OpenVersionEvent.TYPE, this);
      handlers.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      handlers.addHandler(ShowNextVersionEvent.TYPE, this);
      handlers.addHandler(ShowPreviousVersionEvent.TYPE, this);
      handlers.addHandler(ViewOpenedEvent.TYPE, this);
      handlers.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.OpenVersionHandler#onOpenVersion(org.exoplatform.ide.client.module.navigation.event.versioning.OpenVersionEvent)
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
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.ShowNextVersionHandler#onShowNextVersion(org.exoplatform.ide.client.module.navigation.event.versioning.ShowNextVersionEvent)
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
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.ShowPreviousVersionHandler#onShowPreviousVersion(org.exoplatform.ide.client.module.navigation.event.versioning.ShowPreviousVersionEvent)
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
         View view = new VersionContentForm(eventBus, version);
         view.setImage(new Image(IDEImageBundle.INSTANCE.viewVersions()));
         view.setTitle("Version");
         view.setType(ViewType.VERSIONS);
         IDE.getInstance().openView(view);
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
    * @see org.exoplatform.ide.client.module.navigation.event.versioning.ShowVersionListHandler#onShowVersionList(org.exoplatform.ide.client.module.navigation.event.versioning.ShowVersionListEvent)
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
    * @see org.exoplatform.ide.client.framework.ui.event.ViewClosedHandler#onPanelClosed(org.exoplatform.ide.client.framework.ui.event.ViewClosedEvent)
    */
   public void onViewClosed(ViewClosedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getViewId()))
      {
         isVersionPanelOpened = false;
         version = null;
         handlers.removeHandler(FileContentSavedEvent.TYPE);
      }
   }

   /**
    * @see org.exoplatform.ide.client.panel.event.PanelOpenedHandler#onPanelOpened(org.exoplatform.ide.client.panel.event.PanelOpenedEvent)
    */
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (VersionContentForm.ID.equals(event.getViewId()))
      {
         isVersionPanelOpened = true;
         handlers.addHandler(FileContentSavedEvent.TYPE, this);
      }
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
}
