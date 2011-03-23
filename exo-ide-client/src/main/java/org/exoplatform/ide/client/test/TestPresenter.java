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
package org.exoplatform.ide.client.test;

import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.gwt.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewDisplay;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TestPresenter implements ViewOpenedHandler, ViewClosedHandler, ClosingViewHandler
{

   public interface Display extends ViewDisplay
   {

      static final String ID = "ideTestView";

      static final int DEFAULT_WIDTH = 500;

      static final int DEFAULT_HEIGHT = 250;

      HasClickHandlers getSetIcon1Button();

      HasClickHandlers getSetIcon2Button();

      HasClickHandlers getSetTitle1Button();

      HasClickHandlers getSetTitle2Button();

      HasClickHandlers getAskOnCloseButton();

      void setAskingEnabled(boolean askingEnabled);

   }

   private HandlerManager eventBus;

   private Display display;

   public TestPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(ClosingViewEvent.TYPE, this);

      HTML test =
         new HTML("<font color=\"blue\" style=\"text-decoration:underline; cursor:pointer;\">Open Test View</font>");
      DOM.setStyleAttribute(test.getElement(), "zIndex", Integer.MAX_VALUE - 1000 + "");
      RootPanel.get().add(test, 500, 2);
      test.addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            doTest();
         }
      });
   }

   private boolean askOnClose = false;

   public void bindDisplay(Display d)
   {
      this.display = d;

      display.setAskingEnabled(askOnClose);

      display.getAskOnCloseButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            if (askOnClose)
            {
               askOnClose = false;
               display.setAskingEnabled(false);
            }
            else
            {
               askOnClose = true;
               display.setAskingEnabled(true);
            }
         }
      });

      display.getSetTitle1Button().addClickHandler(new SetTitleClickHandler("this is the simple title"));
      display.getSetTitle2Button().addClickHandler(new SetTitleClickHandler("another title <b>with bold text</b>"));
   }

   private void doTest()
   {
      if (display != null)
      {
         return;
      }

      String viewId = "ideTestView0";
      String type = "modal";
      //String type = "popup";
      //String type = "operation";
      String title = "Test View 0";

      TestView view = new TestView(viewId, type, title);
      IDE.getInstance().openView(view);
      bindDisplay(view);

   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
      }
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
   }

   @Override
   public void onClosingView(final ClosingViewEvent event)
   {
      if (!askOnClose)
      {
         return;
      }

      event.cancelClosing();

      Dialogs.getInstance().ask("IDE", "Close this view?", new BooleanValueReceivedHandler()
      {
         @Override
         public void booleanValueReceived(Boolean value)
         {
            if (true == value)
            {
               IDE.getInstance().closeView(event.getView().getId());
            }
         }
      });
   }

   private class SetTitleClickHandler implements ClickHandler
   {

      private String title;

      public SetTitleClickHandler(String title)
      {
         this.title = title;
      }

      @Override
      public void onClick(ClickEvent event)
      {
         display.getView().setTitle(title);
      }

   }

}
