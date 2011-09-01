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
package org.exoplatform.ide.client.welcome.samples;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.model.github.GithubService;
import org.exoplatform.ide.client.model.github.Repository;
import org.exoplatform.ide.vfs.shared.Item;

import java.util.List;

/**
 * Presenter to show the list of samples, that stored on github.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: GithubSamplesPresenter.java Aug 30, 2011 12:12:39 PM vereshchaka $
 *
 */
public class GithubSamplesPresenter implements GithubSamplesShowHandler, ViewClosedHandler, ItemsSelectedHandler
{
   
   public interface Display extends IsView
   {
      HasClickHandlers getImportButton();
      
      HasClickHandlers getCloseButton();
      
      ListGridItem<Repository> getSamplesListGrid();
      
      List<Repository> getSelectedItems();
      
      void enableImportButton(boolean enable);
   }
   
   private HandlerManager eventBus;
   
   private Display display;
   
   List<Repository> sampleRepos;
   
   List<Repository> selectedRepos;
   
   List<Item> selectedItems;
   
   public GithubSamplesPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(GithubSamplesShowEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
   }
   
   private void bindDisplay()
   {
      display.getImportButton().addClickHandler(new ClickHandler()
      {
         
         @Override
         public void onClick(ClickEvent event)
         {
            closeRepository(selectedRepos.get(0));
         }
      });
      
      display.getCloseButton().addClickHandler(new ClickHandler()
      {
         
         @Override
         public void onClick(ClickEvent event)
         {
            closeView();
         }
      });
      
      display.getSamplesListGrid().addSelectionHandler(new SelectionHandler<Repository>()
      {
         @Override
         public void onSelection(SelectionEvent<Repository> event)
         {
            selectedRepos = display.getSelectedItems();
            if (selectedRepos == null || selectedRepos.isEmpty())
            {
               display.enableImportButton(false);
            }
            else
            {
               display.enableImportButton(true);
            }
         }
      });
      
      display.getSamplesListGrid().setValue(sampleRepos);
      display.enableImportButton(false);
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
    * @see org.exoplatform.ide.client.welcome.samples.GithubSamplesShowHandler#onShowSamples(org.exoplatform.ide.client.welcome.samples.GithubSamplesShowEvent)
    */
   @Override
   public void onShowSamples(GithubSamplesShowEvent event)
   {
      GithubService.getInstance().getRepositoriesList(new AsyncRequestCallback<List<Repository>>()
      {
         @Override
         protected void onSuccess(List<Repository> result)
         {
            sampleRepos = result;
            openView();
         }
      });
   }
   
   private void closeRepository(Repository repo)
   {
      String workDir = selectedItems.get(0).getId();
      String remoteUri = repo.getUrl();
      if (!remoteUri.endsWith(".git"))
      {
         remoteUri += ".git";
      }
/*
      GitClientService.getInstance().cloneRepository(workDir, remoteUri, null, new AsyncRequestCallback<String>()
      {

         @Override
         protected void onSuccess(String result)
         {
            eventBus.fireEvent(new OutputEvent(GitExtension.MESSAGES.cloneSuccess(), Type.INFO));
            eventBus.fireEvent(new RefreshBrowserEvent());
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            String errorMessage =
               (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                  : GitExtension.MESSAGES.cloneFailed();
            eventBus.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
         }
      });
      */
   }
   
   private void openView()
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView((View)d);
         display = d;
         bindDisplay();
         return;
      }
      else
      {
         eventBus.fireEvent(new ExceptionThrownEvent("GithubSamples View must be null"));
      }
   }
   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());      
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler#onItemsSelected(org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent)
    */
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      this.selectedItems = event.getSelectedItems();
   }

}
