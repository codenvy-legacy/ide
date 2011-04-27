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
package org.exoplatform.ide.git.client.remove;

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
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.Messages;
import org.exoplatform.ide.git.client.marshaller.StatusResponse;
import org.exoplatform.ide.git.shared.GitFile;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for view for removing files from working tree and Git index.
 * The view must be pointed in Views.gwt.xml file.
 * 
 * When user tries to remove files from index:
 * 1. Find Git work directory by selected item in browser tree.
 * 2. Get status for found work directory.
 * 3. Display files ready for commit in grid.
 * (Checked items will be removed from index).
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Apr 12, 2011 4:03:44 PM anya $
 *
 */
public class RemoveFilesPresenter extends GitPresenter implements RemoveFilesHandler
{
   interface Display extends IsView
   {
      /**
       *Get click handler for remove button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getRemoveButton();

      /**
       *Get click handler for cancel button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();

      /**
       * Get grid for displaying Git files.
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
    * @param eventBus event handlers
    */
   public RemoveFilesPresenter(HandlerManager eventBus)
   {
      super(eventBus);

      eventBus.addHandler(RemoveFilesEvent.TYPE, this);
   }

   /**
    * Bind pointed display with presenter.
    * 
    * @param d display
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getRemoveButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doRemove();
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
    * @see org.exoplatform.ide.git.client.remove.RemoveFilesHandler#onRemoveFiles(org.exoplatform.ide.git.client.remove.RemoveFilesEvent)
    */
   @Override
   public void onRemoveFiles(RemoveFilesEvent event)
   {
      getWorkDir();
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
            IDE.getInstance().openView(d.asView());
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
    * Perform removing files from working directory and Git index.
    */
   private void doRemove()
   {
      List<String> files = new ArrayList<String>();
      for (IndexFile file : display.getIndexFilesGrid().getValue())
      {
         if (!file.isIndexed())
         {
            files.add(file.getPath());
         }
      }

      GitClientService.getInstance().remove(workDir, files.toArray(new String[files.size()]),
         new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               IDE.getInstance().closeView(display.asView().getId());
               eventBus.fireEvent(new RefreshBrowserEvent());
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               String errorMassage =
                  (exception.getMessage() != null) ? exception.getMessage() : Messages.REMOVE_FILES_FAILED;
               eventBus.fireEvent(new OutputEvent(errorMassage, Type.ERROR));
            }
         });
   }

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#onWorkDirReceived()
    */
   @Override
   public void onWorkDirReceived()
   {
      getStatus(workDir);
   }
}
