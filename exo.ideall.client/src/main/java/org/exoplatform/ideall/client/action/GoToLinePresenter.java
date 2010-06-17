/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ideall.client.action;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ideall.client.editor.event.EditorGoToLineEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GoToLinePresenter
{

   public interface Display
   {
      HasValue<String> getLineNumberField();

      HasClickHandlers getGoButton();

      HasClickHandlers getCancelButton();

      void closeForm();
      
      void setCaptionLabel(String caption);

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   private ApplicationContext context;
   
   private int maxLineNumber;

   public GoToLinePresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;

      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent arg0)
         {
            display.closeForm();
         }
      });

      display.getGoButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            if (display.getLineNumberField().getValue() != null && !"".equals(display.getLineNumberField().getValue()) )
            {
               goToLine();
            }
         }
      });

      maxLineNumber = getLineNumber(context.getActiveFile().getContent());
      String labelCaption = "Enter line number (1.." + maxLineNumber +"):";
      display.setCaptionLabel(labelCaption);
   }
   
   private native int getLineNumber(String content) /*-{
       if (! content) return 1;

       return content.split("\n").length - 1;
   }-*/;

   /**
    * 
    */
   private void goToLine()
   {
      String lineString = display.getLineNumberField().getValue();
      try
      {
         int line = Integer.parseInt(lineString);
         if (line >0 && line <= maxLineNumber)
         {
            display.closeForm();
            eventBus.fireEvent(new EditorGoToLineEvent(line));
         }
         else
         {
            Dialogs.getInstance().showError("Line number out of range");
         }
      }
      catch (NumberFormatException e)
      {
         Dialogs.getInstance().showError("Can't parse line number.");
      }
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

}
