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

import org.exoplatform.ide.client.Log;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ClosingViewHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;

import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ViewActionsLogger implements ViewOpenedHandler, ClosingViewHandler, ViewClosedHandler,
   ViewVisibilityChangedHandler, ViewActivatedHandler
{

   public ViewActionsLogger(HandlerManager eventBus)
   {
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(ClosingViewEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      eventBus.addHandler(ViewActivatedEvent.TYPE, this);
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      Log.info("View [ " + event.getView().getId() + " ] has been Opened!");
   }

   @Override
   public void onClosingView(ClosingViewEvent event)
   {
      Log.info("Before clofing of View [ " + event.getView().getId() + " ] !");
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      Log.info("View [ " + event.getView().getId() + " ] has been Closed!");
   }

   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      Log.info("Visibility of View [ " + event.getView().getId() + " ] has been chaqnged to [ " + event.getView().isViewVisible() + " ] !");
   }

   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {
      Log.info("Visibility of View [ " + event.getView().getId() + " ] has been Activated!");
   }

}
