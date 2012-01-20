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
package org.exoplatform.ide.extension.logreader.client;

import com.google.gwt.http.client.RequestException;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderEvent;
import org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderHandler;
import org.exoplatform.ide.extension.logreader.client.model.LogEntry;
import org.exoplatform.ide.extension.logreader.client.model.LogReaderService;
import org.exoplatform.ide.extension.logreader.client.model.marshal.LogReaderUnmarshaller;
import org.exoplatform.ide.extension.logreader.client.ui.LogReaderView;

/**
 * Presenter for {@link LogReaderView}
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 * 
 */
public class LogReaderPresenter implements ShowLogReaderHandler, ViewClosedHandler
{
   public interface Display extends IsView
   {
      String ID = "ideExtensionLogReaderView";

      HasClickHandlers getNexLogButton();

      HasClickHandlers getPrevLogButton();

      HasClickHandlers getRefreshLogButton();

      void addLog(String logContent);

      void setPrevLogButtonEnabled(boolean enabled);

      void setNextLogButtonEnabled(boolean enabled);

   }

   private Display display;

   private String currentToken;

   /**
    * 
    */
   public LogReaderPresenter()
   {
      IDE.addHandler(ShowLogReaderEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderHandler#onShowlogReader(org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderEvent)
    */
   @Override
   public void onShowlogReader(ShowLogReaderEvent event)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bind();
         IDE.getInstance().openView(display.asView());
         getLogs();
      }
      else
      {
         display.asView().setViewVisible();
      }

   }

   /**
    * Bind view to presenter
    */
   private void bind()
   {

      display.getNexLogButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            getNextLog();
         }
      });

      display.getPrevLogButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            prevLog();
         }
      });

      display.getRefreshLogButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            refreshLog();
         }
      });
   }

   /**
    * 
    */
   private void refreshLog()
   {
      if (currentToken == null)
      {
         getLogs();
         return;
      }
      try
      {
         LogReaderService.get().getLog(currentToken,
            new AsyncRequestCallback<LogEntry>(new LogReaderUnmarshaller(new LogEntry()))
            {

               @Override
               protected void onSuccess(LogEntry result)
               {
                  display.addLog(result.getContent());
                  updateButtonState(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Get previous log
    */
   private void prevLog()
   {
      try
      {
         LogReaderService.get().getPrevLog(currentToken,
            new AsyncRequestCallback<LogEntry>(new LogReaderUnmarshaller(new LogEntry()))
            {

               @Override
               protected void onSuccess(LogEntry result)
               {
                  currentToken = result.getToken();
                  display.addLog(result.getContent());
                  updateButtonState(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }

            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Update log navigation control enabling
    * 
    * @param log
    */
   private void updateButtonState(LogEntry log)
   {
      display.setNextLogButtonEnabled(log.isHasNext());
      display.setPrevLogButtonEnabled(log.isHasPrevious());
   }

   /**
    * Send request to LogReader service
    */
   private void getLogs()
   {
      try
      {
         LogReaderService.get().getLastLog(
            new AsyncRequestCallback<LogEntry>(new LogReaderUnmarshaller(new LogEntry()))
            {

               @Override
               protected void onSuccess(LogEntry result)
               {
                  display.addLog(result.getContent());
                  currentToken = result.getToken();
                  updateButtonState(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }

            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   /**
    * Get next log
    */
   private void getNextLog()
   {
      try
      {
         LogReaderService.get().getNextLog(currentToken,
            new AsyncRequestCallback<LogEntry>(new LogReaderUnmarshaller(new LogEntry()))
            {

               @Override
               protected void onSuccess(LogEntry result)
               {
                  currentToken = result.getToken();
                  display.addLog(result.getContent());
                  updateButtonState(result);
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
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
}
