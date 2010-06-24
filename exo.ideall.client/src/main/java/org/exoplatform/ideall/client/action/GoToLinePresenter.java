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
import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ideall.client.editor.event.EditorGoToLineEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyUpHandlers;
import com.google.gwt.event.shared.GwtEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.types.KeyNames;
import com.smartgwt.client.widgets.form.fields.events.KeyPressEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyPressHandler;
import com.smartgwt.client.widgets.form.fields.events.KeyUpEvent;
import com.smartgwt.client.widgets.form.fields.events.KeyUpHandler;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GoToLinePresenter
{

   public interface Display
   {
      com.smartgwt.client.widgets.form.fields.events.HasKeyUpHandlers getLineNumberField();

      HasValue<String> getLineNumberValue();

      HasClickHandlers getGoButton();

      HasClickHandlers getCancelButton();

      void closeForm();

      void setCaptionLabel(String caption);
      
      void removeFocusFromLineNumber();

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   private ApplicationContext context;

   private int maxLineNumber;

   private HandlerRegistration keyUpHandler;

   private Browser currentBrowser = BrowserResolver.currentBrowser;

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
            if (display.getLineNumberValue().getValue() != null && !"".equals(display.getLineNumberValue().getValue()))
            {
               goToLine();
            }
         }
      });

      keyUpHandler = display.getLineNumberField().addKeyUpHandler(new KeyUPHandler());
      

      maxLineNumber = getLineNumber(context.getActiveFile().getContent());
      String labelCaption = "Enter line number (1.." + maxLineNumber + "):";
      display.setCaptionLabel(labelCaption);
   }

   private native int getLineNumber(String content) /*-{
                                                    if (! content) return 1;

                                                    switch (this.@org.exoplatform.ideall.client.action.GoToLinePresenter::currentBrowser) {          
                                                    // fix bug with CodeMirror in the IE
                                                    case @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE :
                                                    return content.split("\n").length;
                                                    break;
                                                    
                                                    default:
                                                    return content.split("\n").length - 1;     
                                                    } 
                                                    
                                                    }-*/;

   /**
    * 
    */
   private void goToLine()
   {
      String lineString = display.getLineNumberValue().getValue();
      try
      {
         int line = Integer.parseInt(lineString);
         if (line > 0 && line <= maxLineNumber)
         {
            display.closeForm();
            eventBus.fireEvent(new EditorGoToLineEvent(line));
         }
         else
         {
             display.removeFocusFromLineNumber();
             Dialogs.getInstance().showError("Line number out of range");
         }
      }
      catch (NumberFormatException e)
      {
         display.removeFocusFromLineNumber();
         Dialogs.getInstance().showError("Can't parse line number.");
      }
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   private class KeyUPHandler implements KeyUpHandler
   {

      /**
       * @see com.smartgwt.client.widgets.form.fields.events.KeyUpHandler#onKeyUp(com.smartgwt.client.widgets.form.fields.events.KeyUpEvent)
       */
      public void onKeyUp(KeyUpEvent event)
      {
         if (event.getKeyName().equals(KeyNames.ENTER))
         {
            goToLine();
         }
      }

   }

}
