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
package org.exoplatform.ide.git.client.commit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
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
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.git.shared.Revision;

import java.util.Date;
import java.util.List;

/**
 * Presenter for commit view. 
 * The view must implement {@link CommitPresenter.Display} interface
 * and pointed in Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 31, 2011 10:02:25 AM anya $
 *
 */
public class CommitPresenter implements ItemsSelectedHandler, CommitHandler
{
   public interface Display extends IsView
   {
      /**
       * Get commit button handler.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCommitButton();
      
      /**
       * Get cancel button handler.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();

      /**
       * Get message field value.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getMessage();

      /**
       * Change the enable state of the commit button.
       * 
       * @param enable enabled or not
       */
      void enableCommitButton(boolean enable);

      /**
       * Give focus to message field.
       */
      void focusInMessageField();
   }

   /**
    * Display.
    */
   private Display display;

   /**
    * Events handler.
    */
   private HandlerManager eventBus;

   /**
    * Selected items in the workspace tree.
    */
   private List<Item> selectedItems;

   /**
    * The working directory of the Git repository.
    */
   private String workDir;

   /**
    * @param eventBus events handler
    */
   public CommitPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(CommitEvent.TYPE, this);
   }

   /**
    * Bind display(view) with presenter. 
    * 
    * @param d display
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getCommitButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doCommit();
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

      display.getMessage().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            boolean notEmpty = (event.getValue() != null && event.getValue().length() > 0);
            display.enableCommitButton(notEmpty);
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
    * @see org.exoplatform.ide.git.client.commit.CommitHandler#onCommit(org.exoplatform.ide.git.client.commit.CommitEvent)
    */
   @Override
   public void onCommit(CommitEvent event)
   {
      if (selectedItems == null || selectedItems.size() <= 0)
         return;

      getWorkDir(selectedItems.get(0).getHref());
   }

   /**
    * Get the location of the Git working directory, starting 
    * from pointed href.
    * 
    * @param href
    */
   private void getWorkDir(String href)
   {
      GitClientService.getInstance().getWorkDir(href, new AsyncRequestCallback<WorkDirResponse>()
      {

         @Override
         protected void onSuccess(WorkDirResponse result)
         {
            workDir = result.getWorkDir();

            Display d = GWT.create(Display.class);
            IDE.getInstance().openView((ViewEx)d);
            bindDisplay(d);
            //Commit button is disabled, because message is empty:
            display.enableCommitButton(false);
            display.focusInMessageField();
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            Dialogs.getInstance().showInfo(Messages.NOT_GIT_REPOSITORY);
         }
      });
   }

   /**
    * Perform the commit to repository and process the response.
    */
   private void doCommit()
   {
      if (workDir == null)
         return;
      workDir = (workDir.endsWith("/.git")) ? workDir.substring(0, workDir.lastIndexOf("/.git")) : workDir;

      String message = display.getMessage().getValue();

      GitClientService.getInstance().commit(workDir, message, new AsyncRequestCallback<Revision>()
      {
         @Override
         protected void onSuccess(Revision result)
         {
            Date date = new Date(result.getCommitTime());
            String message = "Commited with revision # <b>" + result.getId() + "</b> at time " + date.toString();
            message +=
               (result.getCommitter() != null && result.getCommitter().getName() != null && result.getCommitter()
                  .getName().length() > 0) ? " by user " + result.getCommitter().getName() : "";
            eventBus.fireEvent(new OutputEvent(message, Type.INFO));
            eventBus.fireEvent(new RefreshBrowserEvent());
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                  : Messages.COMMIT_FAILED;
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
      IDE.getInstance().closeView(display.asView().getId());
   }
}
