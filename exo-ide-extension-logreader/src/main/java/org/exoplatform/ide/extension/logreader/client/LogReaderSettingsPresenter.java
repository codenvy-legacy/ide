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

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.extension.logreader.client.event.LogReaderSettingsChangedEvent;

import java.util.Date;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class LogReaderSettingsPresenter implements ViewClosedHandler
{
   public interface Display extends IsView
   {
      String ID = "ideLogReaderSettingsView";

      HasValue<Date> getDateField();

      HasValue<String> getOffsetField();

      HasValue<String> getLimitField();

      HasClickHandlers getOkButton();

      HasClickHandlers getCancelButton();

      void showErrorMessage(String message);

      void clearErrorMessage();

      void setOkButtonEnabled(boolean enabled);
   }

   private Date date;

   private int limit;

   private int offset;

   private Display display;

   /**
    * @param date
    * @param limit
    * @param offset
    */
   public LogReaderSettingsPresenter(Date date, int limit, int offset)
   {
      super();
      this.date = date;
      this.limit = limit;
      this.offset = offset;
      IDE.EVENT_BUS.addHandler(ViewClosedEvent.TYPE, this);

      display = GWT.create(Display.class);
      bind();
      IDE.getInstance().openView(display.asView());
   }

   private void checkFields()
   {
      try
      {
         Integer.parseInt(display.getLimitField().getValue());
         display.clearErrorMessage();
         display.setOkButtonEnabled(true);
      }
      catch (NumberFormatException e)
      {
         display.setOkButtonEnabled(false);
         display.showErrorMessage(LogReaderExtension.MESSAGES.getLimit() + "not a number");
         return;
      }

      try
      {
         Integer.parseInt(display.getOffsetField().getValue());
         display.clearErrorMessage();
         display.setOkButtonEnabled(true);

      }
      catch (NumberFormatException e)
      {
         display.setOkButtonEnabled(false);
         display.showErrorMessage(LogReaderExtension.MESSAGES.getOffset() + "not a number");
      }
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
            IDE.getInstance().closeView(Display.ID);
         }
      });

      display.getOkButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            IDE.EVENT_BUS.fireEvent(new LogReaderSettingsChangedEvent(display.getDateField().getValue(), Integer
               .valueOf(display.getLimitField().getValue()), Integer.valueOf(display.getOffsetField().getValue())));
            IDE.getInstance().closeView(Display.ID);
         }
      });

      display.getDateField().setValue(date);
      display.getLimitField().setValue(String.valueOf(limit));
      display.getOffsetField().setValue(String.valueOf(offset));

      display.getLimitField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            checkFields();
         }
      });

      display.getOffsetField().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            checkFields();
         }
      });
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
         IDE.EVENT_BUS.removeHandler(ViewClosedEvent.TYPE, this);
      }
   }

}
