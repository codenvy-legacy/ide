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
package org.exoplatform.ide.client.app.impl.layers;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.ui.client.window.CloseClickHandler;
import org.exoplatform.gwtframework.ui.client.window.Window;
import org.exoplatform.ide.client.app.impl.Layer;
import org.exoplatform.ide.client.framework.ui.gwt.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.gwt.HasClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.gwt.HasViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.HasViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewEx;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedHandler;

import com.google.gwt.event.shared.HandlerRegistration;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public abstract class AbstractWindowsLayer extends Layer implements HasViewOpenedHandler, HasViewClosedHandler,
   HasClosingViewHandler
{

   public class ClosingViewHandlerRegistration implements HandlerRegistration
   {

      private ClosingViewHandler handler;

      public ClosingViewHandlerRegistration(ClosingViewHandler handler)
      {
         this.handler = handler;
      }

      @Override
      public void removeHandler()
      {
         closingViewHandlers.remove(handler);
      }
   }

   protected class ViewClosedHandlerRegistration implements HandlerRegistration
   {

      private ViewClosedHandler handler;

      public ViewClosedHandlerRegistration(ViewClosedHandler handler)
      {
         this.handler = handler;
      }

      @Override
      public void removeHandler()
      {
         viewClosedHandlers.remove(handler);
      }

   }

   private class ViewOpenedHandlerRegistration implements HandlerRegistration
   {

      private ViewOpenedHandler handler;

      public ViewOpenedHandlerRegistration(ViewOpenedHandler handler)
      {
         this.handler = handler;
      }

      @Override
      public void removeHandler()
      {
         viewOpenedHandlers.remove(handler);
      }

   }

   protected List<ClosingViewHandler> closingViewHandlers = new ArrayList<ClosingViewHandler>();

   protected List<ViewClosedHandler> viewClosedHandlers = new ArrayList<ViewClosedHandler>();

   protected List<ViewOpenedHandler> viewOpenedHandlers = new ArrayList<ViewOpenedHandler>();

   @Override
   public HandlerRegistration addClosingViewHandler(ClosingViewHandler closingViewHandler)
   {
      closingViewHandlers.add(closingViewHandler);
      return new ClosingViewHandlerRegistration(closingViewHandler);
   }

   @Override
   public HandlerRegistration addViewClosedHandler(ViewClosedHandler viewClosedHandler)
   {
      viewClosedHandlers.add(viewClosedHandler);
      return new ViewClosedHandlerRegistration(viewClosedHandler);
   }

   @Override
   public HandlerRegistration addViewOpenedHandler(ViewOpenedHandler viewOpenedHandler)
   {
      viewOpenedHandlers.add(viewOpenedHandler);
      return new ViewOpenedHandlerRegistration(viewOpenedHandler);
   }

   protected class WindowController implements CloseClickHandler
   {

      private ViewEx view;

      public WindowController(ViewEx view, Window window)
      {
         this.view = view;
         window.addCloseClickHandler(this);
      }

      @Override
      public void onCloseClick()
      {
         ClosingViewEvent event = new ClosingViewEvent(view);
         for (ClosingViewHandler closingViewHandler : closingViewHandlers)
         {
            closingViewHandler.onClosingView(event);
         }

         if (event.isClosingCanceled())
         {
            return;
         }

         closeView(view.getId());
      }

   }

   public abstract boolean closeView(String viewId);

}
