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
package org.exoplatform.ide.client.versioning;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Version;
import org.exoplatform.ide.client.framework.vfs.VersionsCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.versioning.event.OpenVersionEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionListEvent;
import org.exoplatform.ide.client.versioning.event.ShowVersionListHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

import java.util.List;

/**
 * Presenter for view of versions list.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class VersionsListPresenter implements ShowVersionListHandler, EditorActiveFileChangedHandler, ViewClosedHandler
{
   
   interface Display extends IsView
   {
      HasClickHandlers getOpenVersionButton();

      HasClickHandlers getCloseButton();

      ListGridItem<Version> getVersionsGrid();

      Version getSelectedVersion();

      void enableOpenVersionButton(boolean enable);
      
   }
   
   private static final String RECEIVE_VERSIONS_FAILURE = IDE.ERRORS_CONSTANT.versionsReceiveVersionsFailure();
   
   private static final String OPEN_FILE_TO_VIEW_VERSIONS = IDE.VERSIONS_CONSTANT.versionErrorOpenFile();
   
   private static final String TITLE = IDE.VERSIONS_CONSTANT.viewVersionsTitle();
   
   private FileModel activeFile;

   private HandlerManager eventBus;

   private List<Version> versionHistory;

   public VersionsListPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(ShowVersionListEvent.TYPE, this);
      eventBus.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   private Display display;

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getOpenVersionButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            openVersion();
         }
      });

      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });

      display.getVersionsGrid().addSelectionHandler(new SelectionHandler<Version>()
      {

         public void onSelection(SelectionEvent<Version> event)
         {
            boolean enableButtons = (display.getSelectedVersion() != null);
            display.enableOpenVersionButton(enableButtons);
         }
      });

      display.getVersionsGrid().addDoubleClickHandler(new DoubleClickHandler()
      {

         public void onDoubleClick(DoubleClickEvent event)
         {
            openVersion();
         }
      });

      display.enableOpenVersionButton(false);
      String title =
         ((activeFile != null) && (activeFile.getName() != null) && (activeFile.getName().length() > 0)) ? TITLE + " "
            + IDE.VERSIONS_CONSTANT.viewVersionsFor() + " " + activeFile.getName() : TITLE;

      display.asView().setTitle(title);
      display.getVersionsGrid().setValue(versionHistory);
   }

   /**
    * Open selected version in editor.
    */
   private void openVersion()
   {
      Version selectedVersion = display.getSelectedVersion();
      if (selectedVersion != null)
      {
         eventBus.fireEvent(new OpenVersionEvent(selectedVersion, versionHistory));
      }
      closeView();
   }
   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      activeFile = event.getFile();
   }

   /**
    * @see org.exoplatform.ide.client.versioning.event.ShowVersionListHandler#onShowVersionList(org.exoplatform.ide.client.versioning.event.ShowVersionListEvent)
    */
   @Override
   public void onShowVersionList(ShowVersionListEvent event)
   {
      getVersionHistory();
   }
   
   private void getVersionHistory()
   {
      //TODO
      if (activeFile != null && activeFile.isVersion())
      {
//         VirtualFileSystem.getInstance().getVersions(activeFile, new VersionsCallback()
//         {
//            @Override
//            protected void onSuccess(VersionsData result)
//            {
//               if (result.getVersions() != null && result.getVersions().size() > 0)
//               {
//                  versionHistory = result.getVersions();
//                  openView();
//               }
//               else
//               {
//                  Dialogs.getInstance().showInfo(
//                     IDE.IDE_LOCALIZATION_MESSAGES.showVersionListItemHasNoVersions(result.getItem().getName()));
//               }
//            }
//
//            @Override
//            protected void onFailure(Throwable exception)
//            {
//               eventBus.fireEvent(new ExceptionThrownEvent(RECEIVE_VERSIONS_FAILURE));
//               eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));               
//            }
//         });
      }
      else
      {
         Dialogs.getInstance().showInfo(IDE.VERSIONS_CONSTANT.versionsOpenFile());
      }
   }
   
   private void openView()
   {
      if (activeFile == null)
      {
         eventBus.fireEvent(new ExceptionThrownEvent(OPEN_FILE_TO_VIEW_VERSIONS));
         return;
      }
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView(d.asView());
         bindDisplay(d);
      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent("Display ViewVersions must be null"));
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }
}
