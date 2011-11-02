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
package org.exoplatform.ide.extension.heroku.client.stack;

import java.util.List;

import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.StackListAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.StackMigrationAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.StackMigrationResponse;
import org.exoplatform.ide.extension.heroku.shared.Stack;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;

/**
 * Presenter for changing Heroku application's stack.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jul 29, 2011 9:41:46 AM anya $
 *
 */
public class ChangeStackPresenter extends GitPresenter implements ViewClosedHandler, ChangeApplicationStackHandler,
   LoggedInHandler
{
   interface Display extends IsView
   {
      ListGridItem<Stack> getStackGrid();

      HasClickHandlers getChangeButton();

      HasClickHandlers getCancelButton();

      void enableChangeButton(boolean enable);

      Stack getSelectedStack();
   }

   /**
    * Presenter's view.
    */
   private Display display;

   /**
    *
    */
   public ChangeStackPresenter()
   {
      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ChangeApplicationStackEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getStackGrid().addSelectionHandler(new SelectionHandler<Stack>()
      {

         @Override
         public void onSelection(SelectionEvent<Stack> event)
         {
            display.enableChangeButton(event.getSelectedItem() != null && !event.getSelectedItem().isCurrent());
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

      display.getChangeButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doMigrateStack();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.stack.ChangeApplicationStackHandler#onChangeApplicationStack(org.exoplatform.ide.extension.heroku.client.stack.ChangeApplicationStackEvent)
    */
   @Override
   public void onChangeApplicationStack(ChangeApplicationStackEvent event)
   {
      if (makeSelectionCheck())
      {
         getStacks();
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

   public void getStacks()
   {
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      HerokuClientService.getInstance().getStackList(null, vfs.getId(), projectId, new StackListAsyncRequestCallback(IDE.eventBus(), this)
      {
         @Override
         protected void onSuccess(List<Stack> result)
         {
            if (display == null)
            {
               display = GWT.create(Display.class);
               bindDisplay();
               IDE.getInstance().openView(display.asView());
            }
            display.enableChangeButton(false);
            display.getStackGrid().setValue(result, true);
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler#onLoggedIn(org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent)
    */
   @Override
   public void onLoggedIn(LoggedInEvent event)
   {
      IDE.removeHandler(LoggedInEvent.TYPE, this);
      if (!event.isFailed())
      {
         getStacks();
      }
   }

   /**
    * Perform stack migration.
    */
   public void doMigrateStack()
   {
      Stack stack = display.getSelectedStack();
      if (stack == null)
         return;
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      HerokuClientService.getInstance().migrateStack(null, vfs.getId(), projectId, stack.getName(),
         new StackMigrationAsyncRequestCallback(IDE.eventBus(), this)
         {

            @Override
            protected void onSuccess(StackMigrationResponse result)
            {
               IDE.getInstance().closeView(display.asView().getId());
               String output =
                  (result.getResult() != null && !result.getResult().isEmpty()) ? result.getResult().replace("\n",
                     "<br>") : "";
               IDE.fireEvent(new OutputEvent(output, Type.INFO));
            }
         });
   }

}
