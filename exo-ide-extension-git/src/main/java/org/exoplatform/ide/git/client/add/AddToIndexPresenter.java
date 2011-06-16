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
package org.exoplatform.ide.git.client.add;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitClientUtil;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;

/**
 * Presenter for add changes to index view.
 * The view must implement  {@link AddToIndexPresenter.Display}.
 * Add view to View.gwt.xml.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 29, 2011 4:35:16 PM anya $
 *
 */
public class AddToIndexPresenter extends GitPresenter implements AddFilesHandler
{
   public interface Display extends IsView
   {
      /**
       * Get add button click handler.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getAddButton();

      /**
       * Get cancel button click handler.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCancelButton();

      /**
       * Get update field value.
       * 
       * @return {@link HasValue}
       */
      HasValue<Boolean> getUpdateValue();

      /**
       * Get message label value.
       * 
       *  @return {@link HasValue} 
       */
      HasValue<String> getMessage();
   }

   /**
    * Presenter's display.
    */
   private Display display;

   /**
    * @param eventBus events handler
    */
   public AddToIndexPresenter(HandlerManager eventBus)
   {
      super(eventBus);
      eventBus.addHandler(AddFilesEvent.TYPE, this);
   }

   /**
    * @param d display
    */
   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getAddButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            doAdd();
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
    * @see org.exoplatform.ide.git.client.add.AddFilesHandler#onAddFiles(org.exoplatform.ide.git.client.add.AddFilesEvent)
    */
   @Override
   public void onAddFiles(AddFilesEvent event)
   {
      getWorkDir();
   }

   /**
    * Form the message to display for adding to index, telling the user what 
    * is gonna to be added.
    * 
    * @return {@link String} message to display
    */
   private String formMessage()
   {
      if (selectedItems == null || selectedItems.size() <= 0)
         return "";
      Item selectedItem = selectedItems.get(0);

      String pattern = GitClientUtil.getFilePatternByHref(selectedItem.getHref(), workDir);

      //Root of the working tree:
      if (pattern.length() == 0 || "/".equals(pattern))
      {
         
         return GitExtension.MESSAGES.addToIndexAllChanges();
      }

      if (selectedItem instanceof Folder)
      {
         return GitExtension.MESSAGES.addToIndexFolder(pattern);
      }
      else
      {
         return GitExtension.MESSAGES.addToIndexFile(pattern);
      }
      
   }

   /**
    * Perform adding to index.
    */
   private void doAdd()
   {
      if (workDir == null || selectedItems == null || selectedItems.size() <= 0)
         return;
      boolean update = display.getUpdateValue().getValue();
      String pattern = GitClientUtil.getFilePatternByHref(selectedItems.get(0).getHref(), workDir);
      String[] filePatterns =
         (pattern.length() == 0 || "/".equals(pattern)) ? new String[]{"."} : new String[]{pattern};

      GitClientService.getInstance().add(workDir, update, filePatterns, new AsyncRequestCallback<String>()
      {
         @Override
         protected void onSuccess(String result)
         {
            eventBus.fireEvent(new OutputEvent(GitExtension.MESSAGES.addSuccess()));
            eventBus.fireEvent(new RefreshBrowserEvent());
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                  : GitExtension.MESSAGES.addFailed();
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });

      IDE.getInstance().closeView(display.asView().getId());
   }

   /**
    * @see org.exoplatform.ide.git.client.GitPresenter#onWorkDirReceived()
    */
   @Override
   public void onWorkDirReceived()
   {
      Display d = GWT.create(Display.class);
      IDE.getInstance().openView(d.asView());
      bindDisplay(d);

      display.getMessage().setValue(formMessage(), true);
   }
}
