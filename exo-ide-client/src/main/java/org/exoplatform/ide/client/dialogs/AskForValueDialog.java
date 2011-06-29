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
package org.exoplatform.ide.client.dialogs;

import org.exoplatform.gwtframework.ui.client.api.TextFieldItem;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HasText;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class AskForValueDialog implements ViewClosedHandler
{

   public interface Display extends IsView
   {

      HasText getPromptLabel();

      TextFieldItem getTextField();

      HasClickHandlers getYesButton();

      void setYesButtonEnabled(boolean enabled);

      HasClickHandlers getNoButton();

      void setNoButtonEnabled(boolean enabled);

      HasClickHandlers getCancelButton();

   }

   private static AskForValueDialog instance;

   public static AskForValueDialog getInstance()
   {
      return instance;
   }

   private Display display;

   private ValueCallback callback;

   private ValueDiscardCallback discardCallback;

   public AskForValueDialog(HandlerManager eventBus)
   {
      instance = this;
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
   }

   public void ask(String title, String prompt, String defaultValue, int dialogWidth, ValueCallback callback,
      ValueDiscardCallback discardCallback)
   {
      if (display != null)
      {
         Window.alert("Another Ask For Value Dialog is opened!");
         return;
      }

      this.callback = callback;
      this.discardCallback = discardCallback;

      display = GWT.create(Display.class);

      display.asView().setTitle(title);
      display.getPromptLabel().setText(prompt);

      display.getTextField().setValue(defaultValue);

      display.getTextField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         @Override
         public void onValueChange(ValueChangeEvent<String> event)
         {
            checkYesButtonEnabled();
         }
      });
      checkYesButtonEnabled();

      display.getTextField().addKeyUpHandler(new KeyUpHandler()
      {
         @Override
         public void onKeyUp(KeyUpEvent event)
         {
            if (event.getNativeKeyCode() == 13)
            {
               if (display.getTextField().getValue() == null || display.getTextField().getValue().isEmpty())
               {
                  return;
               }

               yesButtonClicked();
            }
         }
      });

      display.getYesButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            yesButtonClicked();
         }
      });

      display.getNoButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            noButtonClicked();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            cancelButtonClicked();
         }
      });

      IDE.getInstance().openView(display.asView());
   }

   private void checkYesButtonEnabled()
   {
      if (display.getTextField().getValue() == null || display.getTextField().getValue().isEmpty())
      {
         display.setYesButtonEnabled(false);
      }
      else
      {
         display.setYesButtonEnabled(true);
      }
   }

   public void ask(String title, String prompt, String defaultValue, int dialogWidth, ValueCallback callback)
   {
      ask(title, prompt, defaultValue, dialogWidth, callback, null);
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   private void yesButtonClicked()
   {
      String value = display.getTextField().getValue();
      IDE.getInstance().closeView(display.asView().getId());
      callback.execute(value);
   }

   private void noButtonClicked()
   {
      IDE.getInstance().closeView(display.asView().getId());
      discardCallback.discard();
   }

   private void cancelButtonClicked()
   {
      IDE.getInstance().closeView(display.asView().getId());
      callback.execute(null);
   }

}
