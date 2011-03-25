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
package org.exoplatform.ide.client.module.navigation.control.versioning;

import org.exoplatform.gwtframework.ui.client.command.SimpleControl;
import org.exoplatform.ide.client.framework.control.IDEControl;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.versioning.VersionContentPresenter;

import com.google.gwt.event.shared.HandlerManager;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Oct 13, 2010 $
 *
 */
public class VersionControl extends SimpleControl implements IDEControl, ViewOpenedHandler,
   ViewVisibilityChangedHandler, org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler
{

   /**
    * @param id
    */
   public VersionControl(String id)
   {
      super(id);
      
   }

   /**
    * @see org.exoplatform.ide.client.framework.control.IDEControl#initialize(com.google.gwt.event.shared.HandlerManager)
    */
   public void initialize(HandlerManager eventBus)
   {
      eventBus.addHandler(ViewClosedEvent.TYPE, this);
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(ViewVisibilityChangedEvent.TYPE, this);
   }
   

   /**
    * @see org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.gwt.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      if (event.getView() instanceof VersionContentPresenter.Display && event.getView().isViewVisible())
      {
         setVisible(true);
      }
      else
      {
         setVisible(false);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler#onViewClosed(org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent)
    */
   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if(event.getView()  instanceof VersionContentPresenter.Display )
      {
         setVisible(false);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedHandler#onViewOpened(org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedEvent)
    */
   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if(event.getView()  instanceof VersionContentPresenter.Display )
      {
         setVisible(true);
      }
   }
}
