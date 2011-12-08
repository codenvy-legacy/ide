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
package org.exoplatform.ide.git.client.init;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Presenter for Init Repository view.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 24, 2011 9:07:58 AM anya $
 *
 */
public class ShowProjectGitReadOnlyUrlPresenter extends GitPresenter implements ShowProjectGitReadOnlyUrlHandler
{
   public interface Display extends IsView
   {
      /**
       * Get's Git URl field field.
       * 
       * @return {@link HasValue}
       */
      HasValue<String> getGitUrl();

      /**
       * Gets cancel button.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getCloseButton();
   }

   private Display display;

   /**
    * @param eventBus
    */
   public ShowProjectGitReadOnlyUrlPresenter()
   {
      IDE.addHandler(ShowProjectGitReadOnlyUrlEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.getCloseButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });
   }


   @Override
   public void onShowGitUrl(ShowProjectGitReadOnlyUrlEvent event)
   {
      Display d = GWT.create(Display.class);
      IDE.getInstance().openView((View)d);
      bindDisplay(d);
      String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
      try
      {
         GitClientService.getInstance().getGitReadOnlyUrl(
            vfs.getId(),
            projectId,
            new org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback<StringBuilder>(
               new StringUnmarshaller(new StringBuilder()))
            {

               @Override
               protected void onSuccess(StringBuilder result)
               {
                  display.getGitUrl().setValue(result.toString());
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  String errorMessage =
                     (exception.getMessage() != null && exception.getMessage().length() > 0) ? exception.getMessage()
                        : GitExtension.MESSAGES.initFailed();
                  IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
               }
            });
      }
      catch (RequestException e)
      {
         String errorMessage =
            (e.getMessage() != null && e.getMessage().length() > 0) ? e.getMessage() : GitExtension.MESSAGES
               .initFailed();
         IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
      }
   }

   private class StringUnmarshaller implements
      org.exoplatform.gwtframework.commons.rest.copy.Unmarshallable<StringBuilder>
   {

      protected StringBuilder builder;

      /**
       * @param callback
       */
      public StringUnmarshaller(StringBuilder builder)
      {
         this.builder = builder;
      }

      /**
       * @see org.exoplatform.gwtframework.commons.rest.Unmarshallable#unmarshal(com.google.gwt.http.client.Response)
       */
      @Override
      public void unmarshal(Response response)
      {
         builder.append(response.getText());
      }

      @Override
      public StringBuilder getPayload()
      {
         return builder;
      }
   }
}
