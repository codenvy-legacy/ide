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
package org.exoplatform.ide.git.client.history;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitClientUtil;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.Messages;
import org.exoplatform.ide.git.client.marshaller.DiffResponse;
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.shared.DiffRequest.DiffType;
import org.exoplatform.ide.git.shared.Revision;

/**
 * Presenter for history view.
 * The view must be pointed in Views.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 29, 2011 2:57:17 PM anya $
 *
 */
public class HistoryPresenter extends GitPresenter implements ShowInHistoryHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      /**
       * Grid with revisions (commits).
       * 
       * @return {@link ListGridItem}
       */
      ListGridItem<Revision> getRevisionGrid();

      /**
       * Get diff text field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getDiffText();

      /**
       * Get click handler of the refresh button.
       * 
       * @return {@link HasValue} click handler
       */
      HasClickHandlers getRefreshButton();

      /**
       * Get click handler of the changes in project button.
       * 
       * @return {@link HasValue} click handler
       */
      HasClickHandlers getProjectChangesButton();

      /**
       * Get click handler of the resource's changes button.
       * 
       * @return {@link HasValue} click handler
       */
      HasClickHandlers getResourceChangesButton();

      /**
       * Change the selected state of the changes in project button.
       * 
       * @param selected selected state
       */
      void selectProjectChangesButton(boolean selected);

      /**
       * Change the selected state of the resource changes button.
       * 
       * @param selected selected state
       */
      void selectResourceChangesButton(boolean selected);
   }

   /**
    * Presenter's display.
    */
   private Display display;

   /**
    * If <code>true</code> then show all changes in project, 
    * if <code>false</code> then show changes of the selected resource.
    */
   private boolean showChangesInProject;

   /**
    * @param eventBus event handler
    */
   public HistoryPresenter(HandlerManager eventBus)
   {
      super(eventBus);
      eventBus.addHandler(ShowInHistoryEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getRevisionGrid().addSelectionHandler(new SelectionHandler<Revision>()
      {

         @Override
         public void onSelection(SelectionEvent<Revision> event)
         {
            if (event.getSelectedItem() != null)
            {
               getDiff(event.getSelectedItem());
            }
         }
      });

      display.getRefreshButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            getCommitsLog();
         }
      });

      display.getProjectChangesButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            if (showChangesInProject)
               return;
            showChangesInProject = true;
            display.selectProjectChangesButton(true);
            display.selectResourceChangesButton(false);
            getCommitsLog();
         }
      });

      display.getResourceChangesButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            if (!showChangesInProject)
               return;
            showChangesInProject = false;
            display.selectProjectChangesButton(false);
            display.selectResourceChangesButton(true);
            getCommitsLog();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.git.client.history.ShowInHistoryHandler#onShowInHistory(org.exoplatform.ide.git.client.history.ShowInHistoryEvent)
    */
   @Override
   public void onShowInHistory(ShowInHistoryEvent event)
   {
      getWorkDir();
   }

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#onWorkDirReceived()
    */
   @Override
   public void onWorkDirReceived()
   {
      getCommitsLog();
   }

   /**
    * Get the log of the commits. If successfully received, then display in revision grid, otherwise - show error in output panel.
    */
   private void getCommitsLog()
   {
      GitClientService.getInstance().log(workDir, false, new AsyncRequestCallback<LogResponse>()
      {

         @Override
         protected void onSuccess(LogResponse result)
         {
            if (display == null)
            {
               display = GWT.create(Display.class);
               bindDisplay();
               IDE.getInstance().openView(display.asView());
               display.selectProjectChangesButton(true);
               showChangesInProject = true;
            }
            display.getRevisionGrid().setValue(result.getCommits());
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

   /**
    * Get the changes between revisions. On success - display diff in text format, 
    * otherwise - show the error message in output panel.
    * 
    * @param revision revision
    */
   private void getDiff(Revision revision)
   {
      String[] filePatterns = null;
      if (!showChangesInProject && workDir != null && selectedItems != null && selectedItems.size() == 1)
      {
         String pattern = GitClientUtil.getFilePatternByHref(selectedItems.get(0).getHref(), workDir);
         if (pattern.length() > 0)
         {
            filePatterns = new String[]{pattern};
         }
      }

      GitClientService.getInstance().diff(workDir, filePatterns, DiffType.RAW, false,
         new AsyncRequestCallback<DiffResponse>()
         {

            @Override
            protected void onSuccess(DiffResponse result)
            {
               display.getDiffText().setValue(result.getDiffText());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : Messages.DIFF_FAILED;
               eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
            }
         });
   }
}
