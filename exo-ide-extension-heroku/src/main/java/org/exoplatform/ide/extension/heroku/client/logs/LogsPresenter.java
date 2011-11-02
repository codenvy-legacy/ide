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
package org.exoplatform.ide.extension.heroku.client.logs;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.heroku.client.HerokuClientService;
import org.exoplatform.ide.extension.heroku.client.LogsAsyncRequestCallback;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInEvent;
import org.exoplatform.ide.extension.heroku.client.login.LoggedInHandler;
import org.exoplatform.ide.extension.heroku.client.marshaller.LogsResponse;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.vfs.client.model.ItemContext;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * Presenter for application's logs view.
 * View must be pointed in Views.gwt.xml file.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Sep 19, 2011 2:28:02 PM anya $
 *
 */
public class LogsPresenter extends GitPresenter implements ShowLogsHandler, LoggedInHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {
      HasClickHandlers getShowLogButton();

      void addLog(String logContent);

      TextFieldItem getLogLinesCount();

      void enableShowLogButton(boolean enable);

      void focusInLogLinesField();
   }

   /**
    * Presenter's display.
    */
   private Display display;

   /**
    *
    */
   public LogsPresenter()
   {
      IDE.addHandler(ShowLogsEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * Bind display with presenter.
    */
   public void bindDisplay()
   {
      display.getLogLinesCount().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            display.enableShowLogButton(isCorrectValue());
         }
      });

      display.getShowLogButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            getLogs();
         }
      });

      display.getLogLinesCount().addKeyUpHandler(new KeyUpHandler()
      {
         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            if (event.getNativeKeyCode() == 13 && isCorrectValue())
            {
               getLogs();
            }
         }
      });
   }

   /**
    * Checks, whether the value of log lines max count is entered correctly.
    * 
    * @return {@link Boolean} <code>true</code> if value is correct
    */
   private boolean isCorrectValue()
   {
      boolean enabled =
         display.getLogLinesCount().getValue() != null && !display.getLogLinesCount().getValue().trim().isEmpty();
      try
      {
         int value = Integer.parseInt(display.getLogLinesCount().getValue());
         return enabled && value > 0 && value <= 500;
      }
      catch (NumberFormatException e)
      {
         return false;
      }
   }

   /**
    * @see org.exoplatform.ide.extension.heroku.client.logs.ShowLogsHandler#onShowLogs(org.exoplatform.ide.extension.heroku.client.logs.ShowLogsEvent)
    */
   @Override
   public void onShowLogs(ShowLogsEvent event)
   {
      if (makeSelectionCheck())
      {
         getLogs();
      }
   }

   /**
    * Get the application's logs.
    */
   protected void getLogs()
   {
      int logLines =
         (display != null && isCorrectValue()) ? Integer.parseInt(display.getLogLinesCount().getValue()) : 0;
         String projectId = ((ItemContext)selectedItems.get(0)).getProject().getId();
         HerokuClientService.getInstance().logs(null, vfs.getId(), projectId, logLines, new LogsAsyncRequestCallback(IDE.eventBus(), this)
      {
         @Override
         protected void onSuccess(LogsResponse result)
         {
            showLogs(result.getLogs());
         }
      });

   }

   /**
    * Display logs content.
    * 
    * @param logContent content of the logs
    */
   protected void showLogs(String logContent)
   {
      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();
         IDE.getInstance().openView(display.asView());
         display.enableShowLogButton(false);
         display.focusInLogLinesField();
      }
      display.addLog(logContent);
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
         getLogs();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
         display = null;
   }
}
