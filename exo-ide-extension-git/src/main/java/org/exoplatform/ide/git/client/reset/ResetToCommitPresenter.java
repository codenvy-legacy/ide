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
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.component.ListGrid;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.ViewEx;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.Messages;
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.git.shared.ResetRequest.ResetType;
import org.exoplatform.ide.git.shared.Revision;

import java.util.List;

/**
 * Presenter for view for reseting head to commit.
 * The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 15, 2011 10:31:25 AM anya $
 *
 */
public class ResetToCommitPresenter implements ItemsSelectedHandler, ResetToCommitHandler
{
   interface Display extends IsView
   {
      /**
       * Get reset button click handler.
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
       * Get grid with revisions.
       * 
       * @return {@link ListGrid}
       */
      ListGridItem<Revision> getRevisionGrid();

      /**
       * Get selected revision in revision grid.
       * 
       * @return {@link Revision} selected revision
       */
      Revision getSelectedRevision();

      /**
       * Get the soft mode radio field.
       * 
       * @return {@link HasValue}
       */
      HasValue<Boolean> getSoftMode();
      
      /**
      * Get the mix mode radio field.
      * 
      * @return {@link HasValue}
      */
      HasValue<Boolean> getMixMode();
      
      /**
       * Get the hard mode radio field.
       * 
       * @return {@link HasValue}
       */
      HasValue<Boolean> getHardMode();

      /**
       * Set the enabled state of the reset button.
       * 
       * @param enabled enabled state
       */
      void enableResetButon(boolean enabled);
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
    * @param eventBus event handlers
    */
   public ResetToCommitPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ResetToCommitEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getResetButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doReset();
         }
      });

      display.getRevisionGrid().addSelectionHandler(new SelectionHandler<Revision>()
      {

         @Override
         public void onSelection(SelectionEvent<Revision> event)
         {
            boolean enable = event.getSelectedItem() != null;
            display.enableResetButon(enable);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.reset.ResetToCommitHandler#onResetToCommit(org.exoplatform.ide.git.client.reset.ResetToCommitEvent)
    */
   @Override
   public void onResetToCommit(ResetToCommitEvent event)
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

               getCommits();
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               Dialogs.getInstance().showInfo(Messages.NOT_GIT_REPOSITORY);
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

   private void getCommits()
   {
      GitClientService.getInstance().log(workDir, false, new AsyncRequestCallback<LogResponse>()
      {

         @Override
         protected void onSuccess(LogResponse result)
         {
            Display d = GWT.create(Display.class);
            IDE.getInstance().openView((ViewEx)d);
            bindDisplay(d);

            display.getRevisionGrid().setValue(result.getCommits());
            display.getMixMode().setValue(true);
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : Messages.LOG_FAILED;
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
   }

   /**
    * Reset the head to selected revision with chosen mode (hard, soft or mixed).
    */
   private void doReset()
   {
      Revision revision = display.getSelectedRevision();
      ResetType type = display.getMixMode().getValue() ? ResetType.MIXED : null;
      type = (type == null && display.getSoftMode().getValue()) ? ResetType.SOFT : type;
      type = (type == null && display.getHardMode().getValue()) ? ResetType.HARD : type;

      GitClientService.getInstance().reset(workDir, null, revision.getId(), type, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            eventBus.fireEvent(new OutputEvent(Messages.RESET_SUCCESSFULLY, Type.INFO));
            IDE.getInstance().closeView(display.asView().getId());
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : Messages.RESET_FAIL;
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
   }
}
