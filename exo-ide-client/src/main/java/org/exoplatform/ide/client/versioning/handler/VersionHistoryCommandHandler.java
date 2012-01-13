//TODO: need rework according new VFS
///*
// * Copyright (C) 2010 eXo Platform SAS.
// *
// * This is free software; you can redistribute it and/or modify it
// * under the terms of the GNU Lesser General Public License as
// * published by the Free Software Foundation; either version 2.1 of
// * the License, or (at your option) any later version.
// *
// * This software is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this software; if not, write to the Free
// * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
// * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
// */
//package org.exoplatform.ide.client.versioning.handler;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
//import org.exoplatform.ide.client.IDEImageBundle;
//import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
//import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
//import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
//import org.exoplatform.ide.client.framework.event.FileSavedEvent;
//import org.exoplatform.ide.client.framework.event.FileSavedHandler;
//import org.exoplatform.ide.client.framework.module.IDE;
//import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
//import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
//import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
//import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
//import org.exoplatform.ide.client.framework.vfs.File;
//import org.exoplatform.ide.client.framework.vfs.FileCallback;
//import org.exoplatform.ide.client.framework.vfs.Version;
//import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
//import org.exoplatform.ide.client.versioning.VersionContentForm;
//import org.exoplatform.ide.client.versioning.event.OpenVersionEvent;
//import org.exoplatform.ide.client.versioning.event.OpenVersionHandler;
//import org.exoplatform.ide.client.versioning.event.ShowNextVersionEvent;
//import org.exoplatform.ide.client.versioning.event.ShowNextVersionHandler;
//import org.exoplatform.ide.client.versioning.event.ShowPreviousVersionEvent;
//import org.exoplatform.ide.client.versioning.event.ShowPreviousVersionHandler;
//import org.exoplatform.ide.client.versioning.event.ShowVersionContentEvent;
//import org.exoplatform.ide.client.versioning.event.ShowVersionListEvent;
//import org.exoplatform.ide.client.versioning.event.VersionRestoredEvent;
//import org.exoplatform.ide.client.versioning.event.VersionRestoredHandler;
//import org.exoplatform.ide.vfs.client.model.FileModel;
//
//import com.google.gwt.user.client.Timer;
//import com.google.gwt.user.client.ui.Image;
//
///**
// * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
// * @version $Id: Sep 27, 2010 $
// *
// */
//public class VersionHistoryCommandHandler implements OpenVersionHandler, EditorActiveFileChangedHandler,
//   ShowPreviousVersionHandler, ShowNextVersionHandler, ViewClosedHandler, ViewOpenedHandler, FileSavedHandler, VersionRestoredHandler
//{
//   
//   private static final String RECEIVE_VERSIONS_FAILURE = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT.versionsReceiveVersionsFailure();
//   
//   private static final String PLEASE_OPEN_FILE = org.exoplatform.ide.client.IDE.VERSIONS_CONSTANT.versionsOpenFile();
//   
//   private static final String VERSION_TITLE = org.exoplatform.ide.client.IDE.VERSIONS_CONSTANT.versionTitle();
//   
//   private Version version;
//
//   private FileModel activeFile;
//
//   private List<Version> versionHistory = new ArrayList<Version>();
//
//   private Version versionToOpenOnError;
//
//   private int ignoreErrorCount = 0;
//
//   private boolean isVersionPanelOpened = false;
//
//   private VersionContentForm view;
//
//   public VersionHistoryCommandHandler()
//   {
//      IDE.addHandler(OpenVersionEvent.TYPE, this);
//      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
//      IDE.addHandler(ShowNextVersionEvent.TYPE, this);
//      IDE.addHandler(ShowPreviousVersionEvent.TYPE, this);
//      IDE.addHandler(ViewOpenedEvent.TYPE, this);
//      IDE.addHandler(ViewClosedEvent.TYPE, this);
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.event.OpenVersionHandler#onOpenVersion(org.exoplatform.ide.client.versioning.event.OpenVersionEvent)
//    */
//   public void onOpenVersion(OpenVersionEvent event)
//   {
//      if (event.isShowVersionHistory())
//      {
//         getVersionHistory();
//      }
//      else
//      {
//         if (event.getVersion() == null && view != null)
//         {
//            IDE.getInstance().closeView(view.getId());
//         }
//         else
//         {
//            versionHistory = event.getVersionHistory();
//            openVersion(event.getVersion());
//         }
//      }
//   }
//
//   private void getVersionHistory()
//   {
//      if (activeFile != null)
//      {
//         versionHistory.clear();
//         //TODO
////         VirtualFileSystem.getInstance().getVersions(activeFile, new VersionsCallback()
////         {
////            @Override
////            protected void onSuccess(VersionsData result)
////            {
////               if (result.getVersions() != null && result.getVersions().size() > 0)
////               {
////                  versionHistory = result.getVersions();
////                  int index = 0;
////                  if (version != null)
////                  {
////                     index = getVersionIndexInList(version.getHref());
////                     index = (index > 0) ? index : 0;
////                  }
////                  openVersion(result.getVersions().get(index));
////               }
////               else
////               {
////                  Dialogs.getInstance().showInfo(
////                     org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES.versionHistoryItemHasNoVersions(result
////                        .getItem().getName()));
////               }
////            }
////
////            @Override
////            protected void onFailure(Throwable exception)
////            {
////               eventBus.fireEvent(new ExceptionThrownEvent(RECEIVE_VERSIONS_FAILURE));
////
////               if (versionToOpenOnError != null && ignoreErrorCount > 0)
////               {
////                  ignoreErrorCount--;
////                  getVersionContent(versionToOpenOnError);
////                  return;
////               }
////
////               eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));
////            }
////         });
//      }
//      else
//      {
//         Dialogs.getInstance().showInfo(PLEASE_OPEN_FILE);
//      }
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
//    */
//   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
//   {
//      activeFile = event.getFile();
//      if (view != null) {
//         IDE.getInstance().closeView(view.getId());
//         view = null;
//      }
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.event.ShowNextVersionHandler#onShowNextVersion(org.exoplatform.ide.client.versioning.event.ShowNextVersionEvent)
//    */
//   public void onShowNextVersion(ShowNextVersionEvent event)
//   {
//      if (versionHistory == null || versionHistory.size() <= 0)
//      {
//         return;
//      }
//      int currentIndex = getVersionIndexInList(version.getHref());
//      if ((currentIndex >= 1))
//      {
//         int nextIndex = currentIndex - 1;
//         getVersionContent(versionHistory.get(nextIndex));
//      }
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.event.ShowPreviousVersionHandler#onShowPreviousVersion(org.exoplatform.ide.client.versioning.event.ShowPreviousVersionEvent)
//    */
//   public void onShowPreviousVersion(ShowPreviousVersionEvent event)
//   {
//      if (versionHistory == null || versionHistory.size() <= 0)
//      {
//         return;
//      }
//
//      int currentIndex = getVersionIndexInList(version.getHref());
//      if ((currentIndex >= 0 && currentIndex < versionHistory.size() - 1))
//      {
//         getVersionContent(versionHistory.get(currentIndex + 1));
//      }
//   }
//
//   private void getVersionContent(Version version)
//   {
//      ignoreErrorCount = 3;
//      versionToOpenOnError = version;
//      IDE.fireEvent(new EnableStandartErrorsHandlingEvent(false));
//      VirtualFileSystem.getInstance().getContent(version, new FileCallback()
//      {
//         @Override
//         protected void onFailure(Throwable exception)
//         {
//            super.onFailure(exception);
//
//            if (versionToOpenOnError != null && ignoreErrorCount > 0)
//            {
//               ignoreErrorCount--;
//               getVersionContent(versionToOpenOnError);
//               return;
//            }
//
//            IDE.fireEvent(new EnableStandartErrorsHandlingEvent(false));
//         }
//
//         @Override
//         protected void onSuccess(File result)
//         {
//            if (result instanceof Version)
//            {
//               showVersion((Version)result);
//            }
//         }
//      });
//   }
//
//   private void showVersion(final Version versionToShow)
//   {
//      version = versionToShow;
//      if (!isVersionPanelOpened)
//      {
//         if (view == null)
//         {
//            view = new VersionContentForm(version);
//            view.setIcon(new Image(IDEImageBundle.INSTANCE.viewVersions()));
//            view.setTitle(VERSION_TITLE);
//            IDE.getInstance().openView(view);
//         }
//         else
//         {
//            view.setVersionContent(versionToShow.getContent());
//         }
//
//         Timer timer = new Timer()
//         {
//            @Override
//            public void run()
//            {
//               IDE.fireEvent(new ShowVersionContentEvent(versionToShow));
//            }
//         };
//         timer.schedule(2000);
//      }
//      else
//      {
//         IDE.fireEvent(new ShowVersionContentEvent(versionToShow));
//      }
//
//      IDE.fireEvent(new EnableStandartErrorsHandlingEvent());
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.event.ShowVersionListHandler#onShowVersionList(org.exoplatform.ide.client.versioning.event.ShowVersionListEvent)
//    */
//   public void onShowVersionList(ShowVersionListEvent event)
//   {
//      getVersionHistory();
//   }
//
//   private void openVersion(Version version)
//   {
//      getVersionContent(version);
//   }
//
//   private int getVersionIndexInList(String href)
//   {
//      if (versionHistory.size() <= 0)
//         return -1;
//      for (int i = 0; i < versionHistory.size(); i++)
//      {
//         if (versionHistory.get(i).getHref().equals(href))
//         {
//            return i;
//         }
//      }
//      return -1;
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent)
//    */
//   @Override
//   public void onViewOpened(ViewOpenedEvent event)
//   {
//      if (event.getView() instanceof VersionContentForm) {
//         isVersionPanelOpened = true;
//         IDE.addHandler(FileSavedEvent.TYPE, this);
//         IDE.addHandler(VersionRestoredEvent.TYPE, this);         
//      }
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
//    */
//   @Override
//   public void onViewClosed(ViewClosedEvent event)
//   {
//      if (event.getView() instanceof VersionContentForm)
//      {
//         isVersionPanelOpened = false;
//         version = null;
//         IDE.removeHandler(FileSavedEvent.TYPE, this);
//         IDE.removeHandler(VersionRestoredEvent.TYPE, this);
//         view = null;
//      }
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.framework.event.FileSavedHandler#onFileSaved(org.exoplatform.ide.client.framework.event.FileSavedEvent)
//    */
//   @Override
//   public void onFileSaved(FileSavedEvent event)
//   {
//      //TODO
////      if (version != null && event.getFile().getHref().equals(version.getItemHref()))
////      {
////         getVersionHistory();
////      }
//   }
//
//   /**
//    * @see org.exoplatform.ide.client.versioning.event.VersionRestoredHandler#onVersionRestored(org.exoplatform.ide.client.versioning.event.VersionRestoredEvent)
//    */
//   @Override
//   public void onVersionRestored(VersionRestoredEvent event)
//   {
//      //TODO
////      if (version != null && event.getFile().getHref().equals(version.getItemHref()))
////      {
////         getVersionHistory();
////      }
//   }
// }
