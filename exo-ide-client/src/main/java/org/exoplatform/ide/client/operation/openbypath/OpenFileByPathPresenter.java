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
package org.exoplatform.ide.client.operation.openbypath;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class OpenFileByPathPresenter implements ViewClosedHandler, OpenFileByPathHandler
{

   public interface Display extends IsView
   {
      HasClickHandlers getOpenButton();

      HasClickHandlers getCancelButton();

      void enableOpenButton();

      void disableOpenButton();

      HasKeyPressHandlers getFilePathField();

      void selectPathField();

      void focusInPathField();

      TextFieldItem getFilePathFieldOrigin();
   }

   private Display display;

   public OpenFileByPathPresenter()
   {
      IDE.getInstance().addControl(new OpenFileByPathControl());

      IDE.addHandler(OpenFileByPathEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   void bindDisplay(Display d)
   {
      display = d;

      display.getOpenButton().addClickHandler(new ClickHandler()
      {

         @Override
         public void onClick(ClickEvent event)
         {
            openFile();
         }

      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
         }
      });

      display.getFilePathField().addKeyPressHandler(new KeyPressHandler()
      {

         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getCharCode() == KeyCodes.KEY_ENTER)
            {
               openFile();
            }
         }

      });

      display.getFilePathFieldOrigin().addValueChangeHandler(new ValueChangeHandler<String>()
      {

         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            updateOpenButtonState(event.getValue());
         }
      });

      display.disableOpenButton();

   }

   private void updateOpenButtonState(Object filePath)
   {
      if (filePath == null || filePath.toString().trim().length() == 0)
      {
         display.disableOpenButton();
      }
      else
      {
         display.enableOpenButton();
      }
   }

   private void openFile()
   {
      String filePath = display.getFilePathFieldOrigin().getValue();

      if (filePath == null || filePath.trim().length() == 0)
      {
         display.disableOpenButton();
         return;
      }

      IDE.fireEvent(new OpenFileEvent(filePath));
      IDE.getInstance().closeView(display.asView().getId());
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
    * @see org.exoplatform.ide.client.navigation.event.OpenFileByPathHandler#onOpenFileByPath(org.exoplatform.ide.client.navigation.event.OpenFileByPathEvent)
    */
   @Override
   public void onOpenFileByPath(OpenFileByPathEvent event)
   {
      if (display == null)
      {
         Display d = GWT.create(Display.class);
         IDE.getInstance().openView(d.asView());
         bindDisplay(d);
         display.focusInPathField();
      }
      else
      {
         IDE.fireEvent(new ExceptionThrownEvent("Display OpenFileByPath must be null"));
      }
   }

}
