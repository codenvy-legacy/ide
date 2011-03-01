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
package org.exoplatform.ide.client.module.edit.action;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;

import com.google.gwt.user.client.Window;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorSetFocusEvent;
import org.exoplatform.ide.client.framework.vfs.File;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GoToLinePresenter
{

   public interface Display
   {
      TextFieldItem getLineNumber();

      HasClickHandlers getGoButton();

      HasClickHandlers getCancelButton();

      void closeForm();

      void setCaptionLabel(String caption);

      void removeFocusFromLineNumber();

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   private int maxLineNumber;

   private HandlerRegistration keyUpHandler;

   private final Browser currentBrowser = BrowserResolver.CURRENT_BROWSER;

   private File activeFile;

   public GoToLinePresenter(HandlerManager eventBus, File activeFile)
   {
      this.eventBus = eventBus;
      this.activeFile = activeFile;

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
            if (display.getLineNumber().getValue() != null && !"".equals(display.getLineNumber().getValue()))
            {
               goToLine();
            }
         }
      });

      display.getLineNumber().addKeyUpHandler(new KeyUpHandler()
      {

         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            if (event.getNativeKeyCode() == 13)
               goToLine();
         }
      });

      maxLineNumber = getLineNumber(activeFile.getContent());
      String labelCaption = "Enter line number (1.." + maxLineNumber + "):";
      display.setCaptionLabel(labelCaption);
   }

   private native int getLineNumber(String content) /*-{
      if (! content) return 1;

      // test if content is not ended with line break
      if (content.charAt(content.length - 1) !== "\n") {
         return content.split("\n").length;
      }

      // in the Internet Explorer editor.setCode("\n") is displayed as 2 lines 
      if (this.@org.exoplatform.ide.client.module.edit.action.GoToLinePresenter::currentBrowser == @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE) 
      {          
        return content.split("\n").length;
      }

      return content.split("\n").length - 1;
   }-*/;

   /**
    * 
    */
   private void goToLine()
   {
      String lineString = display.getLineNumber().getValue();
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
      eventBus.fireEvent(new EditorSetFocusEvent());
      handlers.removeHandlers();
   }

}
