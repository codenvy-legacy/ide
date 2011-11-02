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
package org.exoplatform.ide.client.edit;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.util.BrowserResolver;
import org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.edit.event.GoToLineEvent;
import org.exoplatform.ide.client.edit.event.GoToLineHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorGoToLineEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorSetFocusEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.vfs.client.model.FileModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.shared.HandlerRegistration;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: $
 *
 */
public class GoToLinePresenter implements EditorActiveFileChangedHandler, GoToLineHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {
      
      TextFieldItem getLineNumber();

      HasClickHandlers getGoButton();

      HasClickHandlers getCancelButton();

      void setCaptionLabel(String caption);

      void removeFocusFromLineNumber();
      
      void setFocusInLineNumberField();

   }

   /* Error messages */
   private static final String LINE_OUT_OF_RANGE = IDE.ERRORS_CONSTANT.goToLineLineNumberOutOfRange();

   private static final String CANT_PARSE_LINE_NUMBER = IDE.ERRORS_CONSTANT.goToLineCantParseLineNumber();

   private Display display;

   private int maxLineNumber;

   private HandlerRegistration keyUpHandler;

   private final Browser currentBrowser = BrowserResolver.CURRENT_BROWSER;

   private FileModel activeFile;

   public GoToLinePresenter()
   {
      IDE.addHandler(GoToLineEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent arg0)
         {
            closeView();
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
      String labelCaption = IDE.EDITOR_CONSTANT.goToLineLabelEnterLineNumber(maxLineNumber);
      display.setCaptionLabel(labelCaption);
   }

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
            closeView();
            IDE.fireEvent(new EditorGoToLineEvent(line));
         }
         else
         {
            display.removeFocusFromLineNumber();
            Dialogs.getInstance().showError(LINE_OUT_OF_RANGE);
         }
      }
      catch (NumberFormatException e)
      {
         display.removeFocusFromLineNumber();
         Dialogs.getInstance().showError(CANT_PARSE_LINE_NUMBER);
      }
   }
   
   private void closeView()
   {
      IDE.getInstance().closeView(display.asView().getId());
      IDE.fireEvent(new EditorSetFocusEvent());
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      this.activeFile = event.getFile();
   }

   /**
    * @see org.exoplatform.ide.client.edit.event.GoToLineHandler#onGoToLine(org.exoplatform.ide.client.edit.event.GoToLineEvent)
    */
   @Override
   public void onGoToLine(GoToLineEvent event)
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView(d.asView());
         bindDisplay(d);
         display.setFocusInLineNumberField();
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Display Go To Line must be null"));
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
   
   private native int getLineNumber(String content) /*-{
   if (! content) return 1;

   // test if content is not ended with line break
   if (content.charAt(content.length - 1) !== "\n") {
   return content.split("\n").length;
   }

   // in the Internet Explorer editor.setCode("\n") is displayed as 2 lines 
   if (this.@org.exoplatform.ide.client.edit.GoToLinePresenter::currentBrowser == @org.exoplatform.gwtframework.commons.util.BrowserResolver.Browser::IE) 
   {          
   return content.split("\n").length;
   }

   return content.split("\n").length - 1;
   }-*/;

}
