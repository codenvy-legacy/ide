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
package org.exoplatform.ide.client.unsupportedbrowser;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.Window;

import org.exoplatform.ide.client.framework.event.IDELoadCompleteEvent;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatfrom.com">Artem Zatsarynnyy</a>
 * @version $Id: UnsupportedBrowserHandler.java Oct 23, 2012 12:17:25 PM azatsarynnyy $
 *
 */
public class UnsupportedBrowserHandler implements IDELoadCompleteHandler, ViewClosedHandler
{

   public interface Display extends IsView
   {
      /**
       * Get Continue button handler.
       * 
       * @return {@link HasClickHandlers}
       */
      HasClickHandlers getContinueButton();
   }

   /**
    * The display.
    */
   private Display display;

   public UnsupportedBrowserHandler()
   {
      IDE.addHandler(IDELoadCompleteEvent.TYPE, this);
      IDE.addHandler(ViewClosedEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.IDELoadCompleteHandler#onIDELoadComplete(org.exoplatform.ide.client.framework.event.IDELoadCompleteEvent)
    */
   @Override
   public void onIDELoadComplete(IDELoadCompleteEvent event)
   {
      String userAgent = Window.Navigator.getUserAgent().toLowerCase();

      if ((userAgent.indexOf("chrome") != -1) || (userAgent.indexOf("firefox") != -1)
         || (userAgent.indexOf("safari") != -1))
      {
         return;
      }

      if (display == null)
      {
         display = GWT.create(Display.class);
         bindDisplay();

         IDE.getInstance().openView(display.asView());
      }
   }

   /**
    * Bind display (view) with presenter.
    */
   public void bindDisplay()
   {
      display.getContinueButton().addClickHandler(new ClickHandler()
      {
         @Override
         public void onClick(ClickEvent event)
         {
            IDE.getInstance().closeView(display.asView().getId());
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
      }
   }
}
