/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.git.client.reset;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.Messages;
import org.exoplatform.ide.git.client.marshaller.StatusResponse;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.git.client.remove.IndexFile;
import org.exoplatform.ide.git.shared.GitFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for view for reseting files from index.
 * The view must be pointed in Views.gwt.xml file.
 * 
 * When user tries to reset files from index:
 * 1. Find Git work directory by selected item in browser tree.
 * 2. Get status for found work directory.
 * 3. Display files ready for commit in grid.
 * (Checked items will be reseted from index).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 13, 2011 4:52:42 PM anya $
 *
 */
public class ResetFilesPresenter implements ItemsSelectedHandler, ResetFilesHandler
{
   interface Display extends IsView
   {
      /**
       * Get reset files button click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getResetButton();

      /**
       * Get cancel button click handler.
       * 
       * @return {@link HasClickHandlers} click handler
       */
      HasClickHandlers getCancelButton();

      /**
       * Get grid with Git files.
       * 
       * @return {@link ListGrid}
       */
      ListGrid<IndexFile> getIndexFilesGrid();
   }

   /**
    * Presenter' display.
    */
   private Display display;

   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * Selected items in browser tree.
    */
   private List<Item> selectedItems;

   /**
    * Git repository working directory.
    */
   private String workDir;

   /**
    * @param eventBus events handler
    */
   public ResetFilesPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(ResetFilesEvent.TYPE, this);
   }

   /**
    * Bind pointed display with presenter.
    * 
    * @param d display
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getResetButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doReset();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetFilesHandler#onResetFiles(org.exoplatform.ide.git.client.reset.ResetFilesEvent)
    */
   @Override
   public void onResetFiles(ResetFilesEvent event)
   {
      if (selectedItems == null || selectedItems.size() <= 0)
         return;

      GitClientService.getInstance().getWorkDir(selectedItems.get(0).getHref(),
         new AsyncRequestCallback<WorkDirResponse>()
         {

            @Override
            protected void onSuccess(WorkDirResponse result)
            {
               workDir = result.getWorkDir();
               workDir = workDir.endsWith("/.git") ? workDir.substring(0, workDir.lastIndexOf("/.git")) : workDir;

               getStatus(workDir);
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showInfo(Messages.NOT_GIT_REPOSITORY);
            }
         });
   }

   /**
    * Get the information about git files' states.
    * 
    * @param workDir
    */
   private void getStatus(final String workDir)
   {
      GitClientService.getInstance().status(workDir, new AsyncRequestCallback<StatusResponse>()
      {

         @Override
         protected void onSuccess(StatusResponse result)
         {
            if (result.getChangedNotCommited() == null || result.getChangedNotCommited().size() <= 0)
            {
               Dialogs.getInstance().showInfo(Messages.NOTHING_TO_COMMIT);
               return;
            }

            Display d = GWT.create(Display.class);
            IDE.getInstance().openView((View)d);
            bindDisplay(d);

            List<IndexFile> values = new ArrayList<IndexFile>();
            for (GitFile file : result.getChangedNotCommited())
            {
               values.add(new IndexFile(file, true));
            }

            display.getIndexFilesGrid().setValue(values);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMassage = (exception.getMessage() != null) ? exception.getMessage() : Messages.STATUS_FAILED;
            eventBus.fireEvent(new OutputEvent(errorMassage, Type.ERROR));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   /**
    * Reset checked files from index.
    */
   private void doReset()
   {
      List<String> files = new ArrayList<String>();
      for (IndexFile file : display.getIndexFilesGrid().getValue())
      {
         if (!file.isIndexed())
         {
            files.add(file.getPath());
         }
      }

      GitClientService.getInstance().reset(workDir, files.toArray(new String[files.size()]), "HEAD", null,
         new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               IDE.getInstance().closeView(display.asView().getId());
               eventBus.fireEvent(new OutputEvent(Messages.RESET_FILES_SUCCESSFULLY, Type.INFO));
               eventBus.fireEvent(new RefreshBrowserEvent());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               String errorMassage =
                  (exception.getMessage() != null) ? exception.getMessage() : Messages.RESET_FILES_FAILED;
               eventBus.fireEvent(new OutputEvent(errorMassage, Type.ERROR));
            }
         });
   }
}
