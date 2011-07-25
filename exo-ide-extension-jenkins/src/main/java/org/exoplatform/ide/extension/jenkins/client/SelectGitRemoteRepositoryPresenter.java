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
package org.exoplatform.ide.extension.jenkins.client;

import com.google.gwt.event.shared.HandlerRegistration;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.jenkins.client.event.GitRemoteRepositorySelectedEvent;

/**
 * Presenter for select project git remote repository.
 * Calls only if git has more then 1 remote repository.
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class SelectGitRemoteRepositoryPresenter implements ViewClosedHandler
{
   public interface Display extends IsView
   {
      HasClickHandlers getBuildButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getGitRepository();

      void setGitRepositoryValues(String[] values);
   }

   private Display display;

   private HandlerRegistration addHandler;

   /**
    * 
    */
   public SelectGitRemoteRepositoryPresenter(String[] remotes)
   {
      addHandler = IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
      display = GWT.create(Display.class);
      bind();
      IDE.getInstance().openView(display.asView());
   }

   /**
    * 
    */
   private void bind()
   {
      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getBuildButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            remoteRepositorySelected();
         }
      });
   }

   /**
    * 
    */
   private void remoteRepositorySelected()
   {
      IDE.EVENT_BUS.fireEvent(new GitRemoteRepositorySelectedEvent(display.getGitRepository().getValue()));
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
         addHandler.removeHandler();
      }
   }

}
