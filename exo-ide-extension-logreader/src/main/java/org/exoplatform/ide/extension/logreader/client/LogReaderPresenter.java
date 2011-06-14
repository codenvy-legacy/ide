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

import com.google.gwt.user.client.Command;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.logreader.client.event.LogReaderSettingsChangedEvent;
import org.exoplatform.ide.extension.logreader.client.event.LogReaderSettingsChangedHandler;
import org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderEvent;
import org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderHandler;
import org.exoplatform.ide.extension.logreader.client.model.LogReaderService;
import org.exoplatform.ide.extension.logreader.client.ui.LogReaderView;

import java.util.Date;

/**
 * Presenter for {@link LogReaderView}
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class LogReaderPresenter implements ShowLogReaderHandler, ViewClosedHandler, LogReaderSettingsChangedHandler
{
   public interface Display extends IsView
   {
      String ID = "ideExtensionLogReaderView";

      HasClickHandlers getLogButton();

      HasClickHandlers getClearLogButton();

      HasClickHandlers getSettingsButton();

      void addLogs(String logs);

      void clearLogs();

      void setNextButtonText(String text);

      void addNextButtonCommand(Command command);

   }

   private Display display;

   private int offset;

   private int limit;

   private Date date = new Date();

   /**
    * 
    */
   public LogReaderPresenter()
   {
      IDE.EVENT_BUS.addHandler(ShowLogReaderEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);
      IDE.EVENT_BUS.addHandler(LogReaderSettingsChangedEvent.TYPE, this);
      date.setTime(0);

   }

   /**
    * @see org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderHandler#onShowlogReader(org.exoplatform.ide.extension.logreader.client.event.ShowLogReaderEvent)
    */
   @Override
   public void onShowlogReader(ShowLogReaderEvent event)
   {
      offset = 0;
      date.setTime(0);
      limit = 10;
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

      display.addNextButtonCommand(new Command()
      {

         @Override
         public void execute()
         {
            offset += limit;
            getLogs();
         }
      });

      display.getClearLogButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            display.clearLogs();
         }
      });

      display.getSettingsButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            new LogRederSettingsPresenter(date, limit, offset);
         }
      });

      display.setNextButtonText(LogReaderExtension.MESSAGES.getNextButtonMessage(limit));
   }

   /**
    * Send request to LogReader service
    */
   private void getLogs()
   {
      try
      {
         LogReaderService.get().getLogs(date.getTime(), limit, offset, new AsyncRequestCallback<String>()
         {

            @Override
            protected void onSuccess(String result)
            {
               if (result.isEmpty())
                  return;
               display.addLogs(result);
            }
         });
      }
      catch (NumberFormatException e)
      {

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

   /**
    * @see org.exoplatform.ide.extension.logreader.client.event.LogReaderSettingsChangedHandler#onLogRederSettingsChanged(org.exoplatform.ide.extension.logreader.client.event.LogReaderSettingsChangedEvent)
    */
   @Override
   public void onLogRederSettingsChanged(LogReaderSettingsChangedEvent event)
   {
      this.date = event.getDate();
      this.limit = event.getLimit();
      this.offset = event.getOffset();
      display.setNextButtonText(LogReaderExtension.MESSAGES.getNextButtonMessage(limit));
   }
}
