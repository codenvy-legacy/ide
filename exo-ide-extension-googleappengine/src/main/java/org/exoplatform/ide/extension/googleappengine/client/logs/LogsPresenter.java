/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.extension.googleappengine.client.logs;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.View;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineAsyncRequestCallback;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineClientService;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEngineExtension;
import org.exoplatform.ide.extension.googleappengine.client.GoogleAppEnginePresenter;
import org.exoplatform.ide.extension.googleappengine.client.model.StringUnmarshaller;

import java.util.LinkedHashMap;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: May 24, 2012 11:30:14 AM anya $
 * 
 */
public class LogsPresenter extends GoogleAppEnginePresenter implements ShowLogsHandler, ViewClosedHandler
{
   interface Display extends IsView
   {
      HasClickHandlers getLogsButton();

      HasValue<String> getDaysField();

      HasValue<String> getSeverityField();

      void setSeverities(LinkedHashMap<String, String> values);

      void setLogs(String content);
   }

   private Display display;

   private LinkedHashMap<String, String> severities = new LinkedHashMap<String, String>();

   {
      severities.put("", "All");
      severities.put("ERROR", "Error");
      severities.put("INFO", "Info");
      severities.put("WARN", "Warning");
      severities.put("DEBUG", "Debug");
      severities.put("CRITICAL", "Critical");
   }

   public LogsPresenter()
   {
      IDE.addHandler(ShowLogsEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay()
   {
      display.getLogsButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            getLogs();
         }
      });
   }

   /**
    * @see org.exoplatform.ide.extension.googleappengine.client.logs.ShowLogsHandler#onShowLogs(org.exoplatform.ide.extension.googleappengine.client.logs.ShowLogsEvent)
    */
   @Override
   public void onShowLogs(ShowLogsEvent event)
   {
      if (isAppEngineProject())
      {
         if (display == null)
         {
            display = GWT.create(Display.class);
            bindDisplay();
            IDE.getInstance().openView(display.asView());
            display.setSeverities(severities);
            display.getDaysField().setValue("1");
         }
         else
         {
            ((View)display).setViewVisible();
         }
         getLogs();
      }
      else
      {
         Dialogs.getInstance().showError(GoogleAppEngineExtension.GAE_LOCALIZATION.notAppEngineProjectError());
      }
   }

   public void getLogs()
   {
      String logSeverity = display.getSeverityField().getValue();
      int numDays = isCorrectValue() ? Integer.parseInt(display.getDaysField().getValue()) : 1;
      try
      {
         GoogleAppEngineClientService.getInstance().requestLogs(currentVfs.getId(), currentProject.getId(), numDays,
            logSeverity,
            new GoogleAppEngineAsyncRequestCallback<StringBuilder>(new StringUnmarshaller(new StringBuilder()))
            {
               @Override
               protected void onSuccess(StringBuilder result)
               {
                  display.setLogs(result.toString());
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

   /**
    * Checks, whether the value of number of days is entered correctly.
    * 
    * @return {@link Boolean} <code>true</code> if value is correct
    */
   private boolean isCorrectValue()
   {
      boolean enabled =
         display.getDaysField().getValue() != null && !display.getDaysField().getValue().trim().isEmpty();
      try
      {
         int value = Integer.parseInt(display.getDaysField().getValue());
         return enabled && value > 0;
      }
      catch (NumberFormatException e)
      {
         return false;
      }
   }
}
